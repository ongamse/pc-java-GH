# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-path-confine -->
### sec-path-confine — Confine user-derived file paths

Resolve user-supplied file names against a normalized base directory and reject anything that escapes it (absolute names, `..`, invalid paths) before any file operation.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-no-java-deserialization -->
### sec-no-java-deserialization — Never deserialize client-supplied objects

Auth state in cookies is a plain role value signed with HMAC-SHA256 (key from `AUTH_TOKEN_SECRET`) and verified in constant time. Do not use `ObjectInputStream` on request data.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-constant-time-compare -->
### sec-constant-time-compare — Compare digests and tokens in constant time

Use `MessageDigest.isEqual` for any digest, MAC or token comparison instead of `String.equals`.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-html-encode-output -->
### sec-html-encode-output — Encode request-derived output

Return request-derived strings through `HtmlUtils.htmlEscape` rather than blacklist filtering such as `replace("script", "")`.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-safe-headers-redirects -->
### sec-safe-headers-redirects — Build headers and redirects server-side

Location targets come from `buildCustomerLocation` (server-relative path, numeric id) and header values pass `requireSafeHeaderValue`, which rejects CR/LF and control characters. Reuse them for any new header or redirect built from request data.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-secrets-from-env -->
### sec-secrets-from-env — Load secrets from the environment

The Jasypt master password is read from `JASYPT_ENCRYPTOR_PASSWORD` with no fallback value. Rotate the previously committed value and re-encrypt `ENC()` properties with the new one.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-no-spel-on-user-input -->
### sec-no-spel-on-user-input — Do not evaluate user input as SpEL

Request parameters must not be parsed as expressions. If expression evaluation is required, use a restricted context (no type locator, no constructor/method resolvers) and prefer `SimpleEvaluationContext` once Spring is upgraded.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-authorize-id-lookups -->
### sec-authorize-id-lookups — Authorize id-based record access

Endpoints that load records by a client-supplied id call `AdminController.hasAdminSession` first and fail closed with 403 via `AccessDeniedException`.
<!-- /harness-remediation -->
