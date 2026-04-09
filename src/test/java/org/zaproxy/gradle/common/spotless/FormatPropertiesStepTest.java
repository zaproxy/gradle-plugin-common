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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class FormatPropertiesStepTest {
    @Test
    void shouldTrimSpacesBeforeSeparator() throws Exception {
        assertThat(FormatPropertiesStep.format("spaces.before.separator     = value\n"))
                .isEqualTo("spaces.before.separator = value\n");
    }

    @Test
    void shouldTrimSpacesAfterSeparator() throws Exception {
        assertThat(FormatPropertiesStep.format("spaces.after.separator =      value\n"))
                .isEqualTo("spaces.after.separator = value\n");
    }

    @Test
    void shouldAddSpacesToSeparatorWithNoSpaces() throws Exception {
        assertThat(FormatPropertiesStep.format("no.spaces.separator=value\n"))
                .isEqualTo("no.spaces.separator = value\n");
    }

    @Test
    void shouldFormatMultilineValue() throws Exception {
        assertThat(
                        FormatPropertiesStep.format(
                                "multiline.value = value1 \\\n  value2 \\\n  value3\n"))
                .isEqualTo("multiline.value = value1 value2 value3\n");
    }

    @Test
    void shouldIgnoreComments() throws Exception {
        assertThat(FormatPropertiesStep.format("# comment1\n    #    comment2\n! comment3\n"))
                .isEqualTo("# comment1\n    #    comment2\n! comment3\n");
    }

    @Test
    void shouldFormatKeyWithNoValueNoSeparator() throws Exception {
        assertThat(FormatPropertiesStep.format("no.value\n")).isEqualTo("no.value = \n");
    }

    @Test
    void shouldFormatKeyWithNoValueWithSeparator() throws Exception {
        assertThat(FormatPropertiesStep.format("no.value.with.separator =\n"))
                .isEqualTo("no.value.with.separator = \n");
    }

    @Test
    void shouldIgnoreSpacesInKey() throws Exception {
        assertThat(FormatPropertiesStep.format("spaces\\ in\\ key = value\n"))
                .isEqualTo("spaces\\ in\\ key = value\n");
    }

    @Test
    void shouldIgnoreCommas() throws Exception {
        assertThat(FormatPropertiesStep.format("commas = value1, value2, value3\n"))
                .isEqualTo("commas = value1, value2, value3\n");
    }

    @Test
    void shouldIgnoreDoubleQuotes() throws Exception {
        assertThat(FormatPropertiesStep.format("quotes = \"value\"\n"))
                .isEqualTo("quotes = \"value\"\n");
    }

    @Test
    void shouldIgnoreEqualsSymbolInValue() throws Exception {
        assertThat(FormatPropertiesStep.format("equals = value=1\n"))
                .isEqualTo("equals = value=1\n");
    }

    @Test
    void shouldIgnoreArgs() throws Exception {
        assertThat(FormatPropertiesStep.format("args = value1 {0}\n"))
                .isEqualTo("args = value1 {0}\n");
    }

    @Test
    void shouldIgnoreQuotedArgs() throws Exception {
        assertThat(FormatPropertiesStep.format("quoted.args = value1 \"{0}\"\n"))
                .isEqualTo("quoted.args = value1 \"{0}\"\n");
    }

    @Test
    void shouldIgnoreSingleQuotedArgs() throws Exception {
        assertThat(FormatPropertiesStep.format("single.quoted.args = value1 ''{0}''\n"))
                .isEqualTo("single.quoted.args = value1 ''{0}''\n");
    }

    @Test
    void shouldIgnoreSingleQuotes() throws Exception {
        assertThat(FormatPropertiesStep.format("single.quotes = 'value'\n"))
                .isEqualTo("single.quotes = 'value'\n");
    }

    @Test
    void shouldConvertColonSeparator() throws Exception {
        assertThat(FormatPropertiesStep.format("colon.separator: value\n"))
                .isEqualTo("colon.separator = value\n");
    }

    @Test
    void shouldConvertSpaceSeparator() throws Exception {
        assertThat(FormatPropertiesStep.format("space.separator value\n"))
                .isEqualTo("space.separator = value\n");
    }

    @Test
    void shouldThrowExceptionOnDuplicateKeys() {
        assertThatThrownBy(
                        () ->
                                FormatPropertiesStep.format(
                                        "duplicate.key = value1\nduplicate.key = value2\n"))
                .isInstanceOf(FormatPropertiesStep.InvalidPropertiesException.class)
                .hasMessage("Invalid properties:\nDUPLICATE: [duplicate.key]\n");
    }

    @Test
    void shouldIgnoreEscapeChars() throws Exception {
        assertThat(FormatPropertiesStep.format("escape.chars = value1\\nvalue2\\tvalue3\n"))
                .isEqualTo("escape.chars = value1\\nvalue2\\tvalue3\n");
    }

    @Test
    void shouldPreserveTrailingWhitespace() throws Exception {
        assertThat(FormatPropertiesStep.format("trailing.whitespace = value   \n"))
                .isEqualTo("trailing.whitespace = value   \n");
    }

    @Test
    void shouldPreserveUnicodeSpaces() throws Exception {
        assertThat(FormatPropertiesStep.format("unicode.whitespace = \\u0020hello world\\u0020"))
                .isEqualTo("unicode.whitespace = \\ hello world \n");
    }

    @Test
    void shouldIgnoreDecodedUnicode() throws Exception {
        assertThat(FormatPropertiesStep.format("unicode.unencoded = नमस्ते\n"))
                .isEqualTo("unicode.unencoded = नमस्ते\n");
    }

    @Test
    void shouldDecodeEncodedUnicode() throws Exception {
        assertThat(
                        FormatPropertiesStep.format(
                                "unicode.encoded = \\u0928\\u092E\\u0938\\u094D\\u0924\\u0947\n"))
                .isEqualTo("unicode.encoded = नमस्ते\n");
    }

    @Test
    void shouldAddNewlineAtEndOfLine() throws Exception {
        assertThat(FormatPropertiesStep.format("no.newline = value"))
                .isEqualTo("no.newline = value\n");
    }

    @Test
    void shouldNotTrimNewlines() throws Exception {
        assertThat(FormatPropertiesStep.format("key = \\n\\nvalue\\n\\n"))
                .isEqualTo("key = \\n\\nvalue\\n\\n\n");
    }

    @Test
    void shouldNotTrimTabs() throws Exception {
        assertThat(FormatPropertiesStep.format("key = \\t\\tvalue\\t\\t"))
                .isEqualTo("key = \\t\\tvalue\\t\\t\n");
    }

    @Test
    void shouldNotEscapeLeadingBackslashes() throws Exception {
        assertThat(FormatPropertiesStep.format("key = \\ value")).isEqualTo("key = \\ value\n");
    }

    @Test
    void shouldThrowExceptionOnKeysWithLeadingSpaces() {
        assertThatThrownBy(() -> FormatPropertiesStep.format(" key = value1"))
                .isInstanceOf(FormatPropertiesStep.InvalidPropertiesException.class)
                .hasMessage("Invalid properties:\nLEADING_SPACE_IN_NAME: [key]\n");
    }
}
