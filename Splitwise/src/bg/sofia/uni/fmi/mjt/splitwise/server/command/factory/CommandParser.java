package bg.sofia.uni.fmi.mjt.splitwise.server.command.factory;

import java.util.Arrays;
import java.util.Set;

public class CommandParser {
    private static final int MIN_ARGUMENTS_FOR_REASON = 3; // Minimum tokens before "reason" starts
    private static final Set<String> REASON_FIELD = Set.of("split", "split-group");

    public static String[] parseCommand(String clientInput) {
        if (clientInput == null || clientInput.isBlank()) {
            return new String[0];
        }

        String[] tokens = clientInput.trim().split("\\s+");
        if (tokens.length < 2) {
            return tokens;
        }

        String command = tokens[0];

        // If command involves a reason field (like `split` or `split-group`), process it separately
        if (REASON_FIELD.contains(command) && tokens.length >= MIN_ARGUMENTS_FOR_REASON) {
            return processReasonField(tokens);
        }

        return tokens;
    }

    private static String[] processReasonField(String[] tokens) {
        // Keep first 3 parts: [command, amount, target]
        String[] extractedArgs = Arrays.copyOfRange(tokens, 0, MIN_ARGUMENTS_FOR_REASON);
        // Merge the remaining as reason
        String reason = String.join(" ", Arrays.copyOfRange(tokens, MIN_ARGUMENTS_FOR_REASON, tokens.length));
        return new String[] {extractedArgs[0], extractedArgs[1], extractedArgs[2], reason};
    }
}
