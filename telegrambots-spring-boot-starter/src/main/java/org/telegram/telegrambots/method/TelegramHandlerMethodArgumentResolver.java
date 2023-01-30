package org.telegram.telegrambots.method;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramHandlerMethodArgumentResolver {

    /**
     * Whether the given {@linkplain MethodParameter method parameter} is supported by this resolver.
     *
     * @param parameter the method parameter to check
     * @return {@code true} if this resolver supports the supplied parameter; {@code false} otherwise
     */
    boolean supportsParameter(MethodParameter parameter);

    /**
     * Resolves a method parameter into an argument value from a given request.
     *
     * @param parameter the method parameter to resolve. This parameter must have previously been
     *                  passed to {@link #supportsParameter} which must have returned {@code true}.
     * @return the resolved argument value, or {@code null} if not resolvable
     * @throws Exception in case of errors with the preparation of argument values
     */
    @Nullable
    Object resolveArgument(MethodParameter parameter, Update update) throws Exception;

}
