package donutauction;

import java.util.Locale;

/** Money amounts as DonutSMP writes them: "1,250", "2.5m", "$377M", "1.2B". */
public final class Money {
    private Money() {}

    /** Parses an amount; returns -1 if it isn't one. Blank counts as 0. */
    public static double parse(String text) {
        if (text == null) return -1;
        String s = text.trim().replace(",", "").replace("$", "").replace(" ", "").toLowerCase(Locale.ROOT);
        if (s.isEmpty()) return 0;
        double mult = 1;
        char last = s.charAt(s.length() - 1);
        switch (last) {
            case 'k' -> mult = 1e3;
            case 'm' -> mult = 1e6;
            case 'b' -> mult = 1e9;
            case 't' -> mult = 1e12;
            default -> { }
        }
        if (mult != 1) s = s.substring(0, s.length() - 1);
        try {
            double v = Double.parseDouble(s) * mult;
            return v < 0 || Double.isNaN(v) || Double.isInfinite(v) ? -1 : v;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /** Short form: 950, 12.5K, 377M, 1.25B. */
    public static String format(double v) {
        if (v < 1000) return trim(v);
        String[] units = {"K", "M", "B", "T"};
        int i = -1;
        while (v >= 1000 && i < units.length - 1) {
            v /= 1000;
            i++;
        }
        return trim(v) + units[i];
    }

    /** Whole number for commands like /pay: 2500000. */
    public static String plain(double v) {
        return String.format(Locale.ROOT, "%.0f", Math.floor(v));
    }

    private static String trim(double v) {
        String s = v >= 100 ? String.format(Locale.ROOT, "%.0f", v)
                : v >= 10 ? String.format(Locale.ROOT, "%.1f", v) : String.format(Locale.ROOT, "%.2f", v);
        if (s.contains(".")) s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        return s;
    }
}
