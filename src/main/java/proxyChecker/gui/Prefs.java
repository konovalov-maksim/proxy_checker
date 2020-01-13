package proxyChecker.gui;

import java.util.HashMap;
import java.util.prefs.Preferences;

public class Prefs {
    private static Preferences preferences;
    private static HashMap<String, Object> defaults = new HashMap<>();

    private Prefs() {}

    public enum Key{
        URL, TIMEOUT, THREADS, CHECKS
    }

    static {
        preferences = Preferences.userNodeForPackage(Prefs.class);

        defaults.put(Key.URL.name().toLowerCase(), "https://www.google.com/");
        defaults.put(Key.TIMEOUT.name().toLowerCase(), 4000);
        defaults.put(Key.THREADS.name().toLowerCase(), 5);
        defaults.put(Key.CHECKS.name().toLowerCase(), 1);
    }

    public static int getInt(Key key) {
        return preferences.getInt(key.name().toLowerCase(), (int) defaults.get(key.name().toLowerCase()));
    }

    public static double getDouble(Key key) {
        return preferences.getDouble(key.name().toLowerCase(), (double) defaults.get(key.name().toLowerCase()));
    }

    public static String getString(Key key) {
        return preferences.get(key.name().toLowerCase(), (String) defaults.get(key.name().toLowerCase()));
    }

    public static boolean getBoolean(String propName) {
        return preferences.getBoolean(propName, (boolean) defaults.get(propName));
    }

    public static void put(Key key, String value) {
        preferences.put(key.name().toLowerCase(), value);
    }

    public static void put(Key key, int value) {
        preferences.put(key.name().toLowerCase(), String.valueOf(value));
    }

    public static void put(Key key, double value) {
        preferences.put(key.name().toLowerCase(), String.valueOf(value));
    }

    public static void put(Key key, boolean value) {
        preferences.put(key.name().toLowerCase(), String.valueOf(value));
    }

    public static void remove(Key key) {
        preferences.remove(key.name().toLowerCase());
    }
}
