
# Разное

## Вопросы при разработке

Каким способом получить SessionFactory
есть объект DAO, что нужно для связи с Hibernate (конструкторы, сеттеры, геттеры)
id авто генерация - сквозная на все таблицы. Как свои на каждую?

## Способы запуска

* через файл hibernate.cfg.xml
* программно 
    - создаем **ServiceRegistry** путем задания настроек через **StandardServiceRegistryBuilder**
        + `applySetting()`
        + или `loadProperties()`
    - добавить классы-источники метаданных через **MetadataSources**
        + `addAnnotatedClass()` - через аннотации
        + `addFile()` - через файлы-маппинги hbm.xml
        + `addJar()` - через файлы hbm.xml из jar
    - создать **SessionFactory** 

### Полный пример программного создания

     // 1. Задаем настройки подключения через объект StandardServiceRegistryBuilder 
    
    StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
    
    registryBuilder.applySetting("hibernate.dialect", "org.hibernate.dialect.SQLiteDialect");
    registryBuilder.applySetting("hibernate.connection.driver_class", "org.sqlite.JDBC");
    registryBuilder.applySetting("hibernate.show_sql", "true");
    registryBuilder.applySetting("hibernate.connection.url", "jdbc:sqlite:myDB.db");
    registryBuilder.applySetting("hibernate.hbm2ddl.auto", "create-drop");
    
    // 2. на основе настроек подключения создаем объект ServiceRegistry для задания сущностей
    ServiceRegistry serviceRegistry = registryBuilder.build();
    MetadataSources sources = new MetadataSources(serviceRegistry);
    sources.addAnnotatedClass(User.class);
    
    // 3. получаем фабрику
    Metadata metadata = sources.getMetadataBuilder().build();
    SessionFactory sessionFactory = metadata.buildSessionFactory();
    
    // 4. получаем сессию, получаем транзакцию, сохраняем данные
    
    Session session = sessionFactory.openSession();
    Transaction tx = session.getTransaction();
    tx.begin();
    session.save(new User("Bob", 1L));
    tx.commit();
    session.close();

Здесь `User` - POJO с аннотацией `@Entity`

# Documentation 5.4.7

## 1. Архитектура

### 1.1 Обзор

Hibernate как реализация спецификации JPA

**SessionFactory** - потокобезопасное, иммутабельное представление отражения модели на БД. **EntityManagerFactory** - прямой эквивалент из JPA. Дорогой для создания, должен быть в одном экземпляре на одну БД.

**Session** - однопоточный, короткоживущий объект для выполнения работы. В JPA соответствует **EntityManager**. Внутри обертка над JDBC Connection

**Transaction** - однопоточный, короткоживущий объект, реализует механизм транзакций

## 2. Domain model (предметная модель)

### 2.1 Mapping types

Hibernate типы определяют возможность чтения / записи в БД, плюс другие аспекты типа проверки на идентичность, клонирования и т. п. Это реализация интерфейса `org.hibernate.type.Type`

Делятся на 2 группы

* Value types
* Entity types

#### 2.1.1 Values types (типы-значения) 

Данные, не имеющие жизненного цикла. Подчинены entity, которые имеют жизненный цикл

С другой стороны любое состояние entity это набор value types. Это свойства JavaBean или persistent attributes.

Делятся на:

* базовые
* встаиваемые (embeddable) 
* коллекции

#### 2.1.2 Entity types (типы-сущности)

Классы модели, соответствуют строкам таблицы базы данных. Имеют уникальный идентификатор и собственный жизненный цикл

#### (4.1.2 JPA-Hibernate)

Экземпляр сущности можно извечь, имеет хранимую идентичность. Ссылка на экземпляр сущности сохраняется как ссылка в БД. Не зависит от других сущностей (независимый жизненный цикл). В объектой модели это классы.

