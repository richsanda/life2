package w.whateva.service.life2.service.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import w.whateva.service.life2.api.ShredOperations;
import w.whateva.service.life2.api.dto.DtoShred;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

/**
 *
 */
@Primary
@Service
public class ShredServiceImpl implements ShredOperations {

    @Override
    public List<String> allKeys() {
        return null;
    }

    @Override
    public void addShred(DtoShred shred) {

    }

    @Override
    public DtoShred readShred(String key) {
        return null;
    }

    @Override
    public List<DtoShred> allShreds() {
        return null;
    }

    @Override
    public List<DtoShred> allShreds(LocalDate after, LocalDate before, HashSet<String> names) {
        return null;
    }
}
