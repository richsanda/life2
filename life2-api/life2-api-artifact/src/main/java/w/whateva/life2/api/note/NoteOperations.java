package w.whateva.life2.api.note;

import org.springframework.web.bind.annotation.*;
import w.whateva.life2.api.note.dto.ApiNote;

import java.util.List;

@RequestMapping
public interface NoteOperations {

    @RequestMapping(value = "/note/{trove}/{key}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    ApiNote read(@PathVariable("trove") String trove, @PathVariable("key") String key);

    @RequestMapping(value = "/note/{trove}/{key}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    ApiNote update(@PathVariable("trove") String trove, @PathVariable("key") String key, @RequestBody ApiNote note);

    @RequestMapping(value = "/notes/{trove}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    List<ApiNote> update(@PathVariable("trove") String trove, @RequestBody List<ApiNote> note);

    @RequestMapping(value = "/notes/{trove}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    List<ApiNote> readTrove(@PathVariable("trove") String trove);

    @RequestMapping(value = "/notes", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    List<String> listTroves();

    @RequestMapping(value = "/test", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    String test();
}
