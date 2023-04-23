package io.github.vampirestudios.obsidian.api.events;

import java.util.function.BiFunction;

public interface FlexEventHandler extends BiFunction<String, FlexEventContext, FlexEventResult>
{
    @Override
    FlexEventResult apply(String eventName, FlexEventContext context);
}