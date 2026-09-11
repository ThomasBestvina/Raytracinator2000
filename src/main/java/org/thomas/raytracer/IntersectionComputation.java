package org.thomas.raytracer;

import org.thomas.math.Vector;
import org.thomas.shape.Shape;

public class IntersectionComputation {
    public double t;
    public Vector point;
    public Vector eyev;
    public Vector normalv;
    public Shape shape;
    public boolean inside;
    public Vector overPoint;
    public Vector underPoint;
    public Vector reflectv;
    public double n1;
    public double n2;
    public Intersection intersection;

    public double schlick() {
        double cos = eyev.dot(normalv);

        if(n1 > n2){
            double n = n1/n2;
            double sin2_t = n*n*(1.0-cos*cos);
            if(sin2_t > 1.0) return 1.0;

            cos = Math.sqrt(1.0 - sin2_t);
        }

        double r0 = ((n1-n2) / (n1+n2))*((n1-n2) / (n1+n2));
        return r0 + (1-r0) * Math.pow(1-cos,5);
    }
}