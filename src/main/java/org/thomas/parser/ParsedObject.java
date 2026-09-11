package org.thomas.parser;

import org.thomas.math.Vector;
import org.thomas.shape.Group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParsedObject {
    public List<Vector> vertices = new ArrayList<>();
    public List<double[]> texCoords = new ArrayList<>();
    public List<Vector> normals = new ArrayList<>();
    public Group defaultGroup = new Group();
    public HashMap<String, Group> groups = new HashMap<>();

    public Group toGroup() {
        for(Group group : groups.values()) {
            defaultGroup.addChild(group);
        }
        return defaultGroup;
    }
}