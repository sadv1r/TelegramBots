package org.telegram.telegrambots.context;

import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;

/**
 * Holder class to expose the web request in the form of a thread-bound {@link TelegramRequestAttributes} object.
 *
 * @author Dmitry Ivanov
 * @since 6.5
 */
public abstract class TelegramRequestContextHolder {

    private static final ThreadLocal<TelegramRequestAttributes> requestAttributesHolder =
            new NamedThreadLocal<>("Telegram Request attributes");

    /**
     * Reset the TelegramRequestAttributes for the current thread.
     */
    public static void resetRequestAttributes() {
        requestAttributesHolder.remove();
    }

    /**
     * Bind the given TelegramRequestAttributes to the current thread.
     *
     * @param attributes the TelegramRequestAttributes to expose
     */
    public static void setRequestAttributes(@Nullable TelegramRequestAttributes attributes) {
        requestAttributesHolder.set(attributes);
    }

    /**
     * Return the TelegramRequestAttributes currently bound to the thread.
     *
     * @return the TelegramRequestAttributes currently bound to the thread,
     * or {@code null} if none bound
     */
    @Nullable
    public static TelegramRequestAttributes getRequestAttributes() {
        return requestAttributesHolder.get();
    }

}
