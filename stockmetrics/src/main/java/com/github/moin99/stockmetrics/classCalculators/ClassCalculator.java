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
import com.github.moin99.stockmetrics.execution.BaseMetricsCalculator;
import com.intellij.psi.PsiClass;

public abstract class ClassCalculator extends BaseMetricsCalculator {

    public ClassCalculator(Metric metric) {
        super(metric);
    }

    protected void postMetric(PsiClass aClass, int numerator, int denominator) {
        resultsHolder.postClassMetric(metric, aClass, (double) numerator, (double) denominator);
    }

    protected void postMetric(PsiClass aClass, int value) {
        resultsHolder.postClassMetric(metric, aClass, (double) value);
    }

    protected void postMetric(PsiClass aClass, double value) {
        resultsHolder.postClassMetric(metric, aClass, value);
    }

    protected static boolean isConcreteClass(PsiClass aClass) {
        return !(aClass.isInterface() || ClassUtils.isAnonymous(aClass));
    }
}
