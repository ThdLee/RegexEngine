package nfa;

import java.util.Set;

class NFAEdgeFactory {
    static NFAEdge createEpsilonEdge(NFANode node) {
        return new NFAEdge(node);
    }

    static NFAEdge createCharEdge(NFANode node, char c) {
        return new NFAEdge(node, c);
    }

    static NFAEdge createSetEdge(NFANode node, Set<Character> set) {
        return new NFAEdge(node, set);
    }

}
