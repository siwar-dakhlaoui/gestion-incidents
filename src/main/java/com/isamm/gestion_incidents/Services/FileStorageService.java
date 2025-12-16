package com.isamm.gestion_incidents.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.*;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * Sauvegarde les images localement
     */
    public List<String> saveFiles(MultipartFile[] files) {
        List<String> filenames = new ArrayList<>();

        if (files == null || files.length == 0) {
            return filenames;
        }

        try {
            Path path = Paths.get(uploadDir);
            Files.createDirectories(path);

            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = path.resolve(filename);

                Files.copy(
                        file.getInputStream(),
                        filePath,
                        StandardCopyOption.REPLACE_EXISTING
                );

                filenames.add(filename);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur upload images", e);
        }

        return filenames;
    }
}
