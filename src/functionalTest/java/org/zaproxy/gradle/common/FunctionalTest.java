/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2023 The ZAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.gradle.common;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.io.TempDir;

public abstract class FunctionalTest {

    @TempDir protected Path projectDir;

    protected static String contentOf(Path file) throws IOException {
        return Files.readString(file);
    }

    protected static void createFile(String content, Path file) throws Exception {
        Files.createDirectories(file.getParent());
        Files.writeString(file, content);
    }

    protected void buildFile(String content) throws Exception {
        createFile(content, projectDir.resolve("build.gradle.kts"));
    }

    protected static Iterable<? extends File> pluginClasspath() throws Exception {
        Path pluginClasspath =
                Path.of(
                        FunctionalTest.class
                                .getClassLoader()
                                .getResource("pluginClasspath.txt")
                                .toURI());
        return Files.readAllLines(pluginClasspath).stream()
                .map(File::new)
                .collect(Collectors.toList());
    }

    protected BuildResult build(String... arguments) throws Exception {
        return GradleRunner.create()
                .withProjectDir(projectDir.toFile())
                .withArguments(arguments)
                .withPluginClasspath(pluginClasspath())
                .build();
    }

    protected static void assertTaskSuccess(BuildResult result, String taskName) {
        assertTaskOutcome(result, taskName, TaskOutcome.SUCCESS);
    }

    private static void assertTaskOutcome(
            BuildResult result, String taskName, TaskOutcome outcome) {
        BuildTask task = result.task(taskName);
        assertThat(task, is(notNullValue()));
        assertThat(task.getOutcome(), is(outcome));
    }

    protected static void assertTaskFailed(BuildResult result, String taskName) {
        assertTaskOutcome(result, taskName, TaskOutcome.FAILED);
    }
}
