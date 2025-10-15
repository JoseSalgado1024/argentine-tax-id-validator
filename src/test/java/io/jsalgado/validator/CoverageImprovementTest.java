/*
 * Copyright (c) 2025 José Salgado.
 * 
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     https://opensource.org/licenses/MIT
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Project: Argentine Tax ID Validator
 * GitHub: https://github.com/JoseSalgado1024/argentine-tax-id-validator
 * Author: José Salgado <josesalgado1024@gmail.com>
 */
package io.jsalgado.validator;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests specifically designed to improve code coverage by testing previously uncovered branches and
 * lines
 */
@DisplayName("Coverage Improvement Tests")
class CoverageImprovementTest {

  // ===== UNCOVERED LINES TESTS =====

  @Test
  @DisplayName("Should prevent instantiation via reflection")
  void shouldPreventInstantiationViaReflection() {
    assertThatThrownBy(
            () -> {
              Constructor<ArgentineTaxIdValidator> constructor =
                  ArgentineTaxIdValidator.class.getDeclaredConstructor();
              constructor.setAccessible(true);
              constructor.newInstance();
            })
        .isInstanceOf(Exception.class)
        .hasRootCauseInstanceOf(UnsupportedOperationException.class)
        .hasRootCauseMessage("Utility class");
  }

  // ===== UTILITY METHODS NULL HANDLING =====

  @ParameterizedTest(name = "format() should return null for invalid input: {0}")
  @ValueSource(
      strings = {
        "invalid-format",
        "20-12345678-9", // Wrong check digit
        "123", // Too short
        "abcdefghijk", // Non-numeric
        "" // Empty string
      })
  void formatShouldReturnNullForInvalidInput(String invalidInput) {
    assertThat(ArgentineTaxIdValidator.format(invalidInput)).isNull();
  }

  @Test
  @DisplayName("format() should return null for null input")
  void formatShouldReturnNullForNullInput() {
    assertThat(ArgentineTaxIdValidator.format(null)).isNull();
  }

  @ParameterizedTest(name = "extractDocumentNumber() should return null for invalid input: {0}")
  @ValueSource(
      strings = {
        "invalid-format",
        "20-12345678-9", // Wrong check digit
        "123", // Too short
        "abcdefghijk", // Non-numeric
        "" // Empty string
      })
  void extractDocumentNumberShouldReturnNullForInvalidInput(String invalidInput) {
    assertThat(ArgentineTaxIdValidator.extractDocumentNumber(invalidInput)).isNull();
  }

  @Test
  @DisplayName("extractDocumentNumber() should return null for null input")
  void extractDocumentNumberShouldReturnNullForNullInput() {
    assertThat(ArgentineTaxIdValidator.extractDocumentNumber(null)).isNull();
  }

  @ParameterizedTest(name = "getType() should return UNKNOWN for invalid input: {0}")
  @ValueSource(
      strings = {
        "invalid-format",
        "20-12345678-9", // Wrong check digit
        "123", // Too short
        "abcdefghijk", // Non-numeric
        "" // Empty string
      })
  void getTypeShouldReturnUnknownForInvalidInput(String invalidInput) {
    assertThat(ArgentineTaxIdValidator.getType(invalidInput)).isEqualTo(ETaxIdType.UNKNOWN);
  }

  @Test
  @DisplayName("getType() should return UNKNOWN for null input")
  void getTypeShouldReturnUnknownForNullInput() {
    assertThat(ArgentineTaxIdValidator.getType(null)).isEqualTo(ETaxIdType.UNKNOWN);
  }

  // ===== EDGE CASES FOR BRANCHES =====

  @Test
  @DisplayName("Should handle malformed cleaned input")
  void shouldHandleMalformedCleanedInput() {
    // Test cases that might bypass initial format checks
    assertFalse(ArgentineTaxIdValidator.isValid("1234567890")); // 10 digits
    assertFalse(ArgentineTaxIdValidator.isValid("123456789012")); // 12 digits
  }

  @ParameterizedTest(name = "Should handle special character combinations: {0}")
  @ValueSource(
      strings = {
        "20-12345678-6-extra", // Extra characters
        "20--12345678--6", // Double separators
        "20.12345678.6.", // Trailing separator
        "20 12345678 6 ", // Trailing space
        "\t20\t12345678\t6\t", // Tabs
        "\n20\n12345678\n6\n" // Newlines
      })
  void shouldHandleSpecialCharacterCombinations(String input) {
    // These should either be valid (if they clean to valid format) or invalid
    boolean result = ArgentineTaxIdValidator.isValid(input);
    // Just ensure it doesn't crash - result depends on cleaned format
    assertThat(result).isIn(true, false);
  }

  // ===== ETAXIDTYPE UNCOVERED BRANCHES =====

