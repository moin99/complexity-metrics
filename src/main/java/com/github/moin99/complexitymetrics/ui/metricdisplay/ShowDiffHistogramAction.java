/*
 * Copyright 2005-2016 Sixth and Red River Software, Bas Leijdekkers
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
import com.github.moin99.complexitymetrics.metrics.MetricCategory;
import com.github.moin99.complexitymetrics.metrics.MetricType;
import com.github.moin99.complexitymetrics.ui.charts.DiffHistogramDialog;
import com.github.moin99.complexitymetrics.utils.MetricsCategoryNameUtil;
import com.github.moin99.complexitymetrics.utils.MetricsReloadedBundle;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;

import javax.swing.*;
import java.awt.event.ActionEvent;

class ShowDiffHistogramAction extends AbstractAction {
    private final Project project;
    private final JTable table;
    private final MetricTableModel model;

    ShowDiffHistogramAction(Project project, JTable table) {
        super(MetricsReloadedBundle.message("show.histogram.action"));
        this.project = project;
        this.table = table;
        model = (MetricTableModel) table.getModel();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int numRows = model.getRowCount();
        if (numRows > 1) {
            numRows -= 2;
        }
        final Double[] values = new Double[numRows];
        final Double[] prevValues = new Double[numRows];
        final int selectedColumn = table.getSelectedColumn();
        final int modelColumn = table.convertColumnIndexToModel(selectedColumn);
        for (int i = 0; i < numRows; i++) {
            final Pair<Double, Double> value = (Pair<Double, Double>) model.getValueAt(i, modelColumn);
            assert value != null;
            values[i] = value.getFirst();
            prevValues[i] = value.getSecond();
        }
        final Metric metric = model.getMetricForColumn(modelColumn).getMetric();
        final String name = metric.getDisplayName();
        final MetricCategory category = metric.getCategory();
        final String categoryName = MetricsCategoryNameUtil.getShortNameForCategory(category);
        final MetricType type = metric.getType();
        final DiffHistogramDialog histogramDialog = new DiffHistogramDialog(project, categoryName, name, type, values,
                prevValues);
        histogramDialog.show();
    }
}
