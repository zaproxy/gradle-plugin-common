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

import static org.zaproxy.gradle.common.spotless.FormatPropertiesStep.InvalidPropertiesException.InvalidPropertyReason.DUPLICATE;
import static org.zaproxy.gradle.common.spotless.FormatPropertiesStep.InvalidPropertiesException.InvalidPropertyReason.LEADING_SPACE_IN_NAME;

import com.diffplug.spotless.FormatterStep;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfigurationLayout;
import org.jetbrains.annotations.NotNull;
import org.zaproxy.gradle.common.spotless.FormatPropertiesStep.InvalidPropertiesException.InvalidPropertyReason;

/** A Spotless formatter step that formats properties files. */
public final class FormatPropertiesStep {

    private FormatPropertiesStep() {}

    /**
     * Creates a new {@link FormatPropertiesStep}.
     *
     * @return a {@link FormatterStep}
     */
    public static FormatterStep create() {
        return FormatterStep.create(
                "properties", FormatPropertiesStep.class, unused -> FormatPropertiesStep::format);
    }

    @NotNull
    static String format(String rawUnix) throws Exception {
        var properties = new PropertiesConfiguration();
        properties.setLayout(new SortedPropertiesConfigurationLayout());
        properties.setIOFactory(new PropertiesConfiguration.JupIOFactory(false));
        properties.read(new StringReader(rawUnix));

        Map<InvalidPropertyReason, List<String>> invalidProperties =
                new EnumMap<>(InvalidPropertyReason.class);
        properties
                .getKeys()
                .forEachRemaining(
                        key -> {
                            if ("".equals(key)) {
                                invalidProperties
                                        .computeIfAbsent(
                                                LEADING_SPACE_IN_NAME, k -> new ArrayList<>())
                                        .add(properties.getString(key).split("=", 2)[0].trim());
                            } else if (!properties.getLayout().isSingleLine(key)) {
                                invalidProperties
                                        .computeIfAbsent(DUPLICATE, k -> new ArrayList<>())
                                        .add(key);
                            }
                        });
        if (!invalidProperties.isEmpty()) {
            throw new InvalidPropertiesException(invalidProperties);
        }

        properties.getLayout().setGlobalSeparator(" = ");
        var writer = new StringWriter();
        properties.write(writer);
        return writer.toString();
    }

    private static class SortedPropertiesConfigurationLayout extends PropertiesConfigurationLayout {
        @Override
        public Set<String> getKeys() {
            return new TreeSet<>(super.getKeys());
        }
    }

    static class InvalidPropertiesException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public InvalidPropertiesException(
                Map<InvalidPropertyReason, List<String>> invalidProperties) {
            super(createMessage(invalidProperties));
        }

        private static String createMessage(
                Map<InvalidPropertyReason, List<String>> invalidProperties) {
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid properties:\n");
            invalidProperties.forEach(
                    (reason, properties) ->
                            sb.append(reason.toString())
                                    .append(": ")
                                    .append(properties.toString())
                                    .append('\n'));
            return sb.toString();
        }

        enum InvalidPropertyReason {
            DUPLICATE,
            LEADING_SPACE_IN_NAME
        }
    }
}
