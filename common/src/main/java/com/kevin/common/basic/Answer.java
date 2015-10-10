package com.kevin.common.basic;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.data.annotation.Transient;

/**
 * 响应共同结构
 *
 * @author Administrator
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Answer<T> {
    private Integer cmd;
    private String  serviceName;
    private String  methodName;
    private Integer code;
    private String  msg;

    @Transient
    private Boolean localMatch;
    private T       result;

    public Answer() {
        code = 0;
    }

    @SuppressWarnings("rawtypes")
    public static Answer<?> newBuilder() {
        return new Answer();
    }

    public T getResult() {
        return result;
    }

    public Answer<T> setResult(T result) {
        this.result = result;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public Answer<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public Answer<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Boolean getLocalMatch() {
        return localMatch;
    }

    public Answer<T> setLocalMatch(Boolean localMatch) {
        this.localMatch = localMatch;
        return this;
    }

    public Answer<T> setCodeAndMsg(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Integer getCmd() {
        return cmd;
    }

    public void setCmd(Integer cmd) {
        this.cmd = cmd;
    }

}
