package yofred.dev.justessentials;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SafeFilesTest {
    @TempDir java.nio.file.Path directory;
    @Test void atomicallyReplacesExistingFile() throws Exception {
        var target = directory.resolve("data.json");
        SafeFiles.writeAtomically(target, "old");
        SafeFiles.writeAtomically(target, "new");
        assertEquals("new", Files.readString(target));
        assertEquals(false, Files.exists(directory.resolve("data.json.tmp")));
    }
}
