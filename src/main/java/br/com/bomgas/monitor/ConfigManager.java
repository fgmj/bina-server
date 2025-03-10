package br.com.bomgas.monitor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class ConfigManager {
    private static final String FILE_PATH = "settings.properties";
    private static final Properties properties = new Properties();

    static {
        try (FileInputStream in = new FileInputStream(FILE_PATH)) {
            properties.load(in);
        } catch (Exception e) {
            System.out.println("⚠️ Arquivo de configurações não encontrado.");
        }
    }

    public static void set(String key, String value) {
        try (FileOutputStream out = new FileOutputStream(FILE_PATH)) {
            properties.setProperty(key, value);
            properties.store(out, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