Экземпляр типа-значения принадлежит сущности, нет хранимой идентичности. Время жизни определяется владельцем. В объектой модели это примитивные свойства классов или ссылочные свойства классов (напр. есть класс Address у User, но в БД одна таблица USER, где адрес хранится в отдельных колонках)

Соответствия в JPA: basic property type, embeddable class


### 2.3 Basic types

#### 2.3.1 Типы от Hibernate

Базовые типы отображают одну колонку таблицы в неагрегированный Java-тип. Hibernate имеет свой набор встроенных типов

StringType      String
TextType        String
CharacterType   Character
BooleanType     boolean
YesNoType       boolean
LongType        long
LocalTimeType   LocalTime
и др.

#### 2.3.2 Аннотация @Basic

javax.persistence.Basic 
подразумевается по умолчанию, можно не указывать

JPA ограничивает типы, которые могут быть помечены `@Basic`:

* примитивные и их обертки
* String
* BigInteger, BigDecimal
* java.util.Date, java.util.Calendar
* java.sql.Date, Time, Timestamp
* byte[], char[]
* Serializable

Имеет атрибуты:

**optional** (true по умолчанию) - допускаются ли null
**fetch** - ленивый или нет запрос. Hibernate игнорирует

#### 2.3.3 Аннотация @Column

Явно указывает имя колонки. Если не указано - в соответствии с NamingPolicy: имя атрибута обычно

Пример:

    @Entity
    public class Product {
        @Id
        private int id;
    
        @Colunm(name="NOTES")
        private String desc;
    }

#### 2.3.4 Как определяются типы свойств по умолчанию

Почему напр. свойству типа int будет соответствовать `org.hibernate.type.IntegerType`

За это отвечает `org.hibernate.type.BasicTypeRegistry`

Типа есть соответствие между java-типом и hibernate-типов

#### 2.3.5 Явное указание типа

Аннотация **@org.hibernate.annotations.Type**

    ...
    org.hibernate.annotations.Type( type = "materialized_nclob" )
    private String description;
    ...

В качестве типа передается:

* полное имя имплементации org.hibernate.type.Type
* ключ типа из `BasicTypeRegistry`

#### 2.3.6 Пользовательские BasicTypes

#### 2.3.7 Сохранение перечислений

Вариант 1: через аннотацию **@Enumerated** (из JPA)

Стратегия определяется указанием javax.Persistence.EnumType:

* ORDINAL - в БД сохраняется позиция значения (число)
* STRING - в БД сохраняется строковое представление

Пример

    @Enumerated(EnumType.ORDINAL)
    private PhoneType phoneType;
    
    @Enumerated(EnumType.STRING)
    private PhoneType phoneType;
    

Вариант 2: AttributeConverter

#### 2.3.15 Отображение дат, времени

стандартный SQL определяет следующие типы:

* DATE (JDBC эквивалент - java.sql.Date)
* TIME (java.sql.Time)
* TIMESTAMP (java.sql.Timestamp)

Можно чтобы не зависеть от java.sql использовать java.util или java.time.

Для свойств типа java.sql Hibernate может определить тип, для java.util / java.time явно указывать через **@Temporal** (TemporalType.DATE/TIME/TIMESTAMP)

    @Temporal(TemporalType.DATE)
    private Date date;

Для Date,Time API из Java 8 есть однозначное соответствие между типами, использование @Temporal ведет к исключению.

Соответвтвие типов:

* DATE - java.time.LocalDate
* TIME - java.time.LocalTime, java.time.OffsetTime
* TIMESTAMP - java.time.Instant, java.time.LocalDateTime и др.

#### 2.3.17 Экранирование идентификаторов

Если идентификатор (напр. имя колонки) совпадает с ключевым словом, нужно как-то экранировать

2 варианта:

* через `'` (стиль Hibernate) `@Column(name = "'name'")`
* через `\"` (стиль JPA) `@Column(name = "\"name\"")`

