package chess.controller;

import chess.domain.Position;
import chess.dto.PositionRequest;
import chess.game.ChessGame;
import chess.view.InputView;
import chess.view.MovementMapper;
import chess.view.OutputView;
import java.util.List;

public class ChessController {

    private final ChessGame chessGame;

    public ChessController(ChessGame chessGame) {
        this.chessGame = chessGame;
    }

    public void run() {
        OutputView.printStartMessage();
        repeat(this::extracted);
    }

    private void extracted() {
        while (true) {
            List<String> commands = InputView.readCommand();
            String command = commands.get(0);
            if (command.equals("end")) {
                return;
            }
            if (command.equals("start")) {
                start();
            }
            if (command.equals("move")) {
                move(commands);
            }
        }
    }

    private void start() {
        chessGame.start();
        OutputView.printBoard(chessGame.getBoard());
    }

    private void move(List<String> command) {
        String[] sourceCommand = command.get(1).split("");
        String[] targetCommand = command.get(2).split("");
        PositionRequest source = MovementMapper.map(sourceCommand[0], sourceCommand[1]);
        PositionRequest target = MovementMapper.map(targetCommand[0], targetCommand[1]);
        chessGame.move(Position.of(source.getX(), source.getY()), Position.of(target.getX(), target.getY()));
        OutputView.printBoard(chessGame.getBoard());
    }

    public void repeat(Runnable runnable) {
        try {
            runnable.run();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            repeat(runnable);
        }
    }
}
