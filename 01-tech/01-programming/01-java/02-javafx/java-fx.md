ch 2. Properties and Bindings
    
    Общее
        Properties - доступный атрибут класса, Observable

        Binding - связывание одной величины с другой
            eager binding - сразу при изменении зависимых вычисляется связанная
            lazy binding - связанная вычисляется по мере необходимости (при чтении напр.)
            bidirectional/unidirectional

        Property для одного значения и для списков
        чтение/запись или только чтение
        основа - абстрактные IntegerProperty, DoubleProperty, ObjectProperty<T>
            из них SimpleIntegerProperty (чтение/запись) и ReadOnlyIntegerProperty

        методы get(), set() - для примитивных типов
        методы getValue(), setValue() - для объектных

        обертка для read-only ReadOnlyIntegerWrapper
            ReadOnlyIntegerWrapper wrapper = new ReadOnlyIntegerWrapper(12);
            int value = wrapper.get();
            wrapper.set(13); // оболочка может устанавливать, несмотря на только чтение
            ReadOnlyIntegerProperty prop = wrapper.getReadOnlyProperty();
            value = prop.get();
            чаще используется как внутреннее поле класса, публичный метод возвращает 
                уже свойство для внешнего использования

        в общем случае Properties содержит 3 поля:
            - ссылку на объект-владельца (bean) свойства
            - имя 
            - значение 
            соответственно несколько конструкторов
            методы для получения
                getName(), getBean()


    пример использования в классах:
        чтение/запись
            - создается свойство (приватное)
            - создаются геттеры и сеттеры для установки значения, через свойство
            - создается геттер для самого свойства
            - объявляются final по соглашениям
        только чтение
            - приватным создается оболочка
            - геттер на свойство (через оболочку)
            - геттер на значение
            - сеттеров нет, только внутренние операции
        public class Book{
            private StringProperty title = new StringProperty(this, "title", "unknown");
            public final StringProperty titleProperty(){
                return title;
            }
            public final String getTitle(){
                return title.get();
            }
            public final void setTitle(String tilte){
                title.set(title);
            }

            private ReadOnlyStringWrapper ISBN = new ReadOnlyStringWrapper;
            public final String getISBN(){
                return ISBN.get();
            }
            public final ReadOnlyStringProperty ISBNProperty(){
                return ISBN.getReadOnlyProperty();
            }
        }


    иерархия Property

        интерфейсы
            InvalidationListener
                + void invalidated(Observable)
            ChangeListener<T>
                + void changed(ObservableValue<? extends T, T, T>)
            
            Observable 
                + void addListener(InvalidationListener)
                + void removeListener(InvalidationListener)
            ObservableValue<T>
                + void addListener(ChangeListener<? super T>)
                + T getValue()
                + void removeListener(ChangeListener<? super T>)
            ReadOnlyProperty<T> 
                + Object getBean()
                + String getName()
            WriteableValue<T>
                + T getValue()
                + void setValue(T)
            Property<T>
                + void bind(ObservableValue<? extends T>)
                + void bindBidirectional(Property<T>)
                + boolean isBound()
                + void unbind()
                + void unbindBidirectional(Property<T>)

        в основе Observable - можно просматривать содержимое на факт изменения
            (addListener/removeListener). Метод invalidated listener`а вызывается когда 
            содержимое становится инвалидным.
        Все Property - Observable
        событие об изменении валидности генерируется не только при изменении самого содержимого,
            но вообще при его изменении (напр. отсортирован список)
        если несколько раз подряд меняется статус с валидного на невалидный 
            генерируется одно событие
        ObservableValue расширяет Observable для объектов, имеющих значение. Можно получать 
            зачение(getValue) и отслеживать изменение значения, добавляет ChangeListener
            с методом change


    Обработка события Invalidation
        событие генерируется при изменении состояния с валидного на невалидное
        если невалидное меняется на невалидное, событие не генерируется
        невалидное становится валидным при вычислении (напр. при вызове get(), getValue)
        
        добавить обработчик события: 
            - через лямбда
                public class Main {
                    public static void main(String[] args) {
                        property.addListener(Main::invalidated);}
                    public static void invalidated(Observable prop){
                        System.out.println("invalid (by lambda)"); }
                }
            - через внутренний класс
                property.addListener(new InvalidationListener(){
                    @Override
                    public void invalidated(Observable obs){
                        System.out.println("invalid (by inner class)");
                    }
                });
    

    Обработка события Changed
        при изменении значения 
        подключение обработчика:    
            - через лямбда
                public class Main{
                    public static void main(String[] args) {
                        property.addListener(Main::changed); }
                    public static void changed(ObservableValue<? extend Number>,
                            Number oldValue, Number newValue){
                        ...     }
                }
            - через внутренние:
                property.addListener(new ChangeListener<Number>(){
                    @Override
                    public void changed(ObservableValue<Number> prop,
                            Number oldValue, Number newValue){
                        ....
                 }});

                здесь варианты: 
                    - либо конкретный класс (Number)
                    - либо класс Object
                    - либо вообще без указания типа (предупреждения на этапе компиляции)


    утечки памяти   
        при подключении обработчика, ссылка на него сохраняется в свойстве (addListener)
            даже если обработчик больше не нужен - strong link. В долгоживущих программах -
            лишние траты на память

        выход № 0 - удаление обработчика (removeListener). Проблема в решении 
            когда же он не нужен

        выход № 1 - интерфейс WeakListener (метод wasGarbageCollected())
            реализуют WeakChangeListener и WeakInvalidationListener - обертки

            пример создания 
                ChangeListener<Number> cListener = ...
                WeakChangeListener<Number> wListener = new WeakChangeListener(cListener);
                property.addListener(wListener);

            обычно обработчик присваивается переменной и обертывается в WeakListener.
                когда больше не нужен можно обнулить переменную (null) или авто при 
                выходе из области видимости(!)
                из WeakListener удаляется сборщиком мусора, если не остается других ссылок

    
    Bindings
        общее
            выражение, меняющее свое значение при изменении составляющих (dependencies)
                составляющие - это ObservableValue. Когда создается binding или меняется 
                значение составляющего, binding помечается невалидным. 
            lazy-вычисления, вычисления - при запросе значения (get, getValue)

            IntegerProperty x = new SimpleIntegerProperty(100);
            IntegerProperty y = new SimpleIntegerProperty(200);
            NumberBinding sum = x.add(y);

            получение значения: NumberBinding.intValue()/floatValue()/longValue()

            связывание свойства со связанным выражением:
                z.bind(x.add(y));
            оключение связывания: 
                z.unbind();

        unidirectional/bidirectional
            uni: изменение в составляющих влияет на связанное значение, но не наоборот
            bi: в обоих направлениях. Возможно связать только 2 свойства, не выражения

            bind() - unidirectional
            bindBidirectional() - bidirectional

            Ограничения unidirectional:
                - нельзя менять значение связанной величины
                    y.bind(x);
                    x.set(4); // RuntimeError
                - нельзя несколько раз связывать с разными свойствами, 
                    новое связывание отменяет предыдущее

            bidirectional:
                - связываются величины только одного типа
                - можно несколько связываний:
                    z.bindBidirectional(x);
                    z.bindBidirectional(y);
                    при изменении напр. х поменяются и z, и y

        Bindings API
            high-level / low-level

            high-level
                - Fluent API (беглый/свободный)
                - Bindings class
                можно только через апи, только через класс или комбинированно

            Fluent API
                наглядный 
                поддерживает цепочку методов
                    x.add(z).add(y)    
                основа - интерфейсы XXXExpression и XXXBinding (напр. IntegerExpression, IntegerBinding)

                интерфейс Binding<T>:
                    иерархия: Observable <- ObservableValue <- Binding
                    void dispose() - опционален, сигнал о ненужности связывания, т.е. можно 
                        очищать ссылки (используются WeakListener)  
                    void invalidate() - делает связывание невалидным
                    boolean isValid() - проверка на валидность

                интерфейс NumberBinding
                    ... <- Binding <- NumberBinding
                    интерфейс-маркер

                интерфейс ObservableNumberValue
                    иерархия: Observable <- ObservableValue <- ObservableNumberValue
                    методы intValue(), doubleValue(), floatValue(), longValue()

                интерфейс ObservableIntegerValue
                    иерархия: ... <- ObservableNumberValue <- ObservableIntegerValue
                    int get()

                интерфейс NumberExpression
                    иерархия: Observable <- ObservableValue <- NumberExpression
                    основа Fluent-интерфейса
                    > 50 методов
                    возвращают чаще значения типа NumberBinding, BooleanBinding
                    NumberBinding add()
                    NumberBinding divide()
                    BooleanBinding greaterThan()
                    для числовых - допустимо смешение типов (double с float и т. п.)

                класс NumberExpressionBase 
                    реализация NumberExpression
               
                класс IntegerExpression
                    расширение NumberExpressionBase под конкретный тип (Integer)


                StringBinding
                    
                    интерфейс ObservableStringValue, аналог ObservableIntegerValue
                    String get()

                    интерфейс StringExpression
                        concat(), compare и др.
                        getValue() vs getValueSafe() (safe - пустая строка, если null)

                    пример:
                        DoubleProperty d = new SimpleDoubleProperty(1.3);
                        StringProperty initStr = new SimpleDoublePropertyStringProperty("Area = ");
                        StringExpression desc = initStr.concat(d.asString()).concat(".");
                        desc.getValue();
                    asString - для NumberBinding возвращает StringBinding

                ObjectBinding
                    для объектов любых классов
                    структура интерфейсов - аналог для NumberBinding
                    ObjectExpression - методы для сравнения, проверки на null

                тернарные операции
                    класс When

                    new When(condition).then(value1).otherwise(value2)
                        condition - ObservableBooleanValue
                        value1. value2 - константы или ObservableValue одного типа


            класс Binding
                вспомогательный класс со статическими методами
                типа add(), multiply() и т. п.


ch 3. Observable Collections

    расширение стандартных List, Set, Map
    интерфейсы ObservableList, ObservableSet, ObservableMap
    иерархия: ObservableList <- Observable
                            <- List

    соответственно добавляется отслеживание изменения валидности и изменения значений

    работа через статические методы класса javafx.collections.FXCollections

    ObservableList
        Общее
            void addListener(ListChangeListener<? super E>)
            void removeListener(ListChangeListener<? super E>)

            + addListener(InvalidationListener) 

            интерфейс ListChangeListener<E>
                void onChanged(Change<? extends E>)

                Change - внутренний класс интерфейса

        Создание
            методы FXCollections
                ObservableList<E> emptyObservableList() - пустой, неизменяемый список
                    ObservableList<String> = new FXCollections.emptyObservableList();

                ObservableList<E> observableArrayList(/Collection<? extends E> col/ E...items)

                ObservableList<E> observableList(List<E> list)

        Отслеживание валидности
            обработчик InvalidationListener

        Отслеживание изменений
            обработчик ListChangeListener

            list.addListener(new ListChangeListener<String>(){
                public void onChanged(ListChangeListener.Change<? extends String> change){
                    //...
                }});

            класс Change содержит информацию об изменениях
                различные категории событий
                особенность:
                    list.addAll("1", "2", "3");  // инфо об одном изменении
                    list.removeAll("1", "3");  // инфо о двух изменениях
                        // (удаление элемента с индекса 0("1") 
                        // и удаление элемента с индекса 1("3" после удаления "1"))
                
                обход по событиям:
                    методы next() и reset()
                    вызов next устанавливает курсор на следующее событие, возвращает true 
                    если есть событие
                    (если не было вызовов next или reset - курсор перед первый событием)
                    подходит сниппет
                        while (change.next()){...}

                категория типов событий
                    boolean wasAdded()
                    boolean wasRemoved()
                    boolean wasReplaced() (замена как удаление+добавление, 
                        поэтому если Replaced, то и Added и Removed)
                    boolean wasPermutated() - напр. пересортированы, но не удаление/добавление
                    boolean wasUpdated() - обновлены???

                    wasUpdated и wasPermutated взаимоисключаемы

                    порядок обработки в общем случае:
                        while (change.next()){
                            if (change.wasUpdated()) {...}
                            else if (change.wasPermutated()){...}
                            else if (change.wasReplaced()){...}
                            else {if (change.wasRemoved()) {...}
                                    else if (change.wasAdded()){...}}
                        }


                другие методы:
                    getFrom()/getTo() - если изменения влияли на диапазон элементов, 
                        показывают этот диапазон
                    getAddedSize() - колво добавленных
                    getRemoved() - список удаленных
                    и др.


ch 4. Stage

    Screens
        класс из javafx.stage со статич. методами. Дает информацию о мониторе/экране
            (разрещение, плотность и др.)
        получение:
            - основного 
                Screen primaryScreen = Screen.getPrimary();
            - списка доступных
                ObservableList<Screen> list = Screen.getScreens();

        методы:
            - разрещение dpi
                double dpi = screen.getDpi();
            - границы (как объект Rectangle2D) 
                getBounds()/ getVisualBounds()
                visual - область без учета нативных элементов (меню/панель задач)
                из Rectangle2D получить координаты
                    getMax/Min/x/y 
                или ширину/высоту
                    getWidth/getHeight


    Stage
        контейнер верхнего уровня
        основной Stage создается платформой и передается в метод start
        можно создавать собственные Stage

        наследник класса Window (базовая функциональность для окна: x, y, прозрачность,
            show/hide)

ch 8. Styling Nodes 

    Конвенции наименования
        классы-селекторы Css:
            основаны на именах классов Node 
            lowercase
            если из нескольких слов - разделяются "-"
            напр. Button -> button; CheckBox -> check-box
            для layout классов-соответствий нет
        свойства:
            начинается с "-fx-"
            lowercase
            если из нескольких слов - разделяются "-"
            напр. textAlignment -> "-fx-text-alignment"

    Добавление стилей
        метод getStylesheets() у Scene или Parent возвращает список
        можно добавлять стили
            scene.getStylesheets().add("style1.css");
            scene.getStylesheets().add("style2.css");

    Способы указания путей
        - относительный "resources/css/1.css"
        - абсолютный без схемы "/resources/css/1.css"
        - абсолютный со схемой "file://c:/resources/css/1.css"

        1-й и 2-й вариант разрешаются относительно базового URL ClassLoader`а
            основного класса приложения (тот, который наследует Application)

        можно то же самое так:
            String url = MainClazz.class.getClassLoader()
                            .getResource("resources/css/1.css")
                            toExternalForm();

    inline-стили
        напр. myBtn.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        не имеют селекторов
        передается пара свойство-значение
        поэтому устанавливаются только для конкретного узла

    Приоритеты стилей
        по убыванию:
            inline-стили
            родительский стиль
            стиль сцены
            установка значений программно (setFont() и т. п.)
            user agent style sheet (runtime c caspian.scc)

        так напр. установка программно не дает ничего при наличии стиля у сцены

    Наследование свойств
        - наследование типов свойств:
            если у класса объявлено свойство, все подклассы также имеют это свойство
        - наследование значений свойств:
            дочерний элемент наследует значения свойств от родителя

    Типы CSS свойств
        тип определяет синтаксис, значения
        возможные типы:
            inherit     - указывает что наследуется значение родителя
            boolean
            string      - двойные или одинарные кавычки
            number
            angle
            point
            font 
            и др.

    Селекторы

        классовые селекторы
            2 варианта:
                предопределенные, по умолчанию ("button", "label", "text-field" и др.)
                назначаются объекту через метод getStyleClass()
                    hBox.getStyleClass().addAll("hbox", "myhbox");
            обращение через "."
                    .myhbox { ... }

        корневой селектор
            корневой узел сцены имеет селектор "root"
            .root { ... }
            настройки также применяются ко всем дочерним элементам

        ID селекторы
            по свойсту id класса Node
            обращение через "#"
                #closeBtn { ... }

        при наличии классового селектора и Id-селектора будет учтен Id-селектор
            как более специфичный (есть правила определения специфичности)                


        комбинация селекторов:
            #closeBtn.button - все элементы с ид "closeBtn" и классовым селектором "button"
            .button#closeBtn - аналогично

        универсальный 
            "*" - для любого узла
            самый низкий приоритет
            * { ... }

        Группировка селекторов
            через "," можно перечислять
            .button, .label { ... }

        селекторы для потомков
            разделяются пробелом " "
            для узлов, которые являются потомками других узлов
            иерархия сквозная, не одноуровневая
            .hbox .button { ... }
            применяется для узлов, которые имеют составную структуру
            напр. CheckBox включает текст с ID="text", StackPane с ID="box"
                .check-box .box { ... }

        селекторы для дочерних
            через символ ">"
            иерархия одного уровня
            .hbox > .button { ... } // все кнопки, расположенные в HBox

        селекторы по состоянию
            через ":" в комбинации с др. селекторами
            некоторые варианты:
                disabled    Node            узел в состоянии disabled
                focused     Node            узел получил фокус 
                hover       Node            курсор над элементом 
                pressed     Node            кнопка нажата над элементом
                cancel      Button          кнопка назначена как Cancel 
                default     Button          кнопка  назначена по умолчанию
                empty       Cell
                filled      Cell 
                selected    Cell, CheckBox
                и др.
            .button:hover { ... }







