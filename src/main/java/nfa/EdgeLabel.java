package nfa;

import java.util.Set;

public class EdgeLabel {
    public static final int EPSILON = -1;   //边为空条件为空
    public static final int SET = -2;       //边对应字符集

    private int ch;
    private Set<Character> set = null;

    public EdgeLabel() {
        this.ch = EPSILON;
    }

    public EdgeLabel(char ch) {
        this.ch = ch;
    }

    public EdgeLabel(Set<Character> set) {
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
        if (ch == EdgeLabel.EPSILON) {
            str.append("epsilon");
        } else if (ch == EdgeLabel.SET) {
            for (char c : set) {
                str.append(c);
            }
        } else {
            str.append((char) ch);
        }
        return str.toString();
    }
}
