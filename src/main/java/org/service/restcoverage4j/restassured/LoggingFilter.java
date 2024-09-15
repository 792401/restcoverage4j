package org.service.restcoverage4j.restassured;



import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.service.restcoverage4j.Collector;
import org.service.restcoverage4j.FileUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class LoggingFilter implements Filter {
    private static void saveToJsonFile(String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new FileWriter(path), Collector.calledEndpoints);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {

        String method = requestSpec.getMethod().toUpperCase();
        String endpoint = requestSpec.getURI().replace(requestSpec.getBaseUri(), "");

        Collector.calledEndpoints.computeIfAbsent(method, k -> new HashSet<>()).add(endpoint);
        FileUtils.saveToJsonFile("calledEndpoints.json");

        return ctx.next(requestSpec, responseSpec);
    }

}
