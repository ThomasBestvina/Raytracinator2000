package org.thomas.shape;

import org.thomas.math.AABB;
import org.thomas.math.Vector;
import org.thomas.raytracer.Intersection;
import org.thomas.raytracer.Ray;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BVHNode extends Shape{
    protected Shape left;
    protected Shape right;

    protected BVHNode(Shape left, Shape right){
        this.left = left;
        this.right = right;
    }

    @Override
    protected Vector localNormal(Vector objectPoint, Intersection intersection) {
        return null;
    }

    @Override
    public List<Intersection> intersect(Ray r) {
        if (!this.bounds().intersects(r)) return List.of();
        List<Intersection> hits = new ArrayList<>();
        hits.addAll(left.intersect(r));
        if (right != null) hits.addAll(right.intersect(r));
        hits.sort(Comparator.comparingDouble(a -> a.t));
        return hits;
    }

    @Override
    protected List<Intersection> localIntersect(Ray r) {
        return List.of();
    }

    @Override
    protected AABB localBounds() {
        AABB b = left.bounds();
        if (right != null) b = b.merge(right.bounds());
        return b;
    }

    public static BVHNode build(List<Shape> shapes){
        if (shapes.size() == 1) return new BVHLeaf(shapes.getFirst());
        if (shapes.size() == 2) return new BVHNode(shapes.get(0), shapes.get(1));

        AABB centroidBounds = centroidBounds(shapes);
        int axis = longestAxis(centroidBounds);

        double bestCost = Double.MAX_VALUE;
        List<Shape> bestLeft = null;
        List<Shape> bestRight = null;

        int BUCKETS = 12;
        for (int b = 1; b < BUCKETS; b++) {
            double split = lerp(
                    getAxisValue(centroidBounds.min, axis),
                    getAxisValue(centroidBounds.max, axis),
                    (double) b / BUCKETS
            );

            List<Shape> left  = new ArrayList<>();
            List<Shape> right = new ArrayList<>();
            for (Shape s : shapes) {
                if (s.bounds().centroid().getFromAxis(axis) < split)
                    left.add(s);
                else
                    right.add(s);
            }

            if (left.isEmpty() || right.isEmpty()) continue;

            double cost = left.size()  * surfaceArea(left)
                    + right.size() * surfaceArea(right);

            if (cost < bestCost) {
                bestCost  = cost;
                bestLeft  = left;
                bestRight = right;
            }
        }

        if (bestLeft == null) {
            bestLeft  = new ArrayList<>(shapes.subList(0, shapes.size() / 2));
            bestRight = new ArrayList<>(shapes.subList(shapes.size() / 2, shapes.size()));
        }

        return new BVHNode(build(bestLeft), build(bestRight));
    }

    public static AABB centroidBounds(List<Shape> shapes)
    {
        AABB result = AABB.empty();
        for(Shape s: shapes)
        {
            Vector c = s.bounds().centroid();
            result = result.merge(new AABB(c,c));
        }
        return result;
    }

    public static int longestAxis(AABB bounds)
    {
        double dx = bounds.max.x - bounds.min.x;
        double dy = bounds.max.y - bounds.min.y;
        double dz = bounds.max.z - bounds.min.z;
        if(dx >= dy && dx >= dz) return 0;
        if(dy >= dx && dy >= dz) return 1;
        return 2;
    }

    private static double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    private static double getAxisValue(Vector v, int axis) {
        return v.getFromAxis(axis);
    }

    private static double surfaceArea(List<Shape> shapes) {
        AABB merged = AABB.empty();
        for (Shape s : shapes) merged = merged.merge(s.bounds());
        double dx = merged.max.x - merged.min.x;
        double dy = merged.max.y - merged.min.y;
        double dz = merged.max.z - merged.min.z;
        return 2 * (dx * dy + dy * dz + dz * dx);
    }
}