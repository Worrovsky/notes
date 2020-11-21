## 1. Базовые

### 1.1 Список

#### 1.1.1

    <v-list>
        <v-list-group>...</v-list-group>
        <v-list-item>...</v-list-item>
    </v-list>    

Атрибуты:

* **nav** - уменьшает ширину, закругляет края (обычно с v-navigation-drawer)
* **dense** - 


#### 1.1.2

    <v-list-item>
        <v-list-item-icon>...</v-list-item-icon>
        <v-list-item-content>...</v-list-item-content>
        <v-list-item-avatar>...</v-list-item-avatar>
        <v-list-item-action>...</v-list-item-action>
    </v-list-item>

#### 1.1.3 v-list-group

#### 1.1.4 v-list-item-icon

    <v-list-item-icon> 
        <v-icon>mdi-home</v-icon>
    </v-list-item-icon>

#### 1.1.5 v-list-item-content

    <v-list-item-content>
        <v-list-item-title>Заголовок</v-list-item-title>
        <v-list-item-subtitle>Подзаголовок</v-list-item-subtitle>
    </v-list-item-content>

#### 1.1.6 v-list-item-action

    <v-list-item-action>
        <v-list-item-action-text>...</v-list-item-action-text>
        <v-icon>...</v-icon>
    
        // или
        <v-btn icon>
            <v-icon>..</v-icon>
        </v-btn>
    
    </v-list-item-action>





### 1.2 Сетка (grid)

#### 1.2.1 Основы

Основана на *flex-box*. Аналог bootsprap: 12 колонок, брейкпоинты

    <v-container>
        <v-row>
            <v-col></v-col>
            <v-spacer></v-spacer>
            <v-col></v-col>
        </v-row>
    <v-container>

#### 1.2.2 Container

Может иметь доп. атрибуты (`ma-*, pa-*`), распространяющиеся на дочерние элементы

**fluid** - по умолчанию на каждый брейкпоинт установлена максимальная ширина (отступы по бокам видны). *fluid* убирает ограничение (отступов не будет)

#### 1.2.3 Row

*gutter* (отступ) по умолчанию 24px между колонками

**dense** - уменьшает *gutter*
**no-gutter** - убирает

**align** - выравнивание дочерних элементов (вертикальное для обычного бокса). Допустимые значения: *start, end, center, stretch* (`style="height:200px"`)
**align-md** и т. п.

**justify** - выравнивание дочерних (горизонтальное для обычного). Допустимые значения: *start, end, center, space-between, space-around*
**justify-md** и т. п.


#### 1.2.4 Col

**cols** - определяет кол-во колонок (1-12, auto)
**sm**, **md**, **lg**, **xl** - *cols* для брейкпоинтов (*xs* - нет)
**offset** - смещение 
**offset-sm** и т. п.

#### 1.2.5 Spacer

Пространство между колонками


### 1.3 Spacing

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


### 1.4 Elevation

Изменение по z-оси

Через свойство **elevation** (для некоторых элементов)
Или через класс **elevation**

Значения от 0 до 24

## 2. Компоненты

### 2.1 Navigation-drawer

#### 2.1.1 Атрибуты

Варианты видимости:
* **permanent** - виден всегда, независимо от размера экрана
* **temporary** - виден как плавающая панель (аналог мобильного приложения)

**v-model** - (булево) определяет видимость в режиме **temporary**, обычно через кнопку с data-свойством связывают

**app** - указывает, что навигация - часть приложения
**right** - справа (нужен **absolute** или **app**)

**expand-on-hover** - переводит в мини-вариант, при наведении - раскрывает


##### 2.1.1.2 Mini

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

#### 2.1.2 Слоты

**append** - низ панели

     <template v-slot:append>
        <div class="pa-5">
          <v-btn block>Logout</v-btn>
        </div>
      </template>

**prepend** - верх

### 2.2 App bar

**v-app-bar**
Панель с действиями (обычно верхняя). Обычно располагается внутри **v-app**

#### 2.2.1 Дочерние элементы

**v-app-bar-nav-icon** - гамбургер-кнопка
обычно для открытия панелей `navigation drawler`

    <v-app-bar-nav-icon @click="drawer = true"></v-app-bar-nav-icon>


**v-toolbar-title** - заголовок


#### 2.2.2 Свойства

**app** - обозначает бар как часть макета приложения. (располагается по умолчанию вверху, фиксированая высота)

Высота бара:
* **short** - 56px (по умолчанию)
* **dense** - 48px

**prominent** - удваивает высоту (с *dense* - 96px, c *short* - 112px, без свойств - 128)

**shrink-on-scroll** - сжимает бар со свойством *promitent* при прокрутке (к *short* или *dense*). Также автоматически применяет свойство *promitent* если нет
**hide-on-scroll** - скрывает при прокрутке
**collapse** - сворачивает бар по ширине (первые 2 элемента остаются видны ???)
**collapse-on-scroll** - сворачивает при прокрутке