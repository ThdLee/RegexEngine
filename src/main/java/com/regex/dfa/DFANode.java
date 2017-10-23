package com.regex.dfa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DFANode {
    public enum STATE {
        NONE,
        START,
        END,
        BOTH
    }

    private ArrayList<DFAEdge> edges;
    private Set<Integer> targets;
    private int id;
    private STATE state;

    DFANode() {
        edges = new ArrayList<>();
        targets = new HashSet<>();
        this.state = STATE.NONE;
        id = -1;
    }

    DFANode(int id) {
        this();
        this.id = id;
    }

    public STATE getState() {
        return state;
    }

    void setState(STATE state) {
        this.state = state;
    }

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    void addEdge(DFAEdge edge) {
        int id = edge.getTarget().getId();
        if (targets.contains(id)) return;
        edges.add(edge);
        targets.add(id);
    }

    void clear() {
        edges.clear();
        targets.clear();
        state = STATE.NONE;
        id = -1;
    }

    public Iterable<DFAEdge> getEdges() {
        return edges;
    }

    public boolean isEnd() {
        return state == STATE.END || state == STATE.BOTH;
    }

    @Override
    public String toString() {
        return "[node:" + id + ' ' + state + ']';
    }

}
