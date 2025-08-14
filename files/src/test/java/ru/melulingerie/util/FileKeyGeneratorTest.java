package ru.melulingerie.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileKeyGeneratorTest {

    private FileKeyGenerator fileKeyGenerator;

    @BeforeEach
    void setUp() {
        fileKeyGenerator = new FileKeyGenerator();
    }

    /**
     * Проверяет, что при каждом вызове генерируется уникальный ключ.
     */
    @Test
    void generate_ShouldReturnUniqueKey_ForEachCall() {
        // When
        String key1 = fileKeyGenerator.generate("test.txt");
        String key2 = fileKeyGenerator.generate("test.txt");

        // Then
        assertThat(key1).isNotEqualTo(key2);
    }

    /**
     * Проверяет, что сгенерированный ключ содержит очищенное имя файла и его расширение.
     */
    @Test
    void generate_ShouldContainOriginalFileNameAndExtension() {
        // When
        String generatedKey = fileKeyGenerator.generate("my-document.pdf");

        // Then
        assertThat(generatedKey).startsWith("my-document_");
        assertThat(generatedKey).endsWith(".pdf");
    }

    /**
     * Проверяет, что спецсимволы в имени файла корректно заменяются на подчеркивания.
     */
    @Test
    void generate_ShouldSanitizeSpecialCharacters() {
        // When
        String generatedKey = fileKeyGenerator.generate("file with spaces/and&slashes.jpg");

        // Then
        assertThat(generatedKey).startsWith("file_with_spaces_and_slashes_");
        assertThat(generatedKey).doesNotContain(" ", "/", "&");
    }

    /**
     * Проверяет корректную обработку имени файла без расширения.
     */
    @Test
    void generate_ShouldHandleFileName_WithoutExtension() {
        // When
        String generatedKey = fileKeyGenerator.generate("report");

        // Then
        assertThat(generatedKey).startsWith("report_");
        assertThat(generatedKey).doesNotContain(".");
    }

    /**
     * Проверяет, что при null или пустом имени файла используется имя по умолчанию "file".
     */
    @Test
    void generate_ShouldHandleNullOrEmpty_OriginalName() {
        // When
        String keyForNull = fileKeyGenerator.generate(null);
        String keyForEmpty = fileKeyGenerator.generate("");

        // Then
        assertThat(keyForNull).startsWith("file_");
        assertThat(keyForEmpty).startsWith("file_");
    }

    /**
     * Проверяет, что сгенерированный ключ имеет правильную структуру,
     * включая временную метку и UUID.
     */
    @Test
    void generate_ShouldMatchExpectedFormat() {
        // When
        String generatedKey = fileKeyGenerator.generate("image.jpeg");

        // Then
        // Regex to check for: baseName_YYYYMMDD_HHmmss_SSS_shortUUID.extension
        assertThat(generatedKey).matches("image_\\d{8}_\\d{6}_\\d{3}_[a-f0-9]{8}\\.jpeg");
    }
}