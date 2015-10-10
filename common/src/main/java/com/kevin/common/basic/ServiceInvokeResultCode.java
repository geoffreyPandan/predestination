package com.kevin.common.basic;

/**
 * ServiceInvokeResultCode
 *
 * @author: sunjie
 * @date: 15/9/25
 */
public enum ServiceInvokeResultCode {

    SUCCESS(0, "成功"),

    SERVICENAME_NOT_EXIST(-1, "service不存在"),

    METHODNAME_NOT_EXIST(-2, "method不存在，或者他的参数名称不对"),

    GET_METHOD_EXCEPTION(-3, "获取反射方法异常"),

    INVOKE_METHOD_EXCEPTION(-4, "反射调用方法异常"),

    REQUEST_PARAMETER_NOT_EXIST(-5, "请求参数不合法"),

    NOT_FOUND_EXCEPTION_FROM_JAVASSIST(-6, "来自于Javassist的Class NotFoundException异常"),

    GET_AGENT_ERROR(-7, "获取代理对象的目标对象失败");

    private int    code;

    private String desc;

    ServiceInvokeResultCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
