#
#
#
#
#
## 

### Настройка зависимостей

Напр. используем стартер, но какая-то зависимость не нужна
    
    // исключаем библиотеку для JSON для не REST web приложения
    compile("org.springframework.boot:spring-boot-starter-web") {
        exclude group: 'com.fasterxml.jackson.core'
    }

если нужна более новая версия - включаем ее явно
если нужна более ранняя - исключаем текущую, включаем явно более раннюю


## 14. Структура кода

* не использовать пакет по умолчанию (пустой)
* основной класс приложения располагать в корне проекта, остальные - по другим пакетам


## 15. Конфигурационные классы

* @Configuration
* @Import - дополнительные классы с конфигурациями
* или через @ComponentScan
* @ImportRecource - импорт xml-конфигураций

## 16. Автоконфигурирование

* Конфигурирование приложение автоматически на основе зависимостей
    - напр. если есть зависимость на БД типа H2 и нет бина-соединения с такой базой, будет создан для подключения к БД в памяти
* включается через **@EnableAutoConfiguration** или **@SpringBootApplication** на класс @Configuration
    - рекомендуется только одна из и для одного основного класса
* автоконфигурацию можно отключить, если создавать собственные бины
* запуск приложения с флагом `--debug` покажет как происходит конфигурация или `logging.level.root = debug`

можно отключать конкретные автоконфигурации

    @Configuration
    @EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})

## 17. Бины и DI

* Внедрять бины можно любым способом
* Обычно **@ComponentScan** (если главный класс - в корне проекта - без указания пакета можно) и **@Autowired**

## 18. @SpringBootApplication

* включает 3 аннотации:
    - @EnableAutoConfiguration (включает механизм автоконфигурирования) 
    - @ComponentScan (ищет бины в пакете, где расположено приложение(бины, отмеченные как @Component и производные))
    - @Configuration (позволяет регистрировать доп. бины и импортировать доп. конфигурационные классы)
* т.е. `@SpringBootApplication` это тоже самое, что `@EnableAutoConfiguration @ComponentScan @Configuration`
* использовать можно в любых, пропускать и т. п.

Напр. такая конфигурация (сканирование запрещено, вместо него - импорт)

    @Configuration
    @EnableAutoConfiguration
    @Import(MyConfig.class)

## 19. Запуск приложения

* из IDE
* если упакован в jar (с зависимостями) через плагин Maven/Gradle, тогда `java -jar`
* через плагины: `mvn spring-boot:run` или `gradle bootRun`

## 20. Developer tools

* Дополнительные средства для разработки
* подключаются как дополнительная зависимость

Напр. для Gradle

    configurations {
        developmentOnly
        runtimeClasspath {
            extendsFrom developmentOnly
        }
    }
    dependencies {
        developmentOnly("org.springframework.boot:spring-boot-devtools")
    }

* Автоматически отключаются при запуске упакованного приложения (java -jar)
* При подключении через Gradle создаем свою конфигурацию `developmentOnly`, чтобы не подтягивалась транзитивно в другие модули
* в jar не сохраняется по умолчанию, но можно разрешить (свойство `excludeDevtools`)

* для чего нужен:
    - установка разных свойств автоматически (напр. не кешировать шаблоны)
    - автоматический рестарт (при изменении файлов, файл-триггер)
    


## 23. SpringApplication

* класс **SpringApplication** - основной класс для запуска
    - напр. внутри main `SpringApplication.run(MyConfig.class, args);`
* падения при запуске, как анализировать:
    - запуск в флагом `java -jar --debug`
    - включение логирования на уровне DEBUG

### 23.2 Баннер

* включение в classpath файла banner.txt
* или файла banner изображения (.jpg, .png)
* можно указать раcположение через свойства `spring.banner.location` или `spring.banner.image.location`
* внутри текстового баннера можно использовать разные свойства (${application.version} и др.)
* можно программно установить текст `SpringApplication.setBanner(..)`
* свойство `spring.main.banner-mode` отвечает за показ баннера

