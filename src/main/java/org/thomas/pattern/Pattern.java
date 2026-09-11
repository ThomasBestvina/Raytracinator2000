package org.thomas.pattern;

import org.thomas.material.Texture;
import org.thomas.math.Color;
import org.thomas.math.Matrix;
import org.thomas.math.Vector;
import org.thomas.normalperturber.NormalMap;
import org.thomas.shape.Shape;

public abstract class Pattern {
    protected final Color a;
    protected final Color b;
    protected Matrix transform = Matrix.identityMatrix();

    public Pattern(Color a, Color b) {
        this.a = a;
        this.b = b;
    }

    public abstract Color colorAt(Vector point);

    public Color colorAt(Shape s, Vector point)
    {
        return colorAt(s, point, 0.0, 0.0);
    }

    public Color colorAt(Shape s, Vector point, double u, double v)
    {
        if(this instanceof TexturePattern)
        {
            return colorAt(Vector.point(u,v,0));
        }

        Vector shapePoint = s.worldToObject(point);
        Vector patternPoint = getTransform().inverse().multiply(shapePoint);

        return colorAt(patternPoint);
    }


    public void setTransform(Matrix transform) {
        this.transform = transform;
    }

    public Matrix getTransform() {
        return transform;
    }
}