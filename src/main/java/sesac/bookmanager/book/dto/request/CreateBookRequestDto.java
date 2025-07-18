package sesac.bookmanager.book.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookRequestDto {
    private String title;
    private String author;
    private String publisher;
    private LocalDate publishedAt;
    private String location;
    private int stock;
    private Boolean isAvailable;
    private String cover;
    private MultipartFile coverFile;
    private String isbn;
    private String category;
    private String description = "";

}
