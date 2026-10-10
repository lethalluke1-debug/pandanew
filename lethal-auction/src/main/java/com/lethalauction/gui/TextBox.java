package com.lethalauction.gui;

import java.util.function.IntPredicate;

/** A minimal single-line text field: typing, backspace, paste. Drawing is done by LethalScreen. */
final class TextBox {
    final int maxLength;
    private final IntPredicate allowed;
    String value;
    /** True after Ctrl+A: the next typed text replaces everything, Backspace/Delete clears it. */
    boolean allSelected;

    TextBox(String initial, int maxLength, IntPredicate allowed) {
        this.value = initial;
        this.maxLength = maxLength;
        this.allowed = allowed;
    }

    static TextBox any(int maxLength) {
        return new TextBox("", maxLength, c -> c >= 32 && c != 127);
    }

    static TextBox digits(String initial, int maxLength) {
        return new TextBox(initial, maxLength, c -> c >= '0' && c <= '9');
    }

    /** Digits plus a decimal point and k/m/b suffixes, e.g. "2.5k". */
    static TextBox amount(int maxLength) {
        return new TextBox("", maxLength, c -> (c >= '0' && c <= '9') || c == '.' || c == 'k' || c == 'm' || c == 'b'
                || c == 'K' || c == 'M' || c == 'B');
    }

    /** Returns true if the text changed. */
    boolean type(String text) {
        if (allSelected) {
            value = "";
            allSelected = false;
        }
        StringBuilder sb = new StringBuilder(value);
        text.codePoints().filter(allowed).forEach(c -> {
            if (sb.length() < maxLength) {
                sb.appendCodePoint(c);
            }
        });
        boolean changed = !sb.toString().equals(value);
        value = sb.toString();
        return changed;
    }

    boolean backspace(boolean word) {
        if (value.isEmpty()) {
            allSelected = false;
            return false;
        }
        if (word || allSelected) {
            allSelected = false;
            value = "";
        } else {
            value = value.substring(0, value.offsetByCodePoints(value.length(), -1));
        }
        return true;
    }
}
