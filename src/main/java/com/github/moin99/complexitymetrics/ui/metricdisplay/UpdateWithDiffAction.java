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

import com.github.moin99.complexitymetrics.metricModel.MetricsExecutionContextImpl;
import com.github.moin99.complexitymetrics.metricModel.MetricsRunImpl;
import com.github.moin99.complexitymetrics.metricModel.TimeStamp;
import com.github.moin99.complexitymetrics.profile.MetricsProfile;
import com.github.moin99.complexitymetrics.utils.MetricsReloadedBundle;
import com.intellij.analysis.AnalysisScope;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

class UpdateWithDiffAction extends DumbAwareAction {

    private final MetricsView toolWindow;
    private final Project project;

    UpdateWithDiffAction(MetricsView toolWindow, Project project) {
        super(MetricsReloadedBundle.messagePointer("update.with.differences.action"),
              MetricsReloadedBundle.messagePointer("update.with.differences.description"), AllIcons.Actions.Rerun);
        this.toolWindow = toolWindow;
        this.project = project;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        final AnalysisScope scope = toolWindow.getCurrentScope();
        final MetricsProfile currentProfile = toolWindow.getCurrentProfile();
        final MetricsRunImpl metricsRun = new MetricsRunImpl();
        new MetricsExecutionContextImpl(project, scope) {

            @Override
            public void onFinish() {
                metricsRun.setContext(scope);
                metricsRun.setProfileName(currentProfile.getName());
                metricsRun.setTimestamp(new TimeStamp());
                toolWindow.updateWithDiff(metricsRun);
            }
        }.execute(currentProfile, metricsRun);
    }
}
