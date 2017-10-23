package com.regex.nfa;

import java.util.Stack;

class NFANodeManager {
    private static final int MAX_NODES = 256;

    private Stack<NFANode> busyNodes;
    private Stack<NFANode> freeNodes;

    NFANodeManager() {
        busyNodes = new Stack<>();
        freeNodes = new Stack<>();

        for (int i = 0; i < MAX_NODES; i++) {
            NFANode node = new NFANode();
            freeNodes.push(node);
        }
    }

    NFANode createNode() {
        if (freeNodes.empty())
            return new NFANode();

        NFANode node = freeNodes.pop();
        node.clear();
        busyNodes.push(node);
        return node;
    }

    void clear() {
        while (!busyNodes.empty()) {
            freeNodes.push(busyNodes.pop());
        }
    }

}
