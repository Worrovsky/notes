Эффективная IDEA
https://www.youtube.com/watch?v=y5O8dIW-ROg

Код с нуля:
    - Добавление файлов в проект (в дереве проекта)
        Alt + Insert // Code/Generate
        окно с вариантами, можно поиск с клавиатуры

        вариант через Alt + Home: переход к панели навигации, далее как выше
    
    - сниппеты/Live Template
        Ctrl + J - открыть список
        psvm, sout
    
    - дополнение/Completion
        - базовый Ctrl + Space
            поиск по неполному совпадению:
                BuRe -> BufferedReader
            автопеременные (предложит имя переменной на основании типа)
                BufferedReader -> BufferedReader reader

            переменная/метод:
                re.cl -> reader.close();

        - расширенный/ Smart Type   Ctrl + Shift + Space
            после new предложит конструкторы                           ;

            дважды вызвать на переменной - предложит методы возвращающие такой же тип

    - автозавершение выражений
        Ctrl + Shift + Enter
        завершающие скобки, точка с зпт
        скобки в циклах

    - код справа налево
        Ctrl + Alt + V  // Refactor/Extract/Variable
        ArrayList<String>() -> ArrayList<String> arr = new ArrayList<String>();

Рефакторинг

    - Разделение неявно заданных переменных на объявление и использование:
        Ctrl + Alt + V
        BufferedReader = new Reader("tfdfdd"); -> String s = "tfdfdd"; BufferedReader = new Reader(s);

    - наоборот, в одну строку (inline)
        Ctrl + Alt + N

    - Выделение выражений в отдельный метод:
        выделить текст
        Ctrl + Alt + M

    - Переход к тесту / создание если нет
        Ctrl + Shift + T

Создание новых классов

    - справа-налево
        Alt + Enter
            new Person("ак", 34) -> новый класс с параметрами
        Alt + Enter
            создать поля из параметров
        Alt + Insert
            getters/setters/equals/hash и т.п.


Поиск

    Ctrl + N                класс
    Ctrl + Shift + N        по файлам и папкам, для перехода в папку - завершить набор \
    Ctrl + Shift + Alt + N  символы
    Shift x 2               везде (с историей стрелками)
    F3                      переход к следующему совпадению
    Ctrl + F7               подсвет изпользования символа


Навигация

    Alt + F1                Select In меню
    Ctrl + E
    Alt + 1
    Ctrl + Shift + E        редактируемые файлы
    Ctrl + Alt + Arrow      переключение вперед / назад
    Alt + Arrow             переключение между 2 

    Ctrl + F12              структура файла

    + поиск здесь (просто набор)

    Если находимся в окне (Alt + 1) Esc вернет  редактор

    Ctrl + Shift + F12      отключение доп. окон, только редактор

    Shift + Ctrl + стрелки      изменение размеров соседних окон




Создание новых папок/файлов в окне проекта
    Alt + Insert
        можно создавать сразу несколько папок f1/f2/file.txt

    Ctrl + Alt + Insert     создание файлов из редактора





Выделение
    Ctrl + W        постепенное выделение
        в начале строки - всю строку

    Ctrl + Shift + Up/Down  перемещение текущей строки

    Alt + Shift + ПКМ       мультикурсор
    Выделение вхождений
        Alt + J                 постепенное (мультикурсор) 
                                (?? заменить на Ctrl+D, дубль строк на Ctrl+Shift+D ??)
        Shift + Alt + J         отмена выбора
        Ctrl + Shift + Alt + J  все

    Ctrl + Alt + L          форматирование кода


Ctrl + P            показывает варианты параметров для функции

Alt + /             дополняет набранный текст до ближайщего имени

Ctrl + Alt + T      surround with ...

<name>.notnull -> if (<name> is not null) {}


Структура
    Ctrl + H            структура метода/класса
    Ctrl + F12          структура файла (для быстрого перехода)
    Alt + 7             Структура файла 

    Alt + Home          navigation bar