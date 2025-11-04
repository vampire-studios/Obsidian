package io.github.vampirestudios.obsidian.scripting.std;

import java.util.ArrayList;
import java.util.List;

sealed interface Stmt permits Stmt.Call, Stmt.CallWithBlock, Stmt.Wait, Stmt.Repeat, Stmt.WhileEvery, Stmt.Block, Stmt.Let,
		Stmt.IfElse, Stmt.FuncCall, Stmt.Return, Stmt.TryCatch, Stmt.Persist, Stmt.StateAssign, Stmt.ConfigRead,
		Stmt.ConfigWrite, Stmt.For, Stmt.Switch, Stmt.Assign, Stmt.Observable, Stmt.Watch, Stmt.Const {
	enum Target {GLOBAL, PLAYER}

	record Call(CallChain chain) implements Stmt {
	}

	record CallWithBlock(CallChain chain, Block body) implements Stmt {
	}

	record Wait(int ticks) implements Stmt {
	}

	record Repeat(int times, Block body) implements Stmt {
	}

	record WhileEvery(String predicate, int periodTicks, Block body) implements Stmt {
	}

	record Block(List<Stmt> stmts) implements Stmt {
		public Block() {
			this(new ArrayList<>());
		}
	}

	record For(String var, Expr start, Expr end, Expr step, Block body) implements Stmt {
	}

	record Let(String name, Expr value) implements Stmt {
	}

	record Const(String name, Expr value) implements Stmt {
	}

	record IfElse(Expr condition, Block thenBlock, Block elseBlock) implements Stmt {
	}

	record Switch(Expr expr, List<Expr> caseValues, List<Block> caseBlocks, Block defaultBlock) implements Stmt {
		public Switch {
			caseValues = List.copyOf(caseValues); // Ensure immutability
			caseBlocks = List.copyOf(caseBlocks); // Ensure immutability
		}
	}

	record FuncCall(String name, List<Expr> args) implements Stmt {
	}

	record Return(@org.jetbrains.annotations.Nullable Expr value) implements Stmt {
	}

	record TryCatch(Stmt.Block body, String name, Stmt.Block handler) implements Stmt {
	}

	record Persist() implements Stmt {
	}

	record StateAssign(Target target, String key, Expr value) implements Stmt {
	}

	record ConfigRead(String varName, String file) implements Stmt {
	}

	record ConfigWrite(String file, Expr expr) implements Stmt {
	}

	record Assign(String name, Expr value) implements Stmt {
	}

	record Observable(String name, Expr init) implements Stmt {
	}

	record Watch(String name, Block body) implements Stmt {
	}
}

/* ===== Expressions ===== */
sealed interface Expr permits Expr.Num, Expr.Str, Expr.Bool, Expr.Var, Expr.Unary, Expr.Binary, Expr.Group, Expr.Obj, Expr.Arr {
	record Num(double v) implements Expr {
	}

	record Str(String v) implements Expr {
	}

	record Bool(boolean v) implements Expr {
	}

	record Var(String name) implements Expr {
	}

	record Unary(String op, Expr right) implements Expr {
	}            // -x, !x

	record Binary(Expr left, String op, Expr right) implements Expr {
	} // + - * / % == != < <= > >= && ||

	record Group(Expr inner) implements Expr {
	}

	record Obj(java.util.Map<String, Expr> entries) implements Expr {
	}

	record Arr(java.util.List<Expr> values) implements Expr {
	}
}

/* ===== Calls ===== */
record CallChain(String base, List<Segment> segments) {
	record Segment(String name, List<Expr> args) {
	}
}