package regex.nfa;

import java.util.Set;

public class NFAEdge implements Cloneable {
    private NFANode target;
    private EdgeLabel label;

    NFAEdge(NFANode target) {
        this.label = new EdgeLabel();
        this.target = target;
    }

    NFAEdge(NFANode target, char c) {
        this.label = new EdgeLabel(c);
        this.target = target;
    }

    NFAEdge(NFANode target, Set<Character> set) {
        this.label = new EdgeLabel(set);
        this.target = target;
    }

    void setTarget(NFANode target) {
        this.target = target;
    }

    public NFANode getTarget() {
        return target;
    }

    public EdgeLabel getLabel() {
        return label;
    }

    public boolean isEpsilon() {
        return label.getChar() == EdgeLabel.EPSILON;
    }

    public boolean isSet() {
        return label.getChar() == EdgeLabel.SET;
    }

    public boolean isChar() {
        return !isEpsilon() && !isSet();
    }

    @Override
    public NFAEdge clone() {
        NFAEdge edge = null;
        try {
            edge = (NFAEdge) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return edge;
    }

    public boolean hasLabel(EdgeLabel l) {
        if (label.getChar() == l.getChar()) {
            return label.getChar() != EdgeLabel.SET || label.getSet() == l.getSet() || label.getSet().equals(l.getSet());
        } else if (isSet() && l.getChar() >= 0) {
            return label.getSet().contains((char)l.getChar());
        }
        return false;
    }


    @Override
    public String toString() {
        return "[target:" +
                target.getId() +
                ' ' +
                label +
                ']';
    }


}
