package com.max.photostore.service;

import com.max.photostore.exception.InternalServerErrorException;
import com.max.photostore.exception.PhotostoreException;
import com.max.photostore.exception.ResourceMissingException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileServiceImpl implements FileService {
    @Value("${photostore.paths.uploadedFiles}")
    private String pathToUploadedFiles;

    @Value("${photostore.filename}")
    private String filename;

    @Override
    public byte[] getFile() throws PhotostoreException {
        Path filepath = Paths.get(pathToUploadedFiles, filename);
        if(Files.notExists(filepath)) {
            throw new ResourceMissingException("File was not yet uploaded.");
        }
        // Open the file locally
        try(FileInputStream fis = new FileInputStream(filepath.toFile())) {
            return IOUtils.toByteArray(fis);
        } catch (FileNotFoundException e) {
            throw new ResourceMissingException("File was not yet uploaded.", e);
        } catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }

    @Override
    public void saveFile(byte[] content) throws InternalServerErrorException {
        try {
            Path directory = Paths.get(pathToUploadedFiles);
            if (Files.notExists(directory)) {
                Files.createDirectories(directory);
            }
            Path filepath = Paths.get(pathToUploadedFiles, filename);

            // Save the file locally
            try (BufferedOutputStream stream =
                    new BufferedOutputStream(new FileOutputStream(filepath.toFile()))) {
                stream.write(content);
            }
        } catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }
}
