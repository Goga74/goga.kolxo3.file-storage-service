package goga.kolxo3.filestorage.vertx;

import io.vertx.core.Vertx;

public class VertxFileHandler {
    private final Vertx vertx;

    public VertxFileHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    public void saveFileAsync(byte[] content, String path) {
        vertx.fileSystem().writeFile(path, io.vertx.core.buffer.Buffer.buffer(content), result -> {
            if (result.failed()) {
                // Обработка ошибки
            }
        });
    }
}