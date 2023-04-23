package io.github.vampirestudios.obsidian.api.scripting.rhino.dsl;

import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import io.github.vampirestudios.obsidian.api.scripting.rhino.RhinoThingScript;

public class LambdaBaseFunction extends BaseFunction
{
    private final RhinoThingScript.LambdaFunction impl;

    public LambdaBaseFunction(RhinoThingScript.LambdaFunction impl)
    {
        this.impl = impl;
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        return impl.call(cx, scope, thisObj, args);
    }
}