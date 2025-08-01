package sesac.bookmanager.wish.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishSearchRequest {

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 10;
}
