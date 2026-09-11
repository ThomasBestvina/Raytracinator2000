package org.thomas.pattern;

import org.thomas.math.Color;
import org.thomas.math.Vector;

public class CheckersPattern extends Pattern {

    public CheckersPattern(Color a, Color b) {
        super(a, b);
    }

    @Override
    public Color colorAt(Vector point) {
        return ( (Math.floor(point.x) + Math.floor(point.y) + Math.floor(point.z)) % 2 == 0 ) ? a : b;
    }
}