# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-file-path-resolve -->
### sec-file-path-resolve — Resolve user-supplied file names against a base directory

Resolve names with Path.resolve().normalize() against the absolute base directory and reject results outside it (see CustomerController.saveSettings).
<!-- /harness-remediation -->

<!-- harness-remediation:sec-safe-deserialization -->
### sec-safe-deserialization — Allowlist classes when deserializing request data

Use an ObjectInputStream subclass whose resolveClass accepts only io.shiftleft.model.AuthToken (AdminController); do not deserialize cookies with a bare ObjectInputStream.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-spel-restricted-context -->
### sec-spel-restricted-context — Never evaluate untrusted SpEL in the default context

SearchController evaluates request expressions in a restricted StandardEvaluationContext; prefer SimpleEvaluationContext after a Spring upgrade.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-output-encoding -->
### sec-output-encoding — Encode untrusted data in HTML responses

Use Spring HtmlUtils.htmlEscape for request-derived output instead of stripping tags or substrings.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-header-and-redirect -->
### sec-header-and-redirect — Sanitize header values and constrain redirects

Use CustomerController.sanitizeHeaderValue for any header built from request data and customerLocation() for Location headers so redirects stay same-origin.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-constant-time-compare -->
### sec-constant-time-compare — Compare digests and tokens in constant time

Use MessageDigest.isEqual on bytes for digest or token comparisons rather than String.equals.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-secrets-from-env -->
### sec-secrets-from-env — Load secrets from the environment

Properties files use placeholders such as ${JASYPT_ENCRYPTOR_PASSWORD} and ${AWS_SECRET_KEY:}; never commit literal secrets, and rotate any that were previously committed.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-resource-authorization -->
### sec-resource-authorization — Authorize id-based lookups

Call ResourceAccess.require(request) before looking up records by a client-supplied id; replace it with per-record ownership checks if an owner model is added.
<!-- /harness-remediation -->
