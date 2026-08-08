package yofred.dev.justessentials;

import java.util.Map;

final class TemplateFormatter {
    static String replace(String template, Map<String, String> values) { String result = template; for (var entry : values.entrySet()) result = result.replace("{" + entry.getKey() + "}", entry.getValue()); return result; }
    private TemplateFormatter() {}
}
