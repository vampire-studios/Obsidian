package io.github.vampirestudios.obsidian.api;

public record EmojiCode(String code, String emoji) {

    public String getCode() {
        return code;
    }

    public String getEmoji() {
        return emoji;
    }

    public boolean match(String string, int charIndex) {
        for (int i = 0; i < code.length(); i++) {
            int stringIndex = charIndex - i;
            int codeIndex = code.length() - 1 - i;
            if (stringIndex < 0 || codeIndex < 0) return false;
            if (string.charAt(stringIndex) != code.charAt(codeIndex)) return false;
        }
        return true;
    }
}