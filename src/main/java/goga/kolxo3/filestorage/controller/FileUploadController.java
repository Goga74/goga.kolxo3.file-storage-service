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

public class FileUploadController extends AbstractVerticle
{
    private String uploadDir;
    private int httpPort;

    @Override
    public void start(Promise<Void> startPromise)
    {
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
        for (io.vertx.ext.web.FileUpload file : context.fileUploads()) {
            System.out.println("Received file: " + file.fileName());

            // Логика обработки файла

            context.response()
                    .putHeader("Content-Type", "text/plain; charset=UTF-8")
                    .setStatusCode(200).end("Файл загружен успешно");

            break; // Остановимся после первого файла
        }
    }
}

