package io.github.vampirestudios.obsidian.scripting.std;

import java.util.ArrayList;
import java.util.List;

final class Lexer {
	static List<Tok> lex(String src) {
		List<Tok> out = new ArrayList<>();
		int i = 0, n = src.length();

		// strip BOM
		if (n > 0 && src.charAt(0) == '\uFEFF') {
			i++;
		}

		while (i < n) {
			char c = src.charAt(i);

			// whitespace
			if (Character.isWhitespace(c)) {
				i++;
				continue;
			}

			// line & block comments
			if (c == '/') {
				if (i + 1 < n) {
					char n1 = src.charAt(i + 1);
					if (n1 == '/') {                // // ...
						i += 2;
						while (i < n && src.charAt(i) != '\n') i++;
						continue;
					}
					if (n1 == '*') {                // /* ... */
						i += 2;
						while (i < n - 1) {
							if (src.charAt(i) == '*' && src.charAt(i + 1) == '/') {
								i += 2;
								break;
							}
							i++;
						}
						continue;
					}
				}
			}

			switch (c) {
				case '.' -> {
					out.add(new Tok(K.DOT, ".", i++));
				}
				case '(' -> {
					out.add(new Tok(K.LP, "(", i++));
				}
				case ')' -> {
					out.add(new Tok(K.RP, ")", i++));
				}
				case '{' -> {
					out.add(new Tok(K.LBRACE, "{", i++));
				}
				case ':' -> {
					out.add(new Tok(K.COLON, ":", i++));
				}
				case '[' -> {
					out.add(new Tok(K.LSQUARE, "[", i++));
				}
				case ']' -> {
					out.add(new Tok(K.RSQUARE, "]", i++));
				}
				case '}' -> {
					out.add(new Tok(K.RBRACE, "}", i++));
				}
				case ',' -> {
					out.add(new Tok(K.COMMA, ",", i++));
				}
				case ';' -> {
					out.add(new Tok(K.SEMI, ";", i++));
				}
				case '+' -> {
					out.add(new Tok(K.PLUS, "+", i++));
				}
				case '-' -> {
					out.add(new Tok(K.MINUS, "-", i++));
				}
				case '*' -> {
					out.add(new Tok(K.STAR, "*", i++));
				}
				case '/' -> {
					out.add(new Tok(K.SLASH, "/", i++));
				}
				case '%' -> {
					out.add(new Tok(K.PERCENT, "%", i++));
				}
				case '!' -> {
					if (i + 1 < n && src.charAt(i + 1) == '=') {
						out.add(new Tok(K.NEQ, "!=", i));
						i += 2;
					} else {
						out.add(new Tok(K.BANG, "!", i++));
					}
				}
				case '=' -> {
					if (i + 1 < n && src.charAt(i + 1) == '=') {
						out.add(new Tok(K.EQ, "==", i));
						i += 2;
					} else {
						out.add(new Tok(K.ASSIGN, "=", i++));
					}
				}
				case '<' -> {
					if (i + 1 < n && src.charAt(i + 1) == '=') {
						out.add(new Tok(K.LTE, "<=", i));
						i += 2;
					} else {
						out.add(new Tok(K.LT, "<", i++));
					}
				}
				case '>' -> {
					if (i + 1 < n && src.charAt(i + 1) == '=') {
						out.add(new Tok(K.GTE, ">=", i));
						i += 2;
					} else {
						out.add(new Tok(K.GT, ">", i++));
					}
				}
				case '&' -> {
					if (i + 1 < n && src.charAt(i + 1) == '&') {
						out.add(new Tok(K.ANDAND, "&&", i));
						i += 2;
					} else throw err(i, "'&'");
				}
				case '|' -> {
					if (i + 1 < n && src.charAt(i + 1) == '|') {
						out.add(new Tok(K.OROR, "||", i));
						i += 2;
					} else throw err(i, "'|'");
				}
				case '"' -> { // string with escapes
					int start = i;
					int j = ++i;
					StringBuilder b = new StringBuilder();
					while (j < n) {
						char d = src.charAt(j);
						if (d == '\\' && j + 1 < n) {
							char e = src.charAt(j + 1);
							switch (e) {
								case 'n' -> b.append('\n');
								case 'r' -> b.append('\r');
								case 't' -> b.append('\t');
								case '"' -> b.append('"');
								case '\\' -> b.append('\\');
								default -> b.append(e); // lenient
							}
							j += 2;
							continue;
						}
						if (d == '"') break;
						b.append(d);
						j++;
					}
					if (j >= n) throw err(start, "Unterminated string");
					out.add(new Tok(K.STR, b.toString(), start));
					i = j + 1;
				}
				default -> {
					if (Character.isDigit(c)) {
						// number or duration literal
						int j = i;
						boolean seenDot = false;
						// integer / fraction
						while (j < n) {
							char d = src.charAt(j);
							if (Character.isDigit(d)) {
								j++;
								continue;
							}
							if (d == '.' && !seenDot && j + 1 < n && Character.isDigit(src.charAt(j + 1))) {
								seenDot = true;
								j++;
								continue;
							}
							break;
						}
						// exponent part (optional)
						int k = j;
						if (k < n && (src.charAt(k) == 'e' || src.charAt(k) == 'E')) {
							int k2 = k + 1;
							if (k2 < n && (src.charAt(k2) == '+' || src.charAt(k2) == '-')) k2++;
							if (k2 < n && Character.isDigit(src.charAt(k2))) {
								k = k2 + 1;
								while (k < n && Character.isDigit(src.charAt(k))) k++;
								j = k;
							}
						}
						// duration suffix?
						int uStart = j;
						String unit = null;
						if (uStart < n) {
							// longest wins: ms, then single-char units
							if (uStart + 1 < n && (src.charAt(uStart) == 'm') && (src.charAt(uStart + 1) == 's')) {
								unit = "ms";
								j = uStart + 2;
							} else {
								char u = src.charAt(uStart);
								if (u == 's' || u == 'm' || u == 'h' || u == 'd' || u == 't') {
									unit = String.valueOf(u);
									j = uStart + 1;
								}
							}
						}

						String numLex = src.substring(i, j);
						if (unit != null) {
							out.add(new Tok(K.DUR, numLex + unit, i));
						} else {
							out.add(new Tok(K.NUM, src.substring(i, j), i));
						}
						i = j;
						continue;
					}

					// identifier: allow letters, digits after first, '_', ':', '-', '/'
					if (Character.isLetter(c) || c == '_') {
						int j = i + 1;
						while (j < n) {
							char d = src.charAt(j);
							if (Character.isLetterOrDigit(d) || d == '_' || d == ':' || d == '-' || d == '/')
								j++;
							else break;
						}
						String w = src.substring(i, j);
						K k = switch (w) {
							case "true" -> K.TRUE;
							case "false" -> K.FALSE;
							case "every" -> K.EVERY;
							case "while" -> K.WHILE;
							case "repeat" -> K.REPEAT;
							case "observable" -> K.OBSERVABLE;
							case "watch" -> K.WATCH;
							case "wait" -> K.WAIT;
							case "let" -> K.LET;
							case "const" -> K.CONST;
							case "if" -> K.IF;
							case "else" -> K.ELSE;
							case "for" -> K.FOR;
							case "in" -> K.IN;
							case "to" -> K.TO;
							case "step" -> K.STEP;
							case "switch" -> K.SWITCH;
							case "case" -> K.CASE;
							case "default" -> K.DEFAULT;
							// Optional readable ops:
							case "and" -> K.ANDAND;
							case "or" -> K.OROR;
							case "not" -> K.BANG;
							default -> K.ID;
						};
						out.add(new Tok(k, w, i));
						i = j;
						continue;
					}

					throw err(i, "Unexpected char: " + c);
				}
			}
		}
		out.add(new Tok(K.EOF, "", n));
		return out;
	}

	static RuntimeException err(int pos, String msg) {
		return new RuntimeException("Lex:[" + pos + "]: " + msg);
	}

	enum K {
		ID, NUM, STR, DUR, RETURN, TRY, CATCH, PERSIST, STATE,
		TRUE, FALSE, EVERY, WHILE, REPEAT, WAIT, LET, IF, ELSE,
		DOT, LP, RP, LBRACE, RBRACE, COMMA, SEMI,
		PLUS, MINUS, STAR, SLASH, PERCENT,
		BANG, ASSIGN,     // ! =
		EQ, NEQ, LT, LTE, GT, GTE, ANDAND, OROR,
		EOF, GLOBAL, PLAYER, CONFIG, AS, SWITCH, CASE, COLON,
		DEFAULT, FOR, IN, TO, STEP, LSQUARE, RSQUARE, OBSERVABLE, WATCH, CONST
	}

	record Tok(K k, String s, int p) {
	}
}
