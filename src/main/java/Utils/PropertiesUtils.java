package Utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

public class PropertiesUtils {

    private PropertiesUtils() {
        super();
    }

    public final static String PROPERTIES_PATH = "src/main/resources/";

    public static Properties loadProperties() {
        try {
            Properties properties = new Properties();
            Collection<File> propertiesFilesList;
            propertiesFilesList = FileUtils.listFiles(new File(PROPERTIES_PATH), new String[]{"properties"}, true);
            propertiesFilesList.forEach(propertyFile -> {
                try {
                    properties.load(new FileInputStream(propertyFile));
                } catch (IOException ioe) {
                    LogsUtil.error(ioe.getMessage());
                }
                properties.putAll(System.getProperties());
                System.getProperties().putAll(properties);
            });
            LogsUtil.info("Loading Properties File Data");
            return properties;
        } catch (Exception e) {
            LogsUtil.error("Failed to Load Properties File Data because: " + e.getMessage());
            return null;
        }
    }

    // Get the value of the property
    public static String getPropertyValue(String key) {

        try {
            return System.getProperty(key);
        } catch (Exception e) {
            LogsUtil.error(e.getMessage());
            return "";
        }
    }
}
