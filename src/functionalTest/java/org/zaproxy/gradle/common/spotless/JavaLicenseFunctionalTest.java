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
package org.zaproxy.gradle.common.spotless;

import static org.assertj.core.api.Assertions.assertThat;

import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.Test;
import org.zaproxy.gradle.common.JavaFunctionalTest;

class JavaLicenseFunctionalTest extends JavaFunctionalTest {

    private static final String SPOTLESS_APPLY = ":spotlessApply";

    @Test
    void shouldNotFormatIfJavaPluginNotApplied() throws Exception {
        // Given
        buildFileWithoutJavaPlugin();
        var javaFile = createJavaFile();
        // When
        BuildResult result = build(SPOTLESS_APPLY);
        // Then
        assertTaskSuccess(result, SPOTLESS_APPLY);
        assertThat(javaFile).content().isEqualTo(CONTENT_JAVA_FILE);
    }

    @Test
    void shouldFormatWithJavaLicense() throws Exception {
        // Given
        buildFileWithJavaPlugin();
        var javaFile = createJavaFile();
        // When
        BuildResult result = build(SPOTLESS_APPLY);
        // Then
        assertTaskSuccess(result, SPOTLESS_APPLY);
        assertThat(javaFile)
                .content()
                .startsWith("/*")
                .contains("Zed Attack Proxy (ZAP) and its related class files.")
                .contains("*/")
                .contains(CONTENT_JAVA_FILE);
    }
}
