package com.example.filestorage.service;

import com.example.filestorage.model.FileMetadata;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class FileStorageService {
    public FileMetadata storeFile(byte[] content, String originalName) {
        // Логика сохранения файла
        return new FileMetadata("generated-id", originalName, "/path/to/file");
    }

    public byte[] getFileContent(String id) {
        // Логика получения содержимого
        return new byte[0];
    }

    public List<FileMetadata> searchFiles(String query) {
        // Логика поиска
        return List.of();
    }
}