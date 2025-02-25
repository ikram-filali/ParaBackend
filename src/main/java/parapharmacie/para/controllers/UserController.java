package parapharmacie.para.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import parapharmacie.para.entities.User;
import parapharmacie.para.services.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    public final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la récupération des Users.");
        }
    }

    @GetMapping("/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        try {
            Optional<User> user = userService.getUserByEmail(email);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.status(404).body("Utilisateur non trouvé.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la récupération de l'User.");
        }
    }


    @PostMapping
    public ResponseEntity<?> ajouterUser(@RequestBody User User) {
        try {
            userService.addUser(User);
            return ResponseEntity.status(201).body("User ajouté avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de l'ajout de l'User.");
        }
    }
}
