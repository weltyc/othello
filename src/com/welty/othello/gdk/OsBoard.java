package com.welty.othello.gdk;

import com.orbanova.common.misc.Require;
import com.welty.othello.c.CReader;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A board knows its size, the disks on the board, and the player to move
 */
public class OsBoard {
    private COsBoardType bt = new COsBoardType("8");

    /**
     * The board is stored as an (n+2)x(n+2) array of squares, with dummy squares
     * on the outside edge.
     * <p/>
     * It may also be null.
     */
    private char[] sBoard;

    public boolean fBlackMove;

    public static final char BLACK = '*';
    public static final char WHITE = 'O';
    public static final char EMPTY = '-';
    private static final char DUMMY = 'd';

    public OsBoard() {
    }

    public OsBoard(OsBoard board) {
        copy(board);
    }

    public OsBoard(CReader is) {
        in(is);
    }

    int isMoveLegal(int row, int col) {
        return nFlipped(row, col, fBlackMove);
    }

    public boolean hasLegalMove() {
        return hasLegalMove(fBlackMove);
    }

    /**
     * Set to the default start position for the given board type
     *
     * @param bt board type
     */
    public void initialize(final COsBoardType bt) {
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
                setPiece(row, col, c);
            }
        }

        int center = this.bt.n / 2;
        setPiece(center, center - 1, BLACK);
        setPiece(center - 1, center, BLACK);
        setPiece(center, center, WHITE);
        setPiece(center - 1, center - 1, WHITE);

        fBlackMove = true;
        validate();
    }

    public char getPiece(int row, int col) {
        // need to adjust for the first row and column of dummy squares
        row++;
        col++;
        final int index = row * (bt.n + 2) + col;
        return sBoard[index];
    }

    public void setPiece(int row, int col, char piece) {
        switch (piece) {
            case BLACK:
            case WHITE:
            case EMPTY:
            case DUMMY:
                // need to adjust for the first row and column of dummy squares
                row++;
                col++;
                sBoard[row * (bt.n + 2) + col] = piece;
                break;
            default:
                throw new IllegalStateException("Illegal piece: " + piece);
        }
    }

    /**
     * Make a move on this board, updating disks and player-to-move.
     *
     * @param move move to make
     */
    void update(final OsMove move) {
        int dRow, dCol;
        char cMover, cOpponent;

        if (!move.isPass()) {

            Require.inRange("Row must be in range of board size", move.row(), "move row", 0, bt.n - 1);
            Require.inRange("Col must be in range of board size", move.col(), "move col", 0, bt.n - 1);

            if (getPiece(move.row(), move.col()) != EMPTY) {
                System.err.println(this);
                throw new IllegalArgumentException("tried to move to a filled square at " + move);
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
                setPiece(move.row(), move.col(), cMover);
                for (dRow = -1; dRow <= 1; dRow++) {
                    for (dCol = -1; dCol <= 1; dCol++) {
                        if ((dRow != 0) || (dCol != 0))
                            nFlipped += updateDirection(move.row(), move.col(), dRow, dCol, cMover, cOpponent);
                    }
                }
                Require.gt(nFlipped, "nFlipped", 0);
            }
        } else {
            // is a pass legal?
            final int nPass = nPass();
            if (nPass == 0) {
                throw new IllegalArgumentException("can't pass, still have a legal move");
            }
            if (nPass == 2) {
                throw new IllegalArgumentException("can't pass, game is over");
            }
        }

        fBlackMove = !fBlackMove;
    }

    /**
     * @return 0 if mover has a legal move, 1 if mover passes but opponent has a legal move, 2 if neither player has
     *         a legal move and the game is therefore over.
     */
    int nPass() {
        if (hasLegalMove(fBlackMove))
            return 0;
        else if (hasLegalMove(!fBlackMove))
            return 1;
        else
            return 2;
    }

    /**
     * @return true if there are no legal moves for either player
     */
    public boolean isGameOver() {
        return !hasLegalMove(fBlackMove) && !hasLegalMove(!fBlackMove);
    }

    /**
     * Determine whether a player has a legal move given the disks on the board.
     * <p/>
     * The player does not need to be the player-to-move.
     *
     * @param fBlackMover if true, consider black's moves
     * @return true if the player has a legal move given the disks on the board
     */
    boolean hasLegalMove(boolean fBlackMover) {
        int r, c;

        for (r = 0; r < bt.n; r++) {
            for (c = 0; c < bt.n; c++)
                if (nFlipped(r, c, fBlackMover) != 0)
                    return true;
        }
        return false;
    }

    /**
     * Get all legal moves for a player
     *
     * @param fMover if true, legal moves for the player to move. If false, legal moves for the opponent.
     * @return list of legal moves
     */
    ArrayList<OsMove> getMoves(boolean fMover) {
        int r, c;
        ArrayList<OsMove> mvs = new ArrayList<>();

        for (r = 0; r < bt.n; r++) {
            for (c = 0; c < bt.n; c++)
                if (nFlipped(r, c, fMover ? fBlackMove : !fBlackMove) != 0)
                    mvs.add(new OsMove(r, c));
        }

        return mvs;
    }

    /**
     * Determine whether a move is legal.
     * <p/>
     * A pass is considered legal if the player has no legal moves, but the opponent does have a legal move.
     * (So a pass is not legal after the end of the game).
     *
     * @param move square to check
     * @return true if move is legal.
     */
    public boolean isMoveLegal(final OsMove move) {
        if (move.isPass()) {
            return !hasLegalMove() && hasLegalMove(!isBlackMove());
        } else {
            return isMoveLegal(move.row(), move.col()) != 0;
        }
    }

    /**
     * Determine number of squares flipped by a move
     *
     * @param row         row of move (starts at 0)
     * @param col         col of move (starts at 0)
     * @param fBlackMover if true, black is making a move
     * @return number of squares flipped, or 0 if no squares are flipped
     */
    int nFlipped(int row, int col, boolean fBlackMover) {
        int dRow, dCol;
        char cMover, cOpponent;

        if (getPiece(row, col) != EMPTY)
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
                    nFlipped += nFlippedInDirection(row, col, dRow, dCol, cMover, cOpponent);
            }
        }
        return nFlipped;
    }

    /**
     * Read in a board from the reader
     *
     * @throws IllegalArgumentException if the reader does not contain a valid board
     */
    public void in(CReader is) {
        clear();

        initialize(new COsBoardType(is));
        for (int i = 0; i < sBoard.length; i++) {
            // find the next non-dummy square
            if (sBoard[i] != 'd') {
                // put something there
                final char c = readNormalized(is);
                sBoard[i] = c;
            }
        }

        final char c = readNormalized(is);
        fBlackMove = (c == BLACK);
        is.ignoreWhitespace();
        if (is.read() != 65535) {
            throw new IllegalArgumentException("Board text has too many characters");
        }
        validate();
    }

    /**
     * Strip whitespace, then read a character and convert it to normalized form (BLACK, WHITE, or EMPTY).
     *
     * @param is input stream
     * @return normalized character (BLACK, WHITE, or EMPTY).
     * @throws IllegalArgumentException if the character can't be converted to normalized form or the reader is at
     *                                  end-of-stream.
     */
    private static char readNormalized(CReader is) {
        is.ignoreWhitespace();
        final char c = is.read();
        return normalizeChar(c);
    }

    /**
     * Convert an input character into BLACK, EMPTY, or WHITE as appropriate.
     * <p/>
     * BLACK (*): *xX
     * WHITE (O): Oo0
     * EMPTY (-): -. _
     *
     * @param c char to convert
     * @return converted char
     * @throws IllegalArgumentException if the input character is not one of the convertible characters.
     */
    private static char normalizeChar(char c) {
        switch (c) {
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
            throw (new IllegalArgumentException("illegal character: '" + c + "'"));
        }
        return c;
    }

    private void validate() {
        if (sBoard != null) {
            final int expectedLength = (bt.n + 2) * (bt.n + 2);
            if (expectedLength != sBoard.length) {
                throw new IllegalStateException("Expected board length " + expectedLength + ", was " + sBoard.length);
            }
        }
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(bt).append(" ");
        for (char c : sBoard) {
            if (c != DUMMY)
                sb.append(c);
        }
        sb.append(" ").append(getMoverChar());
        return sb.toString();
    }

    /**
     * Set this board into an invalid state
     */
    void clear() {
        bt.Clear();
        fBlackMove = true;
        sBoard = null;
    }

    /**
     * Write this board to a PrintStream with headers
     *
     * @param os destination
     */
    public void outFormatted(PrintStream os) {
        outHeader(os);

        int r, c;
        for (r = 0; r < bt.n; r++) {
            os.format("%2d", r + 1);
            for (c = 0; c < bt.n; c++) {
                os.print(" " + getPiece(r, c));
            }
            os.format("%2d\n", r + 1);
        }

        outHeader(os);
        os.println(moverText() + " to move");
    }

    private String moverText() {
        return fBlackMove ? "Black" : "White";
    }

    private void outHeader(PrintStream os) {
        int c;

        os.print("  ");
        for (c = 0; c < bt.n; c++)
            os.print(" " + (char) (c + 'A'));
        os.println();
    }

    /**
     * Update the board in a given direction
     *
     * @return number of disks flipped
     */
    private int updateDirection(int row, int col, int dRow, int dCol, char cMover, char cOpponent) {
        int rowEnd, colEnd;
        int nFlipped;
        nFlipped = 0;

        for (rowEnd = row + dRow, colEnd = col + dCol; getPiece(rowEnd, colEnd) == cOpponent; ) {
            rowEnd += dRow;
            colEnd += dCol;
        }

        if (getPiece(rowEnd, colEnd) == cMover) {
            for (rowEnd = row + dRow, colEnd = col + dCol; getPiece(rowEnd, colEnd) == cOpponent; rowEnd += dRow, colEnd += dCol) {
                setPiece(rowEnd, colEnd, cMover);
                nFlipped++;
            }
        }

        return nFlipped;
    }

    /**
     * Determine how many disks are flipped in a given direction
     *
     * @return number of disks flipped in a given direction
     */
    private int nFlippedInDirection(int row, int col, int dRow, int dCol, char cMover, char cOpponent) {
        int rowEnd, colEnd;
        int nFlipped;
        nFlipped = 0;

        for (rowEnd = row + dRow, colEnd = col + dCol; getPiece(rowEnd, colEnd) == cOpponent; ) {
            rowEnd += dRow;
            colEnd += dCol;
        }

        if (getPiece(rowEnd, colEnd) == cMover) {
            for (rowEnd = row + dRow, colEnd = col + dCol; getPiece(rowEnd, colEnd) == cOpponent; rowEnd += dRow, colEnd += dCol) {
                nFlipped++;
            }
        }

        return nFlipped;
    }

    /**
     * Get the text for the board and a flag for black-to-move
     *
     * @return a GetTextResult containing the information.
     */
    public GetTextResult getText() {
        StringBuilder sb = new StringBuilder();

        for (int r = 0; r <= bt.n; r++) {
            for (int c = 0; c <= bt.n; c++) {
                if (getPiece(r, c) != DUMMY) {
                    sb.append(getPiece(r, c));
                }
            }
        }

        final String sBoard = sb.toString();
        Require.eq(sBoard.length(), "squares used", bt.NPlayableSquares());
        return new GetTextResult(sBoard, fBlackMove);
    }

    /**
     * Determine if it is black to move.
     * <p/>
     * Black may have no legal moves - if he must pass or the game is over.
     *
     * @return True if it is black to move.
     */
    public boolean isBlackMove() {
        return fBlackMove;
    }

    public int nEmpty() {
        return getPieceCounts().nEmpty;
    }

    /**
     * Set this board to be a copy of another board
     *
     * @param board board to copy form
     */
    public void copy(OsBoard board) {
        bt = new COsBoardType(board.bt);
        sBoard = (board.sBoard == null) ? null : Arrays.copyOf(board.sBoard, board.sBoard.length);
        fBlackMove = board.fBlackMove;
        validate();
    }


    public static class GetTextResult {
        final String text;
        final boolean blackMove;

        private GetTextResult(String text, boolean blackMove) {
            this.text = text;
            this.blackMove = blackMove;
        }

        /**
         * Get a text representation of the squares on the board.
         * <p/>
         * The returned value has no spaces. Dummy squares (squares that cannot be played) are not included.
         * All characters will be one of BLACK, WHITE, or EMPTY.
         *
         * @return text of the squares on the board.
         */
        public String getText() {
            return text;
        }

        public boolean isBlackMove() {
            return blackMove;
        }
    }

    /**
     * Set the pieces on the board
     *
     * @param sBoardText text representation of the pieces on the board. No spaces are allowed.
     */
    public void setText(String sBoardText, boolean fBlackMove) {
        setText(sBoardText);
        this.fBlackMove = fBlackMove;
    }

    /**
     * Set the pieces on the board
     *
     * @param sBoard text representation of the pieces on the board. No spaces are allowed. Pieces must be
     *               BLACK, WHITE, or EMPTY (*, O, or -).
     */
    void setText(final String sBoard) {
        int r, c;

        int i = 0;
        for (r = 0; r <= bt.n; r++) {
            for (c = 0; c <= bt.n; c++) {
                if (getPiece(r, c) != DUMMY) {
                    setPiece(r, c, sBoard.charAt(i++));
                }
            }
        }
    }

    /**
     * @return Character representing the player to move
     */
    public char getMoverChar() {
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

    /**
     * Get the result on the board. Winner gets empties. Positive means black won.
     *
     * @param fAnti if true, this is an "anti" game and the winner is the player with the FEWEST disks.
     * @return black disks - white disks (if anti, white disks - black disks).
     */
    public int getResult(boolean fAnti) {
        return getPieceCounts().result(fAnti);
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof OsBoard) {
            OsBoard b = (OsBoard) obj;
            return bt.equals(b.bt) && Arrays.equals(sBoard, b.sBoard) && fBlackMove == b.fBlackMove;
        } else {
            return false;
        }
    }

    /**
     * Width of the board, in squares
     *
     * for instance an 8x8 board returns 8.
     *
     * @return width of the board
     */
    public final int width() {
        return bt.n;
    }
}