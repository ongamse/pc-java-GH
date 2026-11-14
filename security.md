# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-path-containment -->
### sec-path-containment — Contain file paths under a base directory

Untrusted file names are resolved against an absolute, normalized base Path and rejected unless the result stays under that base, before any file I/O.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-spel-restricted -->
### sec-spel-restricted — Never evaluate request data as SpEL with the default context

Request parameters are not parsed with the default StandardEvaluationContext, which allows T(), constructors and method calls. Use a restricted context, or SimpleEvaluationContext once Spring is upgraded past 4.3.15.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-env-secrets -->
### sec-env-secrets — Load credentials from the environment

Jasypt and AWS credentials are Spring placeholders such as ${JASYPT_ENCRYPTOR_PASSWORD} and ${AWS_ACCESS_KEY_ID}, so the app fails closed when they are unset. Never commit them, and rotate values that were already committed.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-no-java-serialization -->
### sec-no-java-serialization — No Java native serialization for cookies

The auth cookie is a plain role.HMAC-SHA256 value verified with MessageDigest.isEqual, not an ObjectInputStream payload. Use a signed plain-text format for session data.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-html-encode -->
### sec-html-encode — Encode request data at the HTML sink

Controller output that echoes request values goes through HtmlUtils.htmlEscape, as debugEscaped already does. Do not rely on stripping tags or keywords.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-constant-time-compare -->
### sec-constant-time-compare — Compare hashes and tokens in constant time

Checksums, tokens and secrets are compared with MessageDigest.isEqual instead of String.equals.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-header-values -->
### sec-header-values — Reject control characters in response headers

Values built from request data go through CustomerController.safeHeaderValue before setHeader, which rejects CR, LF and other control characters.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-redirect-target -->
### sec-redirect-target — Build redirect targets from validated parts

Location headers use safeCustomerLocation, which accepts only an empty or plain /segment context path and a numeric id. Schemes, //host, backslashes and dot segments are rejected.
<!-- /harness-remediation -->
