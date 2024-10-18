package org.service.restcoverage4j;

public class Main {
    public static void main(String[] args) {
        CoverageCalculator.generateCoverageReport("E:\\Projects\\restcoverage4j\\src\\main\\resources\\openapi\\petstore.yml",
                "calledEndpoints.json",
                "coverage.json");

    }
}
