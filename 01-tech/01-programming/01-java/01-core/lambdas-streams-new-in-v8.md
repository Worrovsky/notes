
Лямбда - как реализация функционального интерфейса 
    (интерфейс с единственным абстрактным методом)
    = создание анонимного метода
    например существует (библиотека, сами создали) метод, где есть параметр с типом 
        интерфейса (функционального):
            public void someMethod(FunInterface fun) {...}
            public interface FunInterface {
                public void test();
            }
         тогда варианты что подставить параметром при использовании метода:
            - класс, реализующий интерфейс
            - анонимный класс
            - лямбда-выражение


java.util.function - различные интерфейсы (чтобы не создавать свои)
    базовые:
        Predicate
            один входной параметр - возвращает истина/ложь
            public interface Predicate<T> {
                boolean test(T t);
            }
        Consumer
            просто какая-то операция над одним операндом без возвращаемого значения
            public interface Consumer<T> {  
                void accept(T t);
            }
        Function 
            операция над одним операндом с возвратом значения
            public interface Function<T, R> {
                R apply(T t);
            }
        Supplier
            без параметров возвращает значение

    базовые с приставкой Bi (кроме Supplier) - имеют два входных параметра

    базовые с приставкой типа (Int-, Long-, Double-)
        заданный тип для входного/выходного(в зависимости от функции) параметра
        для Function - только для входного

    для Function есть XXXtoYYY:
        IntToLongFunction, IntToDoubleFunction


Синтаксис
    - входные парамтеры через зпт в ()
        если единственный - можно без скобок
        типы можно не указывать
    - токен "->"
    - тело
        если несколько операндов - {}
        если есть возвращаемое значение - должен быть return
        если оператор единственный - return можно опускать

    примеры:
        (a, b) -> a + b
        a -> 10 - a


    переменной с типом какого-то интерфейса можно присваивать лямбда-выражение
        interface IntegerMath {
            int operation(int a, int b);   
        }
        IntegerMath sub = (a, b) -> a - b;

Использование переменных из объемлющего кода
    до java 8 необходимо было объявлять переменную как final 
        final String name = "ffrogg";
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.out.println("Hell, " + name);
            }
        });

    теперь можно явно не объявлять, но переменная должна быть 
        эффективно финальной (с одним присваиванием). Лямбда "захватывает"
        не переменную, а значение.

        пример (получим ошибку компиляции)
            String name = "n1";
            name = "n2";
            btn.addActionListener(event -> System.out.println("he" + name)); 


Определение типа лямбда-выражения:
    тип лямбда-выражения определяется по целевому типу (target type), т.е. чему назначено выражение
    отсюда применять лямбда-выражения можно:
        - в объявлении переменных
        - в присваивании
        - в return
        - в инициализаторах массивов
        - в аргументах методов/конструкторов
        - в теле лямбда-выражения
        - в выражениях условия, ? :
        - в преобразованиях (cast)


Лямбда - как вызов существующего метода (method reference)
    когда тело лямбды - просто вызов метода

    например:
        class Person {
            public static int compare(Person a, Person b) {...}

            public test(){
                Arrays.sort(personArray, (a, b)->)
            }
        }

