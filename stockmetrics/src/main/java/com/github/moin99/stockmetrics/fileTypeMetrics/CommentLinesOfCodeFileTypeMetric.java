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
import com.github.moin99.stockmetrics.utils.LineUtil;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public class CommentLinesOfCodeFileTypeMetric extends FileTypeMetric {

    @NotNull
    @Override
    public String getDisplayName() {
        return StockMetricsBundle.message("comment.lines.of.code.display.name");
    }

    @NotNull
    @Override
    public String getAbbreviation() {
        return StockMetricsBundle.message("comment.lines.of.code.abbreviation");
    }

    @NotNull
    @Override
    public MetricType getType() {
        return MetricType.Count;
    }

    @NotNull
    @Override
    public MetricCalculator createCalculator() {
        return new CommentLinesOfCodeFileTypeCalculator(this);
    }

    private static class CommentLinesOfCodeFileTypeCalculator extends ElementCountFileTypeCalculator {

        public CommentLinesOfCodeFileTypeCalculator(Metric metric) {
            super(metric);
        }

        @Override
        protected PsiElementVisitor createVisitor() {
            return new PsiRecursiveElementVisitor() {

                @Override
                public void visitFile(PsiFile file) {
                    super.visitFile(file);
                    createCount(file);
                }

                @Override
                public void visitElement(PsiElement element) {
                    super.visitElement(element);
                    if (element instanceof PsiComment) {
                        final int lineCount = LineUtil.countLines(element);
                        incrementCount(element, lineCount);
                    }
                }
            };
        }
    }
}
