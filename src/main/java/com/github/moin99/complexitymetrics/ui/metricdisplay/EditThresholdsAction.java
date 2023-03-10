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

import com.github.moin99.complexitymetrics.metricModel.MetricsResult;
import com.github.moin99.complexitymetrics.metrics.Metric;
import com.github.moin99.complexitymetrics.metrics.MetricCategory;
import com.github.moin99.complexitymetrics.profile.MetricInstance;
import com.github.moin99.complexitymetrics.profile.MetricInstanceImpl;
import com.github.moin99.complexitymetrics.profile.MetricsProfile;
import com.github.moin99.complexitymetrics.profile.MetricsProfileRepository;
import com.github.moin99.complexitymetrics.ui.dialogs.ThresholdDialog;
import com.github.moin99.complexitymetrics.utils.MetricsReloadedBundle;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

class EditThresholdsAction extends DumbAwareAction {

    private final MetricsView toolWindow;

    EditThresholdsAction(MetricsView toolWindow) {
        super(MetricsReloadedBundle.messagePointer("edit.thresholds.action"),
              MetricsReloadedBundle.messagePointer("edit.threshold.values.for.this.metric.profile"),
              AllIcons.Actions.Properties);
        this.toolWindow = toolWindow;
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        final DataContext dataContext = event.getDataContext();
        final Project project = CommonDataKeys.PROJECT.getData(dataContext);
        assert project != null;
        final MetricCategory category = toolWindow.getSelectedCategory();
        final MetricsProfile profile = toolWindow.getCurrentProfile();
        final List<MetricInstance> metrics = new ArrayList<>();
        for (MetricInstance instance : profile.getMetricInstances()) {
            if (!instance.isEnabled()) {
                continue;
            }
            final Metric metric = instance.getMetric();
            if (metric.getCategory() != category) {
                continue;
            }
            metrics.add(new MetricInstanceImpl(instance));
        }
        final MetricsResult results = toolWindow.getCurrentRun().getResultsForCategory(category);
        final ThresholdDialog dialog =
                new ThresholdDialog(project, profile.getName(), metrics, results);
        dialog.show();
        if (dialog.isOK()) {
            profile.copyFrom(metrics);
            MetricsProfileRepository.persistProfile(profile);
        }
    }
}
