package w.whateva.service.life2.api;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import w.whateva.service.life2.api.dto.DtoPiece;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@RequestMapping
public interface PieceOperations {

    @RequestMapping(value = "/keys", method = RequestMethod.GET, produces = "application/json")
    List<String> allKeys();

    void addPiece(DtoPiece piece);

    @RequestMapping(value = "/piece/{key}", method = RequestMethod.GET, produces = "application/json")
    DtoPiece readPiece(@PathVariable("key") String key);

    @RequestMapping(value = "/pieces", method = RequestMethod.GET, produces = "application/json")
    List<DtoPiece> allPieces();

    @RequestMapping(value = "/person/pieces", method = RequestMethod.GET, produces = "application/json")
    List<DtoPiece> allPieces(@RequestParam(value = "after", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate after,
                             @RequestParam(value = "before", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate before,
                             @RequestParam(value = "names", required = false) HashSet<String> names);
}