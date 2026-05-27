package io.github.vampirestudios.obsidian.api.scripting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptParser {
	public static ParseResult parseAll(Path file) throws IOException {
		List<String> lines = Files.readAllLines(file);
		Map<String, String> opts = new HashMap<>();
		int i = 0;
		// ── consume an options: block if present ─────────────────────
		if (i < lines.size() && lines.get(i).trim().equals("options:")) {
			i++;
			while (i < lines.size()) {
				String l = lines.get(i);
				Matcher m = Pattern.compile("^\\s+(\\S+):\\s*(\\S.*)$")
						.matcher(l);
				if (!m.find()) break;
				opts.put(m.group(1), m.group(2));
				i++;
			}
		}

		List<Script> scripts = new ArrayList<>();
		List<RecurringTaskDef> recs = new ArrayList<>();
		List<CommandDef> commands = new ArrayList<>();
		while (i < lines.size()) {
			String raw = lines.get(i).trim();
			// ── on EVENT { ───────────────────────────────────────────
			if (raw.startsWith("on ") && raw.endsWith("{")) {
				String evt = raw.substring(3, raw.length() - 1).trim();
				List<String> body = collectBlock(lines, ++i);
				scripts.add(new Script(evt, body));
				continue;
			}
			// ── command /name <p1> <p2> …: ──────────────────────────────
			else if (raw.startsWith("command ")) {
				// e.g. raw = "command /warp <x> <y> <z>:"
				String inside = raw.substring(8, raw.length() - 1).trim();
				String[] parts = inside.split("\\s+");
				String cmdName = parts[0];                 // "/warp"
				List<ParamDef> params = new ArrayList<>();
				// inside parseAll when you see a command line…
				Pattern p = Pattern.compile("([<\\[])([^:>\\]]+)(?::(\\w+))?([>\\]])");
				for (int j = 1; j < parts.length; j++) {
					Matcher m = p.matcher(parts[j]);
					if (m.matches()) {
						String br = m.group(1);         // "<" or "["
						String nm = m.group(2);         // param name
						String t = m.group(3);         // optional type
						boolean req = br.equals("<");   // required if <>
						ParamType pt = switch (t == null ? "word" : t) {
							case "number", "int", "integer" -> ParamType.INTEGER;
							case "text", "string", "msg" -> ParamType.TEXT;
							case "player", "p" -> ParamType.PLAYER;
							default -> ParamType.WORD;
						};
						params.add(new ParamDef(nm, pt, req));
					}
				}
				List<String> body = collectBlock(lines, ++i);
				commands.add(new CommandDef(cmdName, params, body));
				continue;                   // ← add this
			}
			// ── every N UNIT { ──────────────────────────────────────
			else if (raw.startsWith("every ")) {
				// strip “every ” and trailing “{”
				String inside = raw.substring(6, raw.length() - 1).trim();
				String[] parts = inside.split("\\s+");
				int n = Integer.parseInt(parts[0]);
				int ticksPerUnit = switch (parts[1].toLowerCase()) {
					case "tick", "ticks" -> 1;
					case "second", "seconds" -> 20;
					case "minute", "minutes" -> 20 * 60;
					case "hour", "hours" -> 20 * 60 * 60;
					default -> throw new IllegalArgumentException("Unknown unit: " + parts[1]);
				};
				int interval = n * ticksPerUnit;
				List<String> body = collectBlock(lines, ++i);
				recs.add(new RecurringTaskDef(interval, body));
				continue;
			}
			i++;
		}
		return new ParseResult(scripts, recs, commands, opts);
	}

	private static List<String> collectBlock(List<String> lines, int start) {
		List<String> block = new ArrayList<>();
		int depth = 1;
		for (int j = start; j < lines.size(); j++) {
			String l = lines.get(j).trim();
			if (l.endsWith("{")) depth++;
			else if (l.equals("}")) {
				depth--;
				if (depth == 0) break;
			}
			block.add(l);
		}
		return block;
	}

	public enum ParamType {
        WORD, TEXT, INTEGER, FLOAT, BOOL, TEAM_COLOR, HEX_COLOR, ENTITY,
        ENTITIES, PLAYER, PLAYERS, GAME_MODE, BLOCK_POS, TIME, UUID,
        ROTATION, ANGLE, SWIZZLE, VEC2, VEC3
    }

	/**
	 * @param name  e.g. "/warp"
	 * @param params  e.g. ["x","y","z"]
	 * @param body  the script lines
	 **/
	public record CommandDef(String name, List<ParamDef> params, List<String> body) {
	}

	public record RecurringTaskDef(int intervalTicks, List<String> commands) {
	}

	public static class ParseResult {
		public final List<Script> scripts;
		public final List<RecurringTaskDef> recurring;
		public final List<CommandDef> commands;
		public final Map<String, String> options;

		public ParseResult(List<Script> s, List<RecurringTaskDef> r, List<CommandDef> c, Map<String, String> o) {
			this.scripts = s;
			this.recurring = r;
			this.commands = c;
			this.options = o;
		}
	}

	public record ParamDef(String name, ParamType type, boolean required) {
	}
}
