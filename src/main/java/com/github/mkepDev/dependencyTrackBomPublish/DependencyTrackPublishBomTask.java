/*
 * Copyright (c) 2020.
 * Markus Keppeler
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.mkepDev.dependencyTrackBomPublish;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Task to publish the software bom to the dependency track server
 */
public class DependencyTrackPublishBomTask extends DefaultTask {

    /**
     * The name of the task
     */
    public static final String NAME = "publishBom";

    /**
     * The name of header field for the api key
     */
    private static final String HEADER_API_KEY_HEADER_FIELD = "X-API-Key";
    /**
     * The json field name for the project uuid
     */
    private static final String BODY_PROJECT_JSON_FIELD = "project";
    /**
     * the json field name for the bom
     */
    private static final String BODY_BOM_JSON_FIELD = "bom";

    /**
     * The extension that contains the config
     */
    private final DependencyTrackBomPublishExtension extension;

    /**
     * The logger
     */
    private final Logger logger;


    @Inject
    public DependencyTrackPublishBomTask(DependencyTrackBomPublishExtension extension, Logger logger) {
        setGroup(DependencyTrackBomPublishPlugin.categoryName);
        this.extension = extension;
        this.logger = logger;
    }

    /**
     * Task call to publish the bom to the server
     */
    @TaskAction
    public void publish() {
        if (!extension.isValid()) {
            logger.error("Extension is invalid '{}'.", extension);
            logger.error("The necessary parameters are not set. Specify them inside '" + DependencyTrackBomPublishExtension.EXTENSION_IDENTIFIER + "'");
            throw new IllegalArgumentException("The necessary parameters are not set. Specify them inside '" + DependencyTrackBomPublishExtension.EXTENSION_IDENTIFIER + "'.");
        }


        publishData(extension.getUseHttps(), extension.getHost(), extension.getRealm(), extension.getApiKey(), extension.getProjectUuid(), getFileContent(extension.getBomFile()));
    }

    /**
     * Reads the given file and extracts the bom
     *
     * @param file the file
     * @return the content of the file. null if the file can't be read
     */
    private String getFileContent(File file) {
        logger.info("Try to read file '{}'...", file.getPath());
        try (Stream<String> stream = Files.lines(file.toPath())) {
            return stream.collect(Collectors.joining("\n"));
        } catch (IOException e) {
            logger.error("Failed to read the file '{}'. See {}", file.getAbsolutePath(), e);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Send the given bomString to the dependency track server
     *
     * @param useHttps    whether https should be used
     * @param host        the host
     * @param realm       the realm of rest api
     * @param apiKey      the api key
     * @param projectUuid the uuid of the project
     * @param bomString   the bom string
     */
    private void publishData(boolean useHttps, String host, String realm, String apiKey, String projectUuid, String bomString) {

        if (bomString == null || bomString.isEmpty()) {
            throw new IllegalArgumentException("The bom string is null or empty. Check the generated bom.");
        }

        logger.info("Encode bom string to Base64...");
        String encodedBom = Base64.getEncoder().encodeToString(bomString.getBytes());
        logger.info("Done with encoding");

        //Create json string
        logger.info("Construct json body...");
        Gson gson = new GsonBuilder().create();
        JsonObject jsonData = new JsonObject();
        logger.info("Set project uuid.");
        jsonData.addProperty(BODY_PROJECT_JSON_FIELD, projectUuid);
        logger.info("Set encoded bom.");
        jsonData.addProperty(BODY_BOM_JSON_FIELD, encodedBom);


        logger.info("Convert json object to string...");
        String jsonString = gson.toJson(jsonData);
        logger.info("Json created. Json body:\n{}", jsonString);

        logger.info("Construct url...");
        while (host.startsWith("/")) {
            host = host.substring(1);
        }
        while (host.endsWith("/") && host.length() > 1) {
            host = host.substring(0, host.length() - 1);
        }
        while (realm.startsWith("/")) {
            realm = realm.substring(1);
        }
        while (realm.endsWith("/") && realm.length() > 1) {
            realm = realm.substring(0, host.length() - 1);
        }

        logger.info("Use new reqex");

        String protocol = null;

        if (useHttps) {
            protocol = "https://";
        } else {
            protocol = "http://";
        }

        String dtrackUrl = protocol + host + "/" + realm;

        logger.info("Create request to '{}'...", dtrackUrl);

        Request r = Request.Put(dtrackUrl)
                .addHeader(HEADER_API_KEY_HEADER_FIELD, apiKey)
                .bodyString(jsonString, ContentType.APPLICATION_JSON);

        logger.info("Send request to '{}'...", dtrackUrl);
        try {
            String returnContent = r.execute().returnContent().asString();

            logger.info("Return content: \n'{}'", returnContent);
        } catch (HttpResponseException e) {
            logger.error("HTTP request error: {}", e.getLocalizedMessage());
        } catch (ConnectException e) {
            logger.error("Http connection attempt failed. See error message:\n {}.", e.getLocalizedMessage());
        } catch (IOException e) {
            logger.error("Http request failed. See error message:\n {}.", e.getLocalizedMessage());
        }
        logger.info("Bom has been sent to dependency track server.");
    }
}