ch 9. Event Handling

    События представлены классом javafx.event.Event (или подклассами)
    
    3 свойства:
        - источник события (source)
        - приемник события (target)
        - тип события (type)
    обрабатываются обработчиками или фильтрами (event handlers, event filters)

    Объекты, связанные с событиями:
        class Event
        interface EventTarget
        class EventType
        interface EventHandler

    class Event
        иерархия:
            - Event                         (подкласс java.util.EventObject)
                - InputEvent                (пользовательский ввод)
                    - KeyEvent
                    - MouseEvent
                    - TouchEvent
                - WindowEvent               (события окон)
                - ActionEvent               (дейстия: нажатия кнопок, выбор меню)

        методы:
            Object getSource() - возвращает источник события
            EventTarget getTarget() - приемник события
            EventType getType() - тип события
            consume() - прерывает движения события по цепочке обработчиков
            прочие особые методы для конкретного типа события

    interface EventTarget        
        UI элемент (не только Node), кот. может отвечать на события
        Node, Window, Scene реализуют интерфейс. TreeItem, MenuItem не 
        наследники Node, но сами реализуют EventTarget. Все эти объекты
        могут отвечать на события.

    class EventType
        для разделения событий по типам

        generic
            EventType<T extends Event>

        методы 
            getName()
            getSuperType()

        иерархия (через реализацию, не классовая)
            Event.ANY
                InputEvent.ANY
                    MouseEvent.ANY
                        MouseEvent.MOUSE_CLICKED
                        MouseEvent.MOUSE_PRESSED
                    KeyEvent.ANY
                        ...

    Механизм обработки событий
        - выбор приемника события
            узел-приемник, выбор на основе типа события
            напр. для событий мыши - объекты под курсором, для клавиатуры - 
                объекты в фокусе
        - создание пути/трассы события
            путь начинается с приемника и через все узлы к верхнему
            напр. HBox c Circle и Rect, HBox входит в Scene, Scene - Stage
                при клике на круг: Stage-Scene-Hbox-Circle
        - ход по пути события
            2 фазы:
                - фаза захвата
                - фаза bubbling
            событие проходит через каждый узел дважды (по 1 разу на фазу).
            У узла могут быть зарегистрированы фильтры и обработчики (на определенный тип события)
            На любом этапе прохождение может быть прервано (вызовом consume())

            - фаза захвата
                событие проходит от узла верхнего уровня к узлу нижнего (Stage->Scene->HBox->Circle)
                на каждом узле к событию применяются фильтры, зарегистрированые для узла
                фильтров может быть несколько, даже если в первом фильтре обработка будет 
                    прервана (consume), выполнятся ВСЕ фильтры этого узла
                на этом этапе можно организовать перехват событий на верхнем уровне, 
                    не пуская на дочерние узлы
            - bubbling фаза
                событие проходтит от нижнего в верхнему
                выполняются обработчики событий
                также можно несколько обработчиков на узел, прерывание в одном обработчике
                    не выбрасывает другие того же узла

    Обработка событий
        общее:
            обработчики - объекты интерфейса
            public interface EventHandler<T extends Event> extends EventListener{
                void handle(T event);
            }

            java.util.EventListener - интерфейс-маркер
            и фильтры, и обработчики реализуют один интерфейс, различий между ними нет.
                Только методы регистрации отличаются

        пример через внутренний класс
            EventHandler<MouseEvent> aHandler = new EventHandler<MouseEvent>{
                @Override
                public void handle(MouseEvent e){
                    // 
                }
            }
        
        пример через лямбда-выражение
            EventHandler<MouseEvent> aHandler = e -> /* код */;

        Регистрация:
            2 способа 
                - методы addEventFilter(), addEventHandler() / remove...
                    имеют классы Node, Scene, Window и др.

                    <T extends Event> void addEventHandler(EventType<T> eventType,
                        EventHandler<? super T> EventHandler)
                    параметры - тип, обработчик
                    аналогично для фильтров


                - свойства onXXX
                    классы Node, Scene, Window имеют свойства для хранения 
                        обработчиков некоторых типов, напр. OnMouseClicked

                    для установки setOnXXX(EventHandler handler)
                        node.setOnMouseClicked()
                    для отключения - установить null
                    можно получать getOnXXX

                    особенности:    
                        - свойства только для обработчиков, не фильтров
                        - только один обработчик можно установить
                        - ограниченный перечень событий поддерживается

    Порядок исполнения 
        - фильтры выполняются от верхнего уровня к нижнему, обработчики наоборот
        - для одного узла обработчик события конкретного типа вызывается раньше, 
            чем обработчик события общего типа. Напр. MouseEvent.MOUSE_CLICKED
            будет раньше чем MouseEvent.ANY
        - порядок обработки событий одного типа не определен.Но обработчики, зарегистрированые
            через addEventHandler выполняются раньше чем через onXXX


    Input Events

        иерархия
            Event
                - InputEvent
                    - MouseEvent
                    - KeyEvent
                    - GeastureEvent
                        - SwipeEvent
                        - ScrollEvent
                    - DragEvent
                    - TouchEvent

        MouseEvent
            типы событий (EventType<MouseEvent>)
            ANY - супертип для всех событий (супер тип для него - InputEvent.ANY)
            MOUSE_PRESSED - нажатие кнопки 
                MouseEvent.getButton() возвращает кнопку (перечисление MouseButton) 
            MOUSE_RELEASED - отпускание кнопки. Относится к тому же узлу, на 
                котором было нажатие
            MOUSE_CLICKED - нажатие/отпускание на одном узле
            MOUSE_MOVED - движение мыши
            MOUSE_ENTERED - вхождение мыши в узел, без передачи по цепочке
            MOUSE_ENTERED_TARGET - то же самое, но с передачей по цепочке
            DRAG_DETECTED - нажатие и перемещение мыши на значимое расстояния 
                (платформой определяется)
            MOUSE_DRAGGED - перетаскивание

