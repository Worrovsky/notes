## 4. Работа с Spring Data Repositories

### 4.1 Основа

    public interface CrudRepository<T, ID> extend Repository<T, ID> {
        <S extends T> S save(S entity);
        Optional<T> findById(ID key);
        // и др.
    }


Есть доп. интерфейсы, наследующие **CrudRepository**:

* `JpaRepository`
* `MongoRepository`
* `PagingAndSortRepository`

Общая иерархия:

Repository  <- CrudRepository <- PagingAndSortRepository <- JpaRepository
QueryByExampleExecutor <- JpaRepository

### 4.2 CRUD операции для работы с БД

Шаги по огранизации доступа к БД:

* Определить интерфейс, наследующий `Repository` или его потомков с типизацией его классом-доменом и типом id
    - `interface UserRepository extends Repository<User, Long>{..}`
* Определить методы в этом интерфейсе
    - `List<User> findByName(String name);`
* настроить Spring на создание прокси-классов для этого интерфейса
    - через аннотацию `@EnableJpaRepositories`
    - через XML конфигурацию
* внедрить экземпляр репозитория и использовать

### 4.3 Определение repository интерфейсов

Обычно пользовательский интерфейс расширяет `Repository`, `CrudRepository` или `PagingAndSortRepository`.

Можно свои интерфейсы определять

    @RepositoryDefinition(domain=..., id=)
    interface MyRepInterface { ... }

Можно уменьшить количество методов / создать доп. абстракцию (аннотация `NoRepositoryBean` запрещает создание объектов этого интерфейса):

    @NoRepositoryBean
    interface MyBaseInterface<T, ID> extends CrudRepository<T, ID> {
        // нужные методы
    }
    interface UserRepo extends MyBaseInterface<User, Long>{
        // доп. методы
    }


### 4.4 Определение методов

#### 4.4.0 Основы

Два способа для класса-прокси создать запрос:

* на основе имени метода
* на основе запроса, указанного вручную

#### 4.4.1 Стратегии создания запросов

Стратегия задается:

* через атрибут `query-lookup-strategy` для XML конфигурации
* через свойство `queryLookupStrategy` аннотации `Enable${store}Repositories`

Варианты стратегии:

* **CREATE** - запрос строится на основании имени метода
* **USE_DECLARED_QUERY** - запрос определяется явно. Если не найден - исключение
* **CREATE_IF_NOT_FOUND** - (по умолчанию) сначала ищет явный запрос, если не находит - создает на основе имени

#### 4.4.2 Создание запросов по имени

начало с `find|read|get|query`(равнозначны, поиск) или `count`(подсчет кол-ва)
Наличие `..By..` обязательно
Перед ним можно указать `..Distinct..`
После условие 
Можно несколько условий объединять `Or` / `And`
Игнорирование `IgnoreCase` / `AllIgnoreCase` (подходящие типы зависят от конкретной БД)
Сортировка `OrderBy{имя поля}` + `Desc/Asc`

