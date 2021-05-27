<!-- MarkdownTOC autolink="true" levels="2" uri_encoding="false" -->

- [1. Spring MVC xml config](#1-spring-mvc-xml-config)
- [2. Spring MVC java config](#2-spring-mvc-java-config)
- [3. Логирование](#3-Логирование)
- [4. Spring + Vue](#4-spring--vue)
- [6. \(Idea\) Расцветка консоли Spring Boot](#6-idea-Расцветка-консоли-spring-boot)
- [7. Vue + Axios](#7-vue--axios)
- [8. Spring Security](#8-spring-security)
- [9. Общая организация взаимодействия Spring Security и фронтенда](#9-Общая-организация-взаимодействия-spring-security-и-фронтенда)

<!-- /MarkdownTOC -->


## 1. Spring MVC xml config

Структура проекта

    src
        main
            java
            webapp
                WEB-INF
                    web.xml

1) Создаем `web.xml`
В нем прописываем DispatcherServlet (class, name, loadOnStartup, mapping)

2) Создаем конфигурационный файл для сервлета `dispatcher-servlet.xml`
В нем включаем component-scan

3) Создаем контроллер с маппингами

## 2. Spring MVC java config

Структура проекта (не сильно важно)

    src
        main
            java

для Gradle подключаем плагин `war`

1) реализуем интерфейс `WebApplicationInitializer`
внутри создаем контекст AnnotationConfigWebApplicationContext
в этом контексте регистрируем настройки сервлета (через класс с аннотацией @Configuration)
и добавляем сервлет с настройками

Например 

    AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
    ctx.register(AppConfig.class);
    
    DispatcherServlet dispatcherServlet = new DispatcherServlet(ctx);
    ServletRegistration.Dynamic registration = servletContext.addServlet("dispatcher", dispatcherServlet);
    registration.setLoadOnStartup(1);
    registration.addMapping("/");

2) создаем класс `@Configuration public class DSConfig`
включаем сканирование `@ComponentScan(basePackages=..)`

3) создаем контроллеры с маппингами




## 3. Логирование

### 3.1 Вариант 1

* Подключаем зависимость log4j
* добавляем файл настроек log4j.properties
    - должен находится в `WEB-INF/classes/` (т.е. в classpath ?)
    - для maven - в `resources`
* теперь спринговые классы будут выводить логи автоматически
* в свои классы добавить:
    - `private static final Logger logger = Logger.getLogger(MyClass.class);`
    - можно через @Autowired и создание бина
    
Например так

    @Bean
    @Scope("prototype")
    public Logger logger(InjectionPoint injectionPoint) {
       return Logger.getLogger(injectionPoint.getMember().getDeclaringClass());
    }



Пример файла `log4j.properties`:

    # Root logger option
    log4j.rootLogger=DEBUG, stdout, file
    
    # Redirect log messages to console
    log4j.appender.stdout=org.apache.log4j.ConsoleAppender
    log4j.appender.stdout.Target=System.out
    log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
    log4j.appender.stdout.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n
    
    ## Redirect log messages to a log file
    #log4j.appender.file=org.apache.log4j.RollingFileAppender
    ##outputs to Tomcat home
    #log4j.appender.file.File=${catalina.home}/logs/myapp.log
    #log4j.appender.file.MaxFileSize=5MB
    #log4j.appender.file.MaxBackupIndex=10
    #log4j.appender.file.layout=org.apache.log4j.PatternLayout
    #log4j.appender.file.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n


## 4. Spring + Vue

Варианты: 

* (просто) фронтенд на отдельном сервере (`npm run serve`)
* файлы фронтенда как статический ресурс сервера под Spring
    - отдельно
        + frontend / backend в разных папках
        + frontend разрабатывается отдельно
        + результат копируется в ресурсы backend
            * вручную
            * плагином maven
    - встроенный
        + создаем страницу index.html
        + добавляем скрипты и др.
        + контроллер Spring возвращает эту страницу

### 4.1 Встроенный Vue

[пример встроенного](https://github.com/drucoder/sarafan/tree/VueApp):

Страница html c подключенным Vue, шаблоном и скриптом создания
скрипт создания можно вынести в отдельный файл

    <script src="/js/main.js"></script>

Но здесь нельзя отдельные файлы, импорты и т. п.
Таким образом можно готовые js подключать

    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Sarafan</title>
        <script src="https://cdn.jsdelivr.net/npm/vue/dist/vue.js"></script>
     </head>
    <body>
    <div id="app">{{message}}</div>
    <script>
        var app = new Vue({
          el: '#app',
          data: {
            message: 'Hello Vue!'
          }
    })</script>
    </body>
    </html>
 
### 4.2 Связь фронтенда и бекенда через maven

#### 4.2.1 maven-frondend-plugin

Настройка maven для связи фронтенда и бекенда: [1](https://github.com/jonashackt/spring-boot-vuejs) [2](https://habr.com/ru/post/467161/)

#### 4.2.2 Копирование результатов компиляции фронтенда

Простой способ: фронтенд разрабатываем отдельно. Результат компиляции (`npm run build`) копируем в `src/resources/static`

Копирование через плагин maven **maven-resources-plugin**

Но т. к. фронтенд создает каждый раз новые файлы, необходимо очищать `resources\static` в бекенде

Настройка плагина для очистки:

    <!--clear resources/static folder before before copy frontend files-->
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-clean-plugin</artifactId>
        <version>3.1.0</version>
        <configuration>
            <filesets>
                <fileset>
                    <directory>src/main/resources/static</directory>
                </fileset>
            </filesets>
        </configuration>
    </plugin>    

Настройка копирования

     <build>
        <plugins>
            <plugin>
                <artifactId>maven-resources-plugin</artifactId>
                <executions>
                    <execution>
                        <id>copy Vue.js frontend content</id>
                        <phase>generate-resources</phase>
                        <goals>
                            <goal>copy-resources</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>src/main/resources/public</outputDirectory>
                            <overwrite>true</overwrite>
                            <resources>
                                <resource>
                                    <directory>${project.basedir}/../frontend/dist</directory>
                                    <includes>
                                        <include>static/</include>  
                                        <include>index.html</include>
                                        <include>favicon.ico</include>
                                        <include>js/</include>
                                        <include>img/</include>
                                    </includes>
                                </resource>
                            </resources>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>




### 4.3 Вариант с thymleaf

* простой index.html с div`ом для встраивания
* в dev режиме скрипты берем с тестового локального, иначе встравиваем в 
* здесь удобно front не зависит от back
* скрипт в index `<script th:src="${isDevMode} ? 'http://localhost:8000/main.js' : '/js/main.js'"></script>`
* возможно тут нужен thymleaf (как передать в шаблон параметры???)


## 6. (Idea) Расцветка консоли Spring Boot

Run / Run... / запуск как spring приложение - подсвечивает
gradle подсвечивает (запуск как spring приложение)
maven - нет

что сделать?

Run / Run... / обычный / Edit
добавить опцию VM `-Dspring.output.ansi.enabled=ALWAYS`

### 6. Терминалы Win

стандартные не поддерживают asci colors
только с win 10

Подключение через сторонние утилиты
http://jasonkarns.com/blog/ansi-color-in-windows-shells/

Сторонние терминалы:
    Cygwin Terminal
    Cmder

## 7. Vue + Axios

### 7.1 Глобальный объект Axios для запросов

    npm install axios

в файле `main.js` создаем / настраиваем объект axios, подключаем глобально

    // файл main.js
    import axios from "axios";
    
    const HTTP = axios.create({
      baseURL: 'http://localhost:8090/'
    })
    Vue.prototype.$http = HTTP;
    
    new Vue({ .. }).$mount('#app')

теперь в любом месте можем обращаться

    this.$http.post(url, ...)


подробнее про добавление свойств объекта `Vue` [vue cookbook](https://vuejs.org/v2/cookbook/adding-instance-properties.html)

## 8. Spring Security

посмотреть https://github.com/spring-projects/spring-security/tree/master/samples/javaconfig

### 8.1 Конфигурация

[baeldung](https://www.baeldung.com/java-config-spring-security)

#### 8.1.1 Настройка HTTP Security

    // отдельный класс 
    @Configuration
    //@EnableWebSecurity
    public class SecConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) {
            http.authorizeRequests()
                .antMatchers("/").permitAll();
        }
    }

Вроде везде примеры с `@EnableWebSecurity`, но работает и без (Spring Boot точно)

#### 8.1.2 Настройка AuthenticationManager (inMemory)

через **AuthenticationManagerBuilder** + добавляем **PasswordEncoder**

    @Configuration
    @EnableWebSecurity
    public class SecConfig extends WebSecurityConfigurerAdapter {
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
             auth.inMemoryAuthentication()     
                .withUser("user")
                .password(passwordEncoder().encode("1"))
                .roles("ADMIN");
        }
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }


#### 8.1.3 Отключение сессий

[git](https://github.com/jonashackt/spring-boot-vuejs)

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

(?) Кука `JSESSIONID` все равно есть

#### 8.1.4 Указания логина / пароля

##### 8.1.4.1 В файле свойств (properties)

    spring.security.user.name=user
    spring.security.user.password=1

##### 8.1.4.2 В переменных окружения

напр. в конфигурации запуска в IDE

    SPRING_SECURITY_USER_NAME=user
    SPRING_SECURITY_USER_PASSWORD=1

##### 8.1.4.3 Программно

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("foo").password("{noop}bar").roles("USER");
    }

    
#### 8.1.5 Настройка UserDetailsService

https://www.javainterviewpoint.com/spring-security-inmemoryuserdetailsmanager/

##### 8.1.5.1 Общие заметки

Настройка 2-мя основными способами:

* Получаем реализацию `UserDetailsService` и регистрируем через `AuthenticationManagerBuilder#userDetailsService(myUDS)`
* используем особые методы `AuthenticationManagerBuilder` по типу `imMemoryAuthentication`
* Реализуем `UserDetailsService`, делаем бином

Как создать свой `UserDetailsService`:

* реализовать `UserDetailsService`
* использовать готовые реализации `InMemoryUserDetailsService`, `JdbcUserDetailsManager` и т. п.

Обычно `UserDetailsService` делают бином, чтобы через него можно было добавлять новых пользователей и др.


##### 8.1.5.2 Через реализацию UserDetailsService и добавление через билдер

Реализуем интерфейс **UserDetailsService**

    public class FakeUserDetailsService implements UserDetailsService {
        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            User user = new User("1", "1", new ArrayList<>());
            return user;
    }

добавляем в настройках

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(new FakeUserDetailsService());
    }

или просто делаем бином, например `@Service`

##### 8.1.5.3 ??? Через переопределение метода userDetailsService()

`WebSecurityConfigurerAdapter#userDetailsService()`

    @Override
    protected UserDetailsService userDetailsService() {
        return super.userDetailsService();
    }

не до конца понятно, все равно нужно регистроровать через `AuthenticationManagerBuilder#userDetailsService`

##### 8.1.5.4 Через специальные методы типа AuthenticationManagerBuilder#inMemoryAuthentication

    auth.inMemoryAuthentication()
        .withUser("Bob")
        .password("1")
        .authorities(Collections.emptyList());

##### 8.1.5.5 Получение бина UserDetailsService

через метод WebSecurityConfigurerAdapter

    @Bean(name = "myUserDetailsService")
    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return super.userDetailsServiceBean();
    }

### 8.2 Отладка Spring Security

    @Configuration
    @EnableWebSecurity(debug = true)
    public class SecConfig extends WebSecurityConfigurerAdapter {
        ...
    }

    
### 8.3 Разные методы конфигурации

По умолчанию

    super.configure(http);

все URL под контролем
есть форма авторизации по умолчанию и перенаправление на нее
включена basic авторизация

Подключаем форму
    
    http.formLogin(); // стандартная

Пример (несколько ресурсов разрешены, остальные под авторизацией, basic, авто-форма)

    http.authorizeRequests()
            .antMatchers("/", "/res/**").permitAll()
            .anyRequest().authenticated()
        .and()
            .formLogin();

Ограничения по URL (любые URL)

     http.authorizeRequests().antMatchers("/**")....

Несколько ограничений, порядок важен

    http.authorizeRequests()
        .antMatchers("/**").hasRole("USER")         // любой
        .antMatchers("/admin/**").hasRole("ADMIN")  // не имеет смысла, 1-й 

### 8.4 Spring, CORS

#### 8.4.1 Пример с настройкой через аннотации 

Напр. frontend (Vue) на одном порту, backend (Spring) на другом. Браузеры запрещают в скриптах запросы к другим доменам (origin). Сервер должен явно разрешать

Разрешаем CORS, напр. так

    @RestController
    public class MainController {
        
        @CrossOrigin(origins="*", maxAge=3600
           , methods = {RequestMethod.POST, RequestMethod.GET}
           , allowCredentials = ""
           , allowedHeaders = "*")
        @RequestMapping("login")
        public String login() {
            ...
        }
    }

Если подключен Spring Security POST-запросы по умолчанию будут использовать CSFR. Если на фронте это не поддерживается, получаем 403 ошибку. CSFR можно отключить:

    @Configuration
    public class SecureConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.csrf().disable();
    }
}     

