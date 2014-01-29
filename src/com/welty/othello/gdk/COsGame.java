package com.welty.othello.gdk;

import com.orbanova.common.misc.Require;
import com.welty.othello.c.CReader;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

/**
 * The in-memory data corresponding to a GGF-format game
 * <PRE>
 * User: Chris
 * Date: May 2, 2009
 * Time: 1:31:28 PM
 * </PRE>
 */
public class COsGame {
    public final COsPosition posStart;
    public COsPosition pos = new COsPosition();

    public String sPlace;
    protected String sDateTime;
    public OsPlayerInfo[] pis = new OsPlayerInfo[]{new OsPlayerInfo(), new OsPlayerInfo()};
    public final COsMoveList ml;
    COsMoveListItem[] mlisKomi = new COsMoveListItem[2];
    public OsMatchType mt = new OsMatchType();
    public COsResult result = new COsResult();
    private double dKomiValue = 0;

    public COsGame() {
        posStart = new COsPosition();
        ml = new COsMoveList();
    }

    public COsGame(COsGame b) {
        posStart = new COsPosition(b.posStart);
        pos = new COsPosition(b.pos);
        sPlace = b.sPlace;
        sDateTime = b.sDateTime;
        pis = new OsPlayerInfo[]{new OsPlayerInfo(pis[0]), new OsPlayerInfo(pis[1])};
        ml = new COsMoveList(b.ml);
        mlisKomi = Arrays.copyOf(b.mlisKomi, 2);
        mt = new OsMatchType(b.mt);
        result = new COsResult(b.result);
    }

    public COsGame(String text) {
        this(new CReader(text));
    }

    public COsGame(CReader in) {
        this();
        In(in);
    }

    // Information
    public COsResult Result() {
        return result;
    }

    public COsPosition GetPosStart() {
        return posStart;
    }

    public COsPosition GetPos() {
        return pos;
    }

    boolean NeedsKomi() {
        return mt.fKomi && ml.isEmpty();
    }

    private static boolean fCheckKomiValue = true;

    public void In(CReader in) {
        char c;
        String sToken, sData;

        Clear();

        // CGame header	fOK
        // note that we might have an initial '1' or '2' if this is 1 non-synchro game or 2 synchro games
        // but (in early versions of GGS) there is no initial number at all.
        in.ignoreWhitespace();
        c = in.read();
        if (c == '1' || c == '2') {
            in.ignoreWhitespace();
            c = in.read();
        }
        Require.eq(c, "c", '(');
        c = in.read();
        Require.eq(c, "c", ';');
        // CGame tokens
        while (true) {
            if (in.peek() == ';')
                break;
            sToken = in.readLine('[');
            sData = in.readLine(']');

            CReader is = new CReader(sData);

            if (sToken.equals("GM"))
                Require.eq(sData, "Game type", "Othello");
            else if (sToken.equals("PC"))
                sPlace = sData;
            else if (sToken.equals("DT"))
                sDateTime = sData;
            else if (sToken.equals("PB"))
                pis[1].sName = sData;
            else if (sToken.equals("PW"))
                pis[0].sName = sData;
            else if (sToken.equals("RB"))
                pis[1].dRating = is.readDoubleNoExponent();
            else if (sToken.equals("RW"))
                pis[0].dRating = is.readDoubleNoExponent();
            else if (sToken.equals("TI")) {
                posStart.cks[0].In(is);
                posStart.cks[1] = new OsClock(posStart.cks[0]);
            } else if (sToken.equals("TB"))
                posStart.cks[1].In(is);
            else if (sToken.equals("TW"))
                posStart.cks[0].In(is);
            else if (sToken.equals("TY"))
                mt.In(is);
            else if (sToken.equals("BO"))
                posStart.board.In(is);
            else if (sToken.equals("B") || sToken.equals("W")) {
                COsMoveListItem mli = new COsMoveListItem();
                mli.In(is);
                ml.add(mli);
            } else if (sToken.equals("RE"))
                result.In(is);
            else if (sToken.equals("CO")) {
                // ignore comments
            } else if (sToken.equals("KB")) {
                mlisKomi[1] = new COsMoveListItem(is);
            } else if (sToken.equals("KW")) {
                mlisKomi[0] = new COsMoveListItem(is);
            } else if (sToken.equals("KM")) {
                dKomiValue = in.readDoubleNoExponent();
                fCheckKomiValue = true;
            } else // unknown token
                throw new IllegalArgumentException("Unknown token: " + sToken);
        }
        if (fCheckKomiValue) {
            final double komiCheck;
            if (mlisKomi[0] == null && mlisKomi[1] == null) {
                komiCheck = 0;
            } else if (mlisKomi[0] != null && mlisKomi[1] != null) {
                komiCheck = mlisKomi[0].dEval + mlisKomi[1].dEval;
            } else {
                throw new IllegalArgumentException("Komi error: have komi values for only one player");
            }
            double dErr = 2 * dKomiValue - komiCheck;
            if (!(Math.abs(dErr) < 0.0001)) {
                throw new IllegalArgumentException("Komi error : " + dErr);
            }
        }

        c = in.read();
        Require.eq(c, "c", ';');
        c = in.read();
        Require.eq(c, "c", ')');

        // Get the current position
        CalcCurrentPos();
    }

