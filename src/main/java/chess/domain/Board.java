package chess.domain;

import chess.domain.piece.Empty;
import chess.domain.piece.Piece;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Board {
    private final Map<Position, Piece> squares;

    public Board(Map<Position, Piece> squares) {
        this.squares = squares;
    }

    public Map<Position, Piece> getBoard() {
        return Collections.unmodifiableMap(squares);
    }

    public void move(Position source, Position target) {
        Piece sourcePiece = squares.get(source);
        Piece targetPiece = squares.get(target);
        if (sourcePiece.getTeam() == targetPiece.getTeam()) {
            throw new IllegalArgumentException("[ERROR] 목적지에 아군 말이 존재합니다.");
        }
        if (!sourcePiece.canMove(source, target)) {
            throw new IllegalArgumentException("[ERROR] 해당 목적지로 이동할 수 없습니다.");
        }
        if (sourcePiece.getRole() != Role.KNIGHT) {
            validateCollision(source, target);
        }
        squares.put(source, Empty.INSTANCE);
        squares.put(target, sourcePiece);
    }

    private void validateCollision(Position source, Position target) {
        List<Position> routes = source.getBetweenPositions(target);
        for (Position route : routes) {
            if (squares.get(route) != Empty.INSTANCE) {
                throw new IllegalArgumentException("[ERROR] 해당 경로에 아군 말이 있습니다.");
            }
        }
    }
}
