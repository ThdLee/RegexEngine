package dfa;

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

    ArrayList<DFAEdge> edges;
    Set<Integer> targets;
    private int id;
    private STATE state;

    public DFANode(int id) {
        edges = new ArrayList<>();
        targets = new HashSet<>();
        this.id = id;
        this.state = STATE.NONE;
    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void addEdge(DFAEdge edge) {
        int id = edge.getTarget().getId();
        if (targets.contains(id)) return;
        edges.add(edge);
        targets.add(id);
    }

    public Iterable<DFAEdge> getEdges() {
        return edges;
    }

    public boolean isEnd() {
        return state == STATE.END || state == STATE.BOTH;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("[node:").append(id).append(' ').append(state).append(']');
        return str.toString();
    }

}
