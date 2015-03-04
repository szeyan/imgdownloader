
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

public class ImgDownloader {

    private static final String DEFAULT_PATH = System.getProperty("user.dir");
    private URL url;
    private String localPath = DEFAULT_PATH;
    private Boolean overwrite = false;
    private Boolean isParsed = false;
    private Map<String, URL> images = new HashMap<>();

    public ImgDownloader(String urlOther) throws MalformedURLException {
        if (urlOther.charAt(urlOther.length() - 1) != '/') {
            int lastSlash = urlOther.lastIndexOf('/');
            int dot = urlOther.lastIndexOf('.');
            if ((lastSlash > dot)  && !urlOther.contains("?")) {
                urlOther += '/';
            }
        }

        this.url = new URL(urlOther);
    }

    public String getURL() {
        return this.url.toString();
    }

    public String getLocalPath() {
        return this.localPath;
    }

    public void setLocalPath(String path) throws IllegalArgumentException {
        if(path.charAt(path.length() - 1 ) == '"'){
            path = path.substring(0 , path.length() - 1);
        }
        
        File newPath = new File(path);
        if (newPath.isDirectory()) {
            this.localPath = newPath.getAbsolutePath();
        } else {
            throw new IllegalArgumentException(path + " is not a valid path");
        }
    }

    public Boolean willOverwrite() {
        return this.overwrite;
    }

    public void setOverwrite(Boolean overwrite) {
        this.overwrite = overwrite;
    }

    public void downloadImages() throws IOException, URISyntaxException {
        if (!this.isParsed) {
            this.gatherImgElements();
            this.isParsed = true;
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

        try (BufferedReader in = new BufferedReader(new InputStreamReader(this.url.openStream()))) {

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
            if (img.charAt(srcIndex - 1) != '-') {
                int srcPathBegin = img.indexOf('\"', srcIndex);
                int srcPathEnd = img.indexOf('\"', srcPathBegin + 1);
                
                String ret = img.substring(srcPathBegin + 1, srcPathEnd).trim();
                
                if(ret.charAt(ret.length() - 1) == '\\'){
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
            uri = this.url.toURI().resolve(uri);
        }

        String imgName = srcPath.substring(srcPath.lastIndexOf('/') + 1);

        if (!this.images.containsKey(imgName)) {
            this.images.put(imgName, uri.toURL());
        }
    }

    private String getSaveAsName(String imgName) {
        if (this.overwrite) {
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
            String saveAs = localPath + File.separator + getSaveAsName(this.imgName);

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
