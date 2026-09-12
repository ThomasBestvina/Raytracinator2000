package org.thomas.scene;

import org.thomas.material.Material;
import org.thomas.light.Light;
import org.thomas.light.PointLight;
import org.thomas.material.PhongShadingModel;
import org.thomas.material.Texture;
import org.thomas.math.Color;
import org.thomas.math.DoubleMath;
import org.thomas.math.Matrix;
import org.thomas.math.Vector;
import org.thomas.raytracer.Intersection;
import org.thomas.raytracer.IntersectionComputation;
import org.thomas.raytracer.Ray;
import org.thomas.shape.Shape;
import org.thomas.shape.Sphere;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class World {
    protected List<Shape> shapes = new ArrayList<>();
    protected Texture environment;
    protected List<Light> lights = new ArrayList<>();

    public World() {}

    public static World defaultWorld()
    {
        World world = new World();
        world.lights.add(new PointLight(Vector.point(-10, 10, -10), new Color(1,1,1)));

        Sphere s1 = new Sphere();
        Material mat1 = new Material();
        mat1.albedo = new Color(0.8, 1.0, 0.6);
        mat1.shadingModel = new PhongShadingModel(0.1, 0.7, 0.2, 200.0); // restore original values
        s1.setMaterial(mat1);

        Sphere s2 = new Sphere();
        s2.setTransform(Matrix.scalar(0.5, 0.5, 0.5));

        world.shapes.add(s1);
        world.shapes.add(s2);
        return world;
    }

    public void setEnvironment(Texture environment) {
        this.environment = environment;
    }

    public Color shadeHit(IntersectionComputation comps, int remaining) {
        Color surface = comps.shape.getMaterial()
                .lighting(lights, comps.overPoint, comps.eyev, comps.normalv,
                        this, comps.shape, comps.intersection);
        Color reflected = reflectColor(comps, remaining);
        Color refracted = refractColor(comps, remaining);

        Material mat = comps.shape.getMaterial();
        if (mat.reflective > 0 && mat.transparency > 0) {
            double reflectance = comps.schlick();
            return surface.add(reflected.multiply(reflectance))
                    .add(refracted.multiply(1 - reflectance));
        }
        return surface.add(reflected).add(refracted);
    }

    public List<Light> getLights() {
        return lights;
    }

    public List<Shape> getShapes() {
        return shapes;
    }

    public void setLight(Light light) {

        this.lights.clear();
        this.lights.add(light);
    }

    public void addLight(Light light) {
        this.lights.add(light);
    }

    public List<Intersection> intersect(Ray r) {
        List<Intersection> intersections = new ArrayList<>(shapes.size()*2);
        for(Shape s : shapes) {
            intersections.addAll(s.intersect(r));
        }
        intersections.sort(Comparator.comparingDouble(a -> a.t));
        return intersections;
    }

    public Color colorAt(Ray r)
    {
        return colorAt(r, 7); // TODO: Make config for how many allowed reflects, or perhaps in scene format?
    }

    public Color colorAt(Ray r, int remaining)
    {
        List<Intersection> intersections = intersect(r);
        Intersection hit = Intersection.hit(intersections);
        if(hit == null) return sampleEnvironment(r.getDirection());
        IntersectionComputation comps = hit.prepareComputation(r, intersections);
        return shadeHit(comps, remaining);
    }

    public Color sampleEnvironment(Vector direction)
    {
        if(environment == null) return Color.black;
        Vector d = direction.normalize();
        double u = 0.5 + Math.atan2(d.z, d.x) / (2 * Math.PI);
        double v = 0.5 - Math.asin(Math.max(-1, Math.min(1, d.y))) / Math.PI;
        return environment.sample(u,v);
    }


    public Color reflectColor(IntersectionComputation comps, int remaining)
    {
        if(remaining <= 0) return Color.black;
        if(DoubleMath.equal(comps.shape.getMaterial().reflective, 0))
        {
            return Color.black;
        }

        Ray reflect = new Ray(comps.overPoint, comps.reflectv);
        return colorAt(reflect, remaining-1).multiply(comps.shape.getMaterial().reflective);
    }

    public Color refractColor(IntersectionComputation comps, int remaining)
    {
        if(comps.shape.getMaterial().transparency == 0 || remaining <= 0) return Color.black;

        double n_ratio = comps.n1 / comps.n2;
        double cos_i = comps.eyev.dot(comps.normalv);
        double sin2_t = n_ratio * n_ratio * (1-cos_i*cos_i);

        if(sin2_t > 1) return Color.black;

        double cos_t = Math.sqrt(1-sin2_t);
        Vector direction = comps.normalv.multiply(n_ratio * cos_i - cos_t).subtract(comps.eyev.multiply(n_ratio));

        Ray refract = new Ray(comps.underPoint, direction);

        return colorAt(refract, remaining-1).multiply(comps.shape.getMaterial().transparency);
    }

    public boolean isShadowed(Vector point, Light light)
    {
        Vector v = light.getPosition().subtract(point);
        double distance = v.magnitude();
        Vector direction = v.normalize();
        Ray r = new Ray(point, direction);
        List<Intersection> intersections = intersect(r);
        for (Intersection i : intersections) {
            if (i.t > 1e-4 && i.t < distance && i.shape.getMaterial().castsShadow) {
                return true;
            }
        }
        return false;
    }

    /*
    Compatability function for tests
     */
    public boolean isShadowed(Vector point) {
        return isShadowed(point, this.lights.getFirst());
    }

    public static Matrix viewTransform(Vector from, Vector to, Vector up)
    {
        Vector forward = to.subtract(from).normalize();

        Vector left = forward.cross(up.normalize());

        Vector true_up = left.cross(forward);

        Matrix orientation = new Matrix(new double[][] {
                {left.x, left.y, left.z, 0},
                {true_up.x, true_up.y, true_up.z, 0},
                {-forward.x, -forward.y, -forward.z, 0},
                {0, 0, 0, 1}
        });

        return orientation.multiply(Matrix.translation(-from.x, -from.y, -from.z));
    }

    public void addShape(Shape s) {
        shapes.add(s);
    }
}
