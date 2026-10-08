<%-- Лабораторная работа № 7: вывод данных из Cookie и HttpSession. --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="javax.servlet.http.Cookie" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%!
    // Декодирует значение Cookie, сохранённое первой JSP-страницей.
    private String decodeCookie(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (Exception exception) {
            return "";
        }
    }

    // Защищает HTML от специальных символов в пользовательских данных.
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
    String cookieUserName = "";
    String cookiePageColor = "#ffffff";

    // Получаем значения Cookie из нового HTTP-запроса браузера.
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if ("lab7.userName".equals(cookie.getName())) {
                cookieUserName = decodeCookie(cookie.getValue());
            } else if ("lab7.pageColor".equals(cookie.getName())) {
                cookiePageColor = decodeCookie(cookie.getValue());
            }
        }
    }

    Integer visitCount = (Integer) session.getAttribute("lab7.visitCount");
    Date previousVisit = (Date) session.getAttribute("lab7.previousVisit");
    Date lastVisit = (Date) session.getAttribute("lab7.lastVisit");

    // Прямое открытие страницы без заполнения формы возвращает пользователя к вводу.
    boolean allowedColor = "#fff4cc".equals(cookiePageColor) || "#cfe8ff".equals(cookiePageColor)
            || "#d9f2d9".equals(cookiePageColor) || "#ffd6e7".equals(cookiePageColor);
    if (visitCount == null || !allowedColor) {
        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/lab7/"));
        return;
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    String previousVisitText = previousVisit == null
            ? "Это первое обращение"
            : dateFormat.format(previousVisit);
    String lastVisitText = dateFormat.format(lastVisit);
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Данные Cookie и сессии</title>
</head>
<body style="background-color: <%= cookiePageColor %>;">
    <h1>Здравствуйте, <%= escapeHtml(cookieUserName) %>!</h1>

    <h2>Содержимое Cookie</h2>
    <table border="1" cellpadding="6">
        <tr>
            <th>Имя Cookie</th>
            <th>Значение</th>
        </tr>
        <tr>
            <td>lab7.userName</td>
            <td><%= escapeHtml(cookieUserName) %></td>
        </tr>
        <tr>
            <td>lab7.pageColor</td>
            <td><%= escapeHtml(cookiePageColor) %></td>
        </tr>
    </table>

    <h2>Переменные сессии</h2>
    <table border="1" cellpadding="6">
        <tr>
            <th>Переменная</th>
            <th>Значение</th>
        </tr>
        <tr>
            <td>Количество обращений</td>
            <td><%= visitCount %></td>
        </tr>
        <tr>
            <td>Предыдущее обращение</td>
            <td><%= previousVisitText %></td>
        </tr>
        <tr>
            <td>Текущее обращение</td>
            <td><%= lastVisitText %></td>
        </tr>
    </table>

    <p><a href="<%= request.getContextPath() %>/lab7/">Изменить параметры</a></p>
</body>
</html>

