package io.github.vampirestudios.obsidian.api.scripting;

import io.github.vampirestudios.obsidian.api.events.FlexEventContext;
import io.github.vampirestudios.obsidian.api.events.FlexEventHandler;
import io.github.vampirestudios.obsidian.api.events.FlexEventResult;

public abstract class ThingScript implements FlexEventHandler
{
    @Override
    public abstract FlexEventResult apply(String eventName, FlexEventContext context);
}