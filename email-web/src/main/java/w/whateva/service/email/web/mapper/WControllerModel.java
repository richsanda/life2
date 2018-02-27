package w.whateva.service.email.web.mapper;

import java.util.Collection;

public interface WControllerModel {

    public String getName();

    public String getType();

    public String getReturnType();

    public Collection<? extends WControllerModel> getParams();

    public Collection<? extends WControllerModel> getAnnotations();
}
