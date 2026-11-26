# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-deserialize-allowlist -->
### sec-deserialize-allowlist — Allowlist classes when deserializing

Cookie and session values are no longer read with a plain ObjectInputStream; a resolveClass allowlist accepts only the expected token class. Never deserialize client-supplied bytes without a class allowlist.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-path-containment -->
### sec-path-containment — Contain file paths under a fixed base

File names derived from request data are matched against a strict pattern, then canonicalized and required to sit directly under the fixed base directory before being opened.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-spel-restricted-context -->
### sec-spel-restricted-context — Never evaluate request data with the default SpEL context

Request parameters are evaluated only in a restricted evaluation context with no type, constructor, method, property or bean resolution. Prefer SimpleEvaluationContext once Spring is upgraded.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-secrets-from-env -->
### sec-secrets-from-env — Load credentials from the environment

Jasypt and AWS credentials resolve from environment placeholders instead of committed literals. Previously committed values remain in git history and must be rotated.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-html-encode-output -->
### sec-html-encode-output — Encode output with HtmlUtils

Attacker-controlled values written to a response go through HtmlUtils.htmlEscape; stripping keywords such as script is not a defense.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-header-and-redirect -->
### sec-header-and-redirect — Keep request data out of response headers

Header values are rejected if they contain control characters, and Location is built from a fixed relative path plus a numeric id so it cannot point at another host.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-constant-time-compare -->
### sec-constant-time-compare — Compare digests in constant time

Use MessageDigest.isEqual for any digest or token comparison instead of String.equals.
<!-- /harness-remediation -->
