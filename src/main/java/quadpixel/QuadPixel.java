package quadpixel;

import java.awt.image.BufferedImage;

public class QuadPixel {
    private BufferedImage m_image;
    private int m_width;
    private int m_height;

    public QuadPixel(BufferedImage image)
    {
        m_image = image;
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
    public void processImage(int leftX, int rightX, int bottomY, int topY)
    {
        int width = rightX - leftX + 1;
        int height = topY - bottomY + 1;

        // [leftX, width/2), [bottomY, height/2)
        Quadrant NW = new Quadrant(m_image, leftX, (width / 2) - 1, bottomY, (height / 2) - 1);
        // [width/2, rightX], [bottomY, height/2)
        Quadrant NE = new Quadrant(m_image, (width / 2), rightX, bottomY, (height / 2) - 1);
        // [width/2, rightX], [height/2, topY]
        Quadrant SE = new Quadrant(m_image, (width / 2), rightX, height / 2, topY);
        // [leftX, width/2), [height/2, topY]
        Quadrant SW = new Quadrant(m_image, leftX, (width / 2) - 1, height / 2, topY);

        int blue = 0x00000011;
        int red = 0x00110000;
        int green = 0x00001100;
        NW.setQuadrant(1);
        NE.setQuadrant(1);
        SE.setQuadrant(1);
        SW.setQuadrant(1);
    }

}
