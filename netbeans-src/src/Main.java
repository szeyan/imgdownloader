
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * The Main class parses the arguments and creates an ImgDownloader object to download all the images
 * from a given URL to some local path on the user's system
 *
 * @author Sze Yan Li
 */
public class Main {

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
            /*URL myURL = new URL("http://pages.uoregon.edu/szeyan/img/ml.png");
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

             System.out.println(base);*/

            final String imgTag = "<img";
            final String imgSrcAttributeName = "src";
            String imgElement = "";
            String imgSrc = "";
            int srcIndex = -1;
            int beginSrcPathIndex = -1;
            boolean foundImgTag = false;
            boolean foundSrcAttrib = false;
            boolean foundEndOfImgTag = false;

            URL myURL = new URL("http://pages.uoregon.edu/szeyan/");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(myURL.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String line = inputLine.toLowerCase();

                //Look for an img tag within this line
                if (line.contains(imgTag)) {
                   foundImgTag = true;
                }
                
                if(foundImgTag){
                    imgElement += inputLine;
                    int imgTagIndex = imgElement.indexOf(imgTag);
                    if(imgElement.indexOf('>', imgTagIndex) != -1){
                        
                        System.out.println(imgElement);
                        imgElement = "";
                        foundImgTag = false;
                    }
                }
                
                
                
                /*
                
                //An img tag was found in a previous line or this line
                if (foundImgTag) {
                    //Look for a src attribute within this line
                    if (line.contains(imgSrcAttributeName)) {
                        srcIndex = line.indexOf(imgSrcAttributeName);
                        //Parse path only if this is a "src" not a "data-src" attribute
                        if (line.charAt(srcIndex - 1) != '-') {
                            //this is not a data-src attribute 
                            foundSrcAttrib = true;
                            imgSrc = inputLine;
                        }
                    }                
                }
                
                if(foundSrcAttrib){
                    
                }
                

                
                //An image's src attribute was found in a previous line or this line
                if (foundSrcAttrib) {

                    //try to find the src end path in this same line
                    beginSrcPathIndex = line.indexOf('\"', srcIndex);   
                    if (beginSrcPathIndex != -1) {
                        int endSrcPathIndex = line.indexOf('\"', beginSrcPathIndex + 1);
                        if (endSrcPathIndex != -1) {
                            imgSrc = inputLine.substring(beginSrcPathIndex + 1, endSrcPathIndex).trim();
                            System.out.println(imgSrc);
                            System.out.println("---");
                            //reset search parameters 
                            imgSrc = "";
                            srcIndex = -1;
                            foundImgTag = false;
                            foundSrcAttrib = false;
                        }

                    }

                } */

            }

            in.close();

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
