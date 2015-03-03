
/**
 * This program takes a URL to a web site and a local path and downloads all image files based on <IMG> tags.
 *
 * @author Sze Yan Li
 */
public class ImgDownloader {
    //Default save path is where the program is located
    public static final String DEFAULT_PATH = System.getProperty("user.dir");
    
    public static void main(String args[]) {
        String url = "";
        String localPath = "";
        Boolean overwrite = false;
        
        //Program requires at least 1 argument but no more than 3
        if (args.length < 1 || args.length > 3) {
            System.err.println("Proper Usage: java ImgDownloader <URL> [<Local Path>] [-ow] ");
            System.err.println("\tie: java ImgDownloader "
                    + "http://pages.uoregon.edu/szeyan/img/ml.png "
                    + "C:\\Users\\Melody\\Downloads "
                    + "-ow ");
            System.exit(1);
        }
        
        
        System.out.print(DEFAULT_PATH);
    }
}
