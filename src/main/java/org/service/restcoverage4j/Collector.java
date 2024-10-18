package org.service.restcoverage4j;

public class Collector {
    public static CollectorData data = new CollectorData();

    public static CollectorData getData() {
        if (data == null) {
            data = new CollectorData();
        }
        return data;
    }
}
