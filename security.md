# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-path-confine -->
### sec-path-confine — Confine user-influenced file names

File names taken from cookies or requests are resolved against an absolute, normalized base with Path.resolve().normalize() and rejected unless they stay under that base (CustomerController.saveSettings). Use this pattern for any user-influenced path.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-spel-restricted -->
### sec-spel-restricted — Restrict SpEL evaluation of request input

Request parameters are never evaluated with the default StandardEvaluationContext. SearchController uses a restricted context with no type references, method, constructor, property or bean resolvers.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-deser-allowlist -->
### sec-deser-allowlist — Allowlist classes when deserializing

AdminController reads the auth cookie through an ObjectInputStream subclass whose resolveClass only accepts io.shiftleft.model.AuthToken. Any other Java deserialization of untrusted data should use a class allowlist or a signed, non-Java-serialized token.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-html-encode -->
### sec-html-encode — Encode reflected output with HtmlUtils

Reflected request data is encoded with Spring's HtmlUtils.htmlEscape instead of stripping substrings such as script, which is bypassable (CustomerController.debug, SearchController.doGetSearch).
<!-- /harness-remediation -->

<!-- harness-remediation:sec-header-crlf -->
### sec-header-crlf — Strip CR/LF from header values

Request-derived values placed in response headers go through the private stripCrLf helper in CustomerController. Route any new header value built from request data through it.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-redirect-server-side -->
### sec-redirect-server-side — Build redirect targets from server-side data

Location headers are built by customerLocation(long) from the configured server.context-path and the saved entity id, not from request-derived URLs. Never feed request data into redirect targets.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-constant-time-compare -->
### sec-constant-time-compare — Compare digests in constant time

Cookie checksums are compared with MessageDigest.isEqual on the UTF-8 bytes instead of String.equals. Use it for any secret, MAC or digest comparison.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-secrets-from-env -->
### sec-secrets-from-env — Keep credentials out of properties files

The Jasypt master password and the AWS access and secret keys resolve from environment variables through Spring placeholders (JASYPT_ENCRYPTOR_PASSWORD, AWS_ACCESS_KEY, AWS_SECRET_KEY). The old values remain in git history, so rotate them and re-encrypt the ENC() values.
<!-- /harness-remediation -->
