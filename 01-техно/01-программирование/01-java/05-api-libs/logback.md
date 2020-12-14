## 2. Архитектура

Logger, Appender, Layout

### Иерархия логгеров

Логгеры образуют иерархию через имена.
Логгер с именем "java" родитель для "java.util" и предок для "java.util.Vector"
Корневой логгер можно получить через `LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)`
остальные можно получать через `LoggerFactory.getLogger(<Имя логгера>)`

### Эффективный уровень логгера

Каждому логгеру может быть назначен уровень (TRACE, DEBUG, INFO, WARN, ERROR - logback.classic.Level) (класс Marker - если нужны доп. уровни)
Если не назначен уровень явно - берется уровень ближайшего предка с указанным уровнем
Корневой логгер имеет уровень по умолчанию **DEBUG**
Установить уровень:

    // преобразование типа если через slf4j логгер создавался
    ((ch.qos.logback.classic.Logger)logger).setLevel(Level.TRACE);

### Вывод и уровень логгера

Каждый метод (trace(), debug(), ...) соответствует уровню
метод будет выводить сообщение, если уровень логгера меньше или равен уровню метода
Порядок уровней: **TRACE < DEBUG < INFO < WARN < ERROR**

### Получение логгеров

Создание логгеров с одинаковым именем дает один и тот же экземпляр
Можно настроить логгер в одном месте, использовать в другом
Можно настроить логгер, а после создать/настроить логгер-предок

### Appenders

**Appender** - то, куда выводятся логи
устанавливать можно через метод `addAppender()`
один и более appender'ов можно присоединить к одному логгеру
appender'ы наследуются через иерархию
сообщения выводятся во все appender'ы текущего логгера и (по умолчанию) в appender'ы предков
У логгеров (кроме root) есть **additivity flag**. Если при поиске appender'ов в иерархии доходят до логгера со сброшенным флагом, обход иерархии прекращается

### Layouts

**Layout** - то, как выводятся логи

### Параметры сообщений

Варианты:

* Конструируем сообщение перед передачей в метод:
    - `logger.info("some msg " + var);`
    - минус: вычисляется выражение, даже если не будет выводится из-за уровня логгера
* Передаем шаблон и параметры:
    - `logger.info("some msg {}", var);`
    - плюс: выражение будет вычисляться только если нужно выводить


### Производительность

* Когда логгирование отключено
    - напр. через установку **Level.OFF** в корне
    - траты только на вызов методов типа `logger.info()` и целочисленное сравнение, что не сильно затратно
    - только избегать вычислений в параметрах типа `info("a=" + i + ".")`
    - также избегать сообщений внутри циклов, даже с отключенным логгированием это затратно
* Определение разрешено логгирование или нет на текущем уровне
    - хотя формально логгер должен инспектировать своих предков для определения уровня, это не происходит каждый раз при вызове методов-сообщений
    - при изменении уровня логгера события идут всем дочерним


## 3. Конфигурация

### 3.1 Определение конфигурации

Ищет файлы конфиграции в classpath:

* Просто корень для обычных приложений
* `META-INF/classes/` для веб

Порядок поиска:

* **logback-test.xml**
* **logback.groovy**
* **logback.xml**
* service-provider loading facility
* конфигурация по умолчанию **BasicConfigurator**

### Пример конфигурации (аналог конфигурации по умолчанию)

    <configuration> 
        <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender"> 
             <!-- encoders are  by default assigned the type
             ch.qos.logback.classic.encoder.PatternLayoutEncoder -->
            <encoder>
              <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        <root level="error">
            <appender-ref ref="STDOUT" />
        </root>
    </configuration>

### Включение отладочной информации

    <configuration debug="true"> 
        ...
    </configuration>

Будет выводится если найден конфигурационный файл и он корректен

Иначе можно настроить **StatusListener** 

### Установка расположения файла конфигурации через системное свойство

