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

import java.util.regex.Pattern;

/**
 * High-performance validator for Argentine CUIT and CUIL identifiers.
 *
 * <p>This class provides thread-safe, zero-dependency validation for:
 *
 * <ul>
 *   <li>CUIT (Código Único de Identificación Tributaria)
 *   <li>CUIL (Código Único de Identificación Laboral)
 * </ul>
 *
 * <p>The validation uses the standard Module 11 algorithm as specified by AFIP (Administración
 * Federal de Ingresos Públicos).
 *
 * @author José Salgado
 * @version 1.0.0
 */
public final class ArgentineTaxIdValidator {

  private static final Pattern DIGITS_ONLY = Pattern.compile("\\d{11}");
  private static final int[] WEIGHTS = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
  private static final int IDENTIFIER_LENGTH = 11;

  // Private constructor to prevent instantiation
  private ArgentineTaxIdValidator() {
    throw new UnsupportedOperationException("Utility class");
  }

  /**
   * Validates a CUIT or CUIL identifier.
   *
   * @param identifier the CUIT or CUIL to validate, may be null
   * @return true if the identifier is valid, false otherwise
   */
  public static boolean isValid(String identifier) {
    if (identifier == null) {
      return false;
    }

    String clean = cleanInput(identifier);

    if (!isValidFormat(clean)) {
      return false;
    }

    return isValidCheckDigit(clean);
  }

  /** Validates specifically a CUIT identifier. */
  public static boolean isValidCuit(String cuit) {
    if (!isValid(cuit)) {
      return false;
    }

    String clean = cleanInput(cuit);
    return ETaxIdType.fromPrefix(clean.substring(0, 2)).isCuitType();
  }

  /** Validates specifically a CUIL identifier. */
  public static boolean isValidCuil(String cuil) {
    if (!isValid(cuil)) {
      return false;
    }

    String clean = cleanInput(cuil);
    return ETaxIdType.fromPrefix(clean.substring(0, 2)).isCuilType();
  }

  /** Formats a CUIT/CUIL with standard hyphen notation. */
  public static String format(String identifier) {
    if (!isValid(identifier)) {
      return null;
    }

    String clean = cleanInput(identifier);
    return String.format(
        "%s-%s-%s", clean.substring(0, 2), clean.substring(2, 10), clean.substring(10, 11));
  }

  /** Extracts the document number from a valid CUIT/CUIL. */
  public static String extractDocumentNumber(String identifier) {
    if (!isValid(identifier)) {
      return null;
    }

    String clean = cleanInput(identifier);
    return clean.substring(2, 10);
  }

  /** Gets the type information for a valid identifier. */
  public static ETaxIdType getType(String identifier) {
    if (!isValid(identifier)) {
      return ETaxIdType.UNKNOWN;
    }

    String clean = cleanInput(identifier);
    return ETaxIdType.fromPrefix(clean.substring(0, 2));
  }

  // ===== PRIVATE HELPER METHODS =====

  private static String cleanInput(String identifier) {
    return identifier.replaceAll("[^\\d]", "");
  }

  private static boolean isValidFormat(String clean) {
    return clean.length() == IDENTIFIER_LENGTH && DIGITS_ONLY.matcher(clean).matches();
  }

  private static boolean isValidCheckDigit(String identifier) {
    // ⚠️ SAFETY CHECK: Ya validamos longitud, pero double-check
    if (identifier.length() != IDENTIFIER_LENGTH) {
      return false;
    }

    int sum = 0;

    // Calculate weighted sum of first 10 digits
    for (int i = 0; i < WEIGHTS.length; i++) {
      sum += Character.getNumericValue(identifier.charAt(i)) * WEIGHTS[i];
    }

    // Calculate expected check digit using Module 11
    int remainder = sum % 11;
    int expectedDigit;

    if (remainder < 2) {
      expectedDigit = remainder;
    } else {
      expectedDigit = 11 - remainder;
    }

    // Compare with actual check digit
    int actualDigit = Character.getNumericValue(identifier.charAt(10));

    return expectedDigit == actualDigit;
  }
}
