# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-env-secrets -->
### sec-env-secrets — Load credentials from the environment

Credentials such as the Jasypt master password and AWS keys are read from environment placeholders (JASYPT_ENCRYPTOR_PASSWORD, AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY) and never committed in properties files. Rotate any value that was ever committed, since it stays in git history.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-path-containment -->
### sec-path-containment — Contain user-derived file paths

File names taken from requests are resolved with getCanonicalFile() and must stay under the intended base directory plus a separator, otherwise the request is rejected. Apply the same canonicalize-and-contain check to any new user-derived path.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-no-expression-eval -->
### sec-no-expression-eval — Never evaluate request input as an expression

Request parameters are treated as plain text and never parsed with SpEL or similar evaluators. The pinned Spring 4.3.x has no SimpleEvaluationContext, so there is no safe evaluator to substitute.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-no-native-deserialization -->
### sec-no-native-deserialization — No Java native serialization for client-held state

The auth cookie is a base64url role value signed with HMAC-SHA256 (key from AUTH_TOKEN_SECRET) instead of a serialized object. Sign simple values rather than deserializing client-supplied bytes.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-constant-time-compare -->
### sec-constant-time-compare — Compare hashes and secrets in constant time

Digest and secret comparisons use MessageDigest.isEqual on bytes rather than String.equals, so response timing does not leak the match.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-html-escape -->
### sec-html-escape — Escape reflected data with HtmlUtils

Request data returned in response bodies goes through Spring HtmlUtils.htmlEscape instead of blacklist stripping. Controllers returning such strings must be @ResponseBody so the escaped text is the body and not a view name.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-safe-headers-redirects -->
### sec-safe-headers-redirects — Keep request data out of headers and redirect targets

Header values go through headerSafe(), which rejects CR/LF and control characters. The Location header is built by customerLocation(id) as a fixed server-relative path with the id encoded as one path segment, using no request fields.
<!-- /harness-remediation -->
