package org.thomas.pattern;

import org.thomas.math.Color;
import org.thomas.math.Vector;

public class RingPattern extends Pattern
{

    public RingPattern(Color a, Color b) {
        super(a, b);
    }

    @Override
    public Color colorAt(Vector point) {

        return (Math.floor(Math.sqrt(point.x*point.x + point.z+point.z)) % 2 == 0) ? a : b;
    }
}