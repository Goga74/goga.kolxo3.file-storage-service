package goga.kolxo3.filestorage.service;

import goga.kolxo3.filestorage.model.FileMetadata;
import java.util.List;

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