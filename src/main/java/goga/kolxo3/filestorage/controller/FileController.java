package goga.kolxo3.filestorage.controller;

import com.fasterxml.jackson.databind.JsonNode;
import goga.kolxo3.filestorage.model.FileMetadata;
import goga.kolxo3.filestorage.service.FileStorageService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.CompletedFileUpload;
import jakarta.inject.Inject;

import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Body;
import java.util.List;

@Controller("/files")
public class FileController
{
    private final FileStorageService storageService;

    @Inject
    public FileController(FileStorageService storageService)
    {
        this.storageService = storageService;
    }

    @Post(consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<String> upload(
            @Part("file") CompletedFileUpload file,
            @Part("metadata") String metadata) {

        // Обработка файла

        // Если metadata — это строка в формате JSON:
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonMetadata = mapper.readTree(metadata);

            return HttpResponse.ok("Файл загружен успешно");

        } catch (Exception e) {
            return HttpResponse.badRequest("Ошибка при обработке метаданных");
        }
    }

    @ExecuteOn(TaskExecutors.IO)
    @Post(value = "/{param1}/{param2}", consumes = MediaType.MULTIPART_FORM_DATA,
            produces = MediaType.TEXT_PLAIN)
    public HttpResponse<String> post(CompletedFileUpload file,
                                     String param1, String param2)
    {
        System.out.println("Param1: " + param1 + " Param2: " + param2 + " File name: " + file.getFilename() + " Size: " + file.getSize() + " Type: " + file.getContentType().get());
        return HttpResponse.ok("Uploaded!");
    }

    /*
    @Post(consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<String> uploadFile(
       @Part("file") byte[] file,
       @Part("fileName") String fileName,
       @Part("tags") String tags,
       @Header("Content-Disposition") String contentDisposition)
    {
        if (fileName == null || fileName.isEmpty())
        {
            fileName = extractFileName(contentDisposition);
        }
        //String originalName = extractFileName(contentDisposition);
        FileMetadata metadata = storageService.storeFile(file, fileName);
        return HttpResponse.created(metadata.id());
    }
     */

    @Get("/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public byte[] getFile(@PathVariable String id)
    {
        return storageService.getFileContent(id);
    }

    @Get("/search")
    public List<FileMetadata> searchFiles(@QueryValue String query)
    {
        return storageService.searchFiles(query);
    }

    private String extractFileName(String contentDisposition)
    {
        return "filename";
    }
}