
**IMAGE DOWNLOADER**
================


----------


AUTHOR
------

Sze Yan (Melody) Li


DESCRIPTION
-----------
This program takes an URL to a website and a local destination path and downloads all image files based on `<IMG>` tags.


RELEVANT FILES
--------------
    README                  - this file
    netbeans-src/           - folder that contains the NetBeans project
    notes/                  - folder that contains additional notes
    ImgDownloader.java      - a class that Main.java uses to download the images
    Main.java               - main driver for this program


NOTES/FEATURES
--------------

This program allows the user to do the following:

- Specify the website URL in a few ways:
    - http://pages.uoregon.edu/szeyan/index.html
    - http://pages.uoregon.edu/szeyan/
    - http://pages.uoregon.edu/szeyan
    * http://pages.uoregon.edu/szeyan/ml.png will not work because this is a direct path to an image and not to a website, meaning the link will not return HTML for the program to parse.

- Optionally choose the local path to save the images to:
    - ie: C:\Users\Melody\Downloads
    - if the local path is not specified, the destination will be same directory as where the program is located

- Optionally choose whether to overwrite existing images if they exist in the destination directory
    - if overwrite (ie: "-ow") is not specified, the images will be saved with a timestamp to prevent overwrites

The program finds the images to download by searching for IMG elements within the website.  Specifically, it will look for the "src" attribute within the IMG element; this also includes IMG elements found in scripts.  Additionally, if the source path contains "../" for a prior directory or is a relative path, the program will correct the path and download the image. If the "src" is not specified or if the image fails to download, the program will simply move onto the next IMG element.  

In short, the program will not crash if it fails to download an image.

To speed up the process of downloading the images, threads are created for reading in and saving each image.

Multiple images with the same source path will only be saved once.

*Please view notes/testSites.txt and notes/srcPathTypes.txt for example working websites and acceptable IMG tag formats.


DESIGN DECISIONS
----------------
There were a few ways to parse the HTML for IMG tags:

1. Use a 3rd party HTML parser
2. Use Java's HTMLEditorKit and HTMLDocument classes
3. Use regular expressions
4. Use String methods such as contains() and search through the HTML


At first, I tried to use method #2, but then I realized it would arbitrarily skip over some elements and not work on some pages, making it very unstable.  I briefly considered #3, but immediately knew this would result in messy code and unpredictable behaviour like #2.  #1 is a good choice, and there are many reliable parsers in existence.  However, it means that my program will have to rely on a third-party for stability. 

In the end, I chose #4 because it was easy to implement, and it allowed full control over the parsing behaviour.

In general, the program tries to account for many edge cases and provide flexibility.  For one, the ImageIO class could've been used for saving images, but there are some image formats that are not supported by ImageIO such as .SVG.  Thus, I chose to simply read in the image as arbitrary bytes and save them to disk using the full image name + extension.

Another concern was how the image source path may be specified.  Some web-designers place the src path on another line.  The path could be relative or absolute as well.  The program accounts for all of these cases.


USAGE
-----
**Compile:** 
`javac Main.java`
    
**Run:** 
`java Main <URL> [<Local Path>] [-ow]`

- `<URL>` is the URL to the website
- `[<Local Path>]` is the optional system path to save the images to.  
    - *If this is not specified, program's directory will be used to as the destination path.
- `[-ow]` is the optional flag that states whether to overwrite existing files.  
    - *If this is not specified, a timestamp will be appended to all downloaded images
- ie: `java Main "https://www.goodreads.com/list" "C:\Users\Melody\Downloads" -ow`

