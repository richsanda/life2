package w.whateva.life2.job.email;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.junit.Test;
import org.w3c.tidy.Tidy;
import w.whateva.life2.job.email.util.TransformsUtility;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class YahooGroupTest {

    private final static String transformLocation = "/xsl/yahoogroup/xhtmlToXml.xslt";
    private final static String prefix = "<!DOCTYPE some_name [ \n" +
            "<!ENTITY nbsp \"&#160;\"> \n" +
            "]> ";

    @Test
    public void xhtmlParseTest() throws Exception {

        String in = readFile("/Volumes/20200915/rich-20200910/rich/life2/data/yahoogroups/raw/bbjones/11268.html", Charset.forName("ISO-8859-1"));

        String out = htmlToXhtml(in);

        // FileInputStream is = new FileInputStream(new File("/Volumes/20200915/rich-20200910/rich/life2/data/yahoogroups/raw/bbjones/10533.html"));
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        String xhtmlFileName = "/tmp/tmp.xhtml";
        String xmlFileName = "/tmp/tmp.xml";

        try (PrintStream ps = new PrintStream(new FileOutputStream(xhtmlFileName))) {
            ps.print(out);
        }
        /*
        try {

            Tidy tidy = new Tidy();
            tidy.setInputEncoding("ISO-8859-1");
            tidy.setOutputEncoding("UTF-8");
            // tidy.setPrintBodyOnly(true); // only print the content
            tidy.setXmlOut(true); // to XML
            tidy.setSmartIndent(true);
            // tidy.setMakeClean(true);
            tidy.setShowErrors(0);
            tidy.setDocType("omit");
            // tidy.setXHTML(true);
            tidy.setQuoteNbsp(false);
            tidy.setForceOutput(true);
            tidy.parseDOM(is, os);

        } finally {
            try {
                String xml = os.toString("UTF-8");
                BufferedWriter writer = new BufferedWriter(new FileWriter(xhtmlFileName));
                writer.write(xml);
                os.close();
                writer.close();
            } catch (IOException e) {
                os = null;
            }
            os = null;
            try {
                is.close();
            } catch (IOException e) {
                is = null;
            }
            is = null;
        }
        */
        StringWriter outWriter = new StringWriter();
        StreamResult result = new StreamResult(outWriter);

        TransformsUtility.transform(new StreamSource(new StringReader(out)), result, TransformsUtility.getTransformTemplates(transformLocation), null);

        StringBuffer sb = outWriter.getBuffer();
        String xml = sb.toString();

        System.out.println(xml);
    }

    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private String htmlToXhtml(final String html) {
        final Document document = Jsoup.parse(html);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        document.select("script").remove();
        //document.select("#comment").remove();
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
