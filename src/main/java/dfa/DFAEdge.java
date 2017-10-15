package dfa;

import nfa.EdgeLabel;

public class DFAEdge {
    private DFANode target;
    private EdgeLabel label;

    public DFAEdge(DFANode target, EdgeLabel label) {
        this.target = target;
        this.label = label;
    }

    public DFANode getTarget() {
        return target;
    }

    public EdgeLabel getData() {
        return label;
    }

    public boolean hasChar(char c) {
        if (label.getChar() == EdgeLabel.EPSILON) {
            throw new RuntimeException();
        } else if (label.getChar() == EdgeLabel.SET) {
            return label.getSet().contains(c);
        }
        return label.getChar() == c;
    }
}
