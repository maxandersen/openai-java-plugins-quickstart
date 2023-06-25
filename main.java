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
//FILES META-INF/resources/.well-known/ai-plugin.json=.well-known/ai-plugin.json

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestPath;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;

@ApplicationScoped 
@Path("/")
@OpenAPIDefinition(
  info = 
  @Info(
      title = " IDE Plugin",  
      version = "v1", 
      //openappi descriptions chatgpt uses to get context on the API
      description = """
        A plugin that allows the user to list, create, add and update files in his project.
        """
))
public class main {

    @Inject
    Logger log;
    
    record FileList(@Schema(description="The list of files recursively found in this directory. Will by default return all files in the root directory.")
                     List<String> files) {}

    @GET @Path("/files{directory:.+}")
    @Operation(summary = "Get the files in a directory")
    public FileList getFiles(
            @RestPath @Parameter(description = "The name of the directory relatively to the root of the project. Is a simple string. Use '/' to get the root direc") 
            String directory) throws IOException {
                directory = handleDir(directory);

                log.info("Getting files in directory "+directory);

                var files = Files.list(Paths.get(directory)).map(p->p.toString()).toList();

                return new FileList(files);
    }

     record FileContent(@Schema(description="The content of a file.")
                     String content) {}

    @GET @Path("/read{filename:.+}")
    @Operation(summary = "Get the content of the file")
    public FileContent getFile(
            @RestPath @Parameter(description = "Read the content of a file. The parameter is a single string relative to the project root.") 
            String filename) throws IOException {
                filename = handleDir(filename);

                log.info("Getting content of file '"+filename + "'");

                var content = Files.readString(Paths.get(filename));
                return new FileContent(content);
    }

    @POST @Path("/update{filename:.+}")
    @Operation(summary = "Update the content of the file")
    public void updateFile(
            @RestPath @Parameter(description = "Update the content of a file. The parameter is a single string relative to the project root.") 
            String filename, FileContent content) throws IOException {
                filename = handleDir(filename);

                log.info("Updating content of file '"+filename + "'");

                Files.writeString(Paths.get(filename), content.content);
    }

    private String handleDir(String directory) {
        if(directory==null || directory.isBlank()) {
            directory = "/";
        }

        if(directory.startsWith("/")) {
            directory = directory.substring(1);
        }
        return directory;
    }
}

