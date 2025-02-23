package goga.kolxo3.filestorage;

import goga.kolxo3.filestorage.controller.FileUploadController;
import io.vertx.core.Vertx;

public class Application {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new FileUploadController());
    }
}
