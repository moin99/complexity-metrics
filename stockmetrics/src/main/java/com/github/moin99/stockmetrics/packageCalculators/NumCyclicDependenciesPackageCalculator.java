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
import com.github.moin99.complexitymetrics.utils.ClassUtils;
import com.github.moin99.stockmetrics.dependency.DependencyMap;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiRecursiveElementVisitor;

import java.util.HashSet;
import java.util.Set;

public class NumCyclicDependenciesPackageCalculator extends PackageCalculator {

    private final Set<PsiPackage> packages = new HashSet<>();

    public NumCyclicDependenciesPackageCalculator(Metric metric) {
        super(metric);
    }

    @Override
    public void endMetricsRun() {
        for (PsiPackage aPackage : packages) {
            final DependencyMap dependencyMap = getDependencyMap();
            final Set<PsiPackage> component = dependencyMap.calculateStronglyConnectedPackageComponents(aPackage);
            final int cyclicDependencies = component.size() - 1;
            postMetric(aPackage, (double) cyclicDependencies);
        }
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends PsiRecursiveElementVisitor {

        @Override
        public void visitFile(PsiFile file) {
            final PsiPackage aPackage = ClassUtils.findPackage(file);
            if (aPackage != null) {
                packages.add(aPackage);
            }
        }
    }
}
