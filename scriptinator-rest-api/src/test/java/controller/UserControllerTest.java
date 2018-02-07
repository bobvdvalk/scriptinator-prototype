package controller;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import io.chapp.scriptinator.ScriptinatorRestApi;
import io.chapp.scriptinator.model.User;
import io.chapp.scriptinator.repositories.UserRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.AssertJUnit.assertEquals;

public class UserControllerTest {
    private final OkHttpClient client = new OkHttpClient();

    ApplicationContext context;
    UserRepository userRepository;
    User user;


    @Test
    public void testWhenUserIsRequestedItIsReturned() throws IOException {
        // Precondition
        user = new User();
        user.setDisplayName("UserTestUser");
        user.setEmail("testuser@evil.incoperated");
        user.setUsername("Superusertest93");
        user.setPassword("thisistest");
        user = userRepository.save(user);

        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url("http://localhost:8080/users/Superusertest93")
                        .build()
        ).execute();

        // Validation
        ReadContext json = JsonPath.parse(response.body().string());

        assertEquals(
                json.read("$.displayName"),
                "UserTestUser"
        );

        assertEquals(
                json.read("$.email"),
                "testuser@evil.incoperated"
        );

        assertEquals(
                json.read("$.url"),
                "http://localhost:8080/users/Superusertest93"
        );

        assertEquals(
                json.read("$.avatarUrl"),
                "https://www.gravatar.com/avatar/b18d01a30f85969ec426927c7453c5ea"
        );
    }

    @AfterMethod
    public void cleanUp() {
        userRepository.deleteAll();
    }

    @BeforeClass
    public void startServer() {
        context = SpringApplication.run(ScriptinatorRestApi.class);
        userRepository = context.getBean(UserRepository.class);
    }

    @AfterClass
    public void stopServer() {
        SpringApplication.exit(context);
    }
}
