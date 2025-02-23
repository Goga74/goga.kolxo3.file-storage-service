package goga.kolxo3.filestorage.verticle;

import goga.kolxo3.filestorage.controller.FileGetController;
import goga.kolxo3.filestorage.controller.FileUploadController;
import goga.kolxo3.filestorage.controller.HtmlController;
import goga.kolxo3.filestorage.auth.TokenAuthHandler;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {

    private String validToken;
    private String charset;
    private int httpPort;

    @Override
    public void start(Promise<Void> startPromise) {
        // Настройка конфигурации
        ConfigRetriever retriever = ConfigRetriever.create(vertx,
                new ConfigRetrieverOptions()
                        .addStore(new ConfigStoreOptions()
                                .setType("file")
                                .setConfig(new JsonObject().put("path", "config.json"))));

        retriever.getConfig(configResult -> {
            if (configResult.succeeded()) {
                var config = configResult.result();
                validToken = config.getString("validToken");
                charset = config.getString("charset", "UTF-8");
                httpPort = config.getInteger("httpPort", 8082);

                // Логирование токена для проверки
                System.out.println("Valid token loaded from config: " + validToken);

                // Создаем экземпляр TokenAuthHandler
                TokenAuthHandler tokenAuthHandler = new TokenAuthHandler(validToken, charset);

                // Разворачиваем FileUploadController
                DeploymentOptions fileUploadOptions = new DeploymentOptions().setConfig(config);
                vertx.deployVerticle(new FileUploadController(), fileUploadOptions, res1 -> {
                    if (res1.succeeded()) {
                        System.out.println("FileUploadController развернут успешно.");

                        // Разворачиваем FileGetController
                        DeploymentOptions fileGetOptions = new DeploymentOptions().setConfig(config);
                        vertx.deployVerticle(new FileGetController(), fileGetOptions, res2 -> {
                            if (res2.succeeded()) {
                                System.out.println("FileGetController развернут успешно.");

                                // Разворачиваем HtmlController
                                vertx.deployVerticle(new HtmlController(), res3 -> {
                                    if (res3.succeeded()) {
                                        System.out.println("HtmlController развернут успешно.");

                                        // Создаем Router и настраиваем обработчики
                                        Router router = Router.router(vertx);

                                        // Защищаем эндпоинты FileGetController
                                        router.get("/desc").handler(tokenAuthHandler);
                                        router.get("/content").handler(tokenAuthHandler);

                                        // Добавляем обработчики FileGetController
                                        router.get("/desc").handler(context -> handleGetRequest(context, FileGetController.ADDRESS_GET_DESCRIPTION));
                                        router.get("/content").handler(context -> handleGetRequest(context, FileGetController.ADDRESS_GET_CONTENT));

                                        // Публичный эндпоинт
                                        router.get("/public").handler(ctx -> ctx.response()
                                                .putHeader("Content-Type", "application/json; charset=" + charset)
                                                .end("{\"message\": \"This is a public endpoint!\"}"));

                                        // Обработчик для всех остальных маршрутов
                                        router.route().handler(context -> context.response()
                                                .setStatusCode(404)
                                                .putHeader("Content-Type", "application/json; charset=" + charset)
                                                .end(new JsonObject().put("error", "Not Found").encodePrettily()));

                                        // Создаем HTTP сервер
                                        vertx.createHttpServer().requestHandler(router).listen(httpPort, http -> {
                                            if (http.succeeded()) {
                                                System.out.println("HTTP server started on port " + httpPort);
                                                startPromise.complete();
                                            } else {
                                                System.err.println("Не удалось запустить HTTP сервер: " + http.cause());
                                                startPromise.fail(http.cause());
                                            }
                                        });
                                    } else {
                                        System.err.println("Ошибка при разворачивании HtmlController: " + res3.cause());
                                        startPromise.fail(res3.cause());
                                    }
                                });
                            } else {
                                System.err.println("Ошибка при разворачивании FileGetController: " + res2.cause());
                                startPromise.fail(res2.cause());
                            }
                        });
                    } else {
                        System.err.println("Ошибка при разворачивании FileUploadController: " + res1.cause());
                        startPromise.fail(res1.cause());
                    }
                });
            } else {
                System.err.println("Не удалось получить конфигурацию: " + configResult.cause());
                startPromise.fail(configResult.cause());
            }
        });
    }

    private void handleGetRequest(RoutingContext context, String address) {
        String id = context.request().getParam("id");
        if (id == null) {
            context.response()
                    .putHeader("Content-Type", "application/json; charset=" + charset)
                    .setStatusCode(400)
                    .end(new JsonObject().put("error", "Необходим параметр id").encodePrettily());
            return;
        }

        JsonObject requestData = new JsonObject().put("id", id);
        vertx.eventBus().request(address, requestData, result -> {
            if (result.succeeded()) {
                Message<?> message = result.result();
                String contentType = message.headers().get("Content-Type"); // Get Content-Type from message headers
                context.response()
                        .putHeader("Content-Type", contentType)
                        .end((String) message.body());
            } else {
                context.fail(result.cause());
            }
        });
    }
}
