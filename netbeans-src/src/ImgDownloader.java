
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

/**
 * This class can take an URL to a website and a local destination path
 * and downloads all image files based on <IMG> tags.
 *
 * @author Sze Yan Li
 */
public class ImgDownloader {

    /* Default save path is where the ImgDownloader is located */
    private static final String DEFAULT_PATH = System.getProperty("user.dir");

    /* URL to a website to download the images from */
    private URL url;

    /* local destination path to save all the images to */
    private String localPath = DEFAULT_PATH;

    /* flag that specifies whether to overwrite existing files */
    private Boolean overwrite = false;

    /* flag that specifies whether IMG tags have been parsed from the URL */
    private Boolean isParsed = false;

    /* a map of the names of the parsed images to their source paths */
    private Map<String, URL> images = new HashMap<>();

    /**
     * Constructor takes in a String to a website
     *
     * @param urlOther URL to a website to download the images from
     * @throws MalformedURLException if the URL is not written in a correct format
     */
    public ImgDownloader(String urlOther) throws MalformedURLException {
        /* add a trailing slash to the given URL 
         if it does not point to a file (ie: index.html) or to a query (ie: ?id=123) */
        if (urlOther.charAt(urlOther.length() - 1) != '/') {
            int lastSlash = urlOther.lastIndexOf('/');
            int dot = urlOther.lastIndexOf('.');
            if ((lastSlash > dot)  && !urlOther.contains("?")) {
                urlOther += '/';
            }
        }

        this.url = new URL(urlOther);
    }

    /**
     * @return URL to a website to download the images from
     */
    public String getURL() {
        return this.url.toString();
    }

    /**
     * @return the local destination to save all the images to
     */
    public String getLocalPath() {
        return this.localPath;
    }

    /**
     * Sets the local destination to save the images to
     *
     * @param path is where to save all the images to
     * @throws IllegalArgumentException if the specified directory does not exist
     */
    public void setLocalPath(String path) throws IllegalArgumentException {
        //remove a trailing quote that's caused by /" 
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

    /**
     * @return if the ImgDownloader will overwrite existing images
     *         that have the same names as the newly downloaded images
     */
    public Boolean willOverwrite() {
        return this.overwrite;
    }

    /**
     * Sets whether to overwrite existing images that
     * have the same names as the newly downloaded images
     *
     * @param overwrite whether to overwrite existing images
     */
    public void setOverwrite(Boolean overwrite) {
        this.overwrite = overwrite;
    }

    /**
     * Downloads all the images based on a given URL and local path.
     * Parses through the website for img tags and img source paths only once.
     * Makes a thread to download each image.
     *
     * @throws IOException        if a connection to an URL could not be established
     * @throws URISyntaxException if the URI is not written in a correct format
     */
    public void downloadImages() throws IOException, URISyntaxException {
        if (!isParsed) {
            gatherImgElements();
            isParsed = true;
        }

        //Create a thread to download each image
        for (Map.Entry<String, URL> img : this.images.entrySet()) {
            Runnable r = new SaveImageThread(img.getKey(), img.getValue());
            new Thread(r).start();
        }

    }

    /**
     * Extracts the IMG elements from the website.
     * Calls findSrcPath() to find image source paths from the IMG elements
     * Calls storeSrcPath() to correct any relative paths and then store the paths to the "images" map
     *
     * @throws IOException        if a connection to an URL could not be established
     * @throws URISyntaxException if the URI is not written in a correct format
     */
    private void gatherImgElements() throws IOException, URISyntaxException {

        String container = "";
        boolean foundImgTag = false;

        /*  Open a connection to the website and search for all IMG elements
         (note: try-with-resources will automatically closes any streams) */
        try (BufferedReader in = new BufferedReader(new InputStreamReader(this.url.openStream()))) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {

                //Used to skip current line of HTML until an IMG tag is found
                if (containsImgTag(inputLine)) {
                    foundImgTag = true;
                }

                //Save the inputLines until the end of an IMG tag has been found
                if (foundImgTag) {
                    container += inputLine;

                    //See if the end of an IMG tag has been found
                    int imgTagBegin = container.indexOf("<img");
                    int imgTagEnd = container.indexOf('>', imgTagBegin);
                    if (imgTagEnd != -1) {

                        //Look for a src path inside this IMG element
                        String imgElement = container.substring(imgTagBegin, imgTagEnd + 1);
                        String srcPath = findSrcPath(imgElement);

                        if (!srcPath.isEmpty()) {
                            storeSrcPath(srcPath);
                        }

                        //See if current string contains more IMG elements
                        container = container.substring(imgTagEnd);
                        foundImgTag = containsImgTag(container);
                    }
                }
            }
        }
    }

