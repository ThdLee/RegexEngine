package token;

public class SignToken extends Token{

//    public static final SignToken CONNECT = new SignToken(Sign.CONNECT);
//
//    private Sign sign;
//
//    public SignToken() {}
//
//    public SignToken(Sign s) {
//        sign = s;
//    }
//
//    public Sign getSign() {
//        return sign;
//    }
//
//    public void setSign(Sign sign) {
//        this.sign = sign;
//    }
//
//    public boolean equals(SignToken signToken) {
//        return this.sign == signToken.getSign();
//    }

    public final static char CONNECT_CHAR = Character.MAX_HIGH_SURROGATE;

    public final static SignToken PAREN_START = new SignToken('(');
    public final static SignToken PAREN_END = new SignToken(')');
//    public final static SignToken STAR = new SignToken('*');
//    public final static SignToken PLUS = new SignToken('+');
//    public final static SignToken OPTION= new SignToken('?');
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
