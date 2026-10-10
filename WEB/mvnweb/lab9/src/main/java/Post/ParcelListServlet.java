package Post;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Отображает список посылок на русском или английском языке.
 *
 * <p>Язык интерфейса задаётся обязательным параметром {@code lang}:
 * {@code ru} для русского и {@code en} для английского языка.</p>
 */
@WebServlet(
    urlPatterns = "/ParcelListServlet",
    initParams = {
        @WebInitParam(name = "office-number", value = "15")
    }
)
public class ParcelListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /** Параметр инициализации: номер отделения почты. */
    private String officeNumber;

    /** Создаёт сервлет списка посылок. */
    public ParcelListServlet() {
        super();
    }

    /** Инициализирует номер отделения из параметра конфигурации сервлета. */
    @Override
    public void init() throws ServletException {
        super.init();
        officeNumber = getInitParameter("office-number");
        if (officeNumber == null) {
            officeNumber = "не задано";
        }
    }

    /**
     * Формирует локализованную HTML-страницу со списком посылок.
     *
     * @param request HTTP-запрос с параметрами {@code lang} и {@code name}
     * @param response HTTP-ответ с HTML-страницей
     * @throws ServletException если контейнер сервлетов не может обработать запрос
     * @throws IOException если произошла ошибка ввода-вывода
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String name = request.getParameter("name");
        String lang = request.getParameter("lang");

        if (lang == null) {
            response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE,
                    "Ожидался параметр lang: ru или en");
            return;
        }
        if (!"ru".equalsIgnoreCase(lang) && !"en".equalsIgnoreCase(lang)) {
            response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE,
                    "Параметр lang может принимать значение ru или en");
            return;
        }

        Locale locale = "en".equalsIgnoreCase(lang) ? Locale.ENGLISH : new Locale("ru", "RU");
        ResourceBundle messages = ResourceBundle.getBundle("ParcelMessages", locale);
        String safeName = name == null || name.trim().isEmpty()
                ? messages.getString("client.unknown") : escapeHtml(name.trim());
        String encodedName = name == null ? "" : URLEncoder.encode(name, "UTF-8");

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<html>");
            out.println("<head><meta charset='UTF-8'><title>" + messages.getString("page.title")
                    + "</title></head>");
            out.println("<body>");
            out.println("<p>" + messages.getString("language") + ": "
                    + "<a href='?lang=ru&amp;name=" + encodedName + "'>"
                    + messages.getString("language.ru") + "</a> | "
                    + "<a href='?lang=en&amp;name=" + encodedName + "'>"
                    + messages.getString("language.en") + "</a></p>");
            out.println("<form method='get'>");
            out.println("<input type='hidden' name='lang' value='" + lang.toLowerCase(Locale.ROOT) + "'>");
            out.println("<label>" + messages.getString("client.label")
                    + ": <input type='text' name='name' value='" + escapeHtml(name == null ? "" : name)
                    + "'></label>");
            out.println("<button type='submit'>" + messages.getString("show") + "</button>");
            out.println("</form>");
            out.println("<h1>" + format(messages, "office", officeNumber) + "</h1>");
            out.println("<h2>" + format(messages, "parcel.list", safeName) + "</h2>");
            out.println("<table border='1'>");
            out.println("<tr><th>" + messages.getString("tracking.number") + "</th><th>"
                    + messages.getString("sender") + "</th><th>" + messages.getString("status")
                    + "</th><th>" + messages.getString("arrival.date") + "</th></tr>");
            out.println("<tr><td>RA123456789RU</td><td>" + messages.getString("parcel.one.sender")
                    + "</td><td>" + messages.getString("parcel.one.status")
                    + "</td><td>10.09.2026</td></tr>");
            out.println("<tr><td>RA987654321RU</td><td>" + messages.getString("parcel.two.sender")
                    + "</td><td>" + messages.getString("parcel.two.status")
                    + "</td><td>05.09.2026</td></tr>");
            out.println("</table>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    private String format(ResourceBundle messages, String key, Object value) {
        return java.text.MessageFormat.format(messages.getString(key), value);
    }

    private String escapeHtml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
