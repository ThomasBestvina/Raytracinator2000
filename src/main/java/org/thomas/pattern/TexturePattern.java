package org.thomas.pattern;

import org.thomas.material.Texture;
import org.thomas.math.Color;
import org.thomas.math.Vector;


public class TexturePattern extends Pattern {
    Texture texture;

    public TexturePattern(Texture texture) {
        this.texture = texture;
        super(Color.black, Color.white);
    }

    @Override
    public Color colorAt(Vector point) {
        return texture.sample(point.x,point.y);
    }
}