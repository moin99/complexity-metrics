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
import com.github.moin99.complexitymetrics.utils.MethodUtils;
import com.github.moin99.stockmetrics.utils.CyclomaticComplexity;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;

import java.util.Set;

public class AverageCyclomaticComplexityPackageCalculator extends PackageCalculator {

    private final BucketedCount<PsiPackage> totalComplexityPerPackage = new BucketedCount<>();
    private final BucketedCount<PsiPackage> numMethodsPerPackage = new BucketedCount<>();

    public AverageCyclomaticComplexityPackageCalculator(Metric metric) {
        super(metric);
    }

    @Override
    public void endMetricsRun() {
        final Set<PsiPackage> packages = numMethodsPerPackage.getBuckets();
        for (PsiPackage aPackage : packages) {
            final int numClasses = numMethodsPerPackage.getBucketValue(aPackage);
            final int numAbstractClasses = totalComplexityPerPackage.getBucketValue(aPackage);

            postMetric(aPackage, numAbstractClasses, numClasses);
        }
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitMethod(PsiMethod method) {
            // don't recurse into methods of anonymous or nested classes
            if (MethodUtils.isAbstract(method)) {
                return;
            }
            final PsiPackage aPackage = ClassUtils.findPackage(method);
            if (aPackage == null) {
                return;
            }
            final int complexity = CyclomaticComplexity.calculate(method);
            totalComplexityPerPackage.incrementBucketValue(aPackage, complexity);
            numMethodsPerPackage.incrementBucketValue(aPackage);
        }
    }
}
