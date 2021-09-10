#!/bin/bash
#Este comando crea el keystore del servidor
keytool -genkeypair -keyalg RSA -alias certificado_servidor -keystore keystore_servidor.jks -storepass 69696969
#Este comando obtiene el certificado dentro del keystore del servidor
keytool -exportcert -keystore keystore_servidor.jks -alias certificado_servidor -rfc -file certificado_servidor.pem
#Este comando crea el keystore del cliente
keytool -import -alias certificado_servidor -file certificado_servidor.pem -keystore keystore_cliente.jks -storepass 420420420420
