package org.service.restcoverage4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;


public class SwaggerParser {
    private static void saveToJsonFile(String path) {
        ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper for serialization
        try {
            // Write the calledEndpoints map to a JSON file
            objectMapper.writeValue(new FileWriter(path), Collector.swaggerEndpoints);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void parseSwagger(){
        String swaggerFilePath = "openapi/petstore.yml";

        SwaggerParseResult result = new OpenAPIParser().readLocation(swaggerFilePath, null, null);

        if (result.getOpenAPI() != null) {
            result.getOpenAPI().getPaths().forEach((path, pathItem) -> {

                pathItem.readOperationsMap().forEach((httpMethod, operation) -> {
                    Collector.swaggerEndpoints.computeIfAbsent(operation.toString(), k -> new HashSet<>()).add(path);
                });
            });
        } else {
            throw new RuntimeException("Failed to parse Swagger file: " + swaggerFilePath);
        }
        saveToJsonFile("swaggerEndpoints");
    }
}
