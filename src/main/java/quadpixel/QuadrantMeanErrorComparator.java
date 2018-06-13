package quadpixel;

import java.util.Comparator;

public class QuadrantMeanErrorComparator implements Comparator<Quadrant>
{
    public int compare(Quadrant lhs, Quadrant rhs)
    {
        return (lhs.getMeanError() < rhs.getMeanError()) ? 1 : -1;
    }
}
