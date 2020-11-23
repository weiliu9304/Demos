package demos.web;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class Waggle {
    @Id
    private @NonNull Integer id;
    private @NonNull String name;
}
