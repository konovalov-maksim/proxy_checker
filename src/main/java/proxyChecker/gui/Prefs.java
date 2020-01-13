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

        defaults.put(Key.URL.name(), "https://www.google.com/");
        defaults.put(Key.TIMEOUT.name(), 4000);
        defaults.put(Key.THREADS.name(), 5);
        defaults.put(Key.CHECKS.name(), 1);
    }

    public static int getInt(Key key) {
        return preferences.getInt(key.name(), (int) defaults.get(key.name()));
    }

    public static double getDouble(Key key) {
        return preferences.getDouble(key.name(), (double) defaults.get(key.name()));
    }

    public static String getString(Key key) {
        return preferences.get(key.name(), (String) defaults.get(key.name()));
    }

    public static boolean getBoolean(String propName) {
        return preferences.getBoolean(propName, (boolean) defaults.get(propName));
    }

    public static void put(Key key, String value) {
        preferences.put(key.name(), value);
    }

    public static void put(Key key, int value) {
        preferences.put(key.name(), String.valueOf(value));
    }

    public static void put(Key key, double value) {
        preferences.put(key.name(), String.valueOf(value));
    }

    public static void put(Key key, boolean value) {
        preferences.put(key.name(), String.valueOf(value));
    }

    public static void remove(Key key) {
        preferences.remove(key.name());
    }
}
