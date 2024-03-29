package io.github.vampirestudios.obsidian.api.scripting.rhino;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.ScriptableObject;
import io.github.vampirestudios.obsidian.api.events.ContextValue;
import io.github.vampirestudios.obsidian.api.events.FlexEventContext;

public class FlexEventScriptable extends NativeJavaObject
{
    private final FlexEventContext ctx;

    public FlexEventScriptable(Scriptable scope, FlexEventContext ctx, Context cx)
    {
        super(scope, ctx, FlexEventContext.class, cx);
        this.ctx = ctx;
    }

    @Override
    public boolean has(Context cx, String name, Scriptable start)
    {
        var val = ContextValue.get(name);
        if (ctx.has(val))
            return true;
        return super.has(cx, name, start);
    }

    @Override
    public Object get(Context cx, String name, Scriptable start)
    {
        var val = ContextValue.get(name);
        if (ctx.has(val))
        {
            var rval = ctx.get(val);

            var scope = ScriptableObject.getTopLevelScope(this);
            return cx.getWrapFactory().wrap(cx, scope, rval, val.getType());
        }
        return super.get(cx, name, start);
    }
}