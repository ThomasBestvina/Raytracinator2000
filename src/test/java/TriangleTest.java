import org.junit.jupiter.api.Test;
import org.thomas.math.AABB;
import org.thomas.math.Matrix;
import org.thomas.math.Vector;
import org.thomas.raytracer.Intersection;
import org.thomas.raytracer.Ray;
import org.thomas.shape.Triangle;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TriangleTest {
    @Test void constructingTriangle() {
        Vector p1 = Vector.point(0,1,0);
        Vector p2 = Vector.point(-1,0,0);
        Vector p3 = Vector.point(1,0,0);
        Triangle triangle = new Triangle(p1, p2, p3);
        assertEquals(p1, triangle.p1);
        assertEquals(p2, triangle.p2);
        assertEquals(p3, triangle.p3);
        assertEquals(Vector.vector3(-1,-1,0), triangle.e1);
        assertEquals(Vector.vector3(1,-1,0), triangle.e2);
        assertEquals(Vector.vector3(0,0,-1), triangle.normal);
    }

    @Test void normalOnTriangle()
    {
        Triangle t = new Triangle(Vector.point(0,1,0),
                Vector.point(-1,0,0),
                Vector.point(1,0,0));
        Vector n1 = t.getNormal(Vector.point(0,0.5,0));
        Vector n2 = t.getNormal(Vector.point(-0.5,0.75,0));
        Vector n3 = t.getNormal(Vector.point(0.5,0.25,0));
        assertEquals(t.normal, n1);
        assertEquals(t.normal, n2);
        assertEquals(t.normal, n3);
    }

    @Test void intersectingRayParallelToTriangle()
    {
        Triangle t = new Triangle(Vector.point(0,1,0),
                Vector.point(-1,0,0),
                Vector.point(1,0,0));

        Ray r = new Ray(Vector.point(0,-1,-2),Vector.point(0,1,0));
        List<Intersection> xs = t.intersect(r);
        assertTrue(xs.isEmpty());
    }

    @Test void rayMissesP1P3Edge()
    {
        Triangle t = new Triangle(Vector.point(0,1,0),
                Vector.point(-1,0,0),
                Vector.point(1,0,0));
        Ray r = new Ray(Vector.point(1,1,-2),Vector.point(0,0,1));
        List<Intersection> xs = t.intersect(r);
        assertTrue(xs.isEmpty());
    }

    @Test void rayMissesP1P2Edge()
    {
        Triangle t = new Triangle(Vector.point(0,1,0),
                Vector.point(-1,0,0),
                Vector.point(1,0,0));
        Ray r = new Ray(Vector.point(0,-1,-2),Vector.point(0,0,1));
        List<Intersection> xs = t.intersect(r);
        assertTrue(xs.isEmpty());
    }

    @Test void rayStrikesTriangle()
    {
        Triangle t = new Triangle(Vector.point(0,1,0),
                Vector.point(-1,0,0),
                Vector.point(1,0,0));
        Ray r = new Ray(Vector.point(0,0.5,-2),Vector.point(0,0,1));
        List<Intersection> xs = t.intersect(r);
        assertEquals(1, xs.size());
        assertEquals(2, xs.getFirst().t);
    }

    private static final double EPS = 1e-9;

    @Test
    void triangleLocalBoundsBasic() {
        Triangle t = new Triangle(
                Vector.point(0, 1, 0),
                Vector.point(-1, 0, 0),
                Vector.point(1, 0, 0)
        );
        AABB b = t.bounds();
        assertEquals(-1, b.min.x, EPS);
        assertEquals( 0, b.min.y, EPS);
        assertEquals( 0, b.min.z, EPS);
        assertEquals( 1, b.max.x, EPS);
        assertEquals( 1, b.max.y, EPS);
        assertEquals( 0, b.max.z, EPS);
    }

    @Test
    void triangleLocalBoundsNegativeCoords() {
        Triangle t = new Triangle(
                Vector.point(-3, -2, -1),
                Vector.point(-1, -4, -2),
                Vector.point(-2, -1, -3)
        );
        AABB b = t.bounds();
        assertEquals(-3, b.min.x, EPS);
        assertEquals(-4, b.min.y, EPS);
        assertEquals(-3, b.min.z, EPS);
        assertEquals(-1, b.max.x, EPS);
        assertEquals(-1, b.max.y, EPS);
        assertEquals(-1, b.max.z, EPS);
    }

    @Test
    void triangleLocalBoundsMixedCoords() {
        Triangle t = new Triangle(
                Vector.point(-1, 0, 1),
                Vector.point( 1, 2, 0),
                Vector.point( 0, 1, 3)
        );
        AABB b = t.bounds();
        assertEquals(-1, b.min.x, EPS);
        assertEquals( 0, b.min.y, EPS);
        assertEquals( 0, b.min.z, EPS);
        assertEquals( 1, b.max.x, EPS);
        assertEquals( 2, b.max.y, EPS);
        assertEquals( 3, b.max.z, EPS);
    }

    @Test
    void triangleLocalBoundsFlatOnXZPlane() {
        Triangle t = new Triangle(
                Vector.point(-1, 0,  1),
                Vector.point( 1, 0,  1),
                Vector.point( 0, 0, -1)
        );
        AABB b = t.bounds();
        assertEquals(-1, b.min.x, EPS);
        assertEquals( 0, b.min.y, EPS);
        assertEquals(-1, b.min.z, EPS);
        assertEquals( 1, b.max.x, EPS);
        assertEquals( 0, b.max.y, EPS);
        assertEquals( 1, b.max.z, EPS);
    }

    @Test
    void triangleBoundsContainsAllVertices() {
        Triangle t = new Triangle(
                Vector.point(1, 2, 3),
                Vector.point(4, 0, 1),
                Vector.point(2, 5, 2)
        );
        AABB b = t.bounds();

        for (Vector p : List.of(t.p1, t.p2, t.p3)) {
            assertTrue(p.x >= b.min.x && p.x <= b.max.x);
            assertTrue(p.y >= b.min.y && p.y <= b.max.y);
            assertTrue(p.z >= b.min.z && p.z <= b.max.z);
        }
    }
    @Test
    void triangleUVInterpolation() {
        Triangle t = new Triangle(
                Vector.point(0,0,0), Vector.point(1,0,0), Vector.point(0,1,0)
        );
        t.uv1 = new double[]{0,0};
        t.uv2 = new double[]{1,0};
        t.uv3 = new double[]{0,1};
        Ray r = new Ray(Vector.point(0.25, 0.25, -1), Vector.vector3(0, 0, 1));
        Intersection hit = Intersection.hit(t.intersect(r));
        assertEquals(0.25, hit.texU, 0.001);
        assertEquals(0.25, hit.texV, 0.001);
    }
}