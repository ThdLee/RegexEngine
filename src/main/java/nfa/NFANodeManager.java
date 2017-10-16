package nfa;

import java.util.ArrayList;
import java.util.HashSet;

public class NFANodeManager {
    private static final NFANodeManager instance = new NFANodeManager();

    private NFANodeManager() {}

    public static NFANodeManager getInstance() {
        return instance;
    }

    private int count = 0;

    private ArrayList<NFANode> nodes = new ArrayList<>();

    public NFANode createNode() {
        NFANode node = new NFANode(count++);
        nodes.add(node);
        return node;
    }

    public NFANode copyNode(NFANode copy) {
        NFANode node = new NFANode(count++);
        node.edgeSet = new HashSet<>();
        for (NFAEdge edge : copy.edgeSet) {
            node.edgeSet.add(edge.clone());
        }
        node.setState(copy.getState());
        nodes.add(node);
        return node;
    }

    public void clear() {
        count = 0;
        nodes.clear();
    }


    public ArrayList<NFANode> getNodes() {
        return nodes;
    }

    public String allNodesToString() {
        StringBuilder str = new StringBuilder();
        for (NFANode node : nodes) {
            str.append(node.toString()).append('\n');
        }
        return str.toString();
    }
}
