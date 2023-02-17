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
import com.github.moin99.complexitymetrics.utils.JavaTestUtils;
import com.intellij.psi.PsiClass;

public class NumTestCasesRecursivePackageCalculator extends ClassCountingRecursivePackageCalculator {

    public NumTestCasesRecursivePackageCalculator(Metric metric) {
        super(metric);
    }

    @Override
    public boolean satisfies(PsiClass aClass) {
        return JavaTestUtils.isJUnitTestCase(aClass);
    }
}