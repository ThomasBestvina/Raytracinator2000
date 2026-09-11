package org.thomas.normalperturber;

import org.thomas.material.Texture;
import org.thomas.math.Color;
import org.thomas.math.Vector;

public class NormalMap implements NormalPerturber {
    Texture texture;
    public NormalMap(Texture texture)
    {
        this.texture = texture;
    }

    @Override
    public Vector perturb(Vector normal, Vector tanget, Vector bitangent, double u, double v) {
        Color sample = texture.sample(u,v);

        double nx = sample.r() * 2.0 - 1.0;
        double ny = sample.g() * 2.0 - 1.0;
        double nz = sample.b() * 2.0 - 1.0;

        return tanget.multiply(nx).add(bitangent.multiply(ny)).add(normal.multiply(nz)).normalize();
    }
}