    public CReader InLogbook(CReader is) {
        char c;

        Clear();
        mt.bt.n = 8;
        posStart.board.initialize(mt.bt);
        pis[0].sName = pis[1].sName = "logtest";
        pos = posStart;
        sPlace = "logbook";

        // get moves
        COsMoveListItem mli = new COsMoveListItem();
        mli.tElapsed = 0;
        mli.dEval = 0;
        while (0 != (c = is.read())) {
            if (c == '+' || c == '-') {

                // read move
                boolean fBlackMove = c == '+';
                Require.eq(fBlackMove, "black move", pos.board.blackMove());
                mli.mv.in(is);

                // update game and pass if needed
                Update(mli);
                if (!GameOver() && !pos.board.HasLegalMove()) {
                    mli.mv.fPass = true;
                    Update(mli);
                }
            } else {
                Require.eq(c, "c", ':');
                break;
            }
        }

        // get result
        final int nResult = is.readInt(0);
        Require.eq(nResult, "result", pos.board.netBlackSquares());
        result.Set(nResult);

        // game over flag
        final int n = is.readInt(10);
        Require.eq(n, "n", 10); // what is this?

        return is;
    }

    // 772942166 r idiot    64 ( 30   0   0) TravisS   0 ( 30   0   0) +34-33+43-35+24-42+52-64+23-13+41-32+53-14+25-31+51-61+15-16+63-74+62-73+65-75+66-56+76-57+67-86+46-68+47-38+26-37+58-48+36 +0
    public CReader InIOS(CReader is) throws EOFException {
        char c;
        long timestamp;
        int nBlack, nWhite;

        Clear();
        mt.bt.n = 8;
        posStart.board.initialize(mt.bt);
        sPlace = "IOS";

        if (0 != (timestamp = is.readLong())) {
            // time. For early games no timestamp was stored, it is 0.
            SetTime(timestamp);

            // game end type
            c = is.read();
            switch (c) {
                case 'e':
                    result.status = COsResult.TStatus.kNormalEnd;
                    break;
                case 'r':
                    result.status = COsResult.TStatus.kResigned;
                    break;
                case 't':
                    result.status = COsResult.TStatus.kTimeout;
                    break;
            }

            pis[1].sName = is.readString();
            nBlack = is.readInt();
            posStart.cks[1].InIOS(is);

            pis[0].sName = is.readString();
            nWhite = is.readInt();
            posStart.cks[0].InIOS(is);

            pos = posStart;

            // get moves
            COsMoveListItem mli = new COsMoveListItem();
            mli.tElapsed = 0;
            mli.dEval = 0;
            int iosmove;

            // read move code. move code 0 means game is over
            while (0 != (iosmove = is.readInt())) {
                // positive moves are black, negative are white
                Require.eq(pos.board.blackMove(), "black move", iosmove > 0);

                mli.mv.setIos(iosmove);
                Update(mli);

                // pass if needed
                if (!GameOver() && !pos.board.HasLegalMove()) {
                    mli.mv.fPass = true;
                    Update(mli);
                }
            }

            // calculate result. Might not be equal to the result
            //	on the board if one player resigned.
            result.dResult = nBlack - nWhite;
            if (!(result.dResult == pos.board.netBlackSquares() || result.status != COsResult.TStatus.kNormalEnd)) {
                throw new IllegalArgumentException("Don't understand game result");
            }
        }

        return is;
    }

    public void Out(StringBuilder sb) {
        sb.append("(;GM[Othello]");
        sb.append("PC[").append(sPlace);
        if (!sDateTime.isEmpty())
            sb.append("]DT[").append(sDateTime);
        sb.append("]PB[").append(pis[1].sName);
        sb.append("]PW[").append(pis[0].sName);
        sb.append("]RE[").append(result);

        if (pis[1].dRating != 0)
            sb.append("]RB[").append(pis[1].dRating);
        if (pis[0].dRating != 0)
            sb.append("]RW[").append(pis[0].dRating);
        if (posStart.cks[0].equals(posStart.cks[1])) {
            sb.append("]TI[").append(posStart.cks[1]);
        } else {
            sb.append("]TB[").append(posStart.cks[1]);
            sb.append("]TW[").append(posStart.cks[0]);
        }
        sb.append("]TY[").append(mt);
        sb.append("]BO[").append(posStart.board);

        // komi set moves
        if (mt.fKomi && !ml.isEmpty()) {
            sb.append("]KB[").append(mlisKomi[1]);
            sb.append("]KW[").append(mlisKomi[0]);
            sb.append("]KM[").append((mlisKomi[0].dEval + mlisKomi[1].dEval) / 2);
        }

        // move list
        boolean fBlackMove = posStart.board.blackMove();
        for (COsMoveListItem pmli : ml) {
            sb.append(fBlackMove ? "]B[" : "]W[").append(pmli);
            fBlackMove = !fBlackMove;
        }

        sb.append("];)");
    }

