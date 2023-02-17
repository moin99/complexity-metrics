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

package com.github.moin99.stockmetrics.moduleMetrics;

import com.github.moin99.complexitymetrics.metrics.MetricCalculator;
import com.github.moin99.complexitymetrics.metrics.MetricType;
import com.github.moin99.stockmetrics.i18n.StockMetricsBundle;
import com.github.moin99.stockmetrics.moduleCalculators.FileCountModuleCalculator;
import com.intellij.ide.highlighter.HtmlFileType;
import org.jetbrains.annotations.NotNull;

public class NumHTMLFilesModuleMetric extends ModuleMetric {

    @NotNull
    @Override
    public String getDisplayName() {
        return StockMetricsBundle.message("number.of.html.files.display.name");
    }

    @NotNull
    @Override
    public String getAbbreviation() {
        return StockMetricsBundle.message("number.of.html.files.abbreviation");
    }

    @NotNull
    @Override
    public MetricType getType() {
        return MetricType.Count;
    }

    @NotNull
    @Override
    public MetricCalculator createCalculator() {
        return new FileCountModuleCalculator(this, HtmlFileType.INSTANCE);
    }
}
