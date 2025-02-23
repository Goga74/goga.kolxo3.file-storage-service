package goga.kolxo3.filestorage.controller;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class FileUploadController extends AbstractVerticle {
    private String uploadDir;
    private int httpPort;

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
                uploadDir = config.getString("uploadDir", "F:\\Temp\\file-storage"); // Значение по умолчанию
                httpPort = config.getInteger("httpPort", 8082); // Значение по умолчанию

                Router router = Router.router(vertx);
                router.route("/upload").handler(BodyHandler.create().setUploadsDirectory(uploadDir));
                router.post("/upload").handler(this::handleFileUpload);

                vertx.createHttpServer().requestHandler(router).listen(httpPort, http ->
                {
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

    private void handleFileUpload(RoutingContext context) {
        String filename = context.request().getParam("filename");
        String tags = context.request().getParam("tags");

        if (filename == null || tags == null) {
            context.response()
                    .putHeader("Content-Type", "application/json; charset=UTF-8")
                    .setStatusCode(400)
                    .end("Необходимы параметры filename и tags");
            return;
        }

        String uniqueId = UUID.randomUUID().toString();
        String dirPath = uploadDir + File.separator + uniqueId;

        // Создание уникальной директории
        File uploadDir = new File(dirPath);
        if (!uploadDir.mkdirs()) {
            context.response()
                    .putHeader("Content-Type", "application/json; charset=UTF-8")
                    .setStatusCode(500)
                    .end("Не удалось создать директорию для загрузки");
            return;
        }

        for (io.vertx.ext.web.FileUpload file : context.fileUploads()) {
            System.out.println("Received file: " + file.fileName());

            // Перемещение файла в уникальную директорию
            File uploadedFile = new File(file.uploadedFileName());
            File newFile = new File(uploadDir, filename);
            uploadedFile.renameTo(newFile);

            // Создание Meta.json
            JsonObject metaJson = new JsonObject().put("tags", tags);
            try (FileWriter fileWriter = new FileWriter(new File(uploadDir, "Meta.json"))) {
                fileWriter.write(metaJson.encodePrettily());
            } catch (IOException e) {
                context.response()
                        .putHeader("Content-Type", "application/json; charset=UTF-8")
                        .setStatusCode(500)
                        .end("Ошибка при создании Meta.json");
                return;
            }

            // Возврат ID в ответе
            context.response()
                    .putHeader("Content-Type", "application/json; charset=UTF-8")
                    .setStatusCode(200)
                    .end(new JsonObject().put("id", uniqueId).encodePrettily());

            break; // Остановимся после первого файла
        }
    }

}