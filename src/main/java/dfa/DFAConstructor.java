package dfa;

import nfa.*;

import java.util.*;

public class DFAConstructor {
    private NFAConstructor nfa;

    private Map<Set<NFANode>, DFAEntry> T;

    public DFAConstructor() {
        T = new HashMap<>();
        nfa = new NFAConstructor();
    }

    public DFAConstructor(NFAConstructor nfa) {
        T = new HashMap<>();
        this.nfa = nfa;
    }

    public DFANode constructDFA(String regex) {
        NFAPair pair = nfa.constructNFAPair(regex);
        T.clear();

        covertNFAToDFA(pair);
        Map<Set<NFANode>, DFANode> map = new HashMap<>();

        int count = 0;
        for (Set<NFANode> set : T.keySet()) {
            map.put(set, new DFANode(count++));
        }

        DFANode start = null;
        for (Set<NFANode> set : T.keySet()) {
            DFANode node = map.get(set);
            if (set.contains(pair.startNode) && set.contains(pair.endNode)) {
                node.setState(DFANode.STATE.BOTH);
                start = node;
            } else if (set.contains(pair.startNode)) {
                node.setState(DFANode.STATE.START);
                start = node;
            } else if (set.contains(pair.endNode)) {
                node.setState(DFANode.STATE.END);
            }

            DFAEntry entry = T.get(set);
            for (EdgeLabel label : entry.map.keySet()) {
                Set<NFANode> s = entry.map.get(label);
                if (s.isEmpty()) continue;

                node.addEdge(new DFAEdge(map.get(s), label));
            }
        }

        pair.deprecated();
        return start;
    }

    private void covertNFAToDFA(NFAPair pair) {
        Set<Set<NFANode>> Q = new HashSet<>();
        Set<NFANode> initSet = new HashSet<>();
        initSet.add(pair.startNode);
        handleEpsilonClosure(initSet);
        Q.add(initSet);

        Stack<Set<NFANode>> workList = new Stack<>();
        workList.push(initSet);
        while (!workList.isEmpty()) {
            Set<NFANode> q = workList.pop();
            T.put(q, DFAEntry.createEntry());
            for (EdgeLabel c : pair.getLabelSet()) {
                Set<NFANode> t = delta(q, c);
                handleEpsilonClosure(t);
                DFAEntry entry = T.get(q);
                entry.map.put(c, t);
                if (!t.isEmpty() && !Q.contains(t)) {
                    Q.add(t);
                    workList.push(t);
                }
            }
        }
    }

    private void handleEpsilonClosure(Set<NFANode> set) {

        for (NFANode node : set) {
            nonEpsilonTarget(node, set);
        }

    }

    private void nonEpsilonTarget(NFANode node, Set<NFANode> set) {

        for (NFAEdge edge : node.getEdges()) {
            NFANode n = edge.getTarget();
            if (edge.isEpsilon()) {
                set.add(n);
                nonEpsilonTarget(n, set);
            }
        }
    }


    private Set<NFANode> delta(Set<NFANode> set, EdgeLabel c) {
        Set<NFANode> resSet = new HashSet<>();
        for (NFANode node : set) {
            deltaImpl(node, resSet, c, false);
        }
        return resSet;
    }

    private void deltaImpl(NFANode node, Set<NFANode> res, EdgeLabel label, boolean added) {
        for (NFAEdge edge : node.getEdges()) {
            NFANode n = edge.getTarget();
            if (!added) {
                if (!edge.isEpsilon() && edge.hasLabel(label)) {
                    res.add(n);
                    deltaImpl(n, res, label, true);
                } else if (edge.isEpsilon()) {
                    deltaImpl(n, res, label, false);
                }
            }
        }

    }

    public String toTableString() {
        StringBuilder str = new StringBuilder();
        for (Set<NFANode> set : T.keySet()) {
            str.append('[');
            for (NFANode node : set) {
                str.append(node.getId()).append(' ');
            }
            str.append("] ").append(T.get(set)).append('\n');
        }
        return str.toString();
    }

}
