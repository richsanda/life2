package w.whateva.service.email.web.mapper;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic.Kind;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.*;
import java.net.URL;

/**
 *
 */
@SupportedAnnotationTypes(value= {"w.whateva.service.email.web.mapper.WController", "org.springframework.web.bind.annotation.RequestMapping"})
public class ControllerProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private Messager consoleLogger;

    private static ProcessingEnvironment processingEnvironment;

    @Override
    public void init(ProcessingEnvironment env) {

        filer = env.getFiler();
        messager = env.getMessager();
        processingEnv = env;
        consoleLogger = processingEnv.getMessager();
        processingEnvironment = env;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment environment) {

        Boolean result = Boolean.TRUE;
        try {
            Map<Class, Element> apis = new HashMap<>();
            for (Element element : environment.getElementsAnnotatedWith(WController.class)) {
                write(element);
                consoleLogger.printMessage(Kind.NOTE, "File generated");
            }
        }
        catch (IOException ex) {
            consoleLogger.printMessage(Kind.ERROR, ex.getMessage());
        }
        return result;
    }

    private void write(Element application) throws IOException {

        consoleLogger.printMessage(Kind.NOTE, "ControllerProcessor.write: enter");

        WController annotation = application.getAnnotation(WController.class);
        TypeElement api = getApi(annotation);

        assert (null != api);

        String appPackage = getPackage((TypeElement)application);
        String apiPackage = getPackage(api);
        String apiName = api.getSimpleName().toString();

        Collection<WControllerModel> methods = new ArrayList<>();
        for (Element method : api.getEnclosedElements()) {
            WControllerMethodModel model = new WControllerMethodModel();
            method.accept(new WElementVisitor(), model);
            methods.add(model);
        }

        // initialize velocity

        Properties props = new Properties();
        URL url = this.getClass().getClassLoader().getResource("velocity.properties");
        props.load(url.openStream());

        VelocityEngine ve = new VelocityEngine(props);
        ve.init();

        VelocityContext vc = new VelocityContext();

        String recWebConfigClassName = apiName + "Controller";
        vc.put("appPackage", appPackage);
        vc.put("apiPackage", apiPackage);
        vc.put("className", apiName + "Controller");
        vc.put("interfaceName", apiName);
        vc.put("serviceName", apiName + "Impl");
        vc.put("serviceType", apiName);
        vc.put("serviceVar", "service");
        vc.put("methods", methods);

        Template vt = ve.getTemplate("controller.vm");
        JavaFileObject jfo = processingEnv.getFiler().createSourceFile(appPackage + "." + recWebConfigClassName);

        processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE,
                "Generating source file: " + jfo.toUri());

        Writer writer = jfo.openWriter();

        processingEnv.getMessager().printMessage( Diagnostic.Kind.NOTE,
                "Applying velocity template: " + vt.getName());

        vt.merge(vc, writer);
        writer.flush();
        writer.close();
        consoleLogger.printMessage(Kind.NOTE, "ControllerProcessor.write: exit");
    }

    private static TypeElement getApi(WController annotation) {
        try {
            annotation.api();
        } catch( MirroredTypeException mte ) {
            return (TypeElement)processingEnvironment.getTypeUtils().asElement(mte.getTypeMirror());
        }
        return null;
    }

    private static String getPackage(TypeElement element) {
        return getPackage(element.getQualifiedName().toString());
    }

    private static String getPackage(String fullName) {
        int lastIndex = fullName.lastIndexOf(".");
        if (lastIndex != -1) {
            return fullName.substring(0, lastIndex);
        }
        return fullName;
    }
}
