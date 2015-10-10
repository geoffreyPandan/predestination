package com.kevin.common.basic;

import org.apache.commons.lang.StringUtils;

/**
 * AnswerBuilder
 *
 * @author: sunjie
 * @date: 15/9/25
 */
public abstract class AnswerBuilder {

    public static Answer<?> buildExceptionAnswer(Exception e) {
        Answer<?> answer;
        answer = new Answer<Object>();
        answer.setCode(ExceptionCodeEnum.INNER_ERROR.getCode());
        if (e.getCause() != null) {
            String msg = StringUtils.isBlank(e.getMessage()) ? "" : e.getMessage();
            answer.setMsg(msg + (StringUtils.isBlank(e.getCause().getMessage()) ? "" : "|" + e.getCause().getMessage()));
        } else {
            answer.setMsg(e.getMessage());
        }
        return answer;
    }
}
