public class ImgDownloader {

    public static void main(String args[]) {
        final String DEFAULT_PATH = System.getProperty("user.dir");
        ImgDownloaderHelper imgDownloader;
        String usage = "Proper Usage: java ImgDownloader <URL> [<Local Path>] [-overwrite] ";

        if (args.length < 1 || args.length > 3) {
            System.err.println(usage);
            System.err.println("ie: java ImgDownloader "
                    + "\"http://pages.uoregon.edu/szeyan/\" "
                    + "\"C:\\Users\\Melody\\Downloads\" "
                    + "-overwrite ");
            System.exit(1);
        }
        
        String url = args[0];
        String path = DEFAULT_PATH;
        boolean shouldOverwriteExisting = false;  
        
        try {
            if (args.length > 1) {   
                path = args[1];  
            }
            if (args.length > 2) {
                shouldOverwriteExisting = args[2].toLowerCase().equals("-overwrite");
                if (!shouldOverwriteExisting) {
                    throw new IllegalArgumentException(args[2]);
                }
            }
            
            imgDownloader = new ImgDownloaderHelper(url, path, shouldOverwriteExisting);
            
            System.out.println("Website URL: \t" + imgDownloader.WEBSITE_URL);
            System.out.println("Desination: \t" + imgDownloader.DESTINATION_PATH);
            System.out.println("Overwrite: \t" + imgDownloader.OVERWRITE_EXISTING_FILES);
            System.out.println();

            imgDownloader.downloadImages();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