**logback.configurationFile** 
напр. `java -Dlogback.configurationFile=/path/to/config.xml MyApp`
или установить программно (но до создания логгеров):
`System.setProperty("logback.configurationFile", "/path/to/config.xml")`
`System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, "/path/to/config.xml")`

### Автоизменение конфигурации при изменении конфигурационного файла

    <configuration scan="true"> 
        ...
    </configuration>

Период сканирования по умолчания 1 минута
Можно изменить

    <configuration scan="true" scanPeriod="30 seconds"> 
      ...
    </configuration> 

Если при редактировании возникли ошибки - вернется на прежнюю версию


### Вывод информации о пакетах

    <configuration packagingData="true"> 
      ...
    </configuration> 

выводит информацию о jar'ах: имя и версия
но может быть затратно (особенно при трассировке исключений)

### Остановка логгеров

для освобождения ресурсов остановке приложения

    import org.sflf4j.LoggerFactory;
    import ch.qos.logback.classic.LoggerContext;
    ...
    // assume SLF4J is bound to logback-classic in the current environment
    LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    loggerContext.stop();

или через конфигурацию

    <configuration debug="true">
        <shutdownHook/>
        .... 
    </configuration>

здесь назначается класс по умолчанию `DefaultShutdownHook`
при завершении дает время (30 сек) для процессов типа архивирования файлов (выполняемых в фоне) после остановки

Для веб-приложения автоматически подключается `WebShutdownHook`

### 3.2 Синтаксис конфигурационного файла

#### 3.2.1 Общая схема

* configuration
    - appender (0 или больше)
    - logger (0 или больше)
    - root (ровно 1)

теги:

* основные(logger, appender) - регистронезависимые 
* в общем случае - регистрозависимые
* когда не известно - использовать camelCase

#### 3.2.2 Logger

    <logger name="..." additivity="true/false" level="...">
        <appender-ref>...</appender-ref>
    </logger>

атрибуты **additivity**, **level** - не обязательны
значения **level**:
* TRACE/DEBUG/...
* ALL
* OFF
* INHERITED/NULL - наследует уровень от предков

блоков **appender-ref** - 0 и больше

#### 3.2.3 Root

    <root level="...">
        <appender-ref> ... </appender-ref>
    </root>

Имя по умолчанию `ROOT`, поэтому атрибута `name` нет
**level** не допускается `NULL` или `INHERITED`, по умолчанию - `DEBUG`
appender'ов также может быть 0 и больше



#### 3.2.4 Пример конфигурации логгеров

    <configuration>
        <appender name="STDOUT">
        ...
        </appender>
        <logger name="chapters.configuration" level="INFO" />
        <logger name="chapters.configuration.Foo" level="DEBUG" />
        <root level="OFF">
            <appender-ref ref="STDOUT" />
        </root>
    </configuration>

#### 3.2.5 Конфигурация appender'ов

##### 3.2.5.1 Основы
    <appender name="..." class="...">
        <layout> ... </layout>          // 0 и больше
        <encoder>...</encoder>          // 0 и больше
        <filter>...</filter>            // 0 и больше
    </appender>

`name, class` - обязательны
Могут быть любые другие атрибуты для JavaBean класса appender'а

**layout** имеет обязательный атрибут `class` плюс произвольные реквизиты от JavaBean. Если в качестве layout'а используется `PatternLayout` доп. реквизиты можно не указывать

**encoder** - обязательный атрибут `class`. Также если это `PatternLayoutEncoder` доп. реквизиты можно по умолчанию использовать без указания

Одни и те же appender'ы можно включать в разные логгеры. Но layout'ы и encoder'ы определяются внутри appender'а и не делятся между appender'ами

##### 3.2.5.2 Куммулятивность appender'ов

Логгер выводит данные через appender'ы добавленные к нему и в appender'ы, добавленные в предков этого логгера. Если один и тот же appender добавлен и в логгер, и в его предка (root например), сообщения будут выведены дважды

Так например можно root назначить вывод в консоль, а конкретному логгеру - вывод в файл, тогда все логгеры будут выводить в консоль и конкретные дополнительно в файл.

