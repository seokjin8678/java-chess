package chess.game;

import static java.util.stream.Collectors.toList;

import chess.domain.Board;
import chess.domain.BoardFactory;
import chess.domain.Position;
import chess.dto.SquareResponse;
import java.util.List;

public class ChessGame {
    private Board board;

    public void start() {
        board = new Board(BoardFactory.create());
    }

    public void move(Position source, Position target) {
        if (board == null) {
            throw new IllegalStateException("[ERROR] 보드가 세팅되지 않았습니다.");
        }
        board.move(source, target);
    }

    public List<SquareResponse> getBoard() {
        if (board == null) {
            throw new IllegalStateException("[ERROR] 보드가 세팅되지 않았습니다.");
        }
        return board.getBoard().entrySet().stream()
                .map(entry -> SquareResponse.of(entry.getKey(), entry.getValue()))
                .collect(toList());
    }
}
