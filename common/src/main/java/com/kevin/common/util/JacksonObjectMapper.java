package com.kevin.common.util;

import java.io.IOException;
import java.lang.reflect.Type;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * 处理 open api调用时，集合类型的json串反序列化的问题
 */
public class JacksonObjectMapper extends ObjectMapper {
    /**add by kanguangwen*/
    private static final JacksonObjectMapper jacksonMapper = new JacksonObjectMapper();

    /**
     *
     * 为解决ApIController接口调用时，集合类型的json反序列化问题而添加
     *
     * @param content json串
     * @param type    java.lang.reflect.Type
     * @return
     * @throws IOException
     * @throws JsonParseException
     * @throws JsonMappingException
     * @author kanguangwen
     */
    @SuppressWarnings("unchecked")
    public <T> T readValue(String content, Type type) throws IOException, JsonParseException, JsonMappingException {
        return (T) _readMapAndClose(_jsonFactory.createJsonParser(content), _typeFactory.constructType(type));
    }

    /**
     *
     *
     * @param json
     * @param type
     * @return
     * @author kanguangwen
     */
    @SuppressWarnings("deprecation")
    public static <T> T objectFromJson(String json, Type type) {
        T object;
        try {// 设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
            jacksonMapper.getDeserializationConfig().set(
                    org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            object = jacksonMapper.readValue(json, type);
        } catch (RuntimeException e) {
            //log.error("Runtime exception during deserializing from " + StringUtils.abbreviate(json, 80));
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            //log.error("Exception during deserializing from " + StringUtils.abbreviate(json, 80));
            return null;
        }
        return object;
    }
}
