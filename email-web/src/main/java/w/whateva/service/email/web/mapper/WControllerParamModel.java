package w.whateva.service.email.web.mapper;

import java.util.Collection;

class WControllerParamModel implements WControllerModel {

    private String name;
    private String type;
    private Collection<WControllerAnnotationModel> annotations;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    @Override
    public String getReturnType() {
        return null;
    }

    @Override
    public Collection<? extends WControllerModel> getParams() {
        return null;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Collection<WControllerAnnotationModel> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Collection<WControllerAnnotationModel> annotations) {
        this.annotations = annotations;
    }
}
