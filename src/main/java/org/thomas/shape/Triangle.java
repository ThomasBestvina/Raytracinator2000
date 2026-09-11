package org.thomas.shape;

import org.thomas.material.Material;
import org.thomas.math.AABB;
import org.thomas.math.DoubleMath;
import org.thomas.math.Vector;
import org.thomas.raytracer.Intersection;
import org.thomas.raytracer.Ray;

import java.util.List;
import java.util.concurrent.locks.Lock;

public class Triangle extends Shape {
    public final Vector p1;
    public final Vector p2;
    public final Vector p3;
    public final Vector e1;
    public final Vector e2;
    public final Vector normal;
    public volatile Vector tangent;

    public double[] uv1 = {0,0}, uv2 = {0,0}, uv3 = {0,0};

    protected boolean isSmooth = false;
    // This variable is funny, but, in the smooth triangle calss we say yes
    // but not here, because it decides whether we should store uv.

    public Triangle(Vector p1, Vector p2, Vector p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        e1 = p2.subtract(p1);
        e2 = p3.subtract(p1);
        normal = e2.cross(e1).normalize(); // possible reverse cross order.
    }

    @Override
    public Vector getNormal(Vector v, Intersection intersection)
    {
        if(tangent == null){
            synchronized (this) {
                if(tangent == null){
                    double du1 = uv2[0] - uv1[0];
                    double dv1 = uv2[1] - uv1[1];
                    double du2 = uv3[0] - uv1[0];
                    double dv2 = uv3[1] - uv1[1];
                    double denom = du1 * dv2 - du2 * dv1;
                    if (Math.abs(denom) > DoubleMath.EPSILON) {
                        tangent = e1.multiply(dv2).subtract(e2.multiply(dv1))
                                .multiply(1.0 / denom).normalize();
                    } else {
                        tangent = tangentSpace(normal)[0]; // fallback
                    }
                }
            }
        }
        Vector localPoint = worldToObject(v);
        Vector localNormal = localNormal(localPoint, intersection);
        Vector worldNormal = normalToWorld(localNormal);

        if(material.normalPerturber != null && intersection != null)
        {
            Vector worldTangent = normalToWorld(tangent);
            Vector bitangent = worldNormal.cross(worldTangent).normalize();
            worldNormal = material.perturbNormal(worldNormal, worldTangent, bitangent,
                    intersection.texU, intersection.texV);

            intersection.tangent = worldTangent;
            intersection.bitangent = bitangent;
        }

        return worldNormal;
    }

    @Override
    protected Vector localNormal(Vector objectPoint, Intersection intersection) {
        return normal;
    }

    @Override
    protected List<Intersection> localIntersect(Ray r) {
        Vector dirCrossE2 = r.getDirection().cross(this.e2);
        double det = this.e1.dot(dirCrossE2);
        if(Math.abs(det) < DoubleMath.EPSILON) return List.of();

        double f = 1.0/det;
        Vector p1ToOrigin = r.getOrigin().subtract(this.p1);
        double u = f*p1ToOrigin.dot(dirCrossE2);
        if(u < 0 || u > 1) return List.of();

        Vector origin_cross_e1 = p1ToOrigin.cross(this.e1);
        double v = f*r.getDirection().dot(origin_cross_e1);
        if(v < 0 || (u+v) > 1) return List.of();

        double t = f * this.e2.dot(origin_cross_e1);

        double w = 1 - u - v;

        double texU = w * uv1[0] + u * uv2[0] + v*uv3[0];
        double texV = w * uv1[1] + u * uv2[1] + v*uv3[1];

        if(isSmooth) return List.of(new Intersection(t, this, u, v, texU, texV));

        return List.of(new Intersection(t, this, texU, texV));
    }

    @Override
    protected AABB localBounds() {
        double minX = Math.min(p1.x, Math.min(p2.x, p3.x));
        double minY = Math.min(p1.y, Math.min(p2.y, p3.y));
        double minZ = Math.min(p1.z, Math.min(p2.z, p3.z));

        double maxX = Math.max(p1.x, Math.max(p2.x, p3.x));
        double maxY = Math.max(p1.y, Math.max(p2.y, p3.y));
        double maxZ = Math.max(p1.z, Math.max(p2.z, p3.z));

        return new AABB(Vector.point(minX, minY, minZ), Vector.point(maxX, maxY, maxZ));
    }
}