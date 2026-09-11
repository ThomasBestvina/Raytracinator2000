package org.thomas.pattern;

import org.thomas.math.Color;
import org.thomas.math.Matrix;
import org.thomas.math.Vector;
import org.thomas.shape.Shape;

public class StripePattern extends Pattern {

    public StripePattern(Color a, Color b) {
        super(a, b);
    }

    public Color colorAt(Vector point) {
        return (Math.floor(point.x) % 2 == 0) ? a : b;
    }
}