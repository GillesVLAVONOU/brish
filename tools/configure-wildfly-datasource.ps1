$ErrorActionPreference = 'Stop'

$wildFlyHome = 'C:\wildfly\wildfly-41.0.1.Final'
$cliPath = Join-Path $wildFlyHome 'bin\jboss-cli.bat'

if (-not (Test-Path $cliPath)) {
    throw "CLI WildFly introuvable : $cliPath"
}

$env:NOPAUSE = '1'
if ([string]::IsNullOrWhiteSpace($env:JAVA_HOME)) {
    $javaCommand = Get-Command java.exe -ErrorAction Stop
    $env:JAVA_HOME = Split-Path (Split-Path $javaCommand.Source -Parent) -Parent
}

$javaPath = Join-Path $env:JAVA_HOME 'bin\java.exe'
if (-not (Test-Path $javaPath)) {
    throw "Java introuvable dans JAVA_HOME : $env:JAVA_HOME"
}

$javaVersionOutput = & $javaPath -version 2>&1
$javaMajorVersion = [regex]::Match(($javaVersionOutput -join "`n"), 'version "(\d+)').Groups[1].Value
if ($javaMajorVersion -ne '25') {
    throw "Java 25 est requis pour configurer WildFly. JAVA_HOME actuel : $env:JAVA_HOME"
}

$securePassword = Read-Host 'Mot de passe PostgreSQL pour le compte postgres' -AsSecureString
$passwordPointer = [IntPtr]::Zero

try {
    $passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
    $password = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)
    $escapedPassword = $password.Replace('\', '\\').Replace('"', '\"')
    $addCommand = '/subsystem=datasources/data-source=BrishDS:add(jndi-name="java:/jdbc/BrishDS",driver-name="postgresql",connection-url="jdbc:postgresql://localhost:5432/brish",user-name="postgres",password="' + $escapedPassword + '",enabled=true,use-java-context=true)'

    & $cliPath --connect --command=$addCommand
    if ($LASTEXITCODE -ne 0) {
        throw 'La création du datasource a échoué. Vérifiez le compte PostgreSQL et l’existence de BrishDS.'
    }

    & $cliPath --connect --command='/subsystem=datasources/data-source=BrishDS:test-connection-in-pool'
    if ($LASTEXITCODE -ne 0) {
        throw 'Le datasource a été créé, mais le test de connexion a échoué.'
    }

    Write-Output 'BrishDS est configuré et le test de connexion a réussi.'
}
finally {
    if ($passwordPointer -ne [IntPtr]::Zero) {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
    }
    Remove-Variable password, escapedPassword, addCommand, securePassword -ErrorAction SilentlyContinue
}
