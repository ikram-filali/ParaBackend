package parapharmacie.para.entities;

import lombok.Data;

@Data
public class User {
    public Long id;
    public String nom;
    public String email;
    public String motDePasse;
}
