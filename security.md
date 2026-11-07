# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-path-containment -->
### sec-path-containment — Contain user-influenced file paths

CustomerController.saveSettings now resolves the cookie-supplied file name against the canonical static/ directory and rejects any target outside it. Use the same canonical containment check for any user-influenced file name.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-spel-allowlist -->
### sec-spel-allowlist — Never evaluate request data as SpEL unfiltered

SearchController.doGetSearch only evaluates input that matches a length-capped digits and arithmetic-operator allowlist. Spring 4.3 here has no SimpleEvaluationContext, so keep that gate in front of any expression parser.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-env-secrets -->
### sec-env-secrets — Load secrets from the environment

The Jasypt password and AWS secret key are now ${JASYPT_ENCRYPTOR_PASSWORD} and ${AWS_SECRET_KEY:} placeholders instead of committed literals. Supply them at deploy time and rotate the old values, which remain in git history.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-signed-auth-cookie -->
### sec-signed-auth-cookie — No native Java deserialization of cookies

The auth cookie is now a role plus an HMAC-SHA256 signature checked with MessageDigest.isEqual, instead of an ObjectInputStream payload. Set AUTH_TOKEN_SECRET in multi-instance deployments and never deserialize request data with Java serialization.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-html-encode-output -->
### sec-html-encode-output — Encode attacker data at the HTML sink

CustomerController.debug and SearchController.doGetSearch now return HtmlUtils.htmlEscape(...) output rather than stripping substrings. Use the same encoder for any response written as HTML.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-constant-time-compare -->
### sec-constant-time-compare — Compare hashes in constant time

saveSettings compares the cookie hash with MessageDigest.isEqual instead of String.equals. Use it for any secret, hash or MAC comparison in this repo.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-object-authz-guard -->
### sec-object-authz-guard — Guard id-addressed endpoints

Account and customer endpoints that look up records by a client-supplied id call requireAdmin before any repository access and return 403 otherwise. New id-addressed endpoints should call the same guard.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-header-and-redirect-validation -->
### sec-header-and-redirect-validation — Validate request data placed in response headers

Request-derived header values pass through sanitizeHeaderValue, which strips CR, LF and control characters. Location headers also go through customerLocation, which only accepts single-slash relative paths.
<!-- /harness-remediation -->
