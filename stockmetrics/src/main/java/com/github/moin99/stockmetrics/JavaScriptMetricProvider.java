/*
 * Copyright 20206 Sixth and Red River Software, Bas Leijdekkers
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
import com.github.moin99.stockmetrics.moduleMetrics.LinesOfJavaScriptModuleMetric;
import com.github.moin99.stockmetrics.moduleMetrics.NumJavaScriptFilesModuleMetric;
import com.github.moin99.stockmetrics.projectMetrics.LinesOfJavaScriptProjectMetric;
import com.github.moin99.stockmetrics.projectMetrics.NumJavaScriptFilesProjectMetric;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bas Leijdekkers
 */
public class JavaScriptMetricProvider implements MetricProvider {

    @NotNull
    @Override
    public List<Metric> getMetrics() {
        final List<Metric> metricsClasses = new ArrayList<>(4);
        metricsClasses.add(new NumJavaScriptFilesProjectMetric());
        metricsClasses.add(new NumJavaScriptFilesModuleMetric());
        metricsClasses.add(new LinesOfJavaScriptProjectMetric());
        metricsClasses.add(new LinesOfJavaScriptModuleMetric());
        return metricsClasses;
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
        profile.addMetric("LinesOfJavaScriptProject");
        profile.addMetric("LinesOfJavaScriptModule");
        return profile;
    }

    private static  PrebuiltMetricProfile createFileCountProfile() {
        final PrebuiltMetricProfile profile =
                new PrebuiltMetricProfile(StockMetricsBundle.message("file.count.metrics.profile.name"));
        profile.addMetric("NumJavaScriptFilesProject");
        profile.addMetric("NumJavaScriptFilesModule");
        return profile;
    }
}
