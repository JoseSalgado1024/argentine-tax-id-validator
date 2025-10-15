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

/** Enumeration of CUIT/CUIL types based on the two-digit prefix. */
public enum ETaxIdType {
  MALE_INDIVIDUAL("20", "Male Individual", false, true),
  FEMALE_INDIVIDUAL("27", "Female Individual", false, true),
  COMPANY("30", "Company/Corporation", true, false),
  FOREIGN_INDIVIDUAL("23", "Foreign Individual", false, true),
  FOREIGN_COMPANY("33", "Foreign Company", true, false),
  PUBLIC_ENTITY("34", "Public Entity", true, false),
  MALE_INDIVIDUAL_ALT("24", "Male Individual (Alternative)", false, true),
  UNKNOWN("00", "Unknown", false, false);

  private final String prefix;
  private final String description;
  private final boolean validForCuit;
  private final boolean validForCuil;

  ETaxIdType(String prefix, String description, boolean validForCuit, boolean validForCuil) {
    this.prefix = prefix;
    this.description = description;
    this.validForCuit = validForCuit;
    this.validForCuil = validForCuil;
  }

  public String getPrefix() {
    return prefix;
  }

  public String getDescription() {
    return description;
  }

  public boolean isCuitType() {
    return validForCuit;
  }

  public boolean isCuilType() {
    return validForCuil;
  }

  public boolean isIndividual() {
    return this == MALE_INDIVIDUAL
        || this == FEMALE_INDIVIDUAL
        || this == FOREIGN_INDIVIDUAL
        || this == MALE_INDIVIDUAL_ALT;
  }

  public boolean isCompany() {
    return this == COMPANY || this == FOREIGN_COMPANY || this == PUBLIC_ENTITY;
  }

  /** Gets the type from a two-digit prefix. */
  public static ETaxIdType fromPrefix(String prefix) {
    if (prefix == null || prefix.length() != 2) {
      return UNKNOWN;
    }

    for (ETaxIdType type : values()) {
      if (type.prefix.equals(prefix)) {
        return type;
      }
    }

    return UNKNOWN;
  }
}
