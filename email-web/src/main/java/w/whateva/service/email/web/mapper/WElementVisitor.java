package w.whateva.service.email.web.mapper;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeVisitor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class WElementVisitor implements ElementVisitor<WControllerModel, WControllerModel> {

    @Override
    public WControllerModel visit(Element e, WControllerModel m) {
        return null;
    }

    @Override
    public WControllerModel visit(Element e) {
        if (e instanceof ExecutableElement) {
            return visitExecutable((ExecutableElement) e, new WControllerMethodModel());
        } else if (e instanceof VariableElement) {
            return visitVariable((VariableElement) e, new WControllerParamModel());
        }
        return null;
    }

    @Override
    public WControllerModel visitPackage(PackageElement e, WControllerModel wControllerModel) {
        return null;
    }

    @Override
    public WControllerModel visitType(TypeElement e, WControllerModel m) {

        assert m instanceof WControllerTypeModel : "expected method";

        WControllerTypeModel result = (WControllerTypeModel)m;

        result.setName(e.getSimpleName().toString());

        return result;
    }

    @Override
    public WControllerModel visitVariable(VariableElement e, WControllerModel m) {

        assert m instanceof WControllerParamModel : "expected method";

        WControllerParamModel result = (WControllerParamModel)m;

        result.setName(e.getSimpleName().toString());
        result.setType(e.asType().toString());
        result.setAnnotations(new ArrayList<>());

        for (AnnotationMirror a : e.getAnnotationMirrors()) {
            WControllerAnnotationModel aM = new WControllerAnnotationModel();
            aM.setName(a.toString());
            result.getAnnotations().add(aM);
        }

        return result;
    }

    @Override
    public WControllerModel visitExecutable(ExecutableElement e, WControllerModel m) {

        assert m instanceof WControllerMethodModel : "expected method";

        WControllerMethodModel result = (WControllerMethodModel)m;

        if ( e.getKind() == ElementKind.METHOD ) {

            result.setName(e.getSimpleName().toString());
            result.setReturnType(e.getReturnType().toString());
            result.setParams(new ArrayList<>());
            for (Element c : e.getParameters()) {
                WControllerParamModel param = new WControllerParamModel();
                c.accept(this, param);
                result.getParams().add(param);
            }
            result.setAnnotations(new ArrayList<>());
            for (AnnotationMirror a : e.getAnnotationMirrors()) {
                WControllerAnnotationModel aM = new WControllerAnnotationModel();
                aM.setName(a.toString());
                result.getAnnotations().add(aM);
            }
            return result;

        } else if ( e.getKind() == ElementKind.CONSTRUCTOR ) {

        }
        return null;
    }

    @Override
    public WControllerModel visitTypeParameter(TypeParameterElement e, WControllerModel wControllerModel) {
        return null;
    }

    @Override
    public WControllerModel visitUnknown(Element e, WControllerModel wControllerModel) {
        return null;
    }
}
