package w.whateva.life2.api.neat;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import w.whateva.life2.api.neat.dto.ApiNeatFile;

import java.util.List;

@RequestMapping
public interface NeatOperations {

    @RequestMapping(value = "/neat/{folder}/{filename}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    ApiNeatFile read(@PathVariable("folder") String folder, @PathVariable("filename") String filename);

    @RequestMapping(value = "/neat/{folder}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    List<ApiNeatFile> readFolder(@PathVariable("folder") String folder);

    @RequestMapping(value = "/neat", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    List<String> listFolders();
}
