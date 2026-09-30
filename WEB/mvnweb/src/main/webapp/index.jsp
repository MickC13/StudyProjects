<%-- Стартовая JSP-страница лабораторной работы № 5. --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="javax.servlet.RequestDispatcher" %>
<%
    request.setCharacterEncoding("UTF-8");
    boolean submitted = "search".equals(request.getParameter("action"));
    String name = request.getParameter("name");

    if (submitted && (name == null || name.trim().isEmpty())) {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/ErrorManager.jsp");
        dispatcher.forward(request, response);
        return;
    }
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Поиск посылок клиента</title>
</head>
<body>
    <h1>Интерфейс сотрудника почты</h1>
    <form method="post" action="<%= request.getContextPath() %>/">
        <input type="hidden" name="action" value="search">
        <label for="name">ФИО клиента:</label>
        <input type="text" id="name" name="name">
        <button type="submit">Показать посылки</button>
    </form>

    <% if (submitted) { %>
        <h2>Список посылок клиента <%= name.trim() %></h2>
        <jsp:include page="/WEB-INF/jsp/ParcelData.jsp" />
    <% } %>
</body>
</html>
