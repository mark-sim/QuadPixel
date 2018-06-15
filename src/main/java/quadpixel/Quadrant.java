package quadpixel;

import java.awt.image.BufferedImage;

public class Quadrant {
    private BufferedImage m_image;
    private BufferedImage m_outputImage;
    private int m_leftX;
    private int m_rightX;
    private int m_bottomY;
    private int m_topY;
    private int m_squaredError;
    private int m_averageColor;

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

        m_averageColor = (averageAlphaValue << 24) | (averageRedValue << 16) | (averageGreenValue << 8) | averageBlueValue;
        return m_averageColor;
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

    public int getMeanError()
    {
        return m_squaredError;
    }

    public int getAverageColor()
    {
        return m_averageColor;
    }


    /**
     * sets this quadrant to average color of all pixels in this quadrant
     * @returns returns true if quadrant needs further division, otherwise returns false
     */
    public void processQuadrant()
    {
        setQuadrant(m_averageColor);
    }

    public void calculateSquaredMeanError(int averageColor)
    {
        int meanError = 0;
        int width = m_rightX - m_leftX + 1;
        int height = m_topY - m_bottomY + 1;
        int totalPixel = width * height;
        int averageAlphaValue = (averageColor >> 24) & 0xff;
        int averageRedValue = (averageColor >> 16) & 0xff;
        int averageGreenValue = (averageColor >> 8) & 0xff;
        int averageBlueValue = averageColor & 0xff;

        for(int x=m_leftX; x<=m_rightX; ++x)
        {
            for (int y=m_bottomY; y<=m_topY; ++y)
            {
                meanError += Math.pow((averageAlphaValue - getAlphaValue(x,y)), 2);
                meanError += Math.pow((averageRedValue - getRedValue(x,y)), 2);
                meanError += Math.pow((averageGreenValue - getGreenValue(x,y)), 2);
                meanError += Math.pow((averageBlueValue - getBlueValue(x,y)), 2);
            }
        }

        boolean stop = (width < 6 && height < 6) ? true : false;

        m_squaredError = (stop) ? -1 : meanError / totalPixel;
    }


}
