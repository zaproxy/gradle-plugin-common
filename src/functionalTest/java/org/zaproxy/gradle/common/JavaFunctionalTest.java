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

import java.nio.file.Path;

public abstract class JavaFunctionalTest extends FunctionalTest {

    protected static final String CONTENT_JAVA_FILE = "package org.zaproxy.example;";

    protected void buildFileWithoutJavaPlugin() throws Exception {
        buildFile(
                "plugins {\n"
                        + "    id(\"com.diffplug.spotless\")\n"
                        + "    id(\"org.zaproxy.common\")\n"
                        + "}");
    }

    protected void buildFileWithJavaPlugin() throws Exception {
        buildFile(
                "plugins {\n"
                        + "    `java-library`\n"
                        + "    id(\"com.diffplug.spotless\")\n"
                        + "    id(\"org.zaproxy.common\")\n"
                        + "}\n"
                        + "\n"
                        + "repositories {\n"
                        + "    mavenCentral()\n"
                        + "}");
    }

    protected Path createJavaFile() throws Exception {
        var javaFile = projectDir.resolve("src/main/java/org/zaproxy/example/Example.java");
        createFile(CONTENT_JAVA_FILE, javaFile);
        return javaFile;
    }
}
