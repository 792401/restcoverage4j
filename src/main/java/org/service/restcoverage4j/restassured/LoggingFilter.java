package org.service.restcoverage4j.restassured;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.service.restcoverage4j.Collector;
import org.service.restcoverage4j.CollectorData;
import org.service.restcoverage4j.EndpointCall;
import org.service.restcoverage4j.FileUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class LoggingFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {

        CollectorData collectorData = Collector.getData();

        String method = requestSpec.getMethod() != null ? requestSpec.getMethod().toUpperCase() : "UNKNOWN";

        try {
            String requestUri = requestSpec.getURI() != null ? requestSpec.getURI() : "";
            URI uriObj = new URI(requestUri);
            String endpoint = uriObj.getPath();

            String baseUri = requestSpec.getBaseUri() != null ? requestSpec.getBaseUri() : "";
            URI baseUriObj = new URI(baseUri);
            String basePath = baseUriObj.getPath();

            if (endpoint.startsWith(basePath)) {
                endpoint = endpoint.substring(basePath.length());
                if (endpoint.isEmpty()) {
                    endpoint = "/";
                }
            }

            if (collectorData.getDateCollected() == null) {
                collectorData.setDateCollected(String.valueOf(System.currentTimeMillis()));
                collectorData.setHost(baseUriObj.getScheme() + "://" + baseUriObj.getHost());
            }

            Map<String, Map<String, EndpointCall>> paths = collectorData.getPaths();
            Map<String, EndpointCall> methodsMap = paths.computeIfAbsent(endpoint, k -> new HashMap<>());


            Map<String, String> headers = new HashMap<>();
            if (requestSpec.getHeaders() != null && requestSpec.getHeaders().asList() != null) {
                requestSpec.getHeaders().asList().forEach(header -> {
                    if (header != null && header.getName() != null && header.getValue() != null) {
                        headers.put(header.getName(), header.getValue());
                    }
                });
            }


            Object body = requestSpec.getBody();


            Map<String, String> urlParams = requestSpec.getQueryParams() != null ? requestSpec.getQueryParams() : new HashMap<>();

            EndpointCall endpointCall = new EndpointCall();
            endpointCall.setHeaders(headers);
            endpointCall.setBody(body);
            endpointCall.setUrlParameters(urlParams);

            methodsMap.put(method, endpointCall);


            FileUtils.saveToJsonFile("calledEndpoints.json");

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return ctx.next(requestSpec, responseSpec);
    }
}
