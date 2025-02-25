package parapharmacie.para.entities;
import lombok.Data;
import lombok.Getter;

@Data
public class Product {
    @Getter
    public Long id;
    public String name;
    public String description;
    public Double price;
    public String imageUrl;
    public Integer stock;
    public String parentCategory;
    public String subCategory;

}

