package com.kevin.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.Descriptor;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.google.common.base.Preconditions;
import com.kevin.common.basic.Answer;
import com.kevin.common.basic.ServiceInvokeResultCode;

/**
 * ServiceInvocationUtil
 *
 * @author: sunjie
 * @date: 15/9/25
 */
public class ServiceInvocationUtil {

    private static final Logger    LOGGER    = LoggerFactory.getLogger(ServiceInvocationUtil.class);

    private static final ClassPool CLASSPOOL = ClassPool.getDefault();

    /**
     * 初始化类路径
     */
    static public void initClassPath(String beanId) {
        CLASSPOOL.insertClassPath(new ClassClassPath(SpringContextHolder.getBean(beanId).getClass()));
    }

    /**
     * 通过反射向外提供接口调用
     * note:1.不支持形参为原始类型的方法 2,set类型的形参，在json反序列化过程中无法保证顺序，如果需要顺序，请选用List，Array
     *
     * @param serviceName serviceName
     * @param methodName  服务方法名
     * @param parameters  方法参数列表
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    static public Answer invoke(String serviceName, String methodName, Map<String, Object> parameters) {
        Preconditions.checkArgument(StringUtils.isNotBlank(serviceName), "serviceName is null or empty");
        Preconditions.checkArgument(StringUtils.isNotBlank(methodName), "methodName is null or empty");

        Answer answer = new Answer();
        answer.setServiceName(serviceName);
        answer.setMethodName(methodName);

        Object proxy = null;
        try {
            //通过spring container获取serviceName对应的对象，该对象为Proxy
            proxy = SpringContextHolder.getBean(serviceName);
        } catch (NoSuchBeanDefinitionException e1) {
            //LOGGER.debug("获取服务对象不存在", e1);
            answer.setCodeAndMsg(ServiceInvokeResultCode.SERVICENAME_NOT_EXIST.getCode(),
                    ServiceInvokeResultCode.SERVICENAME_NOT_EXIST.getDesc());
            return answer;
        }

        Object target = null;
        try {
            target = getTarget(proxy);
        } catch (Exception e1) {
            LOGGER.error("获取代理对象的目标对象失败", e1);
            answer.setCodeAndMsg(ServiceInvokeResultCode.GET_AGENT_ERROR.getCode(),
                    ServiceInvokeResultCode.GET_AGENT_ERROR.getDesc());
            return answer;
        }
        if (null == target) {
            LOGGER.error(ServiceInvokeResultCode.SERVICENAME_NOT_EXIST.getDesc());
            answer.setCodeAndMsg(ServiceInvokeResultCode.SERVICENAME_NOT_EXIST.getCode(),
                    ServiceInvokeResultCode.SERVICENAME_NOT_EXIST.getDesc());
            return answer;
        }

        //javassist加载serviceName对应的Class文件
        CtClass ctClass = null;
        boolean loadCtClassSuccess = true;
        try {
            ctClass = CLASSPOOL.get(target.getClass().getName());
        } catch (NotFoundException e) {
            LOGGER.error(ServiceInvokeResultCode.NOT_FOUND_EXCEPTION_FROM_JAVASSIST.getDesc(), e);
            loadCtClassSuccess = false;
        }

        if (null == ctClass || !loadCtClassSuccess) {
            return logJavassistErrorMsg(answer);
        }

        MethodDesc methodDesc = null;
        try {
            methodDesc = getMethodFromCache(serviceName, methodName, parameters, target);
        } catch (Exception e1) {
            LOGGER.error("获取方法异常", e1);
            answer.setCodeAndMsg(ServiceInvokeResultCode.GET_METHOD_EXCEPTION.getCode(),
                    ServiceInvokeResultCode.GET_METHOD_EXCEPTION.getDesc());
            return answer;
        }

        if (null == methodDesc) {
            LOGGER.warn("没有发现方法");
            answer.setCodeAndMsg(ServiceInvokeResultCode.METHODNAME_NOT_EXIST.getCode(),
                    ServiceInvokeResultCode.METHODNAME_NOT_EXIST.getDesc());
            return answer;
        }

        //循环对targetMethod的入参赋值
        Object[] targetMethodArgs = new Object[methodDesc.targetMethod_.getParameterTypes().length];
        int i = 0;
        for (Map.Entry<String, Type> entry : methodDesc.parameterType_.entrySet()) {
            //获取方法参数名;
            String methodParameterName = entry.getKey();
            //获取api调用者传递过来的调用参数
            Object val = parameters.get(methodParameterName);
            String methodParameterValue = val instanceof String ? (String) val : JsonUtil.jsonFromObject(val);
            //检查掉接口调用方传来的参数是否为空
            if (null == methodParameterValue) {
                LOGGER.error("serviceName = " + serviceName + " ,methodName = " + methodName + " ,参数: "
                        + methodParameterName + "为空");
                answer.setCodeAndMsg(ServiceInvokeResultCode.REQUEST_PARAMETER_NOT_EXIST.getCode(),
                        ServiceInvokeResultCode.REQUEST_PARAMETER_NOT_EXIST.getDesc());
                return answer;
            }
            Type type = entry.getValue();
            //检查是否为String类型
            //String类型直接赋值
            if (type instanceof Class && ((Class) type).isAssignableFrom(String.class)) {
                targetMethodArgs[i++] = methodParameterValue;
            } else {
                targetMethodArgs[i++] = JacksonObjectMapper.objectFromJson(methodParameterValue, type);
            }
        }

        //反射调用接口
        Object ret = null;
        try {
            ret = methodDesc.targetMethod_.invoke(target, targetMethodArgs);
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder(256);
            sb.append("反射调用接口失败,serviceName=");
            sb.append(serviceName);
            sb.append(" ,methodName=");
            sb.append(methodName);
            sb.append(" class:");
            sb.append(target.getClass().toString());
            sb.append("/");
            sb.append(target.toString());
            LOGGER.error(sb.toString(), e);
            answer.setCodeAndMsg(ServiceInvokeResultCode.INVOKE_METHOD_EXCEPTION.getCode(),
                    ServiceInvokeResultCode.INVOKE_METHOD_EXCEPTION.getDesc());
            return answer;
        }
        if (null != ret) {
            answer.setResult(ret);
        }

        answer.setCode(ServiceInvokeResultCode.SUCCESS.getCode());
        answer.setMsg(ServiceInvokeResultCode.SUCCESS.getDesc());
        return answer;
    }

    static private Map<String, List<MethodDesc>> methodCache = new HashMap<String, List<MethodDesc>>();

    /**
     * 先从缓存中获取方法，以增加速度
     * @param serviceName
     * @param methodName
     * @param parameters
     * @param targetBean
     * @return
     * @throws Exception
     */
    static private MethodDesc getMethodFromCache(String serviceName, String methodName, Map<String, Object> parameters,
                                                 Object targetBean) throws Exception {
        String id = serviceName + "/" + methodName;

        //用proxy bean对象来减小锁的粒度，
        synchronized (targetBean) {
            List<MethodDesc> list = methodCache.get(id);
            if (list != null) {
                for (MethodDesc methodDesc : list) {
                    if (compareMethodParameters(methodDesc.paramenterNames_, parameters)) {
                        return methodDesc;
                    }
                }
            }

            //cache中没有，那么从类中寻找
            MethodDesc methodDesc = findMethodByParameterNames(serviceName, methodName, parameters, targetBean);

            if (methodDesc != null) {//找到了匹配的方法。 并加入到cache中
                if (list != null) {
                    list.add(methodDesc);
                } else {
                    list = new ArrayList<MethodDesc>();
                    list.add(methodDesc);
                    methodCache.put(id, list);
                }
            }
            return methodDesc;
        }
    }

