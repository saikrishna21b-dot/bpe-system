package com.bpe.security.annotations;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireAnyRole {

    /**
     * User must have AT LEAST ONE role
     */
    String[] value();
}
