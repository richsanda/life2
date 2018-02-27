package w.whateva.service.email.web.mapper;

import java.util.Collection;

class WControllerAnnotationParamModel implements WControllerModel {

    private String name;
    private String value;

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

    @Override
    public Collection<? extends WControllerModel> getParams() {
        return null;
    }

    @Override
    public Collection<? extends WControllerModel> getAnnotations() {
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
