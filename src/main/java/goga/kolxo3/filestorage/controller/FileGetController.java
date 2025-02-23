package goga.kolxo3.filestorage.controller;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import goga.kolxo3.filestorage.filesystem.FileService;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;


public class FileGetController extends AbstractVerticle
{

    public static final String ADDRESS_GET_DESCRIPTION = "file.get.description";
    public static final String ADDRESS_GET_CONTENT = "file.get.content";

    private String uploadDir;
    private int httpPort;
    private String charset;
    private FileService fileService;

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
                System.out.println("HTTP port: " + httpPort);
                charset = config.getString("charset", "UTF-8");
                System.out.println("charset: " + charset);
                fileService = new FileService(uploadDir);

                vertx.eventBus().consumer(ADDRESS_GET_DESCRIPTION, this::handleGetDescription);
                vertx.eventBus().consumer(ADDRESS_GET_CONTENT, this::handleGetContent);

                startPromise.complete();
            } else {
                startPromise.fail(configResult.cause());
            }
        });
    }

    private void handleGetDescription(Message<JsonObject> message) {
        JsonObject body = message.body();
        String id = body.getString("id");

        if (id == null) {
            message.fail(400, "Необходим параметр id");
            return;
        }

        try {
            JsonObject description = fileService.getDescription(id);
            message.reply(description.encodePrettily());
        } catch (Exception e) {
            message.fail(404, e.getMessage());
        }
    }

    private void handleGetContent(Message<JsonObject> message) {
        JsonObject body = message.body();
        String id = body.getString("id");

        if (id == null) {
            message.fail(400, "Необходим параметр id");
            return;
        }

        try {
            String content = fileService.getContent(id);
            message.reply(content);
        } catch (Exception e) {
            message.fail(404, e.getMessage());
        }
    }
}
