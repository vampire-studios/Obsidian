package io.github.vampirestudios.obsidian.scripting.std;

import com.google.gson.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.BlockPositionSource;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.BaseGson.GSON;

public final class ScriptUtils {
	private ScriptUtils() {
	} // Prevent instantiation


	/** Convert arbitrary DSL value (string/number/boolean/list/map) into a JsonElement */
	public static JsonElement toJsonElement(Object val) {
		switch (val) {
			case null -> {
				return JsonNull.INSTANCE;
			}
			case JsonElement je -> {
				return je;
			}
			case String s -> new JsonPrimitive(s);
			case Number n -> new JsonPrimitive(n);
			case Boolean b -> {
				return new JsonPrimitive(b);
			}
			case List<?> list -> {
				JsonArray arr = new JsonArray();
				for (Object o : list) arr.add(toJsonElement(o));
				return arr;
			}
			case Map<?, ?> map -> {
				JsonObject obj = new JsonObject();
				for (Map.Entry<?, ?> e : map.entrySet()) {
					obj.add(String.valueOf(e.getKey()), toJsonElement(e.getValue()));
				}
				return obj;
			}
			default -> {
			}
		}

		// fallback: just serialize via Gson
		return GSON.toJsonTree(val);
	}

	public static Object eval(Expr e, Map<String, Object> vars) {
		if (e instanceof Expr.Num(double v)) return v;
		if (e instanceof Expr.Str(String v)) return v;
		if (e instanceof Expr.Bool(boolean v)) return v;
		if (e instanceof Expr.Group(Expr inner)) return eval(inner, vars);
		if (e instanceof Expr.Obj(var entries)) {
			var out = new java.util.LinkedHashMap<String, Object>();
			for (var en : entries.entrySet()) out.put(en.getKey(), eval(en.getValue(), vars));
			return out;
		}
		if (e instanceof Expr.Arr(var values)) {
			var out = new java.util.ArrayList<Object>(values.size());
			for (var v : values) out.add(eval(v, vars));
			return out;
		}
		if (e instanceof Expr.Var(String name)) {
			return switch (name) {
				case "global" -> ObsInterpreter.globals(); // allow global.someKey via future dot-access
				case "playerState" -> ObsInterpreter.playerState(vars);
				default -> vars.getOrDefault(name, 0);
			};
		}

		if (e instanceof Expr.Unary(String op, Expr right)) {
			Object r = eval(right, vars);
			return switch (op) {
				case "-" -> -asNum(r);
				case "!" -> !truthy(r);
				default -> 0;
			};
		}
		if (e instanceof Expr.Binary(Expr left, String op, Expr right)) {
			Object L = eval(left, vars);
			Object R = eval(right, vars);
			return switch (op) {
				case "+" -> (isStringy(L) || isStringy(R)) ? asStr(L) + asStr(R) : asNum(L) + asNum(R);
				case "-" -> asNum(L) - asNum(R);
				case "*" -> asNum(L) * asNum(R);
				case "/" -> asNum(L) / asNum(R);
				case "%" -> asNum(L) % asNum(R);
				case "==" -> eq(L, R);
				case "!=" -> !eq(L, R);
				case "<" -> asNum(L) < asNum(R);
				case "<=" -> asNum(L) <= asNum(R);
				case ">" -> asNum(L) > asNum(R);
				case ">=" -> asNum(L) >= asNum(R);
				case "&&" -> truthy(L) && truthy(R);
				case "||" -> truthy(L) || truthy(R);
				default -> 0;
			};
		}
		return 0;
	}

	public static double asNum(Object o) {
		if (o instanceof Number n) return n.doubleValue();
		try {
			return Double.parseDouble(String.valueOf(o));
		} catch (Exception e) {
			return 0;
		}
	}

	public static int asInt(Object o) {
		if (o instanceof Number n) return n.intValue();
		try {
			return Integer.parseInt(String.valueOf(o));
		} catch (Exception e) {
			return 0;
		}
	}

	public static String asStr(Object o) {
		return o == null ? "" : String.valueOf(o);
	}

	public static boolean truthy(Object o) {
		if (o instanceof Boolean b) return b;
		if (o instanceof Number n) return n.doubleValue() != 0.0;
		if (o instanceof String s) return !s.isEmpty();
		return o != null;
	}

	public static boolean isStringy(Object o) {
		return (o instanceof String);
	}

	public static boolean eq(Object a, Object b) {
		if (a instanceof Number || b instanceof Number) return asNum(a) == asNum(b);
		return Objects.equals(asStr(a), asStr(b));
	}