#### 8.4.2 Пример настроек через конфигурацию

    @Configuration
    @EnableWebSecurity
    public class SecureConfig extends WebSecurityConfigurerAdapter {
    
        @Override
        protected void configure(HttpSecurity http) {
            http.cors();
        }
        
        @Bean
        CorsConfigurationSource corsConfigurationSource() {
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
            return source;
        }
    }

Почему именно бин с названием `corsConfigurationSource`, см. `http.cors()` и в нем `CorsConfigurer`


### 8.5 Basic Authentication

Конфигурация

    http.authorizeRequests()
        .antMatchers("/home").permitAll()
        .antMatchers("/admin/**").hasRole("ADMIN") 
    .and()
        .httpBasic();

Теперь ответ на запрос защищенных ресурсов будет содержать заголовок

    WWW-Authenticate: Basic realm="Realm"

При авторизации в заголовок запроса включается логин и пароль (base64)

    Authorization: Basic dXNlcjphY2RmYTY2Yy0yMWI2LTQ1MT...

## 9. Общая организация взаимодействия Spring Security и фронтенда

### 9.0 Источники

[spring + angular] (https://spring.io/guides/tutorials/spring-security-and-angular-js/#how-does-it-work)

### 9.1 Первое приближение (Angular)

стандартный Spring, basic-аутентификация, без формы
для доступа к ресурсу `/resource` требуется аутентификация

фронт делает запрос в коде к ресурсу
дальнейшая обработка - на браузере: покажет форму авторизации, перенаправит на ресурс (похоже в Angular`e http-клиент может выполнять перенаправление и авторизовывать)

### 9.2 Второе приближение (login form)

Основная страница со ссылками `/home`, `/login`, `/logout`
 

### 9.x Первое приближение (Vue)

бэкенд аналогичный

Общий смысл: на фронденде храним данные (логин/пароль, токены и т. п.) и флаг авторизован пользователь или нет. Для всего этого - **Vuex**
При обращении к защищенному ресурсу на фронтенде, если не авторизован (проверка флага), перенаправляем на форму авторизации. Да флаг на фронтенде можно легко изменить внешне, но вся чувствительная информация с сервера поступает

Проверку требуется ли авторизация, делает роутер [напр. router.js] (https://github.com/jonashackt/spring-boot-vuejs/blob/master/frontend/src/router.js)

В запросы к серверу добавляем данные пользователя, сохраненные ранее при авторизации.
Напр. так: 

     getSecured(user, password) {
        return AXIOS.get(`/secured/`,{
            auth: {
                username: user,
                password: password
            }}); 

