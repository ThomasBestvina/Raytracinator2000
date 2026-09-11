import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.thomas.scene.Canvas;
import org.thomas.math.Color;

import java.io.IOException;

public class CanvasTest {
    @Test void CanvasCreationTest() {
        Canvas canvas = new Canvas(10,20);
        assertEquals(10, canvas.getWidth());
        assertEquals(20, canvas.getHeight());

        Color black = new Color(0,0,0);

        for(int x = 0; x < 10; x++)
        {
            for(int y = 0; y < 20; y++)
            {
                assertEquals(black, canvas.getColor(x, y));
            }
        }
    }

    @Test void WritingToACanvas()
    {
        Canvas canvas = new Canvas(10,20);
        Color red = new Color(1,0,0);
        canvas.setColor(2,3,red);
        assertEquals(new Color(1,0,0), canvas.getColor(2,3));
    }

    @Test void PPMHeader()
    {
        Canvas canvas = new Canvas(5, 3);
        assertEquals("P3\n5 3\n255" , canvas.getPPMHeader());
    }

    @Test void PPMFooter() throws IOException {
        Color c1 = new Color(1.5, 0, 0);
        Color c2 = new Color(0, 0.5, 0);
        Color c3 = new Color(-0.5, 0, 1);
        Canvas canvas = new Canvas(5, 3);
        canvas.setColor(0,0,c1);
        canvas.setColor(2,1,c2);
        canvas.setColor(4,2,c3);
        assertEquals(
                "255 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
                        "0 0 0 0 0 0 0 128 0 0 0 0 0 0 0\n" +
                        "0 0 0 0 0 0 0 0 0 0 0 0 0 0 255\n", canvas.getPPMFooter()
        );
    }

    @Test void PPMFooterLong()
    {
        Color c = new Color(1, 0.8, 0.6);
        Canvas canvas = new Canvas(10,2);
        for(int x = 0; x < 10; x++)
        {
            canvas.setColor(x,0,c);
            canvas.setColor(x,1,c);
        }
        assertEquals("255 204 153 255 204 153 255 204 153 255 204 153 255 204 153 255 204 " +
                "153 255 204 153 255 204 153 255 204 153 255 204 153\n" +
                "255 204 153 255 204 153 255 204 153 255 204 153 255 204 153 255 204 " +
                "153 255 204 153 255 204 153 255 204 153 255 204 153\n", canvas.getPPMFooter());
    }
}