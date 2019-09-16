package tests.credits;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class TestAppProperties {
    private String jdkPath;

    public TestAppProperties() {
        Properties properties = loadProperties();
        jdkPath = properties.getProperty("jdk.path");
    }

    public String getJdkPath() {
        return jdkPath;
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("settings.properties")){
            properties.load(fis);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("settings.properties is not found", e);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return properties;
    }

}
