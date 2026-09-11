package org.thomas.pattern;

import org.thomas.math.Color;
import org.thomas.math.Vector;
import org.thomas.shape.Shape;

public class LinearGradientPattern extends Pattern {

    public LinearGradientPattern(Color a, Color b) {
        super(a, b);
    }

    @Override
    public Color colorAt(Vector point) {
        Color distance = b.subtract(a);
        double fraction = point.x - Math.floor(point.x);
        return a.add(distance.multiply(fraction));
    }
}