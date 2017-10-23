package com.regex.dfa;

import com.regex.nfa.EdgeLabel;
import com.regex.nfa.NFANode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class DFAEntry {

    private static int count = 0;
    static DFAEntry createEntry() {
        return new DFAEntry(count++);
    }
    private int id;
    Map<EdgeLabel, Set<NFANode>> map;

    private DFAEntry(int id) {
        this.id = id;
        map = new HashMap<>();
    }

    private int getId() {
        return id;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (EdgeLabel l : map.keySet()) {
            str.append('(').append(l).append(',');
            for (NFANode node : map.get(l)) {
                str.append(node.getId()).append(' ');
            }
            str.append(')');
        }
        return str.toString();
    }
}
