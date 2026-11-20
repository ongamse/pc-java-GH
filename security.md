# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-cookie-untrusted -->
### sec-cookie-untrusted — Treat decoded cookie data as untrusted

Base64 cookie values must never reach ObjectInputStream without a resolveClass allowlist (AdminController uses one that accepts only AuthToken), and decoded filenames must be normalized and confined to the static/ base before use.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-spel-restricted -->
### sec-spel-restricted — Evaluate user expressions only in a restricted context

Never pass request input to SpEL with the default evaluation context; SearchController uses a StandardEvaluationContext with no accessors, resolvers or type references.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-html-escape -->
### sec-html-escape — Encode request-derived output

Use Spring HtmlUtils.htmlEscape for any request-derived value written to a response body instead of blacklist filters.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-header-values -->
### sec-header-values — Sanitize header and redirect values

Route request-derived values through CustomerController.sanitizeHeaderValue and customerLocation() before setting Cookie or Location headers.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-const-time-compare -->
### sec-const-time-compare — Compare hashes in constant time

Use MessageDigest.isEqual for any hash or token comparison instead of String.equals.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-config-secrets -->
### sec-config-secrets — Keep master passwords out of properties files

The Jasypt master password is read from JASYPT_ENCRYPTOR_PASSWORD; rotate the old value, which remains in git history, and re-encrypt ENC() values.
<!-- /harness-remediation -->
