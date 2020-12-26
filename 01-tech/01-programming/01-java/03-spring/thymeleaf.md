## 4. Основной Синтаксис

### 4.1. Messages, variables, selected object

#### th:utext vs th:text

**th:text** выводит теги как есть (экранирует спец. символы)
**th:utext** не экранирует (unescape)

#### Переменные из модели:

`th:text="${<ИмяПеременной>}"`

или больше `th:text="${<ИмяПеременной>.<ИмяПоля>}"`

для поля достаточно публичного поля или геттера

#### Параметры в сообщения

Если свойство задано типа `msg.text1=Hello,{0}`
тогда можно подставлять параметр `th:text="#{msg.text1(${user.name})}"`

само сообщение может быть любым выражением, напр. переменной
`th:text="#{${myTemplate}(${var1},${var2})}"`

#### Примеры языка выражений

* доступ к свойствам через точку `${person.father.name}`
* или через `[]` и свойства в одинарных кавычках `${person['father']}`
* для map - обращение по ключу или через `['']` эквивалентны
* доступ к элементу массива `${arr[0]}`
* можно вызывать методы, даже с аргументами

#### Специальные объекты

есть специальные объекты типа контекста, локали, запроса, ответа и т.п.
в основе обычные объекты типа HttpRequest, ApplicationContext и т.п.
можно вызывать методы, свойства
доступ к ним через `#`
обращение к их реквизитам напр. так: `${#locale.country}`

#### Вспомогательные объекты

* #execInfo - информация о процессе шаблонизации
* #dates - методы из java.util.Date
* #numbers - форматирование чисел
* #strings - строковые методы (isEmpty() напр.) 
* #lists - методы для коллекций
* и др.

напр. `th:text="${#dates.format(today)}"`, где `today` - переменная из контекста

#### Выражения на выбранных объектах (астерикс синтаксис)

* То же самое что `${...}`, но `*{...}` вычисляется на **выбранном объекте**
* **Выбранный объект** - значение атрибута `th:object`
* Если th:object не указан, ${..} и *{..} эквивалентны

Пример

    <div th:object="${user}"> 
        <p th:text=*{name}></p> // эквивалент th:text=${user.name}
    </div>


### 4.2. URLs

#### Основы

* используется `@{..}`
* `<a href="" th:href="@{..}">`
* Замена выполняется реализацией org.thymeleaf.linkbuilder.ILinkBuilder
* стандартная реализация `org.thymeleaf.linkbuilder.StandardLinkBuilder`

#### Способы указания

примеры для страницы по адресу `localhost:8080/app/root`

|Способ             |начало |th:href=      |в html       |адрес              |
|-------------------|-------|--------------|-------------|-------------------|
| абсолютный        |полный |http://a.ru/b |http://a.ru/b|http://a.ru/b      |
| page-relative     |       |user/all      |user/all     |lclhst/app/user/all|
| context-relative  |/      |/user         |/app/user    |lclhst/app/user    |
| server-relative   |~      |~/app2        |/app2        |lclhst/app2        |
| protocol-relative |//     |//app2        |//app2       |app2/              |

#### Дополнительные инструменты

* добавление параметров в запрос
    - `th:href="@{user(id=${user.id})}"` -> `user?id=2`
    - `th:href="@{user(id='1', ver='5')}"` -> `user?id=1&ver=5`
* можно шаблоны применять
    - `th:href="@{user/{id}/info(id=${id})}"` -> `user/3/info`
    


### 4.3. Литералы

* текстовые `th:text="'some text'"`, `th:text="'escape \''"`
* числовые `th:text="42 + 5"`
* булевы `true`, `false`
* 'null'
* литеральные токены
    - особые токены, содержащие только цифры, буквы, [], _, -
    - без пробелов, запятых
    - определяется как любой токен, без дополнительных средств, т.е. напр. `"'test'"` и `"test"` одно и тоже
* конкатенация текстовых - через `+`

### 4.4. Подстановка литералов

