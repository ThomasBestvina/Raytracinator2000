package org.thomas.shape;

import org.thomas.math.Vector;
import org.thomas.raytracer.Intersection;

public class SmoothTriangle extends Triangle{
    public Vector n1, n2, n3;

    public SmoothTriangle(Vector p1, Vector p2, Vector p3, Vector n1, Vector n2, Vector n3) {
        super(p1, p2, p3);
        this.n1 = n1;
        this.n2 = n2;
        this.n3 = n3;
        isSmooth = true;
    }

    @Override
    protected Vector localNormal(Vector objectPoint, Intersection intersection)
    {
        return this.n2.multiply(intersection.u)
                .add(this.n3.multiply(intersection.v))
                .add(this.n1.multiply(1-intersection.u - intersection.v));
    }
}