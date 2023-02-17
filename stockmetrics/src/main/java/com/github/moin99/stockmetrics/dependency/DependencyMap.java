/*
 * Copyright 2005-2016 Sixth and Red River Software, Bas Leijdekkers
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

package com.github.moin99.stockmetrics.dependency;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiPackage;

import java.util.Set;

public interface DependencyMap {

    Set<PsiClass> calculateDependencies(PsiClass aClass);

    Set<PsiClass> calculateTransitiveDependencies(PsiClass aClass);

    Set<PsiClass> calculateStronglyConnectedComponents(PsiClass aClass);

    int calculateLevelOrder(PsiClass aClass);

    int calculateAdjustedLevelOrder(PsiClass aClass);

    Set<PsiPackage> calculatePackageDependencies(PsiClass aClass);

    Set<PsiPackage> calculateTransitivePackageDependencies(PsiPackage aPackage);

    Set<PsiPackage> calculateStronglyConnectedPackageComponents(PsiPackage aPackage);

    int calculatePackageLevelOrder(PsiPackage aPackage);

    int calculatePackageAdjustedLevelOrder(PsiPackage aPackage);

    int getStrengthForDependency(PsiClass aClass, PsiClass dependencyClass);

    int getStrengthForPackageDependency(PsiClass aClass, PsiPackage dependencyPackage);

    Set<PsiPackage> calculatePackageToPackageDependencies(PsiPackage aPackage);
}