[Полный список](https://docs.spring.io/spring-data/jpa/docs/2.2.6.RELEASE/reference/html/#repository-query-keywords)

#### 4.4.3 Выражения для вложенных свойств

Напр. класс `Person` со свойством типа `Address`, внутри которого есть поле `ZipCode`. Т. е. `p.address.zipCode` 

Можно такой метод `List<Person> findByAddressZipCode(ZipCode code);`

Как работает алгоритм:

* пытается найти свойство по полному имени `addressZipCode`
* если не находит, разбивает имя по верблюжьей нотации на хвост, шапку и пытается найти так: `addressZip` и `Code`
* если не находит, смещает разбиение и снова ищет: `address` и `zipCode`

Могут быть неоднозначности, тогда можно явно указать точку разделения через `_`

    List<Person> findByAddress_ZipCode(ZipCode code);    

но предпочтительнее camelCase


#### 4.4.4 Особые параметры

сортировка, пагинация

    Page<User> findByName(String name, Pageable p);
    Slice<User> findByName(String name, Pageable p);
    List<User> findByName(String name, Sort s);
    List<User> findByName(String name, Pageable p);

Для этих параметров не допускается `null`, если нужны пустые - `Sort.unsorted()` и `Pageable.unpaged()`

Slice - более легковесная замена для Pageable

Сортировку можно задавать способами:

* простым 
* Типобезопасным
* с помощью QueryDsl

Простой:

    Sort s = Sort.by("name").ascending().and(Sort.by("type").descending())

Типобезопасный:

    TypedSort<User> user = Sort.sort(User.class);
    TypedSort<User> s = user.by(User::getName).ascending()
        .and(user.by(User::getId).descending())

#### 4.4.5 Лимиты на результат

Ключевые слова `first` и `top` (равнозначные)
если не указывать числовое значение, подразумевается 1
можно указывать явно

    List<User> findFirst100ByName(String name, Sort s);

Для ограничений на 1 результат может быть типа Optional
Можно комбинировать с Distinct
Можно с сортировкой

#### 4.4.6 Методы, возвращающие коллекции или итерируемые объекты

Методы могут возвращать стандартные `Iterable`, `List`, `Set`
Дополнительно можно спринговый `Streamable` или сторонний `Vavr`

`Streamable` - аналог `Iterable` с поддержкой методов `filter`, `map`, конкатенацией с другими и др.

    Streamable<User> findByName(String name);

Можно создавать собственные обертки над `Streamable` и возвращать их

#### 4.4.7 Обработка null

Методы могут возвращать Optional в отсутствие результата или настроить можно на возврат `null` 

Подключение аннотации `@NotNullApi` (на уровне пакета) 

[core](https://docs.spring.io/spring/docs/5.2.5.RELEASE/spring-framework-reference/core.html#null-safety)

    @org.springframework.lang.NonNullApi
    package com.acme;

    package com.acme;                                                       
    import org.springframework.lang.Nullable;
    
    interface UserRepository extends Repository<User, Long> {
      User getByEmailAddress(EmailAddress emailAddress); // здесь исключение, если вернет null
      @Nullable
      User findByEmailAddress(@Nullable EmailAddress emailAdress); // здесь допустимо
      Optional<User> findOptionalByEmailAddress(EmailAddress emailAddress); // здесь 
    }

#### 4.4.8 Stream как результат методов

    Stream<User> readAllByFirstNameNotNull();

Поток должен закрываться после использования явно через `close()` или через try-with-resources

    try (Stream<User> stream = repository.findAllByCustomQueryAndStream()) {
      stream.forEach(…);
    }


### 4.5 Создание экземпляра репозитория

#### 4.5.1 Конфигурация через XML

    пока не интересно

#### 4.5.2 JavaConfig

аннотация `@EnableJpaRepositories`
указывается на классе-конфигурации с указанием пакета для для сканирования или по умолчанию


### 4.6 Кастомизация репозиториев

#### 4.6.1 Кастомизация отдельных репозиториев

Создаем интерфейс с произвольными методами
Создаем реализацию этого интерфейса (важно: имя = имя интерфейса + `Impl`)
В реализации что угодно: бины, jdbc и пр.
Этот интерфейс наследуем в основном интерфейсе

    interface CustomRepository {
        void someMethod();
    }
    class CustomRepositoryImpl {
        public void someMethod() { ... }
    }
    interface UserRepository extends CrudRepository<User, Long>, CustomRepositoryImpl { ... }

#### 4.6.2 Кастомизация базового репозитория

Создать свою реализацию одного из Spring Data интерфейсов
Указать ее в `@EnableJpaRepositories(repositoryBaseClass=..)`






## 5. Разное

### 5.2 Сохранение Entity

Через `CrudRepository.save()`.
Под капотом вызывается методы JPA `EntityManager`:

* `entityManager.persist()` для новой записи
* `entityManager.merge()` для уже существующей

Как определяется новая запись или нет:
* если есть поле `version` и оно `null` - это новая. Аналогично для поля `id`
* entity может реализовать интерфейс `Persistable` и проверяется методом `isNew()`
* реализовать `EntityInformation`

### 5.3 Query методы

#### 5.3.3 NamedQuery

На domain-классе задаем метод и запрос.

    @Entity
    @NamedQuery(name="User.do", query="select u from User")
    class User { ... }

Создаем репозиторий (Jpa)

    public interface UserRepository extends JpaRepository<User, Long> {
        List<User> do();
    }

Внимание на поле `name` аннотации: это имя domain-класса + имя метода

#### 5.3.4 Использование @Query

Используется для задания запроса непосредственно у метода в интерфейсе

    public interface UserRepository extends JpaRepository<User, Long> {
      @Query("select u from User u where u.emailAddress = ?1")
      User findByEmailAddress(String emailAddress);
    }

Можно использовать `LIKE`:
     
     @Query("select u from User u where u.firstname like %?1")

Можно указать, что это нативный sql: 
    
    @Query(value = "SELECT * FROM USERS WHERE id = ?1", nativeQuery = true)


#### 5.3.5 

#### 5.3.6 Именованные параметры

По умолчанию Spring Data JPA связывает параметры по позиции
Аннотация **@Param** позволяет по имени

    @Query("select u from User u where u.first = :first or u.last = :last")
    User findByLastnameOrFirstname(@Param("last") String last,
                                 @Param("first") String first);


#### 5.3.7 SpEL выражения в запросах @Query

`#{#entityName}` можно использовать вместо имени таблицы, когда она может меняться или в универсальном запросе

    @Query("select u from #{#entityName} u where u.lastname = ?1")


### 5.6 Запросы через Example

#### 5.6.1 Основы

Еще один способ выполнить запрос к БД

Составные части:

* `Probe` - экземпляр domain-объекта с заполнеными полями
* `ExampleMatcher` - отвечает за условия сравнения (можно без него, простое =)
* `Example` - содержит первые 2 объекта, выполняет запросы

Дополнительно: репозиторий должен наследовать интерфейс `QueryByExampleExecutor` (методы findOne, findAll, count, exists)

Строковые поля сравниваются на starts/contains/ends/regex, остальные только на равенство

#### 5.6.2 Создание объекта Example

статический метод `Example.of()`
Параметры:

* экземпляр domain-класса
    - поля с null игнорируются
    - по остальным идет отбор
* экземпляр `ExampleMatcher`

Пример 1:

    Person person = new Person();                         
    person.setFirstname("Dave");                          
    Example<Person> example = Example.of(person);


Пример 2:

    ExampleMatcher matcher = ExampleMatcher.matching()     
      .withIgnorePaths("lastname")                         
      .withIncludeNullValues()                             
      .withStringMatcherEnding();                          
    Example<Person> example = Example.of(person, matcher); 

Различные варианты:

    ExampleMatcher matcher = ExampleMatcher.matching()
     .withMatcher("firstname", endsWith())
     .withMatcher("lastname", startsWith().ignoreCase());    }

    ExampleMatcher matcher = ExampleMatcher.matching()
      .withMatcher("firstname", match -> match.endsWith())
      .withMatcher("firstname", match -> match.startsWith());    }

#### 5.6.3 Выполнение

Просто вызов метода интерфейса `QueryByExampleExecutor`

     @Autowired PersonRepository personRepository;
     ...
        Person probe = new Person("Alice");
        personRepository.findAll(Example.of(probe));

### 5.7 Транзации


