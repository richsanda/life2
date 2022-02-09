package w.whateva.life2.api.note.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiNote {

    String trove;
    String key;
    String text;
    List<String> notes;
    Map<String, Object> data;
}