ch 10. Layouts

    Основы:
        виды:
            - статический макет
                позиция, размеры узлов не изменяются при изменении размеров окна
            - динамический макет
                динамически вычисляются размеры и положение узлов при изменении размеров окна

        layout pane (container) - узел, который включает другие узлы
        имеет layout policy, которая определяет как выравниваются дочерние узлы
        задачи layout: 
            - вычислить позиции узлов
            - вычислить их размеры

        Иерархия:
            Node
                Parent
                    Group               
                    Region
                        Pane
                            VBox
                            GridPane
                            ...

         Group  - позволяет применять групповые эффекты/преобразования для дочерних узлов
         Region - для расположения дочерних узлов, можно устанавливать стили CSS

         Узел может принадлежать только одному родителю
         методы Parent для получения списка дочерних:
            protected ObservableList<Node> getChildren();
            public ObservableList<Node> getChildrenUnmodifiable();
                возвращает read-only список 
                    - для передачи другим методам без изменений
                    - для получения составляющих узлов, узла не являющегося контейнером (Button, ChoiceBox) 
            protected <E extends Node> List<E> getManagedChildren();
                вспомогательный внутренний метод

    Добавление узлов в контейнер
        контейнер содержит свои дочерние элементы в ObservableList
        для добавления в контейнер - просто добавить в этот список
        варианты добавления:
            - конструктором     (new HBox(new Label(), new Button()))
            - через список      (hBox.getChildren().add(new Label()))

    Вспомогательные классы/перечисления        
        Insets
            представляет отсупы прямоугольной области относительно другой прямоугольной области
            конструкторы
                Insets(double topRightBottomLeft);
                Insets(double top, double right, double bottom, double left);

            методы  getTop, getRight(), ...
            чаще всего для границ, фона
        перечисения 
            HPos 
                LEFT, CENTER, RIGHT - горизонтальное позиционирование и выравнивание
            VPos 
                TOP, CENTER, BASELINE, BOTTOM
            Pos 
                вертикальное и горизонтальное выравнивание
                комбинация VPos и HPos (TOP_LEFT, CENTER_RIGHT, ...)
            HorizontalDirection
                LEFT, RIGHT
            VerticalDirection
                UP, DOWN
            Orientation
                HORIZONTAL, VERTICAL
            Priority
                обозначает изменение размера при изменении размеров контейнера
                ALWAYS  - всегда изменяет свой размер при изменении свободного пространства
                NEVER   - не меняет размер
                SOMETIMES   - изменяет размер только, когда нет узлов с приоритетом ALWAYS
                    или эти узлы больше не могут изменять размер свой

    Group
        хотя имеет дочерние элементы лучшим термином - коллекция узлов, группа
            узлов, не контейнер
        позволяет манипулировать набором узлов как одним узлом. Свойства,
            эффекты, преобразования, примененные для группы - применяются для ее узлов
        политика:
            - рендерит узлы в том порядке, в котором они добавлены
            - не позиционнирует дочерние узлы, все по умолчанию (0, 0), т.е. будут перекрываться
            - по умолчанию устанавливает размер узлов к предпочитаемому. Можно отключить
                (свойство autoSizeChildren = false). При этом не будут видны узлы (размер 0)
            - не имеет своего размера (размер группы - сумма размеров дочерних)

        Создание:
            - конструктор без параметров
                Group myGroup = new Group();
            - с перечислением узлов
                Group myGroup = new Group(new Label(), new Button());
            - со списком узлов
                Group (Collection<Node> list);

        Позиционирование узлов:
            - абсолютное через методы setLayoutX(), setLayoutY()
            - относительные через связывание

    Region

        общее:
            основа для других макетов
            имеет собственный размер, может менять размер
            имеет визуальные характеристики (отсупы, фоны, границы)
            не используется напрямую как макет

            состоит из:
                - backgrounds (фон/изображение)
                - область содержимого
                - padding
                - borders (линии и изображения)
                - margin                            (край/предел)
                - insets

            backgrounds отрисовываются первыми
            область содержимого - та область, где отображаются элементы (controls) региона 
            padding - дополнительная область вокруг области содержимого
            область границ - область вокруг padding
            margin - область вокруг границ. Аналог padding, но padding - с внутренней 
                стороны границы, margin - с внешней.
            область содержимого, padding, borders влияют на границы макета. Область
                между содержимым и границами макета - insets. Вычисляется на основании
                других областей, read-only свойство

        установка backgrounds:

            фон состоит из: 
                - заполнения (fill)
                - изображения (image)
            заполнение состоит из:
                - цвета
                - радиуса
                - insets

            может устанавливаться несколько backgrounds и/или фонов в одном backgrounds

            заполнение:
                CSS стили:
                    -fx-background-color
                    -fx-background-radius
                    -fx-background-insets

                класс Background - установка программно 
                    Background.EMPTY - отсутствие фона
                    содержит 0 или больше объектов BackgroundFill BackgroundImage
                    Region имеет свойство, можно устанавливать 
                        setBackground(Background bg);

                    пример:
                        BackgroundFill redFill = new BackgroundFill(Color.Red,
                                new CornerRadii(4), new Insets(3));
                        Background bg = new Background(redFill);

            Изображение:
                CSS свойства
                    -fx-background-image        // url изображения
                    -fx-background-repeat
                    -fx-background-position
                    -fx-background-size

                Image im = new Image("url_here");
                BackgroundSize bs = new BackgroundSize(100, 100, true, true, false, true);
                BackgroundImage bi = new BackgroundImage(image,
                                                        BackgroundRepeat.Space,
                                                        BackgroundRepeat.Space,
                                                        BackgroundPosition.Default,
                                                        bs);
                Background bg = new Background(bi);

        установка padding:
            hbox.setPadding(new Insets(10));
            или
            hbox.setPadding(new Insets(10, 4, 5, 1));

        установка borders:

        установка margins:
            непосредственно для самого Region не устанавливается
            только для дочерних
            напр.
                Pane p = new Pane();
                HBox hbox = new HBox();
                HBox.setMargin(p1, new Insets(10));
                hBox.getChildren().add(p);

    Pane:
        подкласс Region
        особенности:    
            - может устанавливать позиции дочерних в абсолютных значениях
                по умолчанию - (0, 0)
            - изменяет размеры дочерних к предпочитаемому

        имеет минимальный, предпочитаемый и максимальный размеры
            минимальная ширина - сумма правого и левого отступов, аналогично для высоты
            предпочитаемые - необходимы для отображения всех дочерних в их предпочитаемых 
                размерах с учетом положения
            максимальный - Double.MAX_VALUE

        пример установки позиций:
            Button btn = new Button("=");
            btr.relocate(10, 20);
            Pane p = new Pane(btn);

        установка размеров:
            pane.setPrefSize(100, 200); // жестко указан
            или
            pane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE); 
                // рассчитывается на основе размеров дочерних

    HBox:
        
        общее:
            выравнивает дочерние в горизонтальной строке
            может:
                горизонтальное пространство между дочерними
                margin для любого дочернего
                установка поведения дочерних при изменении размера 
            ширина - достаточная для размещения всех дочерних в их предпочитаемых
            высота - максимальная из высот дочерних
            напрямую устанавливать позиции дочерних нельзя

        свойства:
            alignment    - положение дочерних относительно области HBox
                         - имеет смысл если есть свободное пространство
                         пример:
                            import javafx.geometry.Pos;
                            hbox.setAlignment(Pos.BOTTOM_LEFT)

            fillHeight   - будут ли дочерние заполнять место по вертикали, если доступно
                         - установлен по умолчанию в true
                         - у дочерних может быть ограничен максимальный размер 
                            (часто равен предпочитаемому), для увеличения нужно напр.
                            button.setMaxHeight(Double.MAX_VALUE);
                         - игнорируется, если alignment установлен в BASR_LINE
            spacing

        ограничения для дочерних:
            HBox имеет два вида ограничений:    
                - hgrow     изменяют ли узлы размер по горизонтали при растягивании HBox
                - margin    дополнительная область вокруг узла

            устанавливаются через статические методы
                HBox.setHgrow(узел, свойство);
                HBox.setMargin(узел, свойство);
            сброс ограничений:
                или присвоить null конкретному
                или методом HBox.clearConstraints(Node child) сбросить оба


            hgrow:
                import javafx.scene.layout.Priority
                root.setHgrow(okButton, Priority.ALWAYS);
                ...
                root.setHgrow(okButton, null); // отключаем

    VBox:
        все то же, что и у HBox, но в вертикальном направлении

    FlowPane:

        Общее:
             организует дочерние в колонки и строки
             узлы могут менять строки/колонки при изменении панели (текучая - flow)
             размер строк/колонок не обязательно одинаков
             настраивается вертикальное выравнивание в строках, горизонтальное в колонках

        Создание:
            new FlowPane();
            new FlowPane(hgap, vgap);
            new FlowPane(Orientation, hgap, vgap);

        Свойства:
            alignment
                в горизонтальной панели строка выравнивается по горизонтальной составляющей
                    alignment, строки в целом - по вертикальной составляющей
                в вертикальной панели колонка выравнивается по вертикальной составляющей
                    alignment, строки в целом - по горизонтальной составляющей
            
                pane.setAlignment(...);

            rowValignment
                если узлы в строке разного размера - высота строки = макс из размеров
                    узлов, тогда можно задать вертикальное выравнивание в строке
                pane.setRowValignment();
                VPos.BASELINE - запрещает расширение узлов в высоту, размер = предпочитаемому

            columnHalignment
                узлы в колонке могут быть разного размера, ширина строки = ширине
                    макс из ращмеров узлов, задает горизонтальное выравнивание в колонке
                pane.setColumnHalignment(...);

            hgap, vgap
                задают промежутки между строками и колонками
                pane.setHgap();
                pane.setVgap();

            orientation
                имеет вертикальную или горизонтальную ориентацию
                import javafx.geometry.Orientation;
                ...
                FlowPane p = new FlowPane(Orientation.VERTICAL);
                по умолчанию - HORIZONTAL
                или 
                p.setOrientation(Orientation.VERTICAL);

            prefWrapLength
                определяет область для размещения узлов (предпочитаемую)
                по умолчанию 400
                pane.setPrefWrapLength(...);
                зависит от ориентации панели

    BorderPane:

        разделена на пять областей: top, right, bottom, left, center
        в каждую область можно помешать узлы (Node)
        область может быть null - вообще не показывается

        политика:
            - узлы в верхней и нижней области - к предпочитаемой высоте,
                по ширине растягивается для заполнения свободной области
            - узлы в правой и левой - к предпочитаемой ширине, по высоте 
                растягиваются 
            - оставшееся - на центральную область остается

        создание, заполнение
            BorderPane p = new BorderPane();              // пустая
            BorderPane p = new BorderPane(new HBox());    // в центр
            BorderPane p = new BorderPane(center,top, right, bottom, left);    

            p.setTop(Node node);
            p.setCenter(Node node);
            ...

            getChildren().addAll(); не работает

        Свойства:
            5 свойств для областей с геттерами и сеттерами
            выравнивание через статические методы (выравнивание относительно области, не всей панели)
                BorderPane.setAlignment(node, alignment);
            margin через статический метод

    StackPane:
        наложение узлов 
        отображаются  в порядке добавления
        можно напр. изображение + сверху что-то как часть изображения
        ширина = ширине самого широкого узла, высота аналогично
        растягивает дочерние узлы
        узлы выравниваются по умолчанию в центре
        удобно для центрирования узлов

        свойства:
            alignment
                setAlignment() - устанавливает выравнивание для всех узлов
            alignment для узлов
                статический метод StackPane.setAlignment(node, pos);
                по умолчанию - null
                если не указано выравнивание для узла - берется из панели
            margin
                через статический метод

    TilePane:
        общее
            аналог FlowPane, но ячейки (tile) одинакового размера
            ширина ячейки = ширине самого широкого, высота аналогично

        свойства:
            alignment
                выравнивание содержимого целиком
            tileAlignment
                выравнивание содержимого ячейки
                имеет смысл если узел меньше ячейки
                для всех ячеек устанавливается, может перекрываться выравниванием узла

    GridPane:

        общее
            дочерние размещаются в ячейках динамичекой сетки
            количество ячеек определяется ограничениями, наложенными на дочерние
            ячейка - как номер строки и номер колонки, нумерация с нуля
                строки нумеруются сверху вниз, колонки слева на право 
                (по умолчанию, свойство nodeOrientation.RIGHT_TO_LEFT)

            ширина ячеек в одной колонке - одинакова, высота ячеек в одной строке - одинакова,
                строки могут иметь разную высоту, колонки - разную ширину
            узел может занимать несколько колонок/строк

        создание:
            GridPane p = new GridPane();
        показывать сетку
            p.setGridLinesVisible(true);

        добавление узлов:
            через getChildren().addAll()
            при этом - размещаются в ячейке (0, 0)

        позиционирование
            способ 1 (статические методы):
                setColumnIndex(Node node, Integer value);
                setRowIndex(Node node, Integer value);
                setConstraints(Node node, int col, int row);

                до или после добавления можно

            способ 2 (при добавлении):
                add(Node child, int col, int row);
                add(Node child, int col, int row, int colspan, int row span);
                addRow(int row, Node... children);
                addColumn(int col, Node... children);

                addRow и addColumn последовательно добавляют начиная со свободной


