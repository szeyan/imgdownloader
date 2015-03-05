
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class ImgDownloaderHelper {
    public final URL WEBSITE_URL;
    public final String DESTINATION_PATH;
    public final boolean OVERWRITE_EXISTING_FILES;
    private boolean imagesHaveBeenParsed = false;
    private Map<String, URL> images = new HashMap<>();

    public ImgDownloaderHelper(String urlOther, String path, boolean overwrite) 
            throws MalformedURLException, IllegalArgumentException {
        
        boolean hasNoTrailingSlash = (urlOther.charAt(urlOther.length() - 1) != '/');
        if (hasNoTrailingSlash) {
            
            int lastSlashIndex = urlOther.lastIndexOf('/');
            int lastDotIndex = urlOther.lastIndexOf('.');
            
            boolean urlIsADirectory = (lastSlashIndex > lastDotIndex)  && !urlOther.contains("?");
            if (urlIsADirectory) {
                urlOther += '/';
            }
        }
        this.WEBSITE_URL = new URL(urlOther);
        
        File newPath = new File(path);
        if (newPath.isDirectory()) {
            this.DESTINATION_PATH = newPath.getAbsolutePath();
        } else {
            throw new IllegalArgumentException(path + " is not a valid path");
        }
        
        this.OVERWRITE_EXISTING_FILES = overwrite;
    }

    public void downloadImages() throws IOException, URISyntaxException {
        if (!this.imagesHaveBeenParsed) {
            this.gatherImgElements();
            this.imagesHaveBeenParsed = true;
        }

        for (Map.Entry<String, URL> img : this.images.entrySet()) {
            Runnable r = new SaveImageThread(img.getKey(), img.getValue());
            new Thread(r).start();
        }

    }

    private void gatherImgElements() throws IOException, URISyntaxException {
        final String IMG_TAG = "<img";
        String container = "";
        boolean foundImgTag = false;

        try (BufferedReader in = new BufferedReader(new InputStreamReader(this.WEBSITE_URL.openStream()))) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {

                if (inputLine.toLowerCase().contains(IMG_TAG)) {
                    foundImgTag = true;
                }

                if (foundImgTag) {
                    container += inputLine;

                    int imgTagBegin = container.indexOf(IMG_TAG);
                    int imgTagEnd = container.indexOf('>', imgTagBegin);
                    if (imgTagEnd != -1) {

                        String imgElement = container.substring(imgTagBegin, imgTagEnd + 1);
                        String srcPath = this.findSrcPath(imgElement);

                        if (!srcPath.isEmpty()) {
                            this.storeSrcPath(srcPath);
                        }

                        container = container.substring(imgTagEnd);
                        foundImgTag = container.toLowerCase().contains(IMG_TAG);
                    }
                }
            }
        }
    }

    private String findSrcPath(String img) {
        int srcIndex = img.toLowerCase().indexOf("src");
        if (srcIndex != -1) {
            
            boolean notADataSrcAttribute = (img.charAt(srcIndex - 1) != '-');
            if (notADataSrcAttribute) {
                
                int srcPathBegin = img.indexOf('\"', srcIndex);
                int srcPathEnd = img.indexOf('\"', srcPathBegin + 1);
                
                String ret = img.substring(srcPathBegin + 1, srcPathEnd).trim();
                
                boolean slashWasNotEscaped = (ret.charAt(ret.length() - 1) == '\\');
                if(slashWasNotEscaped){
                    ret = ret.substring(0 , ret.length() - 1).trim();
                }
                
                return ret;
            }
        }

        return "";
    }

    private void storeSrcPath(String srcPath) throws URISyntaxException, MalformedURLException {
        URI uri = new URI(srcPath);

        if (!uri.isAbsolute()) {
            uri = this.WEBSITE_URL.toURI().resolve(uri);
        }

        String imgName = srcPath.substring(srcPath.lastIndexOf('/') + 1);

        if (!this.images.containsKey(imgName)) {
            this.images.put(imgName, uri.toURL());
        }
    }

    private String getSaveAsName(String imgName) {
        if (this.OVERWRITE_EXISTING_FILES) {
            return imgName;
        } else {
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis());
            return timeStamp + "_" + imgName;
        }
    }

    private class SaveImageThread implements Runnable {

        private URL imgSrcUrl;
        private String imgName;

        public SaveImageThread(String imgName, URL imgSrcUrl) {
            this.imgName = imgName;
            this.imgSrcUrl = imgSrcUrl;
        }

        public void run() {
            String saveAs = DESTINATION_PATH + File.separator + getSaveAsName(this.imgName);

            try (
                InputStream in = new BufferedInputStream(this.imgSrcUrl.openStream());
                OutputStream out = new BufferedOutputStream(new FileOutputStream(saveAs))
            ) {

                byte[] buffer = new byte[1024];
                int length = 0;
                while ((length = in.read(buffer)) != -1) {
                    out.write(buffer, 0, length);
                }

                System.out.println("Download SUCCESS -- " + this.imgSrcUrl);

            } catch (Exception e) {
                System.out.println("Download FAILURE -- " + this.imgSrcUrl);
            }
        }
    }
}
