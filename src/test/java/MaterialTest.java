import org.junit.jupiter.api.Test;
import org.thomas.material.Material;
import org.thomas.light.Light;
import org.thomas.light.PointLight;
import org.thomas.material.PhongShadingModel;
import org.thomas.math.Color;
import org.thomas.math.Vector;
import org.thomas.pattern.StripePattern;
import org.thomas.raytracer.Intersection;
import org.thomas.raytracer.IntersectionComputation;
import org.thomas.raytracer.Ray;
import org.thomas.shape.Plane;
import org.thomas.shape.Shape;
import org.thomas.shape.Sphere;

import static org.junit.jupiter.api.Assertions.*;

public class MaterialTest {
    @Test
    void defaultMaterial()
    {
        Material m = new Material();
        assertEquals(new Color(1,1,1), m.albedo);
        assertEquals(0.1, ((PhongShadingModel)m.shadingModel).ambient );
        assertEquals(0.9, ((PhongShadingModel)m.shadingModel).diffuse);
        assertEquals(0.9, ((PhongShadingModel)m.shadingModel).specular);
        assertEquals(200.0, ((PhongShadingModel)m.shadingModel).shininess);
    }

    Material mat = new Material();
    Vector p = Vector.point(0,0,0);

    @Test void lightingWithEyeBetweenLightAndSurface()
    {
        Vector eyev = Vector.vector3(0,0,-1);
        Vector normalv = Vector.vector3(0,0,-1);
        Light light = new PointLight(Vector.point(0,0,-10), new Color(1,1,1));
        Color result = mat.lighting(light,p, eyev, normalv, false,null, null);
        assertEquals(new Color(1.9,1.9,1.9), result);
    }

    @Test void lightingWithEyeBetweenLightAndSurfaceEyeOffset45()
    {
        Vector eyev = Vector.vector3(0,Math.sqrt(2)/2,-Math.sqrt(2)/2);
        Vector normalv = Vector.vector3(0,0,-1);
        Light light = new PointLight(Vector.point(0,0,-10), new Color(1,1,1));
        Color result = mat.lighting(light,p, eyev, normalv, false,null, null);
        assertEquals(new Color(1.0,1.0,1.0), result);
    }

    @Test void LightingWithEyeOppositeSurface()
    {
        Vector eyev = Vector.vector3(0,0, -1);
        Vector normalv = Vector.vector3(0,0,-1);
        Light light = new PointLight(Vector.point(0,10,-10), new Color(1,1,1));
        Color result = mat.lighting(light,p, eyev, normalv, false,null,null);
        assertEquals(new Color(0.7364,0.7364,0.7364), result);
    }

    @Test void LightingWithEyeInPathOfReflectionVector()
    {
        Vector eyev = Vector.vector3(0,-Math.sqrt(2)/2, -Math.sqrt(2)/2);
        Vector normalv = Vector.vector3(0,0,-1);
        Light light = new PointLight(Vector.point(0,10,-10), new Color(1,1,1));
        Color result = mat.lighting(light,p, eyev, normalv, false,null, null);
        assertEquals(new Color(1.6364,1.6364,1.6364), result);
    }

    @Test void LightingWithLightBehindSurface()
    {
        Vector eyev = Vector.vector3(0,0, -1);
        Vector normalv = Vector.vector3(0,0,-1);
        Light light = new PointLight(Vector.point(0,0,10), new Color(1,1,1));
        Color result = mat.lighting(light,p, eyev, normalv, false,null, null);
        assertEquals(new Color(0.1,0.1,0.1), result);
    }

    @Test void LightingWithSurfaceInShadow()
    {
        Vector eyev = Vector.vector3(0,0, -1);
        Vector normalv = Vector.vector3(0,0,-1);
        Light light =  new PointLight(Vector.point(0,0,-10), new Color(1,1,1));
        boolean inShadow = true;
        Color result = mat.lighting(light,p, eyev, normalv, inShadow,null, null);
        assertEquals(new Color(0.1,0.1,0.1), result);
    }

    @Test void LightingWithMaterialApplied()
    {
        Material m = new Material();
        m.pattern = new StripePattern(new Color(1,1,1), new Color(0,0,0));
        ((PhongShadingModel)m.shadingModel).ambient = 1;
        ((PhongShadingModel)m.shadingModel).diffuse = 0;
        ((PhongShadingModel)m.shadingModel).specular = 0;
        Vector eyev = Vector.vector3(0,0,-1);
        Vector normalv = Vector.vector3(0,0,-1);
        Light light = new PointLight(Vector.point(0,0,-10), new Color(1,1,1));
        Color c1 = m.lighting(light,Vector.point(0.9,0,0), eyev, normalv, false,new Sphere(), null);
        Color c2 = m.lighting(light,Vector.point(1.1,0,0), eyev, normalv, false,new Sphere(), null);

        assertEquals(new Color(1,1,1), c1);
        assertEquals(new Color(0,0,0), c2);
    }

    @Test void ReflectivityOfNewMaterial()
    {
        Material m = new Material();
        assertEquals(0,m.reflective);
    }

    @Test void PrecomputingReflectV()
    {
        Shape s = new Plane();
        Ray r = new Ray(Vector.point(0,1,-1), Vector.vector3(0, -Math.sqrt(2)/2, Math.sqrt(2)/2));
        Intersection i = new Intersection(Math.sqrt(2), s);
        IntersectionComputation comps = i.prepareComputation(r);
        assertEquals(Vector.vector3(0, Math.sqrt(2)/2, Math.sqrt(2)/2), comps.reflectv);
    }

    @Test void TransparencyAndRefractiveIndexForDefaultMaterial()
    {
        Material m = new Material();
        assertEquals(0,m.transparency);
        assertEquals(1,m.refractive);
    }
}