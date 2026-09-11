import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.thomas.math.Color;

public class ColorTest {
    @Test void AddColor() {
        Color c1 = new Color(0.9, 0.6, 0.75);
        Color c2 = new Color(0.7, 0.1, 0.25);
        assertEquals(new Color(1.6, 0.7, 1.0), c1.add(c2));
    }

    @Test void SubtractColor()
    {
        Color c1 = new Color(0.9, 0.6, 0.75);
        Color c2 = new Color(0.7, 0.1, 0.25);
        assertEquals(new Color(0.2, 0.5, 0.5), c1.subtract(c2));
    }

    @Test void MultiplyColorByScalar()
    {
        Color c1 = new Color(0.2, 0.3, 0.4);
        assertEquals(new Color(0.4, 0.6, 0.8), c1.multiply(2));
    }

    @Test void HadamardProduct()
    {
        Color c1 = new Color(1,0.2,0.4);
        Color c2 = new Color(0.9, 1, 0.1);
        assertEquals(new Color(0.9, 0.2, 0.04), c1.multiply(c2));
    }
}