package org.thomas.shape;

import org.thomas.math.AABB;
import org.thomas.math.Matrix;
import org.thomas.math.Vector;
import org.thomas.raytracer.Intersection;
import org.thomas.raytracer.Ray;

import java.util.LinkedList;
import java.util.List;

public class Group extends Shape {
    List<Shape>  children = new LinkedList<>();

    @Override
    public void setTransform(Matrix t)
    {
        transform = t;
        cachedBounds = null;
        for (Shape child : children) {
            child.cachedBounds = null;
        }
    }
    @Override
    protected Vector localNormal(Vector objectPoint, Intersection intersection){
        System.out.println("localNormal was called on group. This is a problem!");
        return null;
    }

    @Override
    public List<Intersection> localIntersect(Ray r) {
        List<Intersection> intersections = new LinkedList<>();
        for(int i = children.size()-1; i >= 0; i--) {
            intersections.addAll(children.get(i).intersect(r));
        }
        return intersections;
    }

    @Override
    protected AABB localBounds() {
        AABB result = AABB.empty();
        for(Shape child : children) {
            result = result.merge(child.bounds());
        }
        return result;
    }

    public void addChild(Shape s)
    {
        children.add(s);
        s.setParent(this);
        cachedBounds = null;
    }

    public List<Shape> getChildren() {
        return children;
    }
}