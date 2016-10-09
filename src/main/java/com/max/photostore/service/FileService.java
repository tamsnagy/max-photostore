package com.max.photostore.service;

import com.max.photostore.exception.InternalServerErrorException;
import com.max.photostore.exception.PhotostoreException;


public interface FileService {
    byte[] getFile() throws PhotostoreException;
    void saveFile(byte[] content) throws InternalServerErrorException;
}
