
import java.net.URL;
import java.net.URLConnection;

/**
 * This program takes a URL to a web site and a local path and downloads all image files based on <IMG> tags.
 *
 * @author Sze Yan Li
 */
public class ImgDownloader {

    //Default save path is where the program is located
    public static final String DEFAULT_PATH = System.getProperty("user.dir");

    public static void main(String args[]) {
        URL url;                    //URL to web site to download the images from
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
            URL myURL = new URL("http://pages.uoregon.edu/szeyan/img/ml.png");
            URLConnection myURLConnection = myURL.openConnection();
            myURLConnection.connect();
            
             System.out.println("protocol = " + myURL.getProtocol());
        System.out.println("authority = " + myURL.getAuthority());
        System.out.println("host = " + myURL.getHost());
        System.out.println("port = " + myURL.getPort());
        System.out.println("path = " + myURL.getPath());
        System.out.println("query = " + myURL.getQuery());
        System.out.println("filename = " + myURL.getFile());
        System.out.println("ref = " + myURL.getRef());

        String path = myURL.getFile().substring(0, myURL.getFile().lastIndexOf('/'));
        String base = myURL.getProtocol() + "://" + myURL.getHost() + path;
        
        System.out.println(base);
            
            /*
             URL myURL = new URL("http://pages.uoregon.edu/szeyan/");
             BufferedReader in = new BufferedReader(
             new InputStreamReader(myURL.openStream()));

             String inputLine;
             while ((inputLine = in.readLine()) != null)
             System.out.println(inputLine);
             in.close();*/
            
            /*
            
            url = new URL("http://pages.uoregon.edu/szeyan/img/ml.png");
            InputStream in = new BufferedInputStream(url.openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            
            //Read in and write out the image data in chunks
            while((length = in.read(buffer)) != -1){
                out.write(buffer, 0, length);
            }

            out.close();
            in.close();
            byte[] response = out.toByteArray();

            FileOutputStream fos = new FileOutputStream("C://borrowed_image.png");
            fos.write(response);
            fos.close();
            
            */

        } catch (Exception e) {
            //ie: MalformedURLException where new URL() failed
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
