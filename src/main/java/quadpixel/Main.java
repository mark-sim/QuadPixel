package quadpixel;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class    Main
{
    public static void main( String[] args ) throws IOException
    {
        BufferedImage img = readImage("C:\\Users\\USER\\Desktop\\DailyTransactionTable.png");
        QuadPixel quadPixel = new QuadPixel(img);
        quadPixel.processImage(0, img.getWidth() - 1, 0, img.getHeight() - 1);
        writeImage(img, "C:\\Users\\USER\\Desktop\\output.jpg", "jpg");
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
            f = new File("C:\\Users\\USER\\Desktop\\DailyTransactionTable.png");
            img = ImageIO.read(f);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return img;
    }
}
