# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-path-traversal -->
### sec-path-traversal — Confine user-influenced file paths

File names taken from request data are resolved against a normalized base directory and rejected unless the result stays under it (resolve + normalize + startsWith). Use the same pattern for any new file path built from user input.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-spel-restricted-context -->
### sec-spel-restricted-context — Never evaluate user input in a default SpEL context

Request parameters evaluated as SpEL run in a restricted context with the type locator, constructor/method resolvers, property accessors and bean resolver disabled. Do not use the default StandardEvaluationContext for untrusted expressions.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-env-secrets -->
### sec-env-secrets — Load secrets from the environment

The Jasypt master password is read from JASYPT_ENCRYPTOR_PASSWORD with no default, so startup fails closed when it is unset. Never commit the value; because the old one is in git history, rotate it and re-encrypt the ENC() values.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-deserialization-allowlist -->
### sec-deserialization-allowlist — Allowlist classes when deserializing

Cookies deserialized in AdminController go through an ObjectInputStream subclass that only resolves io.shiftleft.model.AuthToken. Any new deserialization of untrusted data should use the same allowlist or a non-Java-serialization format.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-html-escape -->
### sec-html-escape — Escape request data returned to the browser

Request-derived strings returned from controllers are passed through HtmlUtils.htmlEscape. Return text endpoints with @ResponseBody so the value is not treated as a view name.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-constant-time-compare -->
### sec-constant-time-compare — Compare checksums and tokens in constant time

Checksum and token comparisons use MessageDigest.isEqual on bytes instead of String.equals, which leaks timing. Use it for any new secret or digest comparison.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-session-authorization -->
### sec-session-authorization — Authorize id-based record access

Endpoints that read or write records by a caller-supplied id call SessionAuthorization.requireAdmin before the lookup and fail closed with 403. New id-based endpoints should do the same, or add a real owner field to Customer and Account.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-header-and-redirect-values -->
### sec-header-and-redirect-values — Validate values placed in response headers

Values from the request that go into Cookie or Location headers pass through requireSafeHeaderValue, which rejects CR, LF and other control characters. Redirect targets also pass requireSameOriginPath, which only accepts single-slash paths.
<!-- /harness-remediation -->
