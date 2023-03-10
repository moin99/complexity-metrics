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
import com.intellij.psi.*;
import org.jetbrains.annotations.NonNls;

import java.util.*;

public class LackOfCohesionOfMethodsClassCalculator extends ClassCalculator {

    private static final @NonNls Set<String> boilerplateMethods = new HashSet<>();

    static {
        //noinspection HardCodedStringLiteral
        Collections.addAll(boilerplateMethods, "toString", "equals", "hashCode", "finalize", "clone", "readObject",
                "writeObject");
    }

    public LackOfCohesionOfMethodsClassCalculator(Metric metric) {
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
                final PsiMethod[] methods = aClass.getMethods();
                final Set<PsiMethod> applicableMethods = new HashSet<>();
                for (PsiMethod method : methods) {
                    final String methodName = method.getName();
                    if (!method.isConstructor() && !boilerplateMethods.contains(methodName)) {
                        applicableMethods.add(method);
                    }
                }
                final Map<PsiMethod, Set<PsiField>> fieldsPerMethod = calculateFieldUsage(applicableMethods);
                final Map<PsiMethod, Set<PsiMethod>> linkedMethods = calculateMethodLinkage(applicableMethods);
                final Set<Set<PsiMethod>> components = calculateComponents(applicableMethods, fieldsPerMethod,
                        linkedMethods);
                final int numComponents = components.size();
                postMetric(aClass, numComponents);
            }
        }
    }

    private static Set<Set<PsiMethod>> calculateComponents(Set<PsiMethod> applicableMethods,
                                                           Map<PsiMethod, Set<PsiField>> fieldsPerMethod,
                                                           Map<PsiMethod, Set<PsiMethod>> linkedMethods) {
        final Set<Set<PsiMethod>> components = new HashSet<>();
        while (!applicableMethods.isEmpty()) {
            final Set<PsiMethod> component = new HashSet<>();
            final Set<PsiField> fieldsUsed = new HashSet<>();
            final PsiMethod testMethod = applicableMethods.iterator().next();
            applicableMethods.remove(testMethod);
            component.add(testMethod);
            fieldsUsed.addAll(fieldsPerMethod.get(testMethod));
            while (true) {
                final Set<PsiMethod> methodsToAdd = new HashSet<>();
                for (PsiMethod method : applicableMethods) {
                    if (overlaps(fieldsPerMethod.get(method), fieldsUsed) ||
                            overlaps(linkedMethods.get(method), component)) {
                        methodsToAdd.add(method);
                        fieldsUsed.addAll(fieldsPerMethod.get(method));
                    }
                }
                if (methodsToAdd.isEmpty()) {
                    break;
                }
                applicableMethods.removeAll(methodsToAdd);
                component.addAll(methodsToAdd);
            }
            components.add(component);
        }
        return components;
    }

    private static boolean overlaps(Set<?> set1, Set<?> set2) {
        for (Object element : set1) {
            if (set2.contains(element)) {
                return true;
            }
        }
        return false;
    }

    private static Map<PsiMethod, Set<PsiField>> calculateFieldUsage(Set<PsiMethod> applicableMethods) {
        final Map<PsiMethod, Set<PsiField>> fieldsPerMethod = new HashMap<>();
        for (PsiMethod method : applicableMethods) {
            final Set<PsiField> fields = calculateUsedFields(method);
            fieldsPerMethod.put(method, fields);
        }
        return fieldsPerMethod;
    }

    private static Set<PsiField> calculateUsedFields(PsiMethod method) {
        final FieldsUsedVisitor visitor = new FieldsUsedVisitor();
        method.accept(visitor);
        return visitor.getFieldsUsed();
    }

    private static class FieldsUsedVisitor extends JavaRecursiveElementVisitor {
        private final Set<PsiField> fieldsUsed = new HashSet<>();

        FieldsUsedVisitor() {
        }

        @Override
        public void visitReferenceExpression(PsiReferenceExpression referenceExpression) {
            super.visitReferenceExpression(referenceExpression);
            final PsiElement referent = referenceExpression.resolve();
            if (!(referent instanceof PsiField)) {
                return;
            }
            final PsiField field = (PsiField) referent;
            fieldsUsed.add(field);
        }

        @SuppressWarnings({"ReturnOfCollectionOrArrayField"})
        public Set<PsiField> getFieldsUsed() {
            return fieldsUsed;
        }
    }

    private static Map<PsiMethod, Set<PsiMethod>> calculateMethodLinkage(Set<PsiMethod> applicableMethods) {
        final Map<PsiMethod, Set<PsiMethod>> linkages = new HashMap<>();
        for (PsiMethod method : applicableMethods) {
            final Set<PsiMethod> linkedMethods = calculateLinkedMethods(method, applicableMethods);
            linkages.put(method, linkedMethods);
        }
        //add the transpose, since linkage is undirected
        for (PsiMethod method : applicableMethods) {
            final Set<PsiMethod> linkedMethods = linkages.get(method);
            for (PsiMethod linkedMethod : linkedMethods) {
                linkages.get(linkedMethod).add(method);
            }
        }
        return linkages;
    }

    private static Set<PsiMethod> calculateLinkedMethods(PsiMethod method, Set<PsiMethod> applicableMethods) {
        final MethodsUsedVisitor visitor = new MethodsUsedVisitor(applicableMethods);
        method.accept(visitor);
        return visitor.getMethodsUsed();
    }

    private static class MethodsUsedVisitor extends JavaRecursiveElementVisitor {
        private final Set<PsiMethod> applicableMethods;
        private final Set<PsiMethod> methodsUsed = new HashSet<>();

        MethodsUsedVisitor(Set<PsiMethod> applicableMethods) {
            this.applicableMethods = applicableMethods;
        }

        @Override
        public void visitMethodCallExpression(PsiMethodCallExpression callExpression) {
            super.visitMethodCallExpression(callExpression);
            final PsiMethod testMethod = callExpression.resolveMethod();
            if (applicableMethods.contains(testMethod)) {
                methodsUsed.add(testMethod);
            }
        }

        @SuppressWarnings({"ReturnOfCollectionOrArrayField"})
        public Set<PsiMethod> getMethodsUsed() {
            return methodsUsed;
        }
    }
}
