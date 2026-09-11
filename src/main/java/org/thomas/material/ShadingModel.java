package org.thomas.material;

import org.thomas.light.Light;
import org.thomas.math.Color;
import org.thomas.math.Vector;
import org.thomas.raytracer.Intersection;
import org.thomas.scene.World;

import java.util.List;

public interface ShadingModel {
    default Color evaluate(Vector n, Vector v, Light light, Vector worldPoint, Color baseColor, World world, Intersection intersection, boolean inShadow) {
        return evaluate(n, v, List.of(light), worldPoint, baseColor, world, intersection);
    }


    default Color evaluate(Vector n, Vector v, List<Light> lights,
                           Vector worldPoint, Color baseColor,
                           World world, Intersection intersection) {
        return evaluate(n, v, lights, worldPoint, baseColor,
                world, intersection, Vector.vector3(0,0,0), Vector.vector3(0,0,0));
    }

    Color evaluate(Vector n, Vector v, List<Light> lights,
                   Vector worldPoint, Color baseColor,
                   World world, Intersection intersection,
                   Vector tangent, Vector bitangent);
}