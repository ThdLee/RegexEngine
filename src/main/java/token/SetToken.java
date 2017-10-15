package token;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SetToken extends Token {

    public static final SetToken DOT;
    public static final SetToken SET_XD;
    public static final SetToken SET_D;
    public static final SetToken SET_XS;
    public static final SetToken SET_S;
    public static final SetToken SET_XW;
    public static final SetToken SET_W;

    static {
        DOT = new SetToken();
        DOT.addToSet('\n');
        DOT.addToSet('\r');
        DOT.setComplement();

        SET_XD = new SetToken();
        for (char c = '0'; c <= '9'; c++) {
            SET_XD.addToSet(c);
        }

        SET_D = new SetToken();
        SET_D.set.addAll(SET_XD.set);
        SET_D.setComplement();

        SET_XS = new SetToken();
        SET_XS.set = new HashSet<>();
        SET_XS.addToSet('\n');
        SET_XS.addToSet('\r');
        SET_XS.addToSet('\t');
        SET_XS.addToSet('\f');

        SET_S = new SetToken();
        SET_S.set.addAll(SET_XS.set);
        SET_S.setComplement();

        SET_XW = new SetToken();
        for (char c = '0'; c <= '9'; c++) {
            SET_XW.addToSet(c);
        }
        for (char c = 'a'; c <= 'z'; c++) {
            SET_XW.addToSet(c);
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            SET_XW.addToSet(c);
        }
        SET_XS.addToSet('_');

        SET_W = new SetToken();
        SET_W.set.addAll(SET_XW.set);
        SET_W.setComplement();
    }

    public SetToken() {
        set = new HashSet<>();
    }

    private Set<Character> set;

    public Set<Character> getSet() {
        return set;
    }

    public void addToSet(char b) {
        set.add(b);
    }

    public void setComplement() {
        Set<Character> comSet = new HashSet<Character>();

        for (char c = 0; c <= Byte.MAX_VALUE; c++) {
            if (!set.contains(c)) comSet.add(c);
        }

        set = comSet;
    }

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append("Set [");
        for (char c : set) {
            str.append(c);
        }
        str.append(']');
        return str.toString();
    }
}
