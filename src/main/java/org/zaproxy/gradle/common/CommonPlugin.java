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

import com.diffplug.gradle.spotless.SpotlessExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.zaproxy.gradle.common.spotless.FormatPropertiesStep;

/** A plugin for common ZAP build-related configs and tasks. */
public class CommonPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        var spotlessExtension = target.getExtensions().findByType(SpotlessExtension.class);
        if (spotlessExtension != null) {
            spotlessExtension.format(
                    "properties",
                    format -> {
                        format.target("**/*.properties");
                        format.targetExclude(
                                "**/Messages_*_*.properties",
                                "**/gradle.properties",
                                "**/gradle-wrapper.properties");
                        format.addStep(FormatPropertiesStep.create());
                    });
        }
    }
}
