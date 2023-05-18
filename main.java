///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17+
//JAVAC_OPTIONS -parameters

//DEPS io.quarkus:quarkus-bom:${quarkus.version:3.0.3.Final}@pom
//DEPS io.quarkus:quarkus-resteasy-reactive
//DEPS io.quarkus:quarkus-resteasy-reactive-jackson

//FILES application.properties

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.quarkus.runtime.Quarkus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@ApplicationScoped 
@Path("/")
public class main {

    // Keep track of todo's. Does not persist if Java process is restarted.
    final Map<String, List<String>> todos = new ConcurrentHashMap<>();

    @POST @Path("/todos/{username}")
    public void add_todo(String username, String todo) {
        if (!todos.containsKey(username)) {
            todos.put(username, new ArrayList<>());
        }
        todos.get(username).add(todo);
    }

    @GET @Path("/todos/{username}")
    public List<String> get_todos(String username) {
        return todos.getOrDefault(username, new ArrayList<>());
    }

    @DELETE @Path("/todos/{username}")
    public void delete_todo(@PathParam("username") String username, int todoIdx) {
        if (0 <= todoIdx && todoIdx < todos.getOrDefault(username, new ArrayList<>()).size()) {
            todos.get(username).remove(todoIdx);
        } else {
           // fail silently, it's a simple plugin
        }
    }

    @GET @Path("/logo.png")
    public java.nio.file.Path plugin_logo() {
        return Paths.get("logo.png");
    }

    @GET @Path("/.well-known/ai-plugin.json")
    public java.nio.file.Path plugin_manifest() {
        return Paths.get(".well-known/ai-plugin.json");
    }

    @GET @Path("/openapi.yaml")
    public java.nio.file.Path openapi_spec() {
        return Paths.get("openapi.yaml");
    }
    public static void main(String... args) {
        Quarkus.run();
    }

}

