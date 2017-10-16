import dfa.DFAEdge;
import dfa.DFANode;
import nfa.Lexer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Pattern {
    private static final char EOS = Character.MAX_VALUE;

    private boolean prefix;
    private boolean suffix;

    private String content;
    private int index;
    DFANode start;

    public Pattern(DFANode start) {
        this.start = start;
    }

    public void setPrefix(boolean prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(boolean suffix) {
        this.suffix = suffix;
    }

    public boolean match(String content) {
        this.content = content;

        if (prefix && suffix) {
            if (matchInNodeWithSuffix(start, 0))
                return true;
        } else if (prefix) {
            if (matchInNode(start, 0))
                return true;
        } else if (suffix) {
            for (int i = 0; i < content.length(); i++) {
                if (matchInNodeWithSuffix(start, 0))
                    return true;
            }
        } else {
            for (int i = 0; i < content.length(); i++) {
                if (matchInNode(start, i))
                    return true;
            }
        }


        return false;
    }

    public List<String> matchAll(String content) {
        this.content = content;
        index = 0;
        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < content.length(); i++) {
            if (matchInNodeForMatchAll(start, i)) {
                list.add(content.substring(i, index));
                i = index;
            }
        }
        return list;
    }

    private boolean matchInNode(DFANode node, int i) {
        char c = getChar(i);
        if (node.isEnd()) return true;
        if (c == EOS) return false;
        boolean res = false;
        for (DFAEdge edge : node.getEdges()) {
            DFANode next = edge.getTarget();
            if (edge.hasChar(c)) {
                res = res || matchInNode(next, ++i);
            }
        }
        return res;
    }

    private boolean matchInNodeWithSuffix(DFANode node, int i) {
        char c = getChar(i);
        if (c == EOS && node.isEnd()) { return true; }
        else if (c == EOS) { return false; }
        boolean res = false;
        for (DFAEdge edge : node.getEdges()) {
            DFANode next = edge.getTarget();
            if (edge.hasChar(c)) {
                res = res || matchInNodeWithSuffix(next, ++i);
            }
        }
        return res;
    }

    private boolean matchInNodeForMatchAll(DFANode node, int i) {
        char c = getChar(i++);
        if (node.isEnd()) {
            index = i;
            return true;
        }
        if (c == EOS) return false;
        boolean res = false;
        for (DFAEdge edge : node.getEdges()) {
            DFANode next = edge.getTarget();
            if (edge.hasChar(c)) {
                res = res || matchInNodeForMatchAll(next, i);
            }
        }
        return res;
    }

    public void printTrace(String content) {
        this.content = content;
        index = 0;

        StringBuilder str = new StringBuilder();

        traceMatch(start, 0, str);

        System.out.println(str.toString());
    }

    private void traceMatch(DFANode node, int index, StringBuilder str) {
        str.append(node);
        char c = getChar(index);
        if (c == EOS) {
            str.append(" EOS ");
            return;
        }
        for (DFAEdge edge : node.getEdges()) {
            DFANode next = edge.getTarget();
            if (edge.hasChar(c)) {
                str.append(edge).append('\n');
                traceMatch(next, ++index, str);
            }
        }
    }

    private char getChar(int index) {
        if (index >= content.length()) return EOS;
        return content.charAt(index);
    }

    public void printDFA() {
        StringBuilder str = new StringBuilder();
        Set<DFANode> set = new HashSet<>();
        printDFA(start, str, set);

        System.out.println(str.toString());
    }

    private void printDFA(DFANode node, StringBuilder str, Set<DFANode> set) {
        if (set.contains(node)) return;
        else {
            str.append(node);
            for (DFAEdge edge : node.getEdges()) {
                str.append(edge);
            }
            str.append('\n');
            set.add(node);
        }
        for (DFAEdge edge : node.getEdges()) {
            printDFA(edge.getTarget(), str, set);
        }
    }
}
