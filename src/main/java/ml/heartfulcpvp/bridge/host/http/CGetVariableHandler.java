package ml.heartfulcpvp.bridge.host.http;

import ch.njol.skript.lang.Variable;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ml.heartfulcpvp.bridge.common.StreamObj;
import ml.heartfulcpvp.bridge.host.LoggingUtil;
import ml.heartfulcpvp.bridge.host.SkriptUtil;
import org.apache.commons.lang.SerializationUtils;

import java.io.IOException;

public class CGetVariableHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        var requestURI = exchange.getRequestURI().toString();
        exchange.getResponseHeaders().set("Content-Type", "application/octet-stream; charset=UTF-8");
        LoggingUtil.Log("Request accepted ; " + requestURI + " / Node: " + exchange.getRemoteAddress());

        var variable = requestURI.substring(requestURI.lastIndexOf("/") + 1);
        var response = new byte[0];

        var varContent = SkriptUtil.getVar(variable);

        if (varContent != null) {
            try {
                var obj = new StreamObj(varContent);
                response = SerializationUtils.serialize(obj);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            LoggingUtil.Log(variable + " was null");
        }

        exchange.sendResponseHeaders(200, response.length);
        var os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }
}
