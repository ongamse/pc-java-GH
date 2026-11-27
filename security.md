# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-cookie-input-validation -->
### sec-cookie-input-validation — Validate cookie data before decoding or deserializing

Cookie values in CustomerController and AdminController are length-capped and checked against the Base64 alphabet before decoding, and the auth token is read through an ObjectInputStream that allowlists only io.shiftleft.model.AuthToken. Reuse these checks for any new cookie-derived data.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-path-containment -->
### sec-path-containment — Contain user-supplied file names

Resolve user-supplied file names against the absolute, normalized base directory with java.nio Path and reject any target that does not stay under it before opening a file.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-constant-time-compare -->
### sec-constant-time-compare — Compare digests in constant time

Use MessageDigest.isEqual for checksum and signature comparisons instead of String.equals.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-reflected-output -->
### sec-reflected-output — Escape and type reflected output

Request-derived strings are escaped with HtmlUtils.htmlEscape and served as text/plain with nosniff. Never evaluate request input as a SpEL expression.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-header-values -->
### sec-header-values — Keep request data out of response headers

Route any header or cookie value through CustomerController.sanitizeHeaderValue, and build Location targets from server-side configuration through customerLocation rather than from request data.
<!-- /harness-remediation -->

<!-- harness-remediation:sec-jasypt-password-env -->
### sec-jasypt-password-env — Load the Jasypt password from the environment

jasypt.encryptor.password is resolved from JASYPT_ENCRYPTOR_PASSWORD at runtime. Never commit the value, and re-encrypt ENC() properties when it is rotated.
<!-- /harness-remediation -->