* тоже самое что конкатенация, но без указания `+`
* выражается как `|...|` с токенами внутри
* внутри могут быть выражения `${}`, `#{}`, `*{}` и литеральные токены
* нельзя текстовых литералов
* пример `'Ex:' + |Ttis is ${Text}|`

### 4.5. Операции 

* арифметические + - * / %
* сравнения < > <= >= == != 
    - < > следует заменять на &lt; &gt;
    - есть текстовые синонимы gt lt ge le eq neq/ne
* условное выражение th:text="${boolexp}? exp1: exp2"
    - в качестве выражений - $, #, @, * и литералы
    - могут быть вложенные
    - можно опускать блок else (вернет null)
* оператор по умолчанию (Элвис-оператор)
    - "exp1 ?: exp2" - если exp1 = null, возвращается exp2, иначе exp1
* применять операции можно внутри выражений и вне:
    - `th:text="${count} + 2"
    - `th:text="${count + 2}"`

### 4.6. Токен отсутствия оператора (no-op)

* выражается `_`
* означает не делать ничего, т.е. как будто напр. th:text отсутствует
* используется для отладки/прототипирования
* напр. `th:text="${user.name} ?: _"`

### 4.7. Конвертирование / форматирование

* двойные скобки ${{..}} или *{{..}} означают, что значение дополнительно обрабатывается сервисом конвертации (реализация IStandardConversionService)
* напр. Date -> String
* реализация по умолчанию просто вызывает toString()
* автоматически подтягиваются спринговые сервисы конвертирования

### 4.8. Препроцессинг


## 5. Установка значений атрибутов

### 5.1. Установка значения любого атрибута

* используем `th:attr="<ИмяАтрибута>=<Значение>"`
* напр. `<input type="submit" value="Go" th:attr="value=#{go.text}">`
* не обязательно должны быть в исходном, можно несколько
* `<input th:attr="value=#{value}, submit="@{submit}"/>`

### 5.2. Установка значения конкретного атрибута

* `th:<ИмяАтрибута>="<Значение>"`
* напр. `<input value="Go!" th:value="#{msg.go}"/>`
* есть перечень допустимых имен атрибутов

### 5.3. Fixed-value boolean attrs

* Атрибуты типа checked, default, hidden, disabled и др.
* не имеют значений, само их присутствие уже что-то значит
* поэтому может выражаться без значений или со значением равным самому атрибуту
* напр. `<input type="checkbox" checked/>` или тоже самое `<input type="checkbox" checked="checked"/>`
* для таких атрибутов значение определяется на истину/ложь. Если ложь - вообще не включается `<input type="checkbox" th:checked="${user.isActive}"/>`

### 5.4. Произвольные атрибуты

* через `th:<ИмяАтрибута>=<Значение>` можно устанавливать любые атрибуты, в том числе собственные
* `<p th:myCustomAttr="xxx">`


## 6. Циклы

### 6.1. Основы

* `th:each="<Переменная> : <ИтерируемаяКоллекция>"`
* повторяет для каждого элемента из коллекции этот тег (и вложенные)
* внутри есть доступ к переменной

Напр. 

    <table>
        <tr th:each="prod: ${prods}">
            <td th:text="${prod.name}">Smth</td>
        </tr>
    </table>

* в качестве коллекций:
    - Iterable
    - Enumeration
    - Iterator
    - Map, в качестве елементов - Map.Entry
    - массивы
    - можно любой другой объект, как единственный элемент

### 6.2. Статус итерации

* через переменную **iterStat** есть доступ к:
    - *index* - текущий индекс с 0
    - *count* - текущий индекс с 1
    - *size* - размер коллекции
    - *current* - текущий элемент
    - *even/odd* - четная/нечетная итерация
    - *first/last* - первая/последняя
* переменная статуса объявляется через зпт от имени переменной элемента
* имя произвольное
* если не указано, все равно есть, имя = имяПеременной + "Stat"

Пример

    <tr th:each="prod, prodStat : ${prods}" th:class="${prosStat.odd}? 'odd'">

### 6.3. Ленивое получение данных

