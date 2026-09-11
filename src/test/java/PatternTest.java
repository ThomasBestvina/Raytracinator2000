import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.thomas.math.Color;
import org.thomas.math.Matrix;
import org.thomas.math.Vector;
import org.thomas.pattern.*;
import org.thomas.shape.Shape;
import org.thomas.shape.Sphere;

public class PatternTest {
    @Test void stripePatternConstantY()
    {
        StripePattern pattern = new StripePattern(Color.white, Color.black);
        assertEquals(Color.white, pattern.colorAt(Vector.point(0,0,0)));
        assertEquals(Color.white, pattern.colorAt(Vector.point(0,1,0)));
        assertEquals(Color.white, pattern.colorAt(Vector.point(0,2,0)));
    }
    @Test void stripePatternConstantZ()
    {
        StripePattern pattern = new StripePattern(Color.white, Color.black);
        assertEquals(Color.white, pattern.colorAt(Vector.point(0,0,0)));
        assertEquals(Color.white, pattern.colorAt(Vector.point(0,0,1)));
        assertEquals(Color.white, pattern.colorAt(Vector.point(0,0,2)));
    }
    @Test void stripePatternAlternatesX()
    {
        StripePattern pattern = new StripePattern(Color.white, Color.black);
        assertEquals(Color.white, pattern.colorAt(Vector.point(0,0,0)));
        assertEquals(Color.white, pattern.colorAt(Vector.point(-1.1,0,0)));
        assertEquals(Color.white, pattern.colorAt(Vector.point(0.9,0,0)));
        assertEquals(Color.black, pattern.colorAt(Vector.point(1,0,0)));
        assertEquals(Color.black, pattern.colorAt(Vector.point(-1,0,0)));
        assertEquals(Color.black, pattern.colorAt(Vector.point(-0.1,0,0)));
        assertEquals(Color.white, pattern.colorAt(Vector.point(2,0,0)));
    }
    @Test void stripesWithObjectTransformation()
    {
        Shape s = new Sphere();
        s.setTransform(Matrix.scalar(2,2,2));
        StripePattern pattern = new StripePattern(Color.white, Color.black);
        Color c = pattern.colorAt(s, Vector.point(1.5,0,0));
        assertEquals(Color.white, c);
    }

    @Test void stripesWithVectorTransformation()
    {
        Shape s = new Sphere();
        StripePattern pattern = new StripePattern(Color.white, Color.black);
        pattern.setTransform(Matrix.scalar(2,2,2));
        Color c = pattern.colorAt(s, Vector.point(1.5,0,0));
        assertEquals(Color.white, c);
    }

    @Test void stripesWithBothObjectAndPatternTransform()
    {
        Shape s = new Sphere();
        s.setTransform(Matrix.scalar(2,2,2));
        StripePattern pattern = new StripePattern(Color.white, Color.black);
        pattern.setTransform(Matrix.translation(0.5,0,0));
        Color c = pattern.colorAt(s, Vector.point(2.5,0,0));
        assertEquals(Color.white, c);
    }

    @Test void gradientInterpolatesBetweenColors()
    {
        Pattern pattern = new LinearGradientPattern(Color.white, Color.black);
        assertEquals(Color.white, pattern.colorAt(Vector.point(0,0,0)));
        assertEquals(new Color(0.75,0.75,0.75), pattern.colorAt(Vector.point(0.25,0,0)));
        assertEquals(new Color(0.5,0.5,0.5), pattern.colorAt(Vector.point(0.5,0,0)));
        assertEquals(new Color(0.25,0.25,0.25), pattern.colorAt(Vector.point(0.75,0,0)));

    }

    @Test void RingPatternShouldExtend()
    {
        Pattern pattern = new RingPattern(Color.white, Color.black);
        assertEquals(Color.white, pattern.colorAt(Vector.point(0,0,0)));
        assertEquals(Color.black, pattern.colorAt(Vector.point(1,0,0)));
        assertEquals(Color.black, pattern.colorAt(Vector.point(0,0,1)));
        assertEquals(Color.black, pattern.colorAt(Vector.point(0.708,0,0.708)));
        assertEquals(Color.white, pattern.colorAt(Vector.point(0.2,0,0.2)));
    }

    @Test void CheckersShouldRepeatInX()
    {
        Pattern pattern = new CheckersPattern(Color.white, Color.black);
        assertEquals(Color.white, pattern.colorAt(Vector.point(0,0,0)));
        assertEquals(Color.white, pattern.colorAt(Vector.point(0.99,0,0)));
        assertEquals(Color.black, pattern.colorAt(Vector.point(1.01,0,0)));
    }

    @Test void CheckersShouldUsePatternColors()
    {
        Color a = new Color(0, 1, 0.2);
        Color b = new Color(1, 1, 1);
        Pattern pattern = new CheckersPattern(a, b);

        assertEquals(a, pattern.colorAt(Vector.point(0,0,0)));
        assertEquals(b, pattern.colorAt(Vector.point(1.01,0,0)));
    }

    @Test void CheckersShouldRepeatInY()
    {
        Pattern pattern = new CheckersPattern(Color.white, Color.black);
        assertEquals(Color.white, pattern.colorAt(Vector.point(0,0,0)));
        assertEquals(Color.white, pattern.colorAt(Vector.point(0,0.99,0)));
        assertEquals(Color.black, pattern.colorAt(Vector.point(0,1.01,0)));
    }

    @Test void CheckersShouldRepeatInZ()
    {
        Pattern pattern = new CheckersPattern(Color.white, Color.black);
        assertEquals(Color.white, pattern.colorAt(Vector.point(0,0,0)));
        assertEquals(Color.white, pattern.colorAt(Vector.point(0,0,0.99)));
        assertEquals(Color.black, pattern.colorAt(Vector.point(0,0,1.01)));
    }
}