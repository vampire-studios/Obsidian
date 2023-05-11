package io.github.vampirestudios.obsidian.utils;

public class ThingParseException extends RuntimeException
{
    public ThingParseException(String message)
    {
        super(message);
    }

    public ThingParseException(String message, Throwable cause)
    {
        super(message, cause);
    }
}