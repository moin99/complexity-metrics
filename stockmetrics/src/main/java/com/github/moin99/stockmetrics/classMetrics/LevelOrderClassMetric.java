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

package com.github.moin99.stockmetrics.classMetrics;

import com.github.moin99.complexitymetrics.metrics.MetricCalculator;
import com.github.moin99.complexitymetrics.metrics.MetricType;
import com.github.moin99.stockmetrics.classCalculators.LevelOrderClassCalculator;
import com.github.moin99.stockmetrics.i18n.StockMetricsBundle;
import org.jetbrains.annotations.NotNull;

public class LevelOrderClassMetric extends ClassMetric {

    @NotNull
    @Override
    public String getDisplayName() {
        return StockMetricsBundle.message("level.order.display.name");
    }

    @NotNull
    @Override
    public String getAbbreviation() {
        return StockMetricsBundle.message("level.order.abbreviation");
    }

    @NotNull
    @Override
    public MetricType getType() {
        return MetricType.Score;
    }

    @Override
    public boolean requiresDependents() {
        return true;
    }

    @NotNull
    @Override
    public MetricCalculator createCalculator() {
        return new LevelOrderClassCalculator(this);
    }
}
