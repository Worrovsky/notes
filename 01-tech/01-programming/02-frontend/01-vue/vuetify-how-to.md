## 1. Разметка

## 1.1 Layout`ы (стандартные СSS)

### 1.1.1 block vs inline

Все элементы - как прямоугольники (box)
Два основных типа:

* **block**
    - начинается с новой строки
    - занимает всю доступную ширину в контейнере
    - реагирует на свойства `height` и `width`
    - padding, границы, margin отталкивает другие элементы
    - пример: `div`, `p`
* **inline**
    - не выходит на новую строку
    - не реагирует на свойства `height` и `width`, под содержимое расширяется только
    - вертикальные padding, границы, margin применяются, но не отталкивают соседние элементы
    - горизонтальные - применяются и отталкивают соседние inline-элементы
    - пример: `a`, `span`

### 1.1.2 Свойство display

**display** - CSS свойство. Определяет поведение элемента во внешнем окружении (outer type) и поведение дочерних элементов (inner type)

Значения:

* `none` - не отображается элемент
* внешние (не влияют на дочерние элементы)
    - `block`
    - `inline` 
* внутренние
    - `flex` - сам элемент как box, дочерние - по модели flexbox
    - `table` - как `<table>`
    - `grid` - сам элемент как box, дочерние - по модели grid
* комбинации внешнего и внутреннего (в два слова - более новые стандарты)
    - `inline flex` (`inline-flex`): сам элемент как `inline`, дочерние - по модели `flex`
    - `inline-block`: смесь `inline` и `block`: нет переноса на новую строку, не растягивается, но можно задать размеры, отсупы.

### 1.1.3 Модель flexbox

#### 1.1.3.1 Основы flexbox

Это однонаправленная модель (в отличие от grid, где управляется положением элемента по двум осям).
Есть одна основная ось и есть дополнительная, перпендикулярная главной.

Основная ось задается свойством `flex-direction`:

* `row`
* `row-reverse`
* `column`
* `column-reverse`

Когда устанавливаем свойство `display: flex`:

* дочерние элементы отображаются в строку (`flex-direction = row`)
* элементы начинаются от начала главной оси (слева обычно)
* элементы не растягиваются по главной оси, но могут сжиматься
* по дополнительной оси растягиваются для заполнения пространства
* если элементы не помещаются, переноса не будет (см. свойство `wrap`)

#### 1.1.3.2 Свойство flex-wrap

По умолчанию - `nowrap`: элементы не переносятся на новые строки, если не помещаются
`wrap` - переносятся

#### 1.1.3.3 Свойства flex-basic, -grow, -shrink

Определяют размеры элемента в условиях наличия свободного пространства (в т. ч. отрицательного)

`flex-basic: auto` - размер элемента определяется значением `width`, если есть. А если нет - ???. В `flex-basic` может быть указана явная величина. В общем определили базовый размер элемента.

Затем вычисляется свободное пространство в контейнере. Если оно положительное делится между элементами пропорционально значению свойства **flex-grow**. Если оно 0 - не растет

Если отрицательное - элементы сжимаются аналогично по свойству **flex-shrink**

Есть короткая запись `flex: <grow> <shrink> <basic>`, например `flex: 0 1 auto`

#### 1.1.3.4 Свойство align-items

Выравнивает элементы по дополнительной оси:

* `stretch` - растягивает. Значение по умолчанию
* `flex-start` 
* `flex-end`
* `center`

#### 1.1.3.5 Свойство justify-content

Выравнивает элементы по основной оси:

* `flex-start`
* `flex-end`
* `center`
* `space-around` - 1/2 у краев контейнера, 1 часть между
* `space-between` - 0 у краев
* `space-evenly` - одинаковые у краев и между





## 1.2 Display и flexbox в Vuetify

### 1.2.1 Свойство display

