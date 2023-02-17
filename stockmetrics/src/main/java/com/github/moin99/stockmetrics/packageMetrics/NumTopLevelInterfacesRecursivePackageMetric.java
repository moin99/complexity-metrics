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

package com.github.moin99.stockmetrics.packageMetrics;

import com.github.moin99.complexitymetrics.metrics.MetricCalculator;
import com.github.moin99.complexitymetrics.metrics.MetricType;
import com.github.moin99.stockmetrics.i18n.StockMetricsBundle;
import com.github.moin99.stockmetrics.packageCalculators.NumTopLevelInterfacesRecursivePackageCalculator;
import org.jetbrains.annotations.NotNull;

public class NumTopLevelInterfacesRecursivePackageMetric extends PackageMetric {

    @NotNull
    @Override
    public String getDisplayName() {
        return StockMetricsBundle.message("number.of.top.level.interfaces.recursive.display.name");
    }

    @NotNull
    @Override
    public String getAbbreviation() {
        return StockMetricsBundle.message("number.of.top.level.interfaces.recursive.abbreviation");
    }

    @NotNull
    @Override
    public MetricType getType() {
        return MetricType.RecursiveCount;
    }

    @NotNull
    @Override
    public MetricCalculator createCalculator() {
        return new NumTopLevelInterfacesRecursivePackageCalculator(this);
    }
}
