Ch 4. API

    public class HellWorld{
        private static final Logger logger = LogManager.getLogger("HellWorld");

        public static void main(String[] args) {
            logger.info("Hello, world"); // куда выводить - определяется конфигурацией
        }
    }

    нет необходимости if (logger.isDebugEnable()), при вызове .info() и подобным внутри проверка

    подстановка параметров ({}-style parameters):
        logger.debug("Some param = {}, param №2 = {}", param1, param2)


    форматирование параметров:
        такой же механизм как в стандартном Formatter
        создавать logger через LogManager.getFormatterLogger("Foo");
        logger.debug("Integer.MAX_VALUE = %,d", Integer.MAX_VALUE);


    Комбинация стандартных параметров ({}-style) с форматированием:
        ф-я printf
        Logger logger = LogManager.getLogger("Foo");
        logger.printf(Level.INFO, "Log of %1$s", name);

    Использование лямбда и ленивые вычисления:
        для дорогих операций без лямбд нужна проверка:
        if (logger.isTraceEnable()) {
            logger.trace("Long-running operation return {}", expensiveOper());
        }

        с лямбдами - не нужна:
            logger.trace("Long-running operation return {}", () -> expensiveOper());

    
    Имена для logger
        рекомендация - полное имя класса (org.apache.test.MyTest)
        все варианты сделают так:   
            public class MyTest {
                LogManager.getLogger();
                LogManager.getLogger(MyTest.class);
                LogManager.getLogger(MyTest.class.getName());
            }

Ch 5. Configuration

    способы:
        - через конфигурационные файлы (XML, JSON, YAML, properties-файлы)
        - через ConfigurationFactory и реализацию интерфейса Configuration
        - через добавление компонентов в конфигурацию по умолчанию, путем вызова API из Configuration
        - через методы класса Logger
    
    поиск настроек:
        - системное свойство(устанавливается при запуске ВМ Dlog4j.configurationFile=path/to/log4j2.xml) "log4j.configurationFile" если есть - из него файл и через ConfigurationFactory
        - поиск "log4j2-test.properties" в classpath
        - "log4j2-test.yaml" 
        - "log4j2-test.json" 
        - "log4j2-test.xml" 
        - "log4j2.properties" 
        - "log4j2.yaml" 
        - "log4j2.json" 
        - "log4j2.xml" 
        - DefaultConfiguration (в консоль)

    Автоконфигурация 
        при изменении файла - автоконфигурируется
        можно указать интервал (в сек): реконфигурация произойдет только после 
            истечения этого интервала (от последнего обращения к функциям loggera)

        <Configuration monitorInterval="30">
        ...
        </Configuration>

    Конфигурации (атрибуты):
        advertiser          опционально
        dest                куда направлять вывод ("err" или file path или url)
        monitorInterval     время между проверками состояния в сек, мин - 5
        name                имя конфигурации
        packages            список плагинов
        schema              
        shutdownHook        будет ли автоматически остановлен log4j при остановке jvm (истина по умолчанию)
        shutdownTimeout     время после остановки jvm сколько будет работать
        status              уровень событий самого log4j для отображения в консоль
                                ("trace", "debug", "info", "warm", "error", "fatal")

    Пример:
        <Configuration status="trace", monitorInterval=30>
            ...
        </Configuration>

    Конфигурация логгеров:
        <logger name="MyName", level="trace", additivity="true">
            ...
        </logger>

        атрибут name обязателен
        уровень
            варианты: trace, debug, info, warn, error, all, off
            если не указан: error

        содержит фильтры (элемент ThreadContextMapFilter) и аппендеры (AppenderRef)
        несколько аппендеров возможны, тогда при наступлении события (вызов logger.info() напр.),
            оно будет отправлено в несколько приемников

        Каждая конфигурация должна содержать корневой логгер
            корневой логгер не имеет имени
            не поддерживает свойство additivity (нет родителей)

            пример:
                <Configuration>
                    ...
                    <Loggers>
                        <Root level="info">
                            <AppenderRef ref=""/>
                        </Root>
                        // другие логгеры
                    </Loggers>
                </Configuration>


    Конфигурация аппендеров:
        два варианта:
            - через предопределенное имя аппендера (Console, File)
            - через элемент Appender и атрибут type

        каждый аппендер должен иметь уникальное имя для ссылки в блоке логгеров

        аппендер содержит layout
        layout также можно через предопределенные имена (PatternLayout) или элемент Layout

    пример:
        <Configuration status="debug" name="MyConf">
            <Properties>
                <Property name="filename">users/logs/test.log</Property>
            </Properties>
            <Appenders>
                <Console name="STDOUT">
                    <PatternLayout pattern="%m%n"/>
                </Console>
                <File name="File" filename="${filename}">
                    <PatternLayout>
                        <pattern>%m%n</pattern>
                    </PatternLayout>
                </File>
            </Appenders>
            <Loggers>
                <Logger name="test1" level="debug">
                    <AppenderRef ref="STDOUT">
                    <AppenderRef ref="File" level="error">
                </Logger>
                <Root level="trace">
                </Root>
            </Loggers>
        </Configuration>


