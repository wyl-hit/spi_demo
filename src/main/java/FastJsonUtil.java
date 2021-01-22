import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * json工具类
 *
 * @author wangyunlong1
 */
@Slf4j
public class FastJsonUtil {

    private FastJsonUtil() {

    }

    public static String paramToJsonStr(String paramStr) {
        JSONObject result = new JSONObject();
        try {
            HashMultimap<String, String> map = HashMultimap.create();
            paramStr = URLDecoder.decode(paramStr);
            StringBuilder sBuilder = new StringBuilder();
            if (paramStr != null && !paramStr.trim().equals("")) {
                String[] params = paramStr.split("&");
                if (params != null && params.length > 0) {
                    for (int i = 0; i < params.length; i++) {
                        String item = params[i];
                        String[] tmp = item.split("=");
                        String key = tmp[0];
                        String value = "";
                        if (tmp.length > 1) {
                            value = tmp[1];
                        } else {
                            // 空时不放入，不确定是不是数组
                            continue;
                        }
                        map.put(key, value);
                    }
                    // 组装json
                    for (String key : map.keySet()) {
                        Set<String> sets = map.get(key);
                        if (sets.size() > 1) {
                            JSONArray array = new JSONArray();
                            for (String value : sets) {
                                array.add(value);
                            }
                            result.put(key, array);
                        } else {
                            for (String value : sets) {
                                result.put(key, value);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return result.toString();
    }

    public static String optString(JSONObject jsonObject, String key, String value) {
        try {
            String result = jsonObject.getString(key);
            if (result == null) {
                return value;
            }
            return result;
        } catch (Exception e) {
            return value;
        }

    }

    public static String optString(JSONObject jsonObject, String key) {
        return optString(jsonObject, key, "");
    }

    /**
     *  添加字符值
     * @param jsonStr
     * @param key
     * @param value
     * @return
     */
    public static String addString(String jsonStr, String key, String value) {
        if (StringUtils.isBlank(key)) {
            return jsonStr;
        }
        if (StringUtils.isEmpty(value)) {
            value = "";
        }
        try {
            JSONObject obj = parseObject(jsonStr);
            obj.put(key, value);
            return obj.toString();
        } catch (Exception e) {
            return jsonStr;
        }
    }

    public static int optInt(JSONObject jsonObject, String key, int value) {
        try {
            Integer result = jsonObject.getInteger(key);
            if (result == null) {
                return value;
            }
            return result.intValue();
        } catch (Exception e) {
            return value;
        }
    }

    /**
     * 深度解析
     *
     * @param objStr, json对象串
     * @param keys key1->key2->key3
     * @param defaultValue
     * @return
     */
    public static int optInt(String objStr, String keys, int defaultValue) {
        if (StringUtils.isEmpty(objStr) || StringUtils.isEmpty(keys)) {
            return defaultValue;
        }
        try {
            JSONObject j = parseObject(objStr);
            List<String> ks = Lists.newArrayList(Splitter.on("->").omitEmptyStrings().split(keys));
            JSONObject jObj = j;
            for (int i = 0; i < ks.size() - 1; i++) {
                jObj = optJSONObject(jObj, ks.get(i));
                if (jObj == null) {
                    return defaultValue;
                }
            }
            return optInt(jObj, ks.get(ks.size() - 1), defaultValue);
        } catch (Throwable e) {
            return defaultValue;
        }
    }

    public static int optInt(JSONObject jsonObject, String key) {
        return optInt(jsonObject, key, 0);
    }

    public static double optDouble(JSONObject jsonObject, String key, double value) {
        try {
            Double result = jsonObject.getDouble(key);
            if (result == null) {
                return value;
            }
            return result.doubleValue();
        } catch (Exception e) {
            return value;
        }
    }

    public static double optDouble(JSONObject jsonObject, String key) {
        return optDouble(jsonObject, key, 0);
    }

    /**
     * 深度解析
     *
     * @param objStr, json对象串
     * @param keys key1->key2->key3
     * @param defaultValue
     * @return
     */
    public static long optLong(String objStr, String keys, long defaultValue) {
        if (StringUtils.isEmpty(objStr) || StringUtils.isEmpty(keys)) {
            return defaultValue;
        }
        try {
            JSONObject j = parseObject(objStr);
            List<String> ks = Lists.newArrayList(Splitter.on("->").omitEmptyStrings().split(keys));
            JSONObject jObj = j;
            for (int i = 0; i < ks.size() - 1; i++) {
                jObj = optJSONObject(jObj, ks.get(i));
                if (jObj == null) {
                    return defaultValue;
                }
            }
            return optLong(jObj, ks.get(ks.size() - 1), defaultValue);
        } catch (Throwable e) {
            return defaultValue;
        }
    }

    public static long optLong(JSONObject jsonObject, String key, long value) {
        try {
            Long result = jsonObject.getLong(key);
            if (result == null) {
                return value;
            }
            return result.longValue();
        } catch (Exception e) {
            return value;
        }
    }

    public static long optLong(JSONObject jsonObject, String key) {
        return optLong(jsonObject, key, 0);
    }

    public static JSONObject optJSONObject(JSONObject jsonObject, String key, JSONObject obj) {
        try {
            JSONObject result = jsonObject.getJSONObject(key);
            if (result == null) {
                return obj;
            }
            return result;
        } catch (Exception e) {
            return obj;
        }

    }

    public static JSONObject optJSONObject(JSONObject jsonObject, String key) {
        return jsonObject.getJSONObject(key);
    }

    public static JSONArray optJsonArray(JSONObject jsonObject, String key, JSONArray jsonArray) {
        try {
            JSONArray result = jsonObject.getJSONArray(key);
            if (result == null) {
                return jsonArray;
            }
            return result;
        } catch (Exception e) {
            return jsonArray;
        }

    }

    public static JSONArray getJsonArray(JSONObject jsonObject, String key) {
        return jsonObject.getJSONArray(key);
    }

    public static JSONObject parseObject(String json) {
        return JSON.parseObject(json);
    }

    public static JSONArray parseArray(String json) {
        return JSON.parseArray(json);
    }

    public static String toJsonStr(Object object) {
        return JSON.toJSONString(object);
    }

    public static JSONObject toObject(Object object) {
        try {
            return (JSONObject) JSON.toJSON(object);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 默认返回false
     *
     * @param jsonObject
     * @param key
     * @return
     */
    public static boolean getBoolean(JSONObject jsonObject, String key) {
        return jsonObject.getBooleanValue(key);
    }

    /**
     * 默认返回false
     *
     * @param jsonObject
     * @param key
     * @return
     */
    public static boolean optBoolean(JSONObject jsonObject, String key) {
        return optBoolean(jsonObject, key, false);
    }

    public static boolean optBoolean(JSONObject jsonObject, String key, boolean value) {
        try {
            Boolean result = jsonObject.getBoolean(key);
            if (result == null) {
                return value;
            }
            return result;
        } catch (Exception e) {
            return value;
        }
    }

    public static <T> List<T> parseArray2List(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return new ArrayList<>();
        }
        return JSON.parseArray(json, clazz);
    }

    public static <T> T parseToBean(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    /**
     * 深度解析
     *
     * @param objStr, json对象串
     * @param keys key1->key2->key3
     * @param defaultValue
     * @return
     */
    public static String optString(String objStr, String keys, String defaultValue) {
        if (StringUtils.isEmpty(objStr) || StringUtils.isEmpty(keys)) {
            return defaultValue;
        }
        try {
            JSONObject j = parseObject(objStr);
            List<String> ks = Lists.newArrayList(Splitter.on("->").omitEmptyStrings().split(keys));
            JSONObject jObj = j;
            for (int i = 0; i < ks.size() - 1; i++) {
                jObj = optJSONObject(jObj, ks.get(i));
                if (jObj == null) {
                    return defaultValue;
                }
            }
            return optString(jObj, ks.get(ks.size() - 1), defaultValue);
        } catch (Throwable e) {
            return defaultValue;
        }
    }
}
