package com.regex.dfa;

import com.regex.nfa.*;

import java.util.*;

public class DFAConstructor {
    private NFAConstructor nfa;
    private DFANode start;
    private DFANodeManager manager;

    public DFAConstructor() {
        nfa = new NFAConstructor();
        manager = new DFANodeManager();
    }

    public DFAConstructor(NFAConstructor nfa) {
        this.nfa = nfa;
        manager = new DFANodeManager();
    }

    /***********************************************
     * Subset Construction Method
     **********************************************/

    public DFANode constructDFA(String regex) {
        manager.clear();

        NFAPair pair = nfa.constructNFAPair(regex);
        Map<Set<NFANode>, DFAEntry> table = new HashMap<>();

        covertNFAToDFA(pair, table);
        Map<Set<NFANode>, DFANode> map = new HashMap<>();

        int count = 0;
        for (Set<NFANode> set : table.keySet()) {
            map.put(set, manager.createNode(count++));
        }

        // construct subset table entries
        Set<DFANode> endNodes = new HashSet<>();
        for (Set<NFANode> set : table.keySet()) {
            DFANode node = map.get(set);
            if (set.contains(pair.startNode) && set.contains(pair.endNode)) {
                node.setState(DFANode.STATE.BOTH);
                start = node;
                endNodes.add(node);
            } else if (set.contains(pair.startNode)) {
                node.setState(DFANode.STATE.START);
                start = node;
            } else if (set.contains(pair.endNode)) {
                node.setState(DFANode.STATE.END);
                endNodes.add(node);
            }

            DFAEntry entry = table.get(set);
            for (EdgeLabel label : entry.map.keySet()) {
                Set<NFANode> s = entry.map.get(label);
                if (s.isEmpty()) continue;

                node.addEdge(new DFAEdge(map.get(s), label));
            }
        }



        Set<DFANode> allNodes = new HashSet<>();
        allNodes.addAll(map.values());
        minimizeDFA(allNodes, endNodes);

        pair.deprecated();
        return start;
    }

    private void covertNFAToDFA(NFAPair pair,  Map<Set<NFANode>, DFAEntry> table) {
        Set<Set<NFANode>> Q = new HashSet<>();
        Set<NFANode> initSet = new HashSet<>();

        initSet.add(pair.startNode);
        handleEpsilonClosure(initSet);
        Q.add(initSet);

        Stack<Set<NFANode>> workList = new Stack<>();
        workList.push(initSet);

        while (!workList.isEmpty()) {
            Set<NFANode> q = workList.pop();

            table.put(q, DFAEntry.createEntry());

            for (EdgeLabel c : pair.getLabelSet()) {
                Set<NFANode> t = delta(q, c);
                handleEpsilonClosure(t);

                DFAEntry entry = table.get(q);
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


    private Set<NFANode> delta(Set<NFANode> set, EdgeLabel label) {
        Set<NFANode> resSet = new HashSet<>();
        for (NFANode node : set) {
            deltaImpl(node, resSet, label, false);
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

    /***********************************************
     * Hopcroft algorithm
     **********************************************/

    private void minimizeDFA(Set<DFANode> allNodes, Set<DFANode> endNodes) {
        // collect all similar nodes into a set
        allNodes.removeAll(endNodes);

        Set<Set<DFANode>> T = new HashSet<>();
        Set<Set<DFANode>> P = new HashSet<>();

        T.add(allNodes);
        T.add(endNodes);
        while (P.size() != T.size()) {
            P.clear();
            P.addAll(T);
            T.clear();
            for (Set<DFANode> p : P) {
                if (p.size() > 1) {
                    Set<DFANode> s = split(p, P);
                    if (!s.isEmpty()) T.add(s);
                }
                T.add(p);
            }
        }

        // map every set into new DFA node
        Map<DFANode, DFANode> map = new HashMap<>();
        int count = 0;
        for (Set<DFANode> set : T) {
            DFANode newNode = manager.createNode(++count);
            for (DFANode node : set) {
                map.put(node, newNode);
                setState(node, newNode);
            }
        }

        for (DFANode node : map.keySet()) {
            DFANode org = map.get(node);
            for (DFAEdge edge : node.getEdges()) {
                DFAEdge newEdge = new DFAEdge(map.get(edge.getTarget()), edge.getData());
                org.addEdge(newEdge);
            }
        }

    }

    // split different nodes into two sets
    private Set<DFANode> split(Set<DFANode> p, Set<Set<DFANode>> set) {
        Set<EdgeLabel> labels = new HashSet<>();
        for (DFANode node : p) {
            for (DFAEdge edge : node.getEdges()) {
                labels.add(edge.getData());
            }
        }

        Set<DFANode> res = new HashSet<>();
        for (EdgeLabel label : labels) {
            res.clear();
            Set<DFANode> s = null;
            for (DFANode node : p) {
                if (s == null) {
                    s = getTargetSet(label, node, set);
                    res.add(node);
                } else {
                    Set<DFANode> tSet = getTargetSet(label, node, set);
                    if (s == tSet) res.add(node);
                }
            }
            if (!res.isEmpty() && res.size() != p.size()) break;
        }
        p.removeAll(res);
        return res;
    }

    private Set<DFANode> getTargetSet(EdgeLabel label, DFANode node, Set<Set<DFANode>> set) {
        for (Set<DFANode> s : set) {
            for (DFAEdge edge : node.getEdges()) {
                if (edge.getData() == label && s.contains(edge.getTarget())) {
                    return s;
                }
            }
        }
        return null;
    }

    private void setState(DFANode oldNode, DFANode newNode) {
        DFANode.STATE nState = newNode.getState();
        switch (oldNode.getState()) {
            case START:
                if (nState == DFANode.STATE.NONE) {
                    newNode.setState(DFANode.STATE.START);
                    start = newNode;
                } else if (nState == DFANode.STATE.END) {
                    newNode.setState(DFANode.STATE.BOTH);
                }
                break;
            case END:
                if (nState == DFANode.STATE.NONE) {
                    newNode.setState(DFANode.STATE.END);
                } else if (nState == DFANode.STATE.START) {
                    newNode.setState(DFANode.STATE.BOTH);
                    start = newNode;
                }
                break;
            case BOTH:
                newNode.setState(DFANode.STATE.BOTH);
                start = newNode;
                break;
        }
    }

}
