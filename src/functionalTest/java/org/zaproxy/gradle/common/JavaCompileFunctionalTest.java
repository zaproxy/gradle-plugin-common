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
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.UnexpectedBuildFailure;
import org.junit.jupiter.api.Test;

class JavaCompileFunctionalTest extends FunctionalTest {

    private static final String COMPILE_JAVA = ":compileJava";

    @Test
    void shouldCompileWithUnicodeChars() throws Exception {
        // Given
        buildFileWithJavaPlugin();
        javaClassWith("        String str = \"❌🎉️\"; System.out.println(str);\n");
        // When
        BuildResult result = build(COMPILE_JAVA);
        // Then
        assertTaskSuccess(result, COMPILE_JAVA);
    }

    private void buildFileWithJavaPlugin() throws Exception {
        buildFile(
                "plugins {\n"
                        + "    `java-library`\n"
                        + "    id(\"com.diffplug.spotless\")\n"
                        + "    id(\"org.zaproxy.common\")\n"
                        + "}");
    }

    @Test
    void shouldFailWithWarningsAsErrors() throws Exception {
        // Given
        buildFileWithJavaPlugin();
        javaClassWith(
                "        java.util.List l = new java.util.ArrayList<Number>();\n"
                        + "        java.util.List<String> ls = l;\n");
        // When / Then
        UnexpectedBuildFailure ex =
                assertThrows(UnexpectedBuildFailure.class, () -> build(COMPILE_JAVA));
        BuildResult result = ex.getBuildResult();
        assertTaskFailed(result, COMPILE_JAVA);
        assertThat(
                result.getOutput(), containsString("error: warnings found and -Werror specified"));
    }

    private Path javaClassWith(String body) throws Exception {
        var javaFile = projectDir.resolve("src/main/java/org/zaproxy/example/Example.java");
        createFile(
                "package org.zaproxy.example;\n"
                        + "\n"
                        + "public class Example {\n"
                        + "    public static void main(String[] args) {\n"
                        + body
                        + "    }\n"
                        + "}",
                javaFile);
        return javaFile;
    }
}
