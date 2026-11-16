# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-file-path-containment -->
### sec-file-path-containment — Contain user-supplied file paths

Resolve user-supplied filenames against an absolute normalized base Path and reject any target that does not start with that base before any file I/O (see CustomerController.saveSettings).
<!-- /harness-remediation -->

<!-- harness-remediation:sec-restricted-spel-context -->
### sec-restricted-spel-context — Restrict expression evaluation

Never call Expression.getValue() on untrusted input with the default context; use SimpleEvaluationContext.forReadOnlyDataBinding() (see SearchController).
<!-- /harness-remediation -->

<!-- harness-remediation:sec-no-native-deserialization -->
### sec-no-native-deserialization — No native deserialization of cookies

Cookies are parsed as plain text (base64 of role:<int>) with strict validation instead of ObjectInputStream; add an HMAC with an env-supplied key if the token must be tamper-proof.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-html-encode-output -->
### sec-html-encode-output — HTML-encode reflected values

Encode every reflected request value with Spring HtmlUtils.htmlEscape rather than blacklist filtering.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-header-and-redirect-values -->
### sec-header-and-redirect-values — Validate values placed in headers

Route request-derived header values through sanitizeHeaderValue, and build Location headers with buildCustomerLocation, which only accepts an allowlisted path-only context path.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-constant-time-compare -->
### sec-constant-time-compare — Compare digests in constant time

Use MessageDigest.isEqual on bytes for secret and digest comparisons instead of String.equals.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-secrets-from-env -->
### sec-secrets-from-env — Keep secrets out of properties files

The Jasypt master password and AWS keys come from JASYPT_ENCRYPTOR_PASSWORD, AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY; rotate the values that were committed and never re-commit live secrets.
<!-- /harness-remediation -->
