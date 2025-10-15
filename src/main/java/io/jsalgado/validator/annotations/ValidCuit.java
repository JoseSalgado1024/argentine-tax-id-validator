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
package io.jsalgado.validator.annotations;

import io.jsalgado.validator.ArgentineTaxIdValidator;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Bean Validation annotation for Argentine CUIT validation.
 *
 * @author José Salgado
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCuit.CuitValidator.class)
@Documented
public @interface ValidCuit {

  String message() default "Invalid Argentine CUIT";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  /** CUIT Validator implementation */
  class CuitValidator implements ConstraintValidator<ValidCuit, String> {

    @Override
    public void initialize(ValidCuit constraintAnnotation) {
      // No initialization required
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
      if (value == null) {
        return true; // Let @NotNull handle null validation
      }

      return ArgentineTaxIdValidator.isValidCuit(value);
    }
  }
}
