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

package com.github.moin99.stockmetrics.packageCalculators;

import com.github.moin99.complexitymetrics.metrics.Metric;
import com.github.moin99.complexitymetrics.utils.BucketedCount;
import com.github.moin99.complexitymetrics.utils.ClassUtils;
import com.github.moin99.stockmetrics.utils.LineUtil;
import com.intellij.psi.*;

import java.util.Set;

public class TrueCommentRatioRecursivePackageCalculator extends PackageCalculator {

    private final BucketedCount<PsiPackage> numLinesPerPackage = new BucketedCount<>();
    private final BucketedCount<PsiPackage> numCommentLinesPerPackage = new BucketedCount<>();

    public TrueCommentRatioRecursivePackageCalculator(Metric metric) {
        super(metric);
    }

    @Override
    public void endMetricsRun() {
        final Set<PsiPackage> packages = numLinesPerPackage.getBuckets();
        for (PsiPackage aPackage : packages) {
            final int numLines = numLinesPerPackage.getBucketValue(aPackage);
            final int numCommentLines = numCommentLinesPerPackage.getBucketValue(aPackage);
            postMetric(aPackage, numCommentLines, numLines - numCommentLines);
        }
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends PsiRecursiveElementVisitor {

        @Override
        public void visitFile(PsiFile file) {
            super.visitFile(file);
            final int lineCount = LineUtil.countLines(file);
            final PsiPackage[] packages = ClassUtils.calculatePackagesRecursive(file);
            for (PsiPackage aPackage : packages) {
                numLinesPerPackage.incrementBucketValue(aPackage, lineCount);
            }
        }

        @Override
        public void visitComment(PsiComment comment) {
            super.visitComment(comment);
            final int lineCount = LineUtil.countCommentOnlyLines(comment);
            final PsiPackage[] packages = ClassUtils.calculatePackagesRecursive(comment);
            for (PsiPackage aPackage : packages) {
                numCommentLinesPerPackage.incrementBucketValue(aPackage, lineCount);
            }
        }
    }
}
