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

import com.github.moin99.complexitymetrics.config.MetricsReloadedConfig;
import com.github.moin99.complexitymetrics.metrics.Metric;
import com.github.moin99.complexitymetrics.metrics.MetricType;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class MetricTableMouseListener extends MouseAdapter {
    private final Project project;
    private final JTable table;

    MetricTableMouseListener(Project project, JTable table) {
        this.project = project;
        this.table = table;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        final int x = e.getX();
        final int y = e.getY();
        final Point point = new Point(x, y);
        final int column = table.columnAtPoint(point);
        final int row = table.rowAtPoint(point);
        if (column == -1 || row == -1) {
            return;
        }
        final int modelIndex = table.convertColumnIndexToModel(column);
        final boolean autoscroll = MetricsReloadedConfig.getInstance().isAutoscroll();
        if (modelIndex == 0 && (e.getClickCount() == 2 || autoscroll)) {
            final MetricTableModel model = (MetricTableModel) table.getModel();
            final PsiElement element = model.getElementAtRow(row);
            if (element instanceof Navigatable) {
                final Navigatable navigatable = (Navigatable) element;
                navigatable.navigate(true);
            }
        }
        table.setColumnSelectionInterval(column, column);
        table.repaint();
        final int button = e.getButton();
        if (button == MouseEvent.BUTTON3) {
            showPopupMenu(e);
        }
    }

    private void showPopupMenu(MouseEvent e) {
        final int selectedColumn = table.getSelectedColumn();
        if (selectedColumn == -1 || selectedColumn == 0) {
            return;
        }
        final JPopupMenu popup = new JPopupMenu();
        final MetricTableModel model = (MetricTableModel) table.getModel();
        final Metric metric = model.getMetricForColumn(selectedColumn).getMetric();
        final MetricType metricType = metric.getType();
        if (model.hasDiff()) {
            popup.add(new JMenuItem(new ShowDiffDistributionAction(project, table)));
            popup.add(new JMenuItem(new ShowDiffHistogramAction(project, table)));
        } else {
            if (metricType != MetricType.RecursiveCount && metricType != MetricType.RecursiveRatio) {
                popup.add(new JMenuItem(new ShowDistributionAction(project, table)));
                popup.add(new JMenuItem(new ShowHistogramAction(project, table)));
            }
            if (metricType == MetricType.Count) {
                popup.add(new JMenuItem(new ShowPieChartAction(project, table)));
            }
            popup.add(new JMenuItem(new ShowExplanationAction(project, table)));
        }

        final int x = e.getX();
        final int y = e.getY();
        popup.show(table, x, y);
    }
}
