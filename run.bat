@echo off
setlocal

rem Set Java classpath with Spring Boot dependencies
set CLASSPATH=target\classes
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\springframework\boot\spring-boot\3.2.3\spring-boot-3.2.3.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\springframework\boot\spring-boot-autoconfigure\3.2.3\spring-boot-autoconfigure-3.2.3.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\springframework\spring-core\6.1.4\spring-core-6.1.4.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\springframework\spring-context\6.1.4\spring-context-6.1.4.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\springframework\spring-beans\6.1.4\spring-beans-6.1.4.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\springframework\spring-web\6.1.4\spring-web-6.1.4.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\springframework\spring-webmvc\6.1.4\spring-webmvc-6.1.4.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\springframework\boot\spring-boot-starter-web\3.2.3\spring-boot-starter-web-3.2.3.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\springframework\boot\spring-boot-starter\3.2.3\spring-boot-starter-3.2.3.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\springframework\boot\spring-boot-starter-json\3.2.3\spring-boot-starter-json-3.2.3.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\com\fasterxml\jackson\core\jackson-databind\2.15.3\jackson-databind-2.15.3.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\com\fasterxml\jackson\core\jackson-core\2.15.3\jackson-core-2.15.3.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\com\fasterxml\jackson\core\jackson-annotations\2.15.3\jackson-annotations-2.15.3.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\springframework\boot\spring-boot-starter-data-jpa\3.2.3\spring-boot-starter-data-jpa-3.2.3.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\springframework\spring-orm\6.1.4\spring-orm-6.1.4.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\springframework\spring-jdbc\6.1.4\spring-jdbc-6.1.4.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\springframework\spring-tx\6.1.4\spring-tx-6.1.4.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\springframework\spring-aop\6.1.4\spring-aop-6.1.4.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\springframework\spring-expression\6.1.4\spring-expression-6.1.4.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\hibernate\orm\hibernate-core\6.4.1.Final\hibernate-core-6.4.1.Final.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\com\h2database\h2\2.2.224\h2-2.2.224.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\jakarta\persistence\jakarta.persistence-api\3.1.0\jakarta.persistence-api-3.1.0.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\jakarta\transaction\jakarta.transaction-api\2.0.1\jakarta.transaction-api-2.0.1.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\apache\tomcat\embed\tomcat-embed-core\10.1.19\tomcat-embed-core-10.1.19.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\apache\tomcat\embed\tomcat-embed-websocket\10.1.19\tomcat-embed-websocket-10.1.19.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\apache\tomcat\embed\tomcat-embed-el\10.1.19\tomcat-embed-el-10.1.19.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\springframework\boot\spring-boot-starter-security\3.2.3\spring-boot-starter-security-3.2.3.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\springframework\security\spring-security-config\6.2.1\spring-security-config-6.2.1.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\springframework\security\spring-security-web\6.2.1\spring-security-web-6.2.1.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\springframework\security\spring-security-core\6.2.1\spring-security-core-6.2.1.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\springframework\security\spring-security-crypto\6.2.2\spring-security-crypto-6.2.2.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\io\jsonwebtoken\jjwt-api\0.11.5\jjwt-api-0.11.5.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\io\jsonwebtoken\jjwt-impl\0.11.5\jjwt-impl-0.11.5.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\io\jsonwebtoken\jjwt-jackson\0.11.5\jjwt-jackson-0.11.5.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\springframework\boot\spring-boot-starter-validation\3.2.3\spring-boot-starter-validation-3.2.3.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\hibernate\validator\hibernate-validator\8.0.0.Final\hibernate-validator-8.0.0.Final.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\slf4j\slf4j-api\2.0.9\slf4j-api-2.0.9.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\ch\qos\logback\logback-classic\1.4.11\logback-classic-1.4.11.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\ch\qos\logback\logback-core\1.4.11\logback-core-1.4.11.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\springframework\spring-jcl\6.0.10\spring-jcl-6.0.10.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\yaml\snakeyaml\2.2\snakeyaml-2.2.jar
set CLASSPATH=%CLASSPATH%;C:\Users\nikhi\.m2\repository\org\postgresql\postgresql\42.6.0\postgresql-42.6.0.jar

echo Starting CineReserve...
java -cp "%CLASSPATH%" com.example.movie.MovieReservationApplication

pause
