package cz.chess.engine.view_controller.menu;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import static cz.chess.engine.view_controller.App.customSelectedPiece;

/**
 * The panel in CustomGame Scene where user can choose which piece to place on the board
 * Can be found in CustomGame Scene on the right
 *
 * @author Vojtěch Sýkora
 */
public class CustomPiecesPanel extends GridPane {
    
    private static ImageView WB, WK, WN, WP, WQ, WR, BB , BK, BN, BP, BQ, BR;

    public CustomPiecesPanel() {
        createPieces();

        this.setAlignment(Pos.CENTER);
    }

    private void createPieces() {
        WB = createPieceBlock("bishop_white", 0, 0);
        WB.setOnMouseClicked(e -> {
            customSelectedPiece = "WHITE B";
        });

        WK = createPieceBlock("king_white", 1, 0);
        WK.setOnMouseClicked(e -> {
            customSelectedPiece = "WHITE K";
        });

        WN = createPieceBlock("knight_white", 2, 0);
        WN.setOnMouseClicked(e -> {
            customSelectedPiece = "WHITE N";
        });

        WP = createPieceBlock("pawn_white", 3, 0);
        WP.setOnMouseClicked(e -> {
            customSelectedPiece = "WHITE P";
        });

        WQ = createPieceBlock("queen_white", 4, 0);
        WQ.setOnMouseClicked(e -> {
            customSelectedPiece = "WHITE Q";
        });

        WR = createPieceBlock("rook_white", 5, 0);
        WR.setOnMouseClicked(e -> {
            customSelectedPiece = "WHITE R";
        });

        BB = createPieceBlock("bishop_black", 0, 1);
        BB.setOnMouseClicked(e -> {
            customSelectedPiece = "BLACK B";
        });

        BK = createPieceBlock("king_black", 1, 1);
        BK.setOnMouseClicked(e -> {
            customSelectedPiece = "BLACK K";

        });

        BN = createPieceBlock("knight_black", 2, 1);
        BN.setOnMouseClicked(e -> {
            customSelectedPiece = "BLACK N";
        });

        BP = createPieceBlock("pawn_black", 3, 1);
        BP.setOnMouseClicked(e -> {
            customSelectedPiece = "BLACK P";
        });

        BQ = createPieceBlock("queen_black", 4, 1);
        BQ.setOnMouseClicked(e -> {
            customSelectedPiece = "BLACK Q";
        });

        BR = createPieceBlock("rook_black", 5, 1);
        BR.setOnMouseClicked(e -> {
            customSelectedPiece = "BLACK R";
        });
    }

    private ImageView createPieceBlock(String url, int row, int column) {
        ImageView imageView = new ImageView(new Image(url + ".png"));
        this.add(imageView, column, row);
        return imageView;
    }
}
