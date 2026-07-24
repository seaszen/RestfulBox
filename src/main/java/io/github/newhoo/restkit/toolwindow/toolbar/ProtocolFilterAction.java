package io.github.newhoo.restkit.toolwindow.toolbar;

import com.intellij.execution.runners.ExecutionUtil;
import com.intellij.ide.IdeBundle;
import com.intellij.ide.util.ElementsChooser;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Toggleable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.openapi.util.Condition;
import com.intellij.util.containers.ContainerUtil;
import io.github.newhoo.restkit.common.RestDataKey;
import io.github.newhoo.restkit.common.RestItem;
import io.github.newhoo.restkit.config.ConfigHelper;
import io.github.newhoo.restkit.i18n.RestBundle;
import io.github.newhoo.restkit.intellij.BaseToggleAction;
import io.github.newhoo.restkit.restful.RequestHelper;
import io.github.newhoo.restkit.restful.RestClient;
import io.github.newhoo.restkit.toolwindow.ToolWindowHelper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Protocol filter for service tree.
 */
public class ProtocolFilterAction extends BaseToggleAction {
    private JBPopup myFilterPopup;

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null || project.isDefault()) {
            return;
        }
        Icon icon = getTemplatePresentation().getIcon();
        e.getPresentation().setIcon(isActive(project) ? ExecutionUtil.getLiveIndicator(icon) : icon);
        e.getPresentation().setEnabledAndVisible(true);
        e.getPresentation().setText(() -> RestBundle.message("toolkit.toolwindow.toolbar.protocolfilter.action.text"));
        Toggleable.setSelected(e.getPresentation(), isSelected(e));
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        return myFilterPopup != null && !myFilterPopup.isDisposed();
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        if (state) {
            showPopup(e);
        } else if (myFilterPopup != null && !myFilterPopup.isDisposed()) {
            myFilterPopup.cancel();
        }
    }

    private boolean isActive(Project myProject) {
        return !ConfigHelper.getCommonSetting(myProject).getFilterProtocols().isEmpty();
    }

    private void showPopup(AnActionEvent e) {
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        if (myFilterPopup != null) {
            return;
        }
        JBPopupListener popupCloseListener = new JBPopupListener() {
            @Override
            public void onClosed(@NotNull LightweightWindowEvent event) {
                myFilterPopup = null;
            }
        };
        myFilterPopup = JBPopupFactory.getInstance()
                                      .createComponentPopupBuilder(createFilterPanel(e, project), null)
                                      .setModalContext(false)
                                      .setFocusable(true)
                                      .setRequestFocus(true)
                                      .setResizable(true)
                                      .setMinSize(new Dimension(200, 220))
                                      .addListener(popupCloseListener)
                                      .createPopup();
        Component anchor = e.getInputEvent().getComponent();
        if (anchor.isValid()) {
            myFilterPopup.showUnderneathOf(anchor);
        } else {
            Component component = e.getData(PlatformDataKeys.CONTEXT_COMPONENT);
            if (component != null) {
                myFilterPopup.showUnderneathOf(component);
            } else {
                myFilterPopup.showInFocusCenter();
            }
        }
    }

    private JComponent createFilterPanel(AnActionEvent e, Project project) {
        ElementsChooser<?> chooser = createChooser(e, project);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(chooser);
        JPanel buttons = new JPanel();
        JButton all = new JButton(IdeBundle.message("big.popup.filter.button.all"));
        all.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                chooser.setAllElementsMarked(true);
            }
        });
        buttons.add(all);
        JButton none = new JButton(IdeBundle.message("big.popup.filter.button.none"));
        none.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                chooser.setAllElementsMarked(false);
            }
        });
        buttons.add(none);
        JButton invert = new JButton(IdeBundle.message("big.popup.filter.button.invert"));
        invert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                chooser.invertSelection();
            }
        });
        buttons.add(invert);
        panel.add(buttons);
        return panel;
    }

    private ElementsChooser<String> createChooser(AnActionEvent e, @NotNull Project project) {
        List<String> protocols = collectProtocols(e, project);
        Set<String> filterProtocols = ConfigHelper.getCommonSetting(project).getFilterProtocols();
        ElementsChooser<String> res = new ElementsChooser<String>(protocols, false) {
            @Override
            protected String getItemText(@NotNull String value) {
                return value;
            }
        };
        res.markElements(ContainerUtil.filter(protocols, new Condition<String>() {
            @Override
            public boolean value(String protocol) {
                return !filterProtocols.contains(protocol);
            }
        }));
        ElementsChooser.ElementsMarkListener<String> listener = (element, isMarked) -> {
            if (isMarked) {
                filterProtocols.remove(element);
            } else {
                filterProtocols.add(element);
            }
            ToolWindowHelper.scheduleUpdateTree(project);
        };
        res.addElementsMarkListener(listener);
        return res;
    }

    private List<String> collectProtocols(AnActionEvent e, Project project) {
        Set<String> protocols = new LinkedHashSet<>();
        List<RestItem> allService = RestDataKey.ALL_SERVICE.getData(e.getDataContext());
        if (allService != null) {
            for (RestItem item : allService) {
                if (StringUtils.isNotEmpty(item.getProtocol())) {
                    protocols.add(item.getProtocol());
                }
            }
        }
        for (RestClient client : RequestHelper.getRestClient()) {
            if (StringUtils.isNotEmpty(client.getProtocol())) {
                protocols.add(client.getProtocol());
            }
        }
        return new ArrayList<>(protocols);
    }
}
