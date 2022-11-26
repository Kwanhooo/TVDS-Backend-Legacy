package org.csu.tvds.dto.structure;

import java.util.ArrayList;
import java.util.List;

public class MonthNode {
    public String id;
    public int label;
    public List<DayNode> children;

    public MonthNode(String id, int month) {
        this.id = id;
        this.label = month;
        children = new ArrayList<>();
    }
}