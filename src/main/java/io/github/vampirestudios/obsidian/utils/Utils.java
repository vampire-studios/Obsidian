//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.github.vampirestudios.obsidian.utils;

import net.minecraft.util.Identifier;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class Utils {
    public Utils() {
    }

    public static String toTitleCase(String lowerCase) {
        return "" + Character.toUpperCase(lowerCase.charAt(0)) + lowerCase.substring(1);
    }

    public static String nameToId(String name, Map<String, String> specialCharMap) {
        Entry specialChar;
        for(Iterator var2 = specialCharMap.entrySet().iterator(); var2.hasNext(); name = name.replace((CharSequence)specialChar.getKey(), (CharSequence)specialChar.getValue())) {
            specialChar = (Entry)var2.next();
        }

        return name.toLowerCase(Locale.ENGLISH);
    }

    public static Identifier appendToPath(Identifier identifier, String suffix) {
        return new Identifier(identifier.getNamespace(), identifier.getPath() + suffix);
    }

    public static Identifier prependToPath(Identifier identifier, String prefix) {
        return new Identifier(identifier.getNamespace(), prefix + identifier.getPath());
    }

    public static Identifier appendAndPrependToPath(Identifier identifier, String prefix, String suffix) {
        return new Identifier(identifier.getNamespace(), prefix + identifier.getPath() + suffix);
    }

    public static double dist(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2.0D) + Math.pow(y2 - y1, 2.0D));
    }

    public static boolean checkBitFlag(int toCheck, int flag) {
        return (toCheck & flag) == flag;
    }
}
