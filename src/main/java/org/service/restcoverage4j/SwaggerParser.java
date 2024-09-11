package org.service.restcoverage4j;


import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

public class SwaggerParser {

    public static void main(String[] args) {
        SwaggerParseResult result = new OpenAPIParser().readLocation("openapi/petstore.yml", null, null);
        System.out.println(result.getOpenAPI().getPaths().keySet());
    }
}
