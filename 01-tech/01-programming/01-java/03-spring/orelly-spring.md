## 2

### 1. Структура приложения

    src
    --main
    ----webapp
    ------WEB-INF
    --------web.xml

### 2. Настройка Dispatcher Servlet

#### Подключение сервлета

файл web.xml

    <web-app ...>
        <servler>
            <servlet-name>dispatcher</servlet-name>
            <servlet-class>org.springframework.web.servlet.DispatcherServler</servlet-class>
            <load-on-statup>1</load-on-statup>
        </servlet>
        
        <servler-mapping>
            <servlet-name>dispatcher</servlet-name>
            <url-pattern>/</url-pattern>
        </servler-mapping>
    </web-app>


задаем имя
задаем класс
указываем, что загружается первым при старте приложения
настраиваем маппинг

#### Задание настроек

При старте Spring по умолчанию ищет настройки в файле `<имя сервлета>-servlet.xml` в папке `WEB-INF`, в данном примере `/WEB-INF/dispatcher-servlet.xml`

Эти настройки - обычные Spring настройки (`<beans> ...`)

можно переопределить расположение файла

    <servlet>
        ...
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>/WEB-INF/spring/dispatcher-servlet.xml</param-value>
        </init-param>
    </servlet>

Можно через wildcard указывать несколько файлов:

    ...
    <param-value>/WEB-INF/spring/*-servlet.xml</param-value>
    ...

### 3. Root Application Context

Содержит логику верхнего уровня (БД, бизнес-логика), не связанную с Spring MVC

Один на несколько сервлетов

Внутри web.xml можно объявлять несколько сервлетов

    <web-app>
        <servlet>
            ...
        </servlet>
        <servlet>
            ...
        </servlet>
    </web-app>

#### Настройка

Добавить в web.xml listener

    <web-app>
        <listener>
            <listener-class> org.springframework...ContextLoadListener</listener-class>
        </listener>

Listener отслеживает события приложения, в момент загрузки приложения подгружает бины. Определение бинов ищет (по умолчанию) в WEB-INF/applicationContext.xml

Создать WEB-INF/applicationContext.xml с описанием бинов (обычный `<beans>..`)
Бины из корневого контекста доступны для DS

Можно уточнять расположения файла с описанием бинов

    <listener>
    ...
    </listener>
    <context-param>
        <param-name>contextConfigLocation</context-param>
        <param-value>/WEB-INF/spring/appContext.xml</param-value>
    </context-param>
    


### 4. Подключение контроллеров

Добавляем поддержку аннотаций Spring MVC в файл настроек сервлета 

    // diapatcher-servlet.xml
    <beans ...
        // здесь добавить namespace xmlns:mvc
        ...>
        <mvc:annotation-driven/> // теперь Spring будет понимать @Controller, @RequestBody и др.
    </beans>


Добавляем класс контроллера

    @Controller
    public class HomeController {
        
        @RequestMapping("/home")
        @ResponceBody // делает строку телом ответа
        public String goHome() {
            return "Welcome Home";
        }
    }

Добавляем класс в контейнер любым способом
напр. через аннотации:

    // diapatcher-servlet.xml
    <beans ...
        <context:component-scan base-package="..."/>


### 5. View resolution

Схема:

* DispatcherServler принимает запросы
* Для каждого запроса вызывает свой контроллер (через маппинг задано)
* Контроллер возвращает либо сразу ответ (@ResponceBody) либо логическое имя view
* View resolver по логическому имени определяет конкретный view
* view преобразует данные модели в конкретное представление

#### Настройка view resolver`a

Можно использовать встроенный, зарегистрировав его как бин

    // dispatcher-servlet.xml 
    <bean id="viewResolver" class="org.springframework...InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/views/"/>
        <property name="suffix" value=".jsp"/>
    </bean>

к результату, который будет возвращать контроллер добавляются префикс и суффикс


### 6. Разрешение статических ресурсов

Допустим нужно настроить страницу через css
Для этого создаем файл напр. `webapp/resources/css/home.css`
В jsp добавляем ссылку на этот файл
    
    <link ref="stylesheet href="<spring:url value="/resources/css/home.css"/>" type="text/css">

Теперь при запросе страницы, браузер будет запрашивать ресурс `/resources/css/home.css`
Как и любой другой запрос, этот должен обработать DS

Замаппить ресурсы можно при настройке DS

    // файл dispatcher-servlet.xml
    <beans>
        ...
        <mvc:resources location="/resources/" mapping="/resources/**"/>
    </beans>

`**` - означает вложения любого уровня


## 4

### Контроллеры

#### 1. Подключение контроллера

Обычный класс с аннотацией @Controller
Включить его в контекст DS (просто включить сканирование в настройках DS)
Маппинг подключается через @RequestMapping

    @Controller
    @RequestMapping("/project") // обрабатывает все запросы /project/...
    public class ProjectController {
        @RequestMapping("/add") // обрабатывает запросы /project/add
        public String addProject() {
            return "project_add";
        }
    }

Аннотация @RequestMapping над классом, означает что он обрабатывает все запросы, начинающиеся с параметра аннотации

Аннотация @RequestMapping над методом уточняет путь запроса

Метод возвращает логическое имя view, далее через viewResolver (prefi1x/suffix) подбирается view

#### 2. Параметры аннотации @RequestMapping

Можно уточнять какой тип запроса будет обрабатывать метод

    @RequestMapping(value="/add", method=RequestMethod.POST)

Можно проверять значения параметров в форме имя=значение

    @RequestMapping(value="/add", params={"type=multi"})

Можно просто наличие параметры с любым значением
    
    @RequestMapping(value="/add", params={"type=multi", "special"})

#### 3. Использование моделей

У нас есть класс описывающий некую сущность (напр. User с полями name, login)

Объект такого класса можно передать view в параметре типа Model просто как ключ-значение

    @RequestMapping("/user")
    public String goUser(Model model) {
        User user = new User("Ivan");
        model.addAtribute("currentUser", user);
        return "userPage";
    }

Теперь значения доступны напр. в jsp: `${currentUser.name}`



#### 4. URL Templates

Как обрабатывать запросы типа `/project/1`
Можно связать параметр метода и параметр запроса

    @RequestMapping("/project")
    public class ProjectController {
        
        @RequestMapping("/{projectId}")
        public String findProject(Model model, @PathVariable Long projectId) {

        }
    }

Можно уточнять имя, если имя параметра метода не совпадает с шаблоном в аннотации

    @RequestMapping("/{id}")
    public String findProject(Model model, @PathVariable("id") Long projectId)

#### 5. Параметры методов

Поддерживаются различные варианты для связывания параметров

Сессия:
    
    public String addProject(HttpSession session) {
        session.setAttribute("token", "12345");
    }

Запрос:

    public String addProject(HttpServletRequest request) {
        String name = request.gerParameter("name");
    }


Параметры запроса:

    public String addProject(@RequestParam("name") String name) {
    }

Причем возвможно приведение типов

    public String addProject(@RequestParam("id") Long id) {
    }




#### 6. Data Binding

Напр. на форме вводятся какие-то данные. Эти данные передаются на сервер в параметрах запроса. Можно сразу на основании этих параметров создавать объекты модели:

    public String saveProject(@ModelAttribute Project project) {
        
    }

Теперь при вызове метода, имена параметров запроса будут сопоставляться с полями класса Project и при совпадении - заполняться


## 5 Tag library

### Введение

JSP - Java Server Page
JSTL - Jsp Standart Tag Library - дополнительная поддержка для JSP
одна из реализаций - спринговая tag library

подключается стандартным способом:
    
    `<%@ taglib prefix="..." uri="..."  %>`


### 1. Url tag

напр. 
    
    <form action="/project/save" method="POST">

тогда при нажатии submit вызывается адрес типа `localhost:8080/project/save`
хотя в адресе еще есть имя приложения, поэтому должен быть типа `localhost:8080/app/project/save`

тег url это обеспечивает

1) сначала подключенаем
    
    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

2) вместо прямого url подставляем `<spring:url value="/resource/save">` (здесь `spring` - это префикс-синоним из объявления):

    <form action="<spring:url value="/resource/save">" method="POST">


### 2. Form tag

Применяется для связывания модели/данных и форм

1) Подключение:
   
    <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

2) Использование:

    <spring:url value="/resource/add" var="formUrl"> // здесь вынуждены выносить в отдельную переменную, т.к. нельзя использовать вложенные теги
    
    <form:form action="${formUrl}" method="POST" modelAttribute="resource">
        ...
    </form:form>

Основная суть - в атрибуте `modelAttribute`. Через него мы связываем некий объект с формой. Теперь внутри формы через дополнительные теги (см. дальше) можно непосредственно обращаться к полям объекта.

Вот пример кода, открывающий страницу с этой формой:

    @RequestMapping(value = "/resource/add", method = RequestMethod.GET)
    public String getAddNewResourceForm(Model model) {
        Resource newResource = new Resource();
        // связываем с формой новый объект:
        model.addAttribute("resource", newResource); 
        return "addProduct";
    }

Вот код, обрабатывающий POST-запрос с формы:

    @RequestMapping(value = "/resource/add", method = RequestMethod.POST)
    public String processAddNewResourceForm(@ModelAttribute("resource")
        Resource r) {
        service.addResource(r);
        return "redirect:/allResources";
    }


### 3. Input tag

Применяется совместно с тегом `form`

Связывает поле ввода с полем модели

    <form:form ....
        ...
        <form:input path="name" cssClass=...
        ...
    </form:form>

в атрибуте `path` указывается имя свойства класса-модели

внутри преобразуется в обычный `input` html

Теперь, если ввести в поле значение и нажать submit на форме, объект в методе, обрабатывающем переход, будет содержат это значение

    @RequestMapping(value = "/resource/add", method = RequestMethod.POST)
    public String processAddNewResourceForm(@ModelAttribute("resource")
        Resource r) {
        // r.name содержит введенное значение
        ...
    }

### 4. Select tag

Так же как и Input, применяется внутри тега `form`

Связывает выбранное значение с полем класса-модели

    <form:select path="type" items="${typeOptions}" />

Здесь `path` - указывает имя поля
`items` - массив, содержащий значения для выбора

можно заполнить в контроллере перед показом формы, например так

    @RequestMapping(value = "/resource/add", method = RequestMethod.GET)
    public String getAddNewResourceForm(Model model) {
        List<String> options = ...;
        model.addAtribyte("typeOptions", options);
        ...
    }


### 5. Checkbox, Radiobutton tags

аналогично `Select` тегу

    <form:radiobuttons path="someModelField" items="${radios}">

    <form:checkboxes path="someModelField" items="${checks}">


### 6. TextArea tag

Для ввода текста большого размера

    <form:textarea path="notes" rows="3" />



## 6 Advanced Controllers

### 1. Data binding для сложных объектов

Как связать поле составного типа (напр. класс Sponsor с полями name, phone)

Создать отдельные поля для всех полей составного объекта. Пути к этим полям - составные, через `.`

    <form:form ... modelAttribute="project">
        <form:input ... path="sponsor.name"> 
        <form:input ... path="sponsor.phone"> 
    </form:form>

Здесь, поскольку объявлен атрибут модели, полный путь подразумевается `project.sponsor.name`

### 2. Data binding для полей-списков

Напр. класс Project имеет поле `List<String> contacts`. Связываем по индексу

    <form:form ... modelAttribute="project">
        <form:input ... path="contacts[0]"> 
        <form:input ... path="contacts[1]"> 
    </form:form>

### 3. @ModelAttribute

Можно использовать для параметров методов: ознаает что в этот параметр будет внедрена модель с формы

Можно отметить метод. Возвращаемый результат будет помещен в модель. Или этот метод будет вызван когда для страницы понадобится данные:

Т. е. вместо

    @RequestMapping()
    public String getAdd(Model model) {
        Resource newResource = new Resource();
        model.addAttribute("resource", newResource); 
    }

можно вынести добавление в модель:

    @ModelAttribute("resource")
    public Resource getR() {
        return new Resource();
    }

### 4. Session Attribute

Как передать данные между запросами

Аннотация @SessionAttributes("..") над классом

Теперь один раз создается и при всех вызовах будет использоваться

### 5. SessionStatus

Если есть @SessionAttributes, он будет передаваться везде где используется.
Иногда нужно сбросить / очистить. Для этого в параметры метода добавляем `SessionStatus` и вызываем метод `setComplete()`

    @RequestMapping(..)
    public String getAdd(@ModelAttribute Resource r, SessionStatus status) {
        ...
        status.setComplete();
        return "add";
    }


