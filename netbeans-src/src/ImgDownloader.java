
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
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
    private Map<String, URL> urlSet = new LinkedHashMap<String, URL>();

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
     *
     * @throws IOException if the images cannot be downloaded
     */
    public void downloadImages() throws IOException {
        if (!isParsed) {
            //search through the website (from the URL) and extract the image names and source paths
        }

        //download the images to local path
        //use timestamp if overwrite is not set
        //use thread 
        InputStream in = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;

        //Read in and write out the image data in chunks
        while ((length = in.read(buffer)) != -1) {
            out.write(buffer, 0, length);
        }

        out.close();
        in.close();
        byte[] response = out.toByteArray();

        String path = this.localPath + File.separator + this.getSaveAsName("blue.png");
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(response);
        fos.close();
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
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            return timeStamp + "_" + imgName;
        }
    }

}
