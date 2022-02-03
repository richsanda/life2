package w.whateva.life2.api.trove;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import w.whateva.life2.api.trove.dto.ApiTrove;

import java.util.List;

@RequestMapping
public interface TroveOperations {

    @RequestMapping(value = "/troves", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    List<ApiTrove> allTroves();
}
