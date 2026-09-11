package org.thomas.normalperturber;

import org.thomas.math.Vector;

public interface NormalPerturber {
    Vector perturb(Vector normal, Vector tanget, Vector bitangent, double u, double v);
}