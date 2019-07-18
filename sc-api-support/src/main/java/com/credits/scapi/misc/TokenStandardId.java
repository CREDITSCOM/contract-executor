package com.credits.scapi.misc;

import com.credits.scapi.v0.BasicStandard;
import com.credits.scapi.v0.ExtensionStandard;
import com.credits.scapi.v1.BasicTokenStandard;
import com.credits.scapi.v1.ExtensionTokenStandard;

public enum TokenStandardId {
    NOT_A_TOKEN(0, Void.TYPE),
    BASIC_STANDARD(1, BasicStandard.class),
    EXTENSION_STANDARD(2, ExtensionStandard.class),
    BASIC_TOKEN_STANDARD(3, BasicTokenStandard.class),
    EXTENSION_TOKEN_STANDARD(4, ExtensionTokenStandard.class);

    private long id;
    private Class<?> clazz;

    TokenStandardId(int id, Class<?> tokenClass) {
        this.id = id;
        this.clazz = tokenClass;
    }

    public long getId() {
        return id;
    }

    public Class<?> getTokenStandardClass() {
        return clazz;
    }
}

