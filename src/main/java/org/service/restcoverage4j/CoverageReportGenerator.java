package org.service.restcoverage4j;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.*;

public class CoverageReportGenerator {

    public static void generateHtmlReport(String coverageJsonPath, String outputHtmlPath) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {

            CoverageReport coverageReport = objectMapper.readValue(new File(coverageJsonPath), CoverageReport.class);

            StringBuilder htmlContent = new StringBuilder();

            htmlContent.append("<!DOCTYPE html>\n<html>\n<head>\n");
            htmlContent.append("<title>API Coverage Report</title>\n");
            htmlContent.append("<style>\n");
            htmlContent.append("body { font-family: Arial, sans-serif; }\n");
            htmlContent.append(".endpoint { margin: 10px 0; }\n");
            htmlContent.append(".endpoint .methods { display: none; margin-left: 20px; }\n");
            htmlContent.append(".endpoint.green > .endpoint-name { color: green; }\n");
            htmlContent.append(".endpoint.red > .endpoint-name { color: red; }\n");
            htmlContent.append(".method { margin-left: 20px; cursor: pointer; }\n");
            htmlContent.append(".method.green { color: green; }\n");
            htmlContent.append(".method.red { color: red; }\n");
            htmlContent.append(".details { margin-left: 40px; display: none; }\n");
            htmlContent.append("</style>\n");
            htmlContent.append("<script>\n");
            htmlContent.append("function toggleDisplay(id) {\n");
            htmlContent.append("  var element = document.getElementById(id);\n");
            htmlContent.append("  if (element.style.display === 'none' || element.style.display === '') {\n");
            htmlContent.append("    element.style.display = 'block';\n");
            htmlContent.append("  } else {\n");
            htmlContent.append("    element.style.display = 'none';\n");
            htmlContent.append("  }\n");
            htmlContent.append("}\n");
            htmlContent.append("</script>\n");
            htmlContent.append("</head>\n<body>\n");
            htmlContent.append("<h1>API Coverage Report</h1>\n");

            Map<String, CoverageEntry> endpoints = coverageReport.getCalled();

            for (Map.Entry<String, CoverageEntry> endpointEntry : endpoints.entrySet()) {
                String endpoint = endpointEntry.getKey();
                CoverageEntry coverageEntry = endpointEntry.getValue();

                boolean allMethodsCovered = !coverageEntry.getCalled().isEmpty() && coverageEntry.getNotCalled().isEmpty();
                String endpointColorClass = allMethodsCovered ? "green" : "red";

                String endpointId = "endpoint_" + endpoint.hashCode();

                htmlContent.append("<div class='endpoint " + endpointColorClass + "'>\n");
                htmlContent.append("<div class='endpoint-name' onclick=\"toggleDisplay('" + endpointId + "')\" style='cursor:pointer;'>");
                htmlContent.append(endpoint);
                htmlContent.append("</div>\n");
                htmlContent.append("<div class='methods' id='" + endpointId + "'>\n");

                for (Map.Entry<String, Details> methodEntry : coverageEntry.getCalled().entrySet()) {
                    String method = methodEntry.getKey();
                    Details details = methodEntry.getValue();

                    String methodId = endpointId + "_method_" + method.hashCode();

                    htmlContent.append("<div class='method green' onclick=\"toggleDisplay('" + methodId + "')\">");
                    htmlContent.append(method);
                    htmlContent.append("</div>\n");
                    htmlContent.append("<div class='details' id='" + methodId + "'>\n");

                    htmlContent.append("<p><strong>Headers:</strong> " + listToString(details.getHeaders()) + "</p>\n");
                    htmlContent.append("<p><strong>Body Fields:</strong> " + listToString(details.getBodyFields()) + "</p>\n");
                    htmlContent.append("<p><strong>URL Parameters:</strong> " + listToString(details.getUrlParameters()) + "</p>\n");

                    htmlContent.append("</div>\n");
                }

                for (Map.Entry<String, Details> methodEntry : coverageEntry.getNotCalled().entrySet()) {
                    String method = methodEntry.getKey();
                    Details details = methodEntry.getValue();

                    String methodId = endpointId + "_method_" + method.hashCode();

                    htmlContent.append("<div class='method red' onclick=\"toggleDisplay('" + methodId + "')\">");
                    htmlContent.append(method);
                    htmlContent.append("</div>\n");
                    htmlContent.append("<div class='details' id='" + methodId + "'>\n");


                    htmlContent.append("<p><strong>Headers:</strong> " + listToString(details.getHeaders()) + "</p>\n");
                    htmlContent.append("<p><strong>Body Fields:</strong> " + listToString(details.getBodyFields()) + "</p>\n");
                    htmlContent.append("<p><strong>URL Parameters:</strong> " + listToString(details.getUrlParameters()) + "</p>\n");

                    htmlContent.append("</div>\n");
                }

                htmlContent.append("</div>\n");
                htmlContent.append("</div>\n");
            }

            htmlContent.append("</body>\n</html>");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputHtmlPath))) {
                writer.write(htmlContent.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String listToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "None";
        }
        return String.join(", ", list);
    }
}
