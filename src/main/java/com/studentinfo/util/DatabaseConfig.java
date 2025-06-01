package com.studentinfo.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static Properties properties = new Properties();
    private static final String CONFIG_FILE_PATH = "/app.config"; // Path in classpath

    static {
        try (InputStream input = DatabaseConfig.class.getResourceAsStream(CONFIG_FILE_PATH)) {
            if (input == null) {
                String errorMessage = "ERROR: Could not find database configuration file " + CONFIG_FILE_PATH + " in classpath. Ensure 'app.config' is in the src/main/resources directory.";
                System.err.println(errorMessage);
                throw new RuntimeException(errorMessage);
            }
            properties.load(input);
        } catch (IOException ex) {
            String errorMessage = "ERROR: Could not load database configuration from " + CONFIG_FILE_PATH;
            System.err.println(errorMessage);
            ex.printStackTrace();
            throw new RuntimeException(errorMessage, ex);
        }
    }

    public static String getDbUrl() {
        return properties.getProperty("db.url");
    }

    public static String getDbUsername() {
        return properties.getProperty("db.username");
    }

    public static String getDbPassword() {
        return properties.getProperty("db.password");
    }
} 