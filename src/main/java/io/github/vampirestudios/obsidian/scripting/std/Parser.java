package io.github.vampirestudios.obsidian.scripting.std;

import io.github.vampirestudios.obsidian.scripting.std.Lexer.K;
import io.github.vampirestudios.obsidian.scripting.std.Lexer.Tok;

import java.util.ArrayList;
import java.util.List;

final class Parser {
	private final List<Tok> t;
	private int i = 0;

	Parser(List<Tok> t) {
		this.t = t;
	}

	/* entry */
	static Stmt.Block parse(String src) {
		var lx = Lexer.lex(src);
		return new Parser(lx).parseFile();
	}

	public static Expr parseExpr(String src) {
		return new Parser(Lexer.lex(src)).parseExpr();
	}

	private static RuntimeException err(String m) {
		return new RuntimeException("Parse: " + m);
	}

	Stmt.Block parseFile() {
		var blk = new Stmt.Block();
		while (!peek(K.EOF)) {
			// NEW: tolerate accidental top-level RBRACE/SEMI (e.g., when a DSL "actions" block leaked a brace)
			if (peek(K.SEMI) || peek(K.RBRACE)) { i++; continue; }

			int before = i;
			blk.stmts().add(parseStmt());
			if (i == before) throw err("Parser made no progress at " + curr().p());
		}
		return blk;
	}

	private Stmt parseStmt() {
		if (peek(K.SEMI)) {
			i++;
			return new Stmt.Block();
		}
		if (peek(K.RBRACE)) throw err("Stray '}' at " + curr().p());
		try {
			if (peek(K.WAIT)) return parseWait();
			if (peek(K.REPEAT)) return parseRepeat();
			if (peek(K.WHILE)) return parseWhileEvery();
			if (peek(K.FOR)) return parseFor();
			if (peek(K.LET)) return parseLet();
			if (peek(K.CONST)) return parseConst();        // <— NEW
			if (peek(K.IF)) return parseIfElse();
			if (peek(K.SWITCH)) return parseSwitch();
			if (peek(K.RETURN)) return parseReturn();
			if (peek(K.TRY)) return parseTryCatch();
			if (peek(K.PERSIST)) return parsePersist();
			if (peek(K.STATE)) return parseStateAssign();
			if (peek(K.CONFIG)) return parseConfigStmt();
			if (peek(K.OBSERVABLE)) return parseObservable();
			if (peek(K.WATCH))  return parseWatch();
			if (peek(K.ID) && t.get(i+1).k()==K.ASSIGN) return parseAssign();
			return parseCallOrFuncStmt();
		} catch (RuntimeException ex) {
			synchronize();
			throw ex;
		}
	}

	private void synchronize() {
		// advance until a safe boundary
		while (!peek(K.EOF) && !peek(K.SEMI) && !peek(K.RBRACE)) i++;
		if (peek(K.SEMI)) i++;
	}

	private Stmt parseWait() {
		need(K.WAIT, "wait"); // wait <duration>;
		var tok = needOneOf("duration", K.DUR, K.NUM, K.ID);
		int ticks = Util.parseDurTicks(tok.s());
		need(K.SEMI, "Expected ';' after wait");
		return new Stmt.Wait(ticks);
	}

	private Stmt parseRepeat() {
		need(K.REPEAT, "repeat");
		int times = (int) Double.parseDouble(need(K.NUM, "repeat N").s());
		var body = parseBlock();
		return new Stmt.Repeat(times, body);
	}

	private Stmt parseWhileEvery() {
		need(K.WHILE, "while");
		need(K.LP, "(");
		int start = i;
		int depth = 1;
		// collect raw lexemes into a string until matching ')'
		StringBuilder pred = new StringBuilder();
		while (depth > 0 && !peek(K.EOF)) {
			var tk = curr();
			if (tk.k() == K.LP) depth++;
			else if (tk.k() == K.RP) {
				depth--;
				if (depth == 0) break;
			}
			pred.append(tk.s());
			i++;
		}
		need(K.RP, ")");
		need(K.EVERY, "every");
		var tok = needOneOf("period", K.DUR, K.NUM, K.ID);
		int ticks = Util.parseDurTicks(tok.s());
		var body = parseBlock();
		return new Stmt.WhileEvery(pred.toString().trim(), ticks, body);
	}

