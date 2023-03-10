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

package com.github.moin99.stockmetrics.interfaceCalculators;

import com.github.moin99.complexitymetrics.metrics.Metric;
import com.github.moin99.complexitymetrics.utils.ClassUtils;
import com.github.moin99.stockmetrics.utils.LineUtil;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElementVisitor;

public class CommentRatioInterfaceCalculator extends InterfaceCalculator {

    private int commentLines = 0;

    public CommentRatioInterfaceCalculator(Metric metric) {
        super(metric);
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {

        @Override
        public void visitClass(PsiClass aClass) {
            int prevCommentLines = 0;
            if (!ClassUtils.isAnonymous(aClass)) {
                prevCommentLines = commentLines;
                commentLines = 0;
            }
            super.visitClass(aClass);
            if (!ClassUtils.isAnonymous(aClass)) {
                if (isInterface(aClass)) {
                    int linesOfCode = LineUtil.countLines(aClass);
                    final PsiClass[] innerClasses = aClass.getInnerClasses();
                    for (PsiClass innerClass : innerClasses) {
                        linesOfCode -= LineUtil.countLines(innerClass);
                    }
                    postMetric(aClass, commentLines, linesOfCode);
                }
                commentLines = prevCommentLines;
            }
        }

        @Override
        public void visitComment(PsiComment comment) {
            super.visitComment(comment);
            commentLines += LineUtil.countLines(comment);
        }
    }
}