Отключить можно через сброс флага **additivity**

    <logger name=".." additivity="false">
        ...
    </logger>

    

    


#### 3.2.6 Установка имени контекста

Применяется если нужно различать несколько источников / приложений, которые пишут логи в одно место

    <configuration>
      <contextName>myAppName</contextName>
      <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
          <pattern>%d %contextName [%t] %level %logger{36} - %msg%n</pattern>
        </encoder>
      </appender>
      ...
    </configuration>

По умолчанию имя контекста - *default*
Устанавливается один раз и больше не меняется
Можно настроить вывод через шаблоны



#### 3.2.7 Определение и замена переменных

##### 3.2.7.0

Можно определять переменные
Переменные имеют область видимости
Переменные можно объявлять внутри конфигурационного файла, внешнего файла, внешнего ресурса, вычислять на лету
Обращение к переменной по имени: `${aName}`
Предопределенные переменные: `HOSTNAME` и `CONTEXT_NAME`

##### 3.2.7.1 Определение переменной

Определяется внутри блока **property** или **variable** (равнозначны)
    
    ...
      <property name="USER_HOME" value="/home/sebastien" />
     
      <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>${USER_HOME}/myApp.log</file>
    ...

Эту переменную можно не объявлять в конфигурационном файле, а указать как системную переменную:

`java -DUSER_HOME="/home/sebastien" MyApp`

Вариант с указанием переменных в отдельном файле:

    ...
    <property file="src/main/java/configuration/variables1.properties" />
    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>${USER_HOME}/myApp.log</file>
    ...

Или в файле в classpath:

    ...
    <property resource="resource1.properties" />
    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>${USER_HOME}/myApp.log</file>
    ...



##### 3.2.7.2 Область видимости

**Local scope** - по умолчанию. От определения в конфигурационном файле до конца интерпретации файла. Каждый раз при парсинге файла создается заново
**Context scope** - внедряется в контекст и живет пока действует контекст
**System scope** - внедряется в свойства JVM

Порядок поиска значений: local -> context -> system -> переменные ОС (System.getenv())

Для указания области - атрибут `scope` со значениями `local`, `context` или `system` 

    <property scope="context" name="nodeId" value="firstNode" />

##### 3.2.7.3 Значения по умолчанию

через оператор `:-`, если значение не определено, будет использовано значение по умолчанию

    "${aName:-golden}"

##### 3.2.7.4 Вложенные переменные

Переменную можно определять через другие переменные:

    файл vars.properties
    USER_HOME=/home/seb
    file=myApp.log
    destination=${USER_HOME}/${file}

Имена переменных можно определять через другие переменные:

    ${${userId}.password}

Значения по умолчанию можно через другие переменные:

    ${id:-${userid}}

#### 3.2.8 Условные блоки

внутри конфигурационного файла
    
    <if>
        <then>
        ...
        </then>
        <else>
        ...
        </else>
    </if>

#### 3.2.9 Включение файлов

    <configuration>
        <include file="....xml">
        ...
    </configuration>

Для включения файла из classpath

    <include resource="...">

Сам файл обязательно должен начинаться с тега `<included>`





























## 4. Appenders

### 4.0

Реализация интерфейса `ch.qos.logback.core.Appender`

### 4.1 logback-core

#### 4.1.1 OutputStreamAppender

Это базовый класс для других appender'ов, выводящих в OutputStream
Имеет наследников `ConsoleAppender` и `FileAppender`
Напрямую не используется

Настройки / свойства:
* **encoder**
* **immediateFlush** - по умолчанию истина. Гарантирует, что сообщения будут выведены в поток (сохранены на диск) сразу и не потеряны при выходе из приложения без корректного закрытия логгеров. Устанавливают в ложь для повышения производительности

#### 4.1.2 ConsoleAppender

Выводит в System.out или в System.err

Настройки / свойства:
* **target** - "System.out" (по умолчанию) или "System.err" 
* **withJansi** - по умолчанию ложь. Для вывода в цвете для Win. Требуется jansi.jar

