package io.github.newhoo.restkit.toolwindow.tree;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import io.github.newhoo.restkit.common.RestDataKey;
import io.github.newhoo.restkit.common.RestItem;
import io.github.newhoo.restkit.i18n.RestBundle;
import io.github.newhoo.restkit.intellij.BaseAnAction;
import io.github.newhoo.restkit.toolwindow.RestServiceListener;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Generate request for selected tree node(s) in a new client tab.
 */
public class GenerateRequestAction extends BaseAnAction implements DumbAware {

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setText(() -> RestBundle.message("toolkit.toolwindow.tree.generaterequest.action.text"));
        List<RestItem> serviceItems = RestDataKey.SELECTED_SERVICE.getData(e.getDataContext());
        e.getPresentation().setEnabledAndVisible(CollectionUtils.isNotEmpty(serviceItems));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        List<RestItem> serviceItems = RestDataKey.SELECTED_SERVICE.getData(e.getDataContext());
        if (project == null || CollectionUtils.isEmpty(serviceItems)) {
            return;
        }
        for (RestItem serviceItem : serviceItems) {
            project.getMessageBus().syncPublisher(RestServiceListener.REST_SERVICE_SELECT).generateInNewTab(serviceItem);
        }
    }
}
