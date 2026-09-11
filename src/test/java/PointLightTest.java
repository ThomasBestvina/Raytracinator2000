import org.junit.jupiter.api.Test;
import org.thomas.light.PointLight;
import org.thomas.math.Color;
import org.thomas.math.Vector;

import static org.junit.jupiter.api.Assertions.*;

public class PointLightTest {
    @Test
    void PointLightHasPosAndIntensity()
    {
        Color intensity = new Color(1,1,1);
        Vector position = Vector.point(0,0,0);
        PointLight light = new PointLight(position,intensity);
        assertEquals(position,light.getPosition());
        assertEquals(intensity,light.getIntensity());
    }
}