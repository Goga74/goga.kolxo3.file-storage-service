package goga.kolxo3.filestorage.verticle;

import goga.kolxo3.filestorage.controller.FileUploadController;
import goga.kolxo3.filestorage.controller.FileDescriptionController;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        // Разворачиваем FileUploadController
        vertx.deployVerticle(new FileUploadController(), res1 -> {
            if (res1.succeeded()) {
                System.out.println("FileUploadController развернут успешно.");

                // Разворачиваем FileDescriptionController
                vertx.deployVerticle(new FileDescriptionController(), res2 -> {
                    if (res2.succeeded()) {
                        System.out.println("FileDescriptionController развернут успешно.");
                        startPromise.complete(); // Завершаем старт после успешного развертывания обоих контроллеров
                    } else {
                        System.err.println("Ошибка при разворачивании FileDescriptionController: " + res2.cause());
                        startPromise.fail(res2.cause());
                    }
                });
            } else {
                System.err.println("Ошибка при разворачивании FileUploadController: " + res1.cause());
                startPromise.fail(res1.cause());
            }
        });
    }
}
