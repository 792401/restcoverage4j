package org.service.restcoverage4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CoverageCalculator {
    public static void generateCoverageReport(String swaggerPath, String calledEndpointsPath, String coverageOutputPath) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            CollectorData calledData = objectMapper.readValue(new File(calledEndpointsPath), CollectorData.class);

            OpenAPI openAPI = new OpenAPIV3Parser().read(swaggerPath);

            CoverageReport coverageReport = new CoverageReport();
            coverageReport.setDateGenerated(String.valueOf(System.currentTimeMillis()));
            coverageReport.setHost(calledData.getHost());
            coverageReport.setCalled(new HashMap<>());

            Map<String, Map<String, EndpointDefinition>> swaggerPaths = new HashMap<>();

            openAPI.getPaths().forEach((path, pathItem) -> {
                Map<String, EndpointDefinition> methodsMap = new HashMap<>();

                if (pathItem.getGet() != null) methodsMap.put("GET", new EndpointDefinition(pathItem.getGet()));
                if (pathItem.getPost() != null) methodsMap.put("POST", new EndpointDefinition(pathItem.getPost()));
                if (pathItem.getPut() != null) methodsMap.put("PUT", new EndpointDefinition(pathItem.getPut()));
                if (pathItem.getDelete() != null) methodsMap.put("DELETE", new EndpointDefinition(pathItem.getDelete()));
                if (pathItem.getOptions() != null) methodsMap.put("OPTIONS", new EndpointDefinition(pathItem.getOptions()));
                if (pathItem.getHead() != null) methodsMap.put("HEAD", new EndpointDefinition(pathItem.getHead()));
                if (pathItem.getPatch() != null) methodsMap.put("PATCH", new EndpointDefinition(pathItem.getPatch()));
                if (pathItem.getTrace() != null) methodsMap.put("TRACE", new EndpointDefinition(pathItem.getTrace()));

                swaggerPaths.put(path, methodsMap);
            });

            Map<String, CoverageEntry> coverageEntries = new HashMap<>();

            for (Map.Entry<String, Map<String, EndpointCall>> calledPathEntry : calledData.getPaths().entrySet()) {
                String calledEndpoint = calledPathEntry.getKey();
                Map<String, EndpointCall> calledMethods = calledPathEntry.getValue();

                String matchingSwaggerPath = findMatchingSwaggerPath(calledEndpoint, swaggerPaths.keySet());

                if (matchingSwaggerPath != null) {
                    Map<String, EndpointDefinition> swaggerMethods = swaggerPaths.get(matchingSwaggerPath);

                    CoverageEntry coverageEntry = coverageEntries.computeIfAbsent(matchingSwaggerPath, k -> new CoverageEntry());

                    Map<String, Details> calledMethodsMap = coverageEntry.getCalled();
                    Map<String, Details> notCalledMethodsMap = coverageEntry.getNotCalled();

                    for (String method : swaggerMethods.keySet()) {
                        if (calledMethods.containsKey(method)) {
                            EndpointCall endpointCall = calledMethods.get(method);
                            Details details = new Details();
                            details.setHeaders(new ArrayList<>(endpointCall.getHeaders().keySet()));
                            details.setBodyFields(extractBodyFields(endpointCall.getBody()));
                            details.setUrlParameters(new ArrayList<>(endpointCall.getUrlParameters().keySet()));
                            calledMethodsMap.put(method, details);
                        } else {
                            if (!notCalledMethodsMap.containsKey(method)) {
                                notCalledMethodsMap.put(method, new Details());
                            }
                        }
                    }
                } else {

                    // Handle
                }
            }

            for (String swaggerPathKey : swaggerPaths.keySet()) {
                Map<String, EndpointDefinition> swaggerMethods = swaggerPaths.get(swaggerPathKey);

                CoverageEntry coverageEntry = coverageEntries.computeIfAbsent(swaggerPathKey, k -> new CoverageEntry());
                Map<String, Details> calledMethodsMap = coverageEntry.getCalled();
                Map<String, Details> notCalledMethodsMap = coverageEntry.getNotCalled();

                for (String method : swaggerMethods.keySet()) {
                    if (!calledMethodsMap.containsKey(method) && !notCalledMethodsMap.containsKey(method)) {
                        notCalledMethodsMap.put(method, new Details());
                    }
                }
            }

            coverageReport.setCalled(coverageEntries);

            objectMapper.writeValue(new File(coverageOutputPath), coverageReport);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String findMatchingSwaggerPath(String calledEndpoint, Set<String> swaggerPaths) {
        for (String swaggerPath : swaggerPaths) {
            if (pathsMatch(calledEndpoint, swaggerPath)) {
                return swaggerPath;
            }
        }
        return null;
    }

    private static boolean pathsMatch(String calledEndpoint, String swaggerPath) {
        String[] calledParts = calledEndpoint.split("/");
        String[] swaggerParts = swaggerPath.split("/");


        int calledStart = calledParts.length > 0 && calledParts[0].isEmpty() ? 1 : 0;
        int swaggerStart = swaggerParts.length > 0 && swaggerParts[0].isEmpty() ? 1 : 0;

        if ((calledParts.length - calledStart) != (swaggerParts.length - swaggerStart)) {
            return false;
        }

        for (int i = 0; i < calledParts.length - calledStart; i++) {
            String calledPart = calledParts[i + calledStart];
            String swaggerPart = swaggerParts[i + swaggerStart];

            if (swaggerPart.startsWith("{") && swaggerPart.endsWith("}")) {

                continue;
            }
            if (!calledPart.equals(swaggerPart)) {
                return false;
            }
        }
        return true;
    }

    private static List<String> extractBodyFields(Object body) {
        if (body == null) {
            return null;
        }
        List<String> fields = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> bodyMap;
            if (body instanceof String) {
                bodyMap = objectMapper.readValue((String) body, Map.class);
            } else if (body instanceof Map) {
                bodyMap = (Map<String, Object>) body;
            } else {
                return null;
            }
            fields.addAll(bodyMap.keySet());
            return fields;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
