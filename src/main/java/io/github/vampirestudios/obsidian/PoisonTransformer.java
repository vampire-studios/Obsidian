package io.github.vampirestudios.obsidian;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import static org.objectweb.asm.Opcodes.*;

public class PoisonTransformer {
    public static void transform(ClassNode node) {
        for (MethodNode method : node.methods) {
            if ((method.access & ACC_STATIC) != 0) continue;
            if (method.name.equals("<init>") || method.name.equals("<clinit>") || method.name.contains("obsidian$")) continue;

            var first = method.instructions.getFirst();
            method.instructions.insertBefore(first, new VarInsnNode(ALOAD, 0));
            method.instructions.insertBefore(first, new MethodInsnNode(INVOKEVIRTUAL, node.name, "obsidian$checkPoisoned", "()V", false));
        }
    }
}