import org.junit.jupiter.api.Test;
import org.thomas.math.Vector;
import org.thomas.raytracer.Intersection;
import org.thomas.raytracer.IntersectionComputation;
import org.thomas.raytracer.Ray;
import org.thomas.shape.SmoothTriangle;
import org.thomas.shape.Triangle;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SmoothTriangleTest {
    @Test
    public void testSmoothTriangle()
    {
        Vector p1 = Vector.point(0,1,0);
        Vector p2 = Vector.point(-1,0,0);
        Vector p3 = Vector.point(1,0,0);
        Vector n1 = Vector.vector3(0,1,0);
        Vector n2 = Vector.vector3(-1,0,0);
        Vector n3 = Vector.vector3(1,0,0);
        SmoothTriangle smoothTriangle = new SmoothTriangle(p1,p2,p3,n1,n2,n3);
    }

    @Test
    public void intersectionWithSmoothTriangleStoresUV()
    {
        Ray r = new Ray(Vector.point(-0.2, 0.3, -2), Vector.vector3(0,0,1));
        Vector p1 = Vector.point(0,1,0);
        Vector p2 = Vector.point(-1,0,0);
        Vector p3 = Vector.point(1,0,0);
        Vector n1 = Vector.vector3(0,1,0);
        Vector n2 = Vector.vector3(-1,0,0);
        Vector n3 = Vector.vector3(1,0,0);
        SmoothTriangle smoothTriangle = new SmoothTriangle(p1,p2,p3,n1,n2,n3);
        List<Intersection> xs = smoothTriangle.intersect(r);
        assertEquals(0.45, xs.getFirst().u, 0.001);
        assertEquals(0.25, xs.getFirst().v, 0.001);
    }

    @Test
    public void smoothTriangleUsesUVtoInterpolateNormal()
    {
        Vector p1 = Vector.point(0,1,0);
        Vector p2 = Vector.point(-1,0,0);
        Vector p3 = Vector.point(1,0,0);
        Vector n1 = Vector.vector3(0,1,0);
        Vector n2 = Vector.vector3(-1,0,0);
        Vector n3 = Vector.vector3(1,0,0);
        SmoothTriangle smoothTriangle = new SmoothTriangle(p1,p2,p3,n1,n2,n3);

        Intersection i = new Intersection(1, smoothTriangle, 0.45, 0.25,0,0);

        Vector n = smoothTriangle.getNormal(Vector.point(0,0,0), i);
        assertEquals(Vector.vector3(-0.5547, 0.83205, 0), n);
    }

    @Test
    public void preparingNormalOnSmoothTriangle(){
        Vector p1 = Vector.point(0,1,0);
        Vector p2 = Vector.point(-1,0,0);
        Vector p3 = Vector.point(1,0,0);
        Vector n1 = Vector.vector3(0,1,0);
        Vector n2 = Vector.vector3(-1,0,0);
        Vector n3 = Vector.vector3(1,0,0);
        SmoothTriangle smoothTriangle = new SmoothTriangle(p1,p2,p3,n1,n2,n3);

        Intersection i = new Intersection(1, smoothTriangle, 0.45, 0.25,0,0);
        Ray r = new Ray(Vector.point(-0.2,0.3,-2), Vector.vector3(0,0,1));
        List<Intersection> xs = smoothTriangle.intersect(r);
        IntersectionComputation comps = i.prepareComputation(r, xs);
        assertEquals(Vector.vector3(-0.5547, 0.83205, 0), comps.normalv);
    }
}