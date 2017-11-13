package com.regex.nfa;

import java.util.Stack;

class NFANodeManager {

    private Stack<NFANode> busyNodes;
    private Stack<NFANode> freeNodes;

    NFANodeManager() {
        busyNodes = new Stack<>();
        freeNodes = new Stack<>();
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
