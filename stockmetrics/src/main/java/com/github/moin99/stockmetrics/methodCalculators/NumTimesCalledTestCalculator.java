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

package com.github.moin99.stockmetrics.methodCalculators;

import com.github.moin99.complexitymetrics.metrics.Metric;
import com.github.moin99.stockmetrics.utils.MethodCallMap;
import com.github.moin99.stockmetrics.utils.MethodCallMapImpl;
import com.intellij.openapi.util.Key;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;

import java.util.Set;

public class NumTimesCalledTestCalculator extends MethodCalculator {

    private int methodNestingDepth = 0;

    public NumTimesCalledTestCalculator(Metric metric) {
        super(metric);
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitMethod(PsiMethod method) {
            if (methodNestingDepth == 0) {
                final Key<MethodCallMap> key = new Key<>("MethodCallMap");

                MethodCallMap methodCallMap = executionContext.getUserData(key);
                if (methodCallMap == null) {
                    methodCallMap = new MethodCallMapImpl();
                    executionContext.putUserData(key, methodCallMap);
                }
                final Set<PsiReference> methodCalls = methodCallMap.calculateTestMethodCallPoints(method);
                final int calls = methodCalls.size();
                postMetric(method, calls);
            }
            methodNestingDepth++;
            super.visitMethod(method);
            methodNestingDepth--;
        }
    }
}
