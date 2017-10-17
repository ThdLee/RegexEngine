package regex.nfa;

import java.util.Set;

public class EdgeLabel {
    public static final int EPSILON = -1;   //边为空条件为空
    public static final int SET = -2;       //边对应字符集

    private int ch;
    private Set<Character> set = null;

    EdgeLabel() {
        this.ch = EPSILON;
    }

    EdgeLabel(char ch) {
        this.ch = ch;
    }

    EdgeLabel(Set<Character> set) {
        this.set = set;
        this.ch = SET;
    }

    public int getChar() {
        return ch;
    }

    public Set<Character> getSet() {
        if (ch != SET) throw new RuntimeException();
        return set;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        switch (ch) {
            case EPSILON:
                str.append("epsilon");
                break;
            case SET:
                for (char c : set) str.append(c);
                break;
            default:
                str.append((char) ch);
                break;
        }
        return str.toString();
    }
}
