import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.thomas.material.Material;
import org.thomas.material.PhongShadingModel;
import org.thomas.math.AABB;
import org.thomas.math.Matrix;
import org.thomas.raytracer.Intersection;
import org.thomas.raytracer.Ray;
import org.thomas.shape.Sphere;
import org.thomas.math.Vector;

import java.util.List;

public class SphereTest {
    @Test void rayIntersectsSphereAtTwoPoints()
    {
        Ray r = new Ray(Vector.point(0,0,-5), Vector.vector3(0,0,1));
        Sphere s = new Sphere();
        List<Intersection> xs = s.intersect(r);
        assertEquals(2, xs.size());
        assertEquals(4.0, xs.get(0).t);
        assertEquals(6.0, xs.get(1).t);
    }

    @Test void rayIntersectsSphereAtTangent()
    {
        Ray r = new Ray(Vector.point(0,1,-5), Vector.vector3(0,0,1));
        Sphere s = new Sphere();
        List<Intersection> xs = s.intersect(r);
        assertEquals(2, xs.size());
        assertEquals(5.0, xs.get(0).t);
        assertEquals(5.0, xs.get(1).t);
    }

    @Test void rayMissesSphere()
    {
        Ray r = new Ray(Vector.point(0,2,-5), Vector.vector3(0,0,1));
        Sphere s = new Sphere();
        List<Intersection> xs = s.intersect(r);
        assertEquals(0, xs.size());
    }

    @Test void rayOriginatesInSphere()
    {
        Ray r = new Ray(Vector.point(0,0,0), Vector.vector3(0,0,1));
        Sphere s = new Sphere();
        List<Intersection> xs = s.intersect(r);
        assertEquals(2, xs.size());
        assertEquals(-1.0, xs.get(0).t);
        assertEquals(1.0, xs.get(1).t);
    }

    @Test void sphereIsBehindRay()
    {
        Ray r = new Ray(Vector.point(0,0,5), Vector.vector3(0,0,1));
        Sphere s = new Sphere();
        List<Intersection> xs = s.intersect(r);
        assertEquals(2, xs.size());
        assertEquals(-6.0, xs.get(0).t);
        assertEquals(-4.0, xs.get(1).t);
    }

    @Test void intersectSetsObjectOnIntersection()
    {
        Ray r = new Ray(Vector.point(0,0,-5), Vector.vector3(0,0,1));
        Sphere s = new Sphere();
        List<Intersection> xs = s.intersect(r);
        assertEquals(2, xs.size());
        assertEquals(s, xs.get(0).shape);
        assertEquals(s, xs.get(1).shape);
    }

    @Test void defaultTransform()
    {
        Sphere s = new Sphere();
        assertEquals(s.getTransform(), Matrix.identityMatrix());
    }

    @Test void changingASpheresTransform(){
        Sphere s = new Sphere();
        Matrix t = Matrix.translation(2,3,4);
        s.setTransform(t);
        assertEquals(t, s.getTransform());
    }

    @Test void intersectingWithScaledSphere(){
        Ray r = new  Ray(Vector.point(0,0,-5), Vector.vector3(0,0,1));
        Sphere s = new Sphere();
        s.setTransform(Matrix.scalar(2,2,2));
        List<Intersection> xs = s.intersect(r);
        assertEquals(2, xs.size());
        assertEquals(3.0, xs.get(0).t);
        assertEquals(7.0, xs.get(1).t);
    }

    @Test void intersectingTranslatedSphereWithRay()
    {
        Ray r = new Ray(Vector.point(0,0,-5), Vector.vector3(0,0,1));
        Sphere s = new Sphere();
        s.setTransform(Matrix.translation(5,0,0));
        List<Intersection> xs = s.intersect(r);
        assertEquals(0, xs.size());
    }

    @Test void normalOnSphereOnXAxis()
    {
        Sphere s = new Sphere();
        Vector n = s.getNormal(Vector.point(1,0,0));
        assertEquals(Vector.vector3(1,0,0), n);
    }
    @Test void normalOnSphereOnYAxis()
    {
        Sphere s = new Sphere();
        Vector n = s.getNormal(Vector.point(0,1,0));
        assertEquals(Vector.vector3(0,1,0), n);
    }
    @Test void normalOnSphereOnZAxis()
    {
        Sphere s = new Sphere();
        Vector n = s.getNormal(Vector.point(0,0,1));
        assertEquals(Vector.vector3(0,0,1), n);
    }

    @Test void normalOnSphereNonaxialPoint()
    {
        Sphere s = new Sphere();
        Vector n = s.getNormal(Vector.point(Math.sqrt(3)/3,Math.sqrt(3)/3,Math.sqrt(3)/3));
        assertEquals(Vector.vector3(Math.sqrt(3)/3,Math.sqrt(3)/3,Math.sqrt(3)/3), n);
    }

