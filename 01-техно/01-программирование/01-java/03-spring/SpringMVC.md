#
#
#
#
# Docs

https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html

## 1.1 DispatcherServlet

### Основы

Spring MVC построен на шаблоне front controller. **DispatcherServlet** получает и распределяет входящие запросы

Как и любой сервлет, настраивается в соответствии со спецификацией через:

* Java-конфигурацию
    - реализовать интерфейс `WebApplicationInitializer`
    - унаследовать абстрактный класс `AbstractAnnotationConfigDispatcherServletInitializer`
* через файл `web.xml`

### 1.1.1 Иерархия контекстов

* DispatcherServlet нуждается в `WebApplicationContext` (который в свою очередь расширяет ApplicationContext)
* `WebApplicationContext` имеет ссылку на `ServletContext` и на `Servlet` с которым он ассоциирован
* Обычно достаточно одного WAC 
* Можно вариант когда один корневой WAC входит в разные DispatcherServlet'ы, и эти сервлеты имеют собственные дочерние WAC
* Корневой WAC содержит бины (БД, бизнес-логика), разделяемые между разными сервлетами.
* Дочерние сервлеты содержат бины, отвечающие за веб (mapping, viewResolver), могут наследовать бины корневого контекста

Вот пример настройки иерархии:

    public class MyWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
        @Override
        protected Class<?>[] getRootConfigClasses() {
            return new Class<?>[] { RootConfig.class };
        }
        @Override
        protected Class<?>[] getServletConfigClasses() {
            return new Class<?>[] { App1Config.class };
        }
        @Override
        protected String[] getServletMappings() {
            return new String[] { "/app1/*" };
        }
    }

Если иерархия не нужна, достаточно настроить корневой, а в `getServletConfigClasses()` вернуть null

