
import java.net.URL;



/**
 * The Main class parses the arguments and creates an ImgDownloader object to download all the images
 * from a given URL to some local path on the user's system
 *
 * @author Sze Yan Li
 */
public class Main {

    public static void main(String args[]) {  
        ImgDownloader imgDownloader;        
        String properUsage = "Proper Usage: java Main <URL> [<Local Path>] [-ow] ";
        
        //Program requires at least 1 argument but no more than 3
        if (args.length < 1 || args.length > 3) {
            System.err.println(properUsage);
            System.err.println("\tie: java Main "
                    + "http://pages.uoregon.edu/szeyan/img/ml.png "
                    + "C:\\Users\\Melody\\Downloads "
                    + "-ow ");
            System.exit(1);
        }
 
        //Parse the arguments
        try {
          //Parse URL - args[0]
          imgDownloader = new ImgDownloader("http://pages.uoregon.edu/szeyan/img/ml.png"); 
          
          //Parse Local Path - args[1]
          if(args.length > 1){
             imgDownloader.setLocalPath(args[1]); 
          }
          
          //Parse overwrite flag - args[2]
          if(args.length > 2){
              if (args[2].toLowerCase().equals("-ow")) {
                imgDownloader.setOverwrite(true);
                System.out.println(imgDownloader.willOverwrite());
            } else {
                throw new IllegalArgumentException(properUsage);
            }
          }
          
        } catch (Exception e) {
            //ie: MalformedURLException where new URL() failed
            e.printStackTrace();
            System.exit(1);
        }
        
    }
}
