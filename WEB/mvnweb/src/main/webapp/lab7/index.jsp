<%-- Лабораторная работа № 7: ввод данных и передача их между запросами. --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="javax.servlet.http.Cookie" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.Date" %>
<%!
    // Декодирует строковое значение Cookie, которое было сохранено в UTF-8.
    private String decodeCookie(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (Exception exception) {
            return "";
        }
    }

    // Экранирует специальные символы перед выводом пользовательского текста в HTML.
    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
%>
<%
    request.setCharacterEncoding("UTF-8");

    String userName = "";
    String pageColor = "#fff4cc";

    // При открытии формы подставляем последние значения из Cookie.
    Cookie[] requestCookies = request.getCookies();
    if (requestCookies != null) {
        for (Cookie cookie : requestCookies) {
            if ("lab7.userName".equals(cookie.getName())) {
                userName = decodeCookie(cookie.getValue());
            } else if ("lab7.pageColor".equals(cookie.getName())) {
                pageColor = decodeCookie(cookie.getValue());
            }
        }
    }

    String errorMessage = null;
    if ("POST".equalsIgnoreCase(request.getMethod())) {
        userName = request.getParameter("userName");
        pageColor = request.getParameter("pageColor");
        userName = userName == null ? "" : userName.trim();

        if (userName.isEmpty()) {
            errorMessage = "Введите имя пользователя.";
        } else if (!("#fff4cc".equals(pageColor) || "#cfe8ff".equals(pageColor)
                || "#d9f2d9".equals(pageColor) || "#ffd6e7".equals(pageColor))) {
            errorMessage = "Выберите допустимый цвет страницы.";
            pageColor = "#fff4cc";
        } else {
            // Сохраняем имя и цвет в Cookie на семь суток.
            Cookie nameCookie = new Cookie("lab7.userName", URLEncoder.encode(userName, "UTF-8"));
            Cookie colorCookie = new Cookie("lab7.pageColor", URLEncoder.encode(pageColor, "UTF-8"));
            String cookiePath = request.getContextPath() + "/lab7";
            nameCookie.setPath(cookiePath);
            colorCookie.setPath(cookiePath);
            nameCookie.setMaxAge(7 * 24 * 60 * 60);
            colorCookie.setMaxAge(7 * 24 * 60 * 60);
            response.addCookie(nameCookie);
            response.addCookie(colorCookie);

            // В сессии увеличиваем число обращений и запоминаем время запросов.
            Integer visitCount = (Integer) session.getAttribute("lab7.visitCount");
            visitCount = visitCount == null ? 1 : visitCount + 1;
            Date previousVisit = (Date) session.getAttribute("lab7.lastVisit");
            Date currentVisit = new Date();

            session.setAttribute("lab7.visitCount", visitCount);
            session.setAttribute("lab7.previousVisit", previousVisit);
            session.setAttribute("lab7.lastVisit", currentVisit);
            session.setAttribute("lab7.userName", userName);
            session.setAttribute("lab7.pageColor", pageColor);

            // Перенаправляем браузер, чтобы новый запрос уже содержал созданные Cookie.
            response.sendRedirect(response.encodeRedirectURL(
                    request.getContextPath() + "/lab7/result.jsp"));
            return;
        }
    }
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Лабораторная работа № 7</title>
</head>
<body>
    <h1>Параметры пользователя</h1>
    <p>Введите имя и выберите цвет страницы с результатом.</p>

    <% if (errorMessage != null) { %>
        <p style="color: red;"><%= errorMessage %></p>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %>/lab7/">
        <p>
            <label for="userName">Имя пользователя:</label>
            <input type="text" id="userName" name="userName"
                   value="<%= escapeHtml(userName) %>" required>
        </p>
        <p>
            <label for="pageColor">Цвет страницы:</label>
            <select id="pageColor" name="pageColor">
                <option value="#fff4cc" <%= "#fff4cc".equals(pageColor) ? "selected" : "" %>>Жёлтый</option>
                <option value="#cfe8ff" <%= "#cfe8ff".equals(pageColor) ? "selected" : "" %>>Голубой</option>
                <option value="#d9f2d9" <%= "#d9f2d9".equals(pageColor) ? "selected" : "" %>>Зелёный</option>
                <option value="#ffd6e7" <%= "#ffd6e7".equals(pageColor) ? "selected" : "" %>>Розовый</option>
            </select>
        </p>
        <button type="submit">Сохранить и показать результат</button>
    </form>
</body>
</html>

