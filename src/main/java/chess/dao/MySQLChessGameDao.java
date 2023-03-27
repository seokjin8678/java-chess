package chess.dao;

import static java.util.stream.Collectors.toList;

import chess.dao.template.JdbcContext;
import chess.domain.Board;
import chess.domain.BoardFactory;
import chess.domain.Position;
import chess.domain.Role;
import chess.domain.Team;
import chess.domain.piece.Piece;
import chess.domain.piece.PieceFactory;
import chess.game.GameId;
import chess.game.GameResult;
import chess.game.state.GameState;
import chess.game.state.running.BlackCheckedState;
import chess.game.state.running.BlackTurnState;
import chess.game.state.running.WhiteCheckedState;
import chess.game.state.running.WhiteTurnState;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySQLChessGameDao implements ChessGameDao {
    private final JdbcContext jdbcContext;

    public MySQLChessGameDao(JdbcContext jdbcContext) {
        this.jdbcContext = jdbcContext;
    }

    @Override
    public Board findBoard(GameId gameId) {
        Map<Position, Piece> board = BoardFactory.createEmptyBoard();
        board.putAll(findSquares(gameId));
        return new Board(board);
    }

    private Map<Position, Piece> findSquares(GameId gameId) {
        final String query = "SELECT * FROM board WHERE game_id = ?";
        return jdbcContext.select(query, resultSet -> {
            Map<Position, Piece> squares = new HashMap<>();
            while (resultSet.next()) {
                squares.put(getPosition(resultSet), getPiece(resultSet));
            }
            return squares;
        }, gameId.getGameId());
    }

    private Position getPosition(ResultSet resultSet) throws SQLException {
        int x = resultSet.getInt("x");
        int y = resultSet.getInt("y");
        return Position.of(x, y);
    }

    private Piece getPiece(ResultSet resultSet) throws SQLException {
        Role role = Role.valueOf(resultSet.getString("role"));
        Team team = Team.valueOf(resultSet.getString("team"));
        return PieceFactory.of(role, team);
    }

    @Override
    public GameState findGameState(GameId gameId) {
        final String query = "SELECT * FROM state WHERE game_id = ?";
        return jdbcContext.select(query, resultSet -> {
            resultSet.next();
            return RunningStateMapper.map(resultSet.getString("state"));
        }, gameId.getGameId());
    }

    @Override
    public void saveChessGame(GameId gameId, Board board, GameState gameState) {
        deleteAllBoard(gameId);
        deleteGameState(gameId);
        saveBoard(gameId, board.getBoard());
        saveGameState(gameId, gameState);
    }

    private void saveBoard(GameId gameId, Map<Position, Piece> board) {
        final String query = "INSERT INTO board VALUES(%s, %s, %s, \"%s\", \"%s\")";
        List<String> queries = board.entrySet().stream()
                .filter(entry -> !entry.getValue().isRoleOf(Role.EMPTY))
                .map(entry -> String.format(query, gameId.getGameId(), entry.getKey().getX(), entry.getKey().getY(),
                        entry.getValue().getRole().name(), entry.getValue().getTeam().name()))
                .collect(toList());
        jdbcContext.insertBulk(queries);
    }

    private void saveGameState(GameId gameId, GameState gameState) {
        String query = "INSERT INTO state VALUES(?, ?)";
        jdbcContext.insert(query, gameId.getGameId(), RunningStateMapper.map(gameState));
    }

    @Override
    public void createChessGame(GameId gameId, Board board, GameState gameState) {
        String query = "INSERT INTO game VALUES(?)";
        jdbcContext.insert(query, gameId.getGameId());
        saveBoard(gameId, board.getBoard());
        saveBoard(gameId, board.getBoard());
        saveGameState(gameId, gameState);
    }

    private void deleteAllBoard(GameId gameId) {
        String query = "DELETE FROM board WHERE game_id = ?";
        jdbcContext.update(query, gameId.getGameId());
    }

    private void deleteGameState(GameId gameId) {
        String query = "DELETE FROM state WHERE game_id = ?";
        jdbcContext.update(query, gameId.getGameId());
    }

    @Override
    public boolean isExistGame(GameId gameId) {
        final String query = "SELECT * FROM state WHERE game_id = ? LIMIT 1";
        return jdbcContext.select(query, ResultSet::next, gameId.getGameId());
    }

    @Override
    public void transaction(Runnable runnable) {
        jdbcContext.transaction(runnable);
    }

    @Override
    public void deleteGame(GameId gameId) {
        final String query = "DELETE FROM game WHERE game_id = ?";
        jdbcContext.update(query, gameId.getGameId());
    }

    @Override
    public List<GameId> findAllGameId() {
        final String query = "SELECT game_id FROM game";
        return jdbcContext.select(query, resultSet -> {
            List<GameId> gameIds = new ArrayList<>();
            while (resultSet.next()) {
                gameIds.add(new GameId(resultSet.getString("game_id")));
            }
            return gameIds;
        });
    }

    @Override
    public void saveGameResult(String name, double score, GameResult gameResult) {
        final String query = "INSERT INTO game_result (nickname,score,result) VALUES(?,?,?)";
        jdbcContext.insert(query, name, score, gameResult.name());
    }

    private enum RunningStateMapper {
        WHITE_TURN(WhiteTurnState.STATE),
        BLACK_TURN(BlackTurnState.STATE),
        WHITE_CHECKED(WhiteCheckedState.STATE),
        BLACK_CHECKED(BlackCheckedState.STATE);

        final GameState gameState;

        RunningStateMapper(GameState gameState) {
            this.gameState = gameState;
        }

        public static String map(GameState gameState) {
            return Arrays.stream(values())
                    .filter(runningStateMapper -> runningStateMapper.isSameState(gameState))
                    .findAny()
                    .orElseThrow(IllegalArgumentException::new)
                    .name();
        }

        public static GameState map(String gameState) {
            return RunningStateMapper.valueOf(gameState).gameState;
        }

        private boolean isSameState(GameState gameState) {
            return this.gameState == gameState;
        }
    }
}
