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
import com.github.moin99.complexitymetrics.utils.ClassUtils;
import com.github.moin99.complexitymetrics.utils.MethodUtils;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;

public class NumOperationsOverriddenCalculator extends ClassCalculator {

    public NumOperationsOverriddenCalculator(Metric metric) {
        super(metric);
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitClass(final PsiClass aClass) {
            super.visitClass(aClass);
            if (ClassUtils.isAnonymous(aClass) || aClass.isInterface()) {
                return;
            }
            final PsiMethod[] methods = aClass.getMethods();
            int numOverriddenMethods = 0;
            for (final PsiMethod method : methods) {
                if (!MethodUtils.isConcreteMethod(method)) {
                    continue;
                }
                if (MethodUtils.hasConcreteSuperMethod(method)) {
                    numOverriddenMethods++;
                }
            }
            postMetric(aClass, numOverriddenMethods);
        }
    }
}
