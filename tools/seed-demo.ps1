$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$propertiesPath = Join-Path $projectRoot 'src\main\resources\database.properties'
$properties = @{}
Get-Content $propertiesPath | ForEach-Object {
    if ($_ -match '^\s*([^#=]+)=(.*)$') {
        $properties[$matches[1].Trim()] = $matches[2]
    }
}

$databaseUrl = if ($env:BRISH_DB_URL) { $env:BRISH_DB_URL } else { $properties['database.url'] }
$databaseUser = if ($env:BRISH_DB_USER) { $env:BRISH_DB_USER } else { $properties['database.username'] }
if (-not $databaseUrl -or -not $databaseUser) {
    throw 'Set BRISH_DB_URL and BRISH_DB_USER, or configure them in database.properties.'
}

$databaseMatch = [regex]::Match(
    $databaseUrl,
    '^jdbc:postgresql://(?<host>\[[^\]]+\]|[^:/?]+)(?::(?<port>\d+))?/(?<database>[^?]+)'
)
if (-not $databaseMatch.Success) {
    throw 'BRISH_DB_URL must be a valid PostgreSQL JDBC URL.'
}

$psqlCommand = Get-Command psql -ErrorAction SilentlyContinue
if ($psqlCommand) {
    $psqlPath = $psqlCommand.Source
} else {
    $psqlPath = Get-ChildItem "$env:ProgramFiles\PostgreSQL\*\bin\psql.exe" -ErrorAction SilentlyContinue |
        Sort-Object FullName |
        Select-Object -Last 1 -ExpandProperty FullName
}
if (-not $psqlPath) {
    throw 'psql was not found. Add the PostgreSQL bin directory to PATH or install PostgreSQL.'
}

$previousPgPassword = $env:PGPASSWORD
if ($env:BRISH_DB_PASSWORD) {
    $env:PGPASSWORD = $env:BRISH_DB_PASSWORD
} else {
    $securePassword = Read-Host 'PostgreSQL password (input is hidden)' -AsSecureString
    $env:PGPASSWORD = [System.Net.NetworkCredential]::new('', $securePassword).Password
}

$previousConsoleOutputEncoding = [Console]::OutputEncoding
$previousClientEncoding = $env:PGCLIENTENCODING
[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new($false)
$env:PGCLIENTENCODING = 'UTF8'

try {
    $databaseHost = $databaseMatch.Groups['host'].Value
    if ($databaseHost.StartsWith('[') -and $databaseHost.EndsWith(']')) {
        $databaseHost = $databaseHost.Substring(1, $databaseHost.Length - 2)
    }
    $databasePort = if ($databaseMatch.Groups['port'].Success) {
        $databaseMatch.Groups['port'].Value
    } else {
        '5432'
    }
    $databaseName = $databaseMatch.Groups['database'].Value
    $seedPath = Join-Path $projectRoot 'database\seed-demo.sql'
    $productionUnitMigrationPath = Join-Path $projectRoot 'database\migrations\20261030_add_unite_lignes_production.sql'
    $parametresMigrationPath = Join-Path $projectRoot 'database\migrations\20261101_create_parametres_restaurant.sql'
    $autorisationsMigrationPath = Join-Path $projectRoot 'database\migrations\20261101_create_autorisations.sql'
    $passwordResetMigrationPath = Join-Path $projectRoot 'database\migrations\20261101_create_password_reset_tokens.sql'
    $productionClosureMigrationPath = Join-Path $projectRoot 'database\migrations\20261115_add_date_cloture_to_productions.sql'
    $salesMigrationPath = Join-Path $projectRoot 'database\migrations\20261115_create_sales.sql'

    $previousErrorActionPreference = $ErrorActionPreference
    $ErrorActionPreference = 'Continue'
    $psqlOutput = & $psqlPath -X -v ON_ERROR_STOP=1 `
        -h $databaseHost -p $databasePort -U $databaseUser -d $databaseName `
        -f $productionUnitMigrationPath `
        -f $parametresMigrationPath `
        -f $autorisationsMigrationPath `
        -f $passwordResetMigrationPath `
        -f $productionClosureMigrationPath `
        -f $salesMigrationPath `
        -f $seedPath 2>&1
    $psqlExitCode = $LASTEXITCODE
    $ErrorActionPreference = $previousErrorActionPreference

    if ($psqlExitCode -ne 0) {
        $psqlDetails = if ($psqlOutput) {
            ($psqlOutput | ForEach-Object { "$_" }) -join [Environment]::NewLine
        } else {
            'psql returned no output.'
        }
        throw "Prerequisite migration or demo seed failed (psql exit code $psqlExitCode). psql output:`n$psqlDetails"
    }

    if ($psqlOutput) {
        $psqlOutput | ForEach-Object { Write-Output $_ }
    }
} finally {
    if ($previousErrorActionPreference) {
        $ErrorActionPreference = $previousErrorActionPreference
    }
    [Console]::OutputEncoding = $previousConsoleOutputEncoding
    if ($null -eq $previousClientEncoding) {
        Remove-Item Env:PGCLIENTENCODING -ErrorAction SilentlyContinue
    } else {
        $env:PGCLIENTENCODING = $previousClientEncoding
    }
    if ($null -eq $previousPgPassword) {
        Remove-Item Env:PGPASSWORD -ErrorAction SilentlyContinue
    } else {
        $env:PGPASSWORD = $previousPgPassword
    }
}

Write-Output 'Demo seed applied. No user accounts were created.'
