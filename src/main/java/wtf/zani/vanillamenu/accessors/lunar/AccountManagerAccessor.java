package wtf.zani.vanillamenu.accessors.lunar;

import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import wtf.zani.vanillamenu.Util;
import wtf.zani.vanillamenu.accessors.Accessor;
import wtf.zani.vanillamenu.hooks.delegations.AccountManagerHook;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ASM9;

public class AccountManagerAccessor extends Accessor {
    private final Method getAccountsMethod;
    private final Method getCurrentAccountMethod;
    private final Method setCurrentAccountMethod;

    public AccountManagerAccessor(Object wrapped) throws NoSuchMethodException {
        super(wrapped);

        final AtomicReference<String> accountClass = new AtomicReference<>();

        final ClassNode managerNode = AccountManagerHook.getInstance().node;
        final MethodNode getAccountsMethodNode = Util.findMethod(managerNode, methodNode -> {
            if ((methodNode.access & ACC_PUBLIC) <= 0 || methodNode.signature == null) {
                return false;
            }

            final List<String> types = new ArrayList<>();

            final SignatureVisitor visitor = new SignatureVisitor(ASM9) {
                @Override
                public void visitClassType(String name) {
                    types.add(name);
                }
            };

            final SignatureReader reader = new SignatureReader(methodNode.signature);

            reader.accept(visitor);
            visitor.visitReturnType();

            accountClass.set(types.get(2));

            return types.get(0).equals("java/util/Map");
        });

        this.getAccountsMethod = wrapped.getClass().getMethod(getAccountsMethodNode.name);
        this.getCurrentAccountMethod = Arrays.stream(wrapped.getClass().getMethods())
                .filter(method ->
                        method.getReturnType().getName().replace('.', '/').equals(accountClass.get()))
                .findFirst()
                .orElseThrow();
        this.setCurrentAccountMethod = Arrays.stream(wrapped.getClass().getMethods())
                .filter(method -> {
                    if (method.getParameterCount() != 1) return false;

                    return method.getReturnType().getName().equals(Void.TYPE.getName()) && method.getParameterTypes()[0].getName().replace('.', '/').equals(accountClass.get());
                })
                .findFirst()
                .orElseThrow();
    }

    @SuppressWarnings("unused")
    public static void create(Object accountManager) throws NoSuchMethodException {
        Accessor.accountAccessor = new AccountManagerAccessor(accountManager);
    }

    @SuppressWarnings("unchecked")
    public Map<String, AccountAccessor> getAccounts() {
        try {
            final Map<String, Object> accounts = (Map<String, Object>) this.getAccountsMethod.invoke(this.wrapped);
            final Map<String, AccountAccessor> mappedAccounts = new HashMap<>();

            accounts.forEach((uuid, account) -> mappedAccounts.put(uuid, new AccountAccessor(account)));

            return mappedAccounts;
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public AccountAccessor getCurrentAccount() {
        try {
            final Object account = this.getCurrentAccountMethod.invoke(this.wrapped);

            return account != null ? new AccountAccessor(account) : null;
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void setAccount(AccountAccessor account) {
        try {
            this.setCurrentAccountMethod.invoke(this.wrapped, account.getWrapped());
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