Потоки

    Переход от внешнего итерирования к внутреннему

        int count = 0;
        for (Artist artist : allArtist){
            if (artist.isFrom("London")) {
                count++;
            }
        }

        long count = allArtist.stream()
                            .filter(artict -> artist.isFrom("London"))
                            .count();

    Два вида методов потоков:
        отложенные (lazy)       возвращают Stream
        терминальные (eager)    возвращают не Stream или void

        + разделения:
            + обход коллекции один раз
            + оптимизация (после всей цепочки больше знаем, лучше обрабатываем)

    Методы потоков:
        collect(toList())   
            выводит результат потока в коллекцию
            List<String> collected = Stream.of("s", "d", "f").collect(Collectors.toList());

        map 
            применяет переданную функцию к каждому элементу потока
                и возвращает новый поток
            функция - реализация интерфейса Function (R -> Function -> T)
            List<String> collected = Stream.of("s", "d", "f")
                                    .map(str -> str.toUpperCase())
                                    .collect(Collectors.toList());
        filter
            оставить или пропустить элемент коллекции по переменной функции
            функция - реализация интерфейса Predicate (T -> Predicate -> boolean)

        flatMap
            когда функция в map возвращает поток, flatMap объединяет потоки в один
            List<Integer> collected = Stream.of(asList(1, 2), asList(3, 4))
                                    .flatMap(numbers -> numbers.stream())
                                    .collect(toList());

        max, min
            Track shortest = tracks.stream()
                                .min(Comparator.comparing(track->track.getLength()))
                                .get();
            в min передается объект Comparator, создается через метод comparin, 
                который принимает ф-ю, возвращающую значение для сравнения
            get() возвращает Optional

        общий принцип редукции
            accumulator = initValue;
            for (element : elements) {
                accumulator = combine(accumulator, element);
            }

        reduce
            получение одного значение из списка
            int sum = Stream.of(1, 2, 3)
                    .reduce(0, (acc, elem) -> acc + elem);
            в reduce передается функция-редуктор BinaryOperator

            есть два варианта
                с начальным значением
                без начального (при первом обращении используется 2 первых элемента)
                    возвращает значение Optional


Библиотеки (изменения Java 8)
    
    Вызов лямбд
        пример из логгирования:
            Logger logger = new Logger()
            if (logger.isDebugEnabled()) {
                logger.debug("MSG " + expensiveOp());
            }

        хотелось бы лямбда-выражение
        logger.debug(() -> "MSG" + expensiveOp());

        но для этого изменить реализацию метода debug()
            public void debug(Supplier <String> message) {
                if (isDebugEnabled()) {
                    debug(message.get())
            }}

        + работает обратная совместимость

    Примитивные типы
        соглашения об именовании
            тип возвращаемого значения примитивный - имя функции с To+ИмяТипа
                ToLongFunction
            аргумент - примитывный - имя функции с имени типа
                LongFunction
            в функции высшего порядка используется примитивный тип - имя функции
                заканчивается To+ИмяТипа
                mapToLong

        рекомендуется использовать примитивные типы
            работают быстрее
            + доп. методы типа summaryStatistics()

    Разрешение перегрузки
        при разрешении выбирается самый специфичный тип (напр. подходит Object
            , String  - будет String)
        если среди нескольких типов нельзя определить самый специфичный - ошибка компиляции

    аннотация @FunctionalInterface

        есть интерфейсы с одним методом, но которые не предполагаются для лямбда-выражений
            (напр. Closeable)
        с аннотацией компилятор проверяет признак "функциональности" (один абстрактный метод)

    двоичная совместимость и методы по умолчанию
        в Java 8 была изменена библиотека коллекций
            напр. добавлены методы в интерфейс Collection (stream())
        следовательно нужно реализовать во всех классах с интерфейсом 
            Collection (напр. MyCustomList)
        решение - методы по умолчанию
            default void forEach(Consumer<? super T> action) ...
        если в реализации интерфейса нет реализации метода, вызывается метод по умолчанию

    Методы по умолчанию и наследование
        интерфейс Parent и метод по умолчанию welcome() {print("Hi from Parent");}
        случ. 1
            в реализации ParentImpl метод welcome не определен, тогда вызывается метод
                по умолчанию из интерфейса
        случ. 2
            интерфейс Child переопределяет welcome
                interface Child extends Parent {
                    @Override
                    public void welcome(){print("Hi from child");}
                }
            в реализации ChildImpl без определения метода, welcome будет вызван из 
                интерфейса Child
        случ. 3
            class OverridingParent extends ParentImpl {}, переопределен метод welcome, 
                он и будет вызываться
        случ. 4 
            class OverridingChild extends OverridingParent implements Child{}
              метод welcome не переопределен
              будет вызван из OverridingParent, не из Child
              правило: предпочтение отдается конкретной реализации (в классе), 
                а не методу по умолчанию (в интерфейсе) (опять же для совместимости)

    Множественное наследование
        с введением методов по умолчанию возможно через интерфейса
        если есть 2 интерфейса с одинаковой сигнатурой методов, то при 
            class MyClass implements Int1, Int2 {} ошибка компиляции (не сможет решить какой
                метод использовать)
            можно явно указать 
                @Override
                void myMethod() {
                    Int2.super.myMethod();
                }
            здесь super дает доступ к методам по умолчанию интерфейсов

    Правила разрешения конфликтов
        - классу всегда отдается предпочтение перед интерфейсом
            если в цепочке наследования есть метод с телом или хотя бы абстрактное
            объявление, про интерфейсы забываем
        - Подтипу отдается предпочтение перед супертипом

    Статические методы интерфейсов
        раньше какие-то служебные методы необходимо было выделять в отдельный класс
        теперь можно в интерфейсы
            напр. Stream.of()
        
    класс Optional  

        возвращается функцией reduce без переданного начального значения
        альтернатива null

        создание объекта Optional
            Optional<String> a = Optional.of("a");
        получение значения
            String val = a.get();
        создание пустого значения
            Optional empty = Optional.empty();
        из null 
            Optional alsoEmpty = Optional.ofNullable(null);
        проверка на наличие значения
            empty.isPresent() == false;
        значение по умолчанию, если пуст
            empty.orElse("g");
        или отложенное через передачу Supplier
            opt.orElseGet(() -> "r");