* Напр. получение данных из БД можно делать только когда действительно они нужны (напр. на странице отображаются при каких-то условиях)
* реализуется через интерфейс **ILazyContextVariable**

Напр. через наследование класса LazyContextVariable
    
    model.addAttribute("var",
        new LazyContextVariable<List<String>>() {
            @Override
            protected List<String> loadValue() {
                return ...;
            }
    });

* В макете такая переменная используется как обычная, но вызов метода `loadValue()` происходит только, когда необходимо


## 7. Условные вычисления

### 7.1 **if**, **unless**

* `th:if="<...>"` исключает блок, если выражение вычисляется в ложь
* правила вычисления выражения:
    - булевы обычно
    - null - false
    - числа: 0 - false, остальные - true
    - символы - не 0 - истина
    - строки: все, кроме "false", "no", "off" в истина
    - остальные объекты - истина
* `th:unless` - тоже самое что НЕ if

Пример влючения ссылки по условию

    <a href="comment.html" th:href="@{comments.html}" th:if="${not #lists.isEmpty(prod.comments)}">...</a>

### 7.2 Switch

    <div th:switch="${user.role}">
        <p th:case="'admin'">Admin</p>
        <p th:case="'manager'">Manager</p>
        <p th:case="*">unknown</p>
    </div>

* если одно из условий case вычисляется в Истина, остальные не проверяются
* ветка по умолчанию обозначается как `th:case="*"`



## 8. Template layout

### 8.1 Fragment layout

* объявляем фрагмент (обычно в отдельном файле)
    - `... <div th:fragment="copy">...`
* включаем через **th:insert** или **th:replace**
    - `<div th:insert="~{footer::copy}"><div>`
    - где *footer* - файл шаблона footer.html
* insert/replace требуют fragment expressin (~{...}), но в простых случаях можно опускать
    - `<div th:insert="footer::copy"><div>`

#### Синтаксис 

* **~{templatename::selector}**
    - здесь *templatename* - имя шаблона (файла html)
    - *selector* - селектор для выбора блока (Markup Selector, есть описание)
        + в простом случае - имя фрагмента (`"footer :: copy`)
        + есть вариант по id
            * исходный файл `<div id="copy-section">...`
            * использование `<div th:insert="~{footer :: #copy-section}">`
* **~{templatename}**
    - включает весь шаблон
* **~{::selector}** или **~{this::selector}**
    - ищет фрагмент в текущем шаблоне
    - если не найден, поиск по стеку шаблонов (??)
* в *selector* может помещаться любое выражение:
    - `<div th:insert="footer :: (${user.admin}? #{footer.admin} : default)">`
* фрагменты могут включать любые теги th:*. Они будут вычисляться в контекста конечного шаблона

#### insert vs replace

* **insert** вставляет фрагмент в тело своего тега
* **replace** заменяет свой тег фрагментом

### 8.2 Параметризуемые фрагменты

* Фрагменты могут включать параметры

Пример:

    <div th:fragment="myFrag(var1, var2)">
        <p th:text="${var1} + ' - ' + ${var2}"></p>
    </div>

Использовать такой фрагмент нужно с указанием параметров

    <div th:replace="::myFrag(${value1}, ${value2})">

или с именованными параметрами (тогда порядок не важен)

    <div th:replace="::myFrag(var2=${value2}, var1= ${value1})">

Можно не объявлять параметры при объявлении фрагмента (th:fragment="myFrag"), но все равно вызвать его с именованными параметрами. + работает получение параметров из контекста.

#### assert

* для проверки параметров можно использовать assert
* передаются выражения через зпт, если хотя бы одно не вычисляется в true, вбрасывается исключение
    - `<header th:fragment="foo(title)" th:assert="${!#strings.isEmpty(title)}">`


### 8.3 Гибкость фрагментов

* в качестве параметров фрагментов можно передавать сами фрагменты 

Напр. 

    <head th:fragment="myHeader(title)">
        <title th:replace="${title}">Common</title>
    </head>

Используем:

    <head th:replace="base :: myHeader(~{::title})">
        <title>New title</title>

