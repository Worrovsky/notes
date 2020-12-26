CSS (cascading style sheet) -  язык представления элементов UI

правило: 
    селектор + набор пар свойство-значение
    селектор определяет элемент, для которого применяется правило
    пары свойство-значение разделяются ;

    пример
        .button{
            -fx-background-color: red;
            -fx-text-fill: white;
        }
        здесь селектор - .button (все кнопки)

CSS правило иначе называется стилем(style). Набор правил - таблица стилей
    (style sheet)

программная установка:
    Scene scene = ...;
    ....
    scene.getStylesheets().add("resources/css/buttontyle.css");


имена:
    имена - в lowercase
    для имен из нескольких слов - слова разделяются дефисом
    имена свойств начинаются с -fx-


добавление стилей:
    для классов Scene, Parent
    getStylesheets() возвращает ObservableList стилей
    добавление:
        - относительный путь "resources/button.css"
        - абсолютный путь без указания схемы "/resources/button.css"
        - абсолютный путь "file:/D:/resources/button.css"

        первые два разрешаются относительно базового URL ClassLoader класса,
            который расширяет Application
            это можно получить:
                String url = Main.class.getClassLoader()
                                .getRecsources("resources/style.css")
                                .toExternalForm();

Стиль для приложения
    modena.css и caspian.css 
    String Application.STYLESHEET_CASPIAN и Application.STYLESHEET_MODENA
    по-умолчанию - modena
    статич. методы
        Application.setUserAgentStylesheet(String url);
        String url = Application.getUserAgentStylesheet();
            если стиль по-умолчанию - возвращает null

inline-стили
    установка стиля для Node
    Node может наследовать от top-level контейнера
        или ему свой установлен
    имеет StringProperty свойство
    методы getStyle() / setStyle()
    не имеет селектора, влияет только на node, для которого установлен
    пример
        button.setStyle("-fx-text-fill:red");


    приоритеты для Node:
        (в порядкее уменьшения приоритета)
        - inline style
        - parent style sheet
        - scene style sheet
        - программная установка через JavaFX API
        - user agent style sheet ( другая программа, "владеющая" процессом, обычно JavaFx runtime)

    !!! если установлен стиль через файл .css, программное изменение никак не влияет !!!
