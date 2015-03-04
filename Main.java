
/**
 * The Main class parses the arguments and creates an ImgDownloader object
 * to download all the images from a given URL
 * to some local destination path on the user's system
 *
 * @author Sze Yan Li
 */
public class Main {

    public static void main(String args[]) {
        ImgDownloader imgDownloader;
        String usage = "Proper Usage: java Main <URL> [<Local Path>] [-ow] ";

        if (args.length < 1 || args.length > 3) {
            System.err.println(usage);
            System.err.println("ie: java Main "
                    + "\"http://pages.uoregon.edu/szeyan/\" "
                    + "\"C:\\Users\\Melody\\Downloads\" "
                    + "-ow ");
            System.exit(1);
        }

        try {
            imgDownloader = new ImgDownloader(args[0]);

            if (args.length > 1) {
                imgDownloader.setLocalPath(args[1]);
            }

            if (args.length > 2) {
                if (args[2].toLowerCase().equals("-ow")) {
                    imgDownloader.setOverwrite(true);
                } else {
                    throw new IllegalArgumentException(usage);
                }
            }

            System.out.println("Website URL: \t" + imgDownloader.getURL());
            System.out.println("Desination: \t" + imgDownloader.getLocalPath());
            System.out.println("Overwrite: \t" + imgDownloader.willOverwrite());
            System.out.println();

            imgDownloader.downloadImages();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
