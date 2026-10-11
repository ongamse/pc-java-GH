param(
    [Parameter(Mandatory = $true)][string]$TreePath,
    [string]$ReportPath = 'target/dependency-security.json'
)

$ErrorActionPreference = 'Stop'
$tree = Get-Content -LiteralPath $TreePath -Raw | ConvertFrom-Json
$dependencies = [System.Collections.Generic.List[object]]::new()

function Add-Dependency($node) {
    if ($node -ne $tree) {
        $dependencies.Add([pscustomobject]@{
            name = "$($node.groupId):$($node.artifactId)"
            version = $node.version
        })
    }
    foreach ($child in $node.children) { Add-Dependency $child }
}

Add-Dependency $tree
$resolved = @($dependencies | Sort-Object name, version -Unique)
if ($resolved.Count -eq 0) { throw 'The dependency tree is empty; security verification did not run.' }
$queries = @($resolved | ForEach-Object {
    @{ package = @{ ecosystem = 'Maven'; name = $_.name }; version = $_.version }
})
$response = Invoke-RestMethod -Uri 'https://api.osv.dev/v1/querybatch' -Method Post `
    -ContentType 'application/json' -Body (@{queries = $queries} | ConvertTo-Json -Depth 8 -Compress) `
    -TimeoutSec 50
if ($response.results.Count -ne $resolved.Count) { throw 'OSV returned an incomplete response.' }

$findings = @(for ($index = 0; $index -lt $resolved.Count; $index++) {
    foreach ($vulnerability in $response.results[$index].vulns) {
        [pscustomobject]@{
            name = $resolved[$index].name
            version = $resolved[$index].version
            id = $vulnerability.id
        }
    }
})
@{
    checkedAt = [DateTime]::UtcNow.ToString('o')
    database = 'OSV'
    dependencies = $resolved.Count
    findings = $findings
} | ConvertTo-Json -Depth 6 | Set-Content -LiteralPath $ReportPath -Encoding utf8NoBOM
Write-Output "Checked $($resolved.Count) resolved Maven dependencies; $($findings.Count) OSV matches."
if ($findings.Count -gt 0) {
    $findings | Format-Table -AutoSize
    throw 'Known vulnerable dependencies were found. Review the report before merging.'
}
