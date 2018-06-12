package quadpixel;

import java.awt.image.BufferedImage;

public class Quadrant {
    private BufferedImage m_image;
    private int m_leftX;
    private int m_rightX;
    private int m_bottomY;
    private int m_topY;

    public Quadrant(BufferedImage image, int leftX, int rightX, int bottomY, int topY)
    {
        m_image = image;
        m_leftX = leftX;
        m_rightX = rightX;
        m_bottomY = bottomY;
        m_topY = topY;
    }

    // testing purpose
    public void setQuadrant(int color)
    {
        int colorr = averageQuadrant();
        for(int x=m_leftX; x<=m_rightX; ++x)
        {
            for(int y=m_bottomY; y<=m_topY; ++y)
            {
                m_image.setRGB(x,y,colorr);
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


}
