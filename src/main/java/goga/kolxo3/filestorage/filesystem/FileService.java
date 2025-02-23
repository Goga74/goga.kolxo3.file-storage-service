package goga.kolxo3.filestorage.filesystem;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.nio.file.Files;

public class FileService {
    private final String uploadDir;

    public FileService(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public JsonObject getDescription(String id) throws Exception {
        String folderPath = uploadDir + File.separator + id;
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            throw new Exception("Папка не найдена для данного id");
        }

        JsonObject responseJson = new JsonObject();
        File metaFile = new File(folderPath + File.separator + "Meta.json");

        if (metaFile.exists()) {
            String content = new String(Files.readAllBytes(metaFile.toPath()));
            JsonObject metaJson = new JsonObject(content);
            responseJson.put("meta", metaJson);
        } else {
            responseJson.put("meta", "Файл Meta.json не найден.");
        }

        JsonArray fileNames = new JsonArray();
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (!file.getName().equals("Meta.json") && !file.getName().equals(".") && file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }

        responseJson.put("files", fileNames);
        return responseJson;
    }

    public String getContent(String id) throws Exception {
        String folderPath = uploadDir + File.separator + id;
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            throw new Exception("Папка не найдена для данного id");
        }

        StringBuilder contentBuilder = new StringBuilder();
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (!file.getName().equals("Meta.json") && file.isFile()) {
                    String content = new String(Files.readAllBytes(file.toPath()));
                    contentBuilder.append(content).append("\n");
                }
            }
        }

        return contentBuilder.toString();
    }
}
