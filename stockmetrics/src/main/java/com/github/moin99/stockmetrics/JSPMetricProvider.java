/*
 * Copyright 2016-2020 Sixth and Red River Software, Bas Leijdekkers
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
package com.github.moin99.stockmetrics;

import com.github.moin99.complexitymetrics.metrics.Metric;
import com.github.moin99.complexitymetrics.metrics.MetricProvider;
import com.github.moin99.complexitymetrics.metrics.PrebuiltMetricProfile;
import com.github.moin99.stockmetrics.i18n.StockMetricsBundle;
import com.github.moin99.stockmetrics.moduleMetrics.LinesOfJSPModuleMetric;
import com.github.moin99.stockmetrics.moduleMetrics.NumJSPFilesModuleMetric;
import com.github.moin99.stockmetrics.projectMetrics.LinesOfJSPProjectMetric;
import com.github.moin99.stockmetrics.projectMetrics.NumJSPFilesProjectMetric;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bas Leijdekkers
 */
public class JSPMetricProvider implements MetricProvider {

    @NotNull
    @Override
    public List<Metric> getMetrics() {
        final List<Metric> metrics = new ArrayList<>(4);
        metrics.add(new NumJSPFilesModuleMetric());
        metrics.add(new LinesOfJSPModuleMetric());
        metrics.add(new NumJSPFilesProjectMetric());
        metrics.add(new LinesOfJSPProjectMetric());
        return metrics;
    }

    @NotNull
    @Override
    public List<PrebuiltMetricProfile> getPrebuiltProfiles() {
        final List<PrebuiltMetricProfile> out = new ArrayList<>(2);
        out.add(createCodeSizeProfile());
        out.add(createFileCountProfile());
        return out;
    }

    private static PrebuiltMetricProfile createCodeSizeProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("lines.of.code.metrics.profile.name"));
        profile.addMetric("LinesOfJSPProject");
        profile.addMetric("LinesOfJSPModule");
        return profile;
    }

    private static  PrebuiltMetricProfile createFileCountProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("file.count.metrics.profile.name"));
        profile.addMetric("NumJSPFilesProject");
        profile.addMetric("NumJSPFilesModule");
        return profile;
    }
}
