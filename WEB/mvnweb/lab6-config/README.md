# Лабораторная работа № 6 — конфигурация Tomcat

Исходный код приложения находится в `src/main/webapp/lab6` и защищён настройками `WEB-INF/web.xml`.

Для воспроизведения серверной конфигурации:

1. Добавьте роль и пользователя из `tomcat-users.xml` в файл `%CATALINA_HOME%/conf/tomcat-users.xml`.
2. Выполните `create-keystore.ps1` для создания учебного сертификата `conf/lab6-keystore.p12`.
3. Добавьте содержимое `server-ssl-connector.xml` внутрь элемента `<Service name="Catalina">` файла `%CATALINA_HOME%/conf/server.xml`.
4. Перезапустите Tomcat.
5. Откройте `https://localhost:8443/mvnweb/lab6/` и войдите как `student / student`.

Хранилище сертификата является генерируемым бинарным файлом и в Git не добавляется.
