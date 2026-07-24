package io.github.newhoo.restkit.toolwindow.toolbar;

import com.intellij.execution.runners.ExecutionUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Toggleable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import io.github.newhoo.restkit.config.ConfigHelper;
import io.github.newhoo.restkit.config.ide.CommonSetting;
import io.github.newhoo.restkit.i18n.RestBundle;
import io.github.newhoo.restkit.intellij.BaseToggleAction;
import io.github.newhoo.restkit.toolwindow.ToolWindowHelper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;

/**
 * Keyword filter for service tree (url / description / packageName).
 */
public class KeywordFilterAction extends BaseToggleAction {
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
        e.getPresentation().setText(() -> RestBundle.message("toolkit.toolwindow.toolbar.keywordfilter.action.text"));
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
        return StringUtils.isNotBlank(ConfigHelper.getCommonSetting(myProject).getFilterKeyword());
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
                                      .createComponentPopupBuilder(createFilterPanel(project), null)
                                      .setModalContext(false)
                                      .setFocusable(true)
                                      .setRequestFocus(true)
                                      .setCancelOnClickOutside(true)
                                      .setMinSize(new Dimension(260, 60))
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

    private JComponent createFilterPanel(Project project) {
        CommonSetting setting = ConfigHelper.getCommonSetting(project);
        JPanel panel = new JPanel(new BorderLayout(8, 4));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(new JBLabel(RestBundle.message("toolkit.toolwindow.toolbar.keywordfilter.label")), BorderLayout.WEST);

        JBTextField textField = new JBTextField(StringUtils.defaultString(setting.getFilterKeyword()));
        textField.getEmptyText().setText(RestBundle.message("toolkit.toolwindow.toolbar.keywordfilter.placeholder"));
        textField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                setting.setFilterKeyword(StringUtils.trimToEmpty(textField.getText()));
                ToolWindowHelper.scheduleUpdateTree(project);
            }
        });
        panel.add(textField, BorderLayout.CENTER);
        return panel;
    }
}
