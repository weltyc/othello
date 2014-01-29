package com.welty.othello.gdk;

import com.orbanova.common.misc.Require;
import com.welty.othello.c.CReader;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 2, 2009
 * Time: 11:55:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class COsBoard {
    private COsBoardType bt = new COsBoardType("8");
    private char[] sBoard;
    public boolean fBlackMove;

    public static final char BLACK = '*';
    public static final char WHITE = 'O';
    public static final char EMPTY = '-';
    private static final char DUMMY = 'd';

    public COsBoard() {
    }

    public COsBoard(COsBoard board) {
        copy(board);
    }

    public COsBoard(CReader is) {
        In(is);
    }

    public COsBoard(COsBoardType bt, String sBoard, boolean fBlackMove) {
        this.bt = new COsBoardType(bt);
        this.sBoard = sBoard.toCharArray();
        this.fBlackMove = fBlackMove;
    }

    public void copy(COsBoard board) {
        bt = new COsBoardType(board.bt);
        sBoard = (board.sBoard == null) ? null : Arrays.copyOf(board.sBoard, board.sBoard.length);
        fBlackMove = board.fBlackMove;
    }

    public boolean IsBlackMove() {
        return fBlackMove;
    }

    int IsMoveLegal(int row, int col) {
        return IsMoveLegal(row, col, fBlackMove);
    }

    public boolean HasLegalMove() {
        return HasLegalMove(fBlackMove);
    }

    void initialize(final COsBoardType bt) {
        int row, col;
        char c;
        this.bt = bt;

        sBoard = new char[this.bt.NTotalSquares()];
        for (row = -1; row <= this.bt.n; row++) {
            for (col = -1; col <= this.bt.n; col++) {
                if (row == -1 || col == -1 || row == this.bt.n || col == this.bt.n)
                    c = DUMMY;
                else if (this.bt.DummyCorner(row, col))
                    c = DUMMY;
                else
                    c = EMPTY;
                SetPiece(row, col, c);
            }
        }

        int center = this.bt.n / 2;
        SetPiece(center, center - 1, BLACK);
        SetPiece(center - 1, center, BLACK);
        SetPiece(center, center, WHITE);
        SetPiece(center - 1, center - 1, WHITE);

        fBlackMove = true;
    }

    public char Piece(int row, int col) {
        // need to adjust for the first row and column of dummy squares
        row++;
        col++;
        return sBoard[row * (bt.n + 2) + col];
    }

    public void SetPiece(int row, int col, char piece) {
        // need to adjust for the first row and column of dummy squares
        row++;
        col++;
        sBoard[row * (bt.n + 2) + col] = piece;
    }

    void Update(final COsMove mv) {
        int dRow, dCol;
        char cMover, cOpponent;

        if (!mv.Pass()) {

            Require.inRange("Row must be in range of board size", mv.Row(), "move row", 0, bt.n - 1);
            Require.inRange("Col must be in range of board size", mv.Col(), "move col", 0, bt.n - 1);

            if (Piece(mv.Row(), mv.Col()) != EMPTY) {
                System.err.println(this);
                throw new IllegalArgumentException("tried to move to a filled square at " + mv);
            } else {
                // get colors
                if (fBlackMove) {
                    cMover = BLACK;
                    cOpponent = WHITE;
                } else {
                    cMover = WHITE;
                    cOpponent = BLACK;
                }

                // update board
                int nFlipped = 0;
                SetPiece(mv.Row(), mv.Col(), cMover);
                for (dRow = -1; dRow <= 1; dRow++) {
                    for (dCol = -1; dCol <= 1; dCol++) {
                        if ((dRow != 0) || (dCol != 0))
                            nFlipped += UpdateDirection(mv.Row(), mv.Col(), dRow, dCol, cMover, cOpponent);
                    }
                }
                Require.gt(nFlipped, "nFlipped", 0);
            }
        }

        fBlackMove = !fBlackMove;
    }

    int NPass() {
        if (HasLegalMove(fBlackMove))
            return 0;
        else if (HasLegalMove(!fBlackMove))
            return 1;
        else
            return 2;
    }

    public boolean GameOver() {
        return !HasLegalMove(fBlackMove) && !HasLegalMove(!fBlackMove);
    }

    boolean HasLegalMove(boolean fBlackMover) {
        int r, c;

        for (r = 0; r < bt.n; r++) {
            for (c = 0; c < bt.n; c++)
                if (IsMoveLegal(r, c, fBlackMover) != 0)
                    return true;
        }
        return false;
    }

    ArrayList<COsMove> GetMoves(boolean fMover) {
        int r, c;
        ArrayList<COsMove> mvs = new ArrayList<COsMove>();

        for (r = 0; r < bt.n; r++) {
            for (c = 0; c < bt.n; c++)
                if (IsMoveLegal(r, c, fMover ? fBlackMove : !fBlackMove) != 0)
                    mvs.add(new COsMove(r, c));
        }

        return mvs;
    }

    /**
     * Port note: originally returned number of squares flipped if move was not a pass
     *
     * @param move
     * @return
     */
    public boolean IsMoveLegal(final COsMove move) {
        if (move.Pass())
            return !HasLegalMove();
        else
            return IsMoveLegal(move.Row(), move.Col()) != 0;
    }

    /**
     * @param row
     * @param col
     * @param fBlackMover
     * @return number of squares flipped, or 0 if no squares are flipped
     */
    int IsMoveLegal(int row, int col, boolean fBlackMover) {
        int dRow, dCol;
        char cMover, cOpponent;

        if (Piece(row, col) != EMPTY)
            return 0;

        // get colors
        if (fBlackMover) {
            cMover = BLACK;
            cOpponent = WHITE;
        } else {
            cMover = WHITE;
            cOpponent = BLACK;
        }

        // update board
        int nFlipped = 0;
        for (dRow = -1; dRow <= 1; dRow++) {
            for (dCol = -1; dCol <= 1; dCol++) {
                if ((dRow != 0) || (dCol != 0))
                    nFlipped += IsMoveLegalDirection(row, col, dRow, dCol, cMover, cOpponent);
            }
        }
        return nFlipped;
    }

    /**
     * Read in a board from the reader
     *
     * @throws IllegalArgumentException if the reader does not contain a valid board
     */
    public void In(CReader is) {
        char c;
        int i, nsq;

        Clear();

        initialize(new COsBoardType(is));
        nsq = sBoard.length;
        for (i = 0; i < nsq; i++) {
            // find the next non-dummy square
            if (sBoard[i] != 'd') {
                // put something there
                is.ignoreWhitespace();
                c = is.read();
                switch(c) {
                    case 'x':
                    case 'X':
                        c = BLACK;
                        break;
                    case '.':
                    case ' ':
                    case '_':
                        c = EMPTY;
                        break;
                    case 'o':
                    case '0':
                        c = WHITE;
                        break;
                }
                if (!(c == BLACK || c == WHITE || c == EMPTY)) {
                    throw (new IllegalArgumentException("illegal character " + c));
                }
                sBoard[i] = c;
            }
        }

        is.ignoreWhitespace();
        c = is.read();
        fBlackMove = (c == BLACK);
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(bt).append(" ");
        for (char c : sBoard) {
            if (c != DUMMY)
                sb.append(c);
        }
        sb.append(" ").append(CMover());
        return sb.toString();
    }

    void Clear() {
        bt.Clear();
        fBlackMove = true;
        sBoard = null;
    }

    public void OutFormatted(PrintStream os) {
        OutHeader(os);

        int r, c;
        for (r = 0; r < bt.n; r++) {
            os.format("%2d", r + 1);
            for (c = 0; c < bt.n; c++) {
                os.print(" " + Piece(r, c));
            }
            os.format("%2d\n", r + 1);
        }

        OutHeader(os);
        os.println(moverText() + " to move");
    }

    private String moverText() {
        return fBlackMove ? "Black" : "White";
    }

    void OutHeader(PrintStream os) {
        int c;

        os.print("  ");
        for (c = 0; c < bt.n; c++)
            os.print(" " + (char) (c + 'A'));
        os.println();
    }

    int UpdateDirection(int row, int col, int dRow, int dCol, char cMover, char cOpponent) {
        int rowEnd, colEnd;
        int nFlipped;
        nFlipped = 0;

        for (rowEnd = row + dRow, colEnd = col + dCol; Piece(rowEnd, colEnd) == cOpponent; rowEnd += dRow, colEnd += dCol)
            ;
        if (Piece(rowEnd, colEnd) == cMover) {
            for (rowEnd = row + dRow, colEnd = col + dCol; Piece(rowEnd, colEnd) == cOpponent; rowEnd += dRow, colEnd += dCol) {
                SetPiece(rowEnd, colEnd, cMover);
                nFlipped++;
            }
        }

        return nFlipped;
    }

    int IsMoveLegalDirection(int row, int col, int dRow, int dCol, char cMover, char cOpponent) {
        int rowEnd, colEnd;
        int nFlipped;
        nFlipped = 0;

        for (rowEnd = row + dRow, colEnd = col + dCol; Piece(rowEnd, colEnd) == cOpponent; rowEnd += dRow, colEnd += dCol)
            ;
        if (Piece(rowEnd, colEnd) == cMover) {
            for (rowEnd = row + dRow, colEnd = col + dCol; Piece(rowEnd, colEnd) == cOpponent; rowEnd += dRow, colEnd += dCol) {
                nFlipped++;
            }
        }

        return nFlipped;
    }

    /**
     * Get the text for the board and a flag for black-to-move
     *
     * @return Port note: original C code put the text into sBoard, and returned sBoard and output a boolean fBlackMove; it also
     *         had a trailingNull flag if you wanted a \0 at the end of the text
     */
    public GetTextResult GetText() {
        StringBuilder sb = new StringBuilder();

        for (int r = 0; r <= bt.n; r++) {
            for (int c = 0; c <= bt.n; c++) {
                if (Piece(r, c) != DUMMY) {
                    sb.append(Piece(r, c));
                }
            }
        }

        final String sBoard = sb.toString();
        Require.eq(sBoard.length(), "squares used", bt.NPlayableSquares());
        return new GetTextResult(sBoard, fBlackMove);
    }

    public boolean blackMove() {
        return fBlackMove;
    }

    public int NEmpty() {
        return getPieceCounts().nEmpty;
    }

    public void Set(COsBoard board) {
        bt = new COsBoardType(board.bt);
        setText(board.GetText());
    }

    public static class GetTextResult {
        final String text;
        final boolean blackMove;

        public GetTextResult(String text, boolean blackMove) {
            this.text = text;
            this.blackMove = blackMove;
        }

        public String getText() {
            return text;
        }

        public boolean isBlackMove() {
            return blackMove;
        }
    }

    /**
     * @return a string containing one char for each square on the board
     *         e.g. for the initial position, returns "---------------------------O*------*O---------------------------"
     */
    String GetTextString() {
        return GetText().text;
    }

    private void setText(GetTextResult sBoard) {
        setText(sBoard.text, sBoard.blackMove);
    }


    public void setText(String sBoardText, boolean fBlackMove) {
        setText(sBoardText);
        this.fBlackMove = fBlackMove;
    }

    void setText(final String sBoard) {
        int r, c;

        int i = 0;
        for (r = 0; r <= bt.n; r++) {
            for (c = 0; c <= bt.n; c++) {
                if (Piece(r, c) != DUMMY) {
                    SetPiece(r, c, sBoard.charAt(i++));
                }
            }
        }
    }

    public char CMover() {
        return fBlackMove ? BLACK : WHITE;
    }

    /**
     * Port note: originally had arguments int&nBlack, int&nWhite, int&nEmpty
     */
    public PieceCounts getPieceCounts() {
        int nBlack = 0;
        int nWhite = 0;
        int nEmpty = 0;
        int i;
        for (i = 0; i < bt.NTotalSquares(); i++) {
            switch (sBoard[i]) {
                case BLACK:
                    nBlack++;
                    break;
                case WHITE:
                    nWhite++;
                    break;
                case EMPTY:
                    nEmpty++;
                    break;
                case DUMMY:
                    break;
                default:
                    throw new IllegalStateException("unknown board piece char : " + sBoard[i]);
            }
        }

        return new PieceCounts(nBlack, nWhite, nEmpty);
    }

    public int netBlackSquares() {
        return getPieceCounts().netBlackSquares();
    }

    public int Result() {
        return Result(false);
    }

    public int Result(boolean fAnti) {
        return getPieceCounts().result(fAnti);
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof COsBoard) {
            COsBoard b = (COsBoard) obj;
            return bt.equals(b.bt) && Arrays.equals(sBoard, b.sBoard) && fBlackMove == b.fBlackMove;
        } else {
            return false;
        }
    }
}