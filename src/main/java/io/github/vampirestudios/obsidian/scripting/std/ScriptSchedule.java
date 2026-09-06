package io.github.vampirestudios.obsidian.scripting.std;

import java.time.Duration;
import java.util.List;

public record ScriptSchedule(String tag, Duration every, List<String> body) {
}
