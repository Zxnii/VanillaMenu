package wtf.zani.vanillamenu.accessors;

import wtf.zani.vanillamenu.accessors.lunar.AccountManagerAccessor;
import wtf.zani.vanillamenu.accessors.lunar.LoadingScreenRendererAccessor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class Accessor {
    public static AccountManagerAccessor accountAccessor;
    public static LoadingScreenRendererAccessor loadingScreenRendererAccessor;

    protected final Map<String, Method> methodCache = new HashMap<>();
    protected final Object wrapped;

    public Accessor(Object wrapped) {
        this.wrapped = wrapped;
    }

    protected <T> T callSuperMethod(String name, Object... args) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        return this.callMethod(true, name, args);
    }

    protected <T> T callMethod(String name, Object... args) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        return this.callMethod(false, name, args);
    }

    @SuppressWarnings("unchecked")
    protected <T> T callMethod(boolean superMethod, String name, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (this.methodCache.containsKey(name)) {
            return (T) this.methodCache.get(name).invoke(this.wrapped, args);
        } else {
            final Class<?>[] argTypes = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);
            final Method method = (superMethod ? this.wrapped.getClass().getSuperclass() : this.wrapped.getClass()).getDeclaredMethod(name, argTypes);

            this.methodCache.put(name, method);

            method.setAccessible(true);

            return (T) method.invoke(this.wrapped, args);
        }
    }

    public Object getWrapped() {
        return this.wrapped;
    }
}
