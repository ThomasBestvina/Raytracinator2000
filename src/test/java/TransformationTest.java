import org.junit.jupiter.api.Test;
import org.thomas.math.Matrix;
import org.thomas.math.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransformationTest {

    @Test void TranslatePoint()
    {
        Matrix transform = Matrix.translation(5,-3,2);
        Vector point = Vector.point(-3,4,5);
        Vector translated = transform.multiply(point);
        assertEquals(Vector.point(2,1,7), translated);
    }

    @Test void MultiplyInverseOfTranslationMatrix()
    {
        Matrix transform = Matrix.translation(5,-3,2);
        Matrix inv = transform.inverse();
        Vector point = Vector.point(-3,4,5);
        Vector result = inv.multiply(point);
        assertEquals(Vector.point(-8,7,3), result);
    }

    @Test void TranslationDoesNotAffectVectors()
    {
        Matrix transform = Matrix.translation(5,-3,2);
        Vector v = Vector.vector3(-3,4,5);
        assertEquals(v, transform.multiply(v));
    }
}