Вот настройка через `web.xml`

    <web-app>
        <listener>
            <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
        </listener>
        <context-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>/WEB-INF/root-context.xml</param-value>
        </context-param>
        <servlet>
            <servlet-name>app1</servlet-name>
            <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
            <init-param>
                <param-name>contextConfigLocation</param-name>
                <param-value>/WEB-INF/app1-context.xml</param-value>
            </init-param>
            <load-on-startup>1</load-on-startup>
        </servlet>
        <servlet-mapping>
            <servlet-name>app1</servlet-name>
            <url-pattern>/app1/*</url-pattern>
        </servlet-mapping>
    </web-app>

Можно настроить только корневой в блоке `<context-param>`, и не указывать `contextConfigLocation` в блоке `<servlet>`

### 1.1.2 Специальные типы бинов

`DispatcherServlet` делегирует выполнение задач по обработке запроса особым бинам. Эти бины предоставляются фреймворком, но можно изменять, уточнять.

Типы бинов:

* **HandlerMapping**
    - определяет обработчик для запроса по url
    - реализация `RequestMappingHandlerMapping` (через аннотации)
    - другая `SimpleUrlHandlerMapping` (просто в url указывается путь к обработчику)
* **HandlerAdapter**
    - вспомогательная служба, помогающая DS при вызове обработчиков (напр. если контроллер настроен через аннотации, он помогает эти аннотации разрешить)
* **HandlerExeptionResolver**
    - определяет стратегию обработки ошибок (напр. перенаправлением из на обработчики, показ страниц с ошибками и т. п.)
* **ViewResolver** 
    - переводит строковые имена, возвращенные контроллером в актуальные View
* **LocalResolver**, **LocalContextResolver**
    - используется при I18n
* **ThemeResolver**
    - темы (персонализация)
* **MutlipartResolver** 
    - для обработки запросов из нескольких частей (напр. загрузка файлов)
* **FlashMapManager** 
    - получение, сохранение входных и выходных данным, напр. при редиректе


### 1.1.3 Конфигурация Web MVC

Приложение может реализовывать специальные типы бинов, нужные для обработки запросов

DS проверяет WAC на наличие соответствующих бинов. Если их нет - использует реализации по умолчанию (из библиотеки Spring, прописаны в `DispatcherServlet.properties`)

Настройка делается через MVC Config (аннотации / xml)

### 1.1.4 Конфигурация сервлетов

Варианты конфигурации:

* реализация интерфейса **WebApplicationInitializer**
* наследование класса **AbstractAnnotationConfigDispatcherServletInitializer** для Java-based конфигураций 
* наследование класса **AbstractDispatcherServletInitializer** для xml-конфигураций

Справка по иерархии классов:

* основа - интерфейс **WebApplicationInitializer**
    - единственный метод onStartup(ServletContext ctx)
* класс **AbstractContextLoaderInitializer** реализует WAI
    - с единственным абстрактным WebApplicationContext createRootAplicationContext()
* **AbstractDispatcherServletInitializer** наследует AbstractContextLoaderInitializer, его методы:
    - WebApplicationContext createServletApplicationContext()
    - String[] getServletMapping()
* **AbstractAnnotationConfigDispatcherServletInitializer** наследует AbstractDispatcherServletInitializer, абстрактные методы:
    - Class<?>[] getRootConfigClasses()
    - Class<?>[] getServletConfigClasses()

#### Пример через WebApplicationInitializer

    public class MyWebApplicationInitializer implements WebApplicationInitializer {
        @Override
        public void onStartup(ServletContext container) {
            XmlWebApplicationContext appCtx = new XmlWebApplicationContext();
            appCtx.setConfigLocation("/WEB-INF/spring/dispatcher-config.xml");
            ServletRegistration.Dynamic registration = container.addServlet("dispatcher", new DispatcherServlet(appCtx));
            registration.setLoadOnStartup(1);
            registration.addMapping("/");
        }
    }

* Переопределяем метод `onStartup()`
* создаем DS
    - создаем web-контекст на основе xml
    - на основе этого контекста создаем DS и регистрируем его в контексте сервлетов
* настраиваем DS
    - загрузка при старте (`setLoadOnStartup`)
    - добавляем маппинг

#### Пример через AbstractAnnotationConfigDispatcherServletInitializer (аннотации)

    public class MyWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
        @Override
        protected Class<?>[] getRootConfigClasses() {
            return null;
        }
        @Override
        protected Class<?>[] getServletConfigClasses() {
            return new Class<?>[] { MyWebConfig.class };
        }
        @Override
        protected String[] getServletMappings() {
            return new String[] { "/" };
        }
    }

* Обязаны переопределить `getRootConfigClasses()` и `getServletConfigClasses()`
* можем для маппинга переопределить  `getServletMappings()`
* если корневой контекст не нужен, `getRootConfigClasses` может возвращать null

#### Пример через AbstractDispatcherServletInitializer

    public class MyWebAppInitializer extends AbstractDispatcherServletInitializer {
        @Override
        protected WebApplicationContext createRootApplicationContext() {
            return null;
        }
        @Override
        protected WebApplicationContext createServletApplicationContext() {
            XmlWebApplicationContext cxt = new XmlWebApplicationContext();
            cxt.setConfigLocation("/WEB-INF/spring/dispatcher-config.xml");
            return cxt;
        }
        @Override
        protected String[] getServletMappings() {
            return new String[] { "/" };
        }
    }

* Применяется для конфигурации через xml
* создаем корневой контекст (если не нужен - null возвращаем)
* создаем web контекст
* добавляем маппинги

### 1.1.5 Обработка запросов

DS обрабатывает запросы следующим образом:

* Находится WebApplicationContext и привязывается к запросу по ключу `DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE`. К нему могут обращаться контроллеры и другие элементы
* Locale resolver добавляется к запросу
* Theme resolver добавляется к запросу
* Если определяли multipart file resolver, запрос проверяется на наличие частей. Если найдены, запрос оборачивается в `MultipartHttpServletRequest` для дальнейшей обработки
* Ищется подходящий обработчик. Если найден выполняются связанные действия (пре-, пост-, контроллеры) для подготовки модели или рендеринга
* Если возвращается модель, рендерится view

Бины `HandlerExeptionResolver`, объявленные в WAC, используются для обработки исключений в процессе обработки запроса

#### Настройки DS при инициализации

Можно добавлять следующие параметры (элементы `init-param` в web.xml):

* **contextClass** - класс, реализующий `ConfigurableWebApplicationContext`. Его использует сервлет. Если не указан, используется `XmlWebApplicationContext`
* **contextConfigLocation** - строка, передаваемая контексту (определяемому параметром contextClass), для определения где находится контекст
* **namespace** - namespace для WAC
* **throwExeptionIfNoHandlerFound** - определяет что делать, если не найден обработчик для запроса. По умолчанию ЛОЖЬ, и будет 404 код. Если установить в ИСТИНА, вбрасывается `NoHandlerFoundException`, которое может обрабатываться HandlerExeptionResolver. Еще может быть установлена обработка запроса по умолчанию и все запросы без соответствия будут на конкретный сервлет перенаправляться

### 1.1.6 Interseption

Все реализации HandlerMapping поддерживают перехват. Перехватчик должен реализовать интерфейс `HandlerInterceptor`:

* `preHandle()` - перед вызовом обработчика запроса. Возвращает булево. Если ЛОЖЬ - дальнейшая обработка прекращается. DS считает, что метод `preHandle` сам напр. вернет какое-то view
* `postHandle()` - после 
* `afterCompletion()` - после завершения запроса

#### 1.1.6.2 WebContentInterceptor

Для общих настроек контроллеров: `setSupportedMethods()`, `preHandle() / postHandle() / afterCompletion()` и др.

[немного примеров](https://stackoverflow.com/questions/1362930/how-do-you-set-cache-headers-in-spring-mvc/52652555#52652555)

Подключение напр. так:

    @Configuration
    public class Config extends WebMvcConfigurationSupport {
        // inject     
        private WebContentInterceptor WCI;
        @Override
        protected void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(WCI);
        }
    }

    @Component
    public class Interceptor extends WebContentInterceptor {
        public Interceptor() {
            setSupportedMethods("POST");
        }
    }


#### 1.1.6.3 Кеширование

[что такое кеши](https://www.mnot.net/cache_docs)
[как в Spring](https://stackoverflow.com/questions/1362930/how-do-you-set-cache-headers-in-spring-mvc/)

`WebContentInterceptor` наследник `WebContentGenerator` у которого есть методы для работы с кешем:

* `setCacheSeconds()`
* `setCacheControl()`
* и др.


### 1.1.7 Исключения

Если происходит исключение в процессе маппинга или обработки запроса, DS делегирует его цепочке HandlerExeptionResolver`ов

Реализации HandlerExeptionResolver:

* **SimpleMappingExceptionResolver** - через соответствие между именем класса иселючения и именем view
* **DefaultHandlerExeptionResolver** - исключения, вбрасываемые Spring MVC, отображаются на коды статусов HTTP
* **ResponseStatusExceptionResolver** - исключения с аннотацией @ResponceStatus отображаются на коды статусов HTTP
* **ExceptionHandlerExceptionResolver** - через вызов методов @ExceptionHandler в классах @Controller или @ControllerAdvice 

#### Цепочка resolver`ов

Можно определить несколько бинов в конфигурации с указанием свойства `order`. Исключение будет обрабатывться по цепочке от меньших к большим

Resolver может возвращать:

* объект `ModelAndView`, который описывает view c ошибкой
* пустой `ModelAndView`, означает что ошибка успешно обработана и ничего не нужно показывать
* null - передается дальше по цепочке resolver`ов (может дойти до самого сервлета)

По умолчанию подключаются обработчики для исключений Spring MVC

#### Страница ошибки

Если исключение осталось необработанным или статус установлен ошибочным (4хх, 5хх), контейнеры сервлетов могут отображать дефолтную страницу ошибки.

Можно свою определить в файле `web.xml` напр.
    
    <error-page>
        <location>/error</location>
    </error-page>

В этом случае контейнер обрабатывает запрос по указанному url (напр. `/error`) Теперь просто обычным способом назначаем маппинг на этот url

Назначить страницу ошибки можно только через `web.xml`. Java-based нельзя





### 1.1.8 View resolution

Интерфейсы ViewResolver и View. ViewResolver обеспечивает соответствие между именем view и самим view

Иерархия ViewResolver`ов:

* **AbstractCachingViewResolver** - кеширует view для увеличения производительности. Можно отключать кеширование, можно удалять конкретный view из кеша
* **XmlViewResolver** - работает с конфигурацией, описанной в xml (напр. /WEB-INF/views.xml)
* **ResourceBundleViewResolver** - на основе ResourceBundle
* **UrlBasedViewResolver** - простая реализация, когда логические имена view простым образом соответствуют view (суффикс / префикс)
* **InternalResourceViewResolver** - подкласс UrlBasedViewResolver, дополнительная поддержка jsp, jstl, tiles
* **FreeMarkerViewResolver** - подкласс UrlBasedViewResolver, работает с free marker
* **ContentNegotiatingViewResolver** - разрешение через имена запрашиваемых файлов или заголовок Accept


#### Настройка

Можно указывать несколько ViewResolver с указанием свойства `order` (наименьшее - первым обрабатывает). 
ViewResolver возвращает null, если не смог определить view
InternalResourceViewResolver (jsp) необходимо располагать последним в цепочке (что-то из-за jsp ???)

#### Редирект

Префикс `redirect:` в имени view позволяет выполнять редирект. Это обрабатывает UrlBasedViewResolver и его подклассы

#### Forwarding

Префикс `forward:`





### 1.1.9 Локали

### 1.1.10 Темы

### 1.1.11 Multipart resolver

### 1.1.12 Логгирование

## 1.2 Фильтры

## 1.3 Контроллеры

### Введение

Определяются через `@Controller` и `@RestController`

    @Controller
    public class HelloController {
        @GetMapping("/hello")
        public String handle(Model model) {
            model.addAttribute("message", "Hello World!");
            return "index";
        }
    }

### 1.3.1 Объявление

Определяем констроллеры через добавление бинов в WebApplicationContext любым стандартным способом

Можно использовать autowiring

Программно:
    
    @Configuration
    @ComponentScan("org.example.web")
    public class WebConfig {..}

Через xml:

    <beans ..>
        ...
        <context:component-scan base-package="com.example.web"/>
    </beans>


`@RestController` это составная аннотация: @Controller + @RequestBody. Т. е. любой метод такого контроллера сразу возвращает тело ответа. Фаза с view resolving отсутствует

### 1.3.2 Маппинг запросов

#### 1.3.2.1 Введение

Через использование аннотации `@RequestMapping` с разными атрибутами для url, типа запроса, параметров, заголовков и др.

Есть специальные сокращения для конкретных Http-методов:

* @GetMapping
* @PostMapping
* @PutMapping
* @DeleteMapping
* @PatchMapping

`@RequestMapping` отвечает на запрос с любым типом

но лучше использовать конкретный запрос, вместо общего `@RequestMapping` или конкретно указывать свойство`method`

#### 1.3.2.2 Шаблоны uri

Символы, используемые в шаблонах:

* `?` - один произвольный символ
* `*` - 0 или более символов внутри сегмента (/../) (`/ab`, но не `/ab/cd`)
* `**` - 0 или более сегментов пути (`/ab` и `ab/cd`)
* можно комбинировать `/user/**/id`
* если несколько шаблонов подходят - наиболее специфичный выбирается

Можно объявлять переменные и получать их значения через `@PathVariable`

    @GetMapping("/owners/{ownerId}/pets/{petId}")
    public Pet findPet(@PathVariable Long ownerId, @PathVariable Long petId) {}

Можно на уровне класса и на уровне метода

    @Controller
    @RequestMapping("/owners/{ownerId}")
    public class OwnerController {
        @GetMapping("/pets/{petId}")
        public Pet findPet(@PathVariable Long ownerId, @PathVariable Long petId) { ... }
    }

Переменные из путей приводятся к соответствующим типам, или генерируется TypeMismatchException. Для простых типов (String, Long, Date, ..) - автоматически. Для других типов нужно регистрировать (data binding)

Имя переменной для параметра можно указать явно `@PathVariable("customId")`

Синтаксис `{varName:regex}` объявляет переменную, значение которой определяется через регулярное выражение. Вот пример для пути типа `/spring-web-3.0.5 .jar`

    @GetMapping("/{name:[a-z-]+}-{version:\\d\\.\\d\\.\\d}{ext:\\.[a-z]+}")
    public void handle(@PathVariable String version, @PathVariable String ext) 

  

#### 1.3.2.3 Сравнение шаблонов

По каким правилам определяется шаблон, при соответствии нескольким
За это отвечает класс `AntPathMatcher`. Можно переопределять

Выбирается наиболее специфичный шаблон. 

`/**`  - это по умолчанию, соответствует любому

#### 1.3.2.4 Суффиксы

По умолчанию при указании обычного пути напр. `/user`, подразумевается расширенный `/user.*`

Можно отключить

#### 1.3.2.5 Отбор по заголовку Сontent-Type  

    @PostMapping(path = "/pets", consumes = "application/json") 

Можно инвертировать `!text/plain`

Можно указывать на уровне класса, но указание на уровне метода будет переопределять, а не расширять

Можно использовать класс MediaTypes

#### 1.3.2.5 Отбор по заголовку Accept

    @GetMapping(path = "/pets/{petId}", produces = "application/json") 

Остальное аналогично

#### 1.3.2.6 Отбор по заголовкам / параметрам

    @GetMapping(path = "/pets/{petId}", params = "myParam=myValue") 

    @GetMapping(path = "/pets", headers = "myHeader=myValue")



### 1.3.3 Методы контроллера

#### 1.3.3.1 Аргументы методов

Методы могут иметь разную сигнатуру + разные аннотации

Примеры параметров:

* доступ к запросу, ответу: `WebRequest, NativeWebRequest, ServletRequest, ServletResponse`
* сессия `HttpSession`
* метод запроса `HttpMethod`
* параметры локали `Locale, TimeZone, ZoneId`
* доступ к телу запроса `InputStream, Reader`
* доступ к телу ответа `OutputStream, Writer`
* др. аннотации
* если нет аннотаций и это простой тип, считается как `@RequestParam`, если не простой тип - `@ModelAttribute`

#### 1.3.3.2 Возвращаемый результат

* `@ResponseBody` - конвертация через `HttpMessageConverter` (просто напрямую)
* `HttpEntity<B>`, `ResponseEntity<B>` - полный ответ с заголовками, телом
* `HttpHeaders` - ответ с заголовками, но без тела
* `String` - имя view для определения view через `ViewResolver`
* `View` - собственно view
* `Map`, `Model` - атрибуты модели, имя view определяется неявно через `RequestToViewNameTranslator`
* `@ModelAttribute` - аналогично
* `ModelAndView` - view и атрибуты, иногда статус ответа
* `void`, `null` - контроль над ответом через параметры `ServletResponse, OutputStream или @ResponseStatus`. Или это отсутствие тела для REST запроса или имя view по умолчанию
* разные варианты для асинхронных запросов `Callable` и др.


#### 1.3.3.3 Конвертация типов

Обычно в запросе текстовые данные, но Spring может преобазовывать примитивные типы

Более тонкая настройка: `WebDataBinder` / `DataBinder` или `FormattingConversionService`

#### 1.3.3.4 Matrix Variables 

нужно явно включить возможность использования `;` в путях (см. ниже)

переменные могут передаваться в URI:
это пара ключ-значение, разные переменные отделяются `;`
разные значение внутри одной переменной - `,`

    /cars;color=red,green;year=2012
    /color=red;color=green;color=blue

В методах доступ к таким переменным через аннотацию `@MatrixVariable`

Примеры использования

    // GET /pets/42;q=11;r=22
    @GetMapping("/pets/{petId}")
    void findPet(@PathVariable String petId, @MatrixVariable int q) {
        // q = 11 }

    // уточняем, из какой части пути брать параметр
    // GET /owners/42;q=11/pets/21;q=22
    @GetMapping("/owners/{ownerId}/pets/{petId}")
    void findPet(
        @MatrixVariable(name="q", pathVar="ownerId") int q1,
        @MatrixVariable(name="q", pathVar="petId") int q2) {
            // q1 = 11,  q2 = 22  }

    // можно значение по умолчанию
    // GET /pets/42
    @GetMapping("/pets/{petId}")
    void findPet(@MatrixVariable(required=false, defaultValue="1") int q) {


Включение использования `;`:

    removeSemicolonContent=false

Напр. для SpringBoot

    @Configuration
    public class WebConfig extends WebMvcConfigurerAdapter {
        @Override
        public void configurePathMatch(PathMatchConfigurer configurer) {
            UrlPathHelper urlPathHelper = new UrlPathHelper();
            urlPathHelper.setRemoveSemicolonContent(false);
            configurer.setUrlPathHelper(urlPathHelper);
        }
    }




#### 1.3.3.5 @RequestParam

для связи параметров запроса (`?`) или данных формы (Servlet API их комбинирует в одну коллекцию)

    @GetMapping
    public String setupForm(@RequestParam("petId") int petId) {}

могут быть необязательными: свойство `required=false` или тип `Optional`

ести тип параметра массив / список - несколько значений параметра
если тип `Map<String, String>` / MultiValueMap<String, String> - все параметры


#### 1.3.3.6 @RequestHeader

Связывание заголовков запроса с параметрами метода

    public void handle(@RequestHeader("Accept-Encoding") String encoding, 
        @RequestHeader("Keep-Alive") long keepAlive) { .. }

Если тип `String[]`, `List` - можно принимать заголовки со значениями, перечисленными через `,`

Можно получить все заголовки, если тип параметра `Map<String, String>`, `MultiValueMap<String, String>`, `HttpHeaders`



#### 1.3.3.7 @CookieValue

Для связывание куков

    @GetMapping("/demo")
    public void handle(@CookieValue("JSESSIONID") String cookie) {}

Работает преобразование типов, для не строковых
Тип - простые или javax.servlet.http.Cookie


#### 1.3.3.8 @ModelAttribute

пример data-binding, когда параметры запроса связываются с моделью (вместо ручного связывания)

    // здесь экземпляр Pet будет проинициализирован petId, ownerId
    @PostMapping("/owners/{ownerId}/pets/{petId}/edit")
    public String processSubmit(@ModelAttribute Pet pet) { } 

Как связывается экземпляр модели:

* из модели, если она была раньще настроена через `Model` (см. ниже)
* из сессии через `@SessionAttributes`
* через переменные URI с помощью `Converter`
* через вызов конструктора по умолчанию
* через конструктор с параметрами, соответствующими параметрам запроса

Под капотом:

* за связывание отвечает `WebDataBinder`
* можно валидацию подключить через `@Valid` или `@Validated`
* если есть ошибки - исключение **BindException**, но можно добавить параметр типа **BindingResult** и проверять его состояние
* можно отменить связывание (`@ModelAttribute(binding=false)`) просто получая экземпляр модели
* 


#### 1.3.3.9 @SessionAttributes

для связывание атрибутов модели в HTTP Servlet сессии между запросами
на уровне класса задается

    @Controller
    @SessionAttributes("pet") 
    public class EditPetForm { .. }

Теперь какой-то метод добавляет в сессию атрибут `pet`, когда он будет добавлен в модель

Убрать из сессии можно через `SessionStatus`

    @PostMapping("/pets/{id}")
    public String handle(Pet pet, BindingResult errors, SessionStatus status) {
        if (errors.hasErrors) {
            // ...
        }
            status.setComplete(); 
            // ...
        }

#### 1.3.3.10 @SessionAttribute

Для доступа к глобальному атрибуту сессии, который существует независимо от контроллера (напр. от фильтра)

    @RequestMapping("/")
    public String handle(@SessionAttribute User user) 

#### 1.3.3.11 @RequestAttribute

что-то типа `@SessionAttribute`

#### 1.3.3.12 Redirect Attributes

При редиректах атрибуты модели могут отражаться в URL в виде query
В параметре метода с типом `RedirectAttributes` можно указать какие конкретно атрибуты передавать в URL

В общем поведение настраивается через флаг `ignoreDefaultModelOnRedirect` объекта `RequestMappingHandlerAdapter `   

    @PostMapping("/files/{path}")
    public String upload(...) {
        // ...
        return "redirect:files/{path}";
    }

#### 1.3.3.13 Flash-атрибуты

Способ передачи атрибутов от одного запроса к другому (обычно при редиректах)

Сохраняются через сессию, не через URL

Две абстракции: `FlashMap` и `FlashMapManager`
Поддержку Flash-атрибутов настраивать никак не надо. Сессия будет создаваться только есть нужда. Доступ к атрибутам можно получить через статические методы `RequestContextUtils`

Напрямую обычно нет нужды. Метод обрабатывающий запрос через параметр `RequestAttributes` принимает атрибуты для передачи. В запросе-приемнике они добавляются в модель

Рекомендуются только для редиректов

#### 1.3.3.14 @RequestBody

    @PostMapping("/accounts")
    public void handle(@RequestBody Account account) { .. }

параметр - тело запроса для чтения и преобразования в объект через `HttpMessageConverter`

можно в комбинации с `@Valid`, `@Validated` и параметрами с типом `Errors` или `BindingsResult`


#### 1.3.3.15 HttpEntity

Представляет заголовки запроса и его тело
    
    @PostMapping("/accounts")
    public void handle(HttpEntity<Account> entity) { .. }


#### 1.3.3.16 @ResponseBody

если над методом - результат метода сериализуется в тело ответа через `HttpMessageConverter`
если над классом - ко всем методам этого класса 

`@RestController` - это `@Controller` + `@ResponseBody`

#### 1.3.3.17 ResponseEntity

тип для возвращаемого значения метода
содержит статус, заголовки, тело





### 1.3.4 Модель

Аннотация `@ModelAttribute` для взаимодействия с моделью:

* (1) над агрументом метода с `@RequestMapping` - создание объекта модели и заполнение его полей параметрами запроса через `WebDataBinder`
* (2) над методом в классе `@Controller` или `@ControllerAdvice` для инициализации модели до вызова методов `@RequestMapping`
* (3) над методом c `@RequestMapping` - метод возвращает объект модели

Для варианта (2):

Контроллер может иметь несколько метод с аннотацией `@ModelAttribute`
Все такие методы вызываеются до вызова методов с `@RequestMapping`того же контроллера
Можно шарить такие методы через несколько контроллеров с помощю `@ControllerAdvice`


### 1.3.5 DataBinder

Классы `@Controller` или `@ControllerAdvice` могут иметь метод с `@InitBinder`, который инициализирует экземпляр `WebDataBinder` для:

* связи параметров запроса (query/form) с объектом модели
* конвертации строковых параметров запроса (куки, заголовки, параметры из пути) в другие типы для аргументов методов
* преобразования значений объекта модели в строковые для передачи в ответ

Обычно сигнатура метода такая: void + `WebDataBinder` параметр

    @InitBinder 
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }



### 1.3.6 @ExceptionHandler

аннотирует метод, котовый будет обрабатывать исключения от других методов контроллера

Есть разные комбинации параметров, возвращаемых значений, напр. так:

    @ExceptionHandler({FileSystemException.class, RemoteException.class})
    public ResponseEntity<String> handle(IOException ex) {
        // ...
    }

Для REST-сервисов обычно инфо об ошибках включают в тело ответа. Для этого обычно метод `@ExceptionHandler` возвращает `ResponseEntity` с кодом ответа и телом.

Можно глобально через `@ControllerAdvice`

Можно перехватывать Spring'овые исключения через расширение `ResponseEntityExceptionHandler`

### 1.3.7 Controller Advice

Методы с аннотациями типа `@ExceptionHandler`, `@InitBinder`, `@ModelAttribute` работают внутри того класса (или в иерархии), где они объявлены

Если нужно распространить поведение на несколько контроллеров, используются аннотации `@ControllerAdvice` и `@RestControllerAdvice` (как классы с глобальными методами)

Глобальные методы `@ModelAttribute` и `@InitBinder` применяются до применения локальных. Методы `@ExceptionHandler` после локальных

По умолчанию глобальные работают для всех контроллеров, но можно ограничивать: 

    // только для @RestController
    @ControllerAdvice(annotations = RestController.class)

    // в пределах пакета
    @ControllerAdvice("org.example.controllers")

    // для контрентых классов
    @ControllerAdvice(assignableTypes = {MyController.class, Con2.class})



#
#
#
#
#
#
#
# Pivotal Spring Web Exam

## 3. Spring MVC

### 3.2 MVC Components

#### 3.2.0 

Основа (то, что ищет DispatcherServlet) (интерфейсы):

* **HandlerAdapter**
* **HandlerMapping**
* **ViewResolver**
* **HandlerExceptionResolver**

Spring предлагает реализации по умолчанию для них. Посмотреть можно в jar'е spring-webmvc.jar в org\springframework\web\servlet\DispatcherServlet.properties

Может быть несколько реализаций инфраструктурных бинов (порядок через `Ordered`)

Тут как-то вступает `@EnableWebMVC`, который внутренне подключает RequestMappingHandlerMapping. 

#### 3.2.1 Инфраструктурные компоненты

##### 3.2.1.1 HandlerMapping

Задача: по URL определить обработчики для запросов. Также перехватчики pre- и post-

Реализации, включенные по умолчанию: 

* **BeanNameUrlHandlerMapping** - по имени бина `@Controller("/persons")`
* **RequestMappingHandlerMapping** - по аннотации `@RequestMapping`

**ControllerClassNameHandlerMapping** - маппинг на основе имени класса и имени метода (нужно дополнительно подключать)

#### 3.2.1.2 HandlerAdapter

Задача: через него DispatcherServlet вызывает методы обработчиков. Адаптер обрабатывает аннотации и разные настройки

Реализации по умолчанию: 

* **HttpRequestHandlerAdapter** 
* **SimpleCotrollerHandlerAdapter**
* **AnnotationMehtodHandlerAdapter**

Когда включен `@EnableWebMVC` метод-обработчик запроса определяют совместно `RequestMappingHandlerMapping` и `RequestMappingHandlerAdapter`

#### 3.2.2 Пользовательские компоненты








#
#
#
#
#
#
#
#
# Sping Data Rest

## 3. Начало 

### 3.1 Подключение зависимостей

Spring Boot 

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-rest</artifactId>
    </dependency>

Maven

    <dependency>
      <groupId>org.springframework.data</groupId>
      <artifactId>spring-data-rest-webmvc</artifactId>
      <version>3.3.0.RELEASE</version>
    </dependency>


### 3.2 Конфигурация

основа - класс `RepositoryRestMvcConfiguration`
для Spring Boot настраивается автоматически

для переопределения настроек:

* зарегистрировать `RepositoryRestConfigurer`
* унаследовать `RepositoryRestConfigurerAdapter`

### 3.3 Основные настройки

#### 3.3.1 Стратегия определения репозиториев

`RepositoryDiscoveryStrategies`:

* DEFAULT - все публичные репозитории, но с учетом флага `exported` аннотации `@(Repository)RestResource`
* ALL - все репозитории
* ANNOTATION - только с аннотацией `@(Repository)RestResource` и с учетом флага
* VISIBILITY - только аннотированые репозитории ???

#### 3.3.2 Смена базового URL

по умолчанию - от корня `/`

Способ 1: `application.properties`: `spring.data.rest.basePath=/api`

Способ 2: (без Spring Boot) через бин **RepositoryRestConfigurer**

    @Configuration
    class CustomRestMvcConfiguration {
      @Bean
      public RepositoryRestConfigurer repositoryRestConfigurer() {
        return new RepositoryRestConfigurerAdapter() {
          @Override
          public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
            config.setBasePath("/api");
          }
        };
      }
    }

Способ 3: через бин, наследующий**RepositoryRestConfigurerAdapter** (deprec)

    @Component
    public class CustomizedRestMvcConfiguration extends                     
            RepositoryRestConfigurerAdapter {
      @Override
      public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.setBasePath("/api");
      }
    }



#### 3.3.3 Другие свойства

размер страницы
максимальный размер страницы
имя параметра выбора страницы
имя параметры лимита
имя параметра сортировки
media-type
возвращать тело при создании записи
возвращать тело при обновлении записи


## 4. Репозитории

### 4.1 Основы

В основе Spring Data Rest экспорт ресурсов через репозитории Spring Data

напр. для
    
    public interface OrderRepository extends CrudRepository<Order, Long> { }

доступны `/orders` и `/orders/{id}`

все запросы дополнительно будут возвращать в json дополнительную информацию HATEOAS (`_link`, `_embedded`). Избавиться от этого нельзя, только собственные контроллеры без Data Rest

#### 4.1.1 Методы

Какие методы HTTP доступны определяется типом репозитория
напр. для CrudRepository почти все методы доступны
для репозитория с `@RestResource(exported = false)` - нет доступных методов

#### 4.1.2 Коды статуса по умолчанию

200 - **GET**
201 Created - **POST** / **PUT** при создании новых
204 No Content - **PUT** / **PATCH** / **DELETE** если в конфигурации `returnBodyOnUpdate = false`. Иначе 200 при обновлении, 201 при создании


Если `returnBodyOnUpdate` и `returnBodyCreate` явно установлены в `null`, код определяется по наличию заголовка `Accept` (????)

#### 4.1.3 Описание ресурсов

Доступные точки / ресурсы отображаются при обращении к корневому URL

    curl -v http://localhost:8080/

    { "_links" : {
        "orders" : {
          "href" : "http://localhost:8080/orders"
        },
        "profile" : {
          "href" : "http://localhost:8080/api/alps"
        }
      }
    }




### 4.2 Коллекции ресурсов

#### 4.2.0 Основы

По умолчанию доступ через имя domain класса во мн. числе, напр. `/orders`
можно настроить через `@RepositoryRestResource`

#### 4.2.1 Методы

Доступны `GET`, `POST`. Остальные - `405 Method not allowed`

**GET** (список элементов): 

* через вызов `findAll(..)`
* параметры (если репозиторий с паджинацией)
    - `page` - индекс страницы (0 по умолчанию)
    - `size` - размер страницы (20 по умолчанию)
    - `sort` - сортировка в виде ($propertyname,)+[asc|desc]?
* возвращает `405` если в репозитории не реализованы методы или `exported = false`
* media types:
    - application/hal+json
    - application/json
* ресурсы: 
    - `search`

**HEAD**:

аналог **GET**, но без тела, без статусов    

**POST**:

* через вызов `save(...)`
* тоже `405` если в репозитории не реализован метод или `exported = false`
* media types: аналог GET



### 4.3 Отдельный элемент ресурса

**GET**:

* через метод `findById()` 
* возврат `405` если нет метода в репозитории
* media types:
    - application/hal+json
    - application/json
* ресурсы: свойства (??? `/users/group`)

**HEAD**

**PUT**

* через `save()`
* возврат `405` если нет метода в репозитории
* media types:
    - application/hal+json
    - application/json

**PATCH**

* аналог *PUT* (частичное обновление)
* media types:
    - application/hal+json
    - application/json
    - application/patch+json
    - application/merge-patch+json

**DELETE**

* методы `delete(T)`, `delete(Id)`, `delete(Iterable)` 
* возврат `405` если нет метода в репозитории




### 4.4 Связанные ресурсы

Когда основной ресурс имеет подчиненные свойства

**GET**
**PUT**

* только `text/uri-list`

**POST**

* только `text/uri-list`

**DELETE**

* 405 код, если обязателен подресурс

### 4.5 Ресурс Search