#### 4.1.3 FileAppender

Настройки / свойства:
* **append** - если истина (по умолчанию), события добавляются в конец существующего файла
* **file** - имя файла. Если нет - создается (в т. ч. промежуточные директории) Не забывать экранировать escape-последовательности: `c:/temp/test.log` неправильно. `c://temp//test.log` или `c:\temp\test.log`
* **prudent** - ложь по умолчанию. При установке позволяет писать в один файл от нескольких JVM / хостов. Снижает производительность

#### 4.1.4 Использование timestamp для имен файлов

timestamp - блок в конфиг. файле. Обязательные атрибуты `key` и `dataPattern` 
Под именем значения атрибута `key` будет переменная доступна. `dataPattern` - шаблон (по соглашениям `SimpleDateFormat`) для форматирования текущего времени (времени парсинга конфиг. файла)

можно использовать для сервисов, которые часто запускаются/останавливаются

    <configuration>
        <timestamp key="bySecond" datePattern="yyyyMMdd'T'HHmmss"/>
        <appender name="FILE" class="ch.qos.logback.core.FileAppender">
            <file>log-${bySecond}.txt</file>
        ...
    </configuration>


#### 4.1.5 RollingFileAppender

настройки / свойства: 

* **file**
* **append**
* **encoder**
* **rollingPolicy** - отвечает что делать
* **triggeringPolicy** - когда делать
* **prudent** - аккуратно для разных вариантов политик (напр. нельзя переименовать файл, если он в нескольких JVM)

В некоторых вариантах rollingPolicy уже включен и triggeringPolicy


##### 4.1.5.1 TimeBasedRollingPolicy

Периодическое обновление логов
Не нужен triggeringPolicy

