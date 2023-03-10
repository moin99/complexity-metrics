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

package com.github.moin99.stockmetrics.utils;

import com.intellij.psi.*;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * @author Bas Leijdekkers
 */
public final class CyclomaticComplexity {

    private CyclomaticComplexity() {}

    public static int calculate(@Nullable PsiElement element) {
        return calculate(element, e -> true);
    }

    public static int calculate(@Nullable PsiElement element, Predicate<PsiElement> filter) {
        if (element == null) {
            return 1;
        }
        final CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor(filter);
        element.accept(visitor);
        return visitor.getCyclomaticComplexity();
    }

    private static class CyclomaticComplexityVisitor extends JavaRecursiveElementWalkingVisitor {

        private final Predicate<PsiElement> filter;
        private int complexity = 1;

        public CyclomaticComplexityVisitor(Predicate<PsiElement> filter) {
            this.filter = filter;
        }

        @Override
        public void visitForStatement(PsiForStatement statement) {
            super.visitForStatement(statement);
            if (filter.test(statement)) {
                complexity++;
            }
        }

        @Override
        public void visitForeachStatement(PsiForeachStatement statement) {
            super.visitForeachStatement(statement);
            if (filter.test(statement)) {
                complexity++;
            }
        }

        @Override
        public void visitIfStatement(PsiIfStatement statement) {
            super.visitIfStatement(statement);
            if (filter.test(statement)) {
                complexity++;
            }
        }

        @Override
        public void visitDoWhileStatement(PsiDoWhileStatement statement) {
            super.visitDoWhileStatement(statement);
            if (filter.test(statement)) {
                complexity++;
            }
        }

        @Override
        public void visitConditionalExpression(PsiConditionalExpression expression) {
            super.visitConditionalExpression(expression);
            if (filter.test(expression)) {
                complexity++;
            }
        }

        @Override
        public void visitSwitchExpression(PsiSwitchExpression expression) {
            super.visitSwitchExpression(expression);
            visitSwitchBlock(expression);
        }

        @Override
        public void visitSwitchStatement(PsiSwitchStatement statement) {
            super.visitSwitchStatement(statement);
            visitSwitchBlock(statement);
        }

        private void visitSwitchBlock(PsiSwitchBlock statement) {
            final PsiCodeBlock body = statement.getBody();
            if (body == null) {
                return;
            }
            final PsiStatement[] statements = body.getStatements();
            boolean pendingBranch = false;
            boolean nonDefault = true;
            boolean accepted = true;
            for (PsiStatement child : statements) {
                if (child instanceof PsiSwitchLabelStatement) {
                    if (pendingBranch && accepted) {
                        complexity++;
                    }
                    nonDefault = !((PsiSwitchLabelStatement) child).isDefaultCase();
                    accepted = true;
                    pendingBranch = false;
                } else if (child instanceof PsiSwitchLabeledRuleStatement) {
                    if (!((PsiSwitchLabeledRuleStatement) child).isDefaultCase() &&
                            filter.test(((PsiSwitchLabeledRuleStatement) child).getBody())) {
                        complexity++;
                    }
                } else {
                    accepted &= filter.test(child);
                    pendingBranch = true;
                }
            }
            if (pendingBranch && accepted && nonDefault) {
                complexity++;
            }
        }

        @Override
        public void visitWhileStatement(PsiWhileStatement statement) {
            super.visitWhileStatement(statement);
            if (filter.test(statement)) {
                complexity++;
            }
        }

        @Override
        public void visitCatchSection(PsiCatchSection section) {
            super.visitCatchSection(section);
            if (filter.test(section)) {
                complexity++;
            }
        }

        @Override
        public void visitPolyadicExpression(PsiPolyadicExpression expression) {
            super.visitPolyadicExpression(expression);
            if (!filter.test(expression)) {
                return;
            }
            final IElementType token = expression.getOperationTokenType();
            if (token.equals(JavaTokenType.ANDAND) || token.equals(JavaTokenType.OROR)) {
                complexity += expression.getOperands().length - 1;
            }
        }

        public int getCyclomaticComplexity() {
            return complexity;
        }
    }
}
