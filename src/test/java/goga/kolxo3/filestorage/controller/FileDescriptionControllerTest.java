package goga.kolxo3.filestorage.controller;
/*
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class FileDescriptionControllerTest {

    private Vertx vertx;
    private String uploadDir;

    @BeforeEach
    public void setUp(VertxTestContext testContext) {
        vertx = Vertx.vertx();
        uploadDir = "F:\\Temp\\file-storage"; // Укажите путь к директории загрузки для тестирования

        // Создаем директорию для тестирования
        new File(uploadDir).mkdirs();

        // Разворачиваем контроллер
        vertx.deployVerticle(new FileDescriptionController(), testContext.succeeding(id -> testContext.completeNow()));
    }

    @Test
    public void testGetDescription(VertxTestContext testContext) throws Exception {
        String id = "test-folder";
        String folderPath = uploadDir + File.separator + id;

        // Создаем тестовую папку и файл Meta.json
        new File(folderPath).mkdirs();
        try (FileWriter writer = new FileWriter(folderPath + File.separator + "Meta.json")) {
            writer.write("{\"tags\": \"tag1, tag2, tag3\"}");
        }

        // Выполняем запрос к контроллеру
        Router router = Router.router(vertx);
        router.get("/desc").handler(this::handleGetDescription);

        vertx.createHttpServer().requestHandler(router).listen(8082, http -> {
            if (http.succeeded()) {
                vertx.createHttpClient().getNow(8082, "localhost", "/desc?id=" + id, response -> {
                    response.bodyHandler(body -> {
                        assertEquals(200, response.statusCode());
                        assertEquals("{\"tags\":\"tag1, tag2, tag3\"}", body.toString());
                        testContext.completeNow();
                    });
                });
            }
        });
    }

    private void handleGetDescription(RoutingContext context) {
        String id = context.request().getParam("id");

        if (id == null) {
            context.response()
                    .putHeader("Content-Type", "application/json; charset=UTF-8")
                    .setStatusCode(400)
                    .end("{\"error\": \"Необходим параметр id\"}");
            return;
        }

        String folderPath = uploadDir + File.separator + id;
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            context.response()
                    .putHeader("Content-Type", "application/json; charset=UTF-8")
                    .setStatusCode(404)
                    .end("{\"error\": \"Папка не найдена для данного id\"}");
            return;
        }

        String metaFilePath = folderPath + File.separator + "Meta.json";
        File metaFile = new File(metaFilePath);

        if (!metaFile.exists()) {
            context.response()
                    .putHeader("Content-Type", "application/json; charset=UTF-8")
                    .setStatusCode(404)
                    .end("{\"error\": \"Файл Meta.json не найден для данного id\"}");
            return;
        }

        try {
            String content = new String(Files.readAllBytes(metaFile.toPath()));
            context.response()
                    .putHeader("Content-Type", "application/json; charset=UTF-8")
                    .setStatusCode(200)
                    .end(content);
        } catch (Exception e) {
            context.response()
                    .putHeader("Content-Type", "application/json; charset=UTF-8")
                    .setStatusCode(500)
                    .end("{\"error\": \"Ошибка при чтении файла Meta.json\"}");
        }
    }
}
*/