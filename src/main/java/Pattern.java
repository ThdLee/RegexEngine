import dfa.DFAEdge;
import dfa.DFANode;
import nfa.Lexer;

import java.util.ArrayList;
import java.util.List;

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
        index = 0;

        if (prefix && suffix) {
            if (matchInNodeSuffix(start) && nextChar() == EOS)
                return true;
        } else if (prefix) {
            if (matchInNode(start))
                return true;
        } else if (suffix) {
            for (int i = 0; i < content.length(); i++) {
                index = i;
                if (matchInNodeSuffix(start) && nextChar() == EOS)
                    return true;
            }
        } else {
            for (int i = 0; i < content.length(); i++) {
                index = i;
                if (matchInNode(start))
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
            index = i;
            if (matchInNode(start)) {
                index = index >= content.length() ? index : index-1;
                list.add(content.substring(i, index));
                i = index;
            }
        }
        return list;
    }

    private boolean matchInNode(DFANode node) {
        char c = nextChar();
        if (c == EOS) return node.isEnd();
        for (DFAEdge edge : node.getEdges()) {
            DFANode next = edge.getTarget();
            if (edge.hasChar(c)) {
                return matchInNode(next) || next.isEnd();
            }
        }
        return false;
    }

    private boolean matchInNodeSuffix(DFANode node) {
        char c = nextChar();
        if (c == EOS && node.isEnd()) {
            return true;
        } else if (c == EOS) {
            return false;
        }
        for (DFAEdge edge : node.getEdges()) {
            DFANode next = edge.getTarget();
            if (edge.hasChar(c)) {
                return matchInNodeSuffix(next);
            }
        }
        return false;
    }


    private char nextChar() {
        if (index >= content.length()) return EOS;
        return content.charAt(index++);
    }

}
