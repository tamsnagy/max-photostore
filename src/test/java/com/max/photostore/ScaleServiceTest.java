package com.max.photostore;

import com.max.photostore.exception.PhotostoreException;
import com.max.photostore.service.ScaleService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("test")
public class ScaleServiceTest implements ScaleService {
    @Override
    public byte[] scale(byte[] fileData) throws PhotostoreException {
        return fileData;
    }
}
