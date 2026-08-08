package yofred.dev.justessentials;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Map;
import org.junit.jupiter.api.Test;

class MessagesTest {
    @Test void replacesKnownPlaceholdersAndLeavesUnknownOnesVisible() {
        assertEquals("Alex: griefing ({missing})", TemplateFormatter.replace("{player}: {reason} ({missing})", Map.of("player", "Alex", "reason", "griefing")));
    }
}
