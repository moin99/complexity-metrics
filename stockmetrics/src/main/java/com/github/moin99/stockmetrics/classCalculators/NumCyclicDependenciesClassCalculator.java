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

package com.github.moin99.stockmetrics.classCalculators;

import com.github.moin99.complexitymetrics.metrics.Metric;
import com.github.moin99.stockmetrics.dependency.DependencyMap;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;

import java.util.Set;

public class NumCyclicDependenciesClassCalculator extends ClassCalculator {

    public NumCyclicDependenciesClassCalculator(Metric metric) {
        super(metric);
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitClass(PsiClass aClass) {
            super.visitClass(aClass);
            if (isConcreteClass(aClass)) {
                final DependencyMap dependencyMap = getDependencyMap();
                final Set<PsiClass> dependencies = dependencyMap.calculateStronglyConnectedComponents(aClass);
                final int numDependencies = dependencies.size() - 1;
                postMetric(aClass, numDependencies);
            }
        }
    }
}
