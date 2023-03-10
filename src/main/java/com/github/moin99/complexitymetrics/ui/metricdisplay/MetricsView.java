/*
 * Copyright 2005-2021 Sixth and Red River Software, Bas Leijdekkers
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.moin99.complexitymetrics.ui.metricdisplay;

import com.github.moin99.complexitymetrics.config.MetricsReloadedConfig;
import com.github.moin99.complexitymetrics.metricModel.MetricsRun;
import com.github.moin99.complexitymetrics.metrics.MetricCategory;
import com.github.moin99.complexitymetrics.profile.MetricsProfile;
import com.github.moin99.complexitymetrics.utils.MetricsReloadedBundle;
import com.intellij.analysis.AnalysisScope;
import com.intellij.ide.impl.ContentManagerWatcher;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.RegisterToolWindowTask;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.SideBorder;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class MetricsView {

    @NonNls
    private static final String ICON_PATH = "/images/metrics.svg";
    @NonNls
    public static final String TOOL_WINDOW_ID = "Metrics";

    private final Project project;
    private final MetricsDisplay metricsDisplay;
    private final Content myContent;
    private MetricsRun currentResults = null;
    private AnalysisScope currentScope = null;
    private MetricsProfile currentProfile = null;
    private MetricDisplaySpecification currentDisplaySpecification = null;

    public MetricsView(@NotNull Project project) {
        this.project = project;

        final DefaultActionGroup toolbarGroup = new DefaultActionGroup();
        toolbarGroup.add(new UpdateWithDiffAction(this, project));
        toolbarGroup.add(new ToggleAutoscrollAction());
        toolbarGroup.add(new ExportAction(this, project));
        toolbarGroup.add(new CreateSnapshotAction(this, project));
        toolbarGroup.add(new DiffSnapshotAction(this, project));
        toolbarGroup.add(new RemoveDiffAction(this));
        toolbarGroup.add(new EditThresholdsAction(this));
        final ActionManager actionManager = ActionManager.getInstance();
        final ActionToolbar toolbar = actionManager.createActionToolbar(TOOL_WINDOW_ID, toolbarGroup, false);
        final JPanel contentPanel = new JPanel(new BorderLayout());
        metricsDisplay = new MetricsDisplay(project);
        final JComponent component = toolbar.getComponent();
        component.setBorder(IdeBorderFactory.createBorder(SideBorder.RIGHT));
        contentPanel.add(component, BorderLayout.WEST);
        contentPanel.add(metricsDisplay.getTabbedPane(), BorderLayout.CENTER);

        final ToolWindow toolWindow = getToolWindow();
        final ContentManager contentManager = toolWindow.getContentManager();
        myContent = contentManager.getFactory().createContent(contentPanel, "", true);
    }

    private ToolWindow getToolWindow() {
        final ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowManager.getToolWindow(TOOL_WINDOW_ID);
        if (toolWindow == null) {
            final Icon icon = IconLoader.getIcon(ICON_PATH);
            toolWindow = toolWindowManager.registerToolWindow(RegisterToolWindowTask.closable(TOOL_WINDOW_ID, icon));
            toolWindow.setTitle(MetricsReloadedBundle.message("metrics.reloaded.toolwindow.title"));
            toolWindow.setToHideOnEmptyContent(true);
            ContentManagerWatcher.watchContentManager(toolWindow, toolWindow.getContentManager());
        }
        return toolWindow;
    }

    public void show(@NotNull MetricsRun results, @NotNull MetricsProfile profile, @NotNull AnalysisScope scope,
                     boolean showOnlyWarnings) {
        currentScope = scope;
        currentResults = showOnlyWarnings ? results.filterRowsWithoutWarnings(profile) : results;
        currentProfile = profile;
        currentDisplaySpecification = MetricsReloadedConfig.getInstance().getDisplaySpecification(currentProfile);
        metricsDisplay.setMetricsResults(currentDisplaySpecification, currentResults);

        final ToolWindow toolWindow = getToolWindow();
        final ContentManager contentManager = toolWindow.getContentManager();
        contentManager.addContent(myContent);
        myContent.setDisplayName(MetricsReloadedBundle.message("run.description.format",
                currentResults.getProfileName(),
                currentScope.getDisplayName(), currentResults.getTimestamp()));
        toolWindow.activate(() -> contentManager.setSelectedContent(myContent, true));
    }

    public void update(@NotNull MetricsRun results) {
        currentResults = results;
        metricsDisplay.updateMetricsResults(results, currentDisplaySpecification);
    }

    public void updateWithDiff(@NotNull MetricsRun results) {
        final MetricsRun prevResults = currentResults;
        currentResults = results;
        metricsDisplay.updateMetricsResultsWithDiff(results, currentDisplaySpecification);
        myContent.setDisplayName(MetricsReloadedBundle.message("run.comparison.message",
                currentResults.getProfileName(), currentScope.getDisplayName(),
                prevResults.getTimestamp(), currentResults.getTimestamp()));
    }

    public void reloadAsDiff(@NotNull MetricsRun prevResults) {
        metricsDisplay.overlayWithDiff(prevResults, currentDisplaySpecification);
        myContent.setDisplayName(MetricsReloadedBundle.message("run.comparison.message",
                currentResults.getProfileName(), currentScope.getDisplayName(),
                prevResults.getTimestamp(), currentResults.getTimestamp()));
    }

    public void removeDiffOverlay() {
        metricsDisplay.removeDiffOverlay(currentDisplaySpecification);
        myContent.setDisplayName(MetricsReloadedBundle.message("run.description.format",
                currentResults.getProfileName(), currentScope.getDisplayName(),
                currentResults.getTimestamp()));
    }

    public boolean hasDiffOverlay() {
        return metricsDisplay != null && metricsDisplay.hasDiffOverlay();
    }

    public MetricsRun getCurrentRun() {
        return currentResults;
    }

    public AnalysisScope getCurrentScope() {
        return currentScope;
    }

    public MetricsProfile getCurrentProfile() {
        return currentProfile;
    }

    public MetricCategory getSelectedCategory() {
        return metricsDisplay.getSelectedCategory();
    }
}
