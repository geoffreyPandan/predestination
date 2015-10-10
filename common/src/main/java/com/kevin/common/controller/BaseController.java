package com.kevin.common.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kevin.common.basic.Answer;
import com.kevin.common.basic.AnswerBuilder;
import com.kevin.common.util.JsonUtil;
import com.kevin.common.util.ServiceInvocationUtil;

/**
 * BaseController
 *
 * @author: sunjie
 * @date: 15/9/24
 */
public class BaseController {
    private static final Logger log = LoggerFactory.getLogger(BaseController.class);

    /**
     * 处理请求
     *
     * @param serviceName
     * @param methodName
     * @param requestMap
     * @param request
     * @param response
     */
    protected void processRequest(String serviceName, String methodName, Map<String, Object> requestMap,
                                  HttpServletRequest request, HttpServletResponse response) {
        Answer answer = null;
        try {
            answer = ServiceInvocationUtil.invoke(serviceName, methodName, requestMap);
        }
        catch (Exception e) {
            answer = AnswerBuilder.buildExceptionAnswer(e);
            StringBuilder builder = new StringBuilder();
            builder.append("processRequest Error For Url<").append(request.getQueryString()).append(">");
            builder.append("requestMap is :").append(JsonUtil.jsonFromObject(requestMap));
            log.error(builder.toString(), e);
        } finally {
            String callback = this.getString(request, "callback", null, false);
            if (answer.getResult() instanceof byte[]) {//support for download file
                outByte(response, (byte[]) answer.getResult());
            } else {
                String content = answer.getResult() instanceof String ? answer.getResult().toString() : JsonUtil.jsonFromObject(answer);
                if (StringUtils.isNotBlank(callback)) {
                    this.outJsonP(response, callback, content);
                } else {
                    this.outJson(response, content);
                }
            }
        }
    }

    protected void outJsonP(HttpServletResponse response, String callback, String content) {

        OutputStream os = null;
        try {
            response.setHeader("Content-Type", "application/json;charset=utf-8");
            response.setHeader("Cache-Control", "no-cache");
            if (callback != null && callback.trim().length() > 0) {
                content = callback + "&&" + callback + "(" + content + ");";
            }

            byte[] bs = content.getBytes("utf-8");
            response.setContentLength(bs.length);
            os = response.getOutputStream();
            os.write(bs);
            os.flush();
            os.close();
            os = null;
        } catch (Exception exp) {
            exp.printStackTrace();
            response.setStatus(407);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
        }
    }

    protected void outJson(HttpServletResponse response, String content) {

        OutputStream os = null;
        try {
            response.setHeader("Content-Type", "application/json;charset=utf-8");
            response.setHeader("Cache-Control", "no-cache");
            byte[] bs = content.getBytes("utf-8");
            response.setContentLength(bs.length);
            os = response.getOutputStream();
            os.write(bs);
            os.flush();
        } catch (Exception exp) {
            exp.printStackTrace();
            response.setStatus(407);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
        }
    }

    protected Map<String, Object> parseParams(HttpServletRequest request) {
        Map<String, Object> requestMap = new HashMap<String, Object>();
        try {
            @SuppressWarnings({"rawtypes"})
            Map map = request.getParameterMap();
            for (Object key : map.keySet()) {
                requestMap.put(key.toString(), request.getParameter(key.toString()));
            }
            return requestMap;
        } catch (Exception ex) {
            log.error("parse param error !", ex);
        }
        return requestMap;
    }

    protected void outByte(HttpServletResponse response, byte[] e) {
        OutputStream os = null;

        try {
            response.setHeader("Content-Type", "application/octet-stream;charset=utf-8");
            response.setHeader("Cache-Control", "no-cache");
            //byte[] e = content.getBytes("utf-8");
            response.setContentLength(e.length);
            os = response.getOutputStream();
            os.write(e);
            os.flush();
        } catch (Exception var13) {
            var13.printStackTrace();
            response.setStatus(407);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException var12) {
                    ;
                }
            }

        }
    }

    protected String getString(HttpServletRequest request, String key, String defaultValue, boolean encode) {
        String value = request.getParameter(key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }

        String s = request.getParameter(key);

        try {
            if (s != null && encode) {
                s = new String(s.getBytes("iso-8859-1"), "utf-8");
            }
            return s;
        } catch (Exception exp) {
            return defaultValue;
        }
    }
}
