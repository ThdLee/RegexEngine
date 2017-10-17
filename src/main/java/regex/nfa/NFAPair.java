package regex.nfa;

import java.util.HashSet;
import java.util.Set;

public class NFAPair implements Cloneable {


    public NFANode startNode;
    public NFANode endNode;

    boolean or;

    private Set<NFANode> set;

    private Set<EdgeLabel> labelSet;

    NFAPair() {
        set = new HashSet<>();
        labelSet = new HashSet<>();
        or = false;
    }

    Iterable<NFANode> getNodeSet() {
        return set;
    }

    void addNode(NFANode node) {
        set.add(node);
    }


    public Iterable<EdgeLabel> getLabelSet() {
        return labelSet;
    }

    void addLabel(EdgeLabel label) {
        labelSet.add(label);
    }

    void merge(NFAPair pair) {
        set.addAll(pair.set);
        labelSet.addAll(pair.labelSet);
        pair.deprecated();
    }

    public void deprecated() {
        startNode = null;
        endNode = null;
        set = null;
    }

}
