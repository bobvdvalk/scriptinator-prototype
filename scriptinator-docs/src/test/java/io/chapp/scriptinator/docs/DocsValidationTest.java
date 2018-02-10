/*
 * Copyright © 2018 Scriptinator (support@scriptinator.io)
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
package io.chapp.scriptinator.docs;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.Assert.assertTrue;

public class DocsValidationTest {
    private static final Path GENERATED_DOCS = Paths.get("target/generated-docs").toAbsolutePath();
    private static final Path INDEX = GENERATED_DOCS.resolve("index.html");
    private static final Pattern UNRESOLVED_DIRECTIVE = Pattern.compile(".*Unresolved directive in .* include::([^\\[]*)\\[].*");

    @Test
    public void testDocsAreGenerated() {
        assertTrue(
                Files.isRegularFile(INDEX),
                "Generated index.html exists at " + INDEX
        );
    }

    @Test
    public void testAllDirectivesAreResolved() throws IOException {

        SoftAssert softAssert = new SoftAssert();

        for (String line : Files.readAllLines(INDEX)) {
            Matcher matcher = UNRESOLVED_DIRECTIVE.matcher(line);
            if (matcher.matches()) {
                String path = matcher.group(1);
                softAssert.fail("Missing included snippet: " + path);
            }
        }

        softAssert.assertAll();
    }

}