[docs (styles/display)](https://vuetifyjs.com/en/styles/display/#visibility)

Задается через класс по шаблону `d-{breakpoint}-{value}`

`breakpoint`: xs, sm, md, lg, xl. (можно не указывать)
Применяются от указанного и выше, например:

    d-sm-none // не виден для любого размера
    d-sm-none d-md-flex // не виден только для малых, остальное - flex

`value`: none, inline, inline-block, block, table и дочерние, flex, inline-flex


Скрытие с особыми условиями: `hidden-{breakpoint}-condirion`, где условие `only`, `and-down` или `and-up`

### 1.2.2 flexbox

Варианты классов:

* `d-flex` 
* `d-inline-flex`
* `d-{breakpoint}-flex`
* `d-{breakpoint}-inline-flex`

Направление главной оси:

* `flex-row`
* `flex-column` 
* + `-reverse`
* + брейкпоинты

Выравнивание по основной оси:

* `justify-start`
* `justify-end`
* `justify-center`
* `justify-space-between`
* `justify-space-around`
*  + брейкпоинты

Выравнивание по дополнительной оси: 

* `align-start`
* `align-end`
* `align-center`
* `align-baseline`
* `align-stretch`
* + брейкпоинты

Выравнивание отдельного элемента вдоль главной оси (новое по сравнению с моделью CSS): 

* `align-self-start`
* `align-self-end`
* `align-self-center`
* `align-self-stretch`
* `align-self-auto`
* + брейкпоинты

Поведение при проверке уменьшаются элементы или нет:

* `flex-wrap`
* `flex-nowrap`
* `flex-wrap-reverse`
* + брейкпоинты

Сжатие/расширение (только 0 или 1)

* `flex-grow-0` / `flex-grow-1`
* `flex-shrink-0` / `flex-shrink-1`






## 1.3 Сетка (grid)

### 1.3.1 Основы

Основана на *flex-box*. Аналог bootsprap: 12 колонок, брейкпоинты

    <v-container>
        <v-row>
            <v-col></v-col>
            <v-spacer></v-spacer>
            <v-col></v-col>
        </v-row>
    <v-container>

#### 1.3.2 Container

Может иметь доп. атрибуты (`ma-*, pa-*`), распространяющиеся на дочерние элементы

**fluid** - по умолчанию на каждый брейкпоинт установлена максимальная ширина (отступы по бокам видны). *fluid* убирает ограничение (отступов не будет)

#### 1.3.3 Row

*gutter* (отступ) по умолчанию 24px между колонками

**dense** - уменьшает *gutter*
**no-gutter** - убирает

**align** - выравнивание дочерних элементов (вертикальное для обычного бокса). Допустимые значения: *start, end, center, stretch* (`style="height:200px"`)
**align-md** и т. п.

**justify** - выравнивание дочерних (горизонтальное для обычного). Допустимые значения: *start, end, center, space-between, space-around*
**justify-md** и т. п.


#### 1.3.4 Col

**cols** - определяет кол-во колонок (1-12, auto)
**sm**, **md**, **lg**, **xl** - *cols* для брейкпоинтов (*xs* - нет)
**offset** - смещение 
**offset-sm** и т. п.

#### 1.3.5 Spacer

Пространство между колонками


### 1.4 Spacing

применяются через  `class`: напр. `class="ma-5"`

`{property}{direction}-{size}`

Property:
* **m** - margin
* **p** - padding

Direction: 
* **t**, **b**, **l**, **r**
* **x**, **y**
* **a** - all

Size: 
* 0-12 (шаг 4 px)
* n0-n12 - отрицательные
* auto 

Есть варианты для брейкпоинтов

`{property}{direction}-{breakpoint}-{size}`

Breakpoint:
* **sm, md, lg, xl**
* **xs** нет смысла, равен без указания брейкпоинта

`ma-auto, mx-auto, my-auto` - это обычно выравнивание по центру


### 1.5 Elevation

Изменение по z-оси

Через свойство **elevation** (для некоторых элементов)
Или через класс **elevation**

Значения от 0 до 24






## 3. Разные компоненты

### 3.1 Список

#### 3.1.1 Основы

    <v-list>
        <v-list-group>...</v-list-group>
        <v-list-item>...</v-list-item>
    </v-list>    

Атрибуты:

* **nav** - уменьшает ширину, закругляет края (обычно с v-navigation-drawer)
* **dense** - 


#### 3.1.2 Элементы

    <v-list-item>
        <v-list-item-icon>...</v-list-item-icon>
        <v-list-item-content>...</v-list-item-content>
        <v-list-item-avatar>...</v-list-item-avatar>
        <v-list-item-action>...</v-list-item-action>
    </v-list-item>

#### 3.1.3 v-list-group

#### 3.1.4 v-list-item-icon

    <v-list-item-icon> 
        <v-icon>mdi-home</v-icon>
    </v-list-item-icon>

#### 3.1.5 v-list-item-content

    <v-list-item-content>
        <v-list-item-title>Заголовок</v-list-item-title>
        <v-list-item-subtitle>Подзаголовок</v-list-item-subtitle>
    </v-list-item-content>

#### 3.1.6 v-list-item-action

    <v-list-item-action>
        <v-list-item-action-text>...</v-list-item-action-text>
        <v-icon>...</v-icon>
    
        // или
        <v-btn icon>
            <v-icon>..</v-icon>
        </v-btn>
    
    </v-list-item-action>






### 3.2 Navigation-drawer

#### 3.2.1 Атрибуты

Варианты видимости:
* **permanent** - виден всегда, независимо от размера экрана
* **temporary** - виден как плавающая панель (аналог мобильного приложения)

**v-model** - (булево) определяет видимость в режиме **temporary**, обычно через кнопку с data-свойством связывают

**app** - указывает, что навигация - часть приложения
**right** - справа (нужен **absolute** или **app**)

**expand-on-hover** - переводит в мини-вариант, при наведении - раскрывает


##### 3.2.1.2 Mini

**mini-variant** - сжимает панель (до 56px по умолчанию), в элементах списков виден только первый элемент. Булево значение, можно завязывать на кнопку напр.

добавление модификатора `.sync` позволяет по нажатию раскрывать панель

Пример:

    <v-navigation-drawer
      :mini-variant.sync="mini"
      permanent
    >
    <v-list-item>
        <v-btn
          icon
          @click.stop="mini = !mini"
        >
          <v-icon>mdi-chevron-left</v-icon>
        </v-btn>
    <v-list-item>
    
    ...
    data: () => ({
        mini: false,
    })


**mini-variant-width** - ширина в сжатом состоянии

#### 3.2.2 Слоты

**append** - низ панели

     <template v-slot:append>
        <div class="pa-5">
          <v-btn block>Logout</v-btn>
        </div>
      </template>

**prepend** - верх

### 3.3 App bar

**v-app-bar**
Панель с действиями (обычно верхняя). Обычно располагается внутри **v-app**

#### 3.3.1 Дочерние элементы

**v-app-bar-nav-icon** - гамбургер-кнопка
обычно для открытия панелей `navigation drawler`

    <v-app-bar-nav-icon @click="drawer = true"></v-app-bar-nav-icon>


**v-toolbar-title** - заголовок


#### 3.3.2 Свойства

**app** - обозначает бар как часть макета приложения. (располагается по умолчанию вверху, фиксированая высота)

Высота бара:
* **short** - 56px (по умолчанию)
* **dense** - 48px

**prominent** - удваивает высоту (с *dense* - 96px, c *short* - 112px, без свойств - 128)

**shrink-on-scroll** - сжимает бар со свойством *promitent* при прокрутке (к *short* или *dense*). Также автоматически применяет свойство *promitent* если нет
**hide-on-scroll** - скрывает при прокрутке
**collapse** - сворачивает бар по ширине (первые 2 элемента остаются видны ???)
**collapse-on-scroll** - сворачивает при прокрутке