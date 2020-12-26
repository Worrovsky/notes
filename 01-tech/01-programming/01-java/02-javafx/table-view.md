### ch. 13 TableView

#### Классы, связанные с TableView

* **TableView** 
    - собственно сама таблица
* **TableColumn** 
    - колонка в таблице
    - содержит ячейки (класс TableCell)
    - несколько экземпляров для одной таблицы
    - использует cell value factory для заполнения данных в ячейках
    - нужно определять явно, если хотим видеть данные в ячейках
    - использует cell factory для отрисовки (render) ячеек
    - по умолчанию умеет отрисовывать текстовыми ячейками
    - имеет ссылку на саму таблицу (свойство `tableView`)
    - может определять контекстное меню по нажатию на заголовок (свойство contextMenu)
* **TableRow**
    - строка в таблице
    - используется если надо как-то настроить отображение строк
    - но чаще настраивают ячейки и TableRow редко используется
    - имеет ссылку на саму таблицу (свойство `tableView`)
* **TableCell**
    - ячейка в таблице
    - настраивается
    - отображает данные из модели, ассоциированной с таблицей
    - имеет ссылку на саму таблицу (свойство `tableView`)
* **TablePosition**
    - представляет позицию ячейки в таблице
    - методы `getRow() getColumn()` возвращают индексы строки и колонки
* **TableViewFocusModel**
    - внутренний статический класс TableView
    - управляет обработкой фокуса для таблицы
* **TableViewSelectionModel**
    - внутренний статический класс TableView
    - управляет обработкой выбора для таблицы 

#### Работа с  TableView

##### Создание

* `TableView<Person> table = new TableView<>();` 
* TableView - параметризуемый тип, поэтому указываем тип содержимого таблицы (items)
* в простом случае содержимое таблицы - pojo-класс со свойствами (SimpleStringProperty и т.п.)

##### Добавление колонок

1. Создаем колонки
    - `TableColumn<Person, String> nameCol = new TableColumn<>("<Заголовок>");`
    - первый параметр класса TableColumn - тип элементов (item) таблицы. Тот же, что и при создании TableView указывался.
    - второй параметр - тип содержимого ячейки

2. Настраиваем заполнение содержимого
    - нужно указать как колонка будет заполнять содержимое ячеек
    - для этого устанавливаем свойство `cellValueFactory`
    - если тип item'ов - класс на основе property FX, тогда в качестве value factory используется класс `ProperyValueFactory`
    - `ProperyValueFactory<Person, String> factory = new ProperyValueFactory<>("<имя свойства pojo-класса>");`
    - можно настраивать по-другому

3. Добавляем колонку
    - `table.getColumns().add(column);`

###### Вложенные колонки
* несколько колонок могут включаться в общую (группирующую колонку) 
* общая колонка имеет только визуальный эффект, для нее не задается cell factory
* добавление вложенных колонок выполняется как и для TableView через `getColumns().add(..)`

###### Пример создания таблицы с колонками
     TableView<Person> table = new TableView<>();
     
     // общая колонка
     TableColumn<Person, String> nameCol = new TableColumn<>("Name");
     
     TableColumn<Person, String> firstCol = new TableColumn<>("First");
     firstCol.setValueFactory(new ProperyValueFactory("firstName"));
     
     TableColumn<Person, String> lastCol = new TableColumn<>("Last");
     lastCol.setValueFactory(new ProperyValueFactory("lastName"));
    
     nameCol.getColumns().addAll(firstCol, lastCol);
     table.getColumns().add(nameCol);

##### Настройка placeholder (как показывается отсутствие дянных)
* через метод `setPlaceholder(Node node)`
* параметров - объект класса Node
* напр. через Label 
    - `table.setPlaceholder(new Label("No data!"));`


##### Заполнение таблицы данными

* TableView имеет свойство items типа ObservableList<S>, где S - тип элементов таблицы
* Каждый элемент в этом списке соответствует строке таблицы
* Добавление в список вызывает добавление строки в таблицу, удаление - аналогично
* Доступ к списку - метод `getItems()`

Пример 

    TableView<Person> table = new TableView<>();
    table.getItems().add(new Person("Bob"));

