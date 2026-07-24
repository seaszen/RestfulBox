package io.github.newhoo.restkit.toolwindow;

import com.intellij.util.messages.Topic;
import io.github.newhoo.restkit.common.RestItem;

/**
 * RestServiceListener
 *
 * @author newhoo
 * @since 1.0.8
 */
public interface RestServiceListener {

    Topic<RestServiceListener> REST_SERVICE_SELECT = Topic.create("RestServiceSelect", RestServiceListener.class);

    /** Fill the currently selected client tab. */
    void select(RestItem serviceItem);

    /** Create a new client tab (when multi-tab enabled) and fill it. */
    void generateInNewTab(RestItem serviceItem);
}
