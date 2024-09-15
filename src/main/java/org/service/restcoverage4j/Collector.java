package org.service.restcoverage4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Collector {

    public static Map<String, Set<String>> calledEndpoints = new HashMap<>();
    public static Map<String, Set<String>> swaggerEndpoints = new HashMap<>();
}
