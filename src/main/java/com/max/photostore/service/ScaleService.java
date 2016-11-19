package com.max.photostore.service;

import com.max.photostore.exception.PhotostoreException;

public interface ScaleService {
    byte[] scale(final byte[] fileData) throws PhotostoreException;
}
