package quadpixel;

import java.awt.image.BufferedImage;

public class Quadrant {
    private BufferedImage m_image;
    private BufferedImage m_outputImage;
    private int m_leftX;
    private int m_rightX;
    private int m_bottomY;
    private int m_topY;

    public Quadrant(BufferedImage image, BufferedImage outputImage, int leftX, int rightX, int bottomY, int topY)
    {
        m_image = image;
        m_outputImage = outputImage;
        m_leftX = leftX;
        m_rightX = rightX;
        m_bottomY = bottomY;
        m_topY = topY;
    }

    /**
     * set all the pixels in this quadrant to specified color
     * @param color Color to set this quadrant
     */
    public void setQuadrant(int color)
    {
        for(int x=m_leftX; x<=m_rightX; ++x)
        {
            for(int y=m_bottomY; y<=m_topY; ++y)
            {
                m_outputImage.setRGB(x,y,color);
            }
        }
    }

    /**
     * Averages all the ARGB pixel values present in the quadrant.
     * @return ARGB pixel value averaged out in this quadrant
     */
    public int averageQuadrant()
    {
        int width = m_rightX - m_leftX + 1;
        int height = m_topY - m_bottomY + 1;
        int totalPixel = width * height;
        int totalAlphaValue = 0;
        int totalRedValue = 0;
        int totalGreenValue = 0;
        int totalBlueValue = 0;

        if (totalPixel == 0)
        {
            // -1 means there is no pixel to process
            return -1;
        }

        for(int x=m_leftX; x<=m_rightX; ++x)
        {
            for (int y=m_bottomY; y<=m_topY; ++y)
            {
                totalAlphaValue += getAlphaValue(x,y);
                totalRedValue += getRedValue(x,y);
                totalGreenValue += getGreenValue(x,y);
                totalBlueValue += getBlueValue(x,y);
            }
        }

        int averageAlphaValue = totalAlphaValue / totalPixel;
        int averageRedValue = totalRedValue / totalPixel;
        int averageGreenValue = totalGreenValue / totalPixel;
        int averageBlueValue = totalBlueValue / totalPixel;

        return (averageAlphaValue << 24) | (averageRedValue << 16) | (averageGreenValue << 8) | averageBlueValue;
    }

    /**
     * get alpha pixel value at specified location
     * @param x x coordinate of image
     * @param y y coordinate of image
     * @return green alpha value at (x,y) coordinate
     */
    private int getAlphaValue(int x, int y)
    {
        int pixelValue = m_image.getRGB(x,y);
        return (pixelValue >> 24) & 0xff;
    }

    /**
     * get red pixel value at specified location
     * @param x x coordinate of image
     * @param y y coordinate of image
     * @return red pixel value at (x,y) coordinate
     */
    private int getRedValue(int x, int y)
    {
        int pixelValue = m_image.getRGB(x,y);
        return (pixelValue >> 16) & 0xff;
    }

    /**
     * get green pixel value at specified location
     * @param x x coordinate of image
     * @param y y coordinate of image
     * @return green pixel value at (x,y) coordinate
     */
    private int getGreenValue(int x, int y)
    {
        int pixelValue = m_image.getRGB(x,y);
        return (pixelValue >> 8) & 0xff;
    }

    /**
     * get blue pixel value at specified location
     * @param x x coordinate of image
     * @param y y coordinate of image
     * @return blue pixel value at (x,y) coordinate
     */
    private int getBlueValue(int x, int y)
    {
        int pixelValue = m_image.getRGB(x, y);
        return pixelValue & 0xff;
    }

    public int getLeftX()
    {
        return m_leftX;
    }

    public int getRightX()
    {
        return m_rightX;
    }

    public int getBottomY()
    {
        return m_bottomY;
    }

    public int getTopY()
    {
        return m_topY;
    }

    /**
     * sets this quadrant to average color of all pixels in this quadrant
     * @returns returns true if quadrant needs further division, otherwise returns false
     */
    public boolean processQuadrant()
    {
        int averageColor = averageQuadrant();
        if (averageColor != -1)
        {
            int meanError = calculateMeanError(averageColor);
            if (meanError > 1000)
            {
                setQuadrant(averageColor);
                return true;
            }
        }
        return false;
    }

    public int calculateMeanError(int averageColor)
    {
        int meanError = 0;
        int totalPixel = (m_rightX - m_leftX + 1) * (m_topY - m_bottomY + 1);
        int averageAlphaValue = (averageColor >> 24) & 0xff;
        int averageRedValue = (averageColor >> 16) & 0xff;
        int averageGreenValue = (averageColor >> 8) & 0xff;
        int averageBlueValue = averageColor & 0xff;

        for(int x=m_leftX; x<=m_rightX; ++x)
        {
            for (int y=m_bottomY; y<=m_topY; ++y)
            {
                meanError += Math.abs(meanError - (averageAlphaValue - getAlphaValue(x,y)));
                meanError += Math.abs(meanError - (averageRedValue - getRedValue(x,y)));
                meanError += Math.abs(meanError - (averageGreenValue - getGreenValue(x,y)));
                meanError += Math.abs(meanError - (averageBlueValue - getBlueValue(x,y)));
            }
        }

        return meanError / totalPixel;

    }


}
