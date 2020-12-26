## Ch 1. Начало

### 1.0 Подключение к Vue проекту

`npm install bootstrap`


### 1.1 Теги, нкобходимые Bootstrap

1)

    <head>
        <meta charset
        <meta name="viewport" ...>

viewport - для работы на мобильных устройствах

2) скрипт для работы со старыми версиями браузеров `...oss.maxcdn.com...`

3) `bootstrap.js` и `jquery.js`

### 1.2 Тег контейнера

`<div class="container">...` (для страниц с фиксированной шириной) или `<div class="container-fluid>..` (для страниц, занимающих всю область просмотра)

нужен для использования сетки


## Ch 2. Базовые компоненты

### 2.1 Сеточная система

#### 2.1.1 Основы

Должна размещаться внутри блока с классом `container` и производные
Добавляем строки, каждая строка состоит из 12 колонок, колонки можно групировать произвольно, в каждой строке отдельно

для определения столбцов шаблон `.col-<вид области>-<ширина>`

    <div class="container">
        <div class="row">
            <div class="col-md-8">...</div>
            <div class="col-md-4">...</div>
        </div>
    </div>


#### 2.1.2 Смещение

Для смещения (пустого столбца): `<div class="col-md-offset-4 col-md-4">...`
(не работает)

#### 2.1.3 Вложенные строки

Строки можно вкладывать в другие. При этом вложенная независима от родительской и также из 12 частей состоит

    <div class="col-md-4">
        <div class="row">
            <div class="col-md-8"> ...

### 2.2 Дополнительно

