package io.github.vampirestudios.obsidian.utils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModVersionUtils {

    /**
     * Get mod version from string. String should be in format: %d.%d.%d
     *
     * @param version - {@link String} mod version.
     * @return int mod version.
     */
    public static int getModVersion(String version) {
        if (version.isEmpty()) {
            return 0;
        }
        try {
            int res = 0;
            final String semanticVersionPattern = "(\\d+)\\.(\\d+)\\.(\\d+)\\D*";
            final Matcher matcher = Pattern.compile(semanticVersionPattern)
                    .matcher(version);
            if (matcher.find()) {
                if (matcher.groupCount() > 0) res = (Integer.parseInt(matcher.group(1)) & 0xFF) << 22;
                if (matcher.groupCount() > 1) res |= (Integer.parseInt(matcher.group(2)) & 0xFF) << 14;
                if (matcher.groupCount() > 2) res |= Integer.parseInt(matcher.group(3)) & 0x3FFF;
            }

            return res;
        }
        catch (Exception e) {
            return 0;
        }
    }

    /**
     * Get mod version from integer. String will be in format %d.%d.%d
     *
     * @param version - mod version in integer form.
     * @return {@link String} mod version.
     */
    public static String getModVersion(int version) {
        int a = (version >> 22) & 0xFF;
        int b = (version >> 14) & 0xFF;
        int c = version & 0x3FFF;
        return String.format(Locale.ROOT, "%d.%d.%d", a, b, c);
    }

}
