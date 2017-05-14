package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;

public class MobEffectHandle extends Template.Handle {
    public static final MobEffectClass T = new MobEffectClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(MobEffectHandle.class, "net.minecraft.server.MobEffect");


    /* ============================================================================== */

    public static final MobEffectHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        MobEffectHandle handle = new MobEffectHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public static final class MobEffectClass extends Template.Class {
    }
}
