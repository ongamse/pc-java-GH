# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-path-confine -->
### sec-path-confine — Confine user-supplied file names

Resolve user-supplied file names against a normalized absolute base directory and reject any result that does not stay under it (CustomerController.saveSettings).
<!-- /harness-remediation -->

<!-- harness-remediation:sec-no-native-deserialization -->
### sec-no-native-deserialization — No native deserialization of client data

Cookies are signed plain text (<role>.<HMAC-SHA256>) verified with MessageDigest.isEqual, never ObjectInputStream; set AUTH_COOKIE_SECRET so the key is stable across instances.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-constant-time-compare -->
### sec-constant-time-compare — Constant-time secret comparison

Use MessageDigest.isEqual for any hash, token or checksum comparison instead of String.equals.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-spel-restricted-context -->
### sec-spel-restricted-context — Restrict SpEL evaluation

Evaluate user-influenced expressions only with SimpleEvaluationContext.forReadOnlyDataBinding(); never call Expression.getValue() with the default context on request data.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-html-escape-output -->
### sec-html-escape-output — Escape reflected request data

Return reflected request data through HtmlUtils.htmlEscape rather than keyword filtering such as replace("script", "").
<!-- /harness-remediation -->

<!-- harness-remediation:sec-authz-on-object-ids -->
### sec-authz-on-object-ids — Authorize every object-id handler

Handlers that take a customer or account id call AdminController.requireAdminSession first, which fails closed with HTTP 403.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-header-values-from-server -->
### sec-header-values-from-server — Keep request data out of headers

Pass any request-derived header value through requireNoCrlf, and build Location headers only from server-generated ids, never from the request context path.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-secrets-from-env -->
### sec-secrets-from-env — Secrets come from the environment

Credentials such as the Jasypt master password and AWS keys are resolved from environment placeholders with no committed value; previously committed values must be rotated.
<!-- /harness-remediation -->
