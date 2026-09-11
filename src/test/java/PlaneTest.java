import org.junit.jupiter.api.Test;
import org.thomas.math.AABB;
import org.thomas.math.Matrix;
import org.thomas.math.Vector;
import org.thomas.raytracer.Intersection;
import org.thomas.raytracer.Ray;
import org.thomas.shape.Plane;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlaneTest {
    @Test
    public void normalOfPlaneIsConstantEverywhere() {
        Plane p = new Plane();
        Vector normal = Vector.vector3(0,1,0);
        assertEquals(normal, p.getNormal(Vector.point(0,0,0)));
        assertEquals(normal, p.getNormal(Vector.point(10,0,-10)));
        assertEquals(normal, p.getNormal(Vector.point(-5,0,150)));
    }

    @Test
    public void normalOfTransformedPlaneIsNormalized() {
        Plane p = new Plane();
        p.setTransform(Matrix.scalar(1, 2, 1));

        assertEquals(Vector.vector3(0, 1, 0), p.getNormal(Vector.point(0, 0, 0)));
    }

    @Test
    public void intersectWithRayParallelToPlane() {
        Plane p = new Plane();
        Ray r = new Ray(Vector.point(0,10,0), Vector.vector3(0,0,1));
        List<Intersection> intersections = p.intersect(r);
        assertEquals(0, intersections.size());
    }

    @Test
    public void intersectCoplanarRay()
    {
        Plane p = new Plane();
        Ray r = new Ray(Vector.point(0,0,0), Vector.vector3(0,0,1));
        List<Intersection> intersections = p.intersect(r);
        assertEquals(0, intersections.size());
    }

    @Test
    public void intersectPlaneFromAbove()
    {
        Plane p = new Plane();
        Ray r =  new Ray(Vector.point(0,1,0), Vector.vector3(0,-1,0));
        List<Intersection> intersections = p.intersect(r);
        assertEquals(1, intersections.size());
    }

    @Test
    public void intersectPlaneFromBelow()
    {
        Plane p = new Plane();
        Ray r =  new Ray(Vector.point(0,1,0), Vector.vector3(0,-1,0));
        List<Intersection> intersections = p.intersect(r);
        assertEquals(1, intersections.size());
    }
}