#### 2.3.18 Генерируемые свойства

свойства, которые изменяет сам Hibernate
аннотация **@Generated** с вариантами (GenerationTime):
* NEVER (по умолчанию) - никогда не генерируется
* INSERT - генерируется при insert, не изменяется при update
* ALWAYS - генерируется при insert и изменяется при update

Помечать можно только не итерируемые свойства. Только те, что помечены @Version и @Basic типы

    @Generated(value = GenerationTime.ALWAYS)
    @Column(columnDefinition = "AS CONCAT (COALESCE(firstName ...))")
    private String fullName;


##### Аннотация @GeneratorType

Используется для указания пользовательского генератора значений

Нужно реализовать интерфейс **ValueGenerator<T>** (метод интерфейса просто возвращает значение) и указать реализацию в аннотации

    @GeneratorType( type = ValueGeneratorImpl, when = GeneratorTime.ALWAYS)
    private String updatedBy;


##### Аннотация @CreationTimestamp

заполняет временем создания 

поддерживает типы:

* java.util.Date
* java.util.Calendar
* java.sql.Date
* java.sql.Time
* java.sql.Timestamp

Пример:

    @Column(name = "'timestamp'")
    @CreationTimestamp
    private Date timestamp;

##### Аннотация @UpdateTimestamp

аналогично @CreationTimestamp, на каждое изменение



### 2.4 Embeddable (встраиваемые) типы

Это объединение типов. Напр. класс Publisher как композиция name и country
В терминах Hibernate - components (компоненты), JPA - embeddables

#### 2.4.1 Component / Embedded

Классы-компоненты отображаются в ту же таблицу, что и родительский класс

    @Entity(name="Book")
    public class Book{
        @Id
        @GeneratedValue
        private long id;
    
        private Publisher publisher;
    }
    
    @Embeddable
    public class Publisher {
        @Column(name = "publisher_name")
        private String name;
    }

Здесь будет одна таблица "Book" с колонками, относящимися к Publisher.
Можно было указать просто свойства в классе `Book` без выделения отдельного класса, и тот же результат для БД, но это менее ООП подход 

#### 2.4.2 - 2.4.4 Конфликты имен при множественном использовании одного компонента

Напр. класс Book содержит две ссылки на Publisher. Нужны дополнительные указания для связи колонок

Вариант 1 (JPA): аннотации **@AttributeOverride** и **@AssotiationOverride**
Вариант 2 (Hibernate): через `ImplicitNamingStrategyComponentPathImpl` 

#### 2.4.8 Аннотация @Target

Если свойство (встраиваемое) задается интерфейсом, нужно указать реализацию этого интерфейса. Для этого используется **@Target**

    public interface Coordinates {
        double x();
        double y();
    }
    
    @Embeddable
    public class GPS implements Coordinates {...}
    
    @Entity
    public class City {
        @Embedded
        @Target(GPS.class)
        private Coordinates coords;
    }

#### Аннотация @Parent

Когда нужно внутри встроенного класса обратиться к родителю

    @Embeddable
    public class GPS {
        @Parent
        private City city;
    }




### 2.5 Entity

#### 2.5.1 POJO

JPA накладывает следующие ограничения:

* класс сущности должен быть помечен javax.persistence.Entity
* должен иметь публичный или protected конструктор без аргументов (другие конструкторы также могут быть) 
* класс должен находится на верхнем уровне (не вложенный)
* не интерфейс, не перечисление
* не должен быть финальным. Поля, методы не должны быть финальными
* в случае удаленного использования (???) должен реализовывать Serializable
* и абстрактные, и не абстрактные классы могут быть сущностями. Сущности могут наследовать не сущности и наоборот
* свойства должны иметь геттеры/сеттеры

Hibernate чуть изменяет:

