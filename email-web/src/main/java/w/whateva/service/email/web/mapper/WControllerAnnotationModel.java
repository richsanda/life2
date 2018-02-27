package w.whateva.service.email.web.mapper;

import java.util.Collection;

class WControllerAnnotationModel implements WControllerModel {

    private String name;
    private Collection<WControllerAnnotationParamModel> params;

    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String getReturnType() {
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<WControllerAnnotationParamModel> getParams() {
        return params;
    }

    @Override
    public Collection<? extends WControllerModel> getAnnotations() {
        return null;
    }

    public void setParams(Collection<WControllerAnnotationParamModel> params) {
        this.params = params;
    }
}
