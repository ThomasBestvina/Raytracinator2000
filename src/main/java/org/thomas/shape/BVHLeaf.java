package org.thomas.shape;

import org.thomas.raytracer.Intersection;
import org.thomas.raytracer.Ray;

import java.util.List;

public class BVHLeaf extends BVHNode {

    protected BVHLeaf(Shape shape) {
        super(shape, null);
    }

    @Override
    public List<Intersection> intersect(Ray r) {
        return left.intersect(r);
    }
}