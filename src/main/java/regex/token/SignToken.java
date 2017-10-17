package regex.token;

public class SignToken extends Token{


    private final static char CONNECT_CHAR = Character.MAX_HIGH_SURROGATE;

    public final static SignToken PAREN_START = new SignToken('(');
    public final static SignToken PAREN_END = new SignToken(')');
    public final static SignToken OR = new SignToken('|');
    public final static SignToken CONNECT = new SignToken(CONNECT_CHAR);

    private char sign;

    private SignToken(char s) {
        sign = s;
    }

    public char getChar() {
        return sign;
    }

    public boolean equals(Object o) {
        if (o instanceof SignToken) {
            SignToken token = (SignToken) o;
            return this.sign == token.sign;
        }
        return false;
    }

    @Override
    public String toString() {
        if (sign == CONNECT_CHAR) return "Sign connect";
        return "Sign " + sign;
    }
}