	private Stmt parseFor() {
		need(K.FOR, "for");
		need(K.LP, "(");
		String var = need(K.ID, "variable name").s();
		need(K.IN, "in");
		Expr start = parseExpr();
		need(K.TO, "to");
		Expr end = parseExpr();
		Expr step = null;
		if (peek(K.STEP)) {
			i++;
			step = parseExpr();
		}
		need(K.RP, ")");
		var body = parseBlock();
		return new Stmt.For(var, start, end, step, body);
	}

	private Stmt parseLet() {
		need(K.LET, "let");
		String name = need(K.ID, "variable name").s();
		need(K.ASSIGN, "=");
		Expr value = parseExpr();
		need(K.SEMI, "Expected ';' after let");
		return new Stmt.Let(name, value);
	}

	private Stmt parseConst() {
		need(K.CONST, "const");
		String name = need(K.ID, "constant name").s();
		need(K.ASSIGN, "=");
		Expr value = parseExpr();
		need(K.SEMI, "Expected ';' after const");
		return new Stmt.Const(name, value);
	}

	private Stmt parseIfElse() {
		need(K.IF, "if");
		need(K.LP, "(");
		Expr cond = parseExpr();
		need(K.RP, ")");
		Stmt.Block thenBlk = parseBlock();

		Stmt.Block elseBlk = new Stmt.Block();
		if (peek(K.ELSE)) {
			i++;
			if (peek(K.IF)) {
				// desugar: else if (...) {A} -> else { if (...) {A} }
				Stmt nested = parseIfElse();
				elseBlk.stmts().add(nested);
			} else {
				elseBlk = parseBlock();
			}
		}
		return new Stmt.IfElse(cond, thenBlk, elseBlk);
	}

	private Stmt parseSwitch() {
		need(K.SWITCH, "switch");
		need(K.LP, "(");
		Expr expr = parseExpr();
		need(K.RP, ")");
		need(K.LBRACE, "{");
		List<Expr> caseValues = new ArrayList<>();
		List<Stmt.Block> caseBlocks = new ArrayList<>();
		Stmt.Block defaultBlock = null;
		while (!peek(K.RBRACE)) {
			if (peek(K.EOF)) throw err("Unterminated switch block before EOF at " + curr().p());
			if (peek(K.CASE)) {
				i++;
				Expr value = parsePrimary(); // Restrict to literals (NUM, STR, TRUE, FALSE)
				need(K.COLON, ":");
				Stmt.Block block = parseBlock();
				caseValues.add(value);
				caseBlocks.add(block);
			} else if (peek(K.DEFAULT)) {
				i++;
				need(K.COLON, ":");
				defaultBlock = parseBlock();
			} else {
				throw err("Expected 'case' or 'default' in switch at " + curr().p());
			}
		}
		need(K.RBRACE, "}");
		return new Stmt.Switch(expr, caseValues, caseBlocks, defaultBlock);
	}

	private Stmt parseReturn() {
		need(K.RETURN, "return");
		Expr v = null;
		if (!peek(K.SEMI)) v = parseExpr();
		need(K.SEMI, "Expected ';' after return");
		return new Stmt.Return(v);
	}

	private Stmt parseTryCatch() {
		need(K.TRY, "try");
		var body = parseBlock();
		need(K.CATCH, "catch");
		need(K.LP, "(");
		String name = need(K.ID, "exception name").s();
		need(K.RP, ")");
		var handler = parseBlock();
		return new Stmt.TryCatch(body, name, handler);
	}

	private Stmt parsePersist() {
		need(K.PERSIST, "persist");
		need(K.SEMI, ";");
		return new Stmt.Persist();
	}

	// Syntax: state global.key = <expr>;  |  state player.key = <expr>;
	private Stmt parseStateAssign() {
		need(K.STATE, "state");
		Stmt.StateAssign.Target tgt;
		if (peek(K.GLOBAL)) {
			i++;
			tgt = Stmt.StateAssign.Target.GLOBAL;
		} else if (peek(K.PLAYER)) {
			i++;
			tgt = Stmt.StateAssign.Target.PLAYER;
		} else throw err("Expected 'global' or 'player'");
		need(K.DOT, ".");
		String key = need(K.ID, "state key").s();
		need(K.ASSIGN, "=");
		Expr val = parseExpr();
		need(K.SEMI, ";");
		return new Stmt.StateAssign(tgt, key, val);
	}

