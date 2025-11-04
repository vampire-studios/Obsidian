package io.github.vampirestudios.obsidian.scripting.std;

import java.util.List;

public record ScriptEvent(String name, List<String> body, String playerVar) {}
