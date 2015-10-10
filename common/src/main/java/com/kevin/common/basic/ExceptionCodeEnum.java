package com.kevin.common.basic;

/**
 * ExceptionCodeEnum
 *
 * @author: sunjie
 * @date: 15/9/25
 */
public enum ExceptionCodeEnum implements  IEnum{

    SUCCESS(0, "成功"),

    ENCRPTY_ERROR(106, "生成TOKEN失败"),
    DECRPTY_ERROR(107, "TOKEN解析失败"),
    TOKEN_EMPTY(103, "没有登录，请先登录"),
    TOKEN_INVALID(104, "无效的TOKEN"),
    TOKEN_EXPIRED(105, "登录信息已失效，请尝试注销后重新登录"),
    USER_INVALID(106, "未找到相应的用户ID信息"),
    INNER_ERROR(505, "服务器出错啦"),

    METHOD_NOTFOUND(201, "没有指定的服务方法可被调用"),
    METHOD_INVOKE_ERROR(202, "远程服务调用失败"),
    METHOD_INVOKE_DBERROR(206, "数据库操作异常"),
    REMOTE_PROCESS_ERROR(203, "远程服务处理失败"),
    SERVICE_NOTFOUND(204, "没有指定的服务可被调用"),
    SERVICE_BUSY(205, "服务器繁忙，请稍后再试"),;

    private int code;
    private String message;

    ExceptionCodeEnum(int status, String message) {
        this.code = status;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

