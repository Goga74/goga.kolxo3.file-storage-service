package goga.kolxo3.filestorage.controller;

import goga.kolxo3.filestorage.model.FileMetadata;
import goga.kolxo3.filestorage.service.FileStorageService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;

import java.util.List;

@Controller("/files")
public class FileController {
    private final FileStorageService storageService;

    @Inject
    public FileController(FileStorageService storageService) {
        this.storageService = storageService;
    }

    @Post(consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<String> uploadFile(@Part("file") byte[] file, 
                                         @Header("Content-Disposition") String contentDisposition) {
        String originalName = extractFileName(contentDisposition);
        FileMetadata metadata = storageService.storeFile(file, originalName);
        return HttpResponse.created(metadata.id());
    }

    @Get("/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public byte[] getFile(@PathVariable String id) {
        return storageService.getFileContent(id);
    }

    @Get("/search")
    public List<FileMetadata> searchFiles(@QueryValue String query) {
        return storageService.searchFiles(query);
    }

    private String extractFileName(String contentDisposition) {
        // Извлечение имени файла из заголовка
        return "filename";
    }
}