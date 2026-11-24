# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-cookie-deserialization -->
### sec-cookie-deserialization — Allowlist classes when deserializing cookies

Cookie-derived data is untrusted: deserialize only through an ObjectInputStream whose resolveClass allowlists the expected class, and validate cookie-derived filenames with a plain-name allowlist plus a canonical-path containment check.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-spel-restricted-context -->
### sec-spel-restricted-context — Never evaluate request input as SpEL with the default context

Request parameters must not be parsed as expressions under the default StandardEvaluationContext; use a restricted context (or SimpleEvaluationContext after a Spring upgrade) or avoid expression evaluation entirely.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-html-encode-at-sink -->
### sec-html-encode-at-sink — HTML-encode request-derived text at the response sink

Return request-derived strings through HtmlUtils.htmlEscape rather than stripping tags or substrings.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-secrets-from-env -->
### sec-secrets-from-env — Keep credentials out of committed properties

The Jasypt master password and AWS keys are read from environment placeholders (JASYPT_ENCRYPTOR_PASSWORD, AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY); never commit live values, rotate any that reached git history, and do not log them.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-constant-time-compare -->
### sec-constant-time-compare — Compare digests in constant time

Use MessageDigest.isEqual for any secret or digest comparison instead of String.equals.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-id-endpoints-authz -->
### sec-id-endpoints-authz — Authorize id-addressed endpoints

Customer and account endpoints addressed by a caller-supplied id call AdminController.requireAdminSession and fail closed with 403; reuse that helper for new id-addressed endpoints and bind the path id to the saved entity.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-response-header-values -->
### sec-response-header-values — Sanitize request data placed in response headers

Route request-derived header values through safeHeaderValue (strips CR/LF and control characters) or an allowlist regex.
<!-- /harness-remediation -->
