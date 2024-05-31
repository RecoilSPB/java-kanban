package ru.practicum.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TaskHttpServer {
    public static final int PORT = 8078;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    public TaskHttpServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
    }

    private void load(HttpExchange h) throws IOException {
        try (h) {

            if ("GET".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/load/".length());
                if (key.isEmpty()) {
                    System.out.println("Key для загрузки пустой. key указывается в пути: /load/{key}");
                    h.sendResponseHeaders(HttpStatus.SC_BAD_REQUEST, 0);
                    return;
                }
                String response = data.get(key);
                if (response.isEmpty()) {
                    System.out.println("Value для отправки пустой.");
                    h.sendResponseHeaders(HttpStatus.SC_BAD_REQUEST, 0);
                    return;
                }
                h.sendResponseHeaders(HttpStatus.SC_OK, 0);

                try (OutputStream os = h.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }

    private void save(HttpExchange h) throws IOException {
        try (h) {
            System.out.println("\n/save");

            if ("POST".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
                    h.sendResponseHeaders(HttpStatus.SC_BAD_REQUEST, 0);
                    return;
                }
                String value = readText(h);
                if (value.isEmpty()) {
                    System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                    h.sendResponseHeaders(HttpStatus.SC_BAD_REQUEST, 0);
                    return;
                }
                data.put(key, value);
                System.out.println("Значение для ключа " + key + " успешно обновлено!");
                h.sendResponseHeaders(HttpStatus.SC_OK, 0);
            } else {
                System.out.println("/save ждёт POST-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(HttpStatus.SC_METHOD_NOT_ALLOWED, 0);
            }
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер на порту " + PORT + " остановлен");
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

}