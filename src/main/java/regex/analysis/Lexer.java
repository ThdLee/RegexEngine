package regex.analysis;

import regex.token.*;

import java.util.*;

public class Lexer {



    private final static Map<Character, SetToken> EscapeSets = new HashMap<Character, SetToken>() {{
        put('d', SetToken.SET_XD);
        put('D', SetToken.SET_D);
        put('s', SetToken.SET_XD);
        put('S', SetToken.SET_D);
        put('w', SetToken.SET_XW);
        put('W', SetToken.SET_D);
    }};


    private static final char EOS = Character.MAX_VALUE;

    private final static Map<Character, Character> EscapeChars = new HashMap<Character, Character>() {{
        put('t', '\t');
        put('n', '\n');
        put('f', '\f');
        put('r', '\r');
    }};

    private final static Set<Character> SignSet = new HashSet<Character>() {{
        add('*');
        add('+');
        add('?');
        add('.');
        add('(');
        add(')');
        add('[');
        add(']');
        add('{');
        add('}');
        add('^');
        add('$');
        add('\\');
        add('|');
    }};

    private String regex;
    private int index;


    private boolean prefix;
    private boolean suffix;

    public boolean isPrefix() {
        return prefix;
    }

    public boolean isSuffix() {
        return suffix;
    }


    public ArrayList<Token> buildInfixTokenList(String re) {
        if (re == null) throw new LexerException("Error: regex is null");
        regex = re;
        index = 0;
        prefix = false;
        suffix = false;

        ArrayList<Token> tokenList = new ArrayList<>();

        char c = nextChar();
        if (c == '^') {
            prefix = true;
            c = nextChar();
        }

        while (c != EOS) {
            switch (c) {
                case '*':
                    tokenList.add(RangeToken.STAR_RANGE);
                    break;
                case '+':
                    tokenList.add(RangeToken.PLUS_RANGE);
                    break;
                case '?':
                    tokenList.add(RangeToken.OPTION_RANGE);
                    break;
                case '|':
                    tokenList.add(SignToken.OR);
                    break;
                case '[':
                    tokenList.add(handleSet());
                    break;
                case '{':
                    tokenList.add(handleRange());
                    break;
                case '(':
                    tokenList.add(SignToken.PAREN_START);
                    break;
                case ')':
                    tokenList.add(SignToken.PAREN_END);
                    break;
                case '.':
                    tokenList.add(SetToken.DOT);
                    break;
                case '\\':
                    tokenList.add(handleEscape());
                    break;
                case '$':
                    if (nextChar() != EOS) throw new LexerException("Error: '$' is excepted in end");
                    break;
                default:
                    if (SignSet.contains(c)) throw new LexerException("Error: need // before '" + c +"'" );
                    tokenList.add(new CharToken(c));
                    break;
            }
            c = nextChar();
        }

        if (regex.charAt(regex.length()-1) == '$') suffix = true;


        if (tokenList.size() == 1) return tokenList;
        ArrayList<Token> list = new ArrayList<Token>();

        for (int i = 0; i < tokenList.size()-1; i++) {
            Token cur = tokenList.get(i);
            Token next = tokenList.get(i+1);
            list.add(cur);

            if (((cur instanceof CharToken || cur instanceof SetToken)
                    && (next instanceof CharToken || next == SignToken.PAREN_START || next instanceof SetToken))
                    || ((cur instanceof RangeToken || cur == SignToken.PAREN_END)
                    && (next == SignToken.PAREN_START || next instanceof CharToken || next instanceof SetToken))) {
                list.add(SignToken.CONNECT);
            }
        }
        list.add(tokenList.get(tokenList.size()-1));

        return list;
    }

    public ArrayList<Token> buildSuffixTokenList(String re) {

        ArrayList<Token> infixList = buildInfixTokenList(re);

        Stack<SignToken> signStack = new Stack<SignToken>();
        ArrayList<Token> suffixList = new ArrayList<Token>();

        for (Token token : infixList) {
            if (token instanceof CharToken ||
                    token instanceof RangeToken ||
                    token instanceof SetToken) {

                suffixList.add(token);

            } else if (token instanceof SignToken) {
                if (token.equals(SignToken.PAREN_START)) {

                    signStack.push(SignToken.PAREN_START);

                } else if (token.equals(SignToken.OR)) {

                    if (!signStack.empty() &&
                            signStack.peek().equals(SignToken.OR)) {
                        suffixList.add(SignToken.OR);
                    } else {
                        signStack.push(SignToken.OR);
                    }
                } else if (token.equals(SignToken.PAREN_END)) {
                    while (!signStack.peek().equals(SignToken.PAREN_START)) {
                        suffixList.add(signStack.pop());
                    }
                    signStack.pop();
                } else if (token.equals(SignToken.CONNECT)) {
                    if (!signStack.empty() &&
                            (signStack.peek().equals(SignToken.OR) ||
                                    signStack.peek().equals(SignToken.CONNECT))) {
                        suffixList.add(token);
                    } else {
                        signStack.push(SignToken.CONNECT);
                    }
                }
            }
        }

        while (!signStack.empty()) {
            suffixList.add(signStack.pop());
        }

        return suffixList;
    }

    private char nextChar() {
        if (index >= regex.length()) return EOS;
        return regex.charAt(index++);
    }

    private void checkOutOfIndex() {
        if (index >= regex.length())
            throw new LexerException("Error: out of bound");
    }

    private SetToken handleSet() {
        char c = nextChar();

        boolean negative = c == '^';
        SetToken token = new SetToken();

        int first = -1;
        while (c != ']') {
            if (c != '-') {
                if (c == '\\') {
                    c = nextChar();
                    if (EscapeSets.containsKey(c)) {
                        token.getSet().addAll(EscapeSets.get(c).getSet());
                    } else {
                        first = c;
                        token.addToSet(c);
                    }
                } else {
                    first = c;
                    token.addToSet(c);
                }
            } else if (first < 0) {
                token.addToSet('-');
            } else {
                c = nextChar();
                for (; first <= c; first++) {
                    token.addToSet((char)first);
                }
                first = -1;
            }
            c = nextChar();
        }

        if (negative)  token.setComplement();

        return token;
    }

    private RangeToken handleRange() {
        char c = nextChar();

        RangeToken token = new RangeToken();

        int low = 0, high = 0;
        boolean hasComma = false;
        StringBuffer str = new StringBuffer();
        while (c != '}') {
            if (c >= '0' && c <= '9') {
                str.append(c);
            } else if (c == ',') {
                if (hasComma) throw new LexerException("Error: only need one ','");
                hasComma = true;
                low = Integer.parseInt(str.toString());
                str = new StringBuffer();
            } else {
                throw new LexerException("Error: wrong '" + c + "' in {}");
            }
            c = nextChar();
        }
        if (hasComma && str.length() != 0) {
            high = Integer.parseInt(str.toString());
            token.setRangeState(low, high);
        } else if (hasComma) {
            token.setMoreState(low);
        } else {
            token.setManyState(Integer.parseInt(str.toString()));
        }

        return token;
    }

    private Token handleEscape() {
        char c = nextChar();

        if (EscapeChars.containsKey(c)) {
            return new CharToken(EscapeChars.get(c));
        }

        if (EscapeSets.containsKey(c)) {
            return EscapeSets.get(c);
        }

        return new CharToken(c);
    }


}
