package chess.game.state;

import chess.domain.Board;
import chess.dto.SquareResponse;
import java.util.List;

public class EndState implements GameState {
    private static final String END_STATE_EXCEPTION_MESSAGE = "[ERROR] 잘못된 게임의 상태 입니다.(상태: 종료됨)";
    public static final GameState STATE = new EndState();

    private EndState() {
    }

    @Override
    public void startGame(Runnable runnable) {
        throw new IllegalStateException(END_STATE_EXCEPTION_MESSAGE);
    }

    @Override
    public boolean isEnd() {
        return true;
    }

    @Override
    public void movePiece(Runnable runnable) {
        throw new IllegalStateException(END_STATE_EXCEPTION_MESSAGE);
    }

    @Override
    public List<SquareResponse> getBoard(Board board) {
        throw new IllegalStateException(END_STATE_EXCEPTION_MESSAGE);
    }
}