ch. 27. Concurrency

    Проблемы с многопоточностью:
        приложение JavaFX имеет один главный поток JavaFX Application Thread,
            в котором обрабатываются события элементов GUI
        при запуске длительной задачи в главном потоке - зависание интерфейса
        с другой стороны изменение интерфейса запрещено вне основного потока

    Некоторые методы javafx.application.Platform
        public static boolean isFxApplicationThread() - проверяет основной поток или нет
        public static void runLater(Runnable run) - позволяет выполнить задачу в основном потоке
            в некотором времени в будующем. можно вызывать из любого потока 
             
    Основы фрейморка
    
        построен на java.util.concurrency с учетом работы с GUI
        интерфейс Worker<V> - задача, для выполнения в отдельном потоке(-ках)
            состояние доступно из JavaFX Application Thread
        классы Task, Service, ScheduledService - реализации интерфейса:
            Task - однократная задача
            Service - можно повторно использовать
            ScheduledService - повторный запуск через интервалы
        перечисление Worker.State - различные состояния потоков
        WorkerStateEvent - события при изменении состояния

    Интерфейс Worker<V>
        V - тип возвращаемого значения, используем Void если не возвращает ничего
        состояние потока - observable, можно подключать обработчики, доступно из главного потока
        
        Состояния потока (enum Worker.State):
            READY (создан)
            SCHEDULED (перед запуском)
            RUNNING (выполняется)
            SUCCEEDED (выполнен)
            CANCELD (вызван метод cancel())
            FAILED (возникли исключения во время выполнения)

    Свойства Worker<V>
        read-only properties, можно задать при создании, обновлять при выполнении
        - title
            простое название, заголовок
        - message
            сообщения в процессе выполнения можно передавать
        - running
            true если в состоянии SCHEDULED или RUNNING
        - state
            состояние Worker.State
        - progress
            отношение workDone / totalWork                
        - workDone
        - totalWork
        - value
            результат выполнения типа V, если Void, value = null
        - exception
            содержит объект Throwable, если возникло исключение (статус FAILED)

        со свойствами можно работать через bind или установкой листенеров

    Класс Task<V>
        однократная задача
        реализует интерфейс Worker
        наследует java.util.concurrency.FutureTask<V>

        создание:
            унаследовать класс Task, реализовать метод call

            public class PrimeFinderTask extends Task<List<String>> {
                @Override
                protected List<String> call() {
                    ...
                }
            }    

        изменение свойств
            методы типа updateXXX()
            protected void updateMessage(String message);
            protected void updateTitle(String title);
            protected void updateProgress(long workDone, long totalWork); // + обновляет progressProperty
            protected void updateValue(V value);

            updateValue используется для публикации частичного результата,
            окончательный будет присвоен после завершения метода call()
            при частичной публикации аккуратно с многопоточностью:
                - немутабельные объекты как вариант типа FXCollections.unmodifiableList()
                - добавить property в Task, обновлять его при выполнении задачи 
                    (в Application Thtead)

            updateXXX работают из Application Thread, поэтому они свободно 
                работают со свойствами Task. Для изменения свойств без этих методов
                может понадобится Platform.runLater()

        события по смене состояния
            onCancelled
            onFailed
            onRunning
            onScheduled
            onSucceeded

            пример: 
                Task<Void> task = ...;
                task.setOnSucceeded(e -> {...});

        методы при смене состояния:
            в классе Task есть пустые методы, которые вызываются при достижении состояний
                protected void scheduled();
                protected void running();
                protected void succeeded();
                protected void cancelled();
                protected void failed();
            можно переопределить

        отмена задачи
            public final boolean cancel();
            public boolean cancel(boolean mayInterruptIfRunning);

            первый метод безусловно прерывает поток в очереди или в состоянии RUNNING
            второму явно указывается прерывать ли поток в состоянии RUNNING
            для работы этих методов внутри call() должно перехватываться InterruptException

        Запуск задания
            также как и FutureTask:
                отдельный поток (Runnnable)
                    Thread t = new Thread(task);
                    t.start();
                ExecutorService
                    ExecutorService service = Executors.newSingleThreadExecutor();
                    service.submit(task);

    Класс Service<V>

        обертка для Task, позволяет многократно запускать задачу
        реализует интерфейс Worker

        создание
            унаследовать класс Service, переопределить метод createTask
            List<Long> servise = new Service<List<Long>>() {
                @Override
                protected Task<List<Long>> createTask() {
                    return new PrimeFinderTask();
                }
            } 

            метод createTask будет вызыватся при запуске / перезапуске сервиса
                т. е. каждый раз создается Task

            

