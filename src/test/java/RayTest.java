import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.thomas.math.Matrix;
import org.thomas.raytracer.Ray;
import org.thomas.math.Vector;

public class RayTest {
    @Test void CreatingAndQueryingRay()
    {
        Vector o = Vector.point(1,2,3);
        Vector d = Vector.vector3(4,5,6);
        Ray r = new Ray(o,d);
        assertEquals(r.getOrigin(),o);
        assertEquals(r.getDirection(),d);
    }

    @Test void ComputePointFromDistance()
    {
        Ray r = new Ray(Vector.point(2,3,4), Vector.vector3(1,0,0));
        assertEquals(Vector.point(2,3,4), r.position(0));
        assertEquals(Vector.point(3,3,4), r.position(1));
        assertEquals(Vector.point(1,3,4), r.position(-1));
        assertEquals(Vector.point(4.5,3,4), r.position(2.5));
    }

    @Test
    void translatingARay() {
        Ray r = new Ray(Vector.point(1, 2, 3), Vector.vector3(0, 1, 0));
        Matrix m = Matrix.translation(3, 4, 5);
        Ray r2 = r.transform(m);
        assertEquals(Vector.point(4, 6, 8), r2.getOrigin());
        assertEquals(Vector.vector3(0, 1, 0), r2.getDirection());
    }

    @Test
    void scalingARay() {
        Ray r = new Ray(Vector.point(1, 2, 3), Vector.vector3(0, 1, 0));
        Matrix m = Matrix.scalar(2, 3, 4);
        Ray r2 = r.transform(m);
        assertEquals(Vector.point(2, 6, 12), r2.getOrigin());
        assertEquals(Vector.vector3(0, 3, 0), r2.getDirection());
    }
}