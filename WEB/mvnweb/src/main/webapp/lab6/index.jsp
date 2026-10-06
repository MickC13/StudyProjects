<%-- Защищённая JSP-страница лабораторной работы № 6. --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Защищённый раздел почты</title>
</head>
<body>
    <h1>Защищённый раздел сотрудника почты</h1>
    <p>Пользователь: <strong><%= request.getRemoteUser() %></strong></p>
    <p>Способ аутентификации: <strong><%= request.getAuthType() %></strong></p>
    <p>Роль postal-user: <strong><%= request.isUserInRole("postal-user") ? "подтверждена" : "не подтверждена" %></strong></p>

    <h2>Доступные посылки</h2>
    <jsp:include page="/WEB-INF/jsp/ParcelData.jsp" />
</body>
</html>
