# Spring Sequrity Docs

## 4. Основы

### 4.2 Maven

c Spring Boot

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

минимально без Spring Boot

    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-config</artifactId>
    </dependency>


## 5. Особенности

### 5.0 Основы

Нужен для аутентификации, авторизации и защиты от основным уязвимостей

### 5.1 Аутентификация

Как идентифицировать того, кто пытается получить доступ с ресурсам. Обычно через логин / пароль

#### 5.1.1 Password Storage

#### 5.1.1.1 PasswordEncoder

Интерфейс `PasswordEncoder` преобразует пароли для безопасного хранения (не простым текстом)

Одна из реализаций - **DelegatingPasswordEncoder** (обобщенная, включает разные варианты энкодеров, на основе id выбирается)

    PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

Внутри разные варианты преобразования паролей, выбирается по умолчанию `bcrypt` или можно самим задавать

    String idForEncode = "bcrypt";
    Map encoders = new HashMap<>();
    encoders.put(idForEncode, new BCryptPasswordEncoder());
    encoders.put("noop", NoOpPasswordEncoder.getInstance());
    encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
    encoders.put("scrypt", new SCryptPasswordEncoder());
    encoders.put("sha256", new StandardPasswordEncoder());

    PasswordEncoder passwordEncoder =
        new DelegatingPasswordEncoder(idForEncode, encoders);

Основная задача - возможность смены алгоритма в любой момент. Для этого перечень алгоритмов и особый формат шифрованных паролей

#### 5.1.1.2 Формат хранения паролей

    {id}encodedPassword

`id` - текстовое представление алгоритма. По нему будет подобран обработчик при сравнении пароля и его представления

#### 5.1.1.3 Использование

Проверка паролей
    
    encoder.matches(rawPassword, encodedPassword);

Или через класс `User`

    UserBuilder users = User.withDefaultPasswordEncoder();
    User user = users
        .username("user")
        .password("password")
        .roles("USER")
        .build();


#### 5.1.1.4 Основные реализации

BCryptPasswordEncoder
Argon2PasswordEncoder
Pbkdf2PasswordEncoder
ScryptPasswordEncoder

другие менее

Для защиты от перебора обычно завышают сложность (время + память), заданы по умолчанию, но есть конструкторы









### 5.2 Защита от уязвимостей

#### 5.2.1 Cross Site Request Forgery (CSRF / XSRF)

##### 5.2.1.0 Основа

Подмена межсайтового запроса

Суть: пользователь авторизуется на сайте (напр. банковский) (куки сохраняются). Теперь не выходя из сайта, заходит на другой сайт, который скрытно делает запрос на сайт банка с куками

Способы решения основаны на вводе в запрос чего-то, что зловредный сайт не сможет обеспечить:

* Синхронизированный токен
* атрибут SameSite

Оба способа требуют, что бы "безопасные" методы HTTP (GET, HEAD, OPTIONS) были идемпотентными, т. е. не меняли состояние приложения

Когда использовать защиту: всегда когда пользователь работает через браузер. Если запросы не через браузер - не нужна.

JSON также можно подменять
        
    <form action=http://192.168.1.41:3000 method=post enctype="text/plain" >  
      <input name='{"a":1,"b":{"c":3}, "ignore_me":"' value='test"}'type='hidden'> <input type=submit>  
    </form>  

Stateless приложения тоже нужно защищать. Состояние может передаваться в куках. Базовая аутентификация также передается браузерами

##### 5.2.1.1 Synchronizer Token Pattern

К каждому запросу кроме кук добавляется случайный секретный CSRF токен
Сервер проверяет этот токен. Токен добавляется в запрос туда, куда браузеры не добавляют автоматически - это заголовки или параметры (но не куки)

Токен не добавляется в "безопасные" методы

Например форма может содержать

    <input type="hidden" name="_csrf"
         value="4bfd1575-3ad1-4d21-96c7-4ef2d9f86721"/>

##### 5.2.1.2 SameSite attribute

