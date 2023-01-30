package org.telegram.telegrambots.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.context.TelegramRequestAttributes;
import org.telegram.telegrambots.context.TelegramRequestContextHolder;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Telegram Filter that exposes the request to the current thread,
 * through {@link TelegramRequestContextHolder} object.
 *
 * @author Dmitry Ivanov
 * @since 6.5
 */
@Component
public class TelegramRequestContextFilter implements TelegramFilter, Ordered {

    private final Log logger = LogFactory.getLog(getClass());

    private int order = -105;

    @Override
    public void doFilter(Update update, TelegramFilterChain filterChain) {
        final TelegramRequestAttributes requestAttributes = new TelegramRequestAttributes(update);

        initContextHolders(update, requestAttributes);

        try {
            filterChain.doFilter(update);
        } finally {
            resetContextHolders();
            if (logger.isTraceEnabled()) {
                logger.trace("Cleared thread-bound request context: " + update);
            }
        }
    }

    private void initContextHolders(Update update, TelegramRequestAttributes requestAttributes) {
        TelegramRequestContextHolder.setRequestAttributes(requestAttributes);
        if (logger.isTraceEnabled()) {
            logger.trace("Bound request context to thread: " + update);
        }
    }

    private void resetContextHolders() {
        TelegramRequestContextHolder.resetRequestAttributes();
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}
