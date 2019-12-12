package proxyChecker.gui;

import java.util.HashMap;
import java.util.prefs.Preferences;

public class Prefs {
    private static Preferences preferences;
    private static HashMap<String, Object> defaults = new HashMap<>();

    private Prefs() {}

    static {
        preferences = Preferences.userNodeForPackage(Prefs.class);

        defaults.put("url", "https://www.google.com/");
        defaults.put("timeout", 4000);
        defaults.put("threads", 5);
        defaults.put("checks", 1);

    }

    public static int getInt(String propName) {
        return preferences.getInt(propName, (int) defaults.get(propName));
    }

    public static double getDouble(String propName) {
        return preferences.getDouble(propName, (double) defaults.get(propName));
    }

    public static String getString(String propName) {
        return preferences.get(propName, (String) defaults.get(propName));
    }

    public static boolean getBoolean(String propName) {
        return preferences.getBoolean(propName, (boolean) defaults.get(propName));
    }

    public static void put(String propName, String value) {
        preferences.put(propName, value);
    }

    public static void put(String propName, int value) {
        preferences.put(propName, String.valueOf(value));
    }

    public static void put(String propName, double value) {
        preferences.put(propName, String.valueOf(value));
    }

    public static void put(String propName, boolean value) {
        preferences.put(propName, String.valueOf(value));
    }

    public static void remove(String propName) {
        preferences.remove(propName);
    }
}
