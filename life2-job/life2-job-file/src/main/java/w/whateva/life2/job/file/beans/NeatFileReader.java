package w.whateva.life2.job.file.beans;

import com.google.common.collect.Sets;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import w.whateva.life2.api.neat.dto.ApiNeatFile;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NeatFileReader
        extends AbstractItemStreamItemReader<ApiNeatFile>
        implements ItemReader<ApiNeatFile>, ResourceAwareItemReaderItemStream<ApiNeatFile> {

    private transient Logger log = LoggerFactory.getLogger(NeatFileReader.class);

    private static final String typeRegex = "[a-zA-Z]+";
    private static final String titleRegex = "[0-9a-zA-Z\\s\\.$%\\-]*";
    private static final String numericRegex = "[0-9]+";

    private Resource resource;
    private boolean read = false;

    private Set<Resource> problems = Sets.newHashSet();

    @Override
    public ApiNeatFile read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        if (read) return null;
        read = true;

        try {

            return convertResourceIntoNeatFile();

        } catch (Exception e) {
            log.error("Total error on this one: (" + e.getMessage() + ") " + resource.getURI());
            problems.add(resource);
        }

        return null;
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
        this.read = false;
    }

    private ApiNeatFile convertResourceIntoNeatFile() throws IOException {

        String folder = resource.getFile().getParentFile().getName();
        String canonicalPath = resource.getFile().getCanonicalPath();
        String extension = Files.getFileExtension(canonicalPath);
        if (!extension.equals("jpg")) {
            log.warn("cannot handle non-jpg file: " + canonicalPath);
            return null;
        }
        String filename = Files.getNameWithoutExtension(canonicalPath);
        String patternFilename = String.format("(?<type>%s)-(?<title>%s)(_(?<index>%s))?", typeRegex, titleRegex, numericRegex);
        Matcher matcherFilename = Pattern.compile(patternFilename).matcher(filename);
        if (matcherFilename.find()) {
            String type = matcherFilename.group("type");
            String title = matcherFilename.group("title");
            String index = matcherFilename.group("index");
            String page = null;
            String patternTitle = String.format("(?<title>%s)Page (?<page>%s)?", titleRegex, numericRegex);
            Matcher matcherTitle = Pattern.compile(patternTitle).matcher(title);
            if (matcherTitle.find()) {
                title = matcherTitle.group("title");
                page = matcherTitle.group("page");
            }

            ApiNeatFile result = ApiNeatFile.builder()
                    .key(composeNeatFileKey(folder, filename, extension))
                    .folder(folder)
                    .filename(filename)
                    .extension(extension)
                    .page(!StringUtils.isEmpty(page) ? Integer.valueOf(page) : null)
                    .index(!StringUtils.isEmpty(index) ? Integer.valueOf(index) : null)
                    .type(type)
                    .title(title)
                    .build();

            if (!canonicalPath.endsWith(result.toString())) {
                log.warn(String.format("different: %s", result.toString()));
            }

            log.info(result.toString());
            return result;
        }

        log.warn("filename did not match pattern: " + canonicalPath);

        return null;
    }

    private static String composeNeatFileKey(String folder, String filename, String extension) {
        return String.format("%s/%s.%s", folder, filename, extension).toLowerCase();
    }
}