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
import com.github.moin99.complexitymetrics.utils.MethodUtils;
import com.intellij.psi.*;

public class NumStatementsCalculator extends MethodCalculator {

    private int methodNestingDepth = 0;
    private int elementCount = 0;

    public NumStatementsCalculator(Metric metric) {
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
                elementCount = 0;
            }
            methodNestingDepth++;
            super.visitMethod(method);
            methodNestingDepth--;
            if (methodNestingDepth == 0 && !MethodUtils.isAbstract(method)) {
                postMetric(method, elementCount);
            }
        }

        @Override
        public void visitStatement(PsiStatement statement) {
            super.visitStatement(statement);
            if (statement instanceof PsiEmptyStatement ||
                    statement instanceof PsiBlockStatement) {
                return;
            }
            elementCount++;
        }
    }
}