[docs](https://getbootstrap.com/docs/4.4/layout/grid/)

#### 2.2.1 no-gutters

Колонки имеют отступы (padding). Можно отключить `<div class="row no-gutters>"`

#### 2.2.2 Авто размеры

можно не указывать ширину, тогда разделится поровну между всеми колонками 
    
    <div class="col">1/6</div>
    <div class="col">1/6</div>

можно у одной указать ширину, остальные колонки автоматически

#### 2.2.3 col-{breakpoint}-auto

Подгон размера колонки под содержимое

    <div class="col-md-auto">..</div>
    <div class="col-md-5">..</div>

#### 2.2.4 row-cols-*

Задает **количество** колонок, применяется для `row`

    <div class="row row-cols-2">
        <div class="col">...

#### 2.2.5 Выравнивание

##### 2.2.5.1 Вертикальное

`row align-items-start`, `row align-items-center`, `row align-items-end"`
или `col align-self-start`, `col align-self-center`, `col align-self-end`

##### 2.2.5.2 Горизонтальное

на строке `justify-content-start`, `justify-content-center` и др.

#### 2.2.6 Разрыв колонок

можно несколько `row`, но можно в пределах одной вставить `<div class="w-100"></div>`




## Ch 3. Разработка для разных устройств

Если нужна поддержка разных размеров устройств разработку надо начинать с наименьших.

Приемы:

* Разные размеры колонок в строке: одновременное использование классов от разных размеров
    - `<div class="col-md-4 col-xs-6">...`
* Сокрытие / видимость элементов в зависимости от размеров экрана
    - `div class="col-md-3 hidden-xs`
    - `div class="col-xs-5 visible-xs`


## Ch 5.

### 5.1 Иконки

[docs](https://icons.getbootstrap.com/)

I. Копируем исходный код с github и вставляем в html

    <svg class=".."
        ...
    </svg>

II. Картинку копируем в `/assets` и вставляем через ссылку

    <img src="/assets/img/bootstrap.svg" alt="" width="32" height="32" title="Bootstrap">


Можно менять размер, цвета фона (.bg-), цвет линий (.text-)



## I. Вспомогательные элементы

[docs](https://getbootstrap.com/docs/4.4/utilities)

### I.1 Границы

Добавляющие: `border`, `border-top`, `border-left` и т. д.

Отменяющие: `border-0`, `border-top-0` и т. п.

Цвета: `border-primary`, `border-danger`, `border-white` и т. п.

Скругление границ: 

* `rounded`
* `rounded-top` и т. п.
* `rounded-circle`
* `rounded-pill`
* `rounded-0`

Размеры скруглений: `rounded-sm` и `rounded-lg`

### I.2 Цвета

* .text-primary
* .text-secondary
* .text-success
* .text-danger
* .text-warning
* .text-info
* .text-light
* .text-dark
* .text-body
* .text-muted
* .text-white
* .text-black-50
* .text-white-50

Для ссылок изменяют цвета при наведении (кроме .text-muted, .text-white)

Почти те же цвета для фона `.bg-*` (.bg-primary и др.) + .bg-transparent

### I.3 Свойство display

Шаблоны:

* `d-{value}` - для размеров от *xs* и дальше, если не указаны другие
* `d-{breakpint}-{value}` - для размеров *sm*, *md*, *lg* и *xl*

Значения *value* (dev-moz)[https://developer.mozilla.org/en-US/docs/Web/CSS/display]:

* `none` - не показывать
* `inline` - 


### I.4 Spacing

`{property}{sides}-{size}` - для *xs*
`{property}{sides}-{breakpoint}-{size}` - для *sm*, *md*, *lg*, *xl*

Property:

* **m** - для margin
* **p** для padding

Sides:

* **t**, **b**, **r**, **l** - верх, низ, право, лево
* **х** - лево и право
* **y** - верх и низ
* пустое - для всех 4-х сторон

Size:

* 0 - 5
* auto

Mafgin может быть отрицательным `.mt-n1`


## II. Навигация

### II.1 Базовый nav

Корневой элемент для других элементов на основе flexbox`а

Внутри: `nav-item` и `nav-link`

    <ul class="nav">
        <li class="nav-item">
            <a class="nav-link active" href="#">Active</a>
        </li>
    </ul>

Если корневой элемент `nav`, можно без `nav-item`

    <nav class="nav">
        <a class="nav-link active" href="#">Active</a>
    </nav>

Здесь `.active` выделяет текущий элемент
`.disable` - неактивный

### II.2 Стили навигации

#### II.2.1 Разные варианты

Применяется рядом с классом `.nav`

* Горизонтальное выравнивание: 
    - .justify-content-center
    - .justify-content-start
    - .justify-content-end
* Вертикальное: 
    - .flex-colunm
    - есть варианты для breakpoint
* Страницы (tabs): 
    - .nav-tabs + js нужен
* Pills:
    - .nav-pills (что-то типа блоков/кнопок)
    - можно заставить элементы занимать все свободное пространство (элементы навигации обязательно должны иметь `.nav-item`
        + .nav-justified (блоки равных размеров)
        + .nav-fill (не обязательно одинаковые)

Пример:

    <nav class="nav nav-pills nav-fill">
        <a class="nav-item nav-link" href="..">..</a>
        <a class="nav-item nav-link" href="..">..</a>
    </nav>

#### II.2.2 Использование возможностей flex

Можно комбинировать для разных вариантов в зависимости от размеров экрана

Напр. для малых - вертикальное, для больших - строкой

    <nav class="nav nav-pills flex-column flex-sm-row">

#### II.2.3 Выпадающее меню

Можно добавить для pills и tabs
Нужен html + js


### II.3 Navbar

#### II.3.1 Основы

* `.navbar` + `.navbar-expand{-sm|-md|-lg|-xl}`
* по умолчанию контейнер гибкий, но можно настраивать
* можно исппользовать стандартные padding/margin/flex классы
* на печати не виден по умолчанию, но можно добавить (`.d-print`)
* работает с `nav` из коробки, если нужно с `div` добавляем `role="navigation"`

#### II.3.2 Sub-компоненты

##### II.3.2.1 Общий обзор

* `.navbar-brand` - блок для компании, названия приложения и т. п.
* `.navbar-nav` - блок обычной навигации
* `.navbar-tooggler` 
* `.form-inline` - для любых элементов управления
* `.navbar-text` - строка текста 
* `.collapse .navbar-collapse` - сворачивает меню при изменении размеров (совместно с `.navbar-expand-*`)

Пример меню, которое при уменьшении размеров, сворачивается в кнопку (нужен js). Обращаем внимание: 

* корневой элемент `nav` c `.navbar-expand-md` (это сворачивает меню)
* кнопка с классом `.navbar-toggler` (`data-target` связван с `id`)
* Пункты меню в блоке с классом `.collapse .navbar-collapse`

Сам пример:

    <nav class="navbar navbar-expand-md navbar-light bg-light">
        <a class="navbar-brand" href="#">Navbar</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse"
                    data-target="#navbarNavAltMarkup" aria-controls="navbarNavAltMarkup" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
    
            <div class="collapse navbar-collapse" id="navbarNavAltMarkup">
                <div class="navbar-nav">
                    <a class="nav-item nav-link" href="#">Home</a>
                    <a class="nav-item nav-link" href="#">Features</a>
                </div>
            </div>
        </nav>

##### II.3.2.2 Brand

Работает с любым элементов, наиболее просто с `a`

    <nav class="navbar">
        <a class="navbar-brand">My App</a>
    </nav>

Можно картинки добавлять напр.

##### II.3.2.3 Выпадающее меню

##### II.3.2.4 Формы в меню

Через класс `.form-inline`. Внутри - стандартные элементы. По умолчанию контейнер `.navbar` это *flex* и элементы внутри *justify-content: space-between*. Можно другие *flex* варианты (внимание на `navbar-expand-*`)

    <nav class="navbar navbar-light bg-light">
      <form class="form-inline">
        <input class="form-control mr-sm-2" type="search" aria-label="Search">
        <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>
      </form>
    </nav>




