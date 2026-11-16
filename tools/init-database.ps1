$ErrorActionPreference = 'Stop'

$psqlCommand = Get-Command psql -ErrorAction SilentlyContinue
if ($psqlCommand) {
    $psqlPath = $psqlCommand.Source
} else {
    $psqlPath = Get-ChildItem "$env:ProgramFiles\PostgreSQL\*\bin\psql.exe" -ErrorAction SilentlyContinue |
        Sort-Object FullName |
        Select-Object -Last 1 -ExpandProperty FullName
}

if (-not $psqlPath) {
    throw 'psql est introuvable. Ajoutez le dossier bin de PostgreSQL au PATH ou installez PostgreSQL.'
}

$scriptPath = Join-Path $PSScriptRoot '..\database\init.sql'
$confirmation = Read-Host 'Ce script supprime les tables et leurs donnees. Tapez REINITIALISER BRISH pour confirmer'
if ($confirmation -cne 'REINITIALISER BRISH') {
    Write-Output 'Initialisation annulee ; aucune modification de la base.'
    exit 0
}

& $psqlPath -v ON_ERROR_STOP=1 -U postgres -h localhost -d postgres -f $scriptPath
if ($LASTEXITCODE -ne 0) {
    throw 'Initialisation PostgreSQL echouee. Verifiez le mot de passe du role postgres et relancez le script.'
}

Write-Output 'Base Brish initialisee.'
