package medialab.minesweeper;

public class Scenario {
    private byte diff;
    private int mines;
    private int time;
    public boolean superMine;
    public Scenario(Scenario sc){
        diff=sc.diff;
        mines=sc.mines;
        time=sc.time;
        superMine =sc.superMine;
    }
    public Scenario(byte d, int m, int t,boolean u){
        diff=d;
        mines=m;
        time=t;
        superMine =u;
    }

    public byte getDiff() {
        return diff;
    }

    public int getMines() {
        return mines;
    }

    public int getTime() {
        return time;
    }

    public boolean getSuperMine() {
        return superMine;
    }

    static public void validValueScenario( byte d,int m,int t, boolean s) throws  InvalidValueException {
        if (d!=1 && d!=2){
            throw new InvalidValueException(": Invalid difficulty.");
        } else
        if (d==1) {
            if (m>11 || m<9) throw new InvalidValueException(": Invalid mine amount.");

            if (t<120 || t>180) throw new InvalidValueException(": Invalid time.");

            if (s) throw new InvalidValueException(": Invalid uber-mine flag.");
        } else {
            if (m>45 || m<35) throw new InvalidValueException(": nIvalid mine amount.");

            if (t<240 || t>360) throw new InvalidValueException(": Invalid time.");

            if (!s) throw new InvalidValueException(": Invalid mine amount.");
    }
    }
}
