package parapharmacie.para.entities;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.List;

@Data
public class Category {
    public Long id;
    public String nom;
    public String description;
    public List<Category> subCategories;
    private List<String> subItems;
}

