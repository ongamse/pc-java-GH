# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-no-eval-request-data -->
### sec-no-eval-request-data — Never evaluate request data as code

SearchController used to parse the `foo` parameter as a SpEL expression. Treat request values as plain data and never pass them to SpEL, OGNL or MVEL; if evaluation is needed, use an allowlist of fixed expressions.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-no-java-deserialization -->
### sec-no-java-deserialization — Do not deserialize cookies

AdminController used to run ObjectInputStream on the auth cookie. Cookies are now a role plus an HMAC-SHA256 signature checked with MessageDigest.isEqual, so never deserialize Java objects from request data.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-constant-time-compare -->
### sec-constant-time-compare — Compare secrets in constant time

Hash, token and signature comparisons use MessageDigest.isEqual on bytes rather than String.equals. Use the same for any new secret comparison.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-escape-reflected-output -->
### sec-escape-reflected-output — Escape reflected output

Reflected request data goes through HtmlUtils.htmlEscape, not keyword stripping such as replace("script", ""). Controllers that return a raw string need @ResponseBody so it is a body and not a view name.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-validate-header-values -->
### sec-validate-header-values — Allowlist values placed in headers and redirects

CustomerController.safeContextPath accepts only an empty string or /segment parts, which blocks CR/LF and protocol-relative `//host` redirects. Use it for any Location header built from request data, and check cookie values against a strict pattern before echoing them.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-authorize-id-lookups -->
### sec-authorize-id-lookups — Authorize id-addressed endpoints

The app has no per-customer user model, so id-addressed account and customer endpoints require the signed admin session via AdminController.isAdminSession and return 403 through AccessDeniedException. Reuse that check for any new endpoint that loads a record by a caller-supplied id.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-secrets-from-environment -->
### sec-secrets-from-environment — Load secrets from the environment

The Jasypt master password and AWS keys are read from JASYPT_ENCRYPTOR_PASSWORD, AWS_ACCESS_KEY and AWS_SECRET_KEY, never committed. Removing a value from the branch does not remove it from git history, so rotate anything that was committed.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-contain-file-paths -->
### sec-contain-file-paths — Contain user-supplied file names

saveSettings resolves the cookie file name against the fixed static base directory, normalizes it, and rejects anything outside that directory. Apply the same resolve, normalize and prefix check wherever a request value becomes a file path.
<!-- /harness-remediation -->
