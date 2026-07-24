package io.github.newhoo.restkit.toolwindow.toolbar;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import io.github.newhoo.restkit.config.ConfigHelper;
import io.github.newhoo.restkit.i18n.RestBundle;
import io.github.newhoo.restkit.intellij.BaseToggleAction;
import org.jetbrains.annotations.NotNull;

/**
 * Toggle double-click behavior: jump to source vs generate request.
 */
public class DoubleClickModeAction extends BaseToggleAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        boolean jumpToSource = isSelected(e);
        e.getPresentation().setText(() -> jumpToSource
                ? RestBundle.message("toolkit.toolwindow.toolbar.doubleclick.mode.jump")
                : RestBundle.message("toolkit.toolwindow.toolbar.doubleclick.mode.generate"));
        e.getPresentation().setDescription(() -> RestBundle.message("toolkit.toolwindow.toolbar.doubleclick.mode.desc"));
        e.getPresentation().setIcon(jumpToSource ? AllIcons.Actions.EditSource : AllIcons.Actions.Execute);
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        return ConfigHelper.getGlobalSetting().isJumpToSourceOnDoubleClick();
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        ConfigHelper.getGlobalSetting().setJumpToSourceOnDoubleClick(state);
    }
}
