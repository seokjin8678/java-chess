package chess.view;

import java.util.List;
import java.util.Scanner;

public class InputView {
    private static final List<String> ALLOW_COMMANDS = List.of("start", "end", "move");
    private static final Scanner scanner = new Scanner(System.in);
    private static final String DELIMITER = " ";
    private static final int COMMAND_INDEX = 0;

    private InputView() {
    }

    public static List<String> readCommand() {
        String input = scanner.nextLine();
        List<String> commands = List.of(input.split(DELIMITER));
        validateBlank(commands);
        validateAllowCommand(commands.get(COMMAND_INDEX));
        return commands;
    }

    private static void validateBlank(List<String> commands) {
        boolean hasBlank = commands.stream()
                .anyMatch(String::isBlank);
        if (commands.isEmpty() || hasBlank) {
            throw new IllegalArgumentException("[ERROR] 공백은 입력될 수 없습니다.");
        }
    }

    private static void validateAllowCommand(String command) {
        if (!ALLOW_COMMANDS.contains(command)) {
            throw new IllegalArgumentException("[ERROR] 해당 커맨드가 존재하지 않습니다.");
        }
    }
}
