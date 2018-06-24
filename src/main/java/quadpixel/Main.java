package quadpixel;

import net.coobird.thumbnailator.Thumbnails;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;

public class    Main
{



    public static void main( String[] args ) throws Exception
    {
        String path = args[0];
        BufferedImage img = readImage(path);
        img = resize(img, Math.min(img.getHeight(), img.getWidth()), Math.min(img.getHeight(), img.getWidth()));
        QuadPixel quadPixel = new QuadPixel(img, path);
        BufferedImage outputImg = quadPixel.processImage(0, img.getWidth() - 1, 0, img.getHeight() - 1);
    }

    public static BufferedImage resize(BufferedImage img, int newWidth, int newHeight) throws IOException
    {
        return Thumbnails.of(img).forceSize(newWidth, newHeight).asBufferedImage();
    }

    /**
     * Writes BufferedImage Stream to a file specified in writePath. The extension will be
     * specified in imageType
     * @param img Image Stream of image
     * @param writePath Output path of image
     * @param imageType Extension type of image
     */
    public static void writeImage(BufferedImage img, String writePath, String imageType)
    {
        File f = null;
        try
        {
            f = new File(writePath);
            ImageIO.write(img, imageType, f);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Reads image from path and returns BufferedImage stream
     * @param path path of image to be read from
     * @return BufferedImage stream of image
     */
    public static BufferedImage readImage(String path)
    {
        BufferedImage img = null;
        File f = null;

        try
        {
            f = new File(path);
            img = ImageIO.read(f);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return img;
    }
}
