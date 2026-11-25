# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-untrusted-cookie-input -->
### sec-untrusted-cookie-input — Bound and allowlist untrusted cookie data

Base64 cookie values are length-capped before decoding, and the auth cookie is deserialized through a resolveClass allowlist (AuthToken only). Never readObject untrusted data without an allowlist.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-path-confinement -->
### sec-path-confinement — Confine user-supplied file names

Resolve user-supplied file names against the normalized absolute base directory and reject any result that does not stay under it before opening a File.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-spel-restricted-context -->
### sec-spel-restricted-context — Evaluate untrusted SpEL in a restricted context

Untrusted expressions run in a StandardEvaluationContext with no constructor, method, property or bean resolvers and a type locator that rejects T(...). Never use the default context.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-html-escape -->
### sec-html-escape — HTML-escape values written to responses

Use HtmlUtils.htmlEscape for any attacker-influenced value returned as HTML instead of stripping substrings.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-response-headers -->
### sec-response-headers — Sanitize and constrain response header values

Pass request-derived header values through sanitizeHeaderValue, and build Location values with buildCustomerLocation (validated context path plus an encoded path segment) so the redirect stays same-origin.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-object-authz -->
### sec-object-authz — Authorize before lookups by request-supplied id

Account and Customer endpoints call AdminController.requireAdminSession(request) before touching the repository; it fails closed with 403. Add a per-object owner check if an owner field is introduced.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-secret-comparison -->
### sec-secret-comparison — Compare secrets in constant time

Use MessageDigest.isEqual for checksum and secret comparisons instead of String.equals.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-config-secrets-from-env -->
### sec-config-secrets-from-env — Keep master passwords out of committed properties

jasypt.encryptor.password is read from JASYPT_ENCRYPTOR_PASSWORD and the app fails closed if it is unset. Re-encrypt the ENC(...) values, since the old password remains in git history.
<!-- /harness-remediation -->
