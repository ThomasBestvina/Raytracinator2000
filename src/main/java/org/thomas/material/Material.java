package org.thomas.material;

import org.thomas.light.Light;
import org.thomas.math.Color;
import org.thomas.math.Vector;
import org.thomas.normalperturber.NormalPerturber;
import org.thomas.pattern.Pattern;
import org.thomas.raytracer.Intersection;
import org.thomas.scene.World;
import org.thomas.shape.Shape;

import java.util.List;

public class Material {
    public double reflective;
    public double transparency;
    public double refractive;
    public boolean castsShadow;
    public Pattern pattern;
    public Color albedo;
    public NormalPerturber normalPerturber;
    public ShadingModel shadingModel;

    public Material()
    {
        refractive = 1.0;
        albedo = new Color(1,1,1);
        castsShadow = true;
        shadingModel = new PhongShadingModel(0.1,0.9,0.9,200.0);
    }


    public Color lighting(List<Light> lights, Vector worldPoint, Vector eyev,
                          Vector normalv, World world, Shape shape, Intersection intersection) {
        Color baseColor = pattern != null
                ? pattern.colorAt(shape, worldPoint, intersection.texU, intersection.texV)
                : this.albedo;

        Vector tangent   = intersection.tangent   != null ? intersection.tangent   : Vector.vector3(0,0,0);
        Vector bitangent = intersection.bitangent != null ? intersection.bitangent : Vector.vector3(0,0,0);

        return shadingModel.evaluate(normalv, eyev, lights, worldPoint, baseColor, world, intersection);
    }

    public Color lighting(Light light, Vector worldPoint, Vector eyev,
                          Vector normalv, boolean inShadow, Shape shape, Intersection intersection) {
        Color baseColor = this.albedo;
        if (pattern != null) {
            baseColor = intersection != null
                    ? pattern.colorAt(shape, worldPoint, intersection.texU, intersection.texV)
                    : pattern.colorAt(shape, worldPoint);
        }
        if (shadingModel instanceof PhongShadingModel phong) {
            return phong.evaluate(normalv, eyev, light, worldPoint, baseColor, inShadow);
        }
        // non phong model
        return shadingModel.evaluate(normalv, eyev, List.of(light), worldPoint, baseColor, null, intersection);
    }


    public Vector perturbNormal(Vector normal, Vector tangent, Vector bitangent,double u, double v)
    {
        if (normalPerturber == null) return normal;
        return normalPerturber.perturb(normal, tangent, bitangent, u, v);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Material mat) {
            return mat.albedo.equals(this.albedo);
        }
        return false;
    }
}