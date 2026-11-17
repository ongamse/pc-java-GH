# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-secrets-from-env -->
### sec-secrets-from-env — Keep credentials out of properties files

The Jasypt master password and AWS keys were committed in src/main/resources; they now resolve from JASYPT_ENCRYPTOR_PASSWORD, AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY. Supply them from the environment or a secrets manager, and rotate the old values that remain in git history.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-constant-time-compare -->
### sec-constant-time-compare — Compare hashes in constant time

CustomerController.saveSettings compared the cookie checksum with String.equals, which leaks timing. Use MessageDigest.isEqual for any secret, token or hash comparison.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-signed-cookie -->
### sec-signed-cookie — Never deserialize cookie data

AdminController read the auth cookie with ObjectInputStream. The cookie is now a plain role payload authenticated with HMAC-SHA256; do not use Java native serialization on client-supplied data.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-output-encoding -->
### sec-output-encoding — Encode output at the response boundary

SearchController.doGetSearch and CustomerController.debug returned attacker-influenced strings as HTML. They now go through HtmlUtils.htmlEscape; encode at the output boundary instead of stripping tags.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-header-values -->
### sec-header-values — Sanitize request data placed in headers

Request-derived values written to the Cookie header pass through CustomerController.sanitizeHeaderValue, which strips CR/LF and control characters. Route any new header value built from request data through it.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-redirect-target -->
### sec-redirect-target — Build redirect targets from trusted config

Location headers are built by CustomerController.customerLocation from the configured server.context-path plus the saved id, not from the request. Keep redirect destinations same-origin and server-derived.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-file-path-containment -->
### sec-file-path-containment — Contain user-named file writes

CustomerController.saveSettings resolves the cookie-supplied filename against the static base directory, normalizes it, and rejects paths that escape it. Reuse this containment check for any user-named file.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-spel-restricted-context -->
### sec-spel-restricted-context — Never evaluate user input in a default SpEL context

SearchController.doGetSearch evaluates the request parameter in a StandardEvaluationContext with the type locator, constructor and method resolvers and property accessors removed. Do not evaluate untrusted expressions with a default context.
<!-- /harness-remediation -->
