package com.max.photostore;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import redis.embedded.RedisServer;
import redis.embedded.RedisServerBuilder;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PhotostoreApplicationTests {
    private static RedisServer redisServer;

    @BeforeClass
    public static void setUp() throws IOException {
        redisServer = RedisServer.builder()
                .port(6379)
                .build();

        redisServer.start();
    }

    @AfterClass
    public static void tearDown() {
        redisServer.stop();
    }

	@Test
	public void contextLoads() {

	}

}
