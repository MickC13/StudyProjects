$ErrorActionPreference = 'Stop'

$tomcatHome = 'C:\ApacheTomcat 9\apache-tomcat-9.0.121'
$keytool = 'C:\Program Files\Java\jdk-21\bin\keytool.exe'
$keystore = Join-Path $tomcatHome 'conf\lab6-keystore.p12'

& $keytool `
    -genkeypair `
    -alias lab6 `
    -keyalg RSA `
    -keysize 2048 `
    -validity 365 `
    -storetype PKCS12 `
    -keystore $keystore `
    -storepass lab6pass `
    -keypass lab6pass `
    -dname 'CN=localhost, OU=Study, O=University, L=Saint Petersburg, ST=Saint Petersburg, C=RU' `
    -ext 'SAN=dns:localhost,ip:127.0.0.1' `
    -noprompt

if ($LASTEXITCODE -ne 0) {
    throw 'Не удалось создать хранилище сертификата.'
}

& $keytool -list -v -keystore $keystore -storepass lab6pass -alias lab6
