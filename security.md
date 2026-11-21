# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-secrets-from-env -->
### sec-secrets-from-env — Load credentials from the environment

Jasypt master password and AWS keys were committed in config/*.properties; they now resolve from JASYPT_ENCRYPTOR_PASSWORD, AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY. Never commit literal credentials; rotate any value that reached git history.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-request-input-bounds -->
### sec-request-input-bounds — Validate request data before decoding or using it as a path

Cookie values are length-capped and checked against a strict Base64 pattern before decoding, and cookie-supplied filenames are resolved against a normalized base Path and rejected if they leave it. Apply the same bound-and-confine step to any new decode or file access on request data.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-no-native-deserialization -->
### sec-no-native-deserialization — Do not use Java serialization for cookies

The auth cookie is a Base64 'role=<n>' string parsed with a strict regex instead of ObjectInputStream.readObject. The token is still unsigned, so add an HMAC if integrity matters.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-restricted-spel -->
### sec-restricted-spel — Evaluate SpEL only in a restricted context

Request parameters are evaluated with type lookup, constructors, methods, property accessors and bean resolution disabled. Never call Expression.getValue() with the default context on user input.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-encode-reflected-output -->
### sec-encode-reflected-output — HTML-encode reflected values

Responses that echo request-derived data use Spring HtmlUtils.htmlEscape, not token stripping, and write the encoded string as the body. Use the same encoder for any new reflected output.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-header-and-redirect-targets -->
### sec-header-and-redirect-targets — Keep request data out of headers and redirects

Response headers built from request data pass through sanitizeHeaderValue, which strips CR/LF, and Location targets come from server-configured context path rather than the request. Reuse these helpers for any new header or redirect.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-authorize-id-lookups -->
### sec-authorize-id-lookups — Authorize before looking up records by id

Account and customer endpoints call SessionAuthorizer.requireAdminSession before using a client-supplied id, returning 403 otherwise. New id-based endpoints should call it first.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-constant-time-compare -->
### sec-constant-time-compare — Compare digests in constant time

Checksums are compared with MessageDigest.isEqual instead of String.equals. Use it for any secret or digest comparison.
<!-- /harness-remediation -->