##### Заполнение содержимого ячеек
* за заполнение ячеек отвечает свойство `cellValueFactory` класса TableColumn
* `cellValueFactory` это объект класса `Callback`,  который принимает объект `TableColumn.CellDataFeatures` и возвращает `ObservableValue`
* `CellDataFeaturs` - внутренний класс `TableColumn` и из него можно получить ссылка на
    - таблицу getTableView()
    - колонку getTableColumn()
    - элемент(item) getValue()
* когда таблице нужно обновить данные в ячейке, она вызывает метод `call()` объекта cellValueFactory (назначен колонке), call() возвращает объект типа ObservableValue<>
* если ObservableValue содержит Node, он графически отображается в ячейке
* иначе вызывается метод toString() у содержимого ObservableValue и отображается полученная строка
* можно возвращать объект типа ReadOnlyXXXProperty

Пример 

    TableColumn<Person, String> nameCol = new TableColumn<>("Name");
    
    Callback<CellDataFeatures<Person, String>, ObservableValue<String>> nameCellFactory = new Callback<CellDataFeatures<Person, String>, ObservableValue<String>>() {
        @Override
        public ObservableValue<String> call(CellDataFeatures<Person, String> cellData) {
            Person p = cellData.getValue();
            return p.nameProperty();
    }};
    
    nameCol.setCellValueFactory(nameCellFactory);

То же самое через лямбды

    TableColumn<Person, String> nCol = new TableColumn<>("Name");
    nCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

Частные случаи:
* если значение ячейки берется из JavaFX property, тогда просто через имя этого свойства и класс `PropertyValueFactory`

    TableColumn<Person, String> firstNmeCl = new TableColumn<>("Name");
    firstNmeCl.setCellValueFactory(new PropertyValueFactory<>("firstName");    

* если значение ячейки берется из поля POJO-класса так же как с property через PropertyValueFactory. Будет искать методы setXXX(), getXXX(), isXXX() (для boolean). Еслии только getter есть - ячейка только для чтения

##### Работа с колонками

###### Изменение видимости

По умолчанию - все видимы.
Изменяем видимость:
`myColumn.setVisible(true/false);`

Можно подключить меню к таблице для изменения видимости колонок:
`myTable.setTableMenuButtonVisible(true);`

###### Изменение порядка колонок

* Перетаскиванием в пользовательском режиме
* Программно через изменение позиции колонки в ObservableList, получаемом через getColumns() 
* Чтобы отключить изменение порядка столбцов к ObservableList можно подключить обработчик ChangeListener и изменять порядок как нужно

#### Выбор строк, ячеек

* За выбор отвечает класс `TableViewSelectionModel<>`
* Получить: 
    `TableViewSelectionModel<Person> tsm = myTable.getSelectionModel();`
* Установить множественный выбор:
    `tsm.setSelectionMode(SelectionMode.MULTIPLE);`
* По умолчанию включен режим выбора строк
* Для режима выбора ячеек:
    `tsm.setCellSelectionModel(true);`

Методы TableViewSelectionModel
    - `isSelected(int rowIndex)` - проверяет выбрана ли строка по индексу
    - `isSelected(int rowIndex, TableColumn<S, ?> column)` - выбрана ли ячейка в строке и колонке
    - `selectAll()` - выбирает все строки (или ячейки)
    - `select()` - несколько вариантов для выбора строки, ячейки, значения
    - `isEmpty()` - проверяет есть ли выбранные элементы
    - `getSelectedCells()` - возвращает ObservableList с выбранными ячейками
    - `getSelectedIndicies()` - возвращает ObservableList<Integer> с индексами выбранных строк (или строк в которых выбраны ячейки для режима выбора ячеек)
    - `getSelectedItems()` - возвращает ObservableList<S> с элементами таблицы
    - `clearAndSelect()` - сбросить все выделения перед новый выделением
    - `clearSelection()` - сбросить выделение
    
Шаблон отслеживания выбора:
    
    TableView<Person> table = ..
    TableViewSelectionModel<Person> tsm = table.getSelectionModel();
    ObservableList<Integet> list = tsm.getSelectedIndicies();
    list.addListiner((ListChangeListener.Change<? extends Integer> change) ->   {
               System.out.println("selected");
        });







    

    


