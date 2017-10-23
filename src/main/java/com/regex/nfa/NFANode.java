package com.regex.nfa;

import java.util.HashSet;
import java.util.Set;

public class NFANode {
    public enum STATE {
        NONE,
        START,
        END
    }
    private Set<NFAEdge> edgeSet;

    private STATE state;

    private int id;

    NFANode() {
        edgeSet = new HashSet<>();
        state = STATE.NONE;
    }

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    public Iterable<NFAEdge> getEdges() {
        return edgeSet;
    }

    void addEdge(NFAEdge edge) {
        edgeSet.add(edge);
    }

    STATE getState() {
        return state;
    }

    void setState(STATE state) {
        this.state = state;
    }

    void clear() {
        edgeSet.clear();
        state = STATE.NONE;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append("node:").append(id);

        if (state == STATE.END) {
            str.append(" END ");
        } else if (state == STATE.START) {
            str.append(" START ");
        }
        str.append(" edge:");
        for (NFAEdge edge : edgeSet) {
            str.append(edge.toString());
        }
        return str.toString();
    }

}
