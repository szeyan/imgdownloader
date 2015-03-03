
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class can take an URL to a website and a local path and downloads all image files based on <IMG> tags.
 *
 * @author Sze Yan Li
 */
public class ImgDownloader {

    private static final String DEFAULT_PATH = System.getProperty("user.dir");  //Default save path is where the program is located
    private URL url;    //URL to a website to download the images from
    private String localPath = DEFAULT_PATH;    //local path to save all the images to
    private Boolean overwrite = false;  //flag that specifies whether to overwrite existing files

    /**
     * Constructor takes in a String to a website
     *
     * @param url URL to a website to download the images from
     */
    public ImgDownloader(String url) throws MalformedURLException {
        this.url = new URL(url);
    }
    
    public String getLocalPath(){
        return this.localPath;
    }
    
    public void setLocalPath(String path){
        
    }
    
    public Boolean willOverwrite(){
        return this.overwrite;
    }
    
    public void setOverwrite(Boolean overwrite){
        this.overwrite = overwrite;
    }
    
    public void downloadImages(){
        
    }
}
