package yofred.dev.justessentials;

import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class PunishmentStoreTest {
    @Test void parsesSupportedDurations() {
        assertEquals(Duration.ofSeconds(30), DurationParser.parse("30s"));
        assertEquals(Duration.ofMinutes(15), DurationParser.parse("15m"));
        assertEquals(Duration.ofHours(2), DurationParser.parse("2H"));
        assertEquals(Duration.ofDays(7), DurationParser.parse("7d"));
        assertEquals(Duration.ofDays(28), DurationParser.parse("4w"));
    }
    @Test void rejectsMalformedDurations() {
        assertNull(DurationParser.parse("0m"));
        assertNull(DurationParser.parse("ten minutes"));
        assertNull(DurationParser.parse("-2h"));
        assertNull(DurationParser.parse("2months"));
    }
}
