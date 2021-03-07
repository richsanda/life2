package w.whateva.life2.api.note.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import w.whateva.life2.api.neat.dto.ApiNeatFile;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiNote {

    String trove;
    String key;
    String text;
    Integer index; // sorting, for now....
    ApiNeatFile neatFile; // eh, for now
}
