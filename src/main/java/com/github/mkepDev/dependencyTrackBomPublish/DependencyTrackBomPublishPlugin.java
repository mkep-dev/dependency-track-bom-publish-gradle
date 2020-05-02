/*
 * Copyright (c) 2020.
 * Markus Keppeler
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.mkepDev.dependencyTrackBomPublish;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

/**
 * Plugin to publish a generated software bom to a dependency track server
 */
public class DependencyTrackBomPublishPlugin implements Plugin<Project> {

    /**
     * The name of the task category
     */
    public static final String categoryName = "dependencyTrackPublishBom";

    /**
     * The logger
     */
    private final Logger logger = Logging.getLogger(this.getClass());


    @Override
    public void apply(Project project) {

        DependencyTrackBomPublishExtension extension = project.getExtensions().create(DependencyTrackBomPublishExtension.EXTENSION_IDENTIFIER, DependencyTrackBomPublishExtension.class, project.getBuildDir());

        Task k = project.getTasks().create(DependencyTrackPublishBomTask.NAME, DependencyTrackPublishBomTask.class, extension, logger);

//        if (extension.getUseInternalCycloneDx()) {
//            logger.info("Use internal cycloneDx task to generate bom");
//            CycloneDxTask cycloneTask = project.getTasks().create("DtrackPublishInternalCycloneDxBomCreation", CycloneDxTask.class);
//            k.dependsOn(cycloneTask);
//            cycloneTask.setBuildDir(project.getBuildDir());
//        }
    }

}
