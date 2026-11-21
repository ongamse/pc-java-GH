# Security

Security rules for this repository. Marked blocks are maintained by Harness remediation. Text outside those blocks is left as written.


<!-- harness-remediation:sec-requests-timeout -->
### sec-requests-timeout — Set timeouts on outbound HTTP calls

Every requests call in this repo passes an explicit timeout= so a stalled server cannot hold a socket open indefinitely (CWE-770).
<!-- /harness-remediation -->
