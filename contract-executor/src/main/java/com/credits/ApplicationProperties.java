package com.credits;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

public class ApplicationProperties {

    public static final short API_VERSION = 2;

    public int executorPort = 9080;
    public String nodeApiHost = "localhost";
    public int nodeApiPort = 9070;
    public int readClientTimeout;
    public String tag;
    public String commitId;
    public String buildVersion;

    public ApplicationProperties() {
        Locale.setDefault(Locale.US);
        final var properties = new Properties();

        readSettingProperties(properties);
        readGitProperties(properties);
    }

    private void readSettingProperties(Properties properties) {
        try (FileInputStream fis = new FileInputStream("settings.properties")) {
            properties.load(fis);
            nodeApiHost = properties.getProperty("contract.executor.node.api.host");
            nodeApiPort = Integer.parseInt(properties.getProperty("contract.executor.node.api.port"));
            executorPort = Integer.parseInt(properties.getProperty("contract.executor.port"));
            readClientTimeout = Integer.parseInt(properties.getProperty("contract.executor.read.client.timeout"));
        } catch (IOException e) {
            throw new RuntimeException("can't load propertyFile", e);
        }
    }

    private void readGitProperties(Properties properties) {
        final var url = getClass().getResource("/git.properties");
        if (url != null) {
            try (final var is = url.openStream()) {
                properties.load(is);
                tag = (String) properties.get("git.closest.tag.name");
                buildVersion = (String) properties.get("git.closest.tag.commit.count");
                commitId = (String) properties.get("git.commit.id");
            } catch (IOException e) {
                throw new RuntimeException("can't load git.properties", e);
            }
        }
    }
}
