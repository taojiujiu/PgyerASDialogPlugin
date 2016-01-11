package com.pgyer.dialog.dialoges;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.pgyer.dialog.ui.IconPanel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by Tao9jiu on 16/1/11.
 */
public class TestDialog extends DialogWrapper {
    public IconPanel iconPanel;

    public TestDialog(@Nullable Project project) {
        super(project);
        iconPanel = new IconPanel();
        iconPanel.setIcon("/images/icon.png");


    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return iconPanel;
    }
}
