package regex;

import regex.dfa.DFAEdge;
import regex.dfa.DFANode;

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
    private DFANode start;

    Pattern(DFANode start) {
        this.start = start;
    }

    void setPrefix(boolean prefix) {
        this.prefix = prefix;
    }

    void setSuffix(boolean suffix) {
        this.suffix = suffix;
    }

    /**
     * @param content The character sequence to be matched
     * @return whether or not the regular expression matches on the content
     */
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

    /**
     * @param content The character sequence to be matched
     * @return all sub string in {@param content} which
     * the regular expression can matches
     */
    public List<String> matchAll(String content) {
        this.content = content;
        index = 0;
        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < content.length();) {
            if (matchInNodeForMatchAll(start, i, false)) {
                list.add(content.substring(i, index));
                i = index;
                continue;
            }
            i++;
        }
        return list;
    }

    /**
     * print all nodes and edges in DFA
     */
    public void printDFA() {
        StringBuilder str = new StringBuilder();
        Set<DFANode> set = new HashSet<>();
        printDFA(start, str, set);

        System.out.println(str.toString());
    }

    private boolean matchInNode(DFANode node, int i) {
        char c = getChar(i);
        if (node.isEnd()) return true;
        if (c == EOS) return false;
        boolean res = false;
        for (DFAEdge edge : node.getEdges()) {
            DFANode next = edge.getTarget();
            if (edge.hasChar(c)) {
                res = res || matchInNode(next, i+1);
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

    private boolean matchInNodeForMatchAll(DFANode node, int i, boolean end) {
        char c = getChar(i);
        if (node.isEnd()) {
            index = i > index ? i : index;
            if (!end) end = true;
        } else if (!node.isEnd() && end){
            return true;
        }
        if (c == EOS) return false;
        boolean res = false;
        for (DFAEdge edge : node.getEdges()) {
            DFANode next = edge.getTarget();
            if (edge.hasChar(c)) {
                res = res || matchInNodeForMatchAll(next, i+1, end);
            }
        }
        return res || end;
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

    private char getChar(int index) {
        if (index >= content.length()) return EOS;
        return content.charAt(index);
    }
}
