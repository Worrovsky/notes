# Data Access

## 2. DAO Support

### 2.1 Иерархия исключений

Spring оборачивает все исключения (от JDBC, JPA, Hibernate) в свои собственные.
Причины:

* Исключения JDBC слишком обшие (SQLException это и ошибка доступа к базе, и ошибка SQL)
* Hibernate расширяет иерархию, но тогда будет привязка к Hibernate
* упрощение работы с исключениями, корневое **DataAccessException** является необрабатываемым, т. е. можно без блоков try-catch

### 2.2 Аннотации

Аннотация `@Repository`:

* применяется к классу    
* обеспечивает передачу исключений DataAccessException
* включает `@Component`, т. е. поддерживает сканирование

Дальнейшая настройка зависит от используемой технологии

#### JPA

Для JPA нужен объект `EntityManager`. Внедряем любым способом: @Autowired, @Resourse, @PersistenceContext и т. п.

    @Repository
    public class JpaMovieFinder {
        @PersistenceContext
        private EntityManager entityManager;
    }

#### Hibernate

Для Hibernate нужна `SessionFactory`

    @Repository
    public class HibernateMovieFinder {
        private SessionFactory factory;
        @Autowired
        public void setSessionFactory(SessionFactory factory) {
            this.factory = factory;
        }
    }

#### JDBC

Работа через `JdbcTemplate`, которому нужен объект `DataSource`. Его и внедряют

    @Repository
    public class JdbcMovieFinder {
        private JdbcTemplate jdbcTemplate;
        
        @Autowired
        public void init(DataSource dataSource) {
            this.jdbcTemplate = new JdbcTemplate(dataSource);
        }
    }


## 3. Работа с JDBC

### 3.1 Выбор подхода 

* **JdbcTemplate**
    - низкоуровневый
    - основа для остальных
* **NamedParameterJdbcTemplate** 
    - обертка на `JdbcTemplate`
    - можно работать с именованными параметрами вместо `?`
* **SimpleJdbcInsert**, **SimpleJdbcCall** 
    - упрощает работу с БД, которые предоставляют метаинформацию.
* **RDBMS объекты** (MappingSqlQuery, SqlUpdate) 

### 3.2 Структура пакетов

* **core** 
    - `org.springframework.jdbc.core` 
    - содержит JdbcTemplate
    - `org.springframework.jdbc.core.simple` содержит SimpleJdbcInsert, SimpleJdbcCall
    - `org.springframework.jdbc.core.namedparam` содержит NamedParameterJdbcTemplate
* **datasource**
    - `org.springframework.jdbc.datasource`
    - содержит DataSource и вспомогательные классы
    - `org.springframework.jdbc.datasource.embedded` - поддержка встроенных БД типа HSQL, H2, Derby
* **object** 
    - `org.springframework.jdbc.object`
    - классы для работы с RDBMS
* **support**
    - `org.springframework.jdbc.support`
    - классы для передачи SQLException
    - другие вспомогательные


### 3.3 Основы работы с JDBC

#### 3.3.1 Использование JdbcTemplate

##### Введение

Обязанности JdbcTemplate:

* выполнение SQL-запросов
* выполнение обхода по ResultSet и извлечение параметров
* перехват низкоуровневых исключений, передача их в более информативные

`DataSource` обязательно должен быть бином в составе контейнера (почему? нельзя программно создать?). Внедрить в JdbcTemplate можно любым способом (в т.ч. не средствами Spring)

Основан на паттерне шаблонный метод. JdbcTemplate отвечает за подготовку ресурсов, транзакции, освобождение ресурсов. Остается только реализовать "обратные вызовы" (напр. предоставить SQL запрос, обработать результат)

Логгирование связанное с SQL происходит на уровне **DEBUG**

##### Запросы SELECT

Запрос с возвращаемым значением с указанием типа

    int count = jdbcTemplate.queryForObject("select count(*) from t1", Integer.class);

Запрос с возвращаемым значением и параметрами запроса

    int count = jT.queryForObject("select count(*) from t1 where id = ?", Integer.class, Long.valueOf(12L));

Сигнатуры методов:

    queryForObject(String sql, Class<T> type, Object... args);
    queryForObject(String sql, Object[] args, Class<T> type);

###### Запросы через RowMapper

* **RowMapper** - интерфейс

метод 
    
    T mapRow(ResultSet rs, int rowNum) throws SQLException

реализация не должна итерироваться по ResultSet, просто уже на позиции

Получение одного объекта

    User user = jt.queryForObject(
        "select name from t1 where id = ?",
        new Object[] {123L},
        new RowMapper<User>() {
            public User mapRow(ResultSet rs, int rowNum) {
                String name = rs.getString("name");
                User u = new User(name);
                return u;
            }
        });

Получение списка объектов так же

    List<User> users = jt.queryForObject(
        "select name from t1",
        new RowMapper<User>() {
            public User mapRow(ResultSet rs, int rowNum) {
                String name = rs.getString("name");
                User u = new User(name);
                return u;
            }
        });


##### Запросы изменения (INSERT, UPDATE, DELETE)

выполняются через метод `update(String sql, Object ... params)` или `update(String sql, Object[] params)`

    jt.update("insert into t1 (name) values (?)", "Bill");


