package parapharmacie.para.services;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import parapharmacie.para.entities.Category;
import parapharmacie.para.entities.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    public final String filePath = "static/data/categories.json";
    public final ObjectMapper objectMapper = new ObjectMapper();

    public List<Category> getAllCategories() throws Exception {
        try {
            File jsonFile = new ClassPathResource(filePath).getFile();
            if (jsonFile.exists()) {
                System.out.println("exist");
                return objectMapper.readValue(jsonFile, new TypeReference<List<Category>>() {});
            }
            System.out.println("not exist");
            return new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
