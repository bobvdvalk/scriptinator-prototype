package controller;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import io.chapp.scriptinator.model.Project;
import io.chapp.scriptinator.model.Script;
import io.chapp.scriptinator.model.User;
import io.chapp.scriptinator.repositories.ProjectRepository;
import io.chapp.scriptinator.repositories.ScriptRepository;
import io.chapp.scriptinator.repositories.UserRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.AssertJUnit.assertEquals;

@Listeners(ScriptinatorTestCase.class)
public class ScriptControllerTest {
    private final OkHttpClient client = new OkHttpClient();

    @Autowired
    ScriptRepository scriptRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProjectRepository projectRepository;

    @Test
    public void testWhenScriptIsRequestedItReturned() throws IOException {
        // Precondition
        User user = new User();
        user.setDisplayName("UserTestUser");
        user.setEmail("testuser@evil.incoperated");
        user.setUsername("Superusertest93");
        user.setPassword("thisistest");
        userRepository.save(user);

        Project project = new Project();
        project.setName("HelloWorld");
        project.setDescription("This is a description.");
        project.setOwner(user);
        projectRepository.save(project);

        Script script = new Script();
        script.setProject(project);
        script.setCode("Script.info('Hello World');");
        script.setName("greet");
        script.setDescription("Prints hello world.");
        scriptRepository.save(script);


        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url("http://localhost:8080/scripts/" + script.getId())
                        .build()
        ).execute();

        // Validation
        ReadContext json = JsonPath.parse(response.body().string());

        assertEquals(
                json.read("$.name"),
                "greet"
        );

        assertEquals(
                json.read("$.code"),
                "Script.info('Hello World');"
        );

        assertEquals(
                json.read("$.project.name"),
                "HelloWorld"
        );

        assertEquals(
                json.read("$.project.owner.username"),
                "Superusertest93"
        );
    }

    @Test
    public void testWhenScriptListIsRequestedItReturned() throws IOException {
        // Precondition
        User user = new User();
        user.setDisplayName("UserTestUser");
        user.setEmail("testuser@evil.incoperated");
        user.setUsername("Superusertest93");
        user.setPassword("thisistest");
        userRepository.save(user);

        Project project = new Project();
        project.setName("HelloWorld");
        project.setDescription("This is a description.");
        project.setOwner(user);
        projectRepository.save(project);

        Script script = new Script();
        script.setProject(project);
        script.setCode("Script.info('Hello World');");
        script.setName("greet");
        script.setDescription("Prints hello world.");
        scriptRepository.save(script);


        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url("http://localhost:8080/scripts/")
                        .build()
        ).execute();

        // Validation
        ReadContext json = JsonPath.parse(response.body().string());

        assertEquals(
                json.read("$.totalItemCount"),
                (Integer) 1
        );

        assertEquals(
                json.read("$.items.[0].name"),
                "greet"
        );

        assertEquals(
                json.read("$.items.[0].project.name"),
                "HelloWorld"
        );

        assertEquals(
                json.read("$.hasItems"),
                Boolean.valueOf("true")
        );

        assertEquals(
                json.read("$.hasNext"),
                Boolean.valueOf("false")
        );

        assertEquals(
                json.read("$.hasPrevious"),
                Boolean.valueOf("false")
        );
    }

    @Test
    public void scriptReturnEmptyScriptList() throws IOException {
        // Precondition
        User user = new User();
        user.setDisplayName("UserTestUser");
        user.setEmail("testuser@evil.incoperated");
        user.setUsername("Superusertest93");
        user.setPassword("thisistest");
        userRepository.save(user);

        Project project = new Project();
        project.setName("HelloWorld");
        project.setDescription("This is a description.");
        project.setOwner(user);
        projectRepository.save(project);

        // Action
        Response response = client.newCall(
                new Request.Builder()
                        .get()
                        .url("http://localhost:8080/scripts")
                        .build()
        ).execute();

        // Validation
        ReadContext json = JsonPath.parse(response.body().string());

        assertEquals(
                json.read("$.totalItemCount"),
                (Integer) 0
        );

        assertEquals(
                json.read("$.hasItems"),
                Boolean.valueOf("false")
        );

        assertEquals(
                json.read("$.hasNext"),
                Boolean.valueOf("false")
        );

        assertEquals(
                json.read("$.hasPrevious"),
                Boolean.valueOf("false")
        );

    }
}
