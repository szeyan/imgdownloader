
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * This class can take an URL to a website and a local destination path and downloads all image files based on <IMG> tags.
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
    private Map<String, URL> urlSet = new HashMap<String, URL>();

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
     * TODO
     * Downloads all the images based on a given URL and local path.
     * Makes a thread to download each image.
     *
     * @throws IOException if the images cannot be downloaded
     */
    public void downloadImages() throws MalformedURLException {
        if (!isParsed) {
            //search through the website (from the URL) and extract the image names and source paths
        }

        URL one = new URL("http://pages.uoregon.edu/szeyan/img/in.png");
        URL two = new URL("http://pages.uoregon.edu/szeyan/img/m.png");

        //For each image found, create a thread to download the image
        Runnable r1 = new SaveImageThread(one, "blue.png");
        new Thread(r1).start();
        Runnable r2 = new SaveImageThread(two, "blue2.png");
        new Thread(r2).start();
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
         * @param imgSrcUrl image source URL
         * @param imgName   image name
         */
        public SaveImageThread(URL imgSrcUrl, String imgName) {
            this.imgSrcUrl = imgSrcUrl;
            this.imgName = imgName;
        }

        /**
         * Downloads the image and writes the image to the local path (provided by the outer class).
         * A timestamp will be appended to the provided image name
         * if the user specified not to overwrite existing files.
         * A status message will be printed out indicating download success or failure.
         */
        public void run() {

            try {
                //Open a connection to the image source URL and get the input stream
                InputStream in = new BufferedInputStream(this.imgSrcUrl.openStream());

                //Read in the image data in chunks and write it to the output stream
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length = 0;
                while ((length = in.read(buffer)) != -1) {
                    out.write(buffer, 0, length);
                }
                out.close();
                in.close();

                //Save the image data to the local path (provided by the outer class)
                byte[] bytes = out.toByteArray();
                String path = localPath + File.separator + getSaveAsName(this.imgName);
                FileOutputStream fos = new FileOutputStream(path);
                fos.write(bytes);
                fos.close();

                //Print success status message
                System.out.println("Downloading " + this.imgName + " from " + this.imgSrcUrl
                        + "\t-- Success");

            } catch (Exception e) {
                //Ignore the image if it failed to download.  Print a failure status message.
                System.out.println("Downloading " + this.imgName + " from " + this.imgSrcUrl
                        + "\t-- Failure");
            }
        }
    }

}