В куках устанавливается признак, что куки не могут использоваться со стороннего сайта
Spring Sequrity напрямую не работает с сессионными куками, но можно через Spring Session

Пример куков

    Set-Cookie: JSESSIONID=randomid; Domain=bank.example.com; Secure; HttpOnly; SameSite=Lax

значения **SameSite**:

* *Strict* - любой запрос с *того же* сайта будет включать куки
* *Lax* - c *того же* сайта + с сайтов того же домена

SameSite описан в спецификациях RFC

Нужна поддержка куки `SameSite` со стороны браузера
Обычно защита с SameSite это дополнительный механизм к токену


##### 5.2.1.3 Особые случаи

Logging in:

* Злоумышленник через CSRF логинится под своим логином на атакуемом сайте. Жертва теперь залогинена как злоумышленник
* Жертва совершает какие-либо действия под логином злоумышленника
* Злоумышленник теперь имеет доступ с информации о действиях жертвы

Запросы на вход / аутентификацию должны быть защищены


Logging out: аналогично


CSRF и таймауты сессий:

* обычно токен привязан к сессии и если сессия истекает запрос отклоняется
* для пользователя это не ожидаемо, решения:
    - обновлять форму перед submit для получения CRSF токена
    - уведомлять пользователя об истечении сессии, что бы он явно обновил ее
    - сохранять ожидаемый токен в куках (???)


Загрузка файлов (multipart) 

...свои методы ... (пока не интересно)


#### 5.2.2 Заголовки ответов для безопасности (HTTP Response Headers)

##### 5.2.2.1 Cache Control

Кеширование отключено по умолчанию в Spring Secure. Напр. спасает от ситуации когда по кнопке `Назад` можно получить секретные данные

Значения заголовков по умолчанию:

    Cache-Control: no-cache, no-store, max-age=0, must-revalidate
    Pragma: no-cache
    Expires: 0


##### 5.2.2.2 Content Type options

Если тип содержимого не определен явно (`content-type`), браузеры могут пытаться автоматически определять тип (content sniffing). Атака может быть такая: создается мульти файл (валидный для разных типов) и напр. с вредоносным js-кодом.

В общем нужно: 

* явно указывать content type
* запрещать content sniffing

Spring Sequrity по умолчанию:

    X-Content-Type-Options: nosniff


##### 5.2.2.3 HTTP Strict Transport Security (HSTS)

Как пользователь вводит адрес: `mybank.com` или `https://mybank.com`. Если протокол не вводится - есть опасность MITM. Даже если будет редирект на https, пока будет обмен по http есть опасность

**HSTS** создан для этого: если напр. `mybank.com` установлен как HSTS хост, последующие обращения браузер будет считать как `https://mybank.com`

сервер обозначает себя HSTS хостом через заголовок (Spring Sequrity по умолчанию):

    Strict-Transport-Security: max-age=31536000 ; includeSubDomains ; preload

Здесь 31536000 - год в секундах
includeSubDomains - на поддомены тоже работает

##### 5.2.2.4 HTTP Public Key Pinning (HPKP)

не рекомендуется для использования

##### 5.2.2.5 X-Frame-Options

содержимое сайта может встаиваться в iframe. Через скрытие видимости, можно запутать пользователя (clickjacking)

Spring Sequrity по умолчанию запрещает

    X-Frame-Options: DENY


##### 5.2.2.6 X-XSS-Protection

Защита от отраженного межсайтового скриптинга (reflected XSS) (когда данные от клиента обрабатываются сервером для отображения и выполняется вредоносный код)

Некоторые браузеры могут обнаруживать вредоносные скрипты
Spring Sequrity по умолчанию запрещает загрузку таких страниц

    X-XSS-Protection: 1; mode=block

Но не все браузеры это поддерживают.
CSP более надежная защита

#### 5.2.2.7 Content Security Policy (CSP)

Направлена на защиту от XSS. В основе информирование клиента (сервером) о источниках, к которым будут выполняться запросы

Два заголовка 

