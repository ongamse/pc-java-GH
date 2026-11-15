# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-path-containment -->
### sec-path-containment — Contain user-supplied file names

File names taken from cookies or requests are resolved against a normalized base directory and rejected if the result leaves it (Path.resolve().normalize() plus a startsWith check) before any file is created or written.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-spel-readonly -->
### sec-spel-readonly — Evaluate request data only in a restricted SpEL context

Request-supplied expressions are evaluated with SimpleEvaluationContext.forReadOnlyDataBinding(), never StandardEvaluationContext, so type references and constructors are unavailable.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-no-native-deserialization -->
### sec-no-native-deserialization — No Java native serialization for client-held state

Client-held state such as the auth cookie is a role plus an HMAC-SHA256 signature verified with MessageDigest.isEqual. The key comes from AUTH_COOKIE_SECRET; set it in multi-instance deployments.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-secrets-from-env -->
### sec-secrets-from-env — Load credentials from the environment

Passwords and keys (Jasypt password, AWS keys) are Spring placeholders resolved from environment variables or a secrets manager, never committed. Rotate any value that was previously committed, since it remains in git history.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-constant-time-compare -->
### sec-constant-time-compare — Compare digests in constant time

Checksums, digests and tokens are compared with MessageDigest.isEqual on bytes instead of String.equals.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-encode-and-sanitize-output -->
### sec-encode-and-sanitize-output — Encode response bodies and sanitize header values

Request-derived strings written to a response body go through HtmlUtils.htmlEscape. Values placed in response headers have CR/LF and control characters stripped, and redirect Location values are built from the trusted server context path plus a numeric id.
<!-- /harness-remediation -->
