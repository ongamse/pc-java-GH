# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-base64-validate -->
### sec-base64-validate — Validate Base64 before decoding

Cookie values are length-capped and checked against a strict Base64 alphabet before Base64 decoding, and fail closed on anything else.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-deserialize-allowlist -->
### sec-deserialize-allowlist — Allowlist classes when deserializing request data

Request-derived Java deserialization uses an ObjectInputStream subclass whose resolveClass accepts only the expected class (AuthToken); use the same pattern for any new deserialization.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-path-canonical -->
### sec-path-canonical — Confine file paths to a base directory

Paths built from request data are canonicalized with getCanonicalFile and rejected unless they sit strictly under the base directory.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-spel-restricted -->
### sec-spel-restricted — Never evaluate untrusted SpEL in the default context

Untrusted expressions are evaluated in a restricted EvaluationContext with no type locator, resolvers or bean access, never in the default StandardEvaluationContext.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-html-encode -->
### sec-html-encode — Encode attacker data at the response sink

Responses containing request-influenced data go through HtmlUtils.htmlEscape; substring stripping such as removing the word script is not an encoder.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-header-values -->
### sec-header-values — Keep request data out of header and redirect targets

Header values from request data pass through CustomerController.safeHeaderValue, which rejects CR, LF and control characters, and Location headers use a fixed relative path with only the numeric id.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-constant-time -->
### sec-constant-time — Compare hashes and secrets in constant time

Hash, MAC and secret comparisons use MessageDigest.isEqual on bytes instead of String.equals.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-no-committed-secrets -->
### sec-no-committed-secrets — Keep master passwords out of the repo

The Jasypt master password is supplied at runtime through JASYPT_ENCRYPTOR_PASSWORD or a command-line argument; the app fails closed when it is absent. Rotate the previously committed value.
<!-- /harness-remediation -->