ch 29. FXML

    FXML - язык для построения пользовательского интерфейса
    отделение логики UI от бизнес-логики
    для изменения UI нет необходимости перекомпилировать код

    пример 
        <?xml version="1.0" encoding="UTF-8"?> // опциональна

        <?import javafx.scene.layout.VBox?>
        <?import javafx.scene.control.Label?>
        <?import javafx.scene.control.Button?>
        <VBox>
            <children>
                <Label text="FXML is cool"/>
                <Button text="Say Hello"/>
            </children>
        </VBox>

    Основы:
        создание приложения (VBox c Button и Label)
            <VBox>
            </VBox>  
                тегами создается элемент. Имя тега должно соответствовать классу
                    (вообще нужно полное имя <javafx.scene.layout.VBox>)

            <VBox>
                <Label> </Label>
            </VBox>
                внутри тега размещаются дочерние элементы
                строго говоря должно
                    <VBox>
                        <children> ... </children>
                    </VBox>
                    но классы имеют свойство по умолчанию (объявляется через @DefaultProperty)
                        для VBox - это "children"

        импорт
            импортироваться должны все классы, даже java.lang

            <?import javafx.scene.control.Button?>
            <?import javafx.scene.layout.* ?>

        установка свойств:
            - через атрибут FXML элемента
                <Label text=".."/>
            - через элементы-свойство
                <Label>
                    <Text>..</Text>
                </Label>
            атрибуты представляются строками, когда можно выполнить преобразование из строки
                <Rectangle x="20", y="30", ...>
            если нельзя преобразовать из строки - только свойствами
                <Button>
                    <VBox.margin>
                        <Insets top="20" ... > ...
                // соответствует VBox.setMargin(btn, new Insets(20.0))

        пространство имен:
            в большинстве случаем FXML понимает имена тегов как имена классов, свойств
            но некоторые должны быть с указанием префикса пространства имен "fx"

            объявление пространства имен:
                <VBox xmlns:fx="http://javafx.com/fxml" fx:id="myBox">

        Назначение идентификатора
            через fx:id
            id может использоватся при установке стиля через CSS

            также через id получают ссылку на элемент JavaFX класса при 
                загрузке FXML

        Назначение обработчиков событий
            - скриптовые
            - контроллеры

            значение скриптового обработчика - сам скрипт
            напр. 
                <? language JavaScript?>
                <fx:script>
                    function f1(){...}
                </fx:script>

                <Button onAction="f1();"/>

        Загрузка FXML документа

            класс javafx.fxml.FXMLLoader
            основной метод - load(), есть версии статические и методы экземпляра

            Sring fxmlDocUrl = "file:///C:/resources/fxml/test.fxml";
            URL fxmlUrl = new URL(fxmlDocUrl);
            VBox root = FXMLLoader.<VBox>load(fxmlUrl);
            // или проще без явного указания типа
            // VBox root = FXMLLoader.load(fxmlUrl);

            получаем путь относительно основного класса
            URL fxmlUrl = this.getClass()
                              .getClassLoader()
                              .getResource("resources/fxml/test.fxml");

        Контроллер в FXML

            предыдущие примеры: нет связи между кодом и UI (нет ссылок на
                элементы UI)

            контроллер - просто имя класса, который инициализирует элементы UI
            устанавливается только для корневого элемента через атрибут fx:controller

            <VBox fx:controller="com.jdojo.fxml.SayHelloController"
                xmlns:fx="http://javafx.com/fxml">
            </VBox>

            правила/особенности:
                - контроллер устанавливается FXML загрузчиком (FXMLLoader)
                - контроллер должен иметь публичный конструктор без параметров
                - может иметь "доступные" методы, которые будут обработчиками 
                    событий
                - загрузчик просматривает "доступные" имена переменных в контроллере.
                    Если имя переменной совпадает с атрибутом fx:id FXML объекта, 
                    этой переменной присваивается ссылка на этот объект.
                - контроллер может иметь метод initialize() без аргументов, возвращает 
                    void. Загрузчик вызовет этот метод после загрузки FXML документа.

            пример контроллера
                public class Controller {
                    @FXML
                    private Label msgLbl;
                    
                    public SayHelloController() { }
                    
                    @FXML
                    private void initialize() { }
                    
                    @FXML
                    private void sayHello() {
                        msgLbl.setText("Hello from FXML!");  }

                

            декоратор @FXML дает доступ классу FXMLLoader к элементам 
                контроллера, даже если они приватными объявлены
                декорируются только поля и методы

            особые переменные контроллера
                @FXML private URL location; // расположение FXML документа
                @FXML private ResourceBundle resources; // 

            если значение атрибута в FXML начинается с "#", то это указание 
                загрузчику, что это элемент контроллера (напр. обработчик события);

            правила для обработчиков:
                - метод не должен иметь аргументов или только один 
                    (тип совместим с типом события)
                - может быть оба типа методов, но будет использоваться только
                    метод с одним параметром
                - возвращаемый тип - void (нет приемников для получения значений);

            получение ссылки на контроллер:

                FXMLLoader загружает fxml и может возвращать ссылку на контроллер
                    методом getController() (это метод экземпляра), поэтому нельзя
                    загрузить статическим методом, а потом получить ссылку на контроллер 
                    (вернет null)

                    сигнатуры:
                        - <T> T load();
                        - <T> T load(InputStream is);
                        все остальные - статические
                        - static <T> T load(URL location);
                        - ...
                    пример:
                        URL fxml = getClass().getClassLoader().getResource("...");
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(fxml);
                        Pane root = loader.load(); // нельзя loader.load(fxml); - это статич. метод
                        Controller controller = loader.getController();



