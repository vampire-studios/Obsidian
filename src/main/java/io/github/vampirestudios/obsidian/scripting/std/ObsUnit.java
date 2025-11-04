package io.github.vampirestudios.obsidian.scripting.std;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public record ObsUnit(Path path, List<ScriptEvent> events, List<ScriptCommand> commands, List<ScriptSchedule> schedules,
					  List<ScriptRule> rules, List<ScriptFunc> functions, List<ScriptObservable> observables, List<ScriptWatch> watches) {
	public ObsUnit(Path path) {
		this(path, new java.util.ArrayList<>(), new java.util.ArrayList<>(), new java.util.ArrayList<>(), new java.util.ArrayList<>(), new ArrayList<>(),
				new ArrayList<>(), new ArrayList<>());
	}
}