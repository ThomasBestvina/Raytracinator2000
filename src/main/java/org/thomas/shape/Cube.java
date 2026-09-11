package org.thomas.shape;

import org.thomas.math.AABB;
import org.thomas.math.DoubleMath;
import org.thomas.math.Vector;
import org.thomas.raytracer.Intersection;
import org.thomas.raytracer.Ray;

import java.util.List;


public class Cube extends Shape {
    @Override
    protected Vector localNormal(Vector v, Intersection intersection) {
        double maxc = Math.max(Math.abs(v.x), Math.max(Math.abs(v.y), Math.abs(v.z)));

        if(DoubleMath.equal(maxc, Math.abs(v.x))) return Vector.vector3(v.x, 0,0);
        if(DoubleMath.equal(maxc, Math.abs(v.y))) return Vector.vector3(0, v.y,0);

        return Vector.vector3(0, 0,v.z);
    }

    @Override
    public List<Intersection> localIntersect(Ray r) {
        double[] xt = check_axis(r.getOrigin().x, r.getDirection().x);
        double[] yt = check_axis(r.getOrigin().y, r.getDirection().y);
        double[] zt = check_axis(r.getOrigin().z, r.getDirection().z);

        double tmin = Math.max(xt[0], Math.max(yt[0], zt[0]));
        double tmax = Math.min(xt[1], Math.min(yt[1], zt[1]));

        if(tmin > tmax) return List.of();

        return List.of(intersectionWithUV(tmin, r), intersectionWithUV(tmax, r));
    }

    private Intersection intersectionWithUV(double t, Ray r) {
        Vector p = r.position(t);
        double[] uv = cubeUV(p);
        return new Intersection(t, this, uv[0], uv[1]);
    }

    private double[] cubeUV(Vector p) {
        double absX = Math.abs(p.x), absY = Math.abs(p.y), absZ = Math.abs(p.z);
        double maxC = Math.max(absX, Math.max(absY, absZ));

        if(DoubleMath.equal(maxC, absX)) {
            return new double[]{
                    (p.x > 0) ? (p.z + 1) / 2 : (1 - p.z) / 2,
                    (p.y + 1) / 2
            };
        } else if(DoubleMath.equal(maxC, absY)) {
            return new double[]{
                    (p.x + 1) / 2,
                    (p.y > 0) ? (1 - p.z) / 2 : (p.z + 1) / 2
            };
        } else {
            return new double[]{
                    (p.z > 0) ? (p.x + 1) / 2 : (1 - p.x) / 2,
                    (p.y + 1) / 2
            };
        }
    }

    @Override
    protected AABB localBounds() {
        return new AABB(Vector.point(-1,-1,-1), Vector.point(1,1,1));
    }

    private double[] check_axis(double origin, double direction) {
        double tmin_numerator = (-1-origin);
        double tmax_numerator = (1-origin);

        double tmin;
        double tmax;
        if(Math.abs(direction) >= DoubleMath.EPSILON)
        {
            tmin = tmin_numerator/direction;
            tmax = tmax_numerator/direction;
        }
        else{
            tmin = tmin_numerator * Double.POSITIVE_INFINITY;
            tmax = tmax_numerator * Double.POSITIVE_INFINITY;
        }

        if(tmin > tmax){
            double swap = tmin;
            tmin = tmax;
            tmax = swap;
        }

        return new double[] {tmin, tmax};
    }
}