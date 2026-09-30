<%-- JSP-страница вывода ошибки ввода. --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Ошибка ввода</title>
</head>
<body>
    <h1>Ошибка</h1>
    <p>Необходимо ввести ФИО клиента.</p>
    <p><a href="<%= request.getContextPath() %>/">Вернуться к форме</a></p>
</body>
</html>
