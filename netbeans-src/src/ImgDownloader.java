
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This program takes a URL to a web site and a local path and downloads all image files based on <IMG> tags.
 *
 * @author Sze Yan Li
 */
public class ImgDownloader {

    //Default save path is where the program is located
    public static final String DEFAULT_PATH = System.getProperty("user.dir");

    public static void main(String args[]) {
        String url = "";            //URL to web site to download the images from
        String localPath = "";      //local path to save all the images to
        Boolean overwrite = false;  //flag that specifies whether to overwrite existing files
        String properUsage = "Proper Usage: java ImgDownloader <URL> [<Local Path>] [-ow] ";

        //Program requires at least 1 argument but no more than 3
        if (args.length < 1 || args.length > 3) {
            System.err.println(properUsage);
            System.err.println("\tie: java ImgDownloader "
                    + "http://pages.uoregon.edu/szeyan/img/ml.png "
                    + "C:\\Users\\Melody\\Downloads "
                    + "-ow ");
            System.exit(1);
        }

        //Parse URL - args[0]
        try {
            //URL myURL = new URL(args[0]);
            //URLConnection myURLConnection = myURL.openConnection();
            //myURLConnection.connect();
            
            URL oracle = new URL("http://www.oracle.com/");
            BufferedReader in = new BufferedReader(
            new InputStreamReader(oracle.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
                System.out.println(inputLine);
            in.close();
            
            
        } catch (MalformedURLException e) {
             // new URL() failed
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
             // openConnection() failed
            e.printStackTrace();
            System.exit(1);
        }

        //Parse Local Path - args[1]
        try {
            localPath = args[1];
            System.out.println(localPath);
        } catch (Exception e) {
            //No local path was specified. Program directory will be used as the image save path
            localPath = DEFAULT_PATH;
            System.err.println("heyo1");
            System.out.println(localPath);
        }

        //Parse overwrite flag - args[2]
        try {
            if (args[2].toLowerCase().equals("-ow")) {
                overwrite = true;
                System.out.println(overwrite);
            } else {
                throw new IllegalArgumentException(properUsage);
            }
        } catch (IllegalArgumentException e) {
            //Badly written overwrite flag argument.  Print error and exit.
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            //This argument wasn't specified/does not exist.  Ignore error.
            System.err.println("heyo2");
        }
    }
}
