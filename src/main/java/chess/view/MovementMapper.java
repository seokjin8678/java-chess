package chess.view;

import chess.dto.PositionRequest;
import java.util.HashMap;
import java.util.Map;

public class MovementMapper {
    private static final Map<String, Integer> files = new HashMap<>();
    private static final Map<String, Integer> ranks = new HashMap<>();

    private MovementMapper() {}

    static {
        files.put("a", 0);
        files.put("b", 1);
        files.put("c", 2);
        files.put("d", 3);
        files.put("e", 4);
        files.put("f", 5);
        files.put("g", 6);
        files.put("h", 7);

        ranks.put("1", 0);
        ranks.put("2", 1);
        ranks.put("3", 2);
        ranks.put("4", 3);
        ranks.put("5", 4);
        ranks.put("6", 5);
        ranks.put("7", 6);
        ranks.put("8", 7);
    }

    public static PositionRequest map(String file, String rank) {
        return new PositionRequest(files.get(file), ranks.get(rank));
    }
}
