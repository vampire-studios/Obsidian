package io.github.vampirestudios.obsidian.scripting.std;

import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ObsParser {

	/* headers */
	private static final Pattern EVENT = Pattern.compile(
			"^\\s*event\\s+([A-Za-z_][A-Za-z0-9_]*)\\.([A-Za-z_][A-Za-z0-9_]*)\\s*\\(([^)]*)\\)\\s*\\{\\s*$");
	private static final Pattern CMD = Pattern.compile(
			"^\\s*command\\s+\"([^\"]+)\"\\s*\\(([^)]*)\\)\\s*\\{\\s*$"); // you already allow any quoted name
	private static final Pattern EVERY = Pattern.compile("^\\s*(every|schedule)\\s+([0-9]+[smht])\\s+tag\\s+\"([^\"]+)\"\\s*\\{\\s*$");
	private static final Pattern RULE         = Pattern.compile("^\\s*rule\\s+when\\s+(.*)\\{\\s*$");
	private static final Pattern RULE_EVERY   = Pattern.compile("^\\s*rule\\s+every\\s+([0-9]+[smht])\\s+when\\s+(.*)\\{\\s*$");
	private static final Pattern FUNC = Pattern.compile("^\\s*func\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*\\(([^)]*)\\)\\s*\\{\\s*$");

	/* command block metadata */
	private static final Pattern PERM = Pattern.compile("^\\s*permission\\s*:\\s*\"([^\"]*)\"\\s*;\\s*$"); // kept for backward compat; ignored at runtime
	private static final Pattern ACTIONS = Pattern.compile("^\\s*actions\\s*:\\s*\\{\\s*$");
	private static final Pattern ALIASES = Pattern.compile("^\\s*aliases\\s*:\\s*\\[(.*?)]\\s*;?\\s*$");
	private static final Pattern COOLDOWN = Pattern.compile("^\\s*cooldown\\s*:\\s*([0-9]+[smht]?)\\s*;?\\s*$");
	private static final Pattern DESC = Pattern.compile("^\\s*description\\s*:\\s*\"([^\"]*)\"\\s*;?\\s*$");
	private static final Pattern SUGGEST = Pattern.compile("^\\s*suggest\\s*:\\s*\\{\\s*$");
	private static final Pattern SUG_ENTRY = Pattern.compile("^\\s*([A-Za-z_][A-Za-z0-9_]*)\\s*:\\s*\"([^\"]+)\"\\s*;?\\s*$");
	private static final Pattern OBS = Pattern.compile("^\\s*observable\\s+(player\\s+)?([A-Za-z_][A-Za-z0-9_]*)\\s*=\\s*(.*);\\s*$");
	private static final Pattern WATCH = Pattern.compile("^\\s*watch\\s+(player\\s+)?\\(\\s*([A-Za-z_][A-Za-z0-9_]*)\\s*\\)\\s*(?:when\\s+(.*))?\\{\\s*$");

	public static ObsUnit parse(Path path, String src) {
		var unit = new ObsUnit(path);
		var it = Arrays.asList(src.split("\\R")).listIterator();

		int lineNo = 0;
		while (it.hasNext()) {
			String line = it.next();
			lineNo++;
			if (line.isBlank() || line.strip().startsWith("//") || line.strip().startsWith("#")) continue;
			Matcher m;

			/* events */
			if ((m = EVENT.matcher(line)).matches()) {
				var body = readBlock(it);
				unit.events().add(new ScriptEvent(
						m.group(1) + "." + m.group(2),
						body,
						firstVar(m.group(3))  // still expose first var name for your runtime binding
				));
				continue;
			}

			/* commands */
			if ((m = CMD.matcher(line)).matches()) {
				String cmdName = m.group(1);
				String paramsSig = m.group(2);

				List<ScriptCommand.Param> params = parseParams(paramsSig);

				String perm = "";
				List<String> actions = new ArrayList<>();
				List<String> aliases = List.of();
				Map<String, String> suggests = new HashMap<>();
				int cooldownTicks = 0;
				String description = null;

				int depth = 1; // inside outer '{'
				while (it.hasNext()) {
					String ln = it.next();

					// adjust structural depth
					int open = (int) ln.chars().filter(ch -> ch=='{').count();
					int close = (int) ln.chars().filter(ch -> ch=='}').count();
					depth += open - close;

					if (ln.isBlank() || ln.strip().startsWith("//")) {
						if (depth == 0) break;
						continue;
					}

					Matcher a;
					if ((a = PERM.matcher(ln)).matches()) { perm = a.group(1); if (depth == 0) break; continue; }
					if ((a = ALIASES.matcher(ln)).matches()) { aliases = parseAliases(a.group(1)); if (depth == 0) break; continue; }
					if ((a = COOLDOWN.matcher(ln)).matches()) { cooldownTicks = parseDurTicks(a.group(1)); if (depth == 0) break; continue; }
					if ((a = DESC.matcher(ln)).matches()) { description = a.group(1); if (depth == 0) break; continue; }

					if (SUGGEST.matcher(ln).matches()) {
						int d = 1;
						while (it.hasNext()) {
							String sLn = it.next();
							if (sLn.contains("{")) d++;
							if (sLn.contains("}")) { d--; if (d == 0) break; }
							Matcher se = SUG_ENTRY.matcher(sLn);
							if (se.matches()) suggests.put(se.group(1), se.group(2));
						}
						if (depth == 0) break;
						continue;
					}

					if (ACTIONS.matcher(ln).matches()) {
						// only read lines inside the actions { ... } block
						actions.addAll(readBlock(it));
						if (depth == 0) break;
						continue;
					}

					// DO NOT add raw lines here anymore
					if (depth == 0) break;
				}

				unit.commands().add(new ScriptCommand(cmdName, params, actions, aliases, suggests, cooldownTicks, description, perm));
				continue;
			}

			/* schedules */
			if ((m = EVERY.matcher(line)).matches()) {
				unit.schedules().add(new ScriptSchedule(
						m.group(3),
						Duration.ofMillis(parseDurTicks(m.group(2)) * 50L),
						readBlock(it)
				));
				continue;
			}

			// paced rules: rule every <dur> when <pred> { ... }
			if ((m = RULE_EVERY.matcher(line)).matches()) {
				int ticks = parseDurTicks(m.group(1));
				var body = readBlock(it);
				unit.rules().add(ScriptRule.paced(m.group(2).trim(), body, ticks, 20));
				continue;
			}

			// edge rules: rule when <pred> { ... }
			if ((m = RULE.matcher(line)).matches()) {
				var body = readBlock(it);
				unit.rules().add(ScriptRule.edge(m.group(1).trim(), body, 20));
				continue;
			}

			if ((m = FUNC.matcher(line)).matches()) {
				String fname = m.group(1);
				List<String> fparams = parseFuncParams(m.group(2));
				List<Stmt> body = readStmtBlock(it);
				unit.functions().add(new ScriptFunc(fname, fparams, body));
				continue;
			}

			if ((m = OBS.matcher(line)).matches()) {
				boolean isPlayer = m.group(1) != null;
				String name = m.group(2);
				String expr = m.group(3).trim();
				unit.observables().add(new ScriptObservable(
						name, expr, isPlayer ? ScriptObservable.Scope.PLAYER : ScriptObservable.Scope.GLOBAL
				));
				continue;
			}
			if ((m = WATCH.matcher(line)).matches()) {
				boolean isPlayer = m.group(1) != null;
				String name = m.group(2);
				String when = (m.group(3) == null) ? null : m.group(3).trim();
				var body = readBlock(it);
				unit.watches().add(new ScriptWatch(
						name, body,
						isPlayer ? ScriptObservable.Scope.PLAYER : ScriptObservable.Scope.GLOBAL,
						when
				));
				continue;
			}

			throw err(path, lineNo, "Unrecognized: " + line.trim());
		}
		return unit;
	}

	/* ------------ helpers ------------ */

	private static List<String> parseFuncParams(String sig) {
		if (sig == null || sig.isBlank()) return List.of();
		// split top-level commas (you don’t allow types with [] here anyway)
		var names = new ArrayList<String>();
		int depth = 0;
		boolean inStr = false;
		StringBuilder tok = new StringBuilder();
		for (int i = 0; i < sig.length(); i++) {
			char c = sig.charAt(i);
			if (c == '"' && (i == 0 || sig.charAt(i - 1) != '\\')) {
				inStr = !inStr;
			}
			if (!inStr && c == ',' && depth == 0) {
				names.add(tok.toString().trim());
				tok.setLength(0);
				continue;
			}
			tok.append(c);
		}
		if (tok.length() > 0) names.add(tok.toString().trim());

		var out = new ArrayList<String>();
		for (String s : names) {
			if (s.isBlank()) continue;
			int col = s.indexOf(':');
			String n = (col >= 0) ? s.substring(0, col).trim() : s.trim();
			if (!n.isEmpty()) out.add(n);
		}
		return out;
	}

	private static List<String> readBlock(ListIterator<String> it) {
		var out = new ArrayList<String>();
		int depth = 1;
		boolean inStr = false, inSL = false, inML = false;
		while (it.hasNext()) {
			String raw = it.next();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < raw.length(); i++) {
				char c = raw.charAt(i);
				char p = (i > 0) ? raw.charAt(i - 1) : '\0';
				char n = (i + 1 < raw.length()) ? raw.charAt(i + 1) : '\0';

				// string (supports \" escaping)
				if (!inSL && !inML && c == '"' && p != '\\') inStr = !inStr;

				// enter/exit comments when not in string
				if (!inStr) {
					if (!inML && !inSL && c == '/' && n == '/') {
						inSL = true;
						i = raw.length();
						break;
					}
					if (!inML && !inSL && c == '/' && n == '*') {
						inML = true;
						i++;
						continue;
					}
					if (inML && c == '*' && n == '/') {
						inML = false;
						i++;
						continue;
					}
				}

				if (inSL || inML) continue;

				// depth tracking only when not in string/comment
				if (!inStr) {
					if (c == '{') {
						depth++;
					} else if (c == '}') {
						depth--;
						if (depth == 0) { /* drop rest of line */
							break;
						}
					}
				}

				sb.append(c);
			}
			// end of physical line
			inSL = false; // // ends at EOL

			String ln = sb.toString().trim();
			if (depth == 0) break;
			String trimmed = ln.trim();
			if (!trimmed.isBlank() && !trimmed.startsWith("//") && !trimmed.startsWith("#")) {
				out.add(trimmed);
			}
		}
		return out;
	}

	private static List<Stmt> readStmtBlock(ListIterator<String> it) {
		List<String> lines = readBlock(it);
		if (lines.isEmpty()) return List.of();
		String src = String.join("\n", lines);
		Stmt.Block program = Parser.parse(src); // uses your Lexer/Parser
		return program.stmts();
	}

	private static String firstVar(String params) {
		var s = params.strip();
		if (s.isEmpty()) return "";
		int colon = s.indexOf(':');
		if (colon <= 0) return s.split(",")[0].trim();
		return s.substring(0, colon).trim();
	}

	private static List<String> parseAliases(String inside) {
		if (inside == null) return List.of();
		List<String> out = new ArrayList<>();
		String s = inside.trim();
		boolean inStr = false;
		StringBuilder tok = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '"' && (i == 0 || s.charAt(i - 1) != '\\')) {
				inStr = !inStr;
				continue;
			}
			if (c == ',' && !inStr) {
				addAlias(out, tok);
				tok.setLength(0);
				continue;
			}
			tok.append(c);
		}
		addAlias(out, tok);
		return out;
	}

	private static void addAlias(List<String> out, StringBuilder tok) {
		String v = tok.toString().trim();
		if (v.startsWith("\"") && v.endsWith("\"") && v.length() >= 2) v = v.substring(1, v.length() - 1);
		v = v.replace("\\\"", "\"");
		if (!v.isBlank()) out.add(v);
	}

	private static Duration dur(String lit) {
		long ms = Math.max(0L, parseDurTicks(lit) * 50L);
		return Duration.ofMillis(ms);
	}

	private static int parseDurTicks(String lit) {
		String s = lit.trim().toLowerCase(Locale.ROOT);
		if (s.matches("^\\d+$")) return Integer.parseInt(s); // ticks
		int ticks = 0;
		Matcher m = Pattern.compile("(\\d+(?:\\.\\d+)?)(ms|s|m|h|d|t)").matcher(s);
		int pos = 0;
		while (m.find(pos)) {
			double v = Double.parseDouble(m.group(1));
			String u = m.group(2);
			int t = switch (u) {
				case "ms" -> (int) Math.round(v / 50.0);
				case "s" -> (int) Math.round(v * 20);
				case "m" -> (int) Math.round(v * 20 * 60);
				case "h" -> (int) Math.round(v * 20 * 60 * 60);
				case "d" -> (int) Math.round(v * 20 * 60 * 60 * 24);
				case "t" -> (int) Math.round(v);
				default -> 0;
			};
			ticks += t;
			pos = m.end();
		}
		return (ticks > 0) ? ticks : 0;
	}

	private static RuntimeException err(Path p, int line, String m) {
		return new RuntimeException(p.getFileName() + ":" + line + ": " + m);
	}

	/* ------------ param parsing ------------ */

	// Supports: name: Int = 5, message: Text..., kind: Enum["a","b"] = "a", time: Duration = 30s,
	// sender: Player, targets: Entities, etc.
	// ObsParser.parseParams(...) — drop-in replacement
	private static List<ScriptCommand.Param> parseParams(String sig){
		var out = new ArrayList<ScriptCommand.Param>();
		if (sig == null || sig.isBlank()) return out;

		// split by commas not inside [] or quotes
		int depthSq=0; boolean inStr=false; StringBuilder tok=new StringBuilder();
		List<String> parts = new ArrayList<>();
		for (int i=0;i<sig.length();i++){
			char c = sig.charAt(i);
			if (c=='"' && (i==0 || sig.charAt(i-1)!='\\')) { inStr = !inStr; }
			if (!inStr) {
				if (c=='[') depthSq++;
				else if (c==']') depthSq--;
				else if (c==',' && depthSq==0) { parts.add(tok.toString().trim()); tok.setLength(0); continue; }
			}
			tok.append(c);
		}
		if (tok.length()>0) parts.add(tok.toString().trim());

		for (String raw : parts) {
			if (raw.isBlank()) continue;

			// Optional wrapper: [ ... ] at top level
			String p = raw.trim();
			boolean wrapOptional = p.startsWith("[") && p.endsWith("]");
			if (wrapOptional) p = p.substring(1, p.length()-1).trim();

			// name : rest
			int colon = p.indexOf(':');
			if (colon < 0) {
				// bare word -> Word
				String name = p.replace("?", "").trim();
				boolean explicitOptional = p.endsWith("?");
				out.add(new ScriptCommand.Param(name, ScriptCommand.Kind.WORD, !explicitOptional, null));
				continue;
			}

			String name = p.substring(0, colon).trim();
			String rest = p.substring(colon+1).trim();

			// explicit optional on name/type
			boolean nameOpt = name.endsWith("?");
			if (nameOpt) name = name.substring(0, name.length()-1).trim();
			boolean typeOpt = rest.endsWith("?");
			if (typeOpt) rest = rest.substring(0, rest.length()-1).trim();

			// default?
			String def = null;
			int eq = topLevelEq(rest);
			if (eq >= 0) {
				def = rest.substring(eq+1).trim().replaceAll(";$","");
				rest = rest.substring(0, eq).trim();
			}

			// varargs?
			boolean varargs = rest.endsWith("...");
			if (varargs) rest = rest.substring(0, rest.length()-3).trim();

			// kind
			ScriptCommand.Kind kind = switch (rest) {
				case "Int"        -> ScriptCommand.Kind.INTEGER;
				case "Float"      -> ScriptCommand.Kind.FLOAT;
				case "Bool"       -> ScriptCommand.Kind.BOOL;
				case "Word"       -> ScriptCommand.Kind.WORD;
				case "Text"       -> ScriptCommand.Kind.TEXT;
				case "Duration"   -> ScriptCommand.Kind.DURATION;
				case "Time"       -> ScriptCommand.Kind.TIME;
				case "TeamColor"  -> ScriptCommand.Kind.TEAM_COLOR;
				case "HexColor"   -> ScriptCommand.Kind.HEX_COLOR;
				case "Entity"     -> ScriptCommand.Kind.ENTITY;
				case "Entities"   -> ScriptCommand.Kind.ENTITIES;
				case "Player"     -> ScriptCommand.Kind.PLAYER;
				case "Players"    -> ScriptCommand.Kind.PLAYERS;
				case "GameMode"   -> ScriptCommand.Kind.GAME_MODE;
				case "BlockPos"   -> ScriptCommand.Kind.BLOCK_POS;
				case "UUID"       -> ScriptCommand.Kind.UUID;
				case "Rotation"   -> ScriptCommand.Kind.ROTATION;
				case "Angle"      -> ScriptCommand.Kind.ANGLE;
				case "Swizzle"    -> ScriptCommand.Kind.SWIZZLE;
				case "Vec2"       -> ScriptCommand.Kind.VEC2;
				case "Vec3"       -> ScriptCommand.Kind.VEC3;
				default -> {
					if (rest.startsWith("Enum[")) yield ScriptCommand.Kind.ENUM;
					yield ScriptCommand.Kind.WORD;
				}
			};

			boolean hasDefault = (def != null && !def.isBlank());

			// For ENUM: stash options in defaultLiteral: "[a,b]||default"
			String extra = def;
			if (kind == ScriptCommand.Kind.ENUM) {
				int a = rest.indexOf('['), b = rest.lastIndexOf(']');
				String options = (a>=0 && b>a) ? rest.substring(a+1,b) : "";
				extra = "["+options+"]";
				if (def != null && !def.isBlank()) extra = extra + "||" + def;
				def = null;
				hasDefault = extra.contains("||");
			}

			// REQUIRED = not marked optional AND no default AND not varargs AND not wrapped optional
			boolean explicitOptional = wrapOptional || nameOpt || typeOpt || varargs;
			boolean required = !(explicitOptional || hasDefault);

			// Varargs TEXT should be greedy and last; enforce later in Brigadier
			if (varargs && kind == ScriptCommand.Kind.WORD) kind = ScriptCommand.Kind.TEXT;

			out.add(new ScriptCommand.Param(name, kind, required, (kind==ScriptCommand.Kind.ENUM ? extra : def)));
		}

		return out;
	}

	private static int topLevelEq(String s) {
		boolean inStr = false;
		int depthSq = 0;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '"') inStr = !inStr;
			if (inStr) continue;
			if (c == '[') depthSq++;
			else if (c == ']') depthSq--;
			else if (c == '=' && depthSq == 0) return i;
		}
		return -1;
	}
}
