package w.whateva.life2.job.email.beans;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.w3c.tidy.Tidy;
import w.whateva.life2.job.email.util.MimeMessageUtility;
import w.whateva.life2.job.email.util.TransformsUtility;
import w.whateva.life2.xml.email.def.XmlGroupMessage;

import javax.mail.internet.MimeMessage;
import javax.xml.transform.Templates;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.*;

public class HistoricEmailFileReader
        extends AbstractItemStreamItemReader<MimeMessage>
        implements ItemReader<MimeMessage>, ResourceAwareItemReaderItemStream<MimeMessage> {

    private transient Logger log = LoggerFactory.getLogger(HistoricEmailFileReader.class);

    private Resource resource;
    private boolean read = false;

    @Override
    public MimeMessage read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        if (read) return null;
        read = true;

        InputStream inputStream = resource.getInputStream();
        ByteArrayOutputStream outputStream = MimeMessageUtility.processHistoricEmail(inputStream);

        MimeMessage message = MimeMessageUtility.buildMimeMessage(outputStream);
        inputStream.close();
        return message;
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
        this.read = false;
    }
}