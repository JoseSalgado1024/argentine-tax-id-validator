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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Comprehensive unit tests for Argentine Tax ID Validator */
@DisplayName("🇦🇷 Argentine Tax ID Validator Tests")
class ArgentineTaxIdValidatorTest {

  @Nested
  @DisplayName("✅ Valid CUIT/CUIL Cases")
  class ValidCasesTest {

    @ParameterizedTest(name = "Valid: {0}")
    @ValueSource(
        strings = {
          "20-12345678-6", // CUIL Masculino válido
          "27-12345678-0", // CUIL Femenino válido
          "30-12345678-1", // CUIT Empresa válido
          "23-12345678-5", // CUIL Extranjero válido
          "33-12345678-0", // CUIT Empresa Extranjera válido
          "34-12345678-7" // CUIT Entidad Pública válido
        })
    void shouldValidateCorrectIdentifiers(String validIdentifier) {
      assertTrue(ArgentineTaxIdValidator.isValid(validIdentifier));
    }

    @Test
    @DisplayName("Should validate unformatted identifiers")
    void shouldValidateUnformattedIdentifiers() {
      assertTrue(ArgentineTaxIdValidator.isValid("20123456786"));
      assertTrue(ArgentineTaxIdValidator.isValid("30123456781"));
    }
  }

  @Nested
  @DisplayName("❌ Invalid CUIT/CUIL Cases")
  class InvalidCasesTest {

    @ParameterizedTest(name = "Invalid: {0}")
    @ValueSource(
        strings = {
          "20-12345678-7", // Dígito verificador incorrecto (debería ser 6)
          "30-12345678-8", // Dígito verificador incorrecto (debería ser 1)
          "99-12345678-6", // Prefijo inválido
          "abcdefghijk", // No numérico
          "123456789", // Muy corto
          "123456789012", // Muy largo
          "", // Vacío
          "   " // Solo espacios
        })
    void shouldRejectInvalidIdentifiers(String invalidIdentifier) {
      assertFalse(ArgentineTaxIdValidator.isValid(invalidIdentifier));
    }

    @Test
    @DisplayName("Should reject null input")
    void shouldRejectNullInput() {
      assertFalse(ArgentineTaxIdValidator.isValid(null));
    }
  }

  @Nested
  @DisplayName("🎯 Specific Type Validation")
  class TypeValidationTest {

    @ParameterizedTest(name = "Valid CUIT: {0}")
    @ValueSource(
        strings = {
          "30-12345678-1", // CUIT Empresa
          "33-12345678-0", // CUIT Empresa Extranjera
          "34-12345678-7" // CUIT Entidad Pública
        })
    void shouldValidateSpecificCuit(String cuit) {
      assertTrue(ArgentineTaxIdValidator.isValidCuit(cuit));
    }

    @ParameterizedTest(name = "Valid CUIL: {0}")
    @ValueSource(
        strings = {
          "20-12345678-6", // CUIL Masculino
          "27-12345678-0", // CUIL Femenino
          "23-12345678-5" // CUIL Extranjero
        })
    void shouldValidateSpecificCuil(String cuil) {
      assertTrue(ArgentineTaxIdValidator.isValidCuil(cuil));
    }
  }

  @Nested
  @DisplayName("🔧 Utility Methods Tests")
  class UtilityMethodsTest {

    @Test
    @DisplayName("Should format valid identifiers")
    void shouldFormatValidIdentifiers() {
      assertThat(ArgentineTaxIdValidator.format("20123456786")).isEqualTo("20-12345678-6");
    }

    @Test
    @DisplayName("Should extract document number")
    void shouldExtractDocumentNumber() {
      assertThat(ArgentineTaxIdValidator.extractDocumentNumber("20-12345678-6"))
          .isEqualTo("12345678");
    }

    @Test
    @DisplayName("Should get correct type")
    void shouldGetCorrectType() {
      assertThat(ArgentineTaxIdValidator.getType("20-12345678-6"))
          .isEqualTo(ETaxIdType.MALE_INDIVIDUAL);

      assertThat(ArgentineTaxIdValidator.getType("30-12345678-1")).isEqualTo(ETaxIdType.COMPANY);
    }
  }

  @Nested
  @DisplayName("⚡ Performance Tests")
  class PerformanceTest {

    @Test
    @DisplayName("Should be fast")
    void shouldBeFast() {
      String validCuit = "20-12345678-6";

      long start = System.nanoTime();

      for (int i = 0; i < 10_000; i++) {
        ArgentineTaxIdValidator.isValid(validCuit);
      }

      long duration = (System.nanoTime() - start) / 1_000_000;
      assertThat(duration).isLessThan(500); // Less than 500ms for CI/CD environments
    }
  }
}
