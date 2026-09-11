package org.thomas.shape;

import org.thomas.material.Material;
import org.thomas.math.AABB;
import org.thomas.math.Matrix;
import org.thomas.math.Vector;
import org.thomas.raytracer.Intersection;
import org.thomas.raytracer.Ray;

import java.util.List;

public abstract class Shape {
    protected Matrix transform = Matrix.identityMatrix();
    protected Material material = new Material();
    protected Shape parent;
    protected AABB cachedBounds;
    public void setTransform(Matrix t)
    {
        transform = t;
        cachedBounds = null;
    }
    public Matrix getTransform()
    {
        return transform;
    }
    public void setMaterial(Material m)
    {
        material = m;
    }
    public Material getMaterial(){
        return material;
    }
    public Vector getNormal(Vector v)
    {
        return getNormal(v, null);
    }
    public Vector getNormal(Vector v, Intersection intersection)
    {
        Vector localPoint = worldToObject(v);
        Vector localNormal = localNormal(localPoint, intersection);
        Vector worldNormal = normalToWorld(localNormal);

        if(material.normalPerturber != null && intersection != null)
        {
            Vector[] tb = tangentSpace(worldNormal);
            intersection.tangent = tb[0];
            intersection.bitangent = tb[1];
            worldNormal = material.perturbNormal(worldNormal, tb[0], tb[1], intersection.texU, intersection.texV);
        }

        return worldNormal;
    }

    public static Vector[] tangentSpace(Vector normal)
    {
        Vector tanget;
        if(Math.abs(normal.x) > 0.9) tanget = Vector.vector3(0,1,0);
        else tanget = Vector.vector3(1,0,0);
        Vector bitangent = normal.cross(tanget).normalize();
        tanget = bitangent.cross(normal).normalize();
        return new Vector[]{tanget, bitangent};
    }

    protected abstract Vector localNormal(Vector objectPoint, Intersection intersection);
    public List<Intersection> intersect(Ray r)
    {
        return localIntersect(r.transform(transform.inverse()));
    }
    protected abstract List<Intersection> localIntersect(Ray r);

    protected abstract AABB localBounds();

    public Shape getParent() {
        return parent;
    }
    public void setParent(Shape parent) {
        this.parent = parent;
    }

    public AABB bounds() {
        if(cachedBounds != null) {
            return cachedBounds;
        }
        AABB local = localBounds();
        AABB result = AABB.empty();

        double[] xs = {local.min.x, local.max.x};
        double[] ys = {local.min.y, local.max.y};
        double[] zs = {local.min.z, local.max.z};

        for (double x : xs)
            for (double y : ys)
                for (double z : zs) {
                    Vector corner = transform.multiply(Vector.point(x, y, z));
                    if (parent != null) corner = parent.transform.multiply(corner);
                    result = result.merge(new AABB(corner, corner));
                }
        cachedBounds = result;
        return result;
    }

    public Vector worldToObject(Vector p)
    {
        if(parent != null) p = parent.worldToObject(p);

        return transform.inverse().multiply(p);
    }

    public Vector normalToWorld(Vector v)
    {
        Vector normal = transform.inverse().transpose().multiply(v);
        normal.w = 0;
        normal =  normal.normalize();

        if(parent != null) normal = parent.normalToWorld(normal);

        return normal;
    }
}