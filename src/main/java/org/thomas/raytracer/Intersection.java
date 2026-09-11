package org.thomas.raytracer;

import org.thomas.math.DoubleMath;
import org.thomas.math.Vector;
import org.thomas.shape.Shape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Intersection {
    public double t;
    public double u;
    public double v;
    public double texU;
    public double texV;

    public Vector tangent;
    public Vector bitangent;

    public Shape shape;

    public Intersection(double t, Shape s) {
        this(t,s,0,0,0,0);
    }


    public Intersection(double t, Shape s, double u, double v, double texU, double texV)
    {
        this.shape = s;
        this.t = t;
        this.u = u;
        this.v = v;
        this.texU = texU;
        this.texV = texV;
    }

    public Intersection(double t, Shape s, double texU, double texV)
    {
        this(t,s,0,0,texU,texV);
    }

    public static List<Intersection> intersections(Intersection[] intersections)
    {
        return  Arrays.asList(intersections);
    }

    public static Intersection hit(List<Intersection> intersections)
    {
        Intersection currentHit = null;
        for(Intersection i : intersections)
        {
            if(i.t < DoubleMath.EPSILON){
                continue;
            }
            if(currentHit == null){
                currentHit = i;
            }
            if(currentHit.t > i.t){
                currentHit = i;
            }
        }
        return currentHit;
    }

    public IntersectionComputation prepareComputation(Ray r)
    {
        IntersectionComputation comp = new IntersectionComputation();
        comp.t = this.t;
        comp.shape = this.shape;
        comp.point = r.position(this.t);
        comp.eyev = r.getDirection().negate();
        comp.normalv = shape.getNormal(comp.point, this);

        if(comp.normalv.dot(comp.eyev) < 0){
            comp.inside = true;
            comp.normalv = comp.normalv.negate();
        }

        comp.overPoint = comp.point.add(comp.normalv.multiply(DoubleMath.EPSILON));
        comp.underPoint = comp.point.subtract(comp.normalv.multiply(DoubleMath.EPSILON));

        comp.reflectv = r.getDirection().reflect(comp.normalv);

        comp.intersection = this;

        return comp;
    }

    public IntersectionComputation prepareComputation(Ray r, List<Intersection> xs)
    {
        IntersectionComputation comp = prepareComputation(r);

        List<Shape> containers = new ArrayList<>();
        for(Intersection i : xs)
        {
            if(i == this)
            {
                if(containers.isEmpty())
                {
                    comp.n1 = 1.0;
                }
                else
                {
                    comp.n1 = containers.getLast().getMaterial().refractive;
                }
            }

            if(containers.contains(i.shape))
            {
                containers.remove(i.shape);
            }else{
                containers.add(i.shape);
            }

            if(i == this)
            {
                if(containers.isEmpty())
                {
                    comp.n2 = 1.0;
                }
                else{
                    comp.n2 = containers.getLast().getMaterial().refractive;
                }
            }
        }
        return comp;
    }
}