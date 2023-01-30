package org.telegram.telegrambots.method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TelegramHandlerMethod {

    protected static final Log logger = LogFactory.getLog(TelegramHandlerMethod.class);

    private final Object bean;

    private final Class<?> beanType;

    private final Method method;

    private final Method bridgedMethod;

    private final MethodParameter[] parameters;

    /**
     * Create an instance from a bean instance and a method.
     */
    public TelegramHandlerMethod(Object bean, Method method) {
        Assert.notNull(bean, "Bean is required");
        Assert.notNull(method, "Method is required");
        this.bean = bean;
        this.beanType = ClassUtils.getUserClass(bean);
        this.method = method;
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
        this.parameters = initMethodParameters();
    }

    /**
     * Copy constructor for use in subclasses.
     */
    public TelegramHandlerMethod(TelegramHandlerMethod telegramHandlerMethod) {
        Assert.notNull(telegramHandlerMethod, "TelegramHandlerMethod is required");
        this.bean = telegramHandlerMethod.bean;
        this.beanType = telegramHandlerMethod.beanType;
        this.method = telegramHandlerMethod.method;
        this.bridgedMethod = telegramHandlerMethod.bridgedMethod;
        this.parameters = telegramHandlerMethod.parameters;
    }

    private MethodParameter[] initMethodParameters() {
        int count = this.bridgedMethod.getParameterCount();
        MethodParameter[] result = new MethodParameter[count];
        for (int i = 0; i < count; i++) {
            final SynthesizingMethodParameter methodParameter = new SynthesizingMethodParameter(this.bridgedMethod, i);
            methodParameter.withContainingClass(this.beanType);
            result[i] = methodParameter;
        }
        return result;
    }

    /**
     * Return the bean for this handler method.
     */
    public Object getBean() {
        return this.bean;
    }

    /**
     * This method returns the type of the handler for this handler method.
     * <p>Note that if the bean type is a CGLIB-generated class, the original
     * user-defined class is returned.
     */
    public Class<?> getBeanType() {
        return this.beanType;
    }

    /**
     * Return the method for this handler method.
     */
    public Method getMethod() {
        return this.method;
    }

    /**
     * If the bean method is a bridge method, this method returns the bridged
     * (user-defined) method. Otherwise, it returns the same method as {@link #getMethod()}.
     */
    protected Method getBridgedMethod() {
        return this.bridgedMethod;
    }

    /**
     * Return the method parameters for this handler method.
     */
    public MethodParameter[] getMethodParameters() {
        return this.parameters;
    }

    /**
     * Return the actual return value type.
     */
    public MethodParameter getReturnValueType(@Nullable Object returnValue) {
        return new ReturnValueMethodParameter(returnValue);
    }

    /**
     * A MethodParameter for a TelegramHandlerMethod return type based on an actual return value.
     */
    private class ReturnValueMethodParameter extends SynthesizingMethodParameter {

        @Nullable
        private final Class<?> returnValueType;

        public ReturnValueMethodParameter(@Nullable Object returnValue) {
            super(TelegramHandlerMethod.this.bridgedMethod, -1);
            this.returnValueType = (returnValue != null ? returnValue.getClass() : null);
        }

        protected ReturnValueMethodParameter(ReturnValueMethodParameter original) {
            super(original);
            this.returnValueType = original.returnValueType;
        }

        @Override
        public Class<?> getParameterType() {
            return (this.returnValueType != null ? this.returnValueType : super.getParameterType());
        }

        @Override
        public ReturnValueMethodParameter clone() {
            return new ReturnValueMethodParameter(this);
        }
    }

    /**
     * Assert that the target bean class is an instance of the class where the given
     * method is declared. In some cases the actual controller instance at request-
     * processing time may be a JDK dynamic proxy (lazy initialization, prototype
     * beans, and others). {@code @Controller}'s that require proxying should prefer
     * class-based proxy mechanisms.
     */
    protected void assertTargetBean(Method method, Object targetBean, Object[] args) {
        Class<?> methodDeclaringClass = method.getDeclaringClass();
        Class<?> targetBeanClass = targetBean.getClass();
        if (!methodDeclaringClass.isAssignableFrom(targetBeanClass)) {
            String text = "The mapped handler method class '" + methodDeclaringClass.getName() +
                          "' is not an instance of the actual controller bean class '" +
                          targetBeanClass.getName() + "'. If the controller requires proxying " +
                          "(e.g. due to @Transactional), please use class-based proxying.";
            throw new IllegalStateException(formatInvokeError(text, args));
        }
    }

    protected String formatInvokeError(String text, Object[] args) {
        String formattedArgs = IntStream.range(0, args.length)
                .mapToObj(i -> (args[i] != null ?
                        "[" + i + "] [type=" + args[i].getClass().getName() + "] [value=" + args[i] + "]" :
                        "[" + i + "] [null]"))
                .collect(Collectors.joining(",\n", " ", " "));
        return text + "\n" +
               "Controller [" + getBeanType().getName() + "]\n" +
               "Method [" + getBridgedMethod().toGenericString() + "] " +
               "with argument values:\n" + formattedArgs;
    }

}
