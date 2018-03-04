package w.whateva.service.life2.service.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import w.whateva.service.life2.api.PieceOperations;
import w.whateva.service.life2.api.dto.DtoPiece;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

/**
 *
 */
@Primary
@Service
public class PieceServiceImpl implements PieceOperations {

    @Override
    public List<String> allKeys() {
        return null;
    }

    @Override
    public void addPiece(DtoPiece piece) {

    }

    @Override
    public DtoPiece readPiece(String key) {
        return null;
    }

    @Override
    public List<DtoPiece> allPieces() {
        return null;
    }

    @Override
    public List<DtoPiece> allPieces(LocalDate after, LocalDate before, HashSet<String> names) {
        return null;
    }
}