    /*
     * 通过参数名，寻找匹配的方法
     */
    static private MethodDesc findMethodByParameterNames(String serviceName, String methodName,
                                                         Map<String, Object> parameters, Object targetBean)
            throws Exception {
        //javassist加载serviceName对应的Class文件
        CtClass ctClass = null;
        boolean loadCtClassSuccess = true;
        try {
            ctClass = CLASSPOOL.get(targetBean.getClass().getName());
        } catch (NotFoundException e) {
            LOGGER.warn("调用javassist 异常:" + e.toString());
            loadCtClassSuccess = false;
        }

        if (null == ctClass || !loadCtClassSuccess) {
            LOGGER.error("反射调用接口失败, 找不到class,serviceName=" + serviceName + " ,methodName=" + methodName);
            return null;
        }

        CtMethod[] ctMethods = null;
        try {
            ctMethods = ctClass.getMethods();
            if (ctMethods != null) {
                for (CtMethod ctMethod : ctMethods) {
                    if (methodName.equals(ctMethod.getName())) {
                        List<String> nameList = getParameterNames(ctMethod);
                        if (compareMethodParameters(nameList, parameters)) {//匹配到方法。
                            MethodDesc methodDesc = new MethodDesc();
                            methodDesc.id_ = serviceName + "/" + methodName;
                            methodDesc.paramenterNames_ = nameList;
                            methodDesc.serviceName_ = serviceName;
                            methodDesc.methodName_ = methodName;
                            //寻找对应的jdk method.
                            if (findJDKMethod(methodDesc, ctMethod, targetBean)) {
                                return methodDesc;
                            } else {
                                return null;
                            }
                        }
                    }
                }
            }

        } catch (NotFoundException e) {
            LOGGER.error("反射调用接口失败, serviceName=" + serviceName + " ,methodName=" + methodName + " " + e.toString());
        }

        return null;
    }

