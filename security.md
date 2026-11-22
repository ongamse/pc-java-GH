# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-decoded-input-allowlist -->
### sec-decoded-input-allowlist — Allowlist decoded request data

Base64 cookie data in this repo must not be path-joined or deserialized directly: file names are normalized and confined to the static/ directory, and ObjectInputStream.resolveClass only allows io.shiftleft.model.AuthToken.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-spel-restricted-context -->
### sec-spel-restricted-context — Never evaluate untrusted SpEL with the default context

User-supplied expressions in SearchController are evaluated with a restricted StandardEvaluationContext (no type locator, constructor, method or property resolvers, no bean resolver) so only literals and operators work.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-html-encode-responses -->
### sec-html-encode-responses — Encode user-derived response bodies

Controller responses that include request-derived strings go through Spring HtmlUtils.htmlEscape; a replace("script","") filter is not a control.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-header-value-sanitize -->
### sec-header-value-sanitize — Sanitize values written to response headers

Request-derived values set on response headers pass through CustomerController.sanitizeHeaderValue, which strips CR/LF and control characters; route any new header value through it.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-secrets-from-env -->
### sec-secrets-from-env — Load secrets from the environment

Credentials such as the Jasypt master password and AWS keys resolve from environment variables through Spring placeholders (JASYPT_ENCRYPTOR_PASSWORD, AWS_SECRET_KEY); never commit literals, and rotate values that remain in git history.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-constant-time-compare -->
### sec-constant-time-compare — Compare secrets and digests in constant time

Use MessageDigest.isEqual for any checksum, MAC or hash comparison instead of String.equals.
<!-- /harness-remediation -->
