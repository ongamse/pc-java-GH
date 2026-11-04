# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-constant-time-compare -->
### sec-constant-time-compare — Compare hashes and tokens in constant time

saveSettings compared the cookie checksum with String.equals; use MessageDigest.isEqual on the bytes for any hash, secret or token comparison.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-env-credentials -->
### sec-env-credentials — Inject credentials from the environment

Profile properties such as application-aws.properties must reference env placeholders like ${AWS_SECRET_KEY} instead of literal keys, and credentials must never be logged. Rotate any key that was ever committed.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-encode-response -->
### sec-encode-response — HTML-encode request data in responses

Controllers that return request-derived strings must pass them through HtmlUtils.htmlEscape, as debugEscaped already does.
<!-- /harness-remediation -->