* `Content-Security-Policy`
* `Content-Security-Policy-Report-Only` (для отладки / тестирования)

Напр. так объявляем ожидаемый источник

    Content-Security-Policy: script-src https://trustedscripts.example.com

Дополнительно можно настроить отправку отчетов, если на неразрешенный источник запрос. 

    Content-Security-Policy: script-src https://trustedscripts.example.com; report-uri /csp-report-endpoint/


##### 5.2.2.8 Referrer Policy

Заголовок `Referer` содержит ссылку на предыдущую страницу

Напр. 

    Referrer-Policy: same-origin

Значит, что при переходе в пределах того же (origin) сайта, запросы будут содержать заголовок `referer`, на другой сайт - нет


##### 5.2.2.9 Feature Policy

Политика для настройки API / свойств браузера
Напр.

    Feature-Policy: geolocation 'self'


##### 5.2.2.10 Clear Site Data

Указывает какие данные на стороне клиента/браузера должны быть очищены по завершению запроса. 
Например при logout

    Clear-Site-Data: "cache", "cookies", "storage", "executionContexts"
























## 9. Безопасность сервлетов: общий обзор

### 9.1 Фильтры

Контейнер сервлетов перед обработкой запроса создает `FilterChain` из фильтров и сервлета (для Spring это `DispatcherServlet`). Запрос проходит через фильтры к сервлету, ответ от сервлета через фильтры.

Фильтры при этом могут:

* Отменить вызов следующего фильтра / сервлета, сформировав ответ сразу
* модифицировать `HttpServletRequest` или `HttpServletResponse` перед передачей следующему фильтру / сервлету
    
Напр.

    public void doFilter(ServletRequest req, ServletResponse rese, FilterChain chain) {
        // do something before the rest of the application
        chain.doFilter(req, res); // invoke the rest of the application
        // do something after the rest of the application
    }

### 9.2 DelegatingFilterProxy

**DelegatingFilterProxy** это реализация фильтра. Некая связка между API сервлетов и Spring. Пользовательский бин с логикой обработки внедряется в `DelegatingFilterProxy`, а сам `DelegatingFilterProxy` внедряется в цепочку фильтров

### 9.3 FilterChainProxy

Это бин и фильтр. Обычно встраивается в `DelegatingFilterProxy`. Внутри может содержать несколько фильтров в `SecurityFilterChain`

### 9.4 SecurityFilterChain

Содержит фильтры (бины). `FilterChainProxy` может содержать разные `SecurityFilterChain` с разными наборами фильтров. Какой из SecurityFilterChain будет применен, может решаться на основе пути

### 9.5 Security Filters

Вставляются в FilterChainProxy через API SecurityFilterChain. Порядок важен. 
Есть разные фильтры, напр. `CsrfFilter`, `LogoutFilter`, `OAuth2LoginAuthenticationFilter`  и др.

### 9.6 Обработка исключений

**AccessDeniedException**, **AuthenticationException**
специальный фильтр `ExceptionTranslationFilter ` транслирует исключения в ответы. Вставляется в FilterChainProxy.

## 10 Аутентификация

### 10.1 SecurityContextHolder

В основе модели аутентификации - `SecurityContextHolder`. Содержит `SecurityContext`. 

В SCH Spring хранит данные, о том кто авторизован. Неважно как он заполнен, с точки зрения SS, если данные есть, значит пользователь авторизован

Напр.

    SecurityContext context = SecurityContextHolder.createEmptyContext(); // 1 
    Authentication authentication =
      new TestingAuthenticationToken("username", "password", "ROLE_USER"); // 2
    context.setAuthentication(authentication);
    SecurityContextHolder.setContext(context); // 3

1. Создаем новый SecurityContext. Важно создавать новый, а не использовать `SecurityContextHolder.getContext().setAuthentication(authentication)`, что избежать гонок
2. Создаем объект `Authentication`. Сама реализация не важна, в примере простой текстовый. Есть реализации для логин/пароль, OAuth и др.
3. Устанавливаем контекст в SCH


