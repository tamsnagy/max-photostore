package com.max.photostore;

import com.max.photostore.domain.Album;
import com.max.photostore.domain.AppUser;
import com.max.photostore.domain.Picture;
import com.max.photostore.exception.PhotostoreException;
import com.max.photostore.repository.AlbumRepository;
import com.max.photostore.repository.PictureRepository;
import com.max.photostore.repository.UserRepository;
import com.max.photostore.request.CreateAlbum;
import com.max.photostore.request.UpdatePicture;
import com.max.photostore.service.AlbumService;
import com.max.photostore.service.PictureService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import redis.embedded.RedisServer;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PhotostoreApplicationTests {
    private static RedisServer redisServer;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private UserRepository userRepository;

    private static AppUser user;

    private static final String USERNAME = "testUserName";

    @BeforeClass
    public static void setUp() throws IOException {
        redisServer = RedisServer.builder()
                .port(6379)
                .build();

        redisServer.start();

        user = new AppUser(USERNAME, "test@test.com", "password1".getBytes(), "salt".getBytes());
    }

    @AfterClass
    public static void tearDown() {
        redisServer.stop();
    }

    @Before
    public void before(){
        albumRepository.deleteAll();
        pictureRepository.deleteAll();
        userRepository.deleteAll();

        userRepository.save(user);
    }

	@Test
	public void testAlbumCreation() throws PhotostoreException {
        Long parentId = null;

        albumService.createAlbum(2L, new CreateAlbum("parentAlbum"), USERNAME);
        assertEquals(1, albumRepository.count());
        for(Album album: albumRepository.findAll()) {
            parentId = album.getId();
        }
        albumService.createAlbum(2L, new CreateAlbum("childAlbum1", parentId), USERNAME);
        albumService.createAlbum(2L, new CreateAlbum("childAlbum2", parentId), USERNAME);
        assertEquals(3, albumRepository.count());
        Album test = albumRepository.findOne(parentId);
        assertEquals(2, test.getAlbumList().size());

    }

    @Test
    public void testPictureUpload() throws PhotostoreException {
        Long albumId = null;
        albumService.createAlbum(2L, new CreateAlbum("parentAlbum"), USERNAME);
        for(Album album: albumRepository.findAll()) {
            albumId = album.getId();
        }
        final byte[] content = "pictureContent".getBytes();
        final String name = "testPictureName";
        Long pictureId = pictureService.uploadPicture(content, name, USERNAME, albumId);

        assertEquals(1, albumRepository.count());
        assertEquals(1, pictureRepository.count());

        Album album = albumRepository.findOne(albumId);
        List<Picture> pictureList = album.getPictureList();

        assertEquals(1, pictureList.size());
        Picture pictureFromAlbum = pictureList.get(0);
        Picture pictureFromDB = pictureRepository.findOne(pictureId);

        assertEquals(pictureFromAlbum.getName(), pictureFromDB.getName());
    }

}
