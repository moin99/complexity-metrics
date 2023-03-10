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

package com.github.moin99.complexitymetrics.export;

import com.github.moin99.complexitymetrics.metricModel.MetricAbbreviationComparator;
import com.github.moin99.complexitymetrics.metricModel.MetricsResult;
import com.github.moin99.complexitymetrics.metricModel.MetricsRun;
import com.github.moin99.complexitymetrics.metrics.Metric;
import com.github.moin99.complexitymetrics.metrics.MetricCategory;
import com.github.moin99.complexitymetrics.utils.FormatUtils;
import org.jetbrains.annotations.NonNls;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class HTMLExporter implements Exporter {

    private final MetricsRun manager;

    public HTMLExporter(MetricsRun manager) {
        this.manager = manager;
    }

    @Override
    public void export(String fileName) throws IOException {
        try (PrintWriter writer = new PrintWriter(fileName)) {
            export(writer);
        }
    }

    @Override
    public void export(PrintWriter writer) {
        for (MetricCategory category : MetricCategory.values()) {
            writeResultsForCategory(category, writer);
        }
    }

    private void writeResultsForCategory(MetricCategory category, PrintWriter writer) {
        final MetricsResult results = manager.getResultsForCategory(category);
        final String categoryName = category.name();
        writeResults(results, writer, categoryName);
    }

    private static void writeResults(MetricsResult results, @NonNls PrintWriter writer, String type) {
        final Metric[] metrics = results.getMetrics();
        if (metrics.length == 0) {
            return;
        }
        Arrays.sort(metrics, new MetricAbbreviationComparator());
        writer.print(type);
        for (final Metric metric : metrics) {
            final String abbreviation = metric.getAbbreviation();
            writer.print(',' + abbreviation);
        }
        writer.println();
        final String[] measuredObjects = results.getMeasuredObjects();
        Arrays.sort(measuredObjects);

        for (final String object : measuredObjects) {
            writer.print('\"' + object + '\"');
            for (final Metric metric : metrics) {
                final Double metricValue = results.getValueForMetric(metric, object);
                if (metricValue == null) {
                    writer.print(",n/a");
                } else {
                    String formattedValue = FormatUtils.formatValue(metric, metricValue);
                    if (formattedValue.indexOf((int) ',') >= 0) {
                        formattedValue = '"' + formattedValue + '"';
                    }
                    writer.print(',' + formattedValue);
                }
            }
            writer.println();
        }
        writer.println();
    }
}
