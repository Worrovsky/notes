# 

## 1. Основы

JUnit 5 = JUnit Platform + JUnit Jupiter + JUnit Vintage

Platform: вспомогательные средства
Jupiter: собственно тесты
Vintage: для запуска JUnit3, JUnit4

Пример зависимости

    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.5.2</version>
    <scope>test</scope>

## 2. Аннотации

`@Test`
`@Disabled`

Составные аннотации - можно создавать собственные аннотации на основе аннотаций JUnit (не все можно наследовать, напр. `@Disabled` нельзя)

## 3. Тестовые классы и методы

Класс-тест - обычный класс, статический класс или вложенный класс с `@Nested`
Имеет минимум один тестовый метод
Имеет единственный конструктор
Не абстрактный класс

Тестовый метод - метод с любой аннотацией из: `@Test`, `@RepetedTest`, `@ParametrizedTest`, `@TestFactory` или `@TesTemplate`

Методы жизненного цикла: `@BeforeAll`, `@AfterAll`, `@BeforeEach`, `@AfterEach`

Тестовые и цикла методы:

* объявляются в классе, наследуются из родителя или из интерфейсов
* не абстрактные
* не возвращают результат
* не должны быть `private` (`public` необязательно)


## 4. Имена тестов

`@DisplayName` - можно менять имена тестов, отображаемые в репортах или в IDE (по умолчанию - имя класса и имена методов)

Можно задавать генераторы имен `@DisplayNameGeneration`. Генераторы преобразуют имя метода/класса по некоторым правилам. Напр. заменяют `_` на ` `

## 5. Asserts

статические методы `org.junit.jupiter.api.Assertions`

последний параметр - сообщение (опционально)
может вычисляться лениво через лямбду

    assertEquals(2, 4, () -> "....");


Можно группировать

    assertAll("<header>", () -> assert...,
                        () -> assert...);

Можно с таймаутами

    assertTimeout(ofMinutes(2), () -> ...);

### 4.2 Сторонние библиотеки (AssertJ)

[docs](https://assertj.github.io/doc/)

Кратко: 

* зависимость `assertj-core`
* `import static org.assertj.core.api.Assertions.assertThat;`

начинается с `assertThat(...)..` 
далее можно `as()` как текстовое уточнение при ошибке
далее собственно утверждение `isEqualsTo(...)`

переопределение текста ошибки: `withFailMessage()` или `overridingErrorMessage()`

Основные ошибки:

* пробуск утверждения: `assertThat(true);` / `assertThat(true).isTrue();`
* `as()` должен использоваться перед утверждением  `assertThat().as().isEquals`
* методы переопределения ошибок тоже перед утвердением
* установка компраторов тоже перед утверждением







## 6. Assumption

Условное выполнение тестов. Если не выполняется условие тест просто не 
запускается

    Assumption.assumeTrue(<условие>);
    assert... ;

или

    Assumption.assumingThat(<условие>, () -> {
        assert...;
    }

В качетве условий напр. переменные окружения `System.getenv("ENV")`

Также аналог есть в AssertJ

## 7. Отключение тестов

`@Disabled("описание")`

Рекомендуется устанавливать описание, почему тест отключен


## 8. Условное выполнение тестов

### 8.1 Условия по ОС

`@EnabledOnOs`, `@DisabledOnOs`

вроде можно и над классами, но не работает что-то ???

### 8.2 Условия по JRE

`@EnabledOnJre(JRE.JAVA_9)`
`@DisabledOnJre(JRE.JAVA_9)`

`@EnabledForJreRange(min = JAVA_9)`
`@DisabledForJreRange(min = JAVA_9, max = JAVA_11)`

### 8.3 Условия по системным свойствам JVM

`@EnabledIfSystemProperty`
`@DisabledIfSystemProperty`

c v5.6 можно несколько повторяющихся

### 8.4 Условия по переменным окружения

`@EnabledIfEnvironmentVariable(named = "ENV", matches = "staging-server")`
`@DisabledIfEnvironmentVariable(named = "ENV", matches = ".*development.*")`

