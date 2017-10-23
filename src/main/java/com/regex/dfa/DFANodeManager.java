package com.regex.dfa;

import java.util.Stack;

public class DFANodeManager {

    private static final int MAX_NODES = 64;

    private Stack<DFANode> busyNodes;
    private Stack<DFANode> freeNodes;

    DFANodeManager() {
        busyNodes = new Stack<>();
        freeNodes = new Stack<>();

        for (int i = 0; i < MAX_NODES; i++) {
            DFANode node = new DFANode();
            freeNodes.push(node);
        }
    }

    DFANode createNode(int id) {
        if (freeNodes.empty())
            throw new RuntimeException("no more nodes!");

        DFANode node = freeNodes.pop();
        node.clear();
        busyNodes.push(node);
        node.setId(id);
        return node;
    }

    void clear() {
        while (!busyNodes.empty()) {
            freeNodes.push(busyNodes.pop());
        }
    }
    
}
