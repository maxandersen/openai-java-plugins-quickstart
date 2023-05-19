///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 17+
//JAVAC_OPTIONS -parameters

//DEPS io.quarkus:quarkus-bom:${quarkus.version:3.0.3.Final}@pom
//DEPS io.quarkus:quarkus-resteasy-reactive
//DEPS io.quarkus:quarkus-resteasy-reactive-jackson
//DEPS io.quarkus:quarkus-smallrye-openapi

//FILES application.properties

//Mounting these resources into locations where Quarkus will 
//serve them directly. Removing need to have it handled in code.
//FILES META-INF/resources/logo.png=logo.png
//FILES META-INF/.well-knwon/ai-plugin.json=.well-known/ai-plugin.json

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jboss.resteasy.reactive.RestPath;

import io.quarkus.runtime.Quarkus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped 
@Path("/")
@OpenAPIDefinition(
  info = 
  @Info(
      title = " TODO Plugin",  
      version = "v1", 
      //openappi descriptions chatgpt uses to get context on the API
      description = """
        A plugin that allows the user to create and manage a TODO list using ChatGPT.
        If you do not know the user's username, ask them first before making queries to the plugin. 
        Otherwise, use the username "global".
        """
))
public class main extends Application {

    // Keep track of todo's. Does not persist if Java process is restarted.
    final Map<String, List<String>> todos = new ConcurrentHashMap<>();

    //Using records as OpenAI insist on "objects" schemas rather than just 
    //straight values.
    record AddTodo(String todo) {}

    @POST @Path("/todos/{username}")
    @Operation(summary = "Add a todo to the list")
    public void addTodo(
            @RestPath @Parameter(description = "The name of the user") String username, 
            AddTodo todo) {

        if (!todos.containsKey(username)) {
            todos.put(username, new ArrayList<>());
        }
        todos.get(username).add(todo.todo);
    }

    record TodoList(@Schema(description="The list of todos") List<String> todos) {}

    @GET @Path("/todos/{username}")
    @Operation(summary = "Get the list of todos")
    public TodoList getTodos(
            @RestPath @Parameter(description = "The name of the user") 
            String username) {
       return new TodoList(todos.getOrDefault(username, new ArrayList<>()));
    }

    record DeleteTodo(@Schema(description="The index of the todo to delete", required=true) int todoIdx) {};

    @DELETE @Path("/todos/{username}")
    @Operation(summary = "Delete a todo from the list")
    public void deleteTodo(
                  @RestPath @Parameter(description = "The name of the user") String username, 
                    DeleteTodo t) {
        if (0 <= t.todoIdx && t.todoIdx < todos.getOrDefault(username, new ArrayList<>()).size()) {
            todos.get(username).remove(t.todoIdx);
        } else {
           // fail silently, it's a simple plugin
        }
    }
    public static void main(String... args) {
        Quarkus.run();
    }

}

