import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;

import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.util.ArrayList;
import java.util.List;

public class LoggingFilter implements Filter {

    private static List<LogEntry> logs = new ArrayList<>();

    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        LogEntry logEntry = new LogEntry();

        // Capture request details
        logEntry.setRequestMethod(requestSpec.getMethod());
        logEntry.setRequestUrl(requestSpec.getURI());
        logEntry.setRequestHeaders(requestSpec.getHeaders().toString());
        if (requestSpec.getBody() != null) {
            logEntry.setRequestBody(requestSpec.getBody().toString());
        }

        // Proceed with the request
        Response response = ctx.next(requestSpec, responseSpec);

        // Capture response details
        logEntry.setResponseStatus(String.valueOf(response.getStatusCode()));
        logEntry.setResponseHeaders(response.getHeaders().toString());
        logEntry.setResponseBody(response.getBody().asString());

        // Store the log entry in the list
        logs.add(logEntry);

        return response;
    }

    // Method to retrieve all logged entries
    public static List<LogEntry> getLogs() {
        return logs;
    }

    // Optional: Clear logs (e.g., between test cases)
    public static void clearLogs() {
        logs.clear();
    }
}
