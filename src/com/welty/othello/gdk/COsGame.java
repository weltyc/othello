package com.welty.othello.gdk;

import com.orbanova.common.misc.Require;
import com.welty.othello.c.CReader;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

/**
 * The in-memory data corresponding to a GGF-format game
 */
@EqualsAndHashCode
public class COsGame {
    public final COsPosition posStart;
    public COsPosition pos = new COsPosition();

    public String sPlace;
    protected String sDateTime;
    private OsPlayerInfo[] pis = new OsPlayerInfo[]{new OsPlayerInfo(), new OsPlayerInfo()};
    private final COsMoveList ml;
    OsMoveListItem[] mlisKomi = new OsMoveListItem[2];
    private OsMatchType mt = new OsMatchType();
    public OsResult result = OsResult.INCOMPLETE;
    private double dKomiValue = 0;

    public COsGame() {
        posStart = new COsPosition();
        ml = new COsMoveList();
    }

    public COsGame(COsGame b) {
        this(b, b.ml.size());
    }

    /**
     * Construct a game from text.
     * <p/>
     * The text is in GGF format. The text may also include an initial "1 " or "2 " which is ignored - this is used
     * in some GGF files to denote the number of games in a match.
     *
     * @param text GGF format text.
     */
    public COsGame(String text) {
        this(new CReader(text));
    }

    public COsGame(CReader in) {
        this();
        In(in);
    }

    /**
     * Create a copy of this game, truncated to the given move number
     *
     * @param game       source game
     * @param moveNumber number of moves to retain (0..ml.size()).
     */
    public COsGame(COsGame game, int moveNumber) {
        Require.leq(moveNumber, "move number", game.nMoves());
        Require.geq(moveNumber, "move number", 0);
        posStart = new COsPosition(game.posStart);
        sPlace = game.sPlace;
        sDateTime = game.sDateTime;
        pis = new OsPlayerInfo[]{new OsPlayerInfo(game.pis[0]), new OsPlayerInfo(game.pis[1])};
        ml = new COsMoveList(game.ml, moveNumber);
        if (moveNumber > 0) {
            mlisKomi = Arrays.copyOf(game.mlisKomi, 2);
        }
        CalcCurrentPos();

        mt = new OsMatchType(game.mt);

        if (moveNumber == game.ml.size()) {
            result = game.result;
        } else {
            result = OsResult.INCOMPLETE;
        }
    }

    public static @NotNull COsGame ofLogbook(String s) {
        final COsGame osGame = new COsGame();
        osGame.inLogbook(new CReader(s));
        return osGame;
    }

    // Information
    public OsResult Result() {
        return result;
    }

    public COsPosition getStartPosition() {
        return posStart;
    }

    public COsPosition getPos() {
        return pos;
    }

