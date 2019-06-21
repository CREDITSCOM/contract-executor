package com.credits.scapi.annotations;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable( value = UsingContracts.class )
public @interface UsingContract {
    String address();
    String method();
}
