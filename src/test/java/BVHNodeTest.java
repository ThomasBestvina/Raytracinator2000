import org.junit.jupiter.api.Test;
import org.thomas.math.AABB;
import org.thomas.math.Color;
import org.thomas.math.Matrix;
import org.thomas.math.Vector;
import org.thomas.raytracer.Intersection;
import org.thomas.raytracer.Ray;
import org.thomas.shape.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for BVHNode: tree construction, bounding box computation, ray traversal,
 * and integration with World / Group via the standard Shape intersect() contract.
 */
class BVHNodeTest {

    private static final double EPS = 1e-6;

    private Sphere sphereAt(double x, double y, double z) {
        Sphere s = new Sphere();
        s.setTransform(Matrix.translation(x, y, z));
        return s;
    }

    private Cube cubeAt(double x, double y, double z) {
        Cube c = new Cube();
        c.setTransform(Matrix.translation(x, y, z));
        return c;
    }

    @Test
    void buildSingleShapeReturnsBVHNodeWrappingIt() {
        Sphere s = sphereAt(0, 0, 0);
        BVHNode node = BVHNode.build(List.of(s));
        assertNotNull(node);
    }

    @Test
    void buildSingleShapeBoundsMatchShapeBounds() {
        Sphere s = sphereAt(2, 0, 0);
        BVHNode node = BVHNode.build(List.of(s));
        AABB nb = node.bounds();
        AABB sb = s.bounds();
        assertEquals(sb.min.x, nb.min.x, EPS);
        assertEquals(sb.max.x, nb.max.x, EPS);
    }

    @Test
    void buildTwoShapesReturnsBVHNodeWithMergedBounds() {
        Sphere left  = sphereAt(-3, 0, 0);
        Sphere right = sphereAt( 3, 0, 0);
        BVHNode node = BVHNode.build(List.of(left, right));
        AABB b = node.bounds();
        assertTrue(b.min.x <= -3, "left sphere not included in bounds");
        assertTrue(b.max.x >=  3, "right sphere not included in bounds");
    }

    @Test
    void buildManyShapesBoundsEncloseAll() {
        List<Shape> shapes = List.of(
                sphereAt(-5,  0,  0),
                sphereAt( 5,  0,  0),
                sphereAt( 0, -4,  0),
                sphereAt( 0,  4,  0),
                sphereAt( 0,  0, -6),
                sphereAt( 0,  0,  6)
        );
        BVHNode node = BVHNode.build(shapes);
        AABB b = node.bounds();

        for (Shape s : shapes) {
            AABB sb = s.bounds();
            assertTrue(sb.min.x >= b.min.x - EPS, "child min.x outside BVH bounds");
            assertTrue(sb.max.x <= b.max.x + EPS, "child max.x outside BVH bounds");
            assertTrue(sb.min.y >= b.min.y - EPS, "child min.y outside BVH bounds");
            assertTrue(sb.max.y <= b.max.y + EPS, "child max.y outside BVH bounds");
            assertTrue(sb.min.z >= b.min.z - EPS, "child min.z outside BVH bounds");
            assertTrue(sb.max.z <= b.max.z + EPS, "child max.z outside BVH bounds");
        }
    }

    @Test
    void buildWithMixedShapeTypesBoundsAreCorrect() {
        List<Shape> shapes = List.of(
                sphereAt(-2, 0, 0),
                cubeAt(  2, 0, 0)
        );
        BVHNode node = BVHNode.build(shapes);
        AABB b = node.bounds();
        assertTrue(b.min.x <= -3);
        assertTrue(b.max.x >=  3);
    }

    @Test
    void rayHittingOneChildReturnsIntersections() {
        Sphere s = sphereAt(0, 0, 0);
        BVHNode node = BVHNode.build(List.of(s));
        Ray r = new Ray(Vector.point(0, 0, -5), Vector.vector3(0, 0, 1));
        List<Intersection> xs = node.intersect(r);
        assertFalse(xs.isEmpty(), "Expected intersections with sphere");
    }

    @Test
    void rayHittingBothChildrenReturnsBothIntersections() {
        Sphere left  = sphereAt(-2, 0, 0);
        Sphere right = sphereAt( 2, 0, 0);
        BVHNode node = BVHNode.build(List.of(left, right));

        Ray rLeft  = new Ray(Vector.point(-2, 0, -5), Vector.vector3(0, 0, 1));
        Ray rRight = new Ray(Vector.point( 2, 0, -5), Vector.vector3(0, 0, 1));

        assertFalse(node.intersect(rLeft).isEmpty(),  "Expected hit on left sphere");
        assertFalse(node.intersect(rRight).isEmpty(), "Expected hit on right sphere");
    }

    @Test
    void rayMissingBvhBoundsReturnsEmpty() {
        Sphere s = sphereAt(0, 0, 0);
        BVHNode node = BVHNode.build(List.of(s));

        Ray r = new Ray(Vector.point(0, 100, -5), Vector.vector3(0, 0, 1));
        assertTrue(node.intersect(r).isEmpty(), "Expected no intersections when ray misses BVH box");
    }

    @Test
    void rayMissingOneChildDoesNotReturnItsIntersections() {
        Sphere left  = sphereAt(-2, 0, 0);
        Sphere right = sphereAt( 2, 0, 0);
        BVHNode node = BVHNode.build(List.of(left, right));

        Ray r = new Ray(Vector.point(-2, 0, -5), Vector.vector3(0, 0, 1));
        List<Intersection> xs = node.intersect(r);

        for (Intersection i : xs) {
            assertSame(left, i.shape,
                    "Intersection returned for non-targeted sphere");
        }
    }

    @Test
    void intersectionsAreSortedByT() {
        // Two spheres along the same ray
        Sphere near = sphereAt(0, 0,  0);
        Sphere far  = sphereAt(0, 0, 10);
        BVHNode node = BVHNode.build(List.of(near, far));
        Ray r = new Ray(Vector.point(0, 0, -5), Vector.vector3(0, 0, 1));
        List<Intersection> xs = node.intersect(r);
        assertFalse(xs.isEmpty());
        for (int i = 1; i < xs.size(); i++) {
            assertTrue(xs.get(i).t >= xs.get(i-1).t,
                    "Intersections out of order at index " + i);
        }
    }

    @Test
    void bvhAndBruteForceAgreeOnHitsForManyShapes() {
        List<Shape> shapes = List.of(
                sphereAt(-4, 0, 0),
                sphereAt(-2, 0, 0),
                sphereAt( 0, 0, 0),
                sphereAt( 2, 0, 0),
                sphereAt( 4, 0, 0)
        );

        BVHNode node = BVHNode.build(shapes);

        List<Ray> rays = List.of(
                new Ray(Vector.point( 0, 0, -5), Vector.vector3(0, 0, 1)),
                new Ray(Vector.point(-4, 0, -5), Vector.vector3(0, 0, 1)),
                new Ray(Vector.point( 4, 0, -5), Vector.vector3(0, 0, 1)),
                new Ray(Vector.point( 0, 5, -5), Vector.vector3(0, 0, 1))  // miss
        );

        for (Ray r : rays) {
            int bruteCount = 0;
            for (Shape s : shapes) bruteCount += s.intersect(r).size();

            int bvhCount = node.intersect(r).size();
            assertEquals(bruteCount, bvhCount,
                    "BVH and brute-force disagree for ray " + r);
        }
    }

    @Test
    void bvhWithManyShapesIsNotFlatList() {
        List<Shape> shapes = List.of(
                sphereAt(-6, 0, 0), sphereAt(-4, 0, 0),
                sphereAt(-2, 0, 0), sphereAt( 0, 0, 0),
                sphereAt( 2, 0, 0), sphereAt( 4, 0, 0),
                sphereAt( 6, 0, 0)
        );
        BVHNode node = BVHNode.build(shapes);
        AABB top = node.bounds();

        assertTrue(top.min.x <= -7 + EPS);
        assertTrue(top.max.x >=  7 - EPS);
    }

    @Test
    void bvhNodeWorldBoundsIsMergeOfAllChildWorldBounds() {
        Sphere a = sphereAt(-3,  2, 0);
        Sphere b = sphereAt( 1, -1, 5);
        BVHNode node = BVHNode.build(List.of(a, b));

        AABB expected = a.bounds().merge(b.bounds());
        AABB actual   = node.bounds();

        assertEquals(expected.min.x, actual.min.x, EPS);
        assertEquals(expected.min.y, actual.min.y, EPS);
        assertEquals(expected.min.z, actual.min.z, EPS);
        assertEquals(expected.max.x, actual.max.x, EPS);
        assertEquals(expected.max.y, actual.max.y, EPS);
        assertEquals(expected.max.z, actual.max.z, EPS);
    }

    @Test
    void bvhNodeWithTransformOnChildStillIntersectsCorrectly() {
        Sphere s = new Sphere();

        s.setTransform(Matrix.translation(5, 0, 0).multiply(Matrix.scalar(3, 3, 3)));
        BVHNode node = BVHNode.build(List.of(s));

        Ray hit = new Ray(Vector.point(5, 0, -10), Vector.vector3(0, 0, 1));
        assertFalse(node.intersect(hit).isEmpty());

        Ray miss = new Ray(Vector.point(-5, 0, -10), Vector.vector3(0, 0, 1));
        assertTrue(node.intersect(miss).isEmpty());
    }

    @Test
    void bvhNodeCanBeAddedAsChildOfGroup() {
        Sphere s1 = sphereAt(-1, 0, 0);
        Sphere s2 = sphereAt( 1, 0, 0);
        BVHNode bvh = BVHNode.build(List.of(s1, s2));

        Group g = new Group();
        g.addChild(bvh);

        Ray r = new Ray(Vector.point(0, 0, -5), Vector.vector3(0, 0, 1));
        List<Intersection> xs = g.intersect(r);
        assertFalse(xs.isEmpty(), "Group containing BVH should produce intersections");
    }
}