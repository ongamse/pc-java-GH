# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-path-confine -->
### sec-path-confine — Confine request-derived file names

Resolve any cookie or request supplied file name against the absolute normalized base directory with java.nio Path and reject results that do not stay under it.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-no-expression-eval -->
### sec-no-expression-eval — Never evaluate request input as an expression

Request parameters are plain data. Do not pass them to SpelExpressionParser or any other expression engine; if one is unavoidable use SimpleEvaluationContext with an allowlist.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-deserialize-allowlist -->
### sec-deserialize-allowlist — Allowlist classes when deserializing

Cookie and request data must be read through an ObjectInputStream whose resolveClass accepts only the expected type (io.shiftleft.model.AuthToken here), since the target is Java 8 and has no ObjectInputFilter.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-html-escape -->
### sec-html-escape — HTML-escape reflected values

Use org.springframework.web.util.HtmlUtils.htmlEscape for request-derived text written into responses, and return it as a response body rather than a view name. Do not strip tags with string replace.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-header-and-redirect -->
### sec-header-and-redirect — Keep request data out of response headers

Strip CR/LF and control characters from any request-derived header value, and build Location headers as relative paths rather than echoing the request context path, which also closes open redirects.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-constant-time-compare -->
### sec-constant-time-compare — Compare secrets and hashes in constant time

Use MessageDigest.isEqual over bytes when comparing checksums, tokens or hashes instead of String.equals.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-secrets-from-env -->
### sec-secrets-from-env — Load credentials from the environment

Jasypt and AWS credentials are supplied through placeholders such as ${JASYPT_ENCRYPTOR_PASSWORD} and ${AWS_SECRET_KEY:}. Never commit credential values, and rotate any value that was previously committed.
<!-- /harness-remediation -->