    /**
     * @param line an HTML string
     * @return whether the line contains an IMG tag
     */
    private boolean containsImgTag(String line) {
        return line.toLowerCase().contains("<img");
    }

    /**
     * Finds the image source path from given IMG tag based on the "src" attribute
     *
     * @param img an HTML string containing the IMG tag
     * @return a source path for the image if it was found or an empty string if it wasn't
     */
    private String findSrcPath(String img) {
        int srcIndex = img.toLowerCase().indexOf("src");
        if (srcIndex != -1) {

            //make sure this srcIndex doesn't belong to a "data-src" attribute
            if (img.charAt(srcIndex - 1) != '-') {
                int srcPathBegin = img.indexOf('\"', srcIndex);
                int srcPathEnd = img.indexOf('\"', srcPathBegin + 1);
                
                String ret = img.substring(srcPathBegin + 1, srcPathEnd).trim();
                
                //remove any trailing \ 
                if(ret.charAt(ret.length() - 1) == '\\'){
                    ret = ret.substring(0 , ret.length() - 1).trim();
                }
                
                return ret;
            }
        }

        return "";
    }

    /**
     * Stores the path and image name to the "images" map.
     * Turns a relative path into an absolute one.
     *
     * @param srcPath the source path for an image
     *
     * @throws URISyntaxException    if the URI is not written in a correct format
     * @throws MalformedURLException if the URL is not written in a correct format
     */
    private void storeSrcPath(String srcPath) throws URISyntaxException, MalformedURLException {
        URI uri = new URI(srcPath);

        if (!uri.isAbsolute()) {
            //srcPath is relative. Make it absolute
            uri = this.url.toURI().resolve(uri);
        }

        String imgName = srcPath.substring(srcPath.lastIndexOf('/') + 1);

        if (!images.containsKey(imgName)) {
            images.put(imgName, uri.toURL());
        }
    }

    /**
     * @param imgName name of the image
     * @return the image name if the overwrite flag is true
     *         the timestamp concatenated to the image name if overwrite flag is false
     */
    private String getSaveAsName(String imgName) {
        if (this.overwrite) {
            return imgName;
        } else {
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis());
            return timeStamp + "_" + imgName;
        }
    }

    /**
     * Inner class is used to download and save an image.
     */
    private class SaveImageThread implements Runnable {

        /* the URL of where the image is located on the internet */
        private URL imgSrcUrl;

        /* name of the image */
        private String imgName;

        /**
         * Constructor that takes in an image source URL and an image name
         *
         * @param imgName   image name
         * @param imgSrcUrl image source URL
         */
        public SaveImageThread(String imgName, URL imgSrcUrl) {
            this.imgName = imgName;
            this.imgSrcUrl = imgSrcUrl;
        }

        /**
         * Downloads the image and writes the image to the local path (provided by the outer class).
         * A timestamp will be appended to the provided image name
         * if the user specified not to overwrite existing files.
         * A status message will be printed out indicating download success or failure.
         */
        public void run() {
            //Concatenate the local destination path and the name to save the image as
            String saveAs = localPath + File.separator + getSaveAsName(this.imgName);

            /*  Download the image
             (note: try-with-resources will automatically closes any streams) */
            try (
                //Open a connection to the image source URL and get the input stream
                InputStream in = new BufferedInputStream(this.imgSrcUrl.openStream());
                    
                //Create a stream for writing out the data to the "saveAs" path
                OutputStream out = new BufferedOutputStream(new FileOutputStream(saveAs))
            ) {

                //Read in the image data in chunks and write it out in chunks to disk
                byte[] buffer = new byte[1024];
                int length = 0;
                while ((length = in.read(buffer)) != -1) {
                    out.write(buffer, 0, length);
                }

                System.out.println("Download SUCCESS -- " + this.imgSrcUrl);

            } catch (Exception e) {
                //Ignore any errors when an image fails to download/save.
                System.out.println("Download FAILURE -- " + this.imgSrcUrl);
            }
        }
    }
}
