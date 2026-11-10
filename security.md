# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-path-containment -->
### sec-path-containment — Contain client-supplied file names

Resolve client-supplied file names against an absolute base directory, normalize, and reject anything not contained in it (absolute, empty, NUL, or ../ escapes) before any file I/O.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-no-native-deserialization -->
### sec-no-native-deserialization — No native Java deserialization of cookies

Cookie and session data must not go through ObjectInputStream. Use signed plain-data tokens (role plus HMAC-SHA256, key from AUTH_TOKEN_SECRET) verified with MessageDigest.isEqual.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-constant-time-compare -->
### sec-constant-time-compare — Compare hashes and tokens in constant time

Use MessageDigest.isEqual on bytes for any hash, token or secret comparison instead of String.equals.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-restricted-spel -->
### sec-restricted-spel — Never evaluate user input in a default SpEL context

Evaluate expressions only in a restricted StandardEvaluationContext (no type locator, method, constructor or property resolvers), and prefer literal handling where expressions are not needed.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-encode-and-sanitize-output -->
### sec-encode-and-sanitize-output — Encode response bodies and sanitize header values

Encode request-derived data with Spring HtmlUtils.htmlEscape before writing it to a response body. Route request-derived header values through CustomerController.sanitizeHeaderValue, and build Location targets from server configuration, not request data.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-authorize-id-lookups -->
### sec-authorize-id-lookups — Authorize id-addressed endpoints

Endpoints that load or modify records by a client-supplied id must call AdminController.isAdminSession (or a per-owner check if customer login is added) and fail closed with 403.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-secrets-from-env -->
### sec-secrets-from-env — Load secrets from the environment

Supply the Jasypt master password through JASYPT_ENCRYPTOR_PASSWORD and keep real credentials in env or a secrets manager. The earlier committed value stays in git history and must be rotated.
<!-- /harness-remediation -->
