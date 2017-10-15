package dfa;

import nfa.EdgeLabel;
import nfa.NFANode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DFAEntry {

    public static int count = 0;
    public static DFAEntry createEntry() {
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
            str.append('(');
            str.append(l);
            str.append(',');
            for (NFANode node : map.get(l)) {
                str.append(node.getId());
                str.append(' ');
            }
            str.append(')');
        }
        return str.toString();
    }
}
