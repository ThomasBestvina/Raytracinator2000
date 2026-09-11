import org.junit.jupiter.api.Test;
import org.thomas.math.AABB;
import org.thomas.math.DoubleMath;
import org.thomas.math.Matrix;
import org.thomas.math.Vector;
import org.thomas.raytracer.Intersection;
import org.thomas.raytracer.Ray;
import org.thomas.shape.Cube;
import org.thomas.shape.Shape;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CubeTest {
    @Test
    void rayHitsCubeTest() {
        Shape c = new Cube();

        assertTrue(rayHitsCubeTest(c, Vector.point(5, 0.5, 0), Vector.vector3(-1, 0, 0), 4, 6));
        assertTrue(rayHitsCubeTest(c, Vector.point(-5, 0.5, 0), Vector.vector3(1, 0, 0), 4, 6));
        assertTrue(rayHitsCubeTest(c, Vector.point(0.5, 5, 0), Vector.vector3(0, -1, 0), 4, 6));
        assertTrue(rayHitsCubeTest(c, Vector.point(0.5, -5, 0), Vector.vector3(0, 1, 0), 4, 6));
        assertTrue(rayHitsCubeTest(c, Vector.point(0.5, 0, 5), Vector.vector3(0, 0, -1), 4, 6));
        assertTrue(rayHitsCubeTest(c, Vector.point(0.5, 0, -5), Vector.vector3(0, 0, 1), 4, 6));
        assertTrue(rayHitsCubeTest(c, Vector.point(0, 0.5, 0), Vector.vector3(0, 0, 1), -1, 1));
    }

    private boolean rayHitsCubeTest(Shape s, Vector origin, Vector direction, double t1, double t2)
    {
        Ray ray = new Ray(origin, direction);
        List<Intersection> xs = s.intersect(ray);
        return DoubleMath.equal(t1, xs.get(0).t) &&  DoubleMath.equal(t2, xs.get(1).t);
    }

    @Test
    void rayMissesCube()
    {
        Cube c = new Cube();

        assertTrue(rayMissesCubeTest(c, Vector.point(-2,0,0), Vector.vector3(0.2673, 0.5345, 0.8018)));
        assertTrue(rayMissesCubeTest(c, Vector.point(0,-2,0), Vector.vector3(0.8018, 0.2673, 0.5345)));
        assertTrue(rayMissesCubeTest(c, Vector.point(0,0,-2), Vector.vector3(0.5345, 0.8018, 0.2673)));
        assertTrue(rayMissesCubeTest(c, Vector.point(2,0,2), Vector.vector3(0, 0, -1)));
        assertTrue(rayMissesCubeTest(c, Vector.point(0,2,2), Vector.vector3(0, -1, 0)));
        assertTrue(rayMissesCubeTest(c, Vector.point(2,2,0), Vector.vector3(-1, 0, 0)));
    }

    private boolean rayMissesCubeTest(Shape s, Vector origin, Vector direction)
    {
        Ray ray = new Ray(origin, direction);
        List<Intersection> xs = s.intersect(ray);
        return xs.isEmpty();
    }

    @Test
    void normalOnSurfaceOfCube()
    {
        Cube c = new Cube();
        normalOnSurfaceOfCube(c, Vector.point(1,0.5,-0.8), Vector.vector3(1,0,0));
        normalOnSurfaceOfCube(c, Vector.point(-1,-0.2,0.9), Vector.vector3(-1,0,0));
        normalOnSurfaceOfCube(c, Vector.point(-0.4,1,-0.1), Vector.vector3(0,1,0));
        normalOnSurfaceOfCube(c, Vector.point(0.3,-1,-0.7), Vector.vector3(0,-1,0));
        normalOnSurfaceOfCube(c, Vector.point(-0.6,0.3,1), Vector.vector3(0,0,1));
        normalOnSurfaceOfCube(c, Vector.point(0.4,0.4,-1), Vector.vector3(0,0,-1));
        normalOnSurfaceOfCube(c, Vector.point(1,1,1), Vector.vector3(1,0,0));
        normalOnSurfaceOfCube(c, Vector.point(-1,-1,-1), Vector.vector3(-1,0,0));
    }

    private boolean normalOnSurfaceOfCube(Shape s, Vector point, Vector expected)
    {
        return s.getNormal(point).equals(expected);
    }

    @Test
    void cubeLocalBoundsIsUnitCube() {
        Cube c = new Cube();
        AABB b = c.bounds();
        assertEquals(Vector.point(-1, -1, -1), b.min);
        assertEquals(Vector.point( 1,  1,  1), b.max);
    }

    private static final double EPS = 1e-9;

    @Test
    void cubeWorldBoundsAfterUniformScale() {
        Cube c = new Cube();
        c.setTransform(Matrix.scalar(2, 2, 2));
        AABB wb = c.bounds();
        assertEquals(-2, wb.min.x, EPS);
        assertEquals( 2, wb.max.x, EPS);
    }

    @Test
    void cubeWorldBoundsAfterRotation45Degrees() {
        Cube c = new Cube();
        c.setTransform(Matrix.rotationY(Math.PI / 4));
        AABB wb = c.bounds();
        double expectedHalf = Math.sqrt(2);
        assertEquals(-expectedHalf, wb.min.x, 1e-6);
        assertEquals( expectedHalf, wb.max.x, 1e-6);
        assertEquals(-1, wb.min.y, 1e-6);
        assertEquals( 1, wb.max.y, 1e-6);
    }

    @Test
    void cubeWorldBoundsMatchesMainBlockCubeTransform() {
        Cube c = new Cube();
        c.setTransform(
                Matrix.translation(3.8, 0.4, 2.5)
                        .multiply(Matrix.rotationY(Math.PI / 5))
                        .multiply(Matrix.scalar(0.4, 0.4, 0.4))
        );
        AABB wb = c.bounds();
        Vector centroid = wb.centroid();
        assertEquals(3.8, centroid.x, 0.05);
        assertEquals(0.4, centroid.y, 0.05);
        assertEquals(2.5, centroid.z, 0.05);
        // Extent must be positive on all axes
        assertTrue(wb.max.x > wb.min.x);
        assertTrue(wb.max.y > wb.min.y);
        assertTrue(wb.max.z > wb.min.z);
    }
}