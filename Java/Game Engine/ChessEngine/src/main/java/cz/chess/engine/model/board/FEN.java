package cz.chess.engine.model.board;

import cz.chess.engine.model.PlayingSide;
import cz.chess.engine.model.pieces.*;
import cz.chess.engine.model.board.Board.Builder;


/**
 * This class enables user to save and load games from a string in the Forsyth–Edwards Notation (FEN)
 * This class is only used for its methods, it cannot be instantiated.
 *
 * @author Vojtěch Sýkora
 */
public class FEN {

    private FEN() {
        throw new RuntimeException("FEN class not instantiable");
    }

    /**
     * Creates FEN string from input board
     *
     * @param board a code representation of a chessboard
     * @return FEN format string
     */
    public static String createFENFromBoard(final Board board) {
        return getBoardLayout(board) + " " +
                getCurrentPlayer(board) + " " +
                getCastling(board) + " " +
                getEnPassantPawn(board) + " " +
                "0 1";
    }

    /**
     * Creates Board instance from FEN string
     *
     * @param FEN format string
     * @return Board representation of the FEN string
     */
    public static Board createBoardFromFEN(final String FEN) {
        final Builder builder = new Builder();
        final String[] FENsplitted = FEN.trim().split(" ");
        final boolean whiteKingSideCastle = FENsplitted[2].contains("K");
        final boolean whiteQueenSideCastle = FENsplitted[2].contains("Q");
        final boolean blackKingSideCastle = FENsplitted[2].contains("k");
        final boolean blackQueenSideCastle = FENsplitted[2].contains("q");
        final String gameConfiguration = FENsplitted[0];
        final char[] boardTiles = gameConfiguration.replaceAll("/", "")
                .replaceAll("8", "--------")
                .replaceAll("7", "-------")
                .replaceAll("6", "------")
                .replaceAll("5", "-----")
                .replaceAll("4", "----")
                .replaceAll("3", "---")
                .replaceAll("2", "--")
                .replaceAll("1", "-")
                .toCharArray();
        int i = 0;
        while (i < boardTiles.length) {
            switch (boardTiles[i]) {
                case 'R':
                    builder.setPiece(new Rook(Utils.getCorrectIndex(i), PlayingSide.WHITE));
                    i++;
                    break;
                case 'N':
                    builder.setPiece(new Knight(Utils.getCorrectIndex(i), PlayingSide.WHITE));
                    i++;
                    break;
                case 'B':
                    builder.setPiece(new Bishop(Utils.getCorrectIndex(i), PlayingSide.WHITE));
                    i++;
                    break;
                case 'Q':
                    builder.setPiece(new Queen(Utils.getCorrectIndex(i), PlayingSide.WHITE));
                    i++;
                    break;
                case 'K':
                    builder.setPiece(new King(Utils.getCorrectIndex(i), PlayingSide.WHITE, whiteKingSideCastle, whiteQueenSideCastle));
                    i++;
                    break;
                case 'P':
                    builder.setPiece(new Pawn(Utils.getCorrectIndex(i), PlayingSide.WHITE));
                    i++;
                    break;
                case 'r':
                    builder.setPiece(new Rook(Utils.getCorrectIndex(i), PlayingSide.BLACK));
                    i++;
                    break;
                case 'n':
                    builder.setPiece(new Knight(Utils.getCorrectIndex(i), PlayingSide.BLACK));
                    i++;
                    break;
                case 'b':
                    builder.setPiece(new Bishop(Utils.getCorrectIndex(i), PlayingSide.BLACK));
                    i++;
                    break;
                case 'q':
                    builder.setPiece(new Queen(Utils.getCorrectIndex(i), PlayingSide.BLACK));
                    i++;
                    break;
                case 'k':
                    builder.setPiece(new King(Utils.getCorrectIndex(i), PlayingSide.BLACK, blackKingSideCastle, blackQueenSideCastle));
                    i++;
                    break;
                case 'p':
                    builder.setPiece(new Pawn(Utils.getCorrectIndex(i), PlayingSide.BLACK));
                    i++;
                    break;
                case '-':
                    i++;
                    break;
                default:
                    throw new RuntimeException("Invalid FEN String " +gameConfiguration);
            }
        }
        builder.setWhoPlaysNext(FENsplitted[1].equals("w") ? PlayingSide.WHITE : PlayingSide.BLACK);
        return builder.build();
    }

    private static String getBoardLayout(final Board board) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < Utils.NUM_TILES; i++) {
            final String pieceString = board.getPiece(Utils.getCorrectIndex(i)) == null ? "-" :
                    board.getPiece(Utils.getCorrectIndex(i)).getPlayingSide().isWhite() ?
                            board.getPiece(Utils.getCorrectIndex(i)).toString() :
                            board.getPiece(Utils.getCorrectIndex(i)).toString().toLowerCase();
            stringBuilder.append(pieceString);
        }
        stringBuilder.insert(8, "/");
        stringBuilder.insert(17, "/");
        stringBuilder.insert(26, "/");
        stringBuilder.insert(35, "/");
        stringBuilder.insert(44, "/");
        stringBuilder.insert(53, "/");
        stringBuilder.insert(62, "/");
        final String ret = stringBuilder.toString()
                .replaceAll("--------", "8")
                .replaceAll("-------", "7")
                .replaceAll("------", "6")
                .replaceAll("-----", "5")
                .replaceAll("----", "4")
                .replaceAll("---", "3")
                .replaceAll("--", "2")
                .replaceAll("-", "1");
        return ret;
    }

    private static String getCurrentPlayer(final Board board) {
        return board.getCurrentPlayer().toString().substring(0, 1).toLowerCase();
    }

    private static String getCastling(final Board board) {
        final StringBuilder stringBuilder = new StringBuilder();
        if(board.getWhitePlayer().canCastleKingSide()) {
            stringBuilder.append("K");
        }
        if(board.getWhitePlayer().canCastleQueenSide()) {
            stringBuilder.append("Q");
        }
        if(board.getBlackPlayer().canCastleKingSide()) {
            stringBuilder.append("k");
        }
        if(board.getBlackPlayer().canCastleQueenSide()) {
            stringBuilder.append("q");
        }
        final String ret = stringBuilder.toString();

        return ret.isEmpty() ? "-" : ret;
    }

    private static String getEnPassantPawn(final Board board) {
        final Pawn enPassantPawn = board.getEnPassantPawn();
        if(enPassantPawn != null) {
            return Utils.getAlphaNotationFromCoordinate(enPassantPawn.getPiecePosition() -
                    (8) * enPassantPawn.getPlayingSide().getDirection());
        }
        return "-";
    }

}