* **Пустой фрагмент** (~{})
    - если замещается на пустой фрагмент - блок удаляется
* **no-op** (_)
    - означает, что ничего не происходит, остается тот блок, что был в шаблоне

Так

    <head th:replace="base :: myHeader(_)">
        <title>New title</title>

преобразуется в 

    <head>
        <title>New title</title>

Можно например проверять существует ли шаблон:

    <div th:insert="~{common :: salutation} ?: _">
        .. что-то по умолчанию, если нет швблона

#### Удаление фрагментов

* Можно включать блоки для прототипирования. Без обработки процессором они будут показываться. В конечном шаблоне - не нужно
* используется атрибут `th:remove`
    - напр. `<tr th:remove="all">`
* параметры атрибута `th:remove`:
    - **all** - удаляет сам тег и его дочерние теги
    - **body** - удаляет дочерние теги, но не сам тег
    - **tag** - удаляет тег, дочерние остаются
    - **all-but-first** - удаляет все дочерние, кроме первого
    - **none** - ничего не делает, для программного управления
* также можно использовать любые выражения


## 9. Local variable

* Переменные, видимые в только в определенных блоках.
* напр. переменная цикла `<tr th:each='"prod : ${prods}">` видна внутри блока `tr` и вложенных `td`
* можно объявлять свои `th:with="<Имя>=<Значение>"`
* действует внутри блока, где объявлена
* можно объявлять несколько
* перезаписывают значение переменных из контекста

## 10. Приоритет атрибутов

* Если несколько атрибутов в одном теге, в каком порядке они разрешаются?

Приоритет (по убыванию):

1. Включение фрагмента (th:insert, th:replace)
2. Итерации (th:each)   
3. Условные вычисления (th:if, th:unless, th:switch, th:case)
4. Локальные переменные (th:object, th:with)
5. Общие атрибуты (th:attr)
6. Конкретные атрибуты (th:value, th:href, ...)
7. Текст (th:text)
8. Определение фрагмента (th:fragment)
9. Удаление фрагмента (th:remove)


## 11. Комментарии, блоки

### 11.1 Стандартные комментарии

* работает `<!-- -->`, thymeleaf никак не обрабатывает

### 11.2 Комментарии для парсера

* всё, что между `<!--/*` и `*/-->` удаляется во время обработки процессором
* можно использовать для статической отладки

### 11.3 Комментарии для прототипа

* `<!--/*/` и `/*/-->` - эти комментарии будут удалены процессором
* т. е. код между закомментирован в статике, но становится виден после обработки

### 11.4 Искусственные блоки через `th:block`

* создает контейнер для других атрибутов
* напр. можно две строки в итерации

Напр.

    <table>
        <th:block th:each="...">
            <tr>
                ...
            </tr>
            <tr>
                ...
            </tr>
        </th:block>
    </table>


## 12. Inlining

### 12.1 Для выражений

* выражения можно встраивать сразу в текст вместо атрибутов 
    - `<p>Hello, [[${user.name}]]</p>`
* `[[..]]` - это аналог `th:text`, `[(..)]` - аналог `th:utext`
* при открытии статически, так и будут видны, в отличие от случая с атрибутами
* можно отключить, если в тексте встречаются [[]] или [()]
    - `<p th:inline="none">list [[1, 2], [3]]`

### 12.2 Для текста

* включается через `th:inline="text"`
* используется для режима текстовых шаблонов (см. далее)

### 12.3 Для JavaScript

* для улучшенной поддержки блока <script>
* включается через `th:inline="javascript"`

