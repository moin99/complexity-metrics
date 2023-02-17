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
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public class NonCommentLinesOfCodeFileTypeMetric extends FileTypeMetric {

    @NotNull
    @Override
    public String getDisplayName() {
        return StockMetricsBundle.message("source.lines.of.code.display.name");
    }

    @NotNull
    @Override
    public String getAbbreviation() {
        return StockMetricsBundle.message("source.lines.of.code.abbreviation");
    }

    @NotNull
    @Override
    public MetricType getType() {
        return MetricType.Count;
    }

    @NotNull
    @Override
    public MetricCalculator createCalculator() {
        return new NonCommentLinesOfCodeFileTypeCalculator(this);
    }

    private static class NonCommentLinesOfCodeFileTypeCalculator extends ElementCountFileTypeCalculator {

        public NonCommentLinesOfCodeFileTypeCalculator(Metric metric) {
            super(metric);
        }

        @Override
        protected PsiElementVisitor createVisitor() {
            return new PsiRecursiveElementWalkingVisitor() {

                @Override
                public void visitFile(PsiFile file) {
                    if (file.getFileType() == PlainTextFileType.INSTANCE) {
                        return;
                    }
                    super.visitFile(file);
                    final int lineCount = LineUtil.countLines(file);
                    incrementCount(file, lineCount);
                }

                @Override
                public void visitElement(PsiElement element) {
                    super.visitElement(element);
                    if (element instanceof PsiComment) {
                        final PsiComment comment = (PsiComment) element;
                        final int lineCount = LineUtil.countCommentOnlyLines(comment);
                        incrementCount(comment, -lineCount);
                    }
                }
            };
        }
    }
}
