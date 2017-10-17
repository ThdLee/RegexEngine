package regex.token;

public class CharToken extends Token {

    private char ch;

    public CharToken(char ch) {
        this.ch = ch;
    }

    public char getChar() {
        return ch;
    }

    @Override
    public String toString() {
        return "Char " + ch;
    }
}