Коллекции и коллекторы

    Ссылки на методы
        стандартная идиома: вызов метода от имени параметра
            artist -> artist.getName();
        для этого сокращенный синтаксис
            Artist::getName
        конструкторы
            Artist::new
    Порядок в потоках
        потоки обрабатывают элементы в некотором порядке (порядок поступления)
        если исходная коллекция имеет порядок, то и порядок поступления также определен
            List<Integer> list = asList(1, 2, 3, 4);
            List<Integer> otherList = list.stream().collect(toList());
            assertEquals(list, otherList);
        если коллекция не имеет порядка (hashMap, set и т. п.), то и выходной 
            порядок не гарантирован
        порядок поступления распространяется на всю цепочку операций
        порядок можно создать операцией sorted()

        в параллельных потоках порядок не гарантирован

    Коллекторы
        коллектор - конструкция для получения результата из потока
            интерфейс Collector
            аргумент метода collect()

        различные варианты коллекторов реализованы в классе Collectors
        - коллекторы, порождающие коллекции
            toList, toSet, toMap, toCollection
            здесь не указывается конкретный вид коллекции
            чтобы указать можно 
                stream.collect(toCollection(TreeSet::new));

        - порождение других значений
            maxBy, minBy 
            averagingInt(), summingInt()
                параметрами принимают функцию, получающую int из элемента

        - разбивка потока
            partitoningBy(Predicate) возвращает Map<boolean, List<T>>, в
                котором элементы разбиты на 2 группы по условию Predicate
                stream.collect(partitoningBy(x -> x.isGood()));
            groupingBy(Function) разбивает по произвольному типу значений

        - объединение строк
            stream.collect(Collectors.joining(",", "[", "]"));
            указывается разделитель, начальный и конечный ограничители

        Композиция коллекторов
            напр. задача посчитать количество элементов в группировке
                stream.collect(groupingBy(..., counting()));
            задача помещения наименования элементов групп в список
                stream.collect(groupingBy(..., mapping(::getName, toList())));

    Улучшение интерфейса коллекций
        новые методы Map
            computeIfAbsent(name, this::readFromDB);
                вызывает функцию параметр №2, если ключа параметра №1 нет
            computeIfPresent - наоборот

            обход коллекций с forEach
                map.forEach( (k, v) -> {
                    ...
                });










