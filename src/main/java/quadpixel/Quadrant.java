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
        for(int x=m_leftX; x<=m_rightX; ++x)
        {
            for(int y=m_bottomY; y<=m_topY; ++y)
            {
                m_image.setRGB(x,y,color);
            }
        }
    }

}