	public static String getStringArg(CallChain.Segment segment, int index, Map<String, Object> vars) {
		return segment.args().size() > index ? asStr(eval(segment.args().get(index), vars)) : "";
	}

	public static double getNumberArg(CallChain.Segment segment, int index, Map<String, Object> vars) {
		return segment.args().size() > index ? asNum(eval(segment.args().get(index), vars)) : 0.0;
	}

	public static boolean getBooleanArg(CallChain.Segment segment, int index, Map<String, Object> vars) {
		return segment.args().size() > index ? truthy(eval(segment.args().get(index), vars)) : false;
	}

	public static int clamp(int v, int lo, int hi) {
		return Math.max(lo, Math.min(hi, v));
	}

	public static Block getBlock(String id) {
		Identifier loc = Identifier.tryParse(id);
		if (loc == null) {
			throw new IllegalArgumentException("Invalid block ID: " + id);
		}
		return BuiltInRegistries.BLOCK.getValue(loc);
	}

	public static Item getItem(String id) {
		Identifier loc = Identifier.tryParse(id);
		if (loc == null) {
			throw new IllegalArgumentException("Invalid item ID: " + id);
		}
		return BuiltInRegistries.ITEM.getValue(loc);
	}

	public static String insideString(String call) {
		int a = call.indexOf('"'), b = call.lastIndexOf('"');
		return (a >= 0 && b > a) ? call.substring(a + 1, b) : "";
	}

	public static ParticleOptions getParticleOptions(String particleId, CallChain.Segment segment, Map<String, Object> vars) {
		Identifier loc = Identifier.tryParse(particleId);
		if (loc == null) {
			throw new IllegalArgumentException("Invalid particle ID: " + particleId);
		}
		ParticleType<?> type = BuiltInRegistries.PARTICLE_TYPE.getValue(loc);
		if (type == null) {
			throw new IllegalArgumentException("Unknown particle type: " + particleId);
		}

		if (type instanceof SimpleParticleType simpleType) {
			return simpleType;
		} else if (type == ParticleTypes.BLOCK || type == ParticleTypes.BLOCK_MARKER || type == ParticleTypes.FALLING_DUST) {
			if (segment.args().size() < 9) {
				throw new IllegalArgumentException("Block particle requires block ID as 9th argument");
			}
			String blockId = ScriptUtils.getStringArg(segment, 8, vars);
			Block block = getBlock(blockId);
			return new BlockParticleOption((ParticleType<BlockParticleOption>) type, block.defaultBlockState());
		} else if (type == ParticleTypes.ITEM) {
			if (segment.args().size() < 9) {
				throw new IllegalArgumentException("Item particle requires item ID as 9th argument");
			}
			String itemId = ScriptUtils.getStringArg(segment, 8, vars);
			Item item = getItem(itemId);
			return new ItemParticleOption((ParticleType<ItemParticleOption>) type, item);
		} else if (type == ParticleTypes.DUST) {
			if (segment.args().size() == 10) {
				int color = (int) ScriptUtils.getNumberArg(segment, 8, vars);
				float scale = (float) ScriptUtils.getNumberArg(segment, 9, vars);
				return new DustParticleOptions(color, scale);
			} else if (segment.args().size() >= 12) {
				float red = (float) ScriptUtils.getNumberArg(segment, 8, vars);
				float green = (float) ScriptUtils.getNumberArg(segment, 9, vars);
				float blue = (float) ScriptUtils.getNumberArg(segment, 10, vars);
				float scale = (float) ScriptUtils.getNumberArg(segment, 11, vars);
				int color = rgbToInt(red, green, blue);
				return new DustParticleOptions(color, scale);
			} else {
				throw new IllegalArgumentException("Dust particle requires red, green, blue, and scale as 9th to 12th arguments, or color int and scale as 9th to 10th arguments");
			}
		} else if (type == ParticleTypes.VIBRATION) {
			if (segment.args().size() < 12) {
				throw new IllegalArgumentException("Vibration particle requires destination x, y, z, and arrival time as 9th to 12th arguments");
			}
			double destX = ScriptUtils.getNumberArg(segment, 8, vars);
			double destY = ScriptUtils.getNumberArg(segment, 9, vars);
			double destZ = ScriptUtils.getNumberArg(segment, 10, vars);
			int arrivalTime = (int) ScriptUtils.getNumberArg(segment, 11, vars);
			BlockPos destPos = new BlockPos((int) destX, (int) destY, (int) destZ);
			return new VibrationParticleOption(new BlockPositionSource(destPos), arrivalTime);
		} else if (type == ParticleTypes.SCULK_CHARGE) {
			if (segment.args().size() < 9) {
				throw new IllegalArgumentException("Sculk charge particle requires roll angle as 9th argument");
			}
			float roll = (float) ScriptUtils.getNumberArg(segment, 8, vars);
			return new SculkChargeParticleOptions(roll);
		} else if (type == ParticleTypes.SHRIEK) {
			if (segment.args().size() < 9) {
				throw new IllegalArgumentException("Shriek particle requires delay as 9th argument");
			}
			int delay = (int) ScriptUtils.getNumberArg(segment, 8, vars);
			return new ShriekParticleOption(delay);
		} else {
			throw new IllegalArgumentException("Unsupported particle type: " + particleId);
		}
	}

