package org.telegram.telegrambots.method;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.sadv1r.telegram.bot.bind.annotation.TelegramCommand;

import java.math.BigDecimal;
import java.math.BigInteger;

public class TelegramPathVariableMethodArgumentResolver implements TelegramHandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> paramType = parameter.getParameterType();
        return parameter.hasParameterAnnotation(TelegramCommand.class)
               && (String.class.isAssignableFrom(paramType)
                   || Integer.class.isAssignableFrom(paramType)
                   || Long.class.isAssignableFrom(paramType)
                   || Double.class.isAssignableFrom(paramType)
                   || Float.class.isAssignableFrom(paramType)
                   || BigInteger.class.isAssignableFrom(paramType)
                   || BigDecimal.class.isAssignableFrom(paramType));
    }

    @Override
    @Nullable
    public Object resolveArgument(MethodParameter parameter, Update update) throws Exception {
        Class<?> paramType = parameter.getParameterType();

        TelegramCommand annotation = parameter.getParameterAnnotation(TelegramCommand.class);

        return null;
    }
}
