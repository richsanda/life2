package w.whateva.service.email.web.mapper;

import java.util.Collection;

class WControllerMethodModel implements WControllerModel {

    private String name;
    private String returnType;
    private Collection<WControllerParamModel> params;
    private Collection<WControllerAnnotationModel> annotations;

    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public Collection<WControllerParamModel> getParams() {
        return params;
    }

    public void setParams(Collection<WControllerParamModel> params) {
        this.params = params;
    }

    @Override
    public Collection<WControllerAnnotationModel> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Collection<WControllerAnnotationModel> annotations) {
        this.annotations = annotations;
    }
}
