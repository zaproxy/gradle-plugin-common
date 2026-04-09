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

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.Test;
import org.zaproxy.gradle.common.FunctionalTest;

class FormatPropertiesStepFunctionalTest extends FunctionalTest {
    @Test
    void shouldFormatProperties() throws Exception {
        // Given
        Path originalBuildFilePath =
                Path.of(getClass().getResource("spotlessProperties.gradle.kts").toURI());
        Path copiedBuildFilePath = projectDir.resolve("build.gradle.kts");
        Path originalUnformattedPropertiesPath =
                Path.of(getClass().getResource("unformatted.properties").toURI());
        Path copiedUnformattedPropertiesPath = projectDir.resolve("unformatted.properties");
        Path formattedPropertiesPath =
                Path.of(getClass().getResource("formatted.properties").toURI());
        String spotlessPropertiesApplyTaskName = ":spotlessPropertiesApply";

        Files.copy(originalUnformattedPropertiesPath, copiedUnformattedPropertiesPath);
        Files.copy(originalBuildFilePath, copiedBuildFilePath);
        // When
        BuildResult result = build(spotlessPropertiesApplyTaskName);
        // Then
        assertTaskSuccess(result, spotlessPropertiesApplyTaskName);
        assertThat(copiedUnformattedPropertiesPath).hasSameBinaryContentAs(formattedPropertiesPath);

        var properties = new Properties();
        properties.load(
                new InputStreamReader(
                        Files.newInputStream(copiedUnformattedPropertiesPath),
                        StandardCharsets.UTF_8));
        assertThat(properties)
                .hasSize(22)
                .containsEntry("commas", "value1, value2, value3")
                .containsEntry("spaces.after.separator", "value")
                .containsEntry("no.value", "")
                .containsEntry("colon.separator", "value")
                .containsEntry("unicode.unencoded", "नमस्ते")
                .containsEntry("spaces in key", "value")
                .containsEntry("quoted.args", "value \"{0}\"")
                .containsEntry("single.quotes", "'value'")
                .containsEntry("trailing.whitespace", "value    ")
                .containsEntry("unicode.whitespace", " hello world ")
                .containsEntry("quotes", "\"value\"")
                .containsEntry("args", "value {0}")
                .containsEntry("single.quoted.args", "value ''{0}''")
                .containsEntry("escape.chars", "\nvalue1\nvalue2\tvalue3\t")
                .containsEntry("spaces.before.separator", "value")
                .containsEntry("no.spaces.separator", "value")
                .containsEntry("no.value.with.separator", "")
                .containsEntry("equals", "value=1")
                .containsEntry("unicode.encoded", "नमस्ते")
                .containsEntry("space.separator", "value")
                .containsEntry("multiline.value", "value1 value2 value3")
                .containsEntry("leading.escaped.space", " value");
    }
}
