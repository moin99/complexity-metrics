/*
 * Copyright 2005-2020 Sixth and Red River Software, Bas Leijdekkers
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

import com.github.moin99.complexitymetrics.metrics.Metric;
import com.github.moin99.complexitymetrics.ui.dialogs.ExplanationDialog;
import com.github.moin99.complexitymetrics.utils.MetricsReloadedBundle;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.event.ActionEvent;

class ShowExplanationAction extends AbstractAction {

    private final Project project;
    private final JTable table;
    private final MetricTableModel model;

    ShowExplanationAction(Project project, JTable table) {
        super(MetricsReloadedBundle.message("show.explanation.action"));
        this.project = project;
        this.table = table;
        model = (MetricTableModel) table.getModel();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final ExplanationDialog dialog = new ExplanationDialog(project);
        final int selectedColumn = table.getSelectedColumn();
        final int modelColumn = table.convertColumnIndexToModel(selectedColumn);
        final Metric metric = model.getMetricForColumn(modelColumn).getMetric();
        dialog.show(metric);
    }
}
