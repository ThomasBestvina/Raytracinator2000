import org.junit.jupiter.api.Test;
import org.thomas.math.Matrix;
import org.thomas.math.Vector;
import org.thomas.raytracer.Intersection;
import org.thomas.raytracer.Ray;
import org.thomas.shape.Cube;
import org.thomas.shape.Group;
import org.thomas.shape.Shape;
import org.thomas.shape.Sphere;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GroupTest {
    @Test
    void groupDefault() {
        Group group = new Group();
        assertEquals(Matrix.identityMatrix(), group.getTransform());
    }

    @Test
    void shapeHasAParent(){
        Shape group = new Group();
        assertNull(group.getParent());
    }

    @Test
    void addChildToGroup(){
        Group group = new Group();
        Shape child = new Sphere();
        group.addChild(child);
        assertFalse(group.getChildren().isEmpty());
    }

    @Test
    void intersectingRayWithEmptyGroup()
    {
        Group group = new Group();
        Ray ray = new Ray(Vector.point(0,0,0), Vector.vector3(0,0,1));
        List<Intersection> xs = group.intersect(ray);
        assertTrue(xs.isEmpty());
    }

    @Test
    void intersectingRayWithNonEmptyGroup()
    {
        Group group = new Group();
        Shape s1 = new Sphere();
        Shape s2 = new Sphere();
        s2.setTransform(Matrix.translation(0,0,-3));
        Shape s3 = new Sphere();
        s3.setTransform(Matrix.translation(5,0,0));
        group.addChild(s1);
        group.addChild(s2);
        group.addChild(s3);
        Ray r = new Ray(Vector.point(0,0,-5), Vector.vector3(0,0,1));
        List<Intersection> xs = group.intersect(r);
        assertEquals(4, xs.size());
        assertEquals(s2, xs.get(0).shape);
        assertEquals(s2, xs.get(1).shape);
        assertEquals(s1, xs.get(2).shape);
        assertEquals(s1, xs.get(3).shape);
    }

    @Test
    void intersectingGroupTransform()
    {
        Group group = new Group();
        group.setTransform(Matrix.scalar(2,2,2));
        Shape s1 = new Sphere();
        s1.setTransform(Matrix.translation(5,0,0));
        group.addChild(s1);
        Ray r =  new Ray(Vector.point(10,0,-10), Vector.vector3(0,0,1));
        List<Intersection> xs = group.intersect(r);
        assertEquals(2, xs.size());
    }

    @Test
    void convertingPointFromWorldToObjectSpace()
    {
        Group g1 = new Group();
        g1.setTransform(Matrix.rotationY(Math.PI/2));
        Group g2 = new Group();
        g2.setTransform(Matrix.scalar(2,2,2));
        g1.addChild(g2);
        Sphere s = new Sphere();
        s.setTransform(Matrix.translation(5,0,0));
        g2.addChild(s);
        Vector p = s.worldToObject(Vector.point(-2,0,-10));
        assertEquals(Vector.point(0,0,-1), p);
    }

    @Test
    void convertingNormalFromObjectToWorldSpace()
    {
        Group g1 = new Group();
        g1.setTransform(Matrix.rotationY(Math.PI/2));
        Group g2 = new Group();
        g2.setTransform(Matrix.scalar(1,2,3));
        g1.addChild(g2);
        Sphere s = new Sphere();
        s.setTransform(Matrix.translation(5,0,0));
        g2.addChild(s);
        assertEquals(Vector.vector3(0.2857, 0.4286, -0.8571), s.normalToWorld(Vector.vector3(Math.sqrt(3)/3,Math.sqrt(3)/3,Math.sqrt(3)/3)));
    }

    @Test
    void findingNormalOnChildObject()
    {
        Group g1 = new Group();
        g1.setTransform(Matrix.rotationY(Math.PI/2));
        Group g2 = new Group();
        g2.setTransform(Matrix.scalar(1,2,3));
        g1.addChild(g2);
        Sphere s = new Sphere();
        s.setTransform(Matrix.translation(5,0,0));
        g2.addChild(s);
        assertEquals(Vector.vector3(0.2857, 0.4286, -0.8571), s.getNormal(Vector.point(1.7321,1.1547,-5.5774)));
    }
}