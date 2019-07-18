package com.credits;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ApplicationProperties {

    public static final short APP_VERSION = 2;

    public int executorPort = 9080;
    public String nodeApiHost = "localhost";
    public int nodeApiPort = 9070;

    public ApplicationProperties(){
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("settings.properties")) {
            properties.load(fis);
            nodeApiHost = properties.getProperty("contract.executor.node.api.host");
            nodeApiPort = Integer.parseInt(properties.getProperty("contract.executor.node.api.port"));
            executorPort = Integer.parseInt(properties.getProperty("contract.executor.port"));
        } catch (IOException e) {
            throw new RuntimeException("can't load propertyFile", e);
        }

    }
}
