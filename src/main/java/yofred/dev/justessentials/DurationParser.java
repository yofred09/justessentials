package yofred.dev.justessentials;

import java.time.Duration;
import java.util.Locale;
import java.util.regex.Pattern;

final class DurationParser {
    private static final Pattern PATTERN = Pattern.compile("(?i)^(\\d+)(s|m|h|d|w)$");
    static Duration parse(String input) {
        var matcher = PATTERN.matcher(input); if (!matcher.matches()) return null;
        long value; try { value = Long.parseLong(matcher.group(1)); } catch (NumberFormatException exception) { return null; }
        if (value < 1) return null;
        try { return switch (matcher.group(2).toLowerCase(Locale.ROOT)) { case "s" -> Duration.ofSeconds(value); case "m" -> Duration.ofMinutes(value); case "h" -> Duration.ofHours(value); case "d" -> Duration.ofDays(value); case "w" -> Duration.ofDays(Math.multiplyExact(value, 7)); default -> null; }; }
        catch (ArithmeticException exception) { return null; }
    }
    private DurationParser() {}
}