Можно получить инфо о уже авторизированных из SCH:

    SecurityContext context = SecurityContextHolder.getContext();
    Authentication authentication = context.getAuthentication();
    String username = authentication.getName();
    Object principal = authentication.getPrincipal();
    Collection<? extends GrantedAuthority> authorities 
        = authentication.getAuthorities();







### 10.2 SecurityContext

Получается из SCH, содержит объект `Authentication`

### 10.3 Authentication

Две задачи:

* предоставляет `AuthenticationManager` для ввода данных от пользователя. В этом случае `isAythenticated()` возвращает ложь
* отображает текущего зарегистрированного пользователя. Объект `Authentication` получаем из `SecurityContext`


Объект `Authentication` содержит:

* **principal** - идентифицирует пользователя. Напр. если через логин/пароль, тогда это `UserDetails`
* **credentials** - часто пароль. Обычно очищается после авторизации, чтобы исключить утечки
* **authorities** - это экземпляр `GrantedAuthority`, разрешения, предоставленные пользователю (напр. роли) 


### 10.4 GrantedAuthority

можно получить через `Authentication.getAuthorities()`. Этот метод возвращает коллекцию `GrantedAuthority` объектов. `GrantedAuthority` содержит "роли", напр. "ROLE_ADMINISTRATOR"

Обычно эти роли распространяются на все приложение вцелом, не для разделения прав по записям. Используются другими частями SS

### 10.5. AuthenticationManager

интерфейс, определяющий как фильтры будут выполнять аутентификацию.

    Authentication authenticate(Authentication authentication)

Обычно возвращаемый `Authentication` устанавливается в SCH автоматически, но можно вручную

Одна из реализаций - **ProviderManager**

### 10.6 ProviderManager

Одна из реализаций AuthenticationManager. Содержит список экземпляров `AuthenticationProvider`. Каждый из `AuthenticationProvider` может решить аутентификация успешна, нет или просто передать решение следующему

Если ни один из `AuthenticationProvider` не смог аутентифицировать, вбрасывается исключение `ProviderNotFoundException`

Каждый из `AuthenticationProvider` выполняет конкретную аутент-цию, что позволяет при наличии единственного бина `AuthenticationManager` проводить разные типы аутентификаций

`ProviderManager` после аутентификации пытается очистить секретную информацию. Аккуратнее с кешами

### 10.7 AuthenticationProvider

Внедряются в `ProviderManager`, может быть несколько. Каждыйиз провайдером обычно выполняет конкретный вид авторизации

### 10.8 Запрос сведений через AuthenticationEntryPoint

`AuthenticationEntryPoint` используется для отправки HTTP запросов на логины/пароли клиенту

`AuthenticationEntryPoint` может напр. выполнять редирект к странице логина

### 10.9 AbstractAuthenticationProcessingFilter

Как базовый фильтр.

Процесс:

* Пользователь вводит данные
* `AbstractAuthenticationProcessingFilter ` создает `Authentication` из данных запроса
* Объект `Authentication` передается в `AuthenticationManager`
* Если не успешно, тогда `SecurityContextHolder ` очишается
* Если успешно, тогда
    - `SessionAuthenticationStrategy` оповещается о новой аутентификации
    - `Authentication` устанавливается в `SecurityContextHolder`

### 10.10 Аутентификация Логин/Пароль

#### 10.10.0 Основы

2 задачи:

* Получить данные от пользователя (через `HttpServletRequest`)
    - аутентификация через форму
    - Basic Authentication
    - Digest Authentication
* Сохранить их
    - просто в памяти in-Memory Authentication
    - БД JDBC Authentication
    - произвольный механизм через `UserDetailsService`

#### 10.10.1 Аутентификация через форму


https://docs.spring.io/spring-security/site/docs/4.1.3.RELEASE/guides/html5/hellomvc-javaconfig.html

https://docs.spring.io/spring-security/site/docs/4.1.3.RELEASE/guides/html5/form-javaconfig.html




