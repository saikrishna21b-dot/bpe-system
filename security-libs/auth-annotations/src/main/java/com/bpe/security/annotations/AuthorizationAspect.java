package com.bpe.security.annotations;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import com.bpe.security.common.AuthenticatedUser;
import com.bpe.security.common.SecurityContextHolderExt;

@Aspect
public class AuthorizationAspect {

    /* =============================
       ALL ROLES REQUIRED
       ============================= */
    @Before("@within(requireRole) || @annotation(requireRole)")
    public void checkAllRoles(RequireRole requireRole) {

        AuthenticatedUser user = getUser();

        Set<String> userRoles = user.roles()
                .stream()
                .collect(Collectors.toSet());

        for (String role : requireRole.value()) {
            if (!userRoles.contains(role)) {
                throw new AuthorizationException(
                        "Access denied. Missing role: " + role
                );
            }
        }
    }

    /* =============================
       ANY ROLE REQUIRED
       ============================= */
    @Before("@within(requireAnyRole) || @annotation(requireAnyRole)")
    public void checkAnyRole(RequireAnyRole requireAnyRole) {

        AuthenticatedUser user = getUser();

        boolean allowed = Arrays.stream(requireAnyRole.value())
                .anyMatch(user.roles()::contains);

        if (!allowed) {
            throw new AuthorizationException(
                    "Access denied. Required any role: "
                            + String.join(",", requireAnyRole.value())
            );
        }
    }

    /* =============================
       COMMON USER FETCH
       ============================= */
    private AuthenticatedUser getUser() {
        AuthenticatedUser user = SecurityContextHolderExt.getUser();
        if (user == null) {
            throw new AuthorizationException("Unauthenticated access");
        }
        return user;
    }
}
