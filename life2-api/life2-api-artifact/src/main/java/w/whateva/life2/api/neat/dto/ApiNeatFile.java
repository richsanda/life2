package w.whateva.life2.api.neat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiNeatFile {

    private String key;
    private String folder;
    private String filename;
    private String extension;
    private Integer index;
    private Integer page;
    private String title;
    private String type;

    @Override
    public String toString() {
        return String.format("%s/%s-%s%s%s.jpg",
                folder,
                type,
                title,
                null != page ? "Page " + page : "",
                null != index ? "_" + index : "");
    }
}
