package Post;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Модульные тесты сервлета списка посылок с mock-объектами Mockito.
 */
public class ParcelListServletTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletConfig servletConfig;

    private AutoCloseable mocks;
    private ParcelListServlet servlet;
    private StringWriter responseBody;

    /** Подготавливает mock-объекты и инициализирует тестируемый сервлет. */
    @Before
    public void setUp() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);
        servlet = new ParcelListServlet();
        responseBody = new StringWriter();

        when(servletConfig.getInitParameter("office-number")).thenReturn("15");
        servlet.init(servletConfig);
    }

    /** Освобождает ресурсы Mockito после каждого теста. */
    @After
    public void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
    }

    /** Проверяет русскую страницу, сформированную методом doGet. */
    @Test
    public void doGetWritesRussianParcelPage() throws Exception {
        when(request.getParameter("lang")).thenReturn("ru");
        when(request.getParameter("name")).thenReturn("Иванов И. И.");
        when(response.getWriter()).thenReturn(new PrintWriter(responseBody));

        servlet.doGet(request, response);

        String html = responseBody.toString();
        verify(request).setCharacterEncoding("utf-8");
        verify(response).setContentType("text/html;charset=UTF-8");
        assertTrue(html.contains("Почтовое отделение № 15"));
        assertTrue(html.contains("Список посылок клиента Иванов И. И."));
        assertTrue(html.contains("RA123456789RU"));
    }

    /** Проверяет английскую страницу doPost и экранирование имени клиента. */
    @Test
    public void doPostWritesEnglishPageAndEscapesClientName() throws Exception {
        when(request.getParameter("lang")).thenReturn("en");
        when(request.getParameter("name")).thenReturn("<Admin>");
        when(response.getWriter()).thenReturn(new PrintWriter(responseBody));

        servlet.doPost(request, response);

        String html = responseBody.toString();
        verify(response).setContentType("text/html;charset=UTF-8");
        assertTrue(html.contains("Post office No. 15"));
        assertTrue(html.contains("Parcel list for &lt;Admin&gt;"));
        assertTrue(html.contains("RA987654321RU"));
    }

    /** Проверяет ответ 406 при отсутствии обязательного параметра lang. */
    @Test
    public void missingLanguageReturnsNotAcceptable() throws Exception {
        when(request.getParameter("lang")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_ACCEPTABLE,
                "Ожидался параметр lang: ru или en");
        verify(response, never()).getWriter();
    }

    /** Проверяет ответ 406 при неподдерживаемом языке. */
    @Test
    public void unsupportedLanguageReturnsNotAcceptable() throws Exception {
        when(request.getParameter("lang")).thenReturn("de");

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_ACCEPTABLE,
                "Параметр lang может принимать значение ru или en");
        verify(response, never()).getWriter();
    }
}