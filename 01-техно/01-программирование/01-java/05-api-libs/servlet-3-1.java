
    1.
        
        Сервлет - веб-компонент, управляемый контейнером и генерирующий динамический 
            контент. Обычный Java-класс. Взаимодействует с веб-клиентом по парадигме запрос/ответ

        Контейнер сервлетов - часть веб-сервера, обеспечивает сервисы для работы запросов и ответов (request/response),
            управляет жизненным циклом сервлетов. Должен поддерживать HTTP, дополнительно может HTTPS
            Из-за кеширования может модифицировать ответы от сервлетов перед отправкой клиенту
                или запросы от клиентов перед передачей их сервлетам    

        Пример взаимодействия:
            - веб-клиент обращается к веб-серверу через http-request
            - request, принятый веб-сервером передается контейнеру сервлетов
            - контейнер определяет подходящий сервлет и вызывает его с объектами, представляющими request и response
            - сервлет анализирует request, реализует логику, отправляет данные обратно клиенту через response
            - когда сервлет завершает работу, контейнер обеспечивает окончательную отправку response и передает упрвление веб-серверу


    2. Интерфейс сервлета

        основная абстракция API, все сервлеты реализуют напрямую или чаще через наследование 
            классов GenericServlet или HttpServlet

        Обработка request
            интерфейс Servet - базовый интерфейс
                метод service
                вызывается для каждого request, который передается контейнером

            абстрактный класс HttpServlet
                doGet
                doPost
                doPut
                doDelete и др.

        Жизненный цикл
            выражен в методах init(), service(), destroy() интерфейса javax.servlet.Servlet
            - загрузка сервлета
                выполняется контейнером, либо при старте контейнера, либо когда сервлет понадобится для обработки
                загрузка через обычные механизмы загрузки классов
            - инициализация
                выполняется контейнером через вызов метода init()
                во время инициализации могут возникнуть исключения ServletException, UnavailableException,
                    сервлет не активизируется, объект освобождается (destroy() не вызывается), новый экземпляр
                        контейнер может попробовать запустить позже
            - обработка запросов
                после успешной инициализации, обрабатывают клиентские запросы, представляемые 
                    объектами класса ServletRequest (HttpServletRequest для HttpServlet)
                контейнер может запускать сервлеты в многопоточном режиме
                    синхронизировать методы service (doGet, doPost) не рекомендуется
                во время работы сервлет может вбросить ServletException или UnavailableException
                    ServletException - сигнал о каких-то ошибках, контейнер должен как-то ??? "очистить" запрос
                    UnavailableException - сервлет неспособен обработать запрос временно или постоянно
                        если постоянно - контейнер должен удалить сервлет из сервиса, все запросы к этому сервлету
                            будут возвращены как SC_NOT_FOUND (404)
                        если временно - контейнер на период недействия сервлета должен отклонять запросы с 
                            SC_SERVICE_UNAVAILABLE (503) и заголовком Retry-After
                        вообще контейнер может не обрабатывать временно/постоянно и трактовать все UnavailableException как постоянные

                асинхронный режим
                    часто для обработки запроса необходимо ждать ответа БД и т.п.
                    держать поток с сервлетом, ожидая ответа - неэфффективно,
                        тогда асинхронный режим:
                            - сервлет получает запрос
                            - обрабатывает его и запрашивает данные напр. у БД
                            - сервлет завершает обработку без отправки ответа
                            - после получения данных от БД обработчик продолжает обработку в том же потоке или 
                                передачей ресурсов с помощью AsyncContext (* есть подробности)
            - конец сервиса
                контейнер в любой момент (когда нет незаверщенных потоков с обработкой) может удалить сервлет
                при этом контейнер продолжает работать и если придет запрос, новый экземпляр сервлета будет создан
                перед удалением сервлета вызывается метод destroy(), после него экземпляр сервлета 
                может быть очищен GC
                
