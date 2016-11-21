package com.max.photostore;

import com.max.photostore.domain.Album;
import com.max.photostore.domain.AppGroup;
import com.max.photostore.domain.AppUser;
import com.max.photostore.domain.Picture;
import com.max.photostore.exception.PhotostoreException;
import com.max.photostore.exception.ResourceMissingException;
import com.max.photostore.repository.AlbumRepository;
import com.max.photostore.repository.GroupRepository;
import com.max.photostore.repository.PictureRepository;
import com.max.photostore.repository.UserRepository;
import com.max.photostore.request.CreateAlbum;
import com.max.photostore.response.GetAlbum;
import com.max.photostore.service.AlbumService;
import com.max.photostore.service.PictureService;
import org.hibernate.Hibernate;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
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

    @Autowired
    private GroupRepository groupRepository;

    private static AppUser user1, user2;

    private static final String USERNAME = "testUserName";
    private static final String USERNAME_2 = "testUserName2";

    @BeforeClass
    public static void setUp() throws IOException {
        redisServer = RedisServer.builder()
                .port(6378)
                .build();

        redisServer.start();

        user1 = new AppUser(USERNAME, "test@test.com", "password1".getBytes(), "salt".getBytes());
        user2 = new AppUser(USERNAME_2, "test2@test.com", "password1".getBytes(), "salt".getBytes());
    }

    @AfterClass
    public static void tearDown() {
        redisServer.stop();
    }

    @Before
    public void before(){
        // order is important !!
        groupRepository.deleteAll();
        albumRepository.deleteAll();
        pictureRepository.deleteAll();
        userRepository.deleteAll();

        userRepository.save(user1);
        userRepository.save(user2);
    }

	@Test
    @Ignore
	public void testAlbumCreation() throws PhotostoreException {
        Long parentId = null;

        albumService.createAlbum(new CreateAlbum("parentAlbum"), USERNAME);
        assertEquals(1, albumRepository.count());
        for(Album album: albumRepository.findAll()) {
            parentId = album.getId();
        }
        albumService.createAlbum(new CreateAlbum("childAlbum1", parentId), USERNAME);
        albumService.createAlbum(new CreateAlbum("childAlbum2", parentId), USERNAME);
        assertEquals(3, albumRepository.count());
        Album test = albumRepository.findOne(parentId);
        Hibernate.initialize(test.getAlbumList());
        assertEquals(2, test.getAlbumList().size());

    }

    @Test
    @Ignore
    public void testPictureUpload() throws PhotostoreException {
        Long albumId = null;
        albumService.createAlbum(new CreateAlbum("parentAlbum"), USERNAME);
        for(Album album: albumRepository.findAll()) {
            albumId = album.getId();
        }
        final byte[] content = "R0lGODlhAQABAIAAAAUEBAAAACwAAAAAAQABAAACAkQBADs=".getBytes();
        final String name = "testPictureName";
        Long pictureId = pictureService.uploadPicture(content, name, USERNAME, albumId);

        assertEquals(1, albumRepository.count());
        assertEquals(1, pictureRepository.count());

        Album album = albumRepository.findOne(albumId);
        Hibernate.initialize(album.getPictureList());
        List<Picture> pictureList = album.getPictureList();

        assertEquals(1, pictureList.size());
        Picture pictureFromAlbum = pictureList.get(0);
        Picture pictureFromDB = pictureRepository.findOne(pictureId);

        assertEquals(pictureFromAlbum.getName(), pictureFromDB.getName());
    }


    @Test
    @Ignore
    public void testGroupRepository() throws ResourceMissingException {
        // Given
        Album album1 = new Album("a1", new Date(), user2, null, Collections.emptyList(), Collections.emptyList());
        Album album2 = new Album("a2", new Date(), user2, null, Collections.emptyList(), Collections.emptyList());
        Album album3 = new Album("a3", new Date(), user2, null, Collections.emptyList(), Collections.emptyList());
        Album album4 = new Album("a4", new Date(), user2, null, Collections.emptyList(), Collections.emptyList());
        albumRepository.save(Arrays.asList(album1, album2, album3, album4));

        AppGroup group1 = new AppGroup("group1", user1);
        group1.addAlbum(album1);
        AppGroup group2 = new AppGroup("group2", user2);
        group2.addMember(user1);
        group2.addAlbum(album2);
        group2.addAlbum(album3);
        AppGroup group3 = new AppGroup("group3", user2);
        group3.addAlbum(album4);
        groupRepository.save(Arrays.asList(group1, group2, group3));

        assertEquals(3, groupRepository.count());
        assertEquals(4, albumRepository.count());

        // When
        List<AppGroup> groupsUser1CanSee = groupRepository.findByMembersInOrOwner(Collections.singletonList(user1), user1);

        // Then
        assertEquals(2, groupsUser1CanSee.size());
        assertTrue(groupsUser1CanSee.containsAll(Arrays.asList(group1, group2)));

        List<GetAlbum> albums =  albumService.listAlbums(user1.getUsername());
        assertEquals(3, albums.size());
        List<Long> readableAlbumsByUser1 = Stream.of(album1, album2, album3).map(Album::getId).collect(Collectors.toList());
        assertTrue(readableAlbumsByUser1.containsAll(albums.stream().map(a-> a.id).collect(Collectors.toList())));
    }
}
