package ua.javarush.util.loaders;

import com.fasterxml.jackson.databind.ObjectMapper;
import ua.javarush.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

public class JsonConfigLoader {
    public <T> T getObject(Class<T> clazz){
        validateConfigurationAnnotation(clazz);

        String filePath = getConfigurationFilePath(clazz);
        return initializeObject(filePath, clazz);
    }

    private <T> T initializeObject(String filePath, Class<T> clazz) {
        try (InputStream inputStream = JsonConfigLoader.class.getClassLoader().getResourceAsStream(filePath)){
            return new ObjectMapper().readValue(inputStream, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Error loading object from config file", e);
        }
    }

    private String getConfigurationFilePath(Class<?> clazz) {
        Configuration annotation = clazz.getAnnotation(Configuration.class);
        return annotation.path();
    }

    private void validateConfigurationAnnotation(Class<?> clazz) {
        if(!clazz.isAnnotationPresent(Configuration.class)){
            throw new IllegalArgumentException("Class must be annotated with @Configuration");
        }
    }

    
}
