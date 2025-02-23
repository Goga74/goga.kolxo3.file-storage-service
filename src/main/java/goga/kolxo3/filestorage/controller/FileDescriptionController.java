package goga.kolxo3.filestorage.controller;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileDescriptionController extends AbstractVerticle {
    private String uploadDir;
    private int httpPort;
    private String charset;

    @Override
    public void start(Promise<Void> startPromise) {
        ConfigRetriever retriever = ConfigRetriever.create(vertx,
                new ConfigRetrieverOptions()
                        .addStore(new ConfigStoreOptions()
                                .setType("file")
                                .setConfig(new JsonObject().put("path", "config.json"))));

        retriever.getConfig(configResult -> {
            if (configResult.succeeded()) {
                var config = configResult.result();
                uploadDir = config.getString("uploadDir", "F:\\Temp\\file-storage");
                httpPort = config.getInteger("httpPort", 8082);
                charset = config.getString("charset", "UTF-8"); // Получаем значение charset

                Router router = Router.router(vertx);
                router.get("/desc").handler(this::handleGetDescription);

                vertx.createHttpServer().requestHandler(router).listen(httpPort, http -> {
                    if (http.succeeded()) {
                        startPromise.complete();
                        System.out.println("HTTP server started on port " + httpPort);
                    } else {
                        startPromise.fail(http.cause());
                    }
                });
            } else {
                startPromise.fail(configResult.cause());
            }
        });
    }

    private void handleGetDescription(RoutingContext context) {
        String id = context.request().getParam("id");

        if (id == null) {
            context.response()
                    .putHeader("Content-Type", "application/json; charset=" + charset)
                    .setStatusCode(400)
                    .end(new JsonObject().put("error", "Необходим параметр id").encodePrettily());
            return;
        }

        // Формируем путь к папке по ID
        String folderPath = uploadDir + File.separator + id;
        File folder = new File(folderPath);

        // Проверяем существование папки
        if (!folder.exists() || !folder.isDirectory()) {
            context.response()
                    .putHeader("Content-Type", "application/json; charset=" + charset)
                    .setStatusCode(404)
                    .end(new JsonObject().put("error", "Папка не найдена для данного id").encodePrettily());
            return;
        }

        // Формируем путь к файлу Meta.json
        String metaFilePath = folderPath + File.separator + "Meta.json";
        File metaFile = new File(metaFilePath);

        // Проверяем существование файла Meta.json
        if (!metaFile.exists()) {
            context.response()
                    .putHeader("Content-Type", "application/json; charset=" + charset)
                    .setStatusCode(404)
                    .end(new JsonObject().put("error", "Файл Meta.json не найден для данного id").encodePrettily());
            return;
        }

        try {
            // Чтение содержимого Meta.json
            String content = new String(Files.readAllBytes(Paths.get(metaFilePath)));
            JsonObject metaJson = new JsonObject(content);

            // Возврат значения тегов
            context.response()
                    .putHeader("Content-Type", "application/json; charset=" + charset)
                    .setStatusCode(200)
                    .end(metaJson.encodePrettily());
        } catch (Exception e) {
            context.response()
                    .putHeader("Content-Type", "application/json; charset=" + charset)
                    .setStatusCode(500)
                    .end(new JsonObject().put("error", "Ошибка при чтении файла Meta.json").encodePrettily());
        }
    }
}
