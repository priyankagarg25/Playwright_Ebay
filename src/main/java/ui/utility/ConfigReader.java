package ui.utility;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class ConfigReader {


    public static final String url = getPropertyValue("url");
    public static final String username = getPropertyValue("username");
    public static final String password = getPropertyValue("password");
    public static final String consolelog = getPropertyValue("consolelog");
    public static final String headless = getPropertyValue("headless");
    public static final String browser = getPropertyValue("browser");
    public static final int timeout = Integer.parseInt(getPropertyValue("timeout"));


    public static final String URL = System.getProperty("url");
    static Properties prop;
    static String propertyValue;

    public static String getPropertyValue(String propertyName) {
        prop = accessPropertiesFile();

        try {
            propertyValue = prop.getProperty(propertyName);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return propertyValue;
    }

    public static Properties accessPropertiesFile() {
        prop = new Properties();

        // try retrieve data from file
        try {

            prop.load(new FileInputStream("src/test/resources/config/config.properties"));
        }
        // catch exception in case properties file does not exist
        catch (IOException e) {
            log.info(e.toString());
        }

        return prop;
    }

    public static String getSysPropertyValue(String property) {
        String propertyNew = System.getProperty(property);

        if (propertyNew == null || propertyNew.isEmpty()) {
            propertyNew = getPropertyValue(property);
        }
        return propertyNew;

    }

    private Properties properties;

    public ConfigReader() {
        properties = new Properties();
        try (InputStream input = new FileInputStream("src/test/resources/config/config.properties")) {
            // Load the properties file
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public int getIntProperty(String key) {
        String value = properties.getProperty(key);
        return value != null ? Integer.parseInt(value) : 0;
    }
}