    boolean NeedsKomi() {
        return mt.isKomi() && ml.isEmpty();
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

            switch (sToken) {
                case "GM":
                    Require.eq(sData, "Game type", "Othello");
                    break;
                case "PC":
                    sPlace = sData;
                    break;
                case "DT":
                    sDateTime = sData;
                    break;
                case "PB":
                    pis[1].name = sData;
                    break;
                case "PW":
                    pis[0].name = sData;
                    break;
                case "RB":
                    pis[1].rating = is.readDoubleNoExponent();
                    break;
                case "RW":
                    pis[0].rating = is.readDoubleNoExponent();
                    break;
                case "TI":
                    final OsClock clock = new OsClock(is);
                    posStart.setBlackClock(clock);
                    posStart.setWhiteClock(clock);
                    break;
                case "TB":
                    posStart.setBlackClock(new OsClock(is));
                    break;
                case "TW":
                    posStart.setWhiteClock(new OsClock(is));
                    break;
                case "TY":
                    mt.In(is);
                    break;
                case "BO":
                    posStart.board.in(is);
                    break;
                case "B":
                case "W":
                    final OsMoveListItem mli = new OsMoveListItem(is);
                    ml.add(mli);
                    break;
                case "RE":
                    result = OsResult.of(is);
                    break;
                case "CO":
                    // ignore comments
                    break;
                case "KB":
                    mlisKomi[1] = new OsMoveListItem(is);
                    break;
                case "KW":
                    mlisKomi[0] = new OsMoveListItem(is);
                    break;
                case "KM":
                    dKomiValue = in.readDoubleNoExponent();
                    fCheckKomiValue = true;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown token: " + sToken);
            }
        }
        if (fCheckKomiValue) {
            final double komiCheck;
            if (mlisKomi[0] == null && mlisKomi[1] == null) {
                komiCheck = 0;
            } else if (mlisKomi[0] != null && mlisKomi[1] != null) {
                komiCheck = mlisKomi[0].getEval() + mlisKomi[1].getEval();
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

    /**
     * Read in the game in Logistello's book format
     *
     * @param is reader containing at least one game in Logistello's book format
     * @return the reader
     */
    private CReader inLogbook(@NotNull CReader is) {
        final OsMoveListItem pass = new OsMoveListItem(OsMove.PASS);
        char c;

        Clear();
        mt.bt.n = 8;
        posStart.board.initialize(mt.bt);
        pis[0].name = pis[1].name = "logtest";
        pos = new COsPosition(posStart);
        sPlace = "logbook";

        // get moves
        while (0 != (c = is.read())) {
            if (c == '+' || c == '-') {

                // read move
                boolean fBlackMove = c == '+';
                Require.eq(fBlackMove, "black move", pos.board.isBlackMove());
                OsMoveListItem mli = new OsMoveListItem(new OsMove(is));

                // update game and pass if needed
                append(mli);
                if (!isOver() && !pos.board.hasLegalMove()) {
                    append(pass);
                }
            } else {
                if (c != ':') {
                    throw new IllegalArgumentException("Unable to read logbook. Was expecting ':' but had '" + c + "' to end the game. Game so far: " + this);
                }
                break;
            }
        }

        // get result
        final int nResult = is.readInt(0);
        Require.eq(nResult, "result", pos.board.netBlackSquares());
        result = new OsResult(nResult);

        // game over flag
        final int n = is.readInt(10);
        Require.eq(n, "n", 10); // what is this?

        return is;
    }

    /**
     * Read in a game in IOS format
     * <p/>
     * Example IOS game:
     * 772942166 r idiot    64 ( 30   0   0) TravisS   0 ( 30   0   0) +34-33+43-35+24-42+52-64+23-13+41-32+53-14+25-31+51-61+15-16+63-74+62-73+65-75+66-56+76-57+67-86+46-68+47-38+26-37+58-48+36 +0
     *
     * @param is reader containing the game
     * @return the reader
     * @throws EOFException if EOF occurs while reading in the game
     */
    public CReader inIos(CReader is) throws EOFException {
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
            final char cResultType = is.read();
            pis[1].name = is.readString();
            nBlack = is.readInt();
            posStart.setBlackClock(OsClock.InIOS(is));

            pis[0].name = is.readString();
            nWhite = is.readInt();
            posStart.setWhiteClock(OsClock.InIOS(is));

            pos = posStart;

            // get moves
            int iosMove;

            // read move code. move code 0 means game is over
            while (0 != (iosMove = is.readInt())) {
                // positive moves are black, negative are white
                Require.eq(pos.board.isBlackMove(), "black move", iosMove > 0);

                final OsMoveListItem mli = new OsMoveListItem(OsMove.ofIos(iosMove));
                append(mli);

                // pass if needed
                if (!isOver() && !pos.board.hasLegalMove()) {
                    append(OsMoveListItem.PASS);
                }
            }

            // calculate result. Might not be equal to the result
            //	on the board if one player resigned.

            result = new OsResult(statusFromChar(cResultType), nBlack - nWhite);

            if (!(result.score == pos.board.netBlackSquares() || result.status != OsResult.TStatus.kNormalEnd)) {
                throw new IllegalArgumentException("Don't understand game result");
            }
        }

        return is;
    }

    private OsResult.TStatus statusFromChar(char cResultType) {
        switch (cResultType) {
            case 'e':
                return OsResult.TStatus.kNormalEnd;
            case 'r':
                return OsResult.TStatus.kResigned;
            case 't':
                return OsResult.TStatus.kTimeout;
            default:
                throw new IllegalArgumentException("Unknown IOS game result status: " + cResultType);
        }
    }

    /**
     * Append this game's GGF format text to the StringBuilder
     *
     * @param sb destination
     */
    public void out(StringBuilder sb) {
        sb.append("(;GM[Othello]");
        sb.append("PC[").append(sPlace);
        if (!sDateTime.isEmpty())
            sb.append("]DT[").append(sDateTime);
        sb.append("]PB[").append(pis[1].name);
        sb.append("]PW[").append(pis[0].name);
        sb.append("]RE[").append(result);

        if (pis[1].rating != 0)
            sb.append("]RB[").append(pis[1].rating);
        if (pis[0].rating != 0)
            sb.append("]RW[").append(pis[0].rating);
        if (posStart.getBlackClock().equals(posStart.getWhiteClock())) {
            sb.append("]TI[").append(posStart.getBlackClock());
        } else {
            sb.append("]TB[").append(posStart.getBlackClock());
            sb.append("]TW[").append(posStart.getWhiteClock());
        }
        sb.append("]TY[").append(mt);
        sb.append("]BO[").append(posStart.board);

        // komi set moves
        if (mt.isKomi() && !ml.isEmpty()) {
            sb.append("]KB[").append(mlisKomi[1]);
            sb.append("]KW[").append(mlisKomi[0]);
            sb.append("]KM[").append((mlisKomi[0].getEval() + mlisKomi[1].getEval()) / 2);
        }

        // move list
        boolean fBlackMove = posStart.board.isBlackMove();
        for (OsMoveListItem mli : ml) {
            sb.append(fBlackMove ? "]B[" : "]W[").append(mli);
            fBlackMove = !fBlackMove;
        }

        sb.append("];)");
    }

    public void Clear() {
        result = OsResult.INCOMPLETE;
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


    public void SetResult(final OsResult result) {
        this.result = result;
    }

    public void Initialize(final String sBoardType, final OsClock blackClock, final OsClock whiteClock) {
        Clear();
        COsBoardType bt = new COsBoardType(sBoardType);
        mt.Initialize(sBoardType);
        posStart.board.initialize(bt);
        posStart.setBlackClock(blackClock);
        posStart.setWhiteClock(whiteClock);
        CalcCurrentPos();
    }

// Set the start position to the position given by the board text, clear the move list, and
// set the current position to the start position.
//

    // Does not affect the board type.

    /**
     * @param sBoardText board text. No spaces, and all chars must be BLACK, WHITE, or EMPTY (*, O, -).
     * @param fBlackMove if true, it's black's move
     */
    public void SetToPosition(final String sBoardText, final boolean fBlackMove) {
        posStart.board.setText(sBoardText, fBlackMove);
        pos = new COsPosition(posStart);
        ml.clear();
    }

    public void setToDefaultStartPosition(OsClock blackClock, OsClock whiteClock) {
        Initialize("8", blackClock, whiteClock);
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

    public void CalcCurrentPos() {
        pos = calcPosition(ml);
    }

    COsPosition calcPosition(List<OsMoveListItem> moveList) {
        final COsPosition position = new COsPosition(posStart);
        if (mt.isKomi() && !moveList.isEmpty())
            position.UpdateKomiSet(mlisKomi);

        for (OsMoveListItem mli : moveList) {
            position.append(mli);
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


    /**
     * Append a move to the end of the game.
     * <p/>
     * Updates the current position and, if the move ends the game, sets the result and status
     *
     * @param mli move
     */
    public void append(OsMoveListItem mli) {
        pos.append(mli);
        ml.add(mli);
        if (isOver()) {
            // we don't adjust for timeouts because we don't keep track of
            //	who timed out first. This is a bug; but in games coming from GGS
            //	it should update us with the final result later.
            result = new OsResult(pos.board.getResult(mt.anti));
        }
    }

    /**
     * @return true if there are no legal moves for either player
     */
    public boolean isOver() {
        return pos.board.isGameOver();
    }

    /**
     * @return This game, in GGF format.
     */
    @Override public String toString() {
        final StringBuilder sb = new StringBuilder();
        out(sb);
        return sb.toString();
    }

    public void SetPlace(String s) {
        sPlace = s;
    }

    /**
     * Set the game's current moves
     *
     * @param s string containing a move list, for example "F5 d6 C3 d3"
     */
    public void SetMoveList(String s) {
        ml.clear();
        final CReader in = new CReader(s);
        while (!in.wsEof()) {
            OsMove mv = new OsMove(in);
            ml.add(new OsMoveListItem(mv));
        }
        CalcCurrentPos();
    }

    /**
     * @return Komi value, if this is a komi game and komi has been set. 0 otherwise.
     */
    public double KomiValue() {
        return dKomiValue;
    }

    /**
     * @return a copy of the move list
     */
    public COsMoveList getMoveList() {
        return new COsMoveList(ml, ml.size());
    }

    /**
     * @return number of moves in the move list
     */
    public int nMoves() {
        return ml.size();
    }

    /**
     * @param iMove move index (starts at 0).
     * @return information about that move
     */
    public OsMoveListItem getMli(int iMove) {
        return ml.get(iMove);
    }

    /**
     * Reflect the start position and all moves.
     *
     * @param iReflection reflection index, 0..7
     */
    public void reflect(int iReflection) {
        // reflect start pos
        COsBoard newStart = new COsBoard(posStart.board);
        final int n = pos.board.width();
        for (int col = 0; col < n; col++) {
            for (int row = 0; row < n; row++) {
                final OsMove spot = new OsMove(row, col).reflect(iReflection);
                newStart.setPiece(spot.row(), spot.col(), posStart.board.getPiece(row, col));
            }
        }
        posStart.board.copy(newStart);

        for (int i = 0; i < nMoves(); i++) {
            OsMoveListItem mli = getMli(i);
            ml.set(i, mli.reflect(iReflection));
        }

        pos = PosAtMove(10000);
    }

    /**
     * Remove evals and times from all moves in the move list
     */
    public void stripEvalsAndTimes() {
        ml.stripEvalsAndTimes();
        CalcCurrentPos();
    }

    /**
     * Get information about the black player
     *
     * @return black OsPlayerInfo
     */
    public @NotNull OsPlayerInfo getBlackPlayer() {
        return pis[1];
    }

    /**
     * Get information about the white player
     *
     * @return white OsPlayerInfo
     */
    public @NotNull OsPlayerInfo getWhitePlayer() {
        return pis[0];
    }

    /**
     * @return true if this game is being played on an 8x8 board.
     */
    public boolean is8x8() {
        return getStartPosition().board.getBoardType().equals(COsBoardType.BT_8x8);
    }

    public OsMatchType getMatchType() {
        return mt;
    }

    /**
     * Get information about a player.
     *
     * @param isBlack if true, get information about the black player; if false, get information about the white player.
     * @return the player info.
     */
    public OsPlayerInfo getPlayer(boolean isBlack) {
        return pis[isBlack ? 1 : 0];
    }
}
