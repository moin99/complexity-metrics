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

package com.github.moin99.stockmetrics.execution;

import com.github.moin99.complexitymetrics.metrics.Metric;
import com.github.moin99.complexitymetrics.metrics.MetricCalculator;
import com.github.moin99.complexitymetrics.metrics.MetricsExecutionContext;
import com.github.moin99.complexitymetrics.metrics.MetricsResultsHolder;
import com.github.moin99.stockmetrics.dependency.DependencyMap;
import com.github.moin99.stockmetrics.dependency.DependencyMapImpl;
import com.github.moin99.stockmetrics.dependency.DependentsMap;
import com.github.moin99.stockmetrics.i18n.StockMetricsBundle;
import com.intellij.analysis.AnalysisScope;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.Processor;

public abstract class BaseMetricsCalculator implements MetricCalculator {

    private static final Key<DependencyMapImpl> dependencyMapKey = new Key<>("dependencyMap");

    protected final Metric metric;
    protected MetricsResultsHolder resultsHolder = null;
    protected MetricsExecutionContext executionContext = null;
    private PsiElementVisitor visitor;

    public BaseMetricsCalculator(Metric metric) {
        this.metric = metric;
    }

    @Override
    public void beginMetricsRun(MetricsResultsHolder resultsHolder, MetricsExecutionContext executionContext) {
        this.resultsHolder = resultsHolder;
        this.executionContext = executionContext;
        if (metric.requiresDependents() && getDependencyMap() == null) {
            calculateDependencies();
        }
        visitor = createVisitor();
    }

    @Override
    public void processFile(final PsiFile file) {
        ProgressManager.getInstance().runProcess(() -> file.accept(visitor), new EmptyProgressIndicator());
    }

    protected abstract PsiElementVisitor createVisitor();

    @Override
    public void endMetricsRun() {}

    public DependencyMap getDependencyMap() {
        assert metric.requiresDependents();
        return executionContext.getUserData(dependencyMapKey);
    }

    public DependentsMap getDependentsMap() {
        assert metric.requiresDependents();
        return executionContext.getUserData(dependencyMapKey);
    }

    private void calculateDependencies() {
        final DependencyMapImpl dependencyMap = new DependencyMapImpl();
        final ProgressManager progressManager = ProgressManager.getInstance();
        final ProgressIndicator progressIndicator = progressManager.getProgressIndicator();

        final Project project = executionContext.getProject();
        final AnalysisScope analysisScope = new AnalysisScope(project);
        final int allFilesCount = analysisScope.getFileCount();
        final PsiManager psiManager = PsiManager.getInstance(project);

        analysisScope.accept(new Processor<VirtualFile>() {

            private int dependencyProgress = 0;

            @Override
            public boolean process(VirtualFile virtualFile) {
                final String fileName = virtualFile.getName();
                progressIndicator.setText(
                        StockMetricsBundle.message("building.dependency.structure.progress.string", fileName));
                progressIndicator.setFraction((double) dependencyProgress / (double) allFilesCount);
                dependencyProgress++;
                if (virtualFile.getFileType() != JavaFileType.INSTANCE) {
                    return true;
                }
                ReadAction.run(() -> {
                    final PsiFile file = psiManager.findFile(virtualFile);
                    if (!(file instanceof PsiJavaFile)) {
                        return;
                    }
                    dependencyMap.build(file);
                });
                return true;
            }
        });
        executionContext.putUserData(dependencyMapKey, dependencyMap);
    }
}