    public void Clear() {
        result.Clear();
        ml.clear();
        pis[0].Clear();
        pis[1].Clear();
        pos.Clear();
        posStart.Clear();
        sDateTime = "";
        sPlace = "";
        mt.Clear();
    }

    protected void Undo() {
        if (ml.size() >= 2) {
            ml.remove(ml.size() - 1);
            ml.remove(ml.size() - 1);
            CalcCurrentPos();
        }
    }

    public void Undo(int nUndo) {
        Require.leq(nUndo, "number of moves to undo", ml.size());
        for (int i = 0; i < nUndo; i++) {
            ml.remove(ml.size() - 1);
        }
        CalcCurrentPos();
    }


    void SetResult(final COsResult aresult, final OsPlayerInfo[] apis) {
        result = aresult;
        if (!pis[0].sName.equals(apis[0].sName))
            result.dResult = -result.dResult;
    }

    void SetResult(final COsResult aresult, final String[] sNames) {
        result = aresult;
        if (!pis[0].sName.equals(sNames[0]))
            result.dResult = -result.dResult;
    }

    public void SetResult(final COsResult aresult) {
        result = aresult;
    }

    public void Initialize(final String sBoardType) {
        Clear();
        COsBoardType bt = new COsBoardType(sBoardType);
        mt.Initialize(sBoardType);
        posStart.board.initialize(bt);
        CalcCurrentPos();
    }

// Set the start position to the position given by the board text, clear the move list, and
// set the current position to the start position.
//

    // Does not affect the board type.

    public void SetToPosition(final String sBoardText, final boolean fBlackMove) {
        posStart.board.setText(sBoardText, fBlackMove);
        pos = posStart;
        ml.clear();
    }

    public void SetDefaultStartPos() {
        Initialize("8");
        CalcCurrentPos();
    }

    /**
     * @param t C time, i.e. seconds (not millis) since 1970-01-01
     */
    public void SetTime(long t) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        sDateTime = dateFormat.format(t) + " GMT";
    }

    /**
     * Sets the time string to only the year
     *
     * @param year 4-digit year, e.g. 2006
     */
    public void SetTimeYear(int year) {
        sDateTime = Integer.toString(year);
    }

    void SetCurrentTime() {
        long tCurrent = System.currentTimeMillis() / 1000;
        SetTime(tCurrent);
    }

    public void CalcCurrentPos() {
        pos = calcPosition(ml);
    }

    COsPosition calcPosition(List<COsMoveListItem> moveList) {
        final COsPosition position = new COsPosition(posStart);
        if (mt.fKomi && !moveList.isEmpty())
            position.UpdateKomiSet(mlisKomi);

        for (COsMoveListItem mli : moveList) {
            position.Update(mli);
        }
        return position;
    }

    /**
     * @param iMove index of last move to make. If 0, return the start position.
     * @return position at move i. If iMove > # of moves in game, returns the last position in the game
     */
    public @NotNull COsPosition PosAtMove(int iMove) {
        if (iMove > ml.size()) {
            iMove = ml.size();
        }
        return calcPosition(ml.subList(0, iMove));
    }


    public void Update(COsMoveListItem mli) {
        mli = new COsMoveListItem(mli);
        pos.Update(mli);
        ml.add(mli);
        if (GameOver()) {
            // we don't adjust for timeouts because we don't keep track of
            //	who timed out first. This is a bug; but in games coming from GGS
            //	it should update us with the final result later.
            result.dResult = pos.board.Result(mt.fAnti);
            result.status = COsResult.TStatus.kNormalEnd;
        }
    }

    double max(double a, double b) {
        return (a > b) ? a : b;
    }

    void UpdateKomiSet(final COsMoveListItem[] mlis) {
        Require.isTrue(NeedsKomi(), "needs komi");
        pos.UpdateKomiSet(mlis);
        mlisKomi[0] = mlis[0];
        mlisKomi[1] = mlis[1];
    }

    public boolean GameOver() {
        return pos.board.GameOver();
    }

    boolean ToMove(String sLogin) {
        if (result.status != COsResult.TStatus.kUnfinished)
            return false;
        if (pos.board.GameOver())
            return false;
        if (NeedsKomi())
            return sLogin.equals(pis[0].sName) || sLogin.equals(pis[1].sName);
        else
            return sLogin.equals(pis[pos.board.blackMove() ? 1 : 0].sName);
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder();
        Out(sb);
        return sb.toString();
    }

    public void SetPlace(String s) {
        sPlace = s;
    }

    public COsMoveList GetMoveList() {
        return ml;
    }

    public void SetMoveList(String s) {
        ml.clear();
        final CReader in = new CReader(s);
        while (!in.wsEof()) {
            COsMove mv = new COsMove(in);
            ml.add(new COsMoveListItem(mv, 0, 0));
        }
        CalcCurrentPos();
    }

    /**
     * @return Komi value, if this is a komi game and komi has been set. 0 otherwise.
     */
    public double KomiValue() {
        return dKomiValue;
    }
}
