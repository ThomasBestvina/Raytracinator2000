package org.thomas.material;

import org.thomas.light.Light;
import org.thomas.math.Color;
import org.thomas.math.Vector;
import org.thomas.raytracer.Intersection;
import org.thomas.scene.World;

import java.util.List;

public class PhongShadingModel implements ShadingModel {
    public double ambient;
    public double specular;
    public double diffuse;
    public double shininess;

    public PhongShadingModel(double ambient, double diffuse, double specular, double shininess) {
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
    }

    @Override
    public Color evaluate(Vector n, Vector v, List<Light> lights, Vector worldPoint,
                          Color baseColor, World world, Intersection intersection, Vector tangent, Vector bitangent) {
        Color ambient = baseColor.multiply(this.ambient);
        Color direct = Color.black;
        for (Light light : lights) {
            boolean inShadow = world != null && world.isShadowed(worldPoint, light);
            direct = direct.add(evaluateSingle(n, v, light, worldPoint, baseColor, inShadow));
        }
        return ambient.add(direct);
    }

    public Color evaluate(Vector n, Vector v, Light light, Vector worldPoint,
                          Color baseColor, boolean inShadow) {
        Color ambient = baseColor.multiply(this.ambient);
        return ambient.add(evaluateSingle(n, v, light, worldPoint, baseColor, inShadow));
    }

    private Color evaluateSingle(Vector n, Vector v, Light light,
                                 Vector worldPoint, Color baseColor, boolean inShadow) {
        Color effectiveColor = baseColor.multiply(light.getIntensity());
        Vector lightv = light.getPosition().subtract(worldPoint).normalize();
        double lightDotNormal = lightv.dot(n);
        Color diffuseColor = Color.black;
        Color specularColor = Color.black;
        if (lightDotNormal >= 0 && !inShadow) {
            diffuseColor = effectiveColor.multiply(diffuse).multiply(lightDotNormal);
            Vector reflectv = lightv.negate().reflect(n);
            double reflectDotEye = reflectv.dot(v);
            if (reflectDotEye > 0) {
                double factor = Math.pow(reflectDotEye, shininess);
                specularColor = light.getIntensity().multiply(specular).multiply(factor);
            }
        }
        return diffuseColor.add(specularColor);
    }
}