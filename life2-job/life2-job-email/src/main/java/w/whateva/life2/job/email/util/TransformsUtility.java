package w.whateva.life2.job.email.util;

import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import w.whateva.life2.job.email.beans.YahoogroupMessageFileReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Map;

/**
 * Created by rich on 11/26/17.
 */
public class TransformsUtility {

    private static final Class<?> thisClass = TransformsUtility.class;

    private static transient Logger log = LoggerFactory.getLogger(thisClass);

    private static final String SAXON_TRANSFORMER_FACTORY_NAME = "net.sf.saxon.TransformerFactoryImpl";
    private static final TransformerFactory factory = createFactory();

    private static InputStream getTransformAsStream(String resource) {
        return thisClass.getResourceAsStream(resource);
    }

    public static Templates getTransformTemplates(String transformLocation) {
        InputStream transformInputStream = getTransformAsStream(transformLocation);
        StreamSource transformSource = new StreamSource(transformInputStream);
        try {
            return factory.newTemplates(transformSource);
        } catch (TransformerConfigurationException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public static void transform(String input, Result output, String transformLocation) {
        transform(input, output, transformLocation, null);
    }

    public static void transform(Source input, Result output, String transformLocation) {
        transform(input, output, transformLocation, null);
    }

    public static void transform(String input, Result output, String transformLocation, Map<String, String> params) {
        transform(new StreamSource(new StringReader(input)), output, transformLocation, params);
    }

    public static void transform(Source input, Result output, String transformLocation, Map<String, String> params) {

    }

    public static void transform(Source input, Result output, Templates templates, Map<String, String> params) {

        try {

            Transformer transformer = templates.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            // add params to the transformer
            if (null != params) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    transformer.setParameter(entry.getKey(), entry.getValue());
                }
            }

            // perform transformation
            transformer.transform(input, output);

        } catch (TransformerConfigurationException e) {
            log.error(e.getMessage());
        } catch (TransformerException e) {
            log.error(e.getMessage());
        }
    }

    private static class ClasspathResourceURIResolver implements URIResolver {
        @Override
        public Source resolve(String href, String base) throws TransformerException {
            return new StreamSource(getTransformAsStream(href));
        }
    }

    private static Document document(String input) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element root = doc.createElement("whatever");
            doc.appendChild(root);

            return doc;

        } catch (ParserConfigurationException e) {
            log.error(e.getMessage());
        }

        return null;
    }

    public static void transform(String transformFile, String inFile, String outFile) {

        try {

            File stylesheet = new File(transformFile);
            File datafile = new File(inFile);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(datafile);
            // ...

            InputStream transformInputStream = getTransformAsStream(transformFile);
            StreamSource stylesource = new StreamSource(transformInputStream);

            // StreamSource stylesource = new StreamSource(stylesheet);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(stylesource);

            transformer.transform(new StreamSource(datafile), new StreamResult(outFile));

        } catch (Exception e) {

            log.error(e.getMessage());
        }
    }

    private static TransformerFactory createFactory() {

        // pre-compile the transformer as a "template"
        TransformerFactory factory = TransformerFactory.newInstance(SAXON_TRANSFORMER_FACTORY_NAME, null);

        Configuration config = Configuration.newConfiguration();
        // config.registerExtensionFunction(new RandomDefinition());
        ((TransformerFactoryImpl)factory).setConfiguration(config);

        factory.setURIResolver(new ClasspathResourceURIResolver());

        return factory;
    }
}
