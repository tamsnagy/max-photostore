package com.max.photostore.service;

import com.max.photostore.exception.PhotostoreException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


@Service
@Profile({"live", "dev"})
public class ScaleServiceImpl implements ScaleService{
    @Value("${photos.small.width}")
    private int width;

    @Value("${photos.small.height}")
    private int height;

    @Override
    public byte[] scale(byte[] fileData) throws PhotostoreException {
        ByteArrayInputStream in = new ByteArrayInputStream(fileData);
        try {
            BufferedImage img = ImageIO.read(in);
            final float ratio = img.getHeight() * 1.0f / img.getWidth();
            int small_width = width;
            int small_height = height;
            if(ratio > 1.0f) {
                small_width = (int) (small_width / ratio);
            } else {
                small_height = (int) (small_height * ratio);
            }
            Image scaledImage = img.getScaledInstance(small_width, small_height, Image.SCALE_SMOOTH);
            BufferedImage imageBuff = new BufferedImage(small_width, small_height, BufferedImage.TYPE_INT_RGB);
            imageBuff.getGraphics().drawImage(scaledImage, 0, 0, new Color(0,0,0), null);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            ImageIO.write(imageBuff, "png", buffer);

            return buffer.toByteArray();
        } catch (IOException e) {
            throw new PhotostoreException("IOException in scale");
        }
    }
}
