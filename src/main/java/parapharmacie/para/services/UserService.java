package parapharmacie.para.services;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import parapharmacie.para.entities.Product;
import parapharmacie.para.entities.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    public final String filePath = "static/data/users.json";
    public final ObjectMapper objectMapper = new ObjectMapper();

    public List<User> getAllUsers() throws Exception {
        try {
            File jsonFile = new ClassPathResource(filePath).getFile();
            if (jsonFile.exists()) {
                System.out.println("exist");
                return objectMapper.readValue(jsonFile, new TypeReference<List<User>>() {});
            }
            System.out.println("not exist");
            return new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Optional<User> getUserByEmail(String email) throws Exception {
        return getAllUsers()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    public void addUser(User User) throws Exception {
        List<User> Users = getAllUsers();
        Users.add(User);
        objectMapper.writeValue(Paths.get(filePath).toFile(), Users);
    }
}
