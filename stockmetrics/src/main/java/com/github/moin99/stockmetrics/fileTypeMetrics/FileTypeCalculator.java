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
package com.github.moin99.stockmetrics.fileTypeMetrics;

import com.github.moin99.complexitymetrics.metrics.Metric;
import com.github.moin99.stockmetrics.execution.BaseMetricsCalculator;
import com.intellij.openapi.fileTypes.FileType;

/**
 * @author Bas Leijdekkers
 */
public abstract class FileTypeCalculator extends BaseMetricsCalculator {

    public FileTypeCalculator(Metric metric) {
        super(metric);
    }

    protected void postMetric(FileType fileType, int numerator, int denominator) {
        resultsHolder.postFileTypeMetric(metric, fileType, (double) numerator, (double) denominator);
    }

    protected void postMetric(FileType fileType, int value) {
        resultsHolder.postFileTypeMetric(metric, fileType, (double) value);
    }

    protected void postMetric(FileType fileType, double value) {
        resultsHolder.postFileTypeMetric(metric, fileType, value);
    }
}
