import org.junit.jupiter.api.Test;
import org.thomas.math.DoubleMath;
import org.thomas.math.Matrix;
import org.thomas.math.Vector;
import org.thomas.raytracer.Intersection;
import org.thomas.raytracer.IntersectionComputation;
import org.thomas.raytracer.Ray;
import org.thomas.shape.Shape;
import org.thomas.shape.Sphere;
import org.thomas.shape.Triangle;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IntersectionTest {
    @Test void aggregatingIntersections()
    {
        Sphere s = new Sphere();
        Intersection i1 = new Intersection(1,s);
        Intersection i2 = new Intersection(2,s);
        List<Intersection> xs = Intersection.intersections(new Intersection[] {i1,i2});
        assertEquals(2,xs.size());
        assertEquals(1,xs.get(0).t);
        assertEquals(2,xs.get(1).t);

    }
    @Test
    void hitWhenAllIntersectionsHavePositiveT() {
        Sphere s = new Sphere();
        Intersection i1 = new Intersection(1, s);
        Intersection i2 = new Intersection(2, s);
        List<Intersection> xs = Intersection.intersections(new Intersection[]{i2, i1});
        Intersection i = Intersection.hit(xs);
        assertEquals(i1, i);
    }

    @Test
    void hitWhenSomeIntersectionsHaveNegativeT() {
        Sphere s = new Sphere();
        Intersection i1 = new Intersection(-1, s);
        Intersection i2 = new Intersection(1, s);
        List<Intersection> xs = Intersection.intersections(new Intersection[]{i2, i1});
        Intersection i = Intersection.hit(xs);
        assertEquals(i2, i);
    }

    @Test
    void hitWhenAllIntersectionsHaveNegativeT() {
        Sphere s = new Sphere();
        Intersection i1 = new Intersection(-2, s);
        Intersection i2 = new Intersection(-1, s);
        List<Intersection> xs = Intersection.intersections(new Intersection[]{i2, i1});
        Intersection i = Intersection.hit(xs);
        assertNull(i);
    }

    @Test
    void hitIsAlwaysLowestNonnegativeIntersection() {
        Sphere s = new Sphere();
        Intersection i1 = new Intersection(5, s);
        Intersection i2 = new Intersection(7, s);
        Intersection i3 = new Intersection(-3, s);
        Intersection i4 = new Intersection(2, s);
        List<Intersection> xs = Intersection.intersections(new Intersection[]{i1, i2, i3, i4});
        Intersection i = Intersection.hit(xs);
        assertEquals(i4, i);
    }

    @Test
    void hitIgnoresIntersectionsAtTheRayOrigin() {
        Sphere s = new Sphere();
        Intersection i1 = new Intersection(DoubleMath.EPSILON / 2, s);
        Intersection i2 = new Intersection(1, s);
        List<Intersection> xs = Intersection.intersections(new Intersection[]{i1, i2});
        Intersection i = Intersection.hit(xs);
        assertEquals(i2, i);
    }

    @Test
    void PrecomputingIntersectionState()
    {
        Ray r =  new Ray(Vector.point(0,0,-5), Vector.vector3(0,0,1));
        Shape s = new Sphere();
        Intersection i1 = new Intersection(4,s);
        IntersectionComputation comps = i1.prepareComputation(r);
        assertEquals(i1.t, comps.t);
        assertEquals(i1.shape, comps.shape);
        assertEquals(Vector.point(0,0,-1), comps.point);
        assertEquals(Vector.vector3(0,0,-1),comps.eyev);
        assertEquals(Vector.vector3(0,0,-1), comps.normalv);
        assertFalse(comps.inside);
    }

    @Test
    void PrecomputeIntersectionOnInside()
    {
        Ray r =  new Ray(Vector.point(0,0,0), Vector.vector3(0,0,1));
        Shape s = new Sphere();
        Intersection i1 = new Intersection(1,s);
        IntersectionComputation comps = i1.prepareComputation(r);
        assertEquals(i1.t, comps.t);
        assertEquals(i1.shape, comps.shape);
        assertEquals(Vector.point(0,0,1), comps.point);
        assertEquals(Vector.vector3(0,0,-1),comps.eyev);
        assertEquals(Vector.vector3(0,0,-1), comps.normalv);
        assertTrue(comps.inside);
    }

    @Test
    void hitShouldOffsetPoint()
    {
        Ray r = new Ray(Vector.point(0,0,-5), Vector.vector3(0,0,1));
        Shape s = new Sphere();
        s.setTransform(Matrix.translation(0,0,1));
        Intersection i1 = new Intersection(5,s);
        IntersectionComputation comps = i1.prepareComputation(r);
        assertEquals(-DoubleMath.EPSILON, comps.overPoint.z);
        assertTrue(comps.point.z > comps.overPoint.z);
    }

    @Test void Findingn1n2AtVariousIntersections()
    {
        Sphere a = Sphere.glassSphere();
        a.setTransform(Matrix.scalar(2,2,2));
        a.getMaterial().refractive = 1.5;

        Sphere b = Sphere.glassSphere();
        b.setTransform(Matrix.translation(0,0,-0.25));
        b.getMaterial().refractive = 2.0;

        Sphere c = Sphere.glassSphere();
        c.setTransform(Matrix.translation(0,0,0.25));
        c.getMaterial().refractive = 2.5;

        Ray r = new Ray(Vector.point(0,0,-4), Vector.vector3(0,0,1));
        List<Intersection> xs = Intersection.intersections(new Intersection[] {
                new Intersection(2,a),
                new Intersection(2.75,b),
                new Intersection(3.25,c),
                new Intersection(4.75,b),
                new Intersection(5.25,c),
                new Intersection(6,a),
        });
        List<IntersectionComputation> comps = new ArrayList<>();
        for(Intersection i : xs)
        {
            comps.add(i.prepareComputation(r,xs));
        }

        assertEquals(1.0, comps.get(0).n1);
        assertEquals(1.5, comps.get(0).n2);
        assertEquals(1.5, comps.get(1).n1);
        assertEquals(2.0, comps.get(1).n2);
        assertEquals(2.0, comps.get(2).n1);
        assertEquals(2.5, comps.get(2).n2);
        assertEquals(2.5, comps.get(3).n1);
        assertEquals(2.5, comps.get(3).n2);
        assertEquals(2.5, comps.get(4).n1);
        assertEquals(1.5, comps.get(4).n2);
        assertEquals(1.5, comps.get(5).n1);
        assertEquals(1.0, comps.get(5).n2);
    }

    @Test
    void underPointOffsetBelowSurface()
    {
        Ray r = new Ray(Vector.point(0,0,-5), Vector.vector3(0,0,1));
        Shape s = Sphere.glassSphere();
        s.setTransform(Matrix.translation(0,0,1));
        Intersection i1 = new Intersection(5,s);
        IntersectionComputation comps = i1.prepareComputation(r, Intersection.intersections(new Intersection[] {i1}));
        assertTrue(comps.underPoint.z > DoubleMath.EPSILON/2);
        assertTrue(comps.point.z < comps.underPoint.z);
    }

    @Test void schlickApproximationUnderTotalInternalReflection()
    {
        Shape s = Sphere.glassSphere();
        Ray r = new Ray(Vector.point(0,0,Math.sqrt(2)/2), Vector.vector3(0,1,0));
        List<Intersection> xs = Intersection.intersections(new Intersection[] {
                new Intersection(-Math.sqrt(2)/2,s),
                new Intersection(Math.sqrt(2)/2,s),
        });
        IntersectionComputation comps = xs.get(1).prepareComputation(r, xs);
        assertEquals(1.0, comps.schlick());
    }

    @Test void schlickApproximationWithPerpendicularViewingAngle()
    {
        Shape s = Sphere.glassSphere();
        Ray r = new Ray(Vector.point(0,0,0), Vector.vector3(0,0,1));
        List<Intersection> xs = Intersection.intersections(new Intersection[] {
                new Intersection(-1, s),
                new Intersection(1, s)
        });
        IntersectionComputation comps = xs.get(1).prepareComputation(r, xs);
        assertEquals(0.04, comps.schlick(), 0.0001);
    }


    @Test void schlickApproximationWithSmallAngleAndN2GTN1()
    {
        Shape s = Sphere.glassSphere();
        Ray r = new Ray(Vector.point(0,0.99,-2), Vector.vector3(0,0,1));
        List<Intersection> xs = Intersection.intersections(new Intersection[] {
                new Intersection(1.8589, s),
        });
        IntersectionComputation comps = xs.getFirst().prepareComputation(r, xs);
        assertEquals(0.48873, comps.schlick(), 0.0001);
    }

    @Test void intersectionCanEncapsulateUandV()
    {
        Shape s = new Triangle(Vector.point(0,1,0), Vector.point(-1,0,0), Vector.point(1,0,0));
        Intersection i = new Intersection(3.5,s,0.2,0.4,0,0);
        assertEquals(0.2, i.u);
        assertEquals(0.4, i.v);
    }
}