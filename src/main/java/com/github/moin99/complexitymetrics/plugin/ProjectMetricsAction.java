/*
 * Copyright 2005-2022 Sixth and Red River Software, Bas Leijdekkers
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

package com.github.moin99.complexitymetrics.plugin;

import com.github.moin99.complexitymetrics.config.MetricsReloadedConfig;
import com.github.moin99.complexitymetrics.metricModel.MetricsExecutionContextImpl;
import com.github.moin99.complexitymetrics.metricModel.MetricsRunImpl;
import com.github.moin99.complexitymetrics.metricModel.TimeStamp;
import com.github.moin99.complexitymetrics.profile.MetricsProfile;
import com.github.moin99.complexitymetrics.profile.MetricsProfileRepository;
import com.github.moin99.complexitymetrics.ui.dialogs.ProfileSelectionPanel;
import com.github.moin99.complexitymetrics.ui.metricdisplay.MetricsView;
import com.github.moin99.complexitymetrics.utils.MetricsReloadedBundle;
import com.intellij.analysis.AnalysisScope;
import com.intellij.analysis.BaseAnalysisAction;
import com.intellij.analysis.BaseAnalysisActionDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ProjectMetricsAction extends BaseAnalysisAction {

    public ProjectMetricsAction() {
        super(MetricsReloadedBundle.messagePointer("metrics.calculation"),
              MetricsReloadedBundle.messagePointer("metrics"));
    }

    @Override
    protected @NotNull @NlsContexts.DialogTitle String getDialogTitle() {
        return "My metrics";
    }

    @Override
    protected void analyze(@NotNull Project project, @NotNull AnalysisScope analysisScope) {
        final MetricsProfileRepository repository = MetricsProfileRepository.getInstance();
        final MetricsProfile profile = repository.getSelectedProfile();
        if (profile == null) {
            return;
        }
        final MetricsView toolWindow = new MetricsView(project);
        final MetricsRunImpl metricsRun = new MetricsRunImpl();
        new MetricsExecutionContextImpl(project, analysisScope) {

            @Override
            public void onFinish() {
                final boolean showOnlyWarnings = MetricsReloadedConfig.getInstance().isShowOnlyWarnings();
                if (!metricsRun.hasWarnings(profile) && showOnlyWarnings) {
                    ToolWindowManager.getInstance(project).notifyByBalloon(
                            MetricsView.TOOL_WINDOW_ID, MessageType.INFO,
                            MetricsReloadedBundle.message("no.metrics.warnings.found"));
                    return;
                }
                else if (!metricsRun.hasResults()) {
                    ToolWindowManager.getInstance(project).notifyByBalloon(
                            MetricsView.TOOL_WINDOW_ID, MessageType.WARNING,
                            "Profile '" + profile.getName() + "' has no enabled metrics");
                    return;
                }
                final String profileName = profile.getName();
                metricsRun.setProfileName(profileName);
                metricsRun.setContext(analysisScope);
                metricsRun.setTimestamp(new TimeStamp());
                toolWindow.show(metricsRun, profile, analysisScope, showOnlyWarnings);
            }
        }.execute(profile, metricsRun);
    }

    @Override
    @Nullable
    protected JComponent getAdditionalActionSettings(Project project, BaseAnalysisActionDialog dialog) {
        return new ProfileSelectionPanel(project);
    }
}