	private Stmt parseConfigStmt() {
		need(K.CONFIG, "config");
		if (peek(K.DOT)) i++;
		String op = need(K.ID, "config op").s(); // "read" | "write"
		if (op.equals("read")) {
			String file = stripQuotes(need(K.STR, "filename").s());
			need(K.AS, "as");
			String var = need(K.ID, "variable").s();
			need(K.SEMI, ";");
			return new Stmt.ConfigRead(var, file);
		} else if (op.equals("write")) {
			String file = stripQuotes(need(K.STR, "filename").s());
			need(K.COMMA, ",");
			Expr e = parseExpr();
			need(K.SEMI, ";");
			return new Stmt.ConfigWrite(file, e);
		}
		throw err("Unknown config op: " + op);
	}

	private Stmt parseObservable() {
		need(K.OBSERVABLE, "observable");
		String name = need(K.ID, "observable name").s();
		Expr init = new Expr.Num(0); // default 0
		if (peek(K.ASSIGN)) { i++; init = parseExpr(); }
		need(K.SEMI, "Expected ';' after observable");
		return new Stmt.Observable(name, init);
	}

	private Stmt parseWatch() {
		need(K.WATCH, "watch(");
		need(K.LP, "(");
		String name = need(K.ID, "observable name").s();
		need(K.RP, ")");
		Stmt.Block body = parseBlock();
		return new Stmt.Watch(name, body);
	}

	private Stmt parseAssign() {
		String name = need(K.ID, "variable").s();
		need(K.ASSIGN, "=");
		Expr value = parseExpr();
		need(K.SEMI, "Expected ';' after assignment");
		return new Stmt.Assign(name, value);
	}

	private Stmt.Block parseBlock() {
		need(K.LBRACE, "{");
		var blk = new Stmt.Block();
		while (!peek(K.RBRACE)) {
			if (peek(K.EOF)) throw err("Unterminated block before EOF at " + curr().p());
			blk.stmts().add(parseStmt());
		}
		need(K.RBRACE, "}");
		return blk;
	}

	private Stmt parseCallOrFuncStmt() {
		// function call: ID '(' args ')' ';'
		if (peek2(K.ID, K.LP)) {
			String name = need(K.ID, "function").s();
			need(K.LP, "(");
			List<Expr> args = new ArrayList<>();
			if (!peek(K.RP)) {
				args.add(parseExpr());
				while (peek(K.COMMA)) { i++; if (peek(K.RP)) break; args.add(parseExpr()); }
			}
			need(K.RP, ")");
			need(K.SEMI, "Expected ';' after function call");
			return new Stmt.FuncCall(name, args);
		}

		// method chain: receiver[.segment[...]] [block]?;
		if (!peek(K.ID)) {
			throw err("Expected a statement starting with an identifier, got " + curr().k() + " at " + curr().p());
		}
		var chain = parseCallChain();
		if (peek(K.LBRACE)) return new Stmt.CallWithBlock(chain, parseBlock());
		need(K.SEMI, "Expected ';' after call");
		return new Stmt.Call(chain);
	}

	private CallChain parseCallChain() {
		String base = need(K.ID, "receiver").s();
		List<CallChain.Segment> segs = new ArrayList<>();

		// zero or more segments; supports property-like ".gui"
		while (peek(K.DOT)) {
			i++; // consume '.'
			String name = need(K.ID, "method").s();

			// args are optional: ".gui" is allowed; ".open(...)" too
			List<Expr> args = new ArrayList<>();
			if (peek(K.LP)) {
				i++; // '('
				if (!peek(K.RP)) {
					args.add(parseExpr());
					while (peek(K.COMMA)) {
						i++;
						if (peek(K.RP)) break; // tolerate trailing comma
						args.add(parseExpr());
					}
				}
				need(K.RP, ")");
			}
			segs.add(new CallChain.Segment(name, args));
		}

		// If no ".segment" was found, this wasn't a method-chain; reject here.
		if (segs.isEmpty()) {
			throw err("Expected method call after receiver '" + base + "', missing '.' before " + curr().k() + " at " + curr().p());
		}
		return new CallChain(base, segs);
	}

	/* ===== expressions (precedence climbing) ===== */
	private Expr parseExpr() {
		return parseOr();
	}

	private Expr parseOr() {
		Expr e = parseAnd();
		while (peek(K.OROR)) {
			String op = curr().s();
			i++;
			e = new Expr.Binary(e, op, parseAnd());
		}
		return e;
	}

	private Expr parseAnd() {
		Expr e = parseEquality();
		while (peek(K.ANDAND)) {
			String op = curr().s();
			i++;
			e = new Expr.Binary(e, op, parseEquality());
		}
		return e;
	}

