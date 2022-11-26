package org.csu.tvds.dto.structure;

import java.util.ArrayList;
import java.util.List;

public class YearNode {
    public String id;
    public int label;
    public List<MonthNode> children;

    public YearNode(String id, int year) {
        this.id = id;
        this.label = year;
        children = new ArrayList<>();
    }
}
