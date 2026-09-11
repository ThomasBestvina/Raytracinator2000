import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.thomas.math.Vector;

public class VectorTest {
    @Test
    void isPoint()
    {
        Vector point = Vector.point(4.3,-4.2,3.1);
        assertEquals(1, point.w);
    }

    @Test
    void isVector()
    {
        Vector vector = Vector.vector3(4.3,-4.2,3.1);
        assertEquals(0, vector.w);
    }

    @Test
    void AddingVector()
    {
        Vector a = new Vector(3,-2,5,1);
        Vector b = new Vector(-2,3,1,0);
        Vector c = a.add(b);
        assertEquals(new Vector(1, 1, 6, 1), c);
    }

    @Test
    void SubtractingPoints()
    {
        Vector p1 = Vector.point(3,2,1);
        Vector p2 = Vector.point(5,6,7);
        assertEquals(Vector.vector3(-2, -4, -6), p1.subtract(p2));
    }

    @Test
    void SubtractingVectorAndPoint()
    {
        Vector p1 = Vector.point(3,2,1);
        Vector p2 = Vector.vector3(5,6,7);
        assertEquals(Vector.point(-2, -4, -6), p1.subtract(p2));
    }

    @Test
    void SubtractingVectors()
    {
        Vector p1 = Vector.vector3(3,2,1);
        Vector p2 = Vector.vector3(5,6,7);
        assertEquals(Vector.vector3(-2, -4, -6), p1.subtract(p2));
    }

    @Test
    void NegatingVector()
    {
        Vector v = Vector.vector3(1,-2,3);
        assertEquals(Vector.vector3(-1, 2, -3), v.negate());
    }

    @Test
    void MultiplyingVector()
    {
        Vector v = new Vector(1,-2,3,-4);
        v = v.multiply(3.5);
        assertEquals(new Vector(3.5,-7,10.5,-14), v);
    }
    @Test
    void MultiplyingVectorByFraction()
    {
        Vector v = new Vector(1,-2,3,-4);
        v = v.multiply(0.5);
        assertEquals(new Vector(0.5,-1,1.5,-2), v);
    }
    @Test
    void DividingVector()
    {
        Vector v = new Vector(1,-2,3,-4);
        v = v.divide(2);
        assertEquals(new Vector(0.5,-1,1.5,-2), v);
    }

    @Test
    void magnitudeOne()
    {
        Vector v = Vector.vector3(1,0,0);
        assertEquals(1, v.magnitude());
    }

    @Test
    void magnitudeOne2()
    {
        Vector v = Vector.vector3(0,1,0);
        assertEquals(1, v.magnitude());
    }

    @Test
    void magnitudeOne3()
    {
        Vector v = Vector.vector3(0,0,1);
        assertEquals(1, v.magnitude());
    }

    @Test
    void magnitudeSqrtFourteen()
    {
        Vector v = Vector.vector3(1,2,3);
        assertEquals(Math.sqrt(14), v.magnitude());
    }

    @Test
    void magnitudeSqrtFourteen2()
    {
        Vector v = Vector.vector3(-1,-2,-3);
        assertEquals(Math.sqrt(14), v.magnitude());
    }

    @Test
    void Normalize()
    {
        Vector v = Vector.vector3(4,0,0);
        assertEquals(Vector.vector3(1,0,0), v.normalize());
    }

    @Test
    void Normalize2()
    {
        Vector v = Vector.vector3(1,2,3);
        assertEquals(Vector.vector3(0.26726,0.53452,0.80178), v.normalize());
    }

    @Test
    void DotProduct()
    {
        Vector v1 =  Vector.vector3(1,2,3);
        Vector v2 =  Vector.vector3(2,3,4);
        assertEquals(20, v1.dot(v2));
    }

    @Test
    void CrossProduct()
    {
        Vector v1 = Vector.vector3(1,2,3);
        Vector v2 = Vector.vector3(2,3,4);
        assertEquals(Vector.vector3(-1, 2, -1), v1.cross(v2));
        assertEquals(Vector.vector3(1, -2, 1), v2.cross(v1));
    }

    @Test void ReflectingVector()
    {
        Vector v = Vector.vector3(1,-1,0);
        Vector n = Vector.vector3(0,1,0);
        Vector r = v.reflect(n);
        assertEquals(Vector.vector3(1,1,0), r);
    }

    @Test void ReflectingVectorOffSlantedSurface()
    {
        Vector v = Vector.vector3(0,-1,0);
        Vector n = Vector.vector3(Math.sqrt(2)/2, Math.sqrt(2)/2, 0);
        Vector r = v.reflect(n);
        assertEquals(Vector.vector3(1,0,0), r);
    }
}