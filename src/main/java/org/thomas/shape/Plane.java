package org.thomas.shape;

import org.thomas.math.AABB;
import org.thomas.math.DoubleMath;
import org.thomas.math.Vector;
import org.thomas.raytracer.Intersection;
import org.thomas.raytracer.Ray;

import java.util.List;

public class Plane extends Shape{

    @Override
    protected Vector localNormal(Vector v, Intersection intersection) {
        return Vector.vector3(0, 1, 0);
    }

    @Override
    public List<Intersection> localIntersect(Ray r) {
        if(Math.abs(r.getDirection().y) < DoubleMath.EPSILON)
        {
            return List.of();
        }
        double t = -r.getOrigin().y / r.getDirection().y;

        Vector localp = r.position(t);

        double texU = localp.x - Math.floor(localp.x);
        double texV = localp.z - Math.floor(localp.z);


        return List.of(new Intersection(t, this, texU, texV));
    }

    @Override
    protected AABB localBounds() {
        throw new UnsupportedOperationException("Plane has infinite bounds and should not be added to BVH");
    }
}