Learn Java for web dev

    ch. 2

        Сервлеты
            расположение сервлета
                файл web.xml
                <servlet> // описание
                    <servlet-name>MyServlet</servlet-name>
                    <servlet-class>MyServlet</servlet-class>
                </servlet>
                
                <servlet-mapping> // отображение
                    <servlet-name>MyServlet</servlet-name>
                    <url-pattern>/MyS</url-pattern>
                </servlet-mapping>

            Иерархия
                MyServlet -> HttpServlet -> GenericServlet -> Servet

            методы
                init()
                     в GenericServlet два варианта
                        init(ServletConfig)
                        init() (вызывает внутри init c парамеетром)
                    можно через аннотации или в web.xml указывать
                service()
                    вызывается для обработки запросов
                    обычно не переопределяется
                    вызывает методы doPost, doGet

                destroy()

            ServletContext и ServletConfig
                после инициализации сервлет получает ServletConfig (один на сервлет)
                    и ServletContext (один на приложение (JVM))
                ServletConfig используется для:
                    - передача информации во время развертывания в сервлеты 
                    - доступа к ServletContext
                ServletContext используется для:
                    - доступа к параметрам приложения
                    - установки атрибутов, к которым должен быть доступ из всего приложения
                    - получение информации о сервере, контейнере (версия апи и т.п.)

            Типы параметров сервлета
                - initialization parameters
                    определяются в web.xml
                        <servlet>
                            <init-param>
                                <param-name>email</param-name>
                                <param-value>23@com</param-value>
                            </init-param>
                        </servlet>
                    доступ через 
                        ServletConfig cfg = getServletConfig();
                        cgf.getInitParam(<имя>)
                    при инициализации:
                        - контейнер создает уникальный ServletConfig для сервлета
                        - контейнер читает init параметры из дескриптора (web.xml) в объект ServletConfig
                        - контейнер передает ServletConfig в метод init() сервлета
                        * контейнер читает параметры один раз при развертывании

                - context parameters
                    <context-param> // !! не внутри <servlet>
                        <param-name>...</param-name>
                        <param-value>...</param-value>
                    </context-param>
                    в отличие от init-параметров доступны для всех сервлетов внутри приложения
                    доступ:
                        getServletContext().getInitParam("name");

            RequestDispatcher
                2 способа изменить поток запросов:
                    - редирект 
                        запрос перенаправляется на другой url через вызов sendRedirect() на response объекте
                        на стороне клиента выполняется
                    - dispatching the request
                        отравка  запроса другому компоненту приложения            
                        на стороне сервера вызывается на объекте request

                получение объекта RequestDispatcher
                    - из request
                        RequestDispatcher view = request.getRequestDispatcher("details.jsp");
                        параметром - путь к новому ресурсу, если путь начинается с "/" - от корня приложения,
                            иначе - от исходного запроса
                    - из контекста
                        RequestDispatcher view = getServletContext.getRequestDispatcher("/details.jsp");
                    использование:
                        view.forward(request, response);
                    
        Фильтры
            переиспользуемые компоненты. которые изменяют содержимое запросов, ответов, заголовков
                - доступ к содержимому или заголовкам запроса до вызова запроса
                - выполнение действий над компонентами используюя цепочку фильтров
                - модификация заголовков и содержимого ответа перед отправкой

            создание:
                класс, реализующий javax.servlet.Filter c конструктором без параметров
                <filter>
                    <filter-name>/*псевдоним для привязки к url или сервлету*/</filter-name>
                    <filter-class>/*полное имя класса для контейнера*/</filter-class>
                </filter>

            назначение:
                <filter-mapping>
                    <servlet-name>/* к сервлету привязка */</servlet-name>
                    <url-pattern>/* или к url,  напр /*  */</url-pattern>
                </filter-mapping>

            обычно в приложении используются фильтры:
                аутентификационные, кеширующие, сжимающие данные, кодирующие, логгирующие и др.

        Аннотации
            с версии 3.0 вместе с web.xml можно аннотациями
                @WebFilter
                @WebInitParam
                @WebListener
                @WebServlet

        Java Server Pages (JSP)
            минус сервлетов: 
                html код расположен в java коде

            элементы JSP:
                - Директивы
                    указания JSP-контейнеру во время разбора страницы 
                    <%@ <directive> {attribute=value} %>
                    
                    Page Directive
                        указание особенностей страницы
                        <%@ page attribute=value%>
                        варианты аттрибутов:
                            autoFlush       авто записывать буфер при заполнении
                            buffer          определяет буфер для выходного потока страницы
                            contentType     MIME-тип и кодировка ответа
                            errorPage       назначает страницу для обработки ошибок (авто вызывается при возникновении ошибки)
                            isErrorPage     определяет что текущая страница - для обработки ошибок
                            import      
                            и др.

                    Include Directive
                        включение статических ресурсов
                        <%@ include file="relative url" %>

                        пример
                            <%@ include file="header.jsp" %>
                            <p>content</p>
                            <%@ include file="footer.jsp" %>

                    Taglib
                        можно создавать пользовательское поведение (custom tag, tag library)
                            taglib вызывает это поведение
                        <%@ taglib uri="uri" prefix="prefix" %>
                            uri - относительный или абсолютный путь к библиотеке, prefix - действие
                        пример:
                            <%@ taglib uri="/helloTagLib" prefix="helloTag"%>
                            ...
                            <helloTag:hello/>

                Declarations
                    позволяет определять методы и переменные, после чего они доступны выражениям и 
                        скриптлетам на странице
                    <%!  %>

                Expressions
                    конструция java возвращающая String или объект, конвертируемый в String
                    <%= expression %>

                Scriptlet
                    блок java кода внутри <% %> для получения динамич. содержимого

                    Пример:
                        <%! 
                            public String hello() {
                                String msg = "Hello";
                                return msg;
                            }   
                        %>
                        message from scriptlet <% hello(); %>
                        message from expression <%=hello() %>

                Implicit object
                        объекты передаются между компонентами как атрибуты 4-х областей видимости
                            Application/web context     javax.servlet.ServletContext
                            Session                     javax.servlet.http.HttpSession
                            Request                     javax.servlet.ServletRequest
                            Page                        javax.servlet.jsp.JspContext

                        доступ к ним через соответствующие объекты и методы setAttribute, getAttribute
                        у jsp страниц есть доступ к особым объектам (implicit) через переменные
                            эти переменные могут быть в скриплетах, выражениях, EL-выражениях

                        виды implicit-объектов:
                            - application
                                реализует интерфейс javax.servlet.ServletContext
                                можно получить доступ к параметрам инициализации из web.xml (context-param)
                                    через getInitParameter(<name>)
                            - config
                                интерфейс ServletConfig
                                доступ к иниц. параметрам из web.xml
                                ? как указать параметры, если они указываются в блоке <servlet>
                            - exception
                                для управления ошибками (с директивой errorPage page)
                            - out 
                                экземпляр класса JspWriter
                                можно выводить в response поток через out.print(...)
                            - page 
                                объект класса Object, представляет текущую страницу
                            - pageContext
                                предоставляет доступ к некоторым атрибутам страницы и implicit-объектам
                                нет аналога у программных servlet-ов
                            - request
                                экземпляр javax.servlet.http.HttpServletRequest
                                используется для получения параметров запроса, заголовков, значений запросов
                            - response
                                экземпляр javax.servlet.http.HttpServletResponse
                                используется для установки куков, параметров ответа, перенаправления
                            - session
                                экземпляр javax.servlet.http.HttpSession

                Standart Action
                    <jsp:include>
                        для включения содержимого отдельного компонента в текущую страницу
                        синтаксис
                            <jsp:include page="relativeURL" flush="true"/>
                        напр.
                            <jsp:include page="other.jsp"/>

                    <jsp:forward>
                        перенаправление текущего запроса на другую статич. страницу, jsp-страницу или сервлету
                        синтаксис
                            <jsp:forward page="relativeURL"/>

                    работа с javaBean   
                        <jsp:useBean id="someId" class="someClass"/> 
                            объявление и инициализация javaBean объекта
                        <jsp:setProperty name="someId" propety="someProperty">

    ch. 3

        варианты построения веб-приложения (этапы развития Java EE):
            - на сервлетах
                вывод через PrintWriter::print
                минус: представление (view) расположено в коде сервлета
            - JSP
                установка атрибутов контекста и вызов jsp-страницы через RequestDispatcher
                    на странице атрибут преобразовывался в объект и получали его реквизиты
                плюс: в бизнес-логике теперь нет представления
                минус: в представлении есть бизнес-логика
            - Expression Language (EL) и JSP Standart Tag Library (JSTL)
                созданы для решения ограниченности JSP (для создания функциональных приложений приходилось
                    использовать код в сниппетах и выражениях)

        EL
            задача - создание безскриптовых JSP компонентов
            2 варианта использования:   
                - получение объектов из 4-х областей видомости
                    ищет в порядке расширения области (Page -> Request ->Session -> Application)
                - доступ к параметрам запроса, заголовкам, кукам, иниц. параметрам

            ${выражение} - непосредственное вычисление (компилируется при компиляции страницы
                    и вычисляется при выполнении страницы ???)
            #{выражение} - отложенное вычисление (допустимо только для аттрибутов тегов)

            Синтаксис
                Литералы
                    Boolean     true / false
                    Integer     -4 0 13
                    Floating    5.23 -3.0 2.3E2
                    String      "adf" 'dsfd'
                    Null        null

                Операторы
                   арифметические:
                        + - * / mod
                    отношения:
                        == != < > <= >=
                        и их текстовые аналоги eq ne lt gt le ge 
                    логические 
                        && || not

            Оператор .
                пример jsp файла
                    // предварительно был установлен аттрибут user request.setAttribute("user", )
                    <jsp:useBean id="user" class="com.fddfdf.User"/> 
                    <p>User is ${user.name}</p>

                можно получать доступ к объектам:
                    для атрибутов сохраненных в 4-х областях видимости
                    для неявных (implicit) объектов

                объекты: либо javaBean, либо map, 
                    соответственно через точку указывается либо свойство, либо ключ

            Оператор []
                для работы с массивами, списками переданными в область видимости
                также как аналог (.) при работе с бинами, картами

                внутри []:
                    индекс (числом или строкой)     слева от [] - массив или список
                    строковый литерал               слева от [] - бин или карта
                    implicit-объект EL или аттрибут из областей видимости
                    вложенное выражение

                примеры
                    массив
                        String [] books = {"sd", "ser"};
                        request.setAttribute("books", books);
                        ...
                        <p>Book: ${books[0]}</p>
                    карта
                        Map<String, String> m = new HashMap<>();
                        m.put("b", "book");
                        request.setAttribute("map", m);
                        request.setAttribute("key", "b");
                        ..
                        <p>Book: ${map[key]}</p>

            implicit-объекты
                cookie              map, имя / объект Cookie
                header              map
                headerValues        map, имя заголовка / массив всех возможных значений
                initParam           map
                param               map 
                paramValues         map
                pageContext         PageContext объект
                applicationScope    map, все переменные из области видимости application
                pageScope
                requestScope
                sessionScope

                доступ к параметрам запроса
                    <form action="somewhere" method="post">
                        <input type="text" name="Title">
                        <p>A1 <input type="text" name="Author"></p>
                        <p>A2 <input type="text" name="Author"></p> // c одинаковыми именами - в массив
                    </form>
                    ...
                    <p>Title: ${param.Title}</p>
                    <p>A1: ${paramValues.Author[0]}</p>
                    <p>A2: ${paramValues.Author[1]}</p>

                доступ к заголовкам 
                    аналог request.getHeader() request.getHeaders()
                    напр. ${header.user-agent} или ${header["user-agent"]}

                доступ к куки
                    Cookie c = new Cookie("userName", "Vasya");
                    c.setPath("/");
                    response.addCookie(c);
                    ...
                    ${cookie.userName.value}

                доступ к атрибутам областей
                    HttpSession session = request.getSession();
                    Book b = new Book("title");
                    session.setAttribute("book", b);
                    ...
                    ${sessionScope.book.bookTitle}

            EL-функции
                вызов публичных статических методов классов
                метод класса отображается на EL-функцию через tag library descriptor (TLD)
                в общем 3 компонента:
                    - публичный статический метод
                    - jsp-страница, вызывающая метод через EL-функцию
                    - TLD, связывающий метод с функцией

                пример дескриптора
                    <taglib version="22" ...>
                        <uri>elFunc</uri> // идентификатор дескриптора
                        <function>
                            <name>fname</name>
                            <function-class>com.sf.Function</function-class>
                            <function-signature>String fname()</function-signature>
                        </function> 
                    </taglib>
                вызов функции:
                    <%@ taglib prefix="elf" uri="elFunc"%> // подключение дескриптора по uri
                    <body>
                        ${elf:fname()}              // вызов функции через префикс и по имени, определенном в дескрипторе
                    </body> 
                


        JSLT
            сниппеты усложняют сопровождение jsp-страниц, стандартные действия JSP слишком ограничены
                JSTL - для решения таких проблем
            разбита на области:
                Core (prefix c), работа с XML (x), работа с БД (sql) и др

            Core tag library
                <c:out>
                    вычисляет результат выражение и выводит его через JspWriter
                    аналог <%=...%>
                    синтаксис: 
                        <c:out value="value" [escapeXML="{true/false}"] [default="defaultValue"] />
                        <c:out value="value" [escapeXML="{true/false}"]>
                            default value
                        </c:out>
                        здесь
                            value           само выражение, при выводе преобразуется в строку
                            escapeXML       символы типа <>& преобразуются в код или нет
                            defaultValue    значение по умолчанию, если результат выражения null
                    пример
                        <c:out value="hello" />

                <c:set>
                    используется для:
                        - установки значения свойства бин-объекта (аналог <jsp:setProperty>)
                        - установки значения в карте
                        - создания и установки значения переменной в одной из областей видимости

                    атрибуты:
                        value   Object  выражение
                        var     String  имя переменной, которая будет содержать результат вычисления value
                        scope   String  область видимости
                        target  Object  объект, чьё свойство будет установлено (JavaBean or Map)

                    установка значения переменной 
                        <c:out value="value" var="varName" [scope="page/request/session/application"] />

                        <c:out var="hellVar" value="Hello" />   // по умолчанию в области видимости page
                        эквивалентный сниппет:
                            <% 
                                String hellVar = "hello";
                                pageContext.setAttribute("hellVar", hellVar);
                            %>
                        <c:out var="hellVar" value="Hello" scope="session"/>   
                        <c:out var="title" value="${book.title}"/>   

                    установка свойства:
                        <c:set value="value" target="target" property="propertyName"/>
                        
                        <c:set target="book" property="book.title" value="How To"/> // ~ book.setTitle("How To");
                        <c:set target="bookMap" property="id" value="1"/> // ~ bookMap.put("id", 1);

                <c:remove>
                    удаление переменной из областей видимости
                    <c:remove var="varName"  [scope="page/request/session/application"]/>

                <c:catch>
                    перехват java.lang.Throwable исключений из вложенных выражений
                    <c:catch [var="varName"]> // в эту переменную заносится объект Throwable если возникает исключение
                        // тело: вложенные
                    </c:catch>

                    пример
                        <c:catch var="exception">
                            <% int i = 1/0; %>
                        </c:catch>
                        <c:if test="${exception != null}">
                            ...
                        </c:if>

                <c:if>
                    условное вычисление
                    <c:if test="testCondition" var="varName" [scope=".."]/>
                    <c:if test="testCondition" [var="varName"] [scope=".."]>
                        ...
                    </c:if>
                        test    выражение, которое вычисляется и проверяется
                                    если истина - выполняется тело
                        var     переменная в области, в которую можно помещать результат выражения из test

                    пример:
                        <c:set var="number" value="9" />
                        <c:if test="${number < 10}">
                            <c:out value="number less than 10" />
                        </c:if>

                <c:choose>
                    аналог switch
                    <c:choose>
                        <c:when test="testCondititon1"> body </c:when>
                        <c:when test="testCondititon2"> body </c:when>
                        <c:otherwise> body </c:otherwise>
                    <c:choose>

                <c:forEach>
                    итерация по коллекции
                        <c:forEach [var="varName"] items="collection" [varStatus="varStatusName"]
                            [begin="begin"] [end="end"] [step="step"]>
                                ...
                        </c:forEach>
                    цикл
                        <c:forEach [var="varName"] [varStatus="varStatusName"]
                            begin="begin" end="end" [step="step"]>
                                ...
                        </c:forEach>  


                    атрибуты:
                        var         String      имя переменной для хранения текущего значения итерации
                        items                   коллекция для итерации (коллекции, итерируемые, строки)
                        varStatus   String      имя переменной для хранения статуса итерации    
                                                    javax.servlet.jsp.jstl.core.LoopTagStatus
                        begin       int         начальный индекс
                        end         int         конечный индекс включительно
                        step        int         шаг

                    пример
                        <c:forEach var="i" begin="1" end="3">
                            <p>Item <c:out value="${i}"/> </p>    
                        </c:forEach>

                <c:forTokens>
                    итерация над токенами, полученными из строки по заданным разделителям
                    синтаксис:
                        <c:forTokens items="stringOfTokens" delims="delimiters"
                        [var="varName"] [varStatus="varStatusName"] 
                        [begin="begin"] [end="end"] [step="step"]> ... </c:forTokens>
                    атрибуты:
                        var         String      имя переменной для хранения текущего значения итерации
                        items       String      строка для итерации
                        delims      String      набор разделителей
                        varStatus   String      имя переменной для хранения статуса итерации    
                                                    javax.servlet.jsp.jstl.core.LoopTagStatus
                        begin       int         начальный индекс
                        end         int         конечный индекс включительно
                        step        int         шаг
                    пример
                        <c:forTokens items="Java, Kotlin, Scala" delims="," var="lang">
                            <c:out value="${lang}"/>
                        </c:forTokens>
                        
                <c:import>
                    импорт ресурсов
                    <c:import url="url" [context="context"] [charEncoding="charEncoding"] 
                            [scope=".."] [var="varName"]>
                        опционально <c:param>
                    </c:import>
                
                    <c:import url="/jsp/header.jsp"> (аналог <jsp:include page="..">)

                <c:param>
                    используется как вложенный тег для <c:include> <c:url> <c:redirect>
                    устанавливает параметры
                    <c:param name="name" value="value">

                <c:url>
                    используется для формирования url
                        <c:url value [context] [var] [scope]/>
                        <c:url value [context] [var] [scope]> <c:param> </c:url>
                    value - обязательный реквизит
                    параметры из блока <c:param> добавляются в строку запроса
                    относительные пути разрешает относительно контекста

                <c:redirect>
                    отправляет запрос клиенту с редиректом
                    <c:redirect url="value" [context="context"]>
                    можно с указанием параметров через <c:param>






                    


