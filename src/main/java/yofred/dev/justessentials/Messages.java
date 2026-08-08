package yofred.dev.justessentials;

import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

final class Messages {
    static Component message(String template) { return colored(EssentialsConfig.MESSAGE_PREFIX.get() + template); }
    static Component message(String template, Map<String, String> values) { return message(replace(template, values)); }
    static Component plain(String template, Map<String, String> values) { return colored(replace(template, values)); }
    static String replace(String template, Map<String, String> values) { String result = template; for (var entry : values.entrySet()) result = result.replace("{" + entry.getKey() + "}", entry.getValue()); return result; }
    static Component colored(String input) {
        MutableComponent root = Component.empty();
        StringBuilder text = new StringBuilder();
        ChatFormatting color = ChatFormatting.WHITE;
        boolean bold = false, italic = false, underlined = false, strike = false;
        for (int index = 0; index < input.length(); index++) {
            if (input.charAt(index) == '&' && index + 1 < input.length()) {
                ChatFormatting format = format(input.charAt(index + 1));
                if (format != null) {
                    if (!text.isEmpty()) root.append(styled(text.toString(), color, bold, italic, underlined, strike));
                    text.setLength(0); index++;
                    if (format == ChatFormatting.RESET) { color = ChatFormatting.WHITE; bold = italic = underlined = strike = false; }
                    else if (format.isColor()) color = format;
                    else if (format == ChatFormatting.BOLD) bold = true;
                    else if (format == ChatFormatting.ITALIC) italic = true;
                    else if (format == ChatFormatting.UNDERLINE) underlined = true;
                    else if (format == ChatFormatting.STRIKETHROUGH) strike = true;
                    continue;
                }
            }
            text.append(input.charAt(index));
        }
        if (!text.isEmpty()) root.append(styled(text.toString(), color, bold, italic, underlined, strike));
        return root;
    }
    private static MutableComponent styled(String text, ChatFormatting color, boolean bold, boolean italic, boolean underlined, boolean strike) { return Component.literal(text).withStyle(style -> style.withColor(color).withBold(bold).withItalic(italic).withUnderlined(underlined).withStrikethrough(strike)); }
    private static ChatFormatting format(char code) { return switch (Character.toLowerCase(code)) { case '0' -> ChatFormatting.BLACK; case '1' -> ChatFormatting.DARK_BLUE; case '2' -> ChatFormatting.DARK_GREEN; case '3' -> ChatFormatting.DARK_AQUA; case '4' -> ChatFormatting.DARK_RED; case '5' -> ChatFormatting.DARK_PURPLE; case '6' -> ChatFormatting.GOLD; case '7' -> ChatFormatting.GRAY; case '8' -> ChatFormatting.DARK_GRAY; case '9' -> ChatFormatting.BLUE; case 'a' -> ChatFormatting.GREEN; case 'b' -> ChatFormatting.AQUA; case 'c' -> ChatFormatting.RED; case 'd' -> ChatFormatting.LIGHT_PURPLE; case 'e' -> ChatFormatting.YELLOW; case 'f' -> ChatFormatting.WHITE; case 'l' -> ChatFormatting.BOLD; case 'o' -> ChatFormatting.ITALIC; case 'n' -> ChatFormatting.UNDERLINE; case 'm' -> ChatFormatting.STRIKETHROUGH; case 'r' -> ChatFormatting.RESET; default -> null; }; }
    private Messages() {}
}
