package br.com.i9algo.taxiadv.v2.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static android.content.Context.MODE_PRIVATE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public final class Utils {

    public static final String THREAD_PREFIX = "idooh-";

    /** @deprecated Use {@link #toISO8601String(Date)}. */
    public static String toISO8601Date(Date date) {
        return toISO8601String(date);
    }

    /** Returns {@code date} formatted as yyyy-MM-ddThh:mm:ss.sssZ */
    public static String toISO8601String(Date date) {
        return Iso8601Utils.format(date);
    }

    /**
     * Parse a date from ISO-8601 formatted string. It expects a format
     * [yyyy-MM-dd|yyyyMMdd][T(hh:mm[:ss[.sss]]|hhmm[ss[.sss]])]?[Z|[+-]hh:mm]]
     *
     * @param date ISO string to parse in the appropriate format.
     * @return the parsed date
     */
    public static Date parseISO8601Date(String date) {
        return Iso8601Utils.parse(date);
    }

    /** @deprecated Use {@link #parseISO8601Date(String)}. */
    public static Date toISO8601Date(String date) throws ParseException {
        return parseISO8601Date(date);
    }

    // TODO: Migrate other coercion methods.

    /**
     * Returns the float representation at {@code value} if it exists and is a float or can be coerced
     * to a float. Returns {@code defaultValue} otherwise.
     */
    public static float coerceToFloat(Object value, float defaultValue) {
        if (value instanceof Float) {
            return (float) value;
        }
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        } else if (value instanceof String) {
            try {
                return Float.valueOf((String) value);
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    /** Returns true if the application has the given permission. */
    public static boolean hasPermission(Context context, String permission) {
        return context.checkCallingOrSelfPermission(permission) == PERMISSION_GRANTED;
    }

    /** Returns true if the application has the given feature. */
    public static boolean hasFeature(Context context, String feature) {
        return context.getPackageManager().hasSystemFeature(feature);
    }

    /** Returns the system service for the given string. */
    @SuppressWarnings("unchecked")
    public static <T> T getSystemService(Context context, String serviceConstant) {
        return (T) context.getSystemService(serviceConstant);
    }

    /** Returns true if the string is null, or empty (once trimmed). */
    public static boolean isNullOrEmpty(CharSequence text) {
        return isEmpty(text) || getTrimmedLength(text) == 0;
    }

    /**
     * Returns true if the string is null or 0-length.
     *
     * <p>Copied from {@link TextUtils#isEmpty(CharSequence)}
     *
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    private static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * Returns the length that the specified CharSequence would have if spaces and control characters
     * were trimmed from the start and end, as by {@link String#trim}.
     *
     * <p>Copied from {@link TextUtils#getTrimmedLength(CharSequence)}
     */
    private static int getTrimmedLength(@NonNull CharSequence s) {
        int len = s.length();

        int start = 0;
        while (start < len && s.charAt(start) <= ' ') {
            start++;
        }

        int end = len;
        while (end > start && s.charAt(end - 1) <= ' ') {
            end--;
        }

        return end - start;
    }

    /** Returns true if the collection is null or has a size of 0. */
    public static boolean isNullOrEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }

    /** Returns true if the array is null or has a size of 0. */
    public static <T> boolean isNullOrEmpty(T[] data) {
        return data == null || data.length == 0;
    }

    /** Returns true if the map is null or empty, false otherwise. */
    public static boolean isNullOrEmpty(Map map) {
        return map == null || map.size() == 0;
    }

    /** Throws a {@link NullPointerException} if the given text is null or empty. */
    @NonNull
    public static String assertNotNullOrEmpty(String text, @Nullable String name) {
        if (isNullOrEmpty(text)) {
            throw new NullPointerException(name + " cannot be null or empty");
        }
        return text;
    }

    /** Throws a {@link NullPointerException} if the given map is null or empty. */
    @NonNull
    public static <K, V> Map<K, V> assertNotNullOrEmpty(Map<K, V> data, @Nullable String name) {
        if (isNullOrEmpty(data)) {
            throw new NullPointerException(name + " cannot be null or empty");
        }
        return data;
    }

    /** Throws a {@link NullPointerException} if the given object is null. */
    @NonNull
    public static <T> T assertNotNull(T object, String item) {
        if (object == null) {
            throw new NullPointerException(item + " == null");
        }
        return object;
    }

    /** Returns an immutable copy of the provided map. */
    @NonNull
    public static <K, V> Map<K, V> immutableCopyOf(@NonNull Map<K, V> map) {
        return Collections.unmodifiableMap(new LinkedHashMap<>(map));
    }

    /** Returns an immutable copy of the provided list. */
    @NonNull
    public static <T> List<T> immutableCopyOf(@Nullable List<T> list) {
        if (isNullOrEmpty(list)) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(list));
    }

    /** Returns a shared preferences for storing any library preferences. */
    public static SharedPreferences getSharedPreferences(Context context, String tag) {
        return context.getSharedPreferences("analytics-android-" + tag, MODE_PRIVATE);
    }

    /** Get the string resource for the given key. Returns null if not found. */
    public static String getResourceString(Context context, String key) {
        int id = getIdentifier(context, "string", key);
        if (id != 0) {
            return context.getResources().getString(id);
        } else {
            return null;
        }
    }

    /** Get the identifier for the resource with a given type and key. */
    private static int getIdentifier(Context context, String type, String key) {
        return context.getResources().getIdentifier(key, type, context.getPackageName());
    }

    /** Buffers the given {@code InputStream}. */
    public static BufferedReader buffer(InputStream is) {
        return new BufferedReader(new InputStreamReader(is));
    }

    /** Reads the give {@code InputStream} into a String. */
    public static String readFully(InputStream is) throws IOException {
        return readFully(buffer(is));
    }

    /** Reads the give {@code BufferedReader} into a String. */
    public static String readFully(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String line; (line = reader.readLine()) != null; ) {
            sb.append(line);
        }
        return sb.toString();
    }

    /**
     * Transforms the given map by replacing the keys mapped by {@code mapper}. Any keys not in the
     * mapper preserve their original keys. If a key in the mapper maps to null or a blank string,
     * that value is dropped.
     *
     * <p>e.g. transform({a: 1, b: 2, c: 3}, {a: a, c: ""}) -> {$a: 1, b: 2} - transforms a to $a -
     * keeps b - removes c
     */
    public static <T> Map<String, T> transform(Map<String, T> in, Map<String, String> mapper) {
        Map<String, T> out = new LinkedHashMap<>(in.size());
        for (Map.Entry<String, T> entry : in.entrySet()) {
            String key = entry.getKey();
            if (!mapper.containsKey(key)) {
                out.put(key, entry.getValue()); // keep the original key.
                continue;
            }
            String mappedKey = mapper.get(key);
            if (!isNullOrEmpty(mappedKey)) {
                out.put(mappedKey, entry.getValue());
            }
        }
        return out;
    }

    /**
     * Return a copy of the contents of the given map as a {@link JSONObject}. Instead of failing on
     * {@code null} values like the {@link JSONObject} map constructor, it cleans them up and
     * correctly converts them to {@link JSONObject#NULL}.
     */
    public static JSONObject toJsonObject(Map<String, ?> map) {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            Object value = wrap(entry.getValue());
            try {
                jsonObject.put(entry.getKey(), value);
            } catch (JSONException ignored) {
                // Ignore values that JSONObject doesn't accept.
            }
        }
        return jsonObject;
    }

    /**
     * Wraps the given object if necessary. {@link JSONObject#wrap(Object)} is only available on API
     * 19+, so we've copied the implementation. Deviates from the original implementation in that it
     * always returns {@link JSONObject#NULL} instead of {@code null} in case of a failure, and
     * returns the {@link Object#toString} of any object that is of a custom (non-primitive or
     * non-collection/map) type.
     *
     * <p>If the object is null returns {@link JSONObject#NULL}. If the object is a {@link JSONArray}
     * or {@link JSONObject}, no wrapping is necessary. If the object is {@link JSONObject#NULL}, no
     * wrapping is necessary. If the object is an array or {@link Collection}, returns an equivalent
     * {@link JSONArray}. If the object is a {@link Map}, returns an equivalent {@link JSONObject}. If
     * the object is a primitive wrapper type or {@link String}, returns the object. Otherwise returns
     * the result of {@link Object#toString}. If wrapping fails, returns JSONObject.NULL.
     */
    private static Object wrap(Object o) {
        if (o == null) {
            return JSONObject.NULL;
        }
        if (o instanceof JSONArray || o instanceof JSONObject) {
            return o;
        }
        if (o.equals(JSONObject.NULL)) {
            return o;
        }
        try {
            if (o instanceof Collection) {
                return new JSONArray((Collection) o);
            } else if (o.getClass().isArray()) {
                final int length = Array.getLength(o);
                JSONArray array = new JSONArray();
                for (int i = 0; i < length; ++i) {
                    array.put(wrap(Array.get(array, i)));
                }
                return array;
            }
            if (o instanceof Map) {
                //noinspection unchecked
                return toJsonObject((Map) o);
            }
            if (o instanceof Boolean
                    || o instanceof Byte
                    || o instanceof Character
                    || o instanceof Double
                    || o instanceof Float
                    || o instanceof Integer
                    || o instanceof Long
                    || o instanceof Short
                    || o instanceof String) {
                return o;
            }
            // Deviate from original implementation and return the String representation of the object
            // regardless of package.
            return o.toString();
        } catch (Exception ignored) {
        }
        // Deviate from original and return JSONObject.NULL instead of null.
        return JSONObject.NULL;
    }

    public static <T> Map<String, T> createMap() {
        return new NullableConcurrentHashMap<>();
    }

    /** Copies all the values from {@code src} to {@code target}. */
    public static void copySharedPreferences(SharedPreferences src, SharedPreferences target) {
        SharedPreferences.Editor editor = target.edit();
        for (Map.Entry<String, ?> entry : src.getAll().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                editor.putString(key, (String) value);
            } else if (value instanceof Set) {
                editor.putStringSet(key, (Set<String>) value);
            } else if (value instanceof Integer) {
                editor.putInt(key, (Integer) value);
            } else if (value instanceof Long) {
                editor.putLong(key, (Long) value);
            } else if (value instanceof Float) {
                editor.putFloat(key, (Float) value);
            } else if (value instanceof Boolean) {
                editor.putBoolean(key, (Boolean) value);
            }
        }
        editor.apply();
    }

    private Utils() {
        throw new AssertionError("No instances");
    }

    /** A {@link ConcurrentHashMap} that rejects null keys and values instead of failing. */
    public static class NullableConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {

        public NullableConcurrentHashMap() {
            super();
        }

        public NullableConcurrentHashMap(Map<? extends K, ? extends V> m) {
            super(m);
        }

        @Override
        public V put(K key, V value) {
            if (key == null || value == null) {
                return null;
            }
            return super.put(key, value);
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
                put(e.getKey(), e.getValue());
            }
        }
    }
}