Конфигурирование через xml-файл
    
    имя файла log4j2.xml
    расположен в classpath

    Корневой элемент Configuration
        атрибуты 
            name
            status - уровень логгирования самого log4j
            monitorInterval - интервал проверки изменения конфигурации (проверка файлов)

        Общая структура
            Configuration
                Properties
                Filter
                Appenders
                    Appender
                        Filter
                Loggers
                    Logger
                        Filter
                    Root

    Логгеры
        атрибуты:
            name
            level (error по умолчанию)
            additivity
        каждая конфигурация имеет корневой логгер, даже если не указан явно
        может иметь ссылку на Appender (AppenderRef)


        
Архитектура
    
    https://logging.apache.org/log4j/2.x/manual/architecture.html

    Иерархия логгеров
        логгеры образуют иерархию через имена
            напр. логгер с именем "com.foo.Bar" дочерний по отношению 
            к логгеру с именем "com.foo"
        корневой логгер всегда присутствует и является родителем для всех других
        получить можно
            LogManager.getRootLogger();
            LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

    LoggerContext
        ключевая точка для всей системы логгирования
        в некоторых случаях может быть несколько

    Configuration
        каждый LoggerContext содержит активный Configuration
        Configuration включает в себя логгеры, фильтры, аппендеры
        во время реконфигурации существуют два объекта Configuration: новый и старый

    Logger
        создается через LogManager.getLogger(String name)
        сам по себе не выполняет действий 
        ассоциирован с объектом LoggerConfig
        при изменении конфигурации связывается с другим объектом LoggerConfig
        целиком определяется именем: вызовы LogManager.getLogger(name) на 
            одном значении name будут давать один и тот же объект
        обычно имя логгера = имя класса, в котором он создается (LogManager.getLogger())

    LoggerConfig
        создается при создании логгера
        содержит набор Filter и набор Appender
        событие LogEvent прежде чем попасть на Appender, проходит через Filter

        каждой конфигурации присваивается уровень (TRACE, DEBUG, INFO, WARN, ERROR, FATAL)
            каждое событие LogEvent также имеет уровень
            от комбинации уровней зависит будет ли событие обрабатываться конкретной конфигурацией


            если логгеру не назначена конфигурация, он наследует от родительской конфигурации

    Filter
        может применяться
            перед любым LoggerConfig
            после LoggerConfig, но перед любым Appender
            после LoggerConfig, но перед конкретным Appender
            на каждый Appender
        3 состояния-результата фильтра:
            Accept - событие допущено, больше фильтры вызывать не надо
            Deny - событие отклонено
            Neutral - событие допущено этим фильтром, будут применены другие фильтры

    Appender
        определяет куда будет выводится лог (консоль, файл, сокет, сервер, БД)
        к одному Logger может присоединяться несколько

        событие LogEvent будет передано всем аппендерам текущего логгера, а также 
            всем аппендерам родительских логгеров
            напр. если rootLogger связан с консолью, тогда 
        за это отвечает свойство логгера additivity 
        если additivity=false, тогда событие будет обработано текущим логерром, 
            но не будет передано родителям

    Layout
        настройка формата выводимых сообщений
        привязан к Appender
        часто используется PatternLayout







https://www.tutorialspoint.com/log4j
    
    архитектура:
        Основные объекты
            Logger                  основной объект
            Layout                  форматирование
            Appender                вывод на конечное устройство (файл, ДБ и т. п.)
        Вспомогательные:
            Level
            Filter                  анализ выводить/не выводить
            ObjectRenderer          строковое представление объектов
            LogManager              управление

    конфигурация:
        через файл log4j.properties

            // # определение корневого логгера и appendera X
            log4j.rootLogger = DEBUG, X
            // # appender X - файловый
            log4j.appender.X=org.apache.log4j.FileAppender
            log4j.appender.X.File=${logs}log.out // сохранять в папку logs в файл log.out
            // # определение layout
            log4j.appender.X.layout=org.apache.log4j.PatternLayout
            log4j.appender.X.layout.conversionPatterb=%m%n // каждое сообщение - с новый строки

    уровни сообщений:
        TRACE, DEBUG, INFO, WARN, ERROR, FATAL, ALL

    appenders
        свойства:
            layout      форматирование выводимой информации
            target      файл, консоль, ...
            level       фильтрация сообщений
            threshold   пороговый уровень, ниже которого игнорируются все сообщения, не зависит от level
            filter      после проверки level, дополнительная фильтрация

        подключение:
            log4j.logger.[имя логгера]=level, appender1, appender..n

            через xml:
                <logger name="com.apress.logging.log4j" additivity=false>
                    <appender-ref ref="appender1"/>
                    <appender-ref ref="appender2"/>
                </logger>

            можно программно

        Виды:
            ConsoleAppender
            JDBCAppender
            FileAppender и др.

    layouts
        PatternLayout
        DateLayout
        HTMLLayout
        SimpleLayout
        XMLLayout