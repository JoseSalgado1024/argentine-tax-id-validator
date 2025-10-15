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

import io.jsalgado.validator.annotations.ValidCuit;
import io.jsalgado.validator.annotations.ValidCuil;
import io.jsalgado.validator.annotations.ValidCuitOrCuil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for Bean Validation annotations
 */
@DisplayName("Bean Validation Tests")
class BeanValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // Test DTO classes
    static class TestCuitDto {
        @ValidCuit
        private String cuit;

        public TestCuitDto(String cuit) {
            this.cuit = cuit;
        }

        public String getCuit() {
            return cuit;
        }
    }

    static class TestCuilDto {
        @ValidCuil
        private String cuil;

        public TestCuilDto(String cuil) {
            this.cuil = cuil;
        }

        public String getCuil() {
            return cuil;
        }
    }

    // ===== @ValidCuit TESTS =====

    @ParameterizedTest(name = "@ValidCuit should accept valid CUIT: {0}")
    @ValueSource(strings = {
        "30-12345678-1",    // CUIT Empresa
        "33-12345678-0",    // CUIT Empresa Extranjera
        "34-12345678-7"     // Public entity CUIT
    })
    void validCuitAnnotationShouldAcceptValidCuit(String validCuit) {
        TestCuitDto dto = new TestCuitDto(validCuit);
        
        Set<ConstraintViolation<TestCuitDto>> violations = validator.validate(dto);
        
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest(name = "@ValidCuit should reject invalid CUIT: {0}")
    @ValueSource(strings = {
        "30-12345678-9",    // Wrong check digit
        "20-12345678-6",    // Individual (not valid as CUIT)
        "27-12345678-0",    // Individual female (not valid as CUIT)
        "99-12345678-1",    // Invalid prefix
        "invalid",          // Non-numeric
        ""                  // Empty
    })
    void validCuitAnnotationShouldRejectInvalidCuit(String invalidCuit) {
        TestCuitDto dto = new TestCuitDto(invalidCuit);
        
        Set<ConstraintViolation<TestCuitDto>> violations = validator.validate(dto);
        
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Invalid Argentine CUIT");
    }

    @Test
    @DisplayName("@ValidCuit should accept null")
    void validCuitAnnotationShouldAcceptNull() {
        TestCuitDto dto = new TestCuitDto(null);
        
        Set<ConstraintViolation<TestCuitDto>> violations = validator.validate(dto);
        
        assertThat(violations).isEmpty();
    }

    // ===== @ValidCuil TESTS =====

    @ParameterizedTest(name = "@ValidCuil should accept valid CUIL: {0}")
    @ValueSource(strings = {
        "20-12345678-6",    // CUIL Masculino
        "27-12345678-0",    // CUIL Femenino
        "23-12345678-5"     // CUIL Extranjero
    })
    void validCuilAnnotationShouldAcceptValidCuil(String validCuil) {
        TestCuilDto dto = new TestCuilDto(validCuil);
        
        Set<ConstraintViolation<TestCuilDto>> violations = validator.validate(dto);
        
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest(name = "@ValidCuil should reject invalid CUIL: {0}")
    @ValueSource(strings = {
        "20-12345678-9",    // Wrong check digit
        "30-12345678-1",    // Company CUIT (not valid as CUIL)
        "99-12345678-6",    // Invalid prefix
        "invalid",          // Non-numeric
        ""                  // Empty
    })
    void validCuilAnnotationShouldRejectInvalidCuil(String invalidCuil) {
        TestCuilDto dto = new TestCuilDto(invalidCuil);
        
        Set<ConstraintViolation<TestCuilDto>> violations = validator.validate(dto);
        
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Invalid Argentine CUIL");
    }

    @Test
    @DisplayName("@ValidCuil should accept null")
    void validCuilAnnotationShouldAcceptNull() {
        TestCuilDto dto = new TestCuilDto(null);
        
        Set<ConstraintViolation<TestCuilDto>> violations = validator.validate(dto);
        
        assertThat(violations).isEmpty();
    }

    // ===== INTEGRATION TESTS =====

    static class TestBothDto {
        @ValidCuit
        private String cuit;

        @ValidCuil
        private String cuil;

        public TestBothDto(String cuit, String cuil) {
            this.cuit = cuit;
            this.cuil = cuil;
        }
    }

    @Test
    @DisplayName("Should validate both annotations correctly")
    void shouldValidateBothAnnotationsCorrectly() {
        TestBothDto validDto = new TestBothDto("30-12345678-1", "20-12345678-6");
        
        Set<ConstraintViolation<TestBothDto>> violations = validator.validate(validDto);
        
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should reject invalid values in both fields")
    void shouldRejectInvalidValuesInBothFields() {
        TestBothDto invalidDto = new TestBothDto("invalid-cuit", "30-12345678-1"); // Company as CUIL
        
        Set<ConstraintViolation<TestBothDto>> violations = validator.validate(invalidDto);
        
        assertThat(violations).hasSize(2);
    }

    // ===== MANUAL VALIDATOR TESTS =====
    
    @Test
    @DisplayName("Manual test: ValidCuit validator directly")
    void testValidCuitValidatorDirectly() {
        ValidCuit.CuitValidator validator = new ValidCuit.CuitValidator();
        validator.initialize(null);
        
        // Valid cases
        assertThat(validator.isValid("30-12345678-1", null)).isTrue(); // Company CUIT
        assertThat(validator.isValid("33-12345678-0", null)).isTrue(); // Foreign company CUIT
        assertThat(validator.isValid(null, null)).isTrue(); // Null handling
        
        // Invalid cases
        assertThat(validator.isValid("invalid", null)).isFalse();
        assertThat(validator.isValid("", null)).isFalse();
    }

    @Test
    @DisplayName("Manual test: ValidCuil validator directly")
    void testValidCuilValidatorDirectly() {
        ValidCuil.CuilValidator validator = new ValidCuil.CuilValidator();
        validator.initialize(null);
        
        // Valid cases
        assertThat(validator.isValid("20-12345678-6", null)).isTrue(); // Individual CUIL
        assertThat(validator.isValid("27-12345678-0", null)).isTrue(); // Female CUIL
        assertThat(validator.isValid(null, null)).isTrue(); // Null handling
        
        // Invalid cases  
        assertThat(validator.isValid("30-12345678-1", null)).isFalse(); // Company (not CUIL)
        assertThat(validator.isValid("invalid", null)).isFalse();
        assertThat(validator.isValid("", null)).isFalse();
    }

    // ===== @ValidCuitOrCuil TESTS =====

    static class TestCuitOrCuilDto {
        @ValidCuitOrCuil
        private String taxId;

        public TestCuitOrCuilDto(String taxId) {
            this.taxId = taxId;
        }

        public String getTaxId() {
            return taxId;
        }
    }

    @ParameterizedTest(name = "@ValidCuitOrCuil should accept valid tax ID: {0}")
    @ValueSource(strings = {
        "30-12345678-1",    // Valid CUIT
        "20-12345678-6",    // Valid CUIL
        "27-12345678-0",    // Valid CUIL female
        "33-12345678-0",    // Valid foreign company CUIT
        "23-12345678-5",    // Valid foreign individual CUIL
        "34-12345678-7"     // Valid public entity CUIT
    })
    void validCuitOrCuilShouldAcceptAnyValidTaxId(String validTaxId) {
        TestCuitOrCuilDto dto = new TestCuitOrCuilDto(validTaxId);
        
        Set<ConstraintViolation<TestCuitOrCuilDto>> violations = validator.validate(dto);
        
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest(name = "@ValidCuitOrCuil should reject invalid tax ID: {0}")
    @ValueSource(strings = {
        "30-12345678-9",    // Wrong check digit
        "20-12345678-9",    // Wrong check digit
        "99-12345678-6",    // Invalid prefix
        "invalid",          // Non-numeric
        "123456789",        // Too short
        "123456789012",     // Too long
        "",                 // Empty
        "   "               // Whitespace only
    })
    void validCuitOrCuilShouldRejectInvalidTaxId(String invalidTaxId) {
        TestCuitOrCuilDto dto = new TestCuitOrCuilDto(invalidTaxId);
        
        Set<ConstraintViolation<TestCuitOrCuilDto>> violations = validator.validate(dto);
        
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Invalid Argentine CUIT or CUIL");
    }

    @Test
    @DisplayName("@ValidCuitOrCuil should accept null")
    void validCuitOrCuilShouldAcceptNull() {
        TestCuitOrCuilDto dto = new TestCuitOrCuilDto(null);
        
        Set<ConstraintViolation<TestCuitOrCuilDto>> violations = validator.validate(dto);
        
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Manual test: ValidCuitOrCuil validator directly")
    void testValidCuitOrCuilValidatorDirectly() {
        ValidCuitOrCuil.CuitOrCuilValidator validator = new ValidCuitOrCuil.CuitOrCuilValidator();
        validator.initialize(null);
        
        // Valid cases (both CUIT and CUIL)
        assertThat(validator.isValid("30-12345678-1", null)).isTrue(); // Company CUIT
        assertThat(validator.isValid("20-12345678-6", null)).isTrue(); // Individual CUIL
        assertThat(validator.isValid("27-12345678-0", null)).isTrue(); // Female CUIL
        assertThat(validator.isValid(null, null)).isTrue(); // Null handling
        
        // Invalid cases
        assertThat(validator.isValid("invalid", null)).isFalse();
        assertThat(validator.isValid("", null)).isFalse();
        assertThat(validator.isValid("30-12345678-9", null)).isFalse(); // Wrong check digit
    }

    // ===== COMPREHENSIVE INTEGRATION TEST =====

    static class TestAllAnnotationsDto {
        @ValidCuit
        private String cuit;

        @ValidCuil
        private String cuil;

        @ValidCuitOrCuil
        private String anyTaxId;

        public TestAllAnnotationsDto(String cuit, String cuil, String anyTaxId) {
            this.cuit = cuit;
            this.cuil = cuil;
            this.anyTaxId = anyTaxId;
        }
    }

    @Test
    @DisplayName("Should work with all three annotations in same DTO")
    void shouldWorkWithAllThreeAnnotationsInSameDto() {
        TestAllAnnotationsDto dto = new TestAllAnnotationsDto(
            "30-12345678-1",    // Valid CUIT
            "20-12345678-6",    // Valid CUIL  
            "33-12345678-0"     // Valid CUIT (accepted by @ValidCuitOrCuil)
        );
        
        Set<ConstraintViolation<TestAllAnnotationsDto>> violations = validator.validate(dto);
        
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should properly reject mixed invalid values")
    void shouldProperlyRejectMixedInvalidValues() {
        TestAllAnnotationsDto dto = new TestAllAnnotationsDto(
            "20-12345678-6",    // CUIL in CUIT field (should fail)
            "30-12345678-1",    // CUIT in CUIL field (should fail)
            "invalid"           // Invalid in general field (should fail)
        );
        
        Set<ConstraintViolation<TestAllAnnotationsDto>> violations = validator.validate(dto);
        
        assertThat(violations).hasSize(3);
    }
}
