package dk.acto.demo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

public class Triangle {
    private final int x1;
    private final int x2;
    private final int x3;
    private final int y1;
    private final int y2;
    private final int y3;

    public Triangle(int x1, int y1, int x2, int y2, int x3, int y3) {
        this.x1 = x1;
        this.x2 = x2;
        this.x3 = x3;
        this.y1 = y1;
        this.y2 = y2;
        this.y3 = y3;
    }

    private int edgeFunction(int x1, int y1, int x2, int y2, int x3, int y3)
    {
        return (x2-x1)*(y3-y1) - (y2-y1)*(x3-x1);
    }

    public boolean isContained (int ef1, int ef2, int ef3)
    {
        return (ef1 >= 0 && ef2 >= 0 && ef3 >= 0);
    }

    public void drawToBitmap(Pixmap destination, Color color)
    {
        destination.setColor(color);

        int minX = x1 < x2 ? x1 : x2;
        minX = minX < x3 ? minX : x3;

        int minY = y1 < y2 ? y1 : y2;
        minY = minY < y3 ? minY : y3;

        int maxX = x1 > x2 ? x1 : x2;
        maxX = maxX > x3 ? maxX : x3;

        int maxY = y1 > y2 ? y1 : y2;
        maxY = maxY > y3 ? maxY : y3;

        for (int y = minY; y < maxY; y++ )
        {
            for (int x = minX; x < maxX; x++)
            {
                int t1 = edgeFunction(x1,y1, x2,y2, x,y);
                int t2 = edgeFunction(x2,y2, x3,y3, x,y);
                int t3 = edgeFunction(x3,y3, x1,y1, x,y);

                if (!isContained(t1,t2,t3))
                {
                    continue;
                }

                destination.drawPixel(x,y);
            }
        }
    }
}
