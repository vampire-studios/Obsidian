package io.github.vampirestudios.obsidian.scripting.std;

final class Util {
	static int parseDurTicks(String s) {
		String lit = s.trim();
		int n = 0, i = 0;
		boolean neg = false;
		if (lit.startsWith("-")) {
			neg = true;
			i = 1;
		}
		while (i < lit.length() && Character.isDigit(lit.charAt(i))) {
			n = n * 10 + (lit.charAt(i) - '0');
			i++;
		}
		int sign = neg ? -1 : 1;
		if (i >= lit.length()) return sign * n; // raw ticks
		char u = lit.charAt(i);
		return switch (u) {
			case 's' -> sign * n * 20;
			case 'm' -> sign * n * 20 * 60;
			case 'h' -> sign * n * 20 * 60 * 60;
			default -> sign * n;
		};
	}

	static String template(String s, java.util.Map<String,Object> vars){
		// very small ${name} substitution to preserve your earlier behavior
		StringBuilder out = new StringBuilder();
		for (int i=0; i<s.length();) {
			int a = s.indexOf("${", i);
			if (a < 0) { out.append(s.substring(i)); break; }
			out.append(s, i, a);
			int b = s.indexOf('}', a+2);
			if (b < 0) { out.append(s.substring(a)); break; }
			String key = s.substring(a+2, b).trim();
			Object v = vars.get(key);
			if (v == null && key.endsWith(".name")) {
				Object base = vars.get(key.substring(0, key.length()-5));
				if (base instanceof net.minecraft.server.level.ServerPlayer sp) v = sp.getGameProfile().name();
			}
			out.append(v == null ? "" : v.toString());
			i = b+1;
		}
		return out.toString();
	}
}