*  должен иметь конструктор публичный, protected или default
*  класс не обязательно на верхнем уровне
*  может работать с final, но не рекомендуется (проксирование / ленивая загрузка)
* геттеры/сеттеры не обязательны. Если они есть, Hibernate может работать и с private. Но для проксирования - минимум пакетная видимость

#### 2.5.5 Атрибут идентификатор

Хотя сейчас идентификатор опционален, рекомендуется всегда явно указывать (в будущем может стать обязательным)

Не обязательно должен соответствовать первичному ключу. Может на колонку, которая однозначно идентифицирует строку таблицы

Рекомендуется не использовать примитивные типы, лучше обертки `Long, Integer`

    @Id
    private Long id;

#### 2.5.6 @Entity

атрибут **name** - по умолчанию имя класса. Используется в JPQL запросах
по умолчанию таблице в БД также будет дано имя name
@Table (java.persistence.Table) - явно задать имя таблицы

    @Entity(name="Book")
    @Table(name="book")
    public class Book {...}

Если БД поддерживает схемы, каталоги можно и их указывать в @Table

#### 2.5.7 hashCode(), equals() для классов-сущностей

Проблема: если запись извлекается из БД, каждый раз это должен быть один и тот же объект по смыслу.

В рамках одной сессии (Session) Hibernate гарантирует это на уровне == объектов

Для разных сессий `Object1 != Object2`, но логично обеспечить `Object1.equals(Object2)`. Не реализованный `equals()` наследуется от `Object` и сравнивает по ссылке.

Поэтому если сущности используются из разных сессий, особенно в коллекциях (Set, Map) нужно реализовывать `hashCode()` / `equals()`

Первый подход - сравнение и хеш на основе идентификатора (Id). Но здесь проблема при создании новых объектов: пока коммит в БД не прошел, id не будет заполнен. Для коллекций типа Set недопустимо изменение хеша.

Второй подход: это ввод "натурального" идентификатора, который не связан с ключом БД и построение hashCode() / equals() на его основе

    @Entity
    public class Book {
        @Id
        private Long id;
        
        @NaturalId
        private String isbn;
    }

Если нельзя ввести натуральный идентификатор, придется вводить константу для хеша (чтобы не менялся для коллекций Set) и не сравнивать незаписанные сущности


#### 2.5.8 Отображение сущности на запрос

Когда в БД нет явной таблицы, а запись собирается запросом из других таблиц

    @Entity(name= "AccountSummary")
    @Subselect("select client.id, bids.sum ...")
    public class AccountSummary { ... }

#### 2.5.12 Стратегии доступа

Две стратегии: доступ по полям (instance fields) и по методам доступа (instance properties)

Определяется позицией аннотации @Id: если над полем - по полям, если над геттером - по методам доступа. 

Встраиваемые типы наследуют метод доступа от своих владельцев

Если выбрана стратегия по методам доступа, эти методы должны быть объявлены для всех полей, сохраняемых в БД

аннотация **@Transient** помечает поля / методы, которые не нужно сохранять

аннотация **@Access** позволяет изменить метод для отдельного поля/метода

    @Entity
    public class Book {
        private Long id;
        
        @Id
        public Long getId() {...}
        
        @Version
        @Access(AccessType.FIELD)
        private int version;
    }

также можно сменить стратегию для встраиваемого класса
    
    @Embeddable
    @Access(AccessType.PROPERTY)



### 2.6 Идентификаторы

#### Разные замечания из других источников

Если в классе есть поле с аннотацией `@Id`, Hibernate будет сохранять все поля класса в БД

К полю `@Id` Hibernate способен обращаться напрямую без геттеров, сеттеров. Геттер обычно делают (для внутренней логики приложения), сеттер - нет (Hibernate не меняет первичный ключ, а приложению нет смысла)

Лучше ввести суррогатный первичный ключ, чем использовать натуральный ключ

Автогенерация: `@GeneratedValue(strategy = ...)`
Варианты: 
    
