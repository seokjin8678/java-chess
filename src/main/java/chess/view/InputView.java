package chess.view;

import java.util.List;
import java.util.Scanner;

public class InputView {
    private static final List<String> ALLOW_COMMANDS = List.of("start", "end", "move");
    private static final Scanner scanner = new Scanner(System.in);

    private InputView() {
    }

    public static List<String> readCommand() {
        String input = scanner.nextLine();
        try {
            List<String> commands = List.of(input.split(" "));
            validate(commands.get(0));
            return commands;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return readCommand();
        }
    }

    private static void validate(String command) {
        if (!ALLOW_COMMANDS.contains(command)) {
            throw new IllegalArgumentException("[ERROR] 해당 커맨드가 존재하지 않습니다.");
        }
    }
}
