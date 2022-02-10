package w.whateva.life2.api.artifact;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import w.whateva.life2.api.artifact.dto.ApiTag;
import w.whateva.life2.api.artifact.dto.ApiTrove;

import java.util.List;

@RequestMapping
public interface DataOperations {

    @RequestMapping(value = "/troves", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    List<ApiTrove> allTroves();

    @RequestMapping(value = "/tags", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    List<ApiTag> allTags();
}
