import org.junit.jupiter.api.Test;
import org.thomas.material.Material;
import org.thomas.light.PointLight;
import org.thomas.material.PhongShadingModel;
import org.thomas.math.Color;
import org.thomas.math.Matrix;
import org.thomas.math.Vector;
import org.thomas.pattern.TestPattern;
import org.thomas.raytracer.Intersection;
import org.thomas.raytracer.IntersectionComputation;
import org.thomas.raytracer.Ray;
import org.thomas.scene.World;
import org.thomas.shape.Plane;
import org.thomas.shape.Shape;
import org.thomas.shape.Sphere;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WorldTest {
    @Test void creatingWorld() {
        World w = new World();
        assertTrue(w.getLights().isEmpty());
        assertEquals(0,w.getShapes().size());
    }

    @Test void creatingDefaultWorld()
    {
        World w = World.defaultWorld();
        Material mat1 = new Material();
        mat1.albedo = new Color(0.8,1.0,0.6);
        ((PhongShadingModel)mat1.shadingModel).diffuse = 0.7;
        ((PhongShadingModel)mat1.shadingModel).specular = 0.2;

        Matrix m = Matrix.scalar(0.5,0.5,0.5);

        assertEquals(w.getShapes().get(0).getMaterial(), mat1);
        assertEquals(w.getShapes().get(1).getTransform(), m);
    }

    @Test void intersectWorldWithArray()
    {
        Ray r = new Ray(Vector.point(0,0,-5), Vector.vector3(0,0,1));
        World w = World.defaultWorld();
        List<Intersection> xs = w.intersect(r);
        assertEquals(4, xs.size());
        assertEquals(4, xs.get(0).t);
        assertEquals(4.5, xs.get(1).t);
        assertEquals(5.5, xs.get(2).t);
        assertEquals(6, xs.get(3).t);
    }

    @Test void shadingAnIntersection()
    {
        World w = World.defaultWorld();
        Ray r = new Ray(Vector.point(0,0,-5), Vector.vector3(0,0,1));
        Intersection i = new Intersection(4, w.getShapes().getFirst());
        IntersectionComputation comps = i.prepareComputation(r);
        Color color = w.shadeHit(comps, 5);
        assertEquals(new Color(0.38066,0.47583,0.2855),color);
    }

    @Test void shadingIntersectionFromInside()
    {
        World w = World.defaultWorld();
        w.setLight(new PointLight(Vector.point(0,0.25,0), new Color(1,1,1)));
        Ray r = new Ray(Vector.point(0,0,0), Vector.vector3(0,0,1));
        Intersection i = new Intersection(0.5, w.getShapes().get(1));
        IntersectionComputation comps = i.prepareComputation(r);
        Color color = w.shadeHit(comps, 5);
        assertEquals(new Color(0.90498,0.90498,0.90498),color);
    }

    @Test void shadeHitIsGivenIntersectionInShadow()
    {
        World w = World.defaultWorld();
        w.setLight(new PointLight(Vector.point(0,0,-10), new Color(1,1,1)));
        Sphere s1 = new Sphere();
        w.addShape(s1);
        Sphere s2 = new Sphere();
        s2.setTransform(Matrix.translation(0,0,10));
        w.addShape(s2);
        Ray r = new Ray(Vector.point(0,0,5), Vector.vector3(0,0,1));
        Intersection i = new Intersection(4, s2);
        IntersectionComputation comps = i.prepareComputation(r);
        Color color = w.shadeHit(comps, 5);
        assertEquals(new Color(0.1,0.1,0.1),color);
    }

    @Test void ColorWhenRayMisses()
    {
        World w = World.defaultWorld();
        Ray r = new Ray(Vector.point(0,0,-5), Vector.vector3(0,1,0));
        Color c = w.colorAt(r);
        assertEquals(new Color(0,0,0),c);
    }

    @Test void ColorWhenRayHits()
    {
        World w = World.defaultWorld();
        Ray r = new Ray(Vector.point(0,0,-5), Vector.vector3(0,0,1));
        Color c = w.colorAt(r);
        assertEquals(new Color(0.38066,0.47583,0.2855),c);
    }

    @Test void ColorWithIntersectionBehindRay(){
        World w = World.defaultWorld();
        Ray r = new Ray(Vector.point(0,0,0.75), Vector.vector3(0,0,-1));
        Shape outer = w.getShapes().get(0);
        ((PhongShadingModel)outer.getMaterial().shadingModel).ambient = 1;
        Shape inner = w.getShapes().get(1);
        ((PhongShadingModel)inner.getMaterial().shadingModel).ambient = 1;
        Color c = w.colorAt(r);
        assertEquals(inner.getMaterial().albedo, c);
    }

    @Test void defaultTransformationMatrix(){
        Vector from = Vector.point(0,0,0);
        Vector to = Vector.point(0,0,-1);
        Vector up = Vector.vector3(0,1,0);
        Matrix t = World.viewTransform(from,to,up);
        assertEquals(Matrix.identityMatrix(), t);
    }

    @Test void positiveZTransformationMatrix(){
        Vector from = Vector.point(0,0,0);
        Vector to = Vector.point(0,0,1);
        Vector up = Vector.vector3(0,1,0);
        Matrix t = World.viewTransform(from,to,up);
        assertEquals(Matrix.scalar(-1,1,-1), t);
    }

    @Test void viewTransformationMovesWorld(){
        Vector from = Vector.point(0,0,8);
        Vector to = Vector.point(0,0,0);
        Vector up = Vector.vector3(0,1,0);
        Matrix t = World.viewTransform(from,to,up);
        assertEquals(Matrix.translation(0,0,-8), t);
    }

    @Test void arbitraryViewTransformation(){
        Vector from = Vector.point(1,3,2);
        Vector to = Vector.point(4,-2,8);
        Vector up = Vector.vector3(1,1,0);
        Matrix t = World.viewTransform(from,to,up);
        Matrix expected = new Matrix(new double[][]{
                {-0.50709, 0.50709, 0.67612, -2.36643},
                {0.76772, 0.60609, 0.12122, -2.82843},
                {-0.35857, 0.59761, -0.71714, 0.00000},
                {0.00000, 0.00000, 0.00000, 1.00000}
        });
        assertEquals(expected, t);
    }

    @Test void noShadowWhenNothingIsCollinearWithPointAndLight()
    {
        World w = World.defaultWorld();
        Vector p = Vector.point(0,10,0);
        assertFalse(w.isShadowed(p));
    }

    @Test void shadowWhenObjectIsBetweenPointAndLight()
    {
        World w = World.defaultWorld();
        Vector p = Vector.point(10,-10,10);
        assertTrue(w.isShadowed(p));
    }

    @Test void noShadowWhenObjectIsBehindLight()
    {
        World w = World.defaultWorld();
        Vector p = Vector.point(-20,20,-20);
        assertFalse(w.isShadowed(p));
    }

    @Test void noShadowWhenObjectIsBehindPoint()
    {
        World w = World.defaultWorld();
        Vector p = Vector.point(-2,2,-2);
        assertFalse(w.isShadowed(p));
    }

    @Test void hitsNonReflectiveSurface()
    {
        World w = World.defaultWorld();
        Ray r = new Ray(Vector.point(0,0,0), Vector.vector3(0,0,1));
        Shape shape = w.getShapes().get(1);
        ((PhongShadingModel)shape.getMaterial().shadingModel).ambient = 1;
        Intersection i = new Intersection(1, shape);
        IntersectionComputation comp = i.prepareComputation(r);
        assertEquals(Color.black, w.reflectColor(comp, 5));
    }

    @Test void reflectedColorForReflectiveMaterial()
    {
        World w = World.defaultWorld();
        Shape s = new Plane();
        s.getMaterial().reflective = 0.5;
        s.setTransform(Matrix.translation(0,-1,0));
        w.addShape(s);
        Ray r = new Ray(Vector.point(0,0,-3), Vector.vector3(0, -Math.sqrt(2)/2, Math.sqrt(2)/2));
        Intersection i = new Intersection(Math.sqrt(2), s);
        IntersectionComputation comp = i.prepareComputation(r);
        assertEquals(new Color(0.1903, 0.2379, 0.1427), w.reflectColor(comp, 5));
    }

    @Test void shadeHitWithReflectiveMaterial()
    {
        World w = World.defaultWorld();
        Shape s = new Plane();
        s.getMaterial().reflective = 0.5;
        s.setTransform(Matrix.translation(0,-1,0));
        w.addShape(s);
        Ray r = new Ray(Vector.point(0,0,-3), Vector.vector3(0, -Math.sqrt(2)/2, Math.sqrt(2)/2));
        Intersection i = new Intersection(Math.sqrt(2), s);
        IntersectionComputation comp = i.prepareComputation(r);
        Color color = w.shadeHit(comp, 5);
        assertEquals(new Color(0.87677, 0.92436,0.82918), color);
    }

    @Test void colorAtIsNotInfinitelyRecursive()
    {
        World w = new World();
        w.setLight(new PointLight(Vector.point(0,0,0), new Color(1,1,1)));
        Shape lower = new Plane();
        lower.getMaterial().reflective = 1;
        lower.setTransform(Matrix.translation(0,-1,0));

        Shape upper = new Plane();
        upper.getMaterial().reflective = 1;
        upper.setTransform(Matrix.translation(0,1,0));

        w.addShape(lower);
        w.addShape(upper);

        Ray r = new Ray(Vector.point(0,0,0), Vector.vector3(0,1,0));

        Color c = w.colorAt(r);
    }

    @Test void reflectedColorAtMaximumDepth()
    {
        World w = World.defaultWorld();
        Shape s = new Plane();
        s.getMaterial().reflective = 0.5;
        s.setTransform(Matrix.translation(0,-1,0));
        w.addShape(s);
        Ray r = new Ray(Vector.point(0,0,-3), Vector.vector3(0, -Math.sqrt(2)/2, Math.sqrt(2)/2));
        Intersection i = new Intersection(Math.sqrt(2), s);
        IntersectionComputation comp = i.prepareComputation(r);
        Color color = w.reflectColor(comp, 0);
        assertEquals(Color.black, color);
    }

    @Test void refractedColorOfOpaqueSurface()
    {
        World w = World.defaultWorld();
        Shape s = w.getShapes().getFirst();
        Ray r = new Ray(Vector.point(0,0,-5), Vector.vector3(0,0,1));
        List<Intersection> xs = Intersection.intersections(new Intersection[]
            {
                    new Intersection(4, s),
                    new Intersection(6, s),
            });

        IntersectionComputation comps = xs.getFirst().prepareComputation(r, xs);
        Color c = w.refractColor(comps, 5);
        assertEquals(Color.black, c);
    }

    @Test void refractColorAtMaximumRecursiveDepth()
    {
        World w = World.defaultWorld();
        Shape s = w.getShapes().getFirst();
        s.getMaterial().transparency = 1.0;
        s.getMaterial().refractive = 1.5;
        Ray r = new Ray(Vector.point(0,0,-5), Vector.vector3(0,0,1));
        List<Intersection> xs = Intersection.intersections(new Intersection[]
                {
                        new Intersection(4, s),
                        new Intersection(6, s),
                });
        IntersectionComputation comps = xs.getFirst().prepareComputation(r, xs);
        Color c =  w.refractColor(comps, 0);
        assertEquals(Color.black, c);
    }

    @Test void refractUnderTotalInternalReflection()
    {
        World w = World.defaultWorld();
        Shape s = w.getShapes().getFirst();
        s.getMaterial().transparency = 1.0;
        s.getMaterial().refractive = 1.5;
        Ray r = new Ray(Vector.point(0,0,Math.sqrt(2)/2), Vector.vector3(0,1,0));
        List<Intersection> xs = Intersection.intersections(new Intersection[]
                {
                        new Intersection(-Math.sqrt(2)/2, s),
                        new Intersection(Math.sqrt(2)/2, s),
                });
        IntersectionComputation comps = xs.get(1).prepareComputation(r, xs);
        Color c =  w.refractColor(comps, 5);
        assertEquals(Color.black, c);
    }

    @Test void refractedColorWithRefractedRay()
    {
        World w = World.defaultWorld();
        Shape s = w.getShapes().getFirst();
        s.getMaterial().transparency = 1.0;
        s.getMaterial().pattern = new TestPattern();
        ((PhongShadingModel)s.getMaterial().shadingModel).ambient = 1.0;

        Shape b = w.getShapes().get(1);
        b.getMaterial().transparency = 1.0;
        b.getMaterial().refractive = 1.5;
        ((PhongShadingModel)b.getMaterial().shadingModel).ambient = 1.0;

        Ray r = new Ray(Vector.point(0,0,0.1), Vector.vector3(0,1,0));
        List<Intersection> xs = Intersection.intersections(new Intersection[]
                {
                        new Intersection(-0.9899, s),
                        new Intersection(-0.4899, b),
                        new Intersection(0.4899, b),
                        new Intersection(0.9899, s),
                });
        IntersectionComputation comps = xs.get(2).prepareComputation(r, xs);
        Color c =  w.refractColor(comps, 5);
        assertEquals(new Color(0, 0.9988, 0.0472), c);
    }

    @Test void shadeHitWithTransparentMaterial()
    {
        World w = World.defaultWorld();
        Shape s = w.getShapes().getFirst();
        ((PhongShadingModel)s.getMaterial().shadingModel).ambient = 1.0;

        Shape b = w.getShapes().get(1);
        ((PhongShadingModel)b.getMaterial().shadingModel).ambient = 1.0;

        Shape floor = new Plane();
        floor.setTransform(Matrix.translation(0,-1,0));
        floor.getMaterial().transparency = 0.5;
        floor.getMaterial().refractive = 1.5;
        w.addShape(floor);

        Shape ball = new Sphere();
        ball.getMaterial().albedo = new Color(1,0,0);
        ((PhongShadingModel)ball.getMaterial().shadingModel).ambient = 0.5;
        ball.setTransform(Matrix.translation(0,-3.5,-0.5));
        w.addShape(ball);

        Ray r = new Ray(Vector.point(0,0,-3), Vector.vector3(0,-Math.sqrt(2)/2,Math.sqrt(2)/2));
        List<Intersection> xs = Intersection.intersections(new Intersection[]{new Intersection(Math.sqrt(2), floor)});
        IntersectionComputation comps = xs.get(0).prepareComputation(r, xs);
        Color c =  w.shadeHit(comps, 5);
        assertEquals(new Color(0.93642, 0.68642, 0.68642), c);
    }

    @Test
    void shadeHitWithReflectiveTransparentMaterial()
    {
        World w = World.defaultWorld();
        Shape s = w.getShapes().getFirst();
        //s.getMaterial().ambient = 1.0;

        Shape b = w.getShapes().get(1);
        //b.getMaterial().ambient = 1.0;

        Ray ray = new Ray(Vector.point(0,0,-3), Vector.vector3(0,-Math.sqrt(2)/2,Math.sqrt(2)/2));

        Shape floor = new Plane();
        floor.setTransform(Matrix.translation(0,-1,0));
        floor.getMaterial().reflective = 0.5;
        floor.getMaterial().transparency = 0.5;
        floor.getMaterial().refractive = 1.5;
        w.addShape(floor);

        Shape ball = new Sphere();
        ball.getMaterial().albedo = new Color(1,0,0);
        ((PhongShadingModel)ball.getMaterial().shadingModel).ambient = 0.5;
        ball.setTransform(Matrix.translation(0,-3.5,-0.5));
        w.addShape(ball);

        List<Intersection> xs = Intersection.intersections(new  Intersection[]{new Intersection(Math.sqrt(2), floor)});
        IntersectionComputation comps = xs.getFirst().prepareComputation(ray, xs);
        Color c =  w.shadeHit(comps, 5);
        assertEquals(new Color(0.93391, 0.69643, 0.69243), c);
    }
}