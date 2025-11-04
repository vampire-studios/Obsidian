package io.github.vampirestudios.obsidian.scripting.std;

import java.util.List;

public record ScriptFunc(String name, List<String> params, List<Stmt> bodyStmts) {}
