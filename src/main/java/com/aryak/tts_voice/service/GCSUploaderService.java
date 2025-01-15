package com.aryak.tts_voice.service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;

@Service
public class GCSUploaderService {

    private final Storage storage;

    public GCSUploaderService() {
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    public void uploadFile(String bucketName, String objectName, File file) throws Exception {
        // Read file content
        byte[] fileContent = Files.readAllBytes(file.toPath());

        // Set the content type to audio/mpeg
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
                .setContentType("audio/mpeg")
                .build();

        // Upload the file to GCS
        storage.create(blobInfo, fileContent);

    }
}

