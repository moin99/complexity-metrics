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
import com.github.moin99.complexitymetrics.metrics.MetricCalculator;
import com.github.moin99.complexitymetrics.metrics.MetricType;
import com.github.moin99.stockmetrics.i18n.StockMetricsBundle;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author Bas Leijdekkers
 */
public class NumFilesFileTypeMetric extends FileTypeMetric {

    @NotNull
    @Override
    public String getDisplayName() {
        return StockMetricsBundle.message("number.of.files.display.name");
    }

    @NotNull
    @Override
    public String getAbbreviation() {
        return StockMetricsBundle.message("number.of.files.abbreviation");
    }

    @NotNull
    @Override
    public MetricType getType() {
        return MetricType.Count;
    }

    @NotNull
    @Override
    public MetricCalculator createCalculator() {
        return new NumFilesFileTypeCalculator(this);
    }

    private static class NumFilesFileTypeCalculator extends ElementCountFileTypeCalculator {

        public NumFilesFileTypeCalculator(Metric metric) {
            super(metric);
        }

        @Override
        protected PsiElementVisitor createVisitor() {
            return new Visitor();
        }

        private class Visitor extends PsiElementVisitor {

            @Override
            public void visitFile(PsiFile file) {
                super.visitFile(file);
                incrementCount(file, 1);
            }
        }
    }
}
