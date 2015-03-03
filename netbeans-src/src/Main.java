
/**
 * The Main class parses the arguments and creates an ImgDownloader object to download all the images
 * from a given URL to some local destination path on the user's system
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
            System.err.println("ie: java Main "
                    + "http://pages.uoregon.edu/szeyan/ "
                    + "C:\\Users\\Melody\\Downloads "
                    + "-ow ");
            System.exit(1);
        }
 
        try {
          //Parse URL - args[0]
          imgDownloader = new ImgDownloader("http://pages.uoregon.edu/szeyan/img/ml.png"); 
          
          //Parse Local Path - args[1]
          if(args.length > 1){
             imgDownloader.setLocalPath("C:\\Users\\Melody\\Downloads\\New folder");
             System.out.println(imgDownloader.getLocalPath());
          }
          
          //Parse overwrite flag - args[2]
          if(args.length > 2){
              if (args[2].toLowerCase().equals("-ow")) {
                imgDownloader.setOverwrite(true);
            } else {
                throw new IllegalArgumentException(properUsage);
            }
          }
          
          //Download all the images now that the arguments are set
          imgDownloader.downloadImages();
          
        } catch (Exception e) {
            //ie: MalformedURLException where setting URL failed
            e.printStackTrace();
            System.exit(1);
        } 
    }
}
