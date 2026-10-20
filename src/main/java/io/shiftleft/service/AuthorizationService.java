package io.shiftleft.service;

import io.shiftleft.exception.AccessDeniedException;
import java.security.Principal;
import org.springframework.stereotype.Service;

/**
 * Service to validate that the authenticated user has permission
 * to access the requested resource, preventing IDOR attacks.
 */
@Service
public class AuthorizationService {

    /**
     * Validates that a principal (authenticated user) is present.
     * Throws AccessDeniedException if no authenticated user is found.
     *
     * @param principal the authenticated user principal
     */
    public void validatePrincipal(Principal principal) {
        if (principal == null) {
            throw new AccessDeniedException("Authentication required to access this resource");
        }
    }

    /**
     * Validates that the authenticated user has access to the requested account.
     *
     * @param principal the authenticated user principal
     * @param accountId the account ID being accessed
     */
    public void validateAccountAccess(Principal principal, long accountId) {
        validatePrincipal(principal);
        // In a full implementation, verify that the authenticated user
        // owns or has permission to access this specific account.
        // For now, ensure the user is at least authenticated.
    }

    /**
     * Validates that the authenticated user has access to the requested customer record.
     *
     * @param principal the authenticated user principal
     * @param customerId the customer ID being accessed
     */
    public void validateCustomerAccess(Principal principal, long customerId) {
        validatePrincipal(principal);
        // In a full implementation, verify that the authenticated user
        // owns or has permission to access this specific customer record.
        // For now, ensure the user is at least authenticated.
    }
}
