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
import com.github.moin99.complexitymetrics.utils.TestUtils;
import com.github.moin99.stockmetrics.utils.LineUtil;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;

public class SourceLinesOfCodeTestRecursivePackageCalculator extends ElementCountPackageCalculator {

    public SourceLinesOfCodeTestRecursivePackageCalculator(Metric metric) {
        super(metric);
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends PsiRecursiveElementVisitor {

        @Override
        public void visitFile(PsiFile file) {
            if (file.getFileType() == PlainTextFileType.INSTANCE) {
                return;
            }
            super.visitFile(file);
            if (TestUtils.isTest(file)) {
                final int lineCount = LineUtil.countLines(file);
                incrementCountRecursive(file, lineCount);
            }
        }

        @Override
        public void visitComment(PsiComment comment) {
            super.visitComment(comment);
            if (TestUtils.isTest(comment)) {
                final int lineCount = LineUtil.countCommentOnlyLines(comment);
                incrementCountRecursive(comment, -lineCount);
            }
        }
    }
}
