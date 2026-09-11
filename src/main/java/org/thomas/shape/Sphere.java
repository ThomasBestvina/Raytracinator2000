package org.thomas.shape;

import org.thomas.material.Material;
import org.thomas.math.AABB;
import org.thomas.raytracer.Intersection;
import org.thomas.raytracer.Ray;
import org.thomas.math.Vector;

import java.util.Collections;
import java.util.List;

public class Sphere extends Shape {
    public static Sphere glassSphere() {
        Sphere s = new Sphere();
        Material m = new Material();
        m.transparency = 1.0;
        m.refractive = 1.5;
        s.setMaterial(m);
        return s;
    }

    @Override
    public List<Intersection> localIntersect(Ray r) {
        Vector sphereToRay = r.getOrigin().subtract(Vector.point(0,0,0));
        double a = r.getDirection().dot(r.getDirection());
        double b = 2 * r.getDirection().dot(sphereToRay);
        double c = sphereToRay.dot(sphereToRay) - 1;

        double discriminant = b*b - 4*a*c;
        if (discriminant < 0) {
            return Collections.emptyList();
        }

        double t1 = (-b - Math.sqrt(discriminant)) / (2 * a);
        double t2 = (-b + Math.sqrt(discriminant)) / (2 * a);

        return List.of(getIntersectionWithUV(t1, r),getIntersectionWithUV(t2, r));
    }

    private Intersection getIntersectionWithUV(double t, Ray r)
    {
        Vector localpoint = r.position(t);
        double u = 0.5 + Math.atan2(localpoint.z, localpoint.x) / (2 * Math.PI);
        double v = 0.5 - Math.asin(Math.clamp(localpoint.y, -1, 1)) / Math.PI;
        return new Intersection(t, this, u, v);
    }

    @Override
    protected AABB localBounds() {
        return new AABB(Vector.point(-1,-1,-1), Vector.point(1,1,1));
    }

    @Override
    protected Vector localNormal(Vector v, Intersection intersection) {
        return v.subtract(Vector.point(0, 0, 0));
    }
}