package w.whateva.service.email.web.mapper;

import java.util.Collection;

public class WControllerTypeModel implements WControllerModel {

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    @Override
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
}
