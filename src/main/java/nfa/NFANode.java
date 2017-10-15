package nfa;

import java.util.HashSet;
import java.util.Set;

public class NFANode {
    public enum STATE {
        NONE,
        START,
        END
    }
    Set<NFAEdge> edgeSet;

    private STATE state;

    private int id;

    NFANode(int id) {
        edgeSet = new HashSet<>();
        state = STATE.NONE;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    void addEdge(NFAEdge edge) {
        edgeSet.add(edge);
    }


    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
    }


    public Iterable<NFAEdge> getEdges() {
        return edgeSet;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append("node:");
        str.append(id);

        if (state == STATE.END) {
            str.append(" END ");
            str.toString();
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
