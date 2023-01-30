package org.telegram.telegrambots.method;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class TelegramInvocableHandlerMethod extends TelegramHandlerMethod {

    private static final Object[] EMPTY_ARGS = new Object[0];

    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    private TelegramHandlerMethodArgumentResolver argumentResolver = new TelegramPathVariableMethodArgumentResolver();

    public TelegramInvocableHandlerMethod(TelegramHandlerMethod telegramHandlerMethod) {
        super(telegramHandlerMethod);
    }

    @Nullable
    public Object invokeForRequest(Update update) throws Exception {
        Object[] args = getMethodArgumentValues(update);
        if (logger.isTraceEnabled()) {
            logger.trace("Arguments: " + Arrays.toString(args));
        }
        return doInvoke(args);
    }

    protected Object[] getMethodArgumentValues(Update update) throws Exception {
        MethodParameter[] parameters = getMethodParameters();
        if (ObjectUtils.isEmpty(parameters)) {
            return EMPTY_ARGS;
        }

        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            MethodParameter parameter = parameters[i];
            parameter.initParameterNameDiscovery(this.parameterNameDiscoverer);
            args[i] = this.argumentResolver.resolveArgument(parameter, update);
        }

        return args;
    }

    /**
     * Invoke the handler method with the given argument values.
     */
    @Nullable
    protected Object doInvoke(Object... args) throws Exception {
        Method method = getBridgedMethod();
        try {
            return method.invoke(getBean(), args);
        } catch (IllegalArgumentException ex) {
            assertTargetBean(method, getBean(), args);
            String text = (ex.getMessage() == null || ex.getCause() instanceof NullPointerException) ?
                    "Illegal argument" : ex.getMessage();
            throw new IllegalStateException(formatInvokeError(text, args), ex);
        } catch (InvocationTargetException ex) {
            // Unwrap for HandlerExceptionResolvers ...
            Throwable targetException = ex.getCause();
            if (targetException instanceof RuntimeException runtimeException) {
                throw runtimeException;
            } else if (targetException instanceof Error error) {
                throw error;
            } else if (targetException instanceof Exception exception) {
                throw exception;
            } else {
                throw new IllegalStateException(formatInvokeError("Invocation failure", args), targetException);
            }
        }
    }

}
