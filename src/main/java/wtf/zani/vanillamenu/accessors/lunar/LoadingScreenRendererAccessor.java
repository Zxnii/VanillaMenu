package wtf.zani.vanillamenu.accessors.lunar;

import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import wtf.zani.vanillamenu.accessors.Accessor;
import wtf.zani.vanillamenu.hooks.delegations.LoadingScreenRendererHook;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.objectweb.asm.Opcodes.ASM9;

public class LoadingScreenRendererAccessor extends Accessor {
    private final Field statusField;
    private final Field loadStageField;

    public LoadingScreenRendererAccessor(Object wrapped) throws NoSuchFieldException {
        super(wrapped);

        final ClassNode classNode = LoadingScreenRendererHook.getInstance().node;

        this.statusField = Arrays.stream(wrapped.getClass().getDeclaredFields())
                .filter(field -> field.getType().getName().equals(String.class.getName()))
                .findFirst()
                .orElseThrow();
        this.statusField.trySetAccessible();

        final FieldNode stageList = classNode.fields.stream()
                .filter(fieldNode -> fieldNode.desc.equals("Ljava/util/List;") && fieldNode.signature != null)
                .findFirst()
                .orElseThrow();

        final List<String> types = new ArrayList<>();

        final SignatureVisitor visitor = new SignatureVisitor(ASM9) {
            @Override
            public void visitClassType(String name) {
                types.add(name);
            }
        };

        final SignatureReader reader = new SignatureReader(stageList.signature);

        reader.accept(visitor);
        visitor.visitEnd();

        final FieldNode stageFieldNode = classNode.fields.stream()
                .filter(fieldNode -> fieldNode.desc.equals("L" + types.get(1) + ";"))
                .findFirst()
                .orElseThrow();

        this.loadStageField = wrapped.getClass().getDeclaredField(stageFieldNode.name);
        this.loadStageField.trySetAccessible();
    }

    @SuppressWarnings("unused")
    public static void create(Object renderer) throws NoSuchFieldException {
        if (Accessor.loadingScreenRendererAccessor == null) {
            Accessor.loadingScreenRendererAccessor = new LoadingScreenRendererAccessor(renderer);
        }
    }

    public String getStatus() {
        try {
            return (String) this.statusField.get(this.wrapped);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    public LoadStageAccessor getLoadStage() {
        try {
            final Object value = this.loadStageField.get(this.wrapped);

            return value != null ? new LoadStageAccessor(value) : null;
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }
}
