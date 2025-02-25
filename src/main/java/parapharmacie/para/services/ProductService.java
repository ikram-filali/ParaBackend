package parapharmacie.para.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import parapharmacie.para.entities.Product;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    public final String JSON_FILE_PATH = "static/data/products.json";
    public final String IMAGE_UPLOAD_PATH = "static/images/products/";
    public final ObjectMapper objectMapper = new ObjectMapper();

    public List<Product> getAllProducts() {
        try {
            File jsonFile = new ClassPathResource(JSON_FILE_PATH).getFile();
            if (jsonFile.exists()) {
                System.out.println("exist");
                return objectMapper.readValue(jsonFile, new TypeReference<List<Product>>() {});
            }
            System.out.println("not exist");
            return new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Product saveProduct(Product product, MultipartFile imageFile) {
        try {
            List<Product> products = getAllProducts();

            // Gérer l'ID
            Long id = product.getId();
            if (id == null) {
                id = products.stream()
                        .mapToLong(Product::getId)
                        .max()
                        .orElse(0L) + 1;
                product.setId(id);
            }

            // Gérer l'image
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = id + "_" + imageFile.getOriginalFilename();
                Path imagePath = Paths.get(new ClassPathResource(IMAGE_UPLOAD_PATH).getURI()).resolve(fileName);
                Files.copy(imageFile.getInputStream(), imagePath);
                product.setImageUrl("/images/products/" + fileName);
            }

            // Mettre à jour ou ajouter le produit
            boolean found = false;
            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).getId().equals(product.getId())) {
                    products.set(i, product);
                    found = true;
                    break;
                }
            }
            if (!found) {
                products.add(product);
            }

            // Sauvegarder dans le fichier JSON
            objectMapper.writeValue(new ClassPathResource(JSON_FILE_PATH).getFile(), products);
            return product;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving product", e);
        }
    }

    public void deleteProduct(Long id) {
        try {
            List<Product> products = getAllProducts();
            products.removeIf(p -> p.getId().equals(id));
            objectMapper.writeValue(new ClassPathResource(JSON_FILE_PATH).getFile(), products);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting product", e);
        }
    }
}