package yofred.dev.justessentials;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

final class SafeFiles {
    static void writeAtomically(Path target, String contents) throws IOException {
        Path temporary = target.resolveSibling(target.getFileName() + ".tmp");
        Files.writeString(temporary, contents, StandardCharsets.UTF_8);
        try { Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
        catch (AtomicMoveNotSupportedException exception) { Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING); }
    }
    static void preserveCorrupt(Path source) {
        if (!Files.exists(source)) return;
        try { Files.copy(source, source.resolveSibling(source.getFileName() + ".corrupt-" + System.currentTimeMillis()), StandardCopyOption.REPLACE_EXISTING); }
        catch (IOException exception) { JustEssentials.LOGGER.error("Unable to preserve corrupt data file {}", source, exception); }
    }
    private SafeFiles() {}
}