    @Test void normalOnTranslatedSphere()
    {
        Sphere s = new Sphere();
        s.setTransform(Matrix.translation(0,1,0));
        Vector n = s.getNormal(Vector.point(0,1.70711,-0.70711));
        assertEquals(Vector.vector3(0,0.70711,-0.70711), n);
    }

    @Test void normalOnTransformedSphere()
    {
        Sphere s = new Sphere();
        Matrix m = Matrix.scalar(1,0.5,1).multiply(Matrix.rotationZ(Math.PI/5));
        s.setTransform(m);
        Vector n = s.getNormal(Vector.point(0,Math.sqrt(2)/2,-Math.sqrt(2)/2));
        assertEquals(Vector.vector3(0,0.97014, -0.24254), n);
    }

    @Test void sphereHasDefaultMaterial()
    {
        Sphere s = new Sphere();
        Material m = s.getMaterial();
        assertEquals(0.1, ((PhongShadingModel)m.shadingModel).ambient);
        assertEquals(s.getMaterial(), m);
    }

    @Test void glassSphereHasValidCreation()
    {
        Sphere s = Sphere.glassSphere();
        assertEquals(Matrix.identityMatrix(), s.getTransform());
        assertEquals(1.0, s.getMaterial().transparency);
        assertEquals(1.5, s.getMaterial().refractive);
    }

    @Test
    void sphereLocalBoundsIsUnitCube() {
        Sphere s = new Sphere();
        AABB b = s.bounds();
        assertEquals(Vector.point(-1, -1, -1), b.min);
        assertEquals(Vector.point( 1,  1,  1), b.max);
    }

    private static final double EPS = 1e-9;

    @Test
    void sphereWorldBoundsWithNoTransformMatchesLocal() {
        Sphere s = new Sphere();
        AABB wb = s.bounds();
        assertEquals(-1, wb.min.x, EPS);
        assertEquals( 1, wb.max.x, EPS);
    }

    @Test
    void sphereWorldBoundsAfterUniformScale() {
        Sphere s = new Sphere();
        s.setTransform(Matrix.scalar(2, 2, 2));
        AABB wb = s.bounds();
        assertEquals(-2, wb.min.x, EPS);
        assertEquals( 2, wb.max.x, EPS);
        assertEquals(-2, wb.min.y, EPS);
        assertEquals( 2, wb.max.y, EPS);
    }

    @Test
    void sphereWorldBoundsAfterNonUniformScale() {
        Sphere s = new Sphere();
        s.setTransform(Matrix.scalar(1, 2, 3));
        AABB wb = s.bounds();
        assertEquals(-1, wb.min.x, EPS);
        assertEquals( 1, wb.max.x, EPS);
        assertEquals(-2, wb.min.y, EPS);
        assertEquals( 2, wb.max.y, EPS);
        assertEquals(-3, wb.min.z, EPS);
        assertEquals( 3, wb.max.z, EPS);
    }

    @Test
    void sphereWorldBoundsAfterTranslation() {
        Sphere s = new Sphere();
        s.setTransform(Matrix.translation(3, 0, 0));
        AABB wb = s.bounds();
        assertEquals( 2, wb.min.x, EPS);
        assertEquals( 4, wb.max.x, EPS);
        assertEquals(-1, wb.min.y, EPS);
        assertEquals( 1, wb.max.y, EPS);
    }

    @Test
    void sphereWorldBoundsAfterTranslationAndScale() {
        Sphere s = new Sphere();
        s.setTransform(Matrix.translation(0, 1, 0));
        AABB wb = s.bounds();
        assertEquals( 0, wb.min.y, EPS);
        assertEquals( 2, wb.max.y, EPS);
    }

    @Test
    void sphereUVAtEquator() {
        Sphere s = new Sphere();
        Ray r = new Ray(Vector.point(2, 0, 0), Vector.vector3(-1, 0, 0)); // start outside
        List<Intersection> xs = s.intersect(r);
        Intersection hit = Intersection.hit(xs);
        assertEquals(0.5, hit.texU, 0.001); // hits (1,0,0), atan2(0,1)=0
        assertEquals(0.5, hit.texV, 0.001);
    }

    @Test
    void sphereUVOnPositiveZ() {
        Sphere s = new Sphere();
        Ray r = new Ray(Vector.point(0, 0, 2), Vector.vector3(0, 0, -1));
        List<Intersection> xs = s.intersect(r);
        Intersection hit = Intersection.hit(xs);
        assertEquals(0.75, hit.texU, 0.001); // atan2(1,0) = π/2 -> 0.5+0.25=0.75
        assertEquals(0.5,  hit.texV, 0.001);
    }

    @Test
    void sphereUVAtTopPole() {
        Sphere s = new Sphere();
        Ray r = new Ray(Vector.point(0, 2, 0), Vector.vector3(0, -1, 0));
        List<Intersection> xs = s.intersect(r);
        Intersection hit = Intersection.hit(xs);
        assertEquals(0.0, hit.texV, 0.001);
    }
}