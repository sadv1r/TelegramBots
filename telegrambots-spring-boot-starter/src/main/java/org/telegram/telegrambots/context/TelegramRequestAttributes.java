package org.telegram.telegrambots.context;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TelegramRequestAttributes {

    private final Update update;

    private final ConcurrentMap<String, Object> attributes = new ConcurrentHashMap<>();

    public TelegramRequestAttributes(Update update) {
        Assert.notNull(update, "Update must not be null");
        this.update = update;
    }

    /**
     * Exposes the native {@link Update} that we're wrapping.
     */
    public Update getUpdate() {
        return update;
    }

    /**
     * Return the value for the attribute of the given name, if any.
     *
     * @param name the name of the attribute
     * @return the current attribute value, or {@code null} if not found
     */
    @Nullable
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    /**
     * Set the value for the attribute of the given name,
     * replacing an existing value (if any).
     *
     * @param name  the name of the attribute
     * @param value the value for the attribute
     */
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    /**
     * Remove the attribute of the given name, if it exists.
     *
     * @param name the name of the attribute
     */
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    /**
     * Retrieve the names of all attributes in the scope.
     *
     * @return the attribute names as String array
     */
    String[] getAttributeNames() {
        return StringUtils.toStringArray(attributes.keySet());
    }

}