* AUTO (сам выберет что-то из следущего (на опыте - это SEQUENCE))
* SEQUENCE (на механизме БД, если нет поддержки - доп. таблица)
* TABLE (доп. таблица со следующими значениями ключа для каждой таблицы)
* IDENTITY (создает в таблице БД поле с автогенерацией при вставке)
    
TABLE (и SEQUENCE) означает дополнительный select перед вставкой (для определения ID). 
TABLE - свой идентификатор на каждую таблицу
SEQUENCE - общий для нескольких таблиц

##### Именованные генераторы

Это дополнительные настройки. Указывается так: 

    @GeneratedValue(generator = "<ID_GENERATOR>")

Сам генератор настраивается через аннотации:

* `@javax.pesristence.SequenceGenerator`
* `@javax.pesristence.TableGenerator`
* `@org.hibernate.annotations.GenericGenerator`



#### 2.6.0 

Некий аналог первичного ключа, для уникального обозначения каждой сущности

Соответствующая колонка БД:
* UNIQUE
* NOT NULL
* IMMUTABLE - Hibernate никаким образом не разрешает изменений. Если что-то нужно - разделяем на суррогатный и натуральный

#### 2.6.1 Простые идентификаторы

обозначаются аннотацией **javax.persistence.Id**

JPA разрешает только следующие типы в качестве идентификаторов:

* примитивные и их обертки
* String
* java.util.Date
* java.sql.Date
* BigDecimal, BigInteger

Простые идентификаторы могут генерироваться автоматически 
**javax.persistance.GeneratedValue**

#### 2.6.2 Составные идентификаторы

Содержат несколько свойств сущности

Правила (по JPA):

* составной идентификатор представляется **primary key class**. Этот класс обозначается аннотациями **javax.persistence.EmbeddedId** или **javax.persistence.IdClass**
* класс должен быть публичным и иметь конструктор по умолчанию
* должен быть Serializable
* должны быть реализованы hashCode() и equals()

Hibernate позволяет создавать составной идентификатор просто несколькими @Id, без EmbeddedId и IdClass

#### 2.6.3 Составной идентификатор через @EmbeddedId

Создаем встраиваемый компонент (@Embeddable) и помечаем его @EmbeddedId

    @Entity
    public class SytemUser {
        @EmbeddedId
        private PK pk;
        ...
    }
    
    @Embeddable
    public class PK {
        private String sybsystem;
        private String name;
        ...
    }

#### 2.6.4 Составной идентификатор через @IdClass

    @Entity
    @IdClass(PK.class)
    public class SystemUser {
        @Id
        private String subsystem;
        @Id
        private String username;
        public PK getId() { return new PK(subsystem, username);}
        public void setId() {...}
    }
    
    public class PK implements Serializable {
        private String subsystem;
        private String username;
        ...        
    }


#### 2.6.7 Генерация значений идентификатора

можно не только идентификаторов

JPA поддерживает автогенерацию только для int, Hibernate может больше

аннотация **javax.persistence.GeneratedValue**

каким образом задается через **javax.persistence.GenerationType**:

* **AUTO** (по умолчанию) 
* **IDENTITY**
* **SEQUENCE**
* **TABLE**

#### 2.6.8 Генерация AUTO

По JPA в этом случае стратегия определяется провайдером (т. е. Hibernate)

По умолчанию Hibernate смотрит на тип.
Если это UUID - своя стратегия
Если численные типы - используется `IdGeneratorStrategyInterpreter` в одной из реализаций:

* **FallbackInterpreter** - начиная с 5.0. Если БД поддерживает sequences(??), тогда используется SEQUENCE генератор, иначе TABLE
* **LegacyFallbackInterpreter** - до 5.0

#### 2.6.9 SEQUENCE

Есть промежуточный генератор `SequenceStyleGenerator`. Способен работать с БД, не поддерживающими последовательности.

    @Entity
    class Product {
        @Id
        @GeneratedValue( strategy = GenerationType.SEQUENCE)
        ...
    }