    /**
     * 寻找和CtMethod想匹配的jdk method
     */
    static private boolean findJDKMethod(MethodDesc methodDesc, final CtMethod ctMethod, final Object targetBean)
            throws Exception {
        Method[] methods = targetBean.getClass().getMethods();

        for (Method method : methods) {
            if (method.getName().equals(methodDesc.methodName_)) {
                if (compareMethod(method, ctMethod)) {//找到了jdk method, 取出这个method的形参的type
                    Type[] paramterTypes = method.getGenericParameterTypes();

                    // 单个方法形参名称与形参类型的映射关系,保证有序
                    Map<String, Type> map = new LinkedHashMap<String, Type>();

                    for (int i = 0; i < methodDesc.paramenterNames_.size(); i++) {
                        map.put(methodDesc.paramenterNames_.get(i), paramterTypes[i]);
                    }
                    //修改parameterType_
                    methodDesc.parameterType_ = map;
                    methodDesc.targetMethod_ = method;
                    return true;
                }
            }
        }
        return false;
    }

    static private boolean compareMethod(Method method, CtMethod ctMethod) throws Exception {
        Class<?>[] classes = method.getParameterTypes();

        CtClass[] ctClasses = ctMethod.getParameterTypes();

        if (ctMethod.getParameterTypes().length != classes.length) {
            return false;
        }

        for (int i = 0; i < ctClasses.length; ++i) {

            //String pClassName = Descriptor.toClassName(localVariableAttribute.descriptor(i ));
            String pClassName = Descriptor.toJavaName(Descriptor.toJvmName(ctClasses[i]));

            if (!pClassName.equals(classes[i].getName())) {
                return false;
            } else {

            }
        }
        return true;
    }

    /**
     * 根据参数名称比较形参和入参是否匹配
     * @param parameterNames
     * @param parameters
     * @return
     */
    static private boolean compareMethodParameters(List<String> parameterNames, Map<String, Object> parameters) {
        if (parameterNames.size() != parameters.size()) {
            return false;
        }

        if (parameterNames.size() == 0) {
            return true;
        }

        Set<String> keys = parameters.keySet();
        for (String key : keys) {
            if (!parameterNames.contains(key)) {
                return false;
            }
        }
        return true;
    }

    static private class MethodDesc {
        //id是serviceName + "/" +methodName
        public String            id_;
        public String            serviceName_;
        public String            methodName_;
        public Method            targetMethod_;
        public Map<String, Type> parameterType_;
        public List<String>      paramenterNames_;
    }

    /**
     * 返回方法形参名称(通过javassist获取)
     *
     * @param ctMethod Javassist中对方法的映射实现
     * @throws NotFoundException
     */
    public static List<String> getParameterNames(CtMethod ctMethod) throws NotFoundException {
        MethodInfo info = ctMethod.getMethodInfo();
        CodeAttribute codeAttribute = info.getCodeAttribute();
        LocalVariableAttribute localVariableAttribute = (LocalVariableAttribute) codeAttribute
                .getAttribute(LocalVariableAttribute.tag);
        // 非static的方法，默认第一个参数为this， 忽略this
        int startPos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;

        List<String> nameList = new ArrayList<String>();

        int n = localVariableAttribute.tableLength();
        int maxSize = ctMethod.getParameterTypes().length;
        for (int i = startPos; i < n && i < startPos + maxSize; i++) {
            nameList.add(localVariableAttribute.variableName(i));
        }
        return nameList;
    }

    /**
     *  记录javassist相关的错误日志
     */
    @SuppressWarnings("rawtypes")
    private static Answer logJavassistErrorMsg(Answer answer) {
        LOGGER.error(ServiceInvokeResultCode.NOT_FOUND_EXCEPTION_FROM_JAVASSIST.getDesc());
        answer.setCodeAndMsg(ServiceInvokeResultCode.NOT_FOUND_EXCEPTION_FROM_JAVASSIST.getCode(),
                ServiceInvokeResultCode.NOT_FOUND_EXCEPTION_FROM_JAVASSIST.getDesc());
        return answer;
    }

    /**
     * 获取 目标对象
     * @param proxy 代理对象
     * @return
     * @throws Exception
     */
    public static Object getTarget(Object proxy) throws Exception {

        if (!AopUtils.isAopProxy(proxy)) {
            return proxy;//不是代理对象
        }

        if (AopUtils.isJdkDynamicProxy(proxy)) {
            return getJdkDynamicProxyTargetObject(proxy);
        } else { //cglib
            return getCglibProxyTargetObject(proxy);
        }

    }

    private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);

        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);

        Object target = ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();

        return target;
    }

    private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy) h.get(proxy);

        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);

        Object target = ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();

        return target;
    }

}

