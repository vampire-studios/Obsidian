package io.github.vampirestudios.obsidian.api.scripting;

import java.util.List;

public record Script(String event, List<String> commands) {
}
