package goga.kolxo3.filestorage.auth;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class TokenAuthHandler implements Handler<RoutingContext> {

    private final String validToken;
    private final String charset;

    public TokenAuthHandler(String validToken, String charset) {
        this.validToken = validToken;
        this.charset = charset;
    }

    @Override
    public void handle(RoutingContext context) {
        System.out.println("TokenAuthHandler is processing the request."); // Логирование

        String token = context.request().getParam("token");

        if (token == null || !token.equals(validToken)) {
            context.response()
                    .setStatusCode(401)
                    .putHeader("Content-Type", "application/json; charset=" + charset)
                    .end("{\"error\": \"Unauthorized\"}");
        } else {
            context.next(); // Передаем управление следующему обработчику
        }
    }
}
