package dfa;

import java.util.ArrayList;

public class DFANode {
    public enum STATE {
        NONE,
        START,
        END,
        BOTH
    }

    ArrayList<DFAEdge> edges;
    private int id;
    private STATE state;

    public DFANode(int id) {
        edges = new ArrayList<>();
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

    public void  addEdge(DFAEdge edge) {
        edges.add(edge);
    }

    public Iterable<DFAEdge> getEdges() {
        return edges;
    }

    public boolean isEnd() {
        return state == STATE.END || state == STATE.BOTH;
    }
}
