package goga.kolxo3.filestorage.config;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageConfig {
    private String uploadDir = "/tmp/file-storage";

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}