	private Expr parseEquality() {
		Expr e = parseComparison();
		while (peek(K.EQ) || peek(K.NEQ)) {
			String op = curr().s();
			i++;
			e = new Expr.Binary(e, op, parseComparison());
		}
		return e;
	}

	private Expr parseComparison() {
		Expr e = parseTerm();
		while (peek(K.LT) || peek(K.LTE) || peek(K.GT) || peek(K.GTE)) {
			String op = curr().s();
			i++;
			e = new Expr.Binary(e, op, parseTerm());
		}
		return e;
	}

	private Expr parseTerm() {
		Expr e = parseFactor();
		while (peek(K.PLUS) || peek(K.MINUS)) {
			String op = curr().s();
			i++;
			e = new Expr.Binary(e, op, parseFactor());
		}
		return e;
	}

	private Expr parseFactor() {
		Expr e = parseUnary();
		while (peek(K.STAR) || peek(K.SLASH) || peek(K.PERCENT)) {
			String op = curr().s();
			i++;
			e = new Expr.Binary(e, op, parseUnary());
		}
		return e;
	}

	private Expr parseUnary() {
		if (peek(K.BANG) || peek(K.MINUS)) {
			String op = curr().s();
			i++;
			return new Expr.Unary(op, parseUnary());
		}
		return parsePrimary();
	}

	private Expr parsePrimary() {
		if (peek(K.NUM)) return new Expr.Num(Double.parseDouble(need(K.NUM, "num").s()));
		if (peek(K.STR)) return new Expr.Str(need(K.STR, "str").s());
		if (peek(K.TRUE)) {
			i++;
			return new Expr.Bool(true);
		}
		if (peek(K.FALSE)) {
			i++;
			return new Expr.Bool(false);
		}
		if (peek(K.LP)) {
			i++;
			Expr e = parseExpr();
			need(K.RP, ")");
			return new Expr.Group(e);
		}
		if (peek(K.LBRACE)) return parseObj();
		if (peek(K.LSQUARE)) return parseArr();
		return new Expr.Var(need(K.ID, "ident").s());
	}

	private Expr parseObj() {
		need(K.LBRACE, "{");
		var map = new java.util.LinkedHashMap<String, Expr>();
		if (!peek(K.RBRACE)) {
			while (true) {
				String key;
				if (peek(K.STR)) key = need(K.STR, "key").s();
				else key = need(K.ID, "key").s(); // allow bare keys
				need(K.COLON, ":");
				Expr val = parseExpr();
				map.put(key, val);
				if (peek(K.COMMA)) {
					i++;
					if (peek(K.RBRACE)) break;
				} else break; // allow trailing comma
			}
		}
		need(K.RBRACE, "}");
		return new Expr.Obj(map);
	}

	private Expr parseArr() {
		need(K.LSQUARE, "[");
		var list = new java.util.ArrayList<Expr>();
		if (!peek(K.RSQUARE)) {
			while (true) {
				list.add(parseExpr());
				if (peek(K.COMMA)) {
					i++;
					if (peek(K.RSQUARE)) break;
				} else break;
			}
		}
		need(K.RSQUARE, "]");
		return new Expr.Arr(list);
	}

	/* ===== utils ===== */
	private Tok curr() {
		return t.get(i);
	}

	private boolean peek(K k) {
		return curr().k() == k;
	}

	private boolean peek2(K k1, K k2) {
		if (!peek(k1)) return false;
		if (i + 1 >= t.size()) return false;
		return t.get(i + 1).k() == k2;
	}

	private Tok need(K k, String msg) {
		if (!peek(k)) {
			var got = curr();
			throw err(String.format("Expected %s (%s), got %s at %s", k, msg, got.k(), got.p()));
		}
		return t.get(i++);
	}

	private boolean match(K... ks) {
		for (K k : ks)
			if (peek(k)) {
				i++;
				return true;
			}
		return false;
	}

	private Tok consume(K k, String msg) {
		return need(k, msg);
	}

	private Tok needOneOf(String msg, K... ks) {
		for (K k : ks) if (peek(k)) return t.get(i++);
		throw err("Expected " + msg + " at " + curr().p());
	}

	private String stripQuotes(String s) {
		if (s == null) return null;
		String t = s.trim();
		if (t.length() >= 2 && t.startsWith("\"") && t.endsWith("\"")) {
			return t.substring(1, t.length() - 1);
		}
		return t;
	}
}
