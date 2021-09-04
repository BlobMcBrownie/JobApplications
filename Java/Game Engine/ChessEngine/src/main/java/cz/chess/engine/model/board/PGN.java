package cz.chess.engine.model.board;

/**
 * Class for working with the PGN file format
 *
 * @author Vojtěch Sýkora
 */
public class PGN {

    static String event = "[Event \"Java ChessGame\"]\n";
    static String site = "[Site \"inside a PC\"]\n";
    static String date = "[Date \"2021.20.05\"]\n";
    static String round = "[Round \"6\"]\n";
    static String white = "[White \"human1\"]\n";
    static String black = "[Black \"human2\"]\n";
    static String score = "[Result \"1:0\"]\n";

//    private static final Pattern KING_SIDE_CASTLE = Pattern.compile("O-O#?\\+?");
//    private static final Pattern QUEEN_SIDE_CASTLE = Pattern.compile("O-O-O#?\\+?");
//    private static final Pattern PLAIN_PAWN_MOVE = Pattern.compile("^([a-h][0-8])(\\+)?(#)?$");
//    private static final Pattern PAWN_OFFENSIVE_MOVE = Pattern.compile("(^[a-h])(x)([a-h][0-8])(\\+)?(#)?$");
//    private static final Pattern NORMAL_MOVE = Pattern.compile("^(B|N|R|Q|K)([a-h]|[1-8])?([a-h][0-8])(\\+)?(#)?$");
//    private static final Pattern OFFENSIVE_MOVE = Pattern.compile("^(B|N|R|Q|K)([a-h]|[1-8])?(x)([a-h][0-8])(\\+)?(#)?$");
//    private static final Pattern PLAIN_PAWN_PROMOTION_MOVE = Pattern.compile("(.*?)=(.*?)");
//    private static final Pattern OFFENSIVE_PAWN_PROMOTION_MOVE = Pattern.compile("(.*?)x(.*?)=(.*?)");

    /**
     * Creates a PGN format string from the @params board and moveLog.
     *
     * @param board
     * @param moveLog
     * @return PGN string
     */
    public static String createPGNString(Board board, MoveLog moveLog) {
        StringBuffer sb = new StringBuffer();
        sb.append(event + site + date + round + white + black + score + "\n");
        sb.append(moveLog);
        return sb.toString();
    }

    /**
     * Builds a Board from the @param string using the PGn format rules.
     *
     * @param PGNMoves
     * @return Board instance
     */
    public static Board createGameFromPGN(final String PGNMoves) {
        Board board = Board.createStartingBoard();
//        extractMovesFromPGN(PGNMoves, board);
        return board;
    }

//    private static void extractMovesFromPGN(final String pgnMoves, Board board) {
//        String[] movesStr = pgnMoves.split("([0-9]*)\\.");
//        for (int i = 1; i < movesStr.length; i++) {
//            System.out.println(i + ": " + movesStr[i]);
//        }
//    }

    /**
     * Creates the PGN file format from a MoveLog without the headers.
     *
     * @param moveLog
     * @return PGN string representation of the MoveLog
     */
    public static String createGameRecord(MoveLog moveLog) {
        StringBuffer sb = new StringBuffer();
        sb.append(moveLog);
        return sb.toString();
    }
}
