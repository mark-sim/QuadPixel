package quadpixel;

import java.awt.image.BufferedImage;

public class QuadPixel {
    private BufferedImage m_image;
    private BufferedImage m_outputImage;

    public QuadPixel(BufferedImage image, String path)
    {
        m_image = image;
        m_outputImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
    }

    /**
     * First calls divideIntoQuadrants to divide the image into four quadrants.
     * Then each quadrant's color is averaged based on the pixels in that quadrant.
     * This repeats until the squared error is within some tolerance.
     * @param leftX leftmost x-coordinate of image being processed (inclusive)
     * @param rightX rightmost x-coordinate of image being processed (inclusive)
     * @param bottomY bottom y-coordinate of image being processed (inclusive)
     * @param topY top y_coordinate of image being processed (inclusive)
     * Note: The quadrants are divided the following :
     *             NW   NE
     *             SW   SE
     */
    public BufferedImage processImage(int leftX, int rightX, int bottomY, int topY)
    {
        int width = rightX - leftX + 1;
        int height = topY - bottomY + 1;

        if (width < 2 && height < 2)
        {
            return m_outputImage;
        }

        // [leftX, width/2), [bottomY, height/2)
        Quadrant NW = new Quadrant(m_image, m_outputImage, leftX, leftX + (width / 2) - 1, bottomY, bottomY + (height / 2) - 1);
        // [width/2, rightX], [bottomY, height/2)
        Quadrant NE = new Quadrant(m_image, m_outputImage, leftX + (width / 2), rightX, bottomY, bottomY + (height / 2) - 1);
        // [width/2, rightX], [height/2, topY]
        Quadrant SE = new Quadrant(m_image, m_outputImage, leftX + (width / 2), rightX, bottomY + (height / 2), topY);
        // [leftX, width/2), [height/2, topY]
        Quadrant SW = new Quadrant(m_image, m_outputImage, leftX, leftX + (width / 2) - 1, bottomY + (height / 2), topY);

        NW.processQuadrant();
        NE.processQuadrant();
        SE.processQuadrant();
        SW.processQuadrant();

        processImage(NW.getLeftX(), NW.getRightX(), NW.getBottomY(), NW.getTopY());
        processImage(NE.getLeftX(), NE.getRightX(), NE.getBottomY(), NE.getTopY());
        processImage(SE.getLeftX(), SE.getRightX(), SE.getBottomY(), SE.getTopY());
        processImage(SW.getLeftX(), SW.getRightX(), SW.getBottomY(), SW.getTopY());

        return m_outputImage;
    }

}
