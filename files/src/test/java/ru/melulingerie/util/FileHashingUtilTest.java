package ru.melulingerie.util;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileHashingUtilTest {

    /**
     * Проверяет, что для известного содержимого вычисляется правильный и ожидаемый хеш SHA-256.
     */
    @Test
    void calculateSHA256_ShouldReturnCorrectHash_ForGivenContent() throws IOException, NoSuchAlgorithmException {
        // Given
        String content = "MeluLingerieTestContent";
        String expectedHash = "3728004d83e88748fad20ac8e4d1fce05068627839185aa9b051464c028218ed";
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", content.getBytes());

        // When
        String actualHash = FileHashingUtil.calculateSHA256(file);

        // Then
        assertThat(actualHash).isEqualTo(expectedHash);
    }

    /**
     * Проверяет, что для разного содержимого файлов генерируются разные хеши.
     */
    @Test
    void calculateSHA256_ShouldReturnDifferentHashes_ForDifferentContent() throws IOException, NoSuchAlgorithmException {
        // Given
        MockMultipartFile file1 = new MockMultipartFile("file1", "test1.txt", "text/plain", "content1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("file2", "test2.txt", "text/plain", "content2".getBytes());

        // When
        String hash1 = FileHashingUtil.calculateSHA256(file1);
        String hash2 = FileHashingUtil.calculateSHA256(file2);

        // Then
        assertThat(hash1).isNotEqualTo(hash2);
    }

    /**
     * Проверяет, что для пустого файла вычисляется корректный хеш.
     */
    @Test
    void calculateSHA256_ShouldHandleEmptyFile() throws IOException, NoSuchAlgorithmException {
        // Given
        String expectedHashForEmpty = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);

        // When
        String actualHash = FileHashingUtil.calculateSHA256(emptyFile);

        // Then
        assertThat(actualHash).isEqualTo(expectedHashForEmpty);
    }

    /**
     * Проверяет, что при ошибке чтения из потока файла пробрасывается IOException.
     */
    @Test
    void calculateSHA256_ShouldThrowIOException_WhenInputStreamFails() throws IOException {
        // Given
        InputStream errorInputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Simulated stream read error");
            }
        };

        MockMultipartFile mockFile = mock(MockMultipartFile.class);
        when(mockFile.getInputStream()).thenReturn(errorInputStream);

        // When & Then
        assertThatThrownBy(() -> FileHashingUtil.calculateSHA256(mockFile))
                .isInstanceOf(IOException.class)
                .hasMessage("Simulated stream read error");
    }

    /**
     * Проверяет, что утилитарный класс имеет приватный конструктор,
     * чтобы предотвратить его инстанцирование.
     */
    @Test
    void utilityClass_ShouldNotBeInstantiable() {
        assertThatThrownBy(() -> {
            Constructor<FileHashingUtil> constructor = FileHashingUtil.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }).isInstanceOf(InvocationTargetException.class)
                .hasCauseInstanceOf(UnsupportedOperationException.class);
    }
}