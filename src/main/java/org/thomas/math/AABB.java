package org.thomas.math;

import org.thomas.raytracer.Ray;

public class AABB {
    public Vector min;
    public Vector max;
    public AABB(Vector min, Vector max)
    {
        this.min = min;
        this.max = max;
    }

    public static AABB empty()
    {
        return new AABB(
                Vector.point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY),
                Vector.point(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY));
    }

    public AABB merge(AABB other) {
        return new AABB(
                Vector.point( Math.min(this.min.x, other.min.x), Math.min(this.min.y, other.min.y),
                        Math.min(this.min.z, other.min.z)),
                Vector.point(Math.max(this.max.x, other.max.x), Math.max(this.max.y, other.max.y),
                        Math.max(this.max.z, other.max.z)));
    }


    public boolean intersects(Ray r) {
        double invDx = 1.0 / r.getDirection().x;
        double invDy = 1.0 / r.getDirection().y;
        double invDz = 1.0 / r.getDirection().z;

        double tx1 = (min.x - r.getOrigin().x) * invDx;
        double tx2 = (max.x - r.getOrigin().x) * invDx;
        double ty1 = (min.y - r.getOrigin().y) * invDy;
        double ty2 = (max.y - r.getOrigin().y) * invDy;
        double tz1 = (min.z - r.getOrigin().z) * invDz;
        double tz2 = (max.z - r.getOrigin().z) * invDz;

        double tmin = Math.max(Math.max(Math.min(tx1, tx2), Math.min(ty1, ty2)), Math.min(tz1, tz2));
        double tmax = Math.min(Math.min(Math.max(tx1, tx2), Math.max(ty1, ty2)), Math.max(tz1, tz2));

        return tmin <= tmax && tmax >= 0;
    }


    public Vector centroid() {
        return Vector.point(min.x+max.x,min.y+max.y,min.z+max.z).divide(2);
    }
}