	public static int rgbToInt(float r, float g, float b) {
		// Clamp RGB values to [0, 1]
		r = Math.max(0.0f, Math.min(1.0f, r));
		g = Math.max(0.0f, Math.min(1.0f, g));
		b = Math.max(0.0f, Math.min(1.0f, b));
		// Scale to [0, 255] and convert to integers
		int red = (int) (r * 255.0f);
		int green = (int) (g * 255.0f);
		int blue = (int) (b * 255.0f);
		// Pack into an integer (0xRRGGBB)
		return (red << 16) | (green << 8) | blue;
	}

	public static final java.util.regex.Pattern CMP =
			java.util.regex.Pattern.compile("^(player\\.(?:x|y|z|health|level|food|saturation))\\s*(<=|>=|==|!=|<|>)\\s*([0-9.]+)$");

	public static boolean cmp(double a, String op, double b) {
		return switch (op) {
			case "==" -> a == b;
			case "!=" -> a != b;
			case "<" -> a < b;
			case "<=" -> a <= b;
			case ">" -> a > b;
			case ">=" -> a >= b;
			default -> false;
		};
	}

	public static java.util.List<String> insideArgs(String call) {
		// returns stringy args: accepts both quoted and raw numbers
		int a = call.indexOf('('), b = call.lastIndexOf(')');
		if (a < 0 || b <= a) return java.util.List.of();
		String s = call.substring(a + 1, b).trim();
		java.util.List<String> out = new java.util.ArrayList<>();
		int depth = 0;
		boolean inStr = false;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '"') {
				inStr = !inStr;
				continue;
			} // drop quotes
			if (!inStr) {
				if (c == '(') depth++;
				else if (c == ')') depth--;
				else if (c == ',' && depth == 0) {
					out.add(sb.toString().trim());
					sb.setLength(0);
					continue;
				}
			}
			sb.append(c);
		}
		if (sb.length() > 0) out.add(sb.toString().trim());
		return out;
	}

	public static String stripOuterParens(String s) {
		s = s.trim();
		if (s.startsWith("(") && s.endsWith(")")) {
			int depth = 0;
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (c == '(') depth++;
				else if (c == ')') {
					depth--;
					if (depth == 0 && i < s.length() - 1) return s;
				}
			}
			// if we finished with depth==0 at the last char, it was fully wrapped
			return stripOuterParens(s.substring(1, s.length() - 1));
		}
		return s;
	}

	public static int indexOfTopLevel(String s, String needle) {
		int depth = 0;
		boolean inStr = false;
		for (int i = 0; i <= s.length() - needle.length(); i++) {
			char c = s.charAt(i);
			if (c == '"') inStr = !inStr;
			if (!inStr) {
				if (c == '(') depth++;
				else if (c == ')') depth--;
				if (depth == 0 && s.startsWith(needle, i)) return i;
			}
		}
		return -1;
	}

	public static double parseDouble(String lit) {
		try {
			return Double.parseDouble(lit.trim());
		} catch (Exception e) {
			return 0.0;
		}
	}

	// Accepts "#ns:id" or "ns:id"; returns Identifier
	public static Identifier rlFromHash(String s) {
		String t = s.trim();
		if (t.startsWith("#")) t = t.substring(1).trim();
		return Identifier.parse(t);
	}

	public static boolean isStringLike(Object arg, Map<String, Object> vars) {
		switch (arg) {
			case null -> {
				return false;
			}
			case Expr.Str _ -> {
				return true;
			}
			case Expr e -> {
				Object v = eval(e, vars);
				return v instanceof String;
			}
			default -> {
			}
		}
		return arg instanceof String;
	}

	public static boolean isNumericLike(Object arg, Map<String, Object> vars) {
		if (arg instanceof Expr.Num) return true;
		if (arg instanceof Expr e) {
			Object v = eval(e, vars);
			return (v instanceof Number);
		}
		return arg instanceof Number;
	}
}