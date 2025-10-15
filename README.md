# Argentine Tax ID Validator

A Java library for validating Argentine CUIT and CUIL tax identifiers.

[![Coverage](https://img.shields.io/badge/coverage-81%25-green.svg)](target/site/jacoco/index.html)
[![Java](https://img.shields.io/badge/java-17+-blue.svg)](https://openjdk.org/projects/jdk/17/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Features

- Validates CUIT (Código Único de Identificación Tributaria) 
- Validates CUIL (Código Único de Identificación Laboral)
- Zero runtime dependencies for core functionality
- Thread-safe implementation
- Bean Validation annotations included
- Compatible with Micronaut, Spring Boot, and plain Java

## Installation

### Maven

```xml
<dependency>
    <groupId>io.github.josesalgado1024</groupId>
    <artifactId>argentine-tax-id-validator</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```gradle
implementation 'io.github.josesalgado1024:argentine-tax-id-validator:1.0.0'
```

## Usage

### Basic Validation

```java
import io.jsalgado.validator.ArgentineTaxIdValidator;

// General validation
boolean isValid = ArgentineTaxIdValidator.isValid("20-12345678-6");

// Specific validation
boolean isCuit = ArgentineTaxIdValidator.isValidCuit("30-12345678-1");
boolean isCuil = ArgentineTaxIdValidator.isValidCuil("20-12345678-6");

// Format and utilities
String formatted = ArgentineTaxIdValidator.format("20123456786");
String document = ArgentineTaxIdValidator.extractDocumentNumber("20-12345678-6");
ETaxIdType type = ArgentineTaxIdValidator.getType("20-12345678-6");
```

### Bean Validation

```java
import io.jsalgado.validator.annotations.ValidCuit;
import io.jsalgado.validator.annotations.ValidCuil;

public class TaxPayerDto {
    @ValidCuit
    private String cuit;
    
    @ValidCuil 
    private String cuil;
}
```

## Supported Formats

- Formatted: `20-12345678-6`
- Unformatted: `20123456786`
- With dots: `20.12345678.6`
- With spaces: `20 12345678 6`

## Algorithm

Implements the official Module 11 algorithm as specified by AFIP (Administración Federal de Ingresos Públicos).

## Requirements

- Java 17 or higher
- Maven 3.6.3 or higher (for building from source)

## Building from Source

```bash
git clone https://github.com/JoseSalgado1024/argentine-tax-id-validator.git
cd argentine-tax-id-validator
mvn clean package
```

## Testing

Run tests with coverage report:

```bash
mvn clean test jacoco:report
```

View coverage report: `target/site/jacoco/index.html`

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

José Salgado - [GitHub](https://github.com/JoseSalgado1024)