Настройки / свойства
* **fileNamePattern** 
    - основной обязательный атрибут
    - содержит имя файла + спецификаторы `%d{..}`
    - спецификатор содержит шаблон форматирования (по `SimpleDateFormat`) 
    - если нет - по умолчанию "yyyy-MM-dd"
    - из этого шаблона определяется период для обработки
    - `/` или `\` всегда трактуются как разделители пути
    - можно включать несколько спецификаторов `%d{}`, но только один должен быть основной, остальные помечаются как дополнительные (`aux`)
    - обработка привязана к сообщениям, не к часам. Т.е. например для дневного цикла архивирование будет не ровно в 00:00, а когда будет соообщение позже
* **file** 
    - можно не указывать
    - если указан, тогда текущий активный лог будет по этому имени, а архивные через **fileNamePattern** 
    - для сжатия архивных файлов в конец шаблона добавляется `.gz` или `.zip`
* **maxHistory** - число, максимальное количество сохраняемых архивных файлов. При превышении, будут удаляться
* **totalSizeCap** - максимальный размер архивных файлов. Должен быть установлен также **maxHistory**. Сначала проверяется maxHistory, потом totalSizeCap
* **clearHistoryOnStart** - по умолчанию ложь. Удаление архивов происходит по периоду. Для короткоживущих приложений может не наступать, тогда устанавливают в истина

Примеры шаблонов:
`/wombat/foo.%d` - ежедневные логи типа `/wombat/foo.2019-10-24`
`/wombat/%d{yyyy/MM}/foo.txt` - ежемесячные `/wombat/2019/10/foo.txt`
`/foo/%d{yyyy-MM,aux}/%d.log` - ежедневные `/foo/2019-10/2019-10-24.log`
`/wombat/foo.%d.zip` - ежедневные с архивированием

Пример конфигурации

    <configuration>
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>logFile.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <!-- daily rollover -->
                <fileNamePattern>logFile.%d{yyyy-MM-dd}.log</fileNamePattern>
                <!-- keep 30 days' worth of history / 3GB total size -->
                <maxHistory>30</maxHistory>
                <totalSizeCap>3GB</totalSizeCap>
            </rollingPolicy>
        <encoder>
            <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>
        </encoder>
    </appender> 
    <root level="DEBUG">
        <appender-ref ref="FILE" />
    </root>
    </configuration>


##### 4.1.5.2 SizeAndTimeBasedRollingPolicy

Для архивирования по периодам и с ограничением размера каждого файла
Все тоже самое, что и для `TimeBasedRollingPolicy` (в т. ч. ограничение на общий размер всех файлов totalSizeCap)

Дополнительно в **fileNamePattern** добавляется обязательный спецификатор `%i` (индекс с 0) и вводится атрибут **maxFileSize**

Пример

    ...
    <appender name="ROLLING" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>mylog.txt</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
        <!-- rollover daily -->
            <fileNamePattern>mylog-%d{yyyy-MM-dd}.%i.txt</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>    
            <maxHistory>60</maxHistory>
            <totalSizeCap>20GB</totalSizeCap>
        </rollingPolicy>
    ...


##### 4.1.5.3 FixedWindowRollingPolicy

Формирует имена архивов по алгоритму на основе индекса `%i`
Требует явного указания **triggeringPolicy**

Настройки / свойства 
* **minIndex**
* **maxIndex**
* **fileNamePattern** - шаблон для именования файлов. Должен содержать `%i`. Также поддерживает сжатие через `.zip/.gz`

Т. к. при достижении максимального индекса при последующих сохранениях будут переименовываться все файлы, не рекомендуется указанию большого диапазона. И вообще независимо от значений, заданных пользователем, ограничивается 20.

Обязательно указание имени файла **file**

Пример

    <configuration>
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>test.log</file>
    
        <rollingPolicy class="ch.qos.logback.core.rolling.FixedWindowRollingPolicy">
            <fileNamePattern>tests.%i.log.zip</fileNamePattern>
            <minIndex>1</minIndex>
            <maxIndex>3</maxIndex>
        </rollingPolicy>
    
        <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
          <maxFileSize>5MB</maxFileSize>
        </triggeringPolicy>
        ...






##### 4.1.5.4 SizeBasedTriggeringPolicy

Триггер на основе размера текущего файла **maxFileSize**
По умолчанию 10MB, можно указывать с суффиксами GB, MB, KB

Пример 

    <configuration>
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>test.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.FixedWindowRollingPolicy">
                <fileNamePattern>test.%i.log.zip</fileNamePattern>
                <minIndex>1</minIndex>
                <maxIndex>3</maxIndex>
            </rollingPolicy>
    
            <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
                <maxFileSize>5MB</maxFileSize>
            </triggeringPolicy>
        ...
    </configuration>




### 4.2 logback-classic

#### SocketAppender, SSLSocketAppender

#### ServerSocketAppender, SSLServerSocketAppender

#### SMTPAppender

#### DBAppender

#### SyslogAppender

#### AsyncAppender


## 5. Encoders

### 5.0 

Задачи: 
* преобразование события в массив байтов
* запись в выходной поток

Layout только преобразует событие в строку, encoder имеет больше возможностей по записи (объединять в пакеты, когда и как и т. п.)

Основной - **PatternLayoutEncoder**, типа обертка над **PatternLayout**

### 5.1 LayoutWrappingEncoder

Обертка для layout

### 5.2 PatternLayoutEncoder

Расширение **LayoutWrappingEncoder**

Назначается appender'у по умолчанию

Свойство **outputPatternAsHeader** - выводит шаблон перед логами

Пример

    <appender name="FILE" class="ch.qos.logback.core.FileAppender"> 
      <file>foo.log</file>
      <encoder>
        <pattern>%d %-5level [%thread] %logger{0}: %msg%n</pattern>
        <outputPatternAsHeader>true</outputPatternAsHeader>
      </encoder> 
    </appender>


## 6. Layout

Преобразуют события в строковое сообщение

### 6.1 PatternLayout

#### 6.1.1

FileAppender и его наследники требуют encoder, поэтому для них **PatternLayout** оборачивается в encoder. **PatternLayoutEncoder** - сразу encoder + **PatternLayout**

Шаблон содержит спецификаторы (слова начинающиеся с `%`). Спецификаторы в `{}` содержат параметры

#### 6.1.2 Ключевые слова

* **logger{length} /c /lo** - выводит имя логгера. Параметром указывается некая длина, к которой пытается сократить имя логгера.
    - напр. `%logger` - без сокращений
    - `%logger{0}` - (для `com.sub.sample.Bar`) - `Bar`
    - `%logger{5}` - `c.s.s.Bar`
    - `%logger{15}` - `c.s.sample.Bar`
* **contextName / cn** - выводит имя контекста
* **date{pattern} / date{pattern, timezone} / d** - выводит дату/время сообщения. Без указания шаблона - `yyyy-MM-dd HH:mm:ss,SSS`. Т.к. ЗПТ - разделитель параметров, нельзя просто указать `%d{ss,SSS}`. Нужно `%d{"ss,SS"}`
* **caller{depth} / caller{start..end}** - вывод стек функций, откуда вызвано событие
* **message / msg / m** - само сообщение
* **n** - разделитель линий
* **level / le / p** - уровень сообщения
* **relative / r** - количество мс от старта приложения до момента вывода
* **thread / t** - имя потока
* **exception{depth} / ex** - выводит стек исключения, связанного с событием. Если не задано, выводится полный стек. можно отменить через **%nopex** Варианты параметра:
    - `full`
    - `short` - несколько первых строк
    - число, определяющее количество выводимых строк
* **xException / xEx** - аналогично **exception**, но с выводом доп. инфо о jar'е. Требует включения `packagingData` в конфигурации. Если нет уверенности в точном определении - указывается со знаком `~`
*  **rootException / rEx** - аналог **xEx**, но в обратном порядке
* **nopexception / nopex** - отключает вывод исключений
* **marker** - вывод маркеров, связанных с сообщением

* **class{length} / C** - выводит имя класса. Параметр также как и для `%logger`. Получение имени класса затратная операция, не рекомендуется использовать
* **file / F** - имя файла java кода. Затратно, не рекомендуется    
* **line / L** - номер строки файла java кода. Затратно, не рекомендуется
* **method / M** - метод. Затратно, не рекомендуется
* **mdc** - mapped diagnostic context

#### 6.1.3 Вывод особых символов / экранирование

через символ `\`
`\%`, `\(` и т. п.

#### 6.1.4 Литералы слитно со спецификаторами

Напр. шаблон `%date%n Hello` корректный
`%date%nHello` - нет (будет считать nHello спецификатором, которого нет)
можно так `%date%n{}Hello`


#### 6.1.5 Модификаторы формата

Модификаторы вводятся между знаком `%` и именем спецификатора

Состав: 

* `-` признак левого края
* минимальная ширина (если выводится меньше символов, чем минимальная ширина - пробелы добавляются слева или справа (если есть признак `-`))
* `.` + максимальная ширина (если сообщение больше, начало обрезается)

Примеры:

`%20logger` - выравнивается по правому краю, добавляются пробелы до мин. ширины в 20
`%-20logger` - выравнивается по левому краю, добавляются пробелы до мин. ширины в 20
`%.20logger` - при превышении макс. ширины в 20 - обрезаются слева (с начала)
`%.-20logger` - при превышении макс. ширины в 20 - обрезаются справа (с конца)

Вывод уровня в одну букву: `%.-1level`

#### 6.1.6 Группировка спецификаторов

Можно вместе объединять и назначать модификаторы

`%-30(%d{HH:mm} [%t]) %-5level`

#### 6.1.7 Вывод в цвете

Использование: спецификаторЦвета(обычныйСпецификатор)
Цвета: 
* %black, %red, %green, %yellow, %blue, %magenta, %cyan, %white, %gray
* %boldRed, %boldGreen, %boldYellow, %boldBlue, %boldMagenta, %boldCyan, %boldWhite"
* %highlight

для Win включить `withJansi=true`

Пример: `%highlight(%-5level) %msg`





















### 6.2 HTMLLayout

Вывод в html-формате