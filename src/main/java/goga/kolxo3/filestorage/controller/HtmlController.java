package goga.kolxo3.filestorage.controller;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class HtmlController extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);

        // Serve static resources from the webroot directory
        router.route("/*").handler(StaticHandler.create("webroot"));

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8081, result -> {
                    if (result.succeeded()) {
                        System.out.println("HTML server started on port 8081");
                        startPromise.complete();
                    } else {
                        System.err.println("Failed to start HTML server: " + result.cause());
                        startPromise.fail(result.cause());
                    }
                });
    }
}
