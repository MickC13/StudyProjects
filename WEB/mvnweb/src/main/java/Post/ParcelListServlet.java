package Post;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
    urlPatterns = "/ParcelListServlet",
    initParams = {
        @WebInitParam(name = "office-number", value = "15")
    }
)
public class ParcelListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Параметр инициализации сервлета (номер отделения почты)
    private String officeNumber;

    @Override
    public void init() throws ServletException {
        super.init();
        officeNumber = getInitParameter("office-number");
        if (officeNumber == null) {
            officeNumber = "не задано";
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String name = request.getParameter("name");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            out.println("<html>");
            out.println("<head><title>Интерфейс сотрудника почты</title></head>");
            out.println("<body>");
            out.println("<h1>Отделение № " + officeNumber + "</h1>");
            out.println("<h2>Список посылок клиента " + (name != null ? name : "не указан") + "</h2>");
            out.println("<table border='1'>");
            out.println("<tr><td><b>Трек-номер</b></td><td><b>Отправитель</b></td>"
                    + "<td><b>Статус</b></td><td><b>Дата поступления</b></td></tr>");
            out.println("<tr><td>RA123456789RU</td><td>ООО \"Техносклад\"</td>"
                    + "<td>Ожидает выдачи</td><td>10.09.2026</td></tr>");
            out.println("<tr><td>RA987654321RU</td><td>Иванов П.С.</td>"
                    + "<td>Выдана</td><td>05.09.2026</td></tr>");
            out.println("</table>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
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