Напр.
    
    <script th:inline="javascript">
        ...
        var name = [[${user.name]]
        ...
    </script>

* дополнительно обрамляет кавычками, добавляет экранирование в соответствии с синтаксисом JS если использовать экранированный вариант `[[..]]`


## 13. Режим текстовых шаблонов

* это режимы TEXT, JAVASCRIPT, CSS
* в них нет тегов (в отличие от HTML, XML), нет атрибутов, поэтому приходится иначе: напр. через inlining 

## 15. Конфигурация

### 15.4 Логгирование 

* Использует slf4j
* классы используют TRACE, DEBUG, INFO
* к основному логгеру добавлены дополнительно три:
    - org.thymeleaf.TemplateEngine.CONFIG (информация по инициализации библиотеки)
    - org.thymeleaf.TemplateEngine.TIMER (время на обработку шаблонов)
    - org.thymeleaf.TemplateEngine.cache (инфо о кеше), в двух видах
        + org.thymeleaf.TemplateEngine.cache.TEMPLATE_CACHE
        + org.thymeleaf.TemplateEngine.cache.EXPRESSION_CACHE

Пример конфигурации для log4j:

    log4j.logger.org.thymeleaf=DEBUG
    log4j.logger.org.thymeleaf.TemplateEngine.CONFIG=TRACE
    log4j.logger.org.thymeleaf.TemplateEngine.TIMER=TRACE
    log4j.logger.org.thymeleaf.TemplateEngine.cache.TEMPLATE_CACHE=TRACE

## 99. Заметки

### Работа с файлами properties

#### Вариант 1:

* Файлы .properties в произвольном месте расположены
* тогда нужно в engine добавить MessageSource
    - `engine.setTemplateEngineMessageSource(messageSource());`

создаем так

    private ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        return messageSource;
    }

* в war файл .properties должен быть внутри `/WEB-INF/classes`.

Например:

файл `/src/main/resources/1/messages.properties`
в war попадет в `/WEB-INF/classes/1/messages.properties`
нужно указать `messageSource.setBasename("1/messages");`


#### Вариант 2:

* Файлы .properties размещаем рядом с шаблонами, с тем же именем
* тогда не нужен `MessageSource`
* по умолчанию подтягивет

### Простейший пример

#### Зависимости

    // https://mvnrepository.com/artifact/org.springframework/spring-webmvc
    compile group: 'org.springframework', name: 'spring-webmvc', version: '5.1.9.RELEASE'

    // https://mvnrepository.com/artifact/javax.servlet/javax.servlet-api
    providedCompile group: 'javax.servlet', name: 'javax.servlet-api', version: '3.1.0'

    // https://mvnrepository.com/artifact/log4j/log4j
    compile group: 'log4j', name: 'log4j', version: '1.2.17'

    // https://mvnrepository.com/artifact/org.thymeleaf/thymeleaf-spring5
    compile group: 'org.thymeleaf', name: 'thymeleaf-spring5', version: '3.0.11.RELEASE'

    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
    compile group: 'org.slf4j', name: 'slf4j-simple', version: '1.7.25'

    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    compile group: 'org.slf4j', name: 'slf4j-api', version: '1.7.25'

#### Конфигурация 

    @Configuration
    @ComponentScan("controller")
    public class AppConfig implements WebMvcConfigurer, ApplicationContextAware {
    
        private ApplicationContext applicationContext;
    
        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }
    
        @Bean
        public ViewResolver htmlViewResolver() {
            ThymeleafViewResolver resolver = new ThymeleafViewResolver();
            resolver.setTemplateEngine(templateEngine(htmlTemplateResolver()));
            resolver.setContentType("text/html");
            resolver.setCharacterEncoding("UTF-8");
            return resolver;
        }
    
        @Bean
        public ResourceBundleMessageSource messageSource() {
            ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
            messageSource.setBasename("messages");
            return messageSource;
        }
    
        private ISpringTemplateEngine templateEngine(ITemplateResolver templateResolver) {
            SpringTemplateEngine engine = new SpringTemplateEngine();
            engine.setTemplateResolver(templateResolver);
            engine.setTemplateEngineMessageSource(messageSource());
            return engine;
        }
    
        private ITemplateResolver htmlTemplateResolver() {
            SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
            resolver.setApplicationContext(applicationContext);
            resolver.setPrefix("/WEB-INF/views/");
            resolver.setSuffix(".html");
            resolver.setCacheable(false);
            resolver.setTemplateMode(TemplateMode.HTML);
            return resolver;
        }
