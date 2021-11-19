package com.daskrr.nameplates.util;

import com.daskrr.nameplates.version.Version;
import com.daskrr.nameplates.version.VersionProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Map;

public class NamePlateUtils {

    public static final float PIXEL = 0.03125F;
    public static final Map<Character, Float> CHARACTERS = Maps.newHashMap();

    static {
        CHARACTERS.put(' ', 3*PIXEL);

        CHARACTERS.put('A', 5*PIXEL);
        CHARACTERS.put('B', 5*PIXEL);
        CHARACTERS.put('C', 5*PIXEL);
        CHARACTERS.put('D', 5*PIXEL);
        CHARACTERS.put('E', 5*PIXEL);
        CHARACTERS.put('F', 5*PIXEL);
        CHARACTERS.put('G', 5*PIXEL);
        CHARACTERS.put('H', 5*PIXEL);
        CHARACTERS.put('I', 3*PIXEL);
        CHARACTERS.put('J', 5*PIXEL);
        CHARACTERS.put('K', 5*PIXEL);
        CHARACTERS.put('L', 5*PIXEL);
        CHARACTERS.put('M', 5*PIXEL);
        CHARACTERS.put('N', 5*PIXEL);
        CHARACTERS.put('O', 5*PIXEL);
        CHARACTERS.put('P', 5*PIXEL);
        CHARACTERS.put('Q', 5*PIXEL);
        CHARACTERS.put('R', 5*PIXEL);
        CHARACTERS.put('S', 5*PIXEL);
        CHARACTERS.put('T', 5*PIXEL);
        CHARACTERS.put('U', 5*PIXEL);
        CHARACTERS.put('V', 5*PIXEL);
        CHARACTERS.put('W', 5*PIXEL);
        CHARACTERS.put('X', 5*PIXEL);
        CHARACTERS.put('Y', 5*PIXEL);
        CHARACTERS.put('Z', 5*PIXEL);

        CHARACTERS.put('a', 5*PIXEL);
        CHARACTERS.put('b', 5*PIXEL);
        CHARACTERS.put('c', 5*PIXEL);
        CHARACTERS.put('d', 5*PIXEL);
        CHARACTERS.put('e', 5*PIXEL);
        CHARACTERS.put('f', 4*PIXEL);
        CHARACTERS.put('g', 5*PIXEL);
        CHARACTERS.put('h', 5*PIXEL);
        CHARACTERS.put('i', PIXEL);
        CHARACTERS.put('j', 5*PIXEL);
        CHARACTERS.put('k', 4*PIXEL);
        CHARACTERS.put('l', 2*PIXEL);
        CHARACTERS.put('m', 5*PIXEL);
        CHARACTERS.put('n', 5*PIXEL);
        CHARACTERS.put('o', 5*PIXEL);
        CHARACTERS.put('p', 5*PIXEL);
        CHARACTERS.put('q', 5*PIXEL);
        CHARACTERS.put('r', 5*PIXEL);
        CHARACTERS.put('s', 5*PIXEL);
        CHARACTERS.put('t', 3*PIXEL);
        CHARACTERS.put('u', 5*PIXEL);
        CHARACTERS.put('v', 5*PIXEL);
        CHARACTERS.put('w', 5*PIXEL);
        CHARACTERS.put('x', 5*PIXEL);
        CHARACTERS.put('y', 5*PIXEL);
        CHARACTERS.put('z', 5*PIXEL);

        CHARACTERS.put('0', 5*PIXEL);
        CHARACTERS.put('1', 5*PIXEL);
        CHARACTERS.put('2', 5*PIXEL);
        CHARACTERS.put('3', 5*PIXEL);
        CHARACTERS.put('4', 5*PIXEL);
        CHARACTERS.put('5', 5*PIXEL);
        CHARACTERS.put('6', 5*PIXEL);
        CHARACTERS.put('7', 5*PIXEL);
        CHARACTERS.put('8', 5*PIXEL);
        CHARACTERS.put('9', 5*PIXEL);

        CHARACTERS.put('`', 2*PIXEL);
        CHARACTERS.put('~', 6*PIXEL);
        CHARACTERS.put('!', 1*PIXEL);
        CHARACTERS.put('@', 6*PIXEL);

        CHARACTERS.put('*', 4*PIXEL);
        CHARACTERS.put('(', 4*PIXEL);
        CHARACTERS.put(')', 4*PIXEL);
        CHARACTERS.put('{', 4*PIXEL);
        CHARACTERS.put('}', 4*PIXEL);
        CHARACTERS.put('\'', 2*PIXEL);
        CHARACTERS.put('\"', 4*PIXEL);
        CHARACTERS.put('.', PIXEL);
        CHARACTERS.put(',', PIXEL);
        CHARACTERS.put('[', 3*PIXEL);
        CHARACTERS.put(']', 3*PIXEL);
        CHARACTERS.put('<', 4*PIXEL);
        CHARACTERS.put('>', 4*PIXEL);
    }

    public static double calculatePlateWidth(String text) {
        if (text == null)
            return 0.0D;

        double width = 0D;
        int skipping = 0;
        for (int i = 0; i < text.toCharArray().length; i++) {
            if (skipping > 0) {
                skipping--;
                continue;
            }

            char ch = text.toCharArray()[i];
            // ignore color codes
            if (ch == '§') {
                // look for hex
                // §#rrggbb
                if (VersionProvider.getInstance().getVersion().ordinal() >= Version.v1_16_R1.ordinal())
                    if (text.toCharArray()[i + 1] == '#') {
                        skipping = 7;
                        continue;
                    }

                // lower than 1.16
                // §[a-f]|[0-9]
                skipping = 1;
                continue;
            }

            width += CHARACTERS.getOrDefault(ch, 5 * PIXEL);
            width += 2*PIXEL;
        }

        return width;
    }
}
