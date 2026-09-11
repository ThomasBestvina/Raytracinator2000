import org.junit.jupiter.api.Test;
import org.thomas.math.AABB;
import org.thomas.math.Vector;
import org.thomas.raytracer.Ray;

import static org.junit.jupiter.api.Assertions.*;

public class AABBTest {

    @Test
    void emptyHasInvertedInfinityBounds() {
        assertEquals(Double.POSITIVE_INFINITY, AABB.empty().min.x);
        assertEquals(Double.NEGATIVE_INFINITY, AABB.empty().max.x);
    }

    @Test
    void constructorStoresMinAndMax() {
        AABB box = new AABB(Vector.point(-1, -2, -3), Vector.point(1, 2, 3));
        assertEquals(-1, box.min.x);
        assertEquals(3,  box.max.z);
    }


    @Test
    void mergeProducesEnclosingBox() {
        AABB a = new AABB(Vector.point(-1, -1, -1), Vector.point(0,  0,  0));
        AABB b = new AABB(Vector.point( 0,  0,  0), Vector.point(1,  1,  1));
        AABB merged = a.merge(b);

        assertEquals(-1, merged.min.x);
        assertEquals(-1, merged.min.y);
        assertEquals(-1, merged.min.z);
        assertEquals( 1, merged.max.x);
        assertEquals( 1, merged.max.y);
        assertEquals( 1, merged.max.z);
    }

    @Test
    void mergingWithEmptyReturnsOriginal() {
        AABB box = new AABB(Vector.point(-1, -1, -1), Vector.point(1, 1, 1));
        AABB result = AABB.empty().merge(box);
        assertEquals(box.min.x, result.min.x);
        assertEquals(box.max.x, result.max.x);
    }

    @Test
    void mergingOverlappingBoxes() {
        AABB a = new AABB(Vector.point(-2, -2, -2), Vector.point(1, 1, 1));
        AABB b = new AABB(Vector.point(-1, -1, -1), Vector.point(3, 3, 3));
        AABB merged = a.merge(b);
        assertEquals(-2, merged.min.x);
        assertEquals( 3, merged.max.x);
    }


    @Test
    void centroidIsMiddleOfBox() {
        AABB box = new AABB(Vector.point(0, 0, 0), Vector.point(4, 6, 8));
        Vector c = box.centroid();
        assertEquals(2, c.x, 1e-9);
        assertEquals(3, c.y, 1e-9);
        assertEquals(4, c.z, 1e-9);
    }

    @Test
    void centroidOfSymmetricBox() {
        AABB box = new AABB(Vector.point(-1, -1, -1), Vector.point(1, 1, 1));
        Vector c = box.centroid();
        assertEquals(0, c.x, 1e-9);
        assertEquals(0, c.y, 1e-9);
        assertEquals(0, c.z, 1e-9);
    }


    @Test
    void rayAlongXAxisHitsUnitCubeBox() {
        AABB box = new AABB(Vector.point(-1, -1, -1), Vector.point(1, 1, 1));
        Ray r = new Ray(Vector.point(-5, 0, 0), Vector.vector3(1, 0, 0));
        assertTrue(box.intersects(r));
    }

    @Test
    void rayAlongYAxisHitsUnitCubeBox() {
        AABB box = new AABB(Vector.point(-1, -1, -1), Vector.point(1, 1, 1));
        Ray r = new Ray(Vector.point(0, -5, 0), Vector.vector3(0, 1, 0));
        assertTrue(box.intersects(r));
    }

    @Test
    void rayAlongZAxisHitsUnitCubeBox() {
        AABB box = new AABB(Vector.point(-1, -1, -1), Vector.point(1, 1, 1));
        Ray r = new Ray(Vector.point(0, 0, -5), Vector.vector3(0, 0, 1));
        assertTrue(box.intersects(r));
    }


    @Test
    void diagonalRayThroughCornerHitsBox() {
        AABB box = new AABB(Vector.point(-1, -1, -1), Vector.point(1, 1, 1));
        Ray r = new Ray(Vector.point(-5, -5, -5),
                Vector.vector3(1, 1, 1).normalize());
        assertTrue(box.intersects(r));
    }

    @Test
    void rayCompletelyMissesBox() {
        AABB box = new AABB(Vector.point(-1, -1, -1), Vector.point(1, 1, 1));
        Ray r = new Ray(Vector.point(-5, 5, 0), Vector.vector3(1, 0, 0));
        assertFalse(box.intersects(r));
    }

    @Test
    void rayOriginatingInsideBoxAlwaysHits() {
        AABB box = new AABB(Vector.point(-1, -1, -1), Vector.point(1, 1, 1));
        Ray r = new Ray(Vector.point(0, 0, 0), Vector.vector3(1, 1, 1).normalize());
        assertTrue(box.intersects(r));
    }

    @Test
    void rayPointingAwayFromBoxMisses() {
        AABB box = new AABB(Vector.point(2, 2, 2), Vector.point(4, 4, 4));
        Ray r = new Ray(Vector.point(0, 0, 0), Vector.vector3(-1, -1, -1).normalize());
        assertFalse(box.intersects(r));
    }


    @Test
    void rayParallelToXFaceAndOutsideMisses() {
        AABB box = new AABB(Vector.point(-1, -1, -1), Vector.point(1, 1, 1));
        Ray r = new Ray(Vector.point(-5, 2, 0), Vector.vector3(1, 0, 0));
        assertFalse(box.intersects(r));
    }

    @Test
    void rayParallelToFaceAndInsideHits() {
        AABB box = new AABB(Vector.point(-1, -1, -1), Vector.point(1, 1, 1));
        Ray r = new Ray(Vector.point(-5, 0, 0), Vector.vector3(1, 0, 0));
        assertTrue(box.intersects(r));
    }

    @Test
    void rayHitsNonUnitBox() {
        AABB box = new AABB(Vector.point(-3, -1, -2), Vector.point(3, 5, 2));
        Ray r = new Ray(Vector.point(0, -10, 0), Vector.vector3(0, 1, 0));
        assertTrue(box.intersects(r));
    }

    @Test
    void rayMissesNonUnitBox() {
        AABB box = new AABB(Vector.point(-1, -1, -1), Vector.point(1, 1, 1));
        Ray r = new Ray(Vector.point(5, 5, 5), Vector.vector3(1, 0, 0));
        assertFalse(box.intersects(r));
    }
}
