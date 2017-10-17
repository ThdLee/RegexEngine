package regex.token;


public class RangeToken extends Token {
    public static final RangeToken STAR_RANGE = new RangeToken() {{
        setStarState();
    }};

    public static final RangeToken PLUS_RANGE = new RangeToken() {{
        setPlusState();
    }};

    public static final RangeToken OPTION_RANGE = new RangeToken() {{
        setOptionState();
    }};

    public enum State {
        STAR,
        PLUS,
        OPTION,

        MANY,
        MORE,
        RANGE
    }

    private int low;
    private int high;

    public int getLow() {
        return low;
    }

    public int getHigh() {
        return high;
    }

    private State state;

    public State getState() {
        return state;
    }

    public void setStarState() {
        this.state = State.STAR;
    }

    public void setPlusState() {
        this.state = State.PLUS;
    }

    public void setOptionState() {
        this.state = State.OPTION;
    }

    public void setManyState(int low) {
        if (low < 0) throw new TokenException();
        this.state = State.MANY;
        this.low = low;
    }

    public void setMoreState(int low) {
        if (low < 0) throw new TokenException();
        switch (low) {
            case 0:
                setStarState();
                break;
            case 1:
                setPlusState();
            default:
                this.state = State.MORE;
                this.low = low;
                break;
        }
    }

    public void setRangeState(int low, int high) {
        if (low >= high) throw new TokenException();
        if (low < 0) throw new TokenException();

        if (low == 0 && high == 1) {
            setOptionState();
        } else {
            this.state = State.RANGE;
            this.low = low;
            this.high = high;
        }
    }

    @Override
    public String toString() {
        return "Range " + state;
    }
}