### 23.3 Кастомизация приложения 

можно создать объект `SpringApplication`, настроить его, потом запустить

    SpringApplication app = new SpringApplication(MyConfig.class);
    app.setBannerMode(Banner.Mode.OFF);
    app.run(args);

Другой способ через файл со свойствами `application.properties`

### 23.4 Fluent Builder

    new SpringApplicationBuilder()
            .source(Parent.class)
            .child(Application.class)
            .bannerMode(Banner.Mode.OFF)
            .run(args);

### 23.5 События приложения

* разные события типа `ApplicationStartingEvent`, `ApplicationStartedEvent` и т. п.
* Можно подключать листенеры

### 23.6 Web окружение

* какого типа будет создан контекст:
    - если есть Spring MVC, тогда **AnnotationConfigServletWebServerApplicationContext**
    - если нет Spring MVC, но есть Spring WebFlux - **AnnotationConfigReactivetWebServerApplicationContext**
    - иначе **AnnotationConfigApplicationContext**
* можно указать явно через `setApplicationContextClass(..)`

### 23.7 Доступ к аргументам приложения

Если нужен доступ к аргументам, переданным в метод run, создаем бин с внедрением `org.springframework.boot.ApplicationArguments`

    @Component
    public class MyBean {
        @Autowired
        public MyBean(ApplicationArguments args) {
            boolean debug = args.containsOptions("debug");
        }
    }

### 23.8 ApplicationRunner, CommandLineRunner

* Позволяют запустить любой код прямо перед завершением `SpringApplication.run(..)`
* это способ запуска не веб приложений
* **CommandLineRunner** работает со строковым массивом аргументов
* **ApplicationRunner** работает с `ApplicationArguments`
* просто подключаем бин, реализуем метод `run(..)`
* можно несколько, порядок установить через `org.springframework.core.annotation.Order`

Пример:

    @Component
    public class MyBean implements CommandLineRunner {
        public void run(String .. args) { ... }
    }

