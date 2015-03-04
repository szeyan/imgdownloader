
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

    /* a storage that maps the names of the parsed images to their source paths */
    private Map<String, URL> images = new HashMap<String, URL>();

    /**
     * Constructor takes in a String to a website
     *
     * @param url URL to a website to download the images from
     * @throws MalformedURLException if URL is not written in a correct format
     */
    public ImgDownloader(String url) throws MalformedURLException {
        this.url = new URL(url);
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
     * @throws IllegalArgumentException if the new path is not a directory, cannot be written to, or does not exist
     */
    public void setLocalPath(String path) throws IllegalArgumentException {
        File newPath = new File(path);
        if (newPath.isDirectory() && newPath.canWrite()) {
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
     * Sets whether to overwrite existing images that have the same names as the newly downloaded images
     *
     * @param overwrite is whether to overwrite existing images
     */
    public void setOverwrite(Boolean overwrite) {
        this.overwrite = overwrite;
    }

    /**
     * Downloads all the images based on a given URL and local path.
     * Parses through the website for img tags and img source paths only once.
     * Makes a thread to download each image.
     */
    public void downloadImages() throws MalformedURLException {
        if (!isParsed) {
            /*  search through the website (from the URL) and extract the IMG tags.
             filter the extracted IMG tags for ones with defined source paths.
             correct the image source paths with absolute paths. */
            //ArrayList imgTags = gatherImgTags();
            //ArrayList imgSrcPaths = gatherImgSrcPaths(imgTags);
            //setImgSrcPathsAndNames(imgSrcPaths);

            isParsed = true;
        }

        URL one = new URL("http://pages.uoregon.edu/szeyan/img/in.png");
        URL two = new URL("http://pages.uoregon.edu/szeyan/img/ml.png");

        images.put("1.png", one);
        images.put("2.png", two);

        for (Map.Entry<String, URL> img : images.entrySet()) {
            //For each image found, create a thread to download the image
            Runnable r = new SaveImageThread(img.getKey(), img.getValue());
            new Thread(r).start();
        }

    }

    private ArrayList<String> gatherImgTags() {
        ArrayList<String> imgTags = new ArrayList<String>();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.url.openStream()));

            final String imgTag = "<img";
            String imgElement = "";
            boolean foundImgTag = false;

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String line = inputLine.toLowerCase();

                //Look for an img tag within this line
                if (line.contains(imgTag)) {
                    foundImgTag = true;
                }

                if (foundImgTag) {
                    imgElement += inputLine;
                    int imgTagIndex = imgElement.indexOf(imgTag);
                    if (imgElement.indexOf('>', imgTagIndex) != -1) {

                        System.out.println(imgElement);
                        imgElement = "";
                        foundImgTag = false;
                    }
                }
            }
        } catch (Exception e) {

        }

        return imgTags;

    }

    private ArrayList<String> gatherImgSrcPaths(ArrayList<String> imgTags) {
        ArrayList<String> imgSrcPaths = new ArrayList<String>();

        return imgSrcPaths;
    }

    private void setImgSrcPathsAndNames(ArrayList<String> imgSrcPaths) {

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
     * Inner class is used to save an image.
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
             (note: try-with-resources will automatically close the streams) */
            try (
                    //Open a connection to the image source URL and get the input stream
                    InputStream in = new BufferedInputStream(this.imgSrcUrl.openStream());
                    //Create a stream for writing out the data to the "saveAs" path
                    OutputStream out = new BufferedOutputStream(new FileOutputStream(saveAs))) {

                //Read in the image data in chunks and write it out in chunks to disk
                byte[] buffer = new byte[1024];
                int length = 0;
                while ((length = in.read(buffer)) != -1) {
                    out.write(buffer, 0, length);
                }

                //Print success status message
                System.out.println("Downloading " + this.imgSrcUrl + "\t-- SUCCESS");

            } catch (Exception e) {
                //Ignore any errors when an image fails to download/save.  Print a failure status message.
                System.out.println("Downloading " + this.imgSrcUrl + "\t-- FAILURE");

            }
        }
    }

}
