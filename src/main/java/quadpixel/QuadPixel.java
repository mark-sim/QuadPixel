package quadpixel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.PriorityQueue;

public class QuadPixel {
    private BufferedImage m_image;
    private BufferedImage m_outputImage;
    private PriorityQueue<Quadrant> m_priorityQueue;
    private JFrame m_frame;
    private final int INITAL_CAPACITY = 100;

    public  QuadPixel(BufferedImage image, String path)
    {
        m_image = image;
        m_outputImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        m_priorityQueue = new PriorityQueue<Quadrant>(INITAL_CAPACITY, new QuadrantMeanErrorComparator());

        m_frame = new JFrame();
        m_frame.getContentPane().setLayout(new FlowLayout());
        m_frame.setSize(image.getWidth(), image.getHeight() + 50);
        m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public BufferedImage test()
    {
        for (int i=0; i<m_outputImage.getWidth(); ++i)
        {
            for (int j=0; j<m_outputImage.getHeight(); ++j)
            {
                m_outputImage.setRGB(i,j, m_image.getRGB(i,j));
            }
        }
        return m_outputImage;
    }

    public BufferedImage processImage(int leftX, int rightX, int bottomY, int topY) throws Exception
    {
        Quadrant highestMeanErrorQuadrant = process(leftX, rightX, bottomY, topY);

        while (highestMeanErrorQuadrant.getMeanError() >= 0)
        {
            m_frame.getContentPane().removeAll();
            m_frame.getContentPane().add(new JLabel(new ImageIcon(m_outputImage)));

            m_frame.setVisible(true);
            Thread.sleep(5);

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
        Quadrant NE = new Quadrant(m_image, m_outputImage, leftX + (width / 2), rightX, bottomY, bottomY + (height / 2) - 1);
        // [width/2, rightX], [height/2, topY]
        Quadrant SE = new Quadrant(m_image, m_outputImage, leftX + (width / 2), rightX, bottomY + (height / 2), topY);
        // [leftX, width/2), [height/2, topY]
        Quadrant SW = new Quadrant(m_image, m_outputImage, leftX, leftX + (width / 2) - 1, bottomY + (height / 2), topY);

        NW.calculateSquaredMeanError(NW.averageQuadrant());
        NE.calculateSquaredMeanError(NE.averageQuadrant());
        SE.calculateSquaredMeanError(SE.averageQuadrant());
        SW.calculateSquaredMeanError(SW.averageQuadrant());

        NW.processQuadrant();
        NE.processQuadrant();
        SE.processQuadrant();
        SW.processQuadrant();

        setBlackLine(leftX, rightX, bottomY, topY, leftX + (width / 2), bottomY + (height / 2));

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
            int color = getAverage(m_image.getRGB(midX - 1, y), m_image.getRGB(midX + 1, y));
            m_outputImage.setRGB(midX, y, 0x0);
        }

        for (int x=leftX; x<=rightX; ++x)
        {
            int color = getAverage(m_image.getRGB(x, midY - 1), m_image.getRGB(x, midY + 1));
            m_outputImage.setRGB(x, midY,0x0);
        }
    }

    public int getAverage(int neighbourColor1, int neighbourColor2)
    {
        int averageAlpha = ((neighbourColor1 >> 24) & 0xff + (neighbourColor1 >> 24)) / 2;
        int averageRed = ((neighbourColor1 >> 16) & 0xff + (neighbourColor1 >> 16)) / 2;
        int averageGreen = ((neighbourColor1 >> 8) & 0xff + (neighbourColor1 >> 8)) / 2;
        int averageBlue = ((neighbourColor1) & 0xff + (neighbourColor1)) / 2;

        int color = (averageAlpha << 24) | (averageRed << 16) | (averageGreen << 8) | averageBlue;

        if (color == neighbourColor1 || color == neighbourColor2)
        {
            return 0;
        }
        return (averageAlpha << 24) | (averageRed << 16) | (averageGreen << 8) | averageBlue;
    }



}
