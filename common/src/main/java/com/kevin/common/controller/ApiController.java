package com.kevin.common.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * ApiController
 *
 * @author: sunjie
 * @date: 15/9/25
 */
@Controller
@RequestMapping("/controller")
public class ApiController extends BaseController {

    @RequestMapping(value = "/{serviceName}/{methodName}.do", method = RequestMethod.GET)
    public void doGet(@PathVariable String serviceName, @PathVariable String methodName, HttpServletRequest request,
                      HttpServletResponse response) {
        Map<String, Object> requestMap = parseParams(request);
        this.processRequest(serviceName, methodName, requestMap, request, response);
    }

    @RequestMapping(value = "/{serviceName}/{methodName}.do", method = {RequestMethod.POST})
    public void doPost(@PathVariable String serviceName, @PathVariable String methodName,
                       @RequestBody Map<String, Object> requestMap, HttpServletRequest request,
                       HttpServletResponse response) {
        this.processRequest(serviceName, methodName, requestMap, request, response);
    }

}
