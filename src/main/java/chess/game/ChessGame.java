package chess.game;

import chess.domain.Board;
import chess.domain.BoardFactory;
import chess.domain.Position;
import chess.dto.SquareResponse;
import chess.game.state.EndState;
import chess.game.state.GameState;
import chess.game.state.RunningState;
import chess.game.state.WaitingState;
import java.util.List;

public class ChessGame {
    private Board board;
    private GameState gameState;

    public ChessGame() {
        this.gameState = WaitingState.STATE;
    }

    public void start() {
        gameState.startGame(() -> {
            this.board = new Board(BoardFactory.create());
            this.gameState = RunningState.STATE;
        });
    }

    public void end() {
        this.gameState = EndState.STATE;
    }

    public boolean isEnd() {
        return gameState.isEnd();
    }

    public void move(Position source, Position target) {
        gameState.movePiece(() -> board.move(source, target));
    }

    public List<SquareResponse> getBoard() {
        return gameState.getBoard(board);
    }
}
