package parapharmacie.para.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileChangeEvent {
    public String filePath;
    public String type; // "PRODUCT" ou "IMAGE"
    public String action; // "MODIFIED", "CREATED", "DELETED"
    public long timestamp;
}