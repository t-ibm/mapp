package cloud.softwareag.log.appender;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.plugins.Configurable;
import org.apache.logging.log4j.plugins.Plugin;
import org.apache.logging.log4j.plugins.PluginAttribute;
import org.apache.logging.log4j.plugins.PluginElement;
import org.apache.logging.log4j.plugins.PluginFactory;
import org.apache.logging.log4j.plugins.validation.constraints.Required;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Configurable(elementType = Appender.ELEMENT_TYPE, printObject = true)
@Plugin(ListAppender.PLUGIN_NAME)
public class ListAppender extends AbstractAppender {
    public static final String PLUGIN_NAME = "List";
    private final List<LogEvent> events = Collections.synchronizedList(new ArrayList<>());
    private final List<String> messages = Collections.synchronizedList(new ArrayList<>());
    private volatile CountDownLatch countDownLatch = null;

    public ListAppender(final String name, final Filter filter, final Layout layout) {
        super(name, filter, layout, true, Property.EMPTY_ARRAY);
        if (layout != null) {
            byte[] bytes = layout.getHeader();
            if (bytes != null) {
                write(bytes);
            }
        }
    }

    public boolean stop(final long timeout, final TimeUnit timeUnit) {
        setStopping();
        super.stop(timeout, timeUnit, false);
        Layout layout = getLayout();
        if (layout != null) {
            byte[] bytes = layout.getFooter();
            if (bytes != null) {
                write(bytes);
            }
        }
        setStopped();
        return true;
    }

    public void append(final LogEvent event) {
        Layout layout = getLayout();
        if (layout == null) {
            events.add(event.toImmutable());
        } else {
            write(layout.toByteArray(event));
        }

        if (countDownLatch != null) {
            countDownLatch.countDown();
        }

    }

    public ListAppender clear() {
        events.clear();
        messages.clear();
        return this;
    }

    public List<LogEvent> getEvents() {
        return List.copyOf(events);
    }

    public List<String> getMessages() {
        return List.copyOf(messages);
    }

    @Override
    public String toString() {
        return "ListAppender{" +
            "events=" + events +
            ", messages=" + messages +
            "} " + super.toString();
    }

    @PluginFactory
    public static ListAppender createAppender(
        @Required @PluginAttribute(defaultString = PLUGIN_NAME) final String name,
        @PluginElement final Layout layout,
        @PluginElement final Filter filter
    ) {
        return new ListAppender(name, filter, layout);
    }

    private void write(final byte[] bytes) {
        String str = new String(bytes);
        messages.add(str);
    }
}