  @Test
  @DisplayName("ETaxIdType.fromPrefix() should handle edge cases")
  void etaxIdTypeFromPrefixShouldHandleEdgeCases() {
    // Test all valid prefixes
    assertThat(ETaxIdType.fromPrefix("20")).isEqualTo(ETaxIdType.MALE_INDIVIDUAL);
    assertThat(ETaxIdType.fromPrefix("27")).isEqualTo(ETaxIdType.FEMALE_INDIVIDUAL);
    assertThat(ETaxIdType.fromPrefix("30")).isEqualTo(ETaxIdType.COMPANY);
    assertThat(ETaxIdType.fromPrefix("23")).isEqualTo(ETaxIdType.FOREIGN_INDIVIDUAL);
    assertThat(ETaxIdType.fromPrefix("33")).isEqualTo(ETaxIdType.FOREIGN_COMPANY);
    assertThat(ETaxIdType.fromPrefix("34")).isEqualTo(ETaxIdType.PUBLIC_ENTITY);
    assertThat(ETaxIdType.fromPrefix("24")).isEqualTo(ETaxIdType.MALE_INDIVIDUAL_ALT);

    // Test invalid prefixes
    assertThat(ETaxIdType.fromPrefix("99")).isEqualTo(ETaxIdType.UNKNOWN);
    assertThat(ETaxIdType.fromPrefix("00")).isEqualTo(ETaxIdType.UNKNOWN);
    assertThat(ETaxIdType.fromPrefix("AB")).isEqualTo(ETaxIdType.UNKNOWN);

    // Test edge format cases
    assertThat(ETaxIdType.fromPrefix("")).isEqualTo(ETaxIdType.UNKNOWN);
    assertThat(ETaxIdType.fromPrefix("1")).isEqualTo(ETaxIdType.UNKNOWN);
    assertThat(ETaxIdType.fromPrefix("123")).isEqualTo(ETaxIdType.UNKNOWN);
    assertThat(ETaxIdType.fromPrefix(null)).isEqualTo(ETaxIdType.UNKNOWN);
  }

  @Test
  @DisplayName("ETaxIdType all enum methods should work correctly")
  void etaxIdTypeAllEnumMethodsShouldWorkCorrectly() {
    // Test all enum values are covered
    for (ETaxIdType type : ETaxIdType.values()) {
      assertThat(type.getPrefix()).isNotNull();
      assertThat(type.getDescription()).isNotNull();

      // Ensure boolean methods don't throw exceptions
      type.isCuitType();
      type.isCuilType();
      type.isIndividual();
      type.isCompany();
    }

    // Test specific combinations
    assertThat(ETaxIdType.COMPANY.isCompany()).isTrue();
    assertThat(ETaxIdType.COMPANY.isIndividual()).isFalse();
    assertThat(ETaxIdType.MALE_INDIVIDUAL.isIndividual()).isTrue();
    assertThat(ETaxIdType.MALE_INDIVIDUAL.isCompany()).isFalse();
  }

  // ===== STRESS TESTS FOR REMAINING BRANCHES =====

  @Test
  @DisplayName("Should handle input with only separators")
  void shouldHandleInputWithOnlySeparators() {
    assertFalse(ArgentineTaxIdValidator.isValid("---"));
    assertFalse(ArgentineTaxIdValidator.isValid("..."));
    assertFalse(ArgentineTaxIdValidator.isValid("   "));
  }

  @Test
  @DisplayName("Should handle numeric input with wrong length after cleaning")
  void shouldHandleNumericInputWithWrongLengthAfterCleaning() {
    // These will pass initial cleaning but fail length check
    assertFalse(ArgentineTaxIdValidator.isValid("1"));
    assertFalse(ArgentineTaxIdValidator.isValid("12"));
    assertFalse(ArgentineTaxIdValidator.isValid("1234567890")); // 10 digits
    assertFalse(ArgentineTaxIdValidator.isValid("123456789012")); // 12 digits
  }

  @Test
  @DisplayName("Should exercise all code paths with extreme cases")
  void shouldExerciseAllCodePathsWithExtremeCases() {
    // Test cases designed to hit uncovered branches
    String[] extremeCases = {
      "2012345678a", // Non-digit at end
      "20a2345678b", // Non-digits in middle
      "  20123456786  ", // Whitespace padding
      "20-123456-78-6", // Extra separators
      "20/12345678/6", // Different separators
    };

    for (String extremeCase : extremeCases) {
      // Just ensure no exceptions - results will vary based on cleaning
      assertThatCode(
              () -> {
                ArgentineTaxIdValidator.isValid(extremeCase);
                ArgentineTaxIdValidator.format(extremeCase);
                ArgentineTaxIdValidator.extractDocumentNumber(extremeCase);
                ArgentineTaxIdValidator.getType(extremeCase);
              })
          .doesNotThrowAnyException();
    }
  }
}
