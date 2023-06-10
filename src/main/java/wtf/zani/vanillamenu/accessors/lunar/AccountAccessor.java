package wtf.zani.vanillamenu.accessors.lunar;

import wtf.zani.vanillamenu.accessors.Accessor;

import java.lang.reflect.InvocationTargetException;

public class AccountAccessor extends Accessor {
    public AccountAccessor(Object wrapped) {
        super(wrapped);
    }

    public String getUsername() {
        try {
            return this.callSuperMethod("getUsername");
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
