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

package com.github.moin99.stockmetrics.moduleCalculators;

import com.github.moin99.complexitymetrics.metrics.Metric;
import com.intellij.psi.*;

public abstract class ClassCountingModuleCalculator extends ElementCountModuleCalculator {

    public ClassCountingModuleCalculator(Metric metric) {
        super(metric);
    }

    protected abstract boolean satisfies(PsiClass aClass);

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitClass(PsiClass aClass) {
            super.visitClass(aClass);
            if (aClass instanceof PsiTypeParameter || aClass instanceof PsiEnumConstantInitializer) {
                return;
            }
            createCount(aClass);
            if (satisfies(aClass)) {
                incrementCount(aClass, 1);
            }
        }
    }
}