ИЛИ

    @SpringBootApplication
    public class App {
        public static void main(String[] args) {
           ...
        }
        @Bean
        public CommandLineRunner getRunner() {
            return args -> { ... };
        }

    
### 23.9 Выход из приложения

* Если нужны доп. действия при закрытии:
    - можно стандартные спринговые `@PreDestroy` и т. п.
    - можно через `org.springframework.boot.ExitCodeGenerator`, что-то там ..


## 24. Внешние настройки приложения

### 24.-1 Кратко

Способы получения внешних свойств:

* `@Value`
    - любой бин, на свойстве `@Value("${<имя свойства>}")`
* `@ConfigurationProperties`
    - POJO класс с аннотацией `@ConfigurationProperties`, можно префикс
    - экземпляр этого класса получаем как бин
    - разрешаем использование конфигурационных классов
        + `@EnableConfigurationProperties(Props.class)` на классе-конфигурации (`@Configuration`)
        + или через сканирование `@ConfigurationPropertiesScan` (на конфигурации)
* спринговый объект `Enviroment`

Способы указания:

* ком. строка: `java -jar <имя jar> --<имя свойства>=<значение>`
* файл `application.properties` (положение, имя файла можно переопределить)
    - в папке `config` рядом с jar-ником


### 24.0 Основы

* можно настраивать приложение через:
    - property файлы
    - YAML файлы
    - переменные окружения
    - аргументы коммандной строки и др.
    
* свойства из разных источников могут перезаписывать друг друга
* приоритет источников свойств (по убыванию):
    - глобальные свойства из devtools, если devtools активны
    - аннотация `@TestPropertySource` на тестах
    - свойства-атрибуты тестов (через `@SpringBootTest` или для авто-конфигурируемых тестов)
    - аргументы командной строки
    - свойства из `SPRING_APPLICATION_JSON`
    - свойства из `ServletConfig` 
    - свойства из `ServletContext` 
    - JNDI свойства из `java:comp/env`
    - Java System properties `System.getProperties()`
    - переменные окружения ОС
    - `RandomValuePropertySource`
    - свойства приложения для профиля, вне jar-файла (`application-{profile}.properties` или YAML аналог)
    - свойства приложения для профиля, внутри jar-файла (`application-{profile}.properties` или YAML аналог)
    - свойства приложения, вне jar-файла (`application.properties` или YAML)
    - свойства приложения, внутри jar-файла (`application.properties` или YAML)
    - `@PropertySource` на @Configuration классе
    - свойства по умолчанию (`SpringApplication.setDefaultProperties`)



### 24.1 Случайные значения

* можно использовать для тестов, безопасности
* можно int, long, uuid, string

Пример:

    my.number=${random.int}
    my.bignumber=${random.long}
    my.uuid=${random.uuid}

### 24.2 Свойства из аргументов коммандной строки

* аргументы командной строки преобразуются в свойства и добавляются в Spring `Environemt` (Environemt - класс из пакета core: отвечает за профили и свойства)
    - напр. `--server.port=9009`
* приоритет у командной строки высокий, будет перезаписывать свойства из файлов
* можно отключить сохранение свойств в `Environemt`: `SpringApplication.setAddCommandLineProperties(false)`

### 24.3 Файл свойств 

* Spring загружает свойства из файлов `application.properties`
* папки по умолчанию: (по убыванию, вышестоящий переписывает нижестоящие):
    - папке `/config` в текущей директории
    - текущей папке
    - `/config` в classpath
    - корне classpath
* можно переопределить имя файла и/или его местонахождение через свойства **spring.config.name** и **spring.config.location**
    - в location можно указывать или имя файла или, папку для поиска (`--spring.config.location=classpath:/1.properties, classpath:/props/`)
    - профили не работают при указании в **location** конкретных файлов, если нужна работа с профилями, следует указывать только папки
    - указание **location** отключает поиск в папках по умолчанию
* свойство **spring.config.additional-location** добавляет доп. местонахождения. Они будут перезаписывать настройки из файлов по умолчанию 

### 24.4 Свойства для профилей 

* файлы профилей соответствуют шаблону `application-{profile}.properties`
* `Enviroment` по умолчанию устанавливает профиль с именем `default`, поэтому если есть файл `application-default.properties`, свойства будут загружены из него
* загружаются из тех же мест, что и обычные файлы свойств
* имеют более высокий приоритет, чем обычные файлы свойств

### 24.5 Placeholders в свойствах

Можно ссылаться на уже определенные свойства

    app.name=MyApp
    app.description=${app.name} is Spring app


### 24.7 YAML

* для работы с YAML нужна библиотека **ShakeYAML** в сlasspath`е
* напр. автоматически загружается при использовании `spring-boot-starter`

### 24.8 Type-safe configuration properties

#### 24.8.0. Основы

* `@Value("${property}")` позволяет внедрять свойства
    - т.е. есть файл со свойствами
    - через `@Value` вытягиваем значение этого свойства и внедряем в поле/параметр
* но может потребоваться доп. контроль, валидация. Тогда применяем 

Альтернативный подход

* общая схема:
    - создаем класс-свойство (обычный POJO-класс) (в нем можно вложенные)
        + в общем случае нужны геттеры и сеттеры
    - отмечаем аннотацией `@ConfigurationProperties("prefix=..")`
    - делаем их бинами (позволит заполнять из поля из файлов):
        + добавляем в аннотацию `@EnableConfigurationProperties` (на классе-конфигурации):
            * `@EnableConfigurationProperties(AcmeProps.class)`
        + или помечаем аннотацией напр. `@Component` 
    - создаем бин класса-свойства, при создании поля проинициализируются значениями из файлов свойств (.properties или .yaml)
    - можем обращаться к свойствам через поля класса / геттеры
    - (для IDE: автодополнение) дополнительная зависимость `annotationProcessor "org.springframework.boot:spring-boot-configuration-processor"` в файл gradle.build

Пример

    @ConfigurationProperties("acme")
    public class AcmeProps {
        private boolean enabled;
        private InetAddress remoteAdderss;
        private final Security security = new Security()
        public static class Security {
            private String username;
        }
    }

Соответствующие свойства:
* `acme.enabled`
* `acme.remote-address`
* `acme.security.username`

Эти же свойства должны быть в файле напр. `application.property`

Тогда используем так:

    @Service
    public class MyService {
        private final AcmeProps props;  
        @Autowired
        public MyService(AcmeProps props) {
            this.props = props;
        }
        @PostConstruct
        public void openConnect() {
            Server server = new Server(props.getRemoteAddress());
        }
    }


#### 24.8.1. Сторонние конфигурации

* применение `@Bean` вместе с `@ConfigurationProperties` на публичные методы
* это позволить настраивать бин через свойства из `Enviroment`
* полезно, когда это сторонние компоненты с ограниченным доступом

Напр. свойства с префиксом `another` будут внедрены в поля класса:

    @ConfigurationProperties(prefix = "another")
    @Bean
    public AnotherComponent anotherComponent() {...}

#### 24.8.2. Связи межжу именами свойств и полей бинов

* для префиксов в аннотации `@ConfigurationProperties` всегда используется kebab-case
    - `@ConfigurationProperties(prefix="acme.my-project.person")`
* свойства из Enviroment могут иметь разные имена

Пример для поля класса `private String firstName`:

* `acme.my-project.person.first-name` - рекомендуется для свойств из файлов
* `acme.myProject.person.firstName` - стандартная, можно так
* `acme.my_project.person.first_name` - альтернативная для свойств из файлов
* `ACME_MYPROJECT_PERSON_FIRSTNAME` - рекомендуется для системных переменных





### 24.X1 Секция how-to

#### 24.X1.1 Получение свойств из конфигурации проекта

из pom.xml / build.gradle
напр. `java.version`, `project.build.sourceEncoding` и т. п.

[так](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/htmlsingle/#howto-properties-and-configuration)

#### 24.Х1.2 Логгирование

на уровне `DEBUG` - показывает загружаемые конфигурационные файлы
`TRACE` - показывает кандидатов, кот. не были найдены

запуск с `--debug`

## 25. Профили

* Используются для разделения частей приложения, чтобы сделать их доступными в определенных условиях
* реализуется через `@Profile` для `@Component` или `@Configuration`

Напр. 
    
    @Configuration
    @Profile("production")
    public class ProdConfig {..}

Профиль можно установить:
* через свойство **spring.profiles.active** любым способом:
    - через файл `application.properties` `spring.profiles.active=dev,h2db`
    - через ком. строку `--spring.profiles.active=...`

Свойства также можно делать зависимыми от профиля `application-{profile}.properties` 


## 26. Логгирование

## 29. Spring MVC

* `spring-boot-starter-web`

### 29.1.1 Автоконфигурация

* включает `ContentNegotiatingViewResolver` и `BeanNameViewResolver`
* поддерживает статические ресурсы
* включает бины `Converter`, `GenericConverter`, `Formatter`
* `HttpMessageConverters`
* `MessageCodesResolver`
* поддержка `index.html`
* поддержка favicon
* `ConfigurableWebBindingInintializer`

Расширение конфигурации: 

* свой класс `@Configuration` типа `WebMvcConfigurer`
* но без @EnableWebMvc

Или полностью свой `@Configuration` с `@EnableWebMvc`

### 29.1.5 Статический контент

* видит статический контент в 
    - `/static`
    - `/public`
    - `/resources`
    - `/META-INF/resources`
* определяет с помощью стандартного `ResourceHttpRequestHandler` из Spring MVC. Можно переопределять
* если приложение пакуется в jar, использовать `src/main/webapp` нельзя


### Welcome page

* ищет `index.html` в местах хранения статического контента
* потом ищет шаблон `index`

### Facicon

* ищет `favicon.ico` в статических ресурсах или в корне classpath



# 
#
#
#
#
#
#
#
# Spring Boot Data
#
## 4.10 Работа с БД

[docs](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/reference/htmlsingle/#boot-features-sql)

### 4.10.1 Конфигурирование

через интерфейс `javax.sql.DataSource`

Кратко:

* Подключение БД
    - зависимости (сама БД + пул соединений)
    - свойства
* Использование
    - JdbcTemplate
    - JPA + SpringData JPA
    - Spring Data JDBC

#### 4.10.1.1 Подключение встроенных БД (для тестов / отладки)

поддерживается автоконфигурация:

* зависимость на `spring-jdbc` (обычно через `spring-boot-starter-data-jpa`)
* зависимость на конкретную БД (H2, HSQL, Derby)

#### 4.10.1.2 Подключение рабочих БД

Выбор пула для соединений (также автоконфигурирование):
* если есть HikariCP, он выбирается первым
* пул от Tomcat
* DBCP2
* установить тип вручную `spring.datasource.type` (из 3-х выше)
* задать пул вручную

Если бин `DataSource` задан вручную, пул автоматически не настраивается

Зависимость на `spring-boot-starter-jdbc` или `spring-boot-starter-data-jpa` включает зависимость на `HikariCP`

Настройки подключения задаются через свойства `spring.datasource.` (напр. в `application.properties`):

* обычно нужен `spring.datasource.url`, иначе Spring будет пытаться автоконфигурировать встроенную
* `spring.datasource.driver-class-name` обычно не нужен, может вывести из url
* для пулов есть свои настройки `spring.datasource.hikari.*`, `spring.datasource.tomcat.*` и др.

Пример для postgres

    spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
    spring.datasource.username=postgres
    spring.datasource.password=password

Где хранить пароли:

* в исходниках
* внешние файлы
* переменные окружения
    - временная установка
        - `export spring_datasource_password=mypass` (Linux)
        - `set spring_datasource_password=mypass` (Win)
* вводить как параметр при запуске приложения (автостарт?)
    - `java -jar app.jar --spring.datasource.username=SA`
* если приложение и БД на одной машине - через аутентификацию ОС (если БД поддерживает)
* какие-то схемы с шифрованием и т. п.
* НО если есть доступ к машине пароль не защищен никак (дампы памяти и т. п.)

### 4.10.2 JdbcTemplate

просто через `@Autowired` в бин

    @Component
    class MyBean() {
        private final JdbcTemplate jdbcTemplate;
    
        @Autowired
        public MyBean(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }
    }

Есть некоторые свойства, напр. `spring.jdbc.template.max-rows`

### 4.10.3 JPA + Spring Data JPA

#### 4.10.3.1 Основы

Зависимость `spring-boot-starter-data-jpa` включает в себя:

* Hibernate
* Spring Data JPA
* Spring ORM (корневая ORM)

Создаем Entity классы
Создаем репозитории (из Spring Data JPA)

#### 4.10.3.2 Создание / удаление БД

по умолчанию автоматически создаются только если это встроенная БД

Но можно вручную всегда через свойства `spring.jpa.*` 
напр. `spring.jpa.hibernate.ddl-auto=create-drop`
можно обращаться к свойствам Hibernate через `spring.jpa.properties.hibernate.*`

Анализ / логгирование:

`spring.jpa.show-sql=true` или `--debug` / `--trace`
или `logging.level.root`
Подробнее про инициализацию:

* `spring.jpa.generate-ddl=true/false` - независимо от провайдера
* `spring.jpa.hibernate.ddl-auto=none/validate/update/create/create-drop` - для Hibernate 
    - фвйл `import.sql` - для создании при `create/create-drop`


### 4.10.5 Консоль для H2

Автоматически будет запускаться (приложение не будет останавливаться) если:

* есть зависимость `spring-boot-starter-web`
* есть зависимость `h2`
* одно из следующего:
    - подключены Spring dev tools
    - или включено свойство `spring.h2.console.enabled=true` (не допускать в проде)

адрес по умолчанию `localhost:8080/h2-console`
можно менять `spring.h2.console.path`


