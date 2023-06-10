package wtf.zani.vanillamenu.accessors.lunar;

import wtf.zani.vanillamenu.accessors.Accessor;

import java.lang.reflect.InvocationTargetException;

public class LoadStageAccessor extends Accessor {
    public LoadStageAccessor(Object wrapped) {
        super(wrapped);
    }

    public String getCategory() {
        try {
            return this.callMethod("getCategory");
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