можно дополнительно указывать генератор через **@SequenceGenerator**


#### 2.6.10 IDENTITY

На основе идентификаторов, сгенерированных БД при выполнении `INSERT`
В зависимости от особенностей БД, либо сразу команда INSERT возвращает какое-то значение, либо дополнительно выполняются SELECT-запросы

Естественно, идентификатор становется известным только после физической ставки строки в таблицу

В этом режиме Hibernate не способен выполнять пакетные команды `INSERT`


#### 2.6.11 TABLE 

#### 2.6.12 UUID генераторы

По умолчанию - случайные гуиды формирует

Можно указать дополнительно @GenericGenerator

#### 2.6.13 Оптимизаторы


### 2.7 Ассоциации

#### 2.7.1 @ManyToOne

Читается в прямом порядке: ClassA ManyToOne field type ClassB

Простое соответствие, аналог внешнему ключу (foreign key)

    @Entity
    class Person {
        @Id @GeneratedValue
        private Long id;
        ...
    }
    
    @Entity
    class Phone {
        @Id @GeneratedValue
        private Long id;
    
        private String number;
    
        @ManyToOne
        @JoinColumn(name="person_id", foreignKey = @ForeignKey(name = "P_FK"))
        private Person person;
    }

**@JoinColunm** не обязательна, определяет имя для колонки внешнего ключа
**foreignKey** - определяет ограничения на колонку, если не указан, определяется провайдером

Варианты, как сделать зависимое поле обязательным (запретить null):

* `@ManyToOne(optional = false)`
* `@JoinColumn(name = "...", nullable = false)`
* `@NotNull`

#### 2.7.2 @OneToMany

Связывает родительскую сущность с дочерними. Если на дочерней стороне есть зеркальная **@ManyToOne** - это двунаправленная ассоциация, иначе - однонаправленная

##### Однонаправленная @OneToMany

Создается вспомогательная таблица 

    @Entity
    class Person {
        @Id 
        private Long id;
        @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Phone> phones = new  ...;
    }
    
    @Entity 
    class Phone{
        private Long id;
        private String number;
    }

Соответствующий sql:

    CREATE TABLE Person (id BIGINT, PRIMARY KEY(id))
    CREATE TABLE Phone (id BIGINT, number VARCHAR, PRIMARY KEY(id))
    CREATE TABLE Person_Phone(Person_id BIGINT, phone_id BIGINT)//внешние ключи

Атрибут **orphanRemoval** означает удалять ли сущность при удалении из таблицы связей

##### Двунаправленная @OneToMany

Реализуется как обычный @ManyToOne через внешний ключ

на стороне родителя отмечается через **mappedBy**

Также нужно следить, чтобы обе стороны работали синхронно. Напр. удаляем номер: 
* удалить его из списка в `Person`
* очистить полe (установить в null) `person` в классе `Phone`

    @Entity
    class Person {
        @Id private Long id;
        @OneToMany(mappedBy="person", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Phone> phones = new  ...;
    }    
    
    @Entity 
    class Phone{
        private Long id;
        private String number;
        @ManyToOne
        private Person person
    }

#### @OneToOne

Бывает однонаправленной и двунаправленной (через **mappedBy**)

##### Однонаправленная OneToOne

Реализуется через внешний ключ

    @Entity
    class Phone {
        ...
        @OneToOne
        @JoinColumn(name="details_id")
        private PhoneDetails details
    }
    
    @Entity
    class PhoneDetails {
        ...
    }

С точки зрения БД аналогичен однонаправленной **ManyToOne** (в обоих случаях 2 таблицы с внешним ключем)



## 7. Доступ к БД

### 7.1 ConnectionProvider

Это интерфейс. Его реализация определяет как подключаться к БД.
Есть встроенные реализации
Можно свою

### 7.2 Использование DataSource














