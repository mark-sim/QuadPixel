package quadpixel;

import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.PriorityQueue;

public class QuadPixel {
    private BufferedImage m_image;
    private BufferedImage m_outputImage;
    private PriorityQueue<Quadrant> m_priorityQueue;
    private final int INITAL_CAPACITY = 100;

    public QuadPixel(BufferedImage image, String path)
    {
        m_image = image;
        m_outputImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        m_priorityQueue = new PriorityQueue<Quadrant>(INITAL_CAPACITY, new QuadrantMeanErrorComparator());
    }

    public BufferedImage processImage(int leftX, int rightX, int bottomY, int topY)
    {
        Quadrant highestMeanErrorQuadrant = process(leftX, rightX, bottomY, topY);

        while (highestMeanErrorQuadrant.getMeanError() >= 0)
        {
            highestMeanErrorQuadrant = process(highestMeanErrorQuadrant.getLeftX(), highestMeanErrorQuadrant.getRightX(),
                    highestMeanErrorQuadrant.getBottomY(), highestMeanErrorQuadrant.getTopY());
        }
        return m_outputImage;
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
    public Quadrant process(int leftX, int rightX, int bottomY, int topY)
    {
        int width = rightX - leftX + 1;
        int height = topY - bottomY + 1;

        // [leftX, width/2), [bottomY, height/2)
        Quadrant NW = new Quadrant(m_image, m_outputImage, leftX, leftX + (width / 2) - 1, bottomY, bottomY + (height / 2) - 1);
        // [width/2, rightX], [bottomY, height/2)
        Quadrant NE = new Quadrant(m_image, m_outputImage, leftX + (width / 2) + 1, rightX, bottomY, bottomY + (height / 2) - 1);
        // [width/2, rightX], [height/2, topY]
        Quadrant SE = new Quadrant(m_image, m_outputImage, leftX + (width / 2) + 1, rightX, bottomY + (height / 2) + 1, topY);
        // [leftX, width/2), [height/2, topY]
        Quadrant SW = new Quadrant(m_image, m_outputImage, leftX, leftX + (width / 2) - 1, bottomY + (height / 2) + 1, topY);

        NW.calculateSquaredMeanError(NW.averageQuadrant());
        NE.calculateSquaredMeanError(NE.averageQuadrant());
        SE.calculateSquaredMeanError(SE.averageQuadrant());
        SW.calculateSquaredMeanError(SW.averageQuadrant());

        if (m_priorityQueue.isEmpty())
        {
            setBlackLine(leftX, rightX, bottomY, topY, leftX + (width / 2), bottomY + (height / 2));
        }

        NW.processQuadrant();
        NE.processQuadrant();
        SE.processQuadrant();
        SW.processQuadrant();

        m_priorityQueue.add(NW);
        m_priorityQueue.add(NE);
        m_priorityQueue.add(SE);
        m_priorityQueue.add(SW);

        Quadrant highestMeanErrorQuadrant = m_priorityQueue.poll();

        return highestMeanErrorQuadrant;
    }

    private int averageColor(Quadrant NW, Quadrant NE, Quadrant SE, Quadrant SW)
    {
        int NWcolor = NW.getAverageColor();
        int NEcolor = NE.getAverageColor();
        int SEcolor = SE.getAverageColor();
        int SWcolor = SW.getAverageColor();

        int averageAlpha = ((NWcolor >> 24) & 0xff + (NEcolor >> 24) & 0xff + (SEcolor >> 24) & 0xff + (SWcolor >> 24) & 0xff) / 4;
        int averageRed = ((NWcolor >> 16) & 0xff + (NEcolor >> 16) & 0xff + (SEcolor >> 16) & 0xff + (SWcolor >> 16) & 0xff) / 4;
        int averageGreen = ((NWcolor >> 8) & 0xff + (NEcolor >> 8) & 0xff + (SEcolor >> 8) & 0xff + (SWcolor >> 8) & 0xff) / 4;
        int averageBlue = ((NWcolor) & 0xff + (NEcolor) & 0xff + (SEcolor) & 0xff + (SWcolor) & 0xff) / 4;

        return (averageAlpha << 24) | (averageRed << 16) | (averageGreen << 8) | averageBlue;
    }

    private void setBlackLine(int leftX, int rightX, int bottomY, int topY, int midX, int midY)
    {
        for (int y = bottomY; y<=topY; ++y)
        {
            m_outputImage.setRGB(midX, y, m_image.getRGB(midX, y));
        }

        for (int x=leftX; x<=rightX; ++x)
        {
            m_outputImage.setRGB(x, midY, m_image.getRGB(x, midY));
        }
    }


}