##### JdbcTemplate best practices

* Обычно используется один экземпляр JdbcTemplate для работы с несколькими реализациями DAO (репозиториями)
    - JdbcTemplate thread-safe после того, как сконфигурирован
* заметки по **@Repository**:
    - является @Component, т.е. участвует в сканировании
    - часто им обозначают реализации DAO интерфейса
    - дает доступ к передаче исключений `DataAccessException`
* Обычно делают так:
    - конфигурируют DataSource как бин
    - внедряют его во все реализации DAO
    - создают JdbcTemplate в методе установки DataSource

Например так:

    @Repository
    public class JdbcUserDao implements UserDao {
        private JdbcTemplate jdbcTemplate;
        
        @Autowired
        public void setDataSource(DataSource dataSource) {
            this.jdbcTemplate = new JdbcTemplate(datasource);
        }
        ...
    }


* Есть вспомогательный класс `JdbcDaoSupport`, в нем можно настроить setDataSource() и наследовать этот класс в конкретных реализациях DAO

#### 3.3.2 NamedParameterJdbcTemplate

* обертка над JdbcTemplate
* позволяет работать с именованными параметрами в дополнение к стандартному `?`
* создается и настраивается как обычный JdbcTemplate
* всегда можно получить доступ к JdbcTemplate через метод `getJdbcOperations()` и работать напрямую

Основная схема работы:

* строка SQL указывается с именованными параметрами (через `:`)
* в метод передаются эти параметры одним из способов:
    - через объект **SqlParameterSource**
    - через Map
    - через **BeanPropertySqlParameterSource**


Способ через **SqlParameterSource**

    String sql = "select count(*) from user where id = :id";
    SqlParameterSource namedParams = new MapSqlParameterSource("id", 3L);
    int i = namedParamJT.queryForObject(sql, namedParams, Integer.class);

Способ через **Map**

    String sql = "select count(*) from user where id = :id";
    Map<String, Long> namedParams = new HashMap<>();
    namedParams.put("id", 3L);
    int i = namedParamJT.queryForObject(sql, namedParams, Integer.class);

Способ через **BeanPropertySqlParameterSource**

Нужен объект-bean

    class User {
        private long id;
        private String name;
        ...
    }

    User exampleUser = new User(2L, "Bob");
    String sql = "select count(*) from user where id = :id and name = :name";
    SqlParameterSource namedParams = new BeanPropertySqlParameterSource(exampleUser);
    int i = namedParamJT.queryForObject(sql, namedParams, Integer.class);



### 3.4 Соединение с БД

#### 3.4.1 Использование DataSource

`DataSource` - часть спецификации JDBC (javax.sql.DataSource)
обеспечивает подключение к БД
обертка над `Connection`, через `dataSource.getConnection()` можно получить объект `Connection` и работать с ним напрямую

Можно получать:
    
* через JNDI
* через сторонние библиотеки с поддержкой пула соединений (Apache Jakarta DBCP)
* через Spring`вый DriverManagerDataSource (**на каждый запрос - свое соединение, только для теста**)
* через Spring`вый SingleConnectionDataSource (одно соединение для всех запросов)

Конфигурирование:

* Создать объект DriverManagerDataSource
* указать полное имя класса драйвера для его загрузки
* указать URL (зависит от драйвера)
* логин / пароль для доступа

Пример программной конфигурации:

    DriverManagerDataSource ds = new DriverManagerDataSource();
    ds.setDriverClassName("org.hsqldb.jdbcDriver");
    ds.setUrl("jdbc:hsqldb:hsql://localhost:");
    ds.setUsername("sa");
    ds.setPassword("");

То же самое через xml с чтением настроек из файла `jdbc.properties`:

    <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="${jdbc.driverClassName}"/>
        <property name="url" value="${jdbc.url}"/>
        <property name="username" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>
    
    <context:property-placeholder location="jdbc.properties"/>



### 3.9 Встроенные БД

* Используются для тестирования / отладки


#### 3.9.3 Программное создание

Через EmbeddedDatabaseBuilder

    @Configuration
    public class DBConfig {
        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                    .generateUniqueName(true)
                    .setType(H2)
                    .setScriptEncoding("UTF-8")
                    .ignoreFailedDrops(true)
                    .addScript("schema.sql")
                    .addScripts("user_data.sql", "country_data.sql")
                    .build();
        }
    }

#### 3.9.6 Генерация имен БД

* по умолчанию при создании базе дается имя типа `testdb`
* при попытке создать еще одну базу (напр. во время тестирования, в рамках одного процесса JVM) получаем конфликт
* чтобы избежать включается генерация уникальных имен через:
    - `EmbeddedDatabaseBuilder.generateUniqueName(true);`
    

#### 3.9.10 Инициализация DataSource

* выполняется через запуск скриптов:
    - создание таблиц
    - заполнение данными
* проблемы могут быть при повторном запуске
    - если таблицы уже есть, их можно удалить (drop)
    - если синтаксис поддерживает `DROP .. IF EXISTS` - хорошо, иначе ошибки
* можно игнорировать такие ошибки: `EmbeddedDatabaseBuilder.ignoreFailedDrops`
* можно игнорировать все: `EmbeddedDatabaseBuilder.continueOnError(true)`