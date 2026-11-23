# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-deser-allowlist -->
### sec-deser-allowlist — Allowlist classes when deserializing client data

Cookies are Base64-decoded in AdminController and CustomerController. Deserialize them only through an ObjectInputStream whose resolveClass allows AuthToken, and accept file names from them only if they are plain names. Prefer signed tokens.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-path-canonical -->
### sec-path-canonical — Canonicalize request-derived file paths

Resolve any request-derived file name against the canonical base directory with getCanonicalFile() and fail closed unless its parent is that directory.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-no-expression-eval -->
### sec-no-expression-eval — Never evaluate request input as an expression

SearchController must not pass request parameters to SpelExpressionParser or another expression engine. Treat the value as data.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-html-encode -->
### sec-html-encode — HTML-encode request-derived output

Pass request-derived values through HtmlUtils.htmlEscape before returning them, and mark handlers that return plain text with @ResponseBody so they are not resolved as view names.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-header-safe -->
### sec-header-safe — Validate values written to response headers

Route request-derived header values through safeHeaderValue and build Location headers with customerLocation, which rejects CR/LF, control characters, //, backslashes and scheme or host parts.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-const-time-compare -->
### sec-const-time-compare — Compare digests in constant time

Use MessageDigest.isEqual for any secret or digest comparison instead of String.equals.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-secrets-env -->
### sec-secrets-env — Keep credentials out of committed config

The Jasypt password and AWS keys are read from JASYPT_ENCRYPTOR_PASSWORD, AWS_ACCESS_KEY and AWS_SECRET_KEY. Never commit a replacement value; rotate anything already in git history.
<!-- /harness-remediation -->
