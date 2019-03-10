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
import w.whateva.life2.job.email.util.TransformsUtility;
import w.whateva.life2.xml.email.def.XmlGroupMessage;

import javax.xml.transform.Templates;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class YahoogroupMessageFileReader
        extends AbstractItemStreamItemReader<XmlGroupMessage>
        implements ItemReader<XmlGroupMessage>, ResourceAwareItemReaderItemStream<XmlGroupMessage> {

    private transient Logger log = LoggerFactory.getLogger(YahoogroupMessageFileReader.class);

    private final static String transformLocation = "/xsl/yahoogroup/xhtmlToXml.xslt";
    private final static Templates templates = TransformsUtility.getTransformTemplates(transformLocation);

    private final static String prefix = "<!DOCTYPE some_name [ \n" +
            "<!ENTITY nbsp \"&#160;\"> \n" +
            "]> ";

    private final static String UTF_8 = "UTF-8";
    private final static String ISO_8859_1 = "ISO-8859-1";

    private final Jaxb2Marshaller marshaller;
    private Resource resource;
    private boolean read = false;

    private Set<Resource> problems = Sets.newHashSet();

    public YahoogroupMessageFileReader(Jaxb2Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    @Override
    public XmlGroupMessage read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        assert null != templates;

        if (read) return null;
        read = true;

        try {

            InputStream is = resource.getInputStream();
            String xml = htmlToXhtml(IOUtils.toString(is));
            is.close();

            StreamSource source = new StreamSource(new StringReader(xml));
            StringWriter outWriter = new StringWriter();
            StreamResult result = new StreamResult(outWriter);
            TransformsUtility.transform(source, result, templates, makeParams(resource.getFilename()));
            StringBuffer sb = outWriter.getBuffer();
            String out = sb.toString();

            if (!StringUtils.isEmpty(out)) {
                return (XmlGroupMessage) marshaller.unmarshal(new StreamSource(new StringReader(out)));
            } else {
                log.error("Could not transform this one: " + resource.getURI());
                problems.add(resource);
            }

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

    private Map<String, String> makeParams(String filename) {
        Map<String, String> result = Maps.newHashMap();
        result.put("yahoogroup", "<groupname>");
        result.put("filename", filename);
        return result;
    }

    private String convertToXhtml(InputStream is) {

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        Tidy tidy = new Tidy();
        tidy.setInputEncoding(ISO_8859_1);
        tidy.setOutputEncoding(UTF_8);
        tidy.setXmlOut(true);
        //tidy.setXHTML(true);
        tidy.setQuoteAmpersand(true);
        tidy.setSmartIndent(true);
        tidy.setShowErrors(0);
        tidy.setDocType("omit");
        tidy.setQuoteNbsp(false);
        tidy.setForceOutput(true);
        tidy.parseDOM(is, os);

        try {
            return os.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private String htmlToXhtml(final String html) {
        final Document document = Jsoup.parse(html);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        document.select("script").remove();
        // document.select("#comment").remove();
        removeComments(document);
        List<Node> nodes = document.childNodes();
        for (Node node : nodes) {
            if (node.nodeName().equals("#doctype")) {
                node.remove();
            }
        }
        return prefix + document.html();
    }

    private static void removeComments(Node node) {
        for (int i = 0; i < node.childNodeSize();) {
            Node child = node.childNode(i);
            if (child.nodeName().equals("#comment"))
                child.remove();
            else {
                removeComments(child);
                i++;
            }
        }
    }
}