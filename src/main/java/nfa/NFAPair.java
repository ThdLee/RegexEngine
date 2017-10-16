package nfa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class NFAPair implements Cloneable {


    public NFANode startNode;
    public NFANode endNode;

    boolean or;

    public Set<NFANode> set;

    public Set<EdgeLabel> labelSet;

    public NFAPair() {
        set = new HashSet<>();
        labelSet = new HashSet<>();
        or = false;
    }

    public Set<EdgeLabel> getLabelSet() {
        return labelSet;
    }

    public void deprecated() {
        startNode = null;
        endNode = null;
        set = null;
    }

    public NFAPair clone(NFANodeManager manager) {
        NFAPair pair = null;
        try {
            pair = (NFAPair) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        pair.set = new HashSet<>();
        pair.labelSet = new HashSet<>();

        HashMap<NFANode, NFANode> map = new HashMap<>();
        for (NFANode node : set) {
            NFANode n = manager.copyNode(node);
            map.put(node, n);
            pair.set.add(n);
        }

        for (NFANode node : pair.set) {
            for (NFAEdge edge : node.edgeSet) {
                NFANode n = map.get(edge.getTarget());
                if (n == null) edge.setTarget(edge.getTarget());
                else edge.setTarget(n);
            }
        }

        pair.startNode = map.get(startNode);
        pair.endNode = map.get(endNode);
        pair.labelSet.addAll(labelSet);
        return pair;
    }
}
