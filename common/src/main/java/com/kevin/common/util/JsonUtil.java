package com.kevin.common.util;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;


public abstract class JsonUtil {

    public static final org.slf4j.Logger log    = LoggerFactory.getLogger(JsonUtil.class);

    private static final ObjectMapper    mapper = new ObjectMapper();

    public static String jsonFromObject(Object object) {
        StringWriter writer = new StringWriter();
        try {
            mapper.writeValue(writer, object);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            // e.printStackTrace();
            log.error("Unable to serialize to json: " + object, e);
            return null;
        }
        return writer.toString();
    }

    @SuppressWarnings("deprecation")
    public static <T> T objectFromJson(String json, Class<T> klass) {
        T object;
        try {// 设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
            mapper.getDeserializationConfig().set(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
                    false);
            object = mapper.readValue(json, klass);
        } catch (RuntimeException e) {
            log.error("Runtime exception during deserializing " + klass.getSimpleName() + " from "
                    + StringUtils.abbreviate(json, 80));
            throw e;
        } catch (Exception e) {
            log.error("Exception during deserializing " + klass.getSimpleName() + " from "
                    + StringUtils.abbreviate(json, 80));
            return null;
        }
        return object;
    }

    @SuppressWarnings("deprecation")
    public static <T> T objectFromJson(String json, TypeReference<T> klass) {
        T object;
        try {// 设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
            mapper.getDeserializationConfig().set(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
                    false);
            object = mapper.readValue(json, klass);
        } catch (RuntimeException e) {
            log.error("Runtime exception during deserializing from " + StringUtils.abbreviate(json, 80));
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Exception during deserializing from " + StringUtils.abbreviate(json, 80));
            return null;
        }
        return object;
    }

    /**
     * mapper.readValue(jsonInput, new TypeReference<List<MyClass>>(){});
     *
     * @param json
     * @param klass
     * @return
     */
    @SuppressWarnings("deprecation")
    public static <T> T listFromJson(String json, TypeReference<T> klass) {
        T logs;
        try {
            mapper.getDeserializationConfig().set(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
                    false);
            logs = mapper.readValue(json, klass);
        } catch (RuntimeException e) {
            log.error("Runtime exception during deserializing ");
            throw e;
        } catch (Exception e) {
            log.error("Exception during deserializing " + " from " + StringUtils.abbreviate(json, 80));
            return null;
        }
        return logs;
    }

    @SuppressWarnings("rawtypes")
    public static Object getValue(Map map, String... keys) {
        if (map == null) return null;
        for (int i = 0; i < keys.length - 1; i++) {
            if (!map.containsKey(keys[i]) || map.get(keys[i]) == null || map.get(keys[i]) == JSONObject.NULL) return null;
            map = (Map) map.get(keys[i]);
        }

        if (!map.containsKey(keys[keys.length - 1]) || map.get(keys[keys.length - 1]) == null
                || map.get(keys[keys.length - 1]) == JSONObject.NULL) return null;
        return map.get(keys[keys.length - 1]);
    }

    @SuppressWarnings("rawtypes")
    public static String getString(Map map, String... keys) {
        Object obj = getValue(map, keys);
        if (obj == null) {
            return null;
        } else {
            return String.valueOf(obj);
        }
    }

    @SuppressWarnings("rawtypes")
    public static Map getMap(Map map, String... keys) {
        Object obj = getValue(map, keys);
        if (obj == null) {
            return null;
        } else {
            return (Map) obj;
        }
    }

    @SuppressWarnings("rawtypes")
    public static Date getDate(Map map, String format, String... keys) {
        Object obj = getValue(map, keys);
        if (obj == null) {
            return null;
        } else {
            return DateUtils.getDate(String.valueOf(obj), format);
        }
    }

    @SuppressWarnings("rawtypes")
    public static Integer getInteger(Map map, String... keys) {
        Object obj = getValue(map, keys);
        if (obj == null) {
            return null;
        } else {
            return Integer.valueOf(String.valueOf(obj));
        }
    }

    @SuppressWarnings("rawtypes")
    public static Long getLong(Map map, String... keys) {
        Object obj = getValue(map, keys);
        if (obj == null) {
            return null;
        } else {
            return Long.valueOf(String.valueOf(obj));
        }
    }

    @SuppressWarnings("rawtypes")
    public static BigDecimal getBigDecimal(Map map, String... keys) {
        Object obj = getValue(map, keys);
        if (obj == null) {
            return null;
        } else {
            return new BigDecimal(obj + "");
        }
    }

    @SuppressWarnings("rawtypes")
    public static List getList(Map map, String... keys) {
        Object obj = getValue(map, keys);
        if (obj == null) {
            return null;
        } else {
            return (List) obj;
        }
    }

    static final Pattern RegEx_Double = Pattern.compile("([0-9.]+)");

    @SuppressWarnings("rawtypes")
    public static Double getDouble(Map map, String... keys) {
        Object obj = getValue(map, keys);
        if (obj == null) {
            return null;
        } else {
            Matcher m = RegEx_Double.matcher(String.valueOf(obj));
            if (m.find()) {
                return Double.valueOf(m.group(1));
            } else {
                return null;
            }
        }
    }

    @SuppressWarnings({ "rawtypes" })
    public static Map toMap(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);

        return toMap(jsonObject);
    }

    @SuppressWarnings({ "rawtypes" })
    public static List toList(String jsonString) throws JSONException {
        JSONArray jsonObject = new JSONArray(jsonString);

        return toList(jsonObject);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Map toMap(JSONObject jsonObject) {
        Map result = new HashMap();
        Iterator iterator = jsonObject.keys();
        String key = null;
        Object value = null;

        while (iterator.hasNext()) {
            key = (String) iterator.next();
            value = jsonObject.get(key);// .getJSONObject(key);
            if (value != null && value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            } else if (value != null && value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }
            result.put(key, value);

        }
        return result;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List toList(JSONArray array) {
        List result = new ArrayList();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value != null && value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            } else if (value != null && value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }
            result.add(value);
        }
        return result;
    }
}

