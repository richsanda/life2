package w.whateva.life2.service.artifact.impl;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.GenericWebApplicationContext;
import w.whateva.life2.api.common.ArtifactOperations;
import w.whateva.life2.api.common.dto.ApiArtifact;
import w.whateva.life2.api.email.EmailOperations;
import w.whateva.life2.integration.api.ArtifactProvider;
import w.whateva.life2.integration.email.impl.EmailProviderImpl;
import w.whateva.life2.service.artifact.util.ArtifactUtility;
import w.whateva.life2.service.artifact.util.bucket.AbstractLocalDateTimeOperator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Primary
@Service
@EnableFeignClients(basePackages = "w.whateva.life2.integration")
public class ArtifactServiceImpl implements ArtifactOperations {

    private final ArtifactUtility artifactUtility;

    private final Collection<ArtifactProvider> providers = Lists.newArrayList();

    @Autowired
    public ArtifactServiceImpl(GenericWebApplicationContext context, ArtifactUtility artifactUtility) {

        this.artifactUtility = artifactUtility;

        // dynamically register an artifact provider for each email client (EmailOperations extensions are FeignClient)
        int i = 0;
        for (EmailOperations client : context.getBeansOfType(EmailOperations.class).values()) {
            context.registerBean("emailProvider" + i++, ArtifactProvider.class, () -> new EmailProviderImpl(client));
        }

        providers.addAll(context.getBeansOfType(ArtifactProvider.class).values());
    }

    @Override
    public ApiArtifact read(String key) {
        return null;
    }

    @Override
    public List<ApiArtifact> search(LocalDate after, LocalDate before, HashSet<String> names) {
        return providers
                .parallelStream()
                .map(p -> search(p, after, before, names))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<ApiArtifact> search(ArtifactProvider provider, LocalDate after, LocalDate before, HashSet<String> names) {
        try {
            return provider.search(after, before, names);
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    public List<List<ApiArtifact>> search(LocalDate after, LocalDate before, HashSet<String> names, Integer numBuckets) {

        List<List<ApiArtifact>> buckets = artifactUtility.putInBuckets(
                search(after, before, names),
                new AbstractLocalDateTimeOperator<ApiArtifact>() {
                    @Override
                    public LocalDateTime apply(ApiArtifact artifact) {
                        return artifact.getSent();
                    }
                },
                numBuckets,
                after.atStartOfDay(),
                before.plusDays(1).atStartOfDay()
        );
        return buckets
                .stream()
                .map(ArrayList::new)
                .collect(Collectors.toList());
    }
}
