# Содержание 

<!-- MarkdownTOC levels="2,3,4" autolink="true" -->

- [1. Работа с путями](#1-%D0%A0%D0%B0%D0%B1%D0%BE%D1%82%D0%B0-%D1%81-%D0%BF%D1%83%D1%82%D1%8F%D0%BC%D0%B8)
    - [1.0 Теория](#10-%D0%A2%D0%B5%D0%BE%D1%80%D0%B8%D1%8F)
    - [1.1 Интерфейс Path](#11-%D0%98%D0%BD%D1%82%D0%B5%D1%80%D1%84%D0%B5%D0%B9%D1%81-path)
    - [1.2 Создание объекта Path](#12-%D0%A1%D0%BE%D0%B7%D0%B4%D0%B0%D0%BD%D0%B8%D0%B5-%D0%BE%D0%B1%D1%8A%D0%B5%D0%BA%D1%82%D0%B0-path)
    - [1.3 Получение информации о пути](#13-%D0%9F%D0%BE%D0%BB%D1%83%D1%87%D0%B5%D0%BD%D0%B8%D0%B5-%D0%B8%D0%BD%D1%84%D0%BE%D1%80%D0%BC%D0%B0%D1%86%D0%B8%D0%B8-%D0%BE-%D0%BF%D1%83%D1%82%D0%B8)
    - [1.4 Удаление избыточности](#14-%D0%A3%D0%B4%D0%B0%D0%BB%D0%B5%D0%BD%D0%B8%D0%B5-%D0%B8%D0%B7%D0%B1%D1%8B%D1%82%D0%BE%D1%87%D0%BD%D0%BE%D1%81%D1%82%D0%B8)
    - [1.5 Конвертация путей](#15-%D0%9A%D0%BE%D0%BD%D0%B2%D0%B5%D1%80%D1%82%D0%B0%D1%86%D0%B8%D1%8F-%D0%BF%D1%83%D1%82%D0%B5%D0%B9)
    - [1.6 Объединение путей](#16-%D0%9E%D0%B1%D1%8A%D0%B5%D0%B4%D0%B8%D0%BD%D0%B5%D0%BD%D0%B8%D0%B5-%D0%BF%D1%83%D1%82%D0%B5%D0%B9)
    - [1.7 Нахождение пути между двумя путями](#17-%D0%9D%D0%B0%D1%85%D0%BE%D0%B6%D0%B4%D0%B5%D0%BD%D0%B8%D0%B5-%D0%BF%D1%83%D1%82%D0%B8-%D0%BC%D0%B5%D0%B6%D0%B4%D1%83-%D0%B4%D0%B2%D1%83%D0%BC%D1%8F-%D0%BF%D1%83%D1%82%D1%8F%D0%BC%D0%B8)
    - [1.8 Сравнение путей](#18-%D0%A1%D1%80%D0%B0%D0%B2%D0%BD%D0%B5%D0%BD%D0%B8%D0%B5-%D0%BF%D1%83%D1%82%D0%B5%D0%B9)
- [2. Работа с файлами](#2-%D0%A0%D0%B0%D0%B1%D0%BE%D1%82%D0%B0-%D1%81-%D1%84%D0%B0%D0%B9%D0%BB%D0%B0%D0%BC%D0%B8)
    - [2.1 Общие сведения](#21-%D0%9E%D0%B1%D1%89%D0%B8%D0%B5-%D1%81%D0%B2%D0%B5%D0%B4%D0%B5%D0%BD%D0%B8%D1%8F)
        - [2.1.0 Пакет](#210-%D0%9F%D0%B0%D0%BA%D0%B5%D1%82)
        - [2.1.1 Закрытие ресурсов](#211-%D0%97%D0%B0%D0%BA%D1%80%D1%8B%D1%82%D0%B8%D0%B5-%D1%80%D0%B5%D1%81%D1%83%D1%80%D1%81%D0%BE%D0%B2)
        - [2.1.2 Атомарность операций](#212-%D0%90%D1%82%D0%BE%D0%BC%D0%B0%D1%80%D0%BD%D0%BE%D1%81%D1%82%D1%8C-%D0%BE%D0%BF%D0%B5%D1%80%D0%B0%D1%86%D0%B8%D0%B9)
        - [2.1.3 Цепочки методов](#213-%D0%A6%D0%B5%D0%BF%D0%BE%D1%87%D0%BA%D0%B8-%D0%BC%D0%B5%D1%82%D0%BE%D0%B4%D0%BE%D0%B2)
        - [2.1.4 Glob](#214-glob)
        - [2.1.5 Работа с симлинками](#215-%D0%A0%D0%B0%D0%B1%D0%BE%D1%82%D0%B0-%D1%81-%D1%81%D0%B8%D0%BC%D0%BB%D0%B8%D0%BD%D0%BA%D0%B0%D0%BC%D0%B8)
    - [2.2 Проверка файла или директории](#22-%D0%9F%D1%80%D0%BE%D0%B2%D0%B5%D1%80%D0%BA%D0%B0-%D1%84%D0%B0%D0%B9%D0%BB%D0%B0-%D0%B8%D0%BB%D0%B8-%D0%B4%D0%B8%D1%80%D0%B5%D0%BA%D1%82%D0%BE%D1%80%D0%B8%D0%B8)
    - [2.3 Удаление файлов/директорий](#23-%D0%A3%D0%B4%D0%B0%D0%BB%D0%B5%D0%BD%D0%B8%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D0%BE%D0%B2%D0%B4%D0%B8%D1%80%D0%B5%D0%BA%D1%82%D0%BE%D1%80%D0%B8%D0%B9)
    - [2.4 Копирование файлов](#24-%D0%9A%D0%BE%D0%BF%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D0%BE%D0%B2)
    - [2.5 Перемещение файлов](#25-%D0%9F%D0%B5%D1%80%D0%B5%D0%BC%D0%B5%D1%89%D0%B5%D0%BD%D0%B8%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D0%BE%D0%B2)
    - [2.6 Метаданные \(атрибуты\) файлы](#26-%D0%9C%D0%B5%D1%82%D0%B0%D0%B4%D0%B0%D0%BD%D0%BD%D1%8B%D0%B5-%D0%B0%D1%82%D1%80%D0%B8%D0%B1%D1%83%D1%82%D1%8B-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B)
        - [2.6.1](#261)
        - [2.6.2 Получение атрибутов через классы](#262-%D0%9F%D0%BE%D0%BB%D1%83%D1%87%D0%B5%D0%BD%D0%B8%D0%B5-%D0%B0%D1%82%D1%80%D0%B8%D0%B1%D1%83%D1%82%D0%BE%D0%B2-%D1%87%D0%B5%D1%80%D0%B5%D0%B7-%D0%BA%D0%BB%D0%B0%D1%81%D1%81%D1%8B)
        - [2.6.3 FileStore](#263-filestore)
    - [2.7 Создание, чтение, запись в файлы](#27-%D0%A1%D0%BE%D0%B7%D0%B4%D0%B0%D0%BD%D0%B8%D0%B5-%D1%87%D1%82%D0%B5%D0%BD%D0%B8%D0%B5-%D0%B7%D0%B0%D0%BF%D0%B8%D1%81%D1%8C-%D0%B2-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B)
        - [2.7.1 Параметр OpenOptions](#271-%D0%9F%D0%B0%D1%80%D0%B0%D0%BC%D0%B5%D1%82%D1%80-openoptions)
        - [2.7.2 Методы для работы с небольшими файлами](#272-%D0%9C%D0%B5%D1%82%D0%BE%D0%B4%D1%8B-%D0%B4%D0%BB%D1%8F-%D1%80%D0%B0%D0%B1%D0%BE%D1%82%D1%8B-%D1%81-%D0%BD%D0%B5%D0%B1%D0%BE%D0%BB%D1%8C%D1%88%D0%B8%D0%BC%D0%B8-%D1%84%D0%B0%D0%B9%D0%BB%D0%B0%D0%BC%D0%B8)
        - [2.7.3 Буферизированный ввод/вывод в текстовые файлы](#273-%D0%91%D1%83%D1%84%D0%B5%D1%80%D0%B8%D0%B7%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%BD%D1%8B%D0%B9-%D0%B2%D0%B2%D0%BE%D0%B4%D0%B2%D1%8B%D0%B2%D0%BE%D0%B4-%D0%B2-%D1%82%D0%B5%D0%BA%D1%81%D1%82%D0%BE%D0%B2%D1%8B%D0%B5-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B)
        - [2.7.4 Небуферизированный ввод/вывод \(Input/OutputStream\)](#274-%D0%9D%D0%B5%D0%B1%D1%83%D1%84%D0%B5%D1%80%D0%B8%D0%B7%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%BD%D1%8B%D0%B9-%D0%B2%D0%B2%D0%BE%D0%B4%D0%B2%D1%8B%D0%B2%D0%BE%D0%B4-inputoutputstream)
        - [2.7.5 Методы для каналов и ByteBuffers](#275-%D0%9C%D0%B5%D1%82%D0%BE%D0%B4%D1%8B-%D0%B4%D0%BB%D1%8F-%D0%BA%D0%B0%D0%BD%D0%B0%D0%BB%D0%BE%D0%B2-%D0%B8-bytebuffers)
        - [2.7.6 Создание регулярных и временных файлов](#276-%D0%A1%D0%BE%D0%B7%D0%B4%D0%B0%D0%BD%D0%B8%D0%B5-%D1%80%D0%B5%D0%B3%D1%83%D0%BB%D1%8F%D1%80%D0%BD%D1%8B%D1%85-%D0%B8-%D0%B2%D1%80%D0%B5%D0%BC%D0%B5%D0%BD%D0%BD%D1%8B%D1%85-%D1%84%D0%B0%D0%B9%D0%BB%D0%BE%D0%B2)
    - [2.8 Произвольные чтение/запись в файлы](#28-%D0%9F%D1%80%D0%BE%D0%B8%D0%B7%D0%B2%D0%BE%D0%BB%D1%8C%D0%BD%D1%8B%D0%B5-%D1%87%D1%82%D0%B5%D0%BD%D0%B8%D0%B5%D0%B7%D0%B0%D0%BF%D0%B8%D1%81%D1%8C-%D0%B2-%D1%84%D0%B0%D0%B9%D0%BB%D1%8B)
    - [2.9 Работа с директориями](#29-%D0%A0%D0%B0%D0%B1%D0%BE%D1%82%D0%B0-%D1%81-%D0%B4%D0%B8%D1%80%D0%B5%D0%BA%D1%82%D0%BE%D1%80%D0%B8%D1%8F%D0%BC%D0%B8)
        - [2.9.1 Получение корневых директорий](#291-%D0%9F%D0%BE%D0%BB%D1%83%D1%87%D0%B5%D0%BD%D0%B8%D0%B5-%D0%BA%D0%BE%D1%80%D0%BD%D0%B5%D0%B2%D1%8B%D1%85-%D0%B4%D0%B8%D1%80%D0%B5%D0%BA%D1%82%D0%BE%D1%80%D0%B8%D0%B9)
        - [2.9.2 Создание директорий](#292-%D0%A1%D0%BE%D0%B7%D0%B4%D0%B0%D0%BD%D0%B8%D0%B5-%D0%B4%D0%B8%D1%80%D0%B5%D0%BA%D1%82%D0%BE%D1%80%D0%B8%D0%B9)
        - [2.9.3 Создание временных директорий](#293-%D0%A1%D0%BE%D0%B7%D0%B4%D0%B0%D0%BD%D0%B8%D0%B5-%D0%B2%D1%80%D0%B5%D0%BC%D0%B5%D0%BD%D0%BD%D1%8B%D1%85-%D0%B4%D0%B8%D1%80%D0%B5%D0%BA%D1%82%D0%BE%D1%80%D0%B8%D0%B9)
        - [2.9.4 Просмотр содержимого директории](#294-%D0%9F%D1%80%D0%BE%D1%81%D0%BC%D0%BE%D1%82%D1%80-%D1%81%D0%BE%D0%B4%D0%B5%D1%80%D0%B6%D0%B8%D0%BC%D0%BE%D0%B3%D0%BE-%D0%B4%D0%B8%D1%80%D0%B5%D0%BA%D1%82%D0%BE%D1%80%D0%B8%D0%B8)
    - [2.10 Работа с ссылками](#210-%D0%A0%D0%B0%D0%B1%D0%BE%D1%82%D0%B0-%D1%81-%D1%81%D1%81%D1%8B%D0%BB%D0%BA%D0%B0%D0%BC%D0%B8)
    - [2.11 Обход дерева директории](#211-%D0%9E%D0%B1%D1%85%D0%BE%D0%B4-%D0%B4%D0%B5%D1%80%D0%B5%D0%B2%D0%B0-%D0%B4%D0%B8%D1%80%D0%B5%D0%BA%D1%82%D0%BE%D1%80%D0%B8%D0%B8)
    - [2.12 Поиск файлов через PathMatcher](#212-%D0%9F%D0%BE%D0%B8%D1%81%D0%BA-%D1%84%D0%B0%D0%B9%D0%BB%D0%BE%D0%B2-%D1%87%D0%B5%D1%80%D0%B5%D0%B7-pathmatcher)
    - [2.13 Разные методы работы с файловыми системами](#213-%D0%A0%D0%B0%D0%B7%D0%BD%D1%8B%D0%B5-%D0%BC%D0%B5%D1%82%D0%BE%D0%B4%D1%8B-%D1%80%D0%B0%D0%B1%D0%BE%D1%82%D1%8B-%D1%81-%D1%84%D0%B0%D0%B9%D0%BB%D0%BE%D0%B2%D1%8B%D0%BC%D0%B8-%D1%81%D0%B8%D1%81%D1%82%D0%B5%D0%BC%D0%B0%D0%BC%D0%B8)

<!-- /MarkdownTOC -->



# Работа с файловой системой (NIO.2)

[oracle tutorial](https://docs.oracle.com/javase/tutorial/essential/io/fileio.html)


## 1. Работа с путями

### 1.0 Теория

Что такое путь: представление файлы/директории в иерархии файловой системы

**Абсолютный путь** - начинается с корневого элемента файловой системы (`/` - для UNIX, `C:\` и т. п. для Win)

**Относительный путь** - не содержит корневого элемента, для доступа к файлу нужно объединять с другим абсолютным путем

**Символические ссылки** - элемент файловой системы (как и папки, файлы), указывает на другой элемент ФС (в т. ч. другую симссылку). С точки зрения приложений чтение/запись в симссылку равноценна чтению/записи напрямую в файл. Но переименование, удаление влияет применяется только для ссылки

### 1.1 Интерфейс Path

`java.nio.file.Path`

представляет путь в файловой системе

зависит от операционной системы

никак не связан с наличием файла, на который указывает. Для проверки существования, удаления и других операций используется класс `Files`

Методы класса позволяют создавать, модифицировать и т. п. пути. 

### 1.2 Создание объекта Path

Через метод **Paths.get()**

    Path p = Paths.get("tmp/foo");
    // комбинацией частей:
    Path p2 = Paths.get(System.getProperty("user.home"), "logs", "foo.log");

Синоним для 

    Path p3 = FileSystems.getDefault().getPath(".....");

Есть аналог **Path.of()**

### 1.3 Получение информации о пути

Путь представлен как массив составляющих его элементов. Поэтому доступны следующие методы:

    getNameCount() - возвращает количество элементов в пути
    getFileName() - возвращает последний элемент в пути
    getRoot()
    getParent()
    subpath(0, 2

Методы платформозависимы. Т. е. если создаем путь в синтаксисе Win и запускаем в Linux, не сможет определить root, составные части и т. п.

### 1.4 Удаление избыточности 

Например путь может содержать `.` или `..`. Чтобы избавиться от этого - метод **normalize()**
    
    Path p1 = Paths.get("/home/./joe/foo");
    Path p2 = Paths.get("/home/sally/../joe/foo");
    p1 = p1.normalize(); // теперь /home/joe/foo
    p2 = p1.normalize(); // теперь /home/joe/foo

Этот метод также не проверяет / никак не взаимодействует с реальным файлом

### 1.5 Конвертация путей

**toUri()** 

    Path p = Paths.get("/home/foo");
    p.toUri(); // file:///home/foo

**toAbsolutePath()** - преобразует в абсолютный путь (обычно присоединением текущей рабочей директории)

**toRealPath()** - удаляет избыточность, преобразует в абсолютный путь. Можно передать параметром, как обрабатывать симлинки. Проверяет наличие файла в системе. Выбрасывает исключение, если файла нет или ошибки доступа

### 1.6 Объединение путей

метод **resolve()**

можно передать в него частичный путь, объединяется с исходным. Если передан абсолютный, он же и возвращается

    Path p = Paths.get("/home/foo");
    p.resolve("bar"); // "/home/foo/bar"
    p.resolve("/home/bar"); // "/home/bar"

### 1.7 Нахождение пути между двумя путями

метод **relativize()** - ответ на вопрос как из одного пути попасть в другой

    Path p1 = Paths.get("home");
    Path p2 = Paths.get("home/sally/bar");
    Path p1_to_p2 = p1.relativize(p2); // "sally/bar"
    Path p2_to_p1 = p1.relativize(p1); // "../.."

обратный метод для `resolve()`

Выбрасывает непроверяемые исключения, если нельзя построить путь (например разные корневые элементы (win) или только один путь содержит корневой)

### 1.8 Сравнение путей

**path.equals(otherPath)**
**path.startsWith(otherPath)**
**path.endsWith(otherPath)**

Можно итерироваться по частям пути

    for (Parh p: path) { ... }



## 2. Работа с файлами

### 2.1 Общие сведения

#### 2.1.0 Пакет

java.nio.file.Files

#### 2.1.1 Закрытие ресурсов

Большинство ресурсов для работы с файлами реализуют `Closeable`. Соответственно должны закрываться при окончании работы.

Это или `try-with-resource`, или ручное закрытие в блоке `finally{}`

Методы файловой системы генерируют **IOException**.

Или может быть более специфичный **FileSysteException** с дополнительными методами `getFile()`, `getReason()` и т. п. 

#### 2.1.2 Атомарность операций

Некоторые операции, напр. `move()` атомарны в некоторых ОС

#### 2.1.3 Цепочки методов

Часто используется в файловых методах

    String value = Charset.defaultCharset().decode(buf).toString();

#### 2.1.4 Glob

Glob - шаблон для определения соответствия

* `*` - любое количество символом (пустое в т. ч.)
* `**` - то же, но с переходом между директориями
* `?` - один символ
* `{}` - набор шаблонов через зпт
* `[]` - набор символов или диапазон через `-`. `*?\` внутри просто символы без доп. смысла
* специальные символы могут экранироваться `\`

#### 2.1.5 Работа с симлинками

Каждый метод либо объявляет, как он обрабатывает симлинки, либо регулирует это через входные параметры



### 2.2 Проверка файла или директории

Есть экземпляр `Path`, что это?

**exists(Path p)** - true если файл существует, false - если нет или невозможно определить. Дополнительно параметрами можно определить переходить по симлинкам или нет

**notExists(Path p)** - аналог для проверки отсутсвия файла. Не комплементарны. Оба могут возвращать false (т. е. не могут проверить файл)

Разные методы проверки. Проверяют существование файла и затем дальше проверят. Отрицательный результат может быть когда файл не существует или нет прав и т. п.:

**isDirectory(Path p)** - проверка на директорию
**isReadable(Path p)**
**isWritable(Path p)**
**isExecutable(Path p)**

**isSameFile(p1, p2)** - проверка что это один и тот же файл (для симлинков например имеет смысл)

### 2.3 Удаление файлов/директорий

**delete(Path)** - должен существовать, директория должна быть пустой. Иначе исключения генерируются

**deleteIfExists(Path)** - без исключения, если не существует


### 2.4 Копирование файлов

**copy(Path from, Path to, CopyOption...)**

Директории тоже можно, но содержимое при этом не копируется
Если уже существует - исключение

Опции: 

* **REPLACE_EXISTING** - копирует даже, если файл существует. Если цель - не пустая директория, тогда исключение
* **COPY_ATTRIBUTES** - копирует атрибуты файла
* **NOFOLLOW_LINKS** - симлинки копируются сами, а не их содержимое

Есть методы для копирования в потоки / из потоков:

* `copy(InputStream, Path)`
* `copy(Path, OutputStream)`

### 2.5 Перемещение файлов

**move(Path from, Path to, CopyOption...)**

Пустые директории перемещаются без ограничений. Не пустые - зависит от. Например в Unix перемещение в пределах раздела ФС это просто переименование, будет работать метод `move` 

Опции:

* **REPLACE_EXISTING** - перемещает, даже если целевой файл существует. Без этой опции при перемещении в существующий файл - исключение
* **ATOMIC_MOVE** - перемещение в атомарной операции. Если ФС не поддерживает (или например перемещение между разделами, что требует копирования) - исключение


### 2.6 Метаданные (атрибуты) файлы

#### 2.6.1 

Разные методы:

* **size(Path)**
* **isDirectory(Path)**
* **isRegularFile(Path)**
* **isSymbolicLink(Path)**
* **isHidden(Path)**
* **get/setLastModifiedTime(Path)**
* **get/setOwner(Path)**
* **get/setAttribute(Path, ...)**
* **get/setAttributes(Path, ...)** - повышение производительности для установки нескольких атрибутов

Какие атрибуты есть у файлов, определяется файловой системой. Поэтому атрибуты разделены на группы

#### 2.6.2 Получение атрибутов через классы

Общий шаблон

    A attrs = Files.readAttributes(path, A.class);

Например:

    BasicFilesAttributes attr = Files.readAttributes(path, BasicFilesAttributes.class);
    attr.creationTime();
    // и др. методы

Другие классы:

* **PosixFileAttributes**
* **DosFileAttributes**

#### 2.6.3 FileStore

Класс для получения информации о диске/разделе, где размешен файл.

    Path p = ...;
    FileStore fs = Files.getFileStore(p);
    long total = fs.getTotalSpace();
    long usage = fs.getUsableSpace();

### 2.7 Создание, чтение, запись в файлы

#### 2.7.1 Параметр OpenOptions

Есть у некоторых методов

* **WRITE** - открытие файла для записи
* **APPEND** - добавление данных в конец файла. Вместе с **WRITE** или **CREATE**
* **TRUNCATE_EXISTING** - обрезать файл до 0 байт. Вместе с **WRITE**
* **CREATE_NEW** - создание нового файла, исключение если уже есть
* **CREATE** - создание нового файла или открытие существующего
* **DELETE_ON_CLOSE** - удалять при закрытии (для временных файлов)
* **SYNC** - синхронизировать содержимое и метаданные с устройством
* **DSYNC** - синхронизировать содержимое с устройством

#### 2.7.2 Методы для работы с небольшими файлами

**readAllBytes(Path)**
**readAllLines(Path, Charset)**
**write(Path, byte[], OpenOption...)**
**write(Path, Iterable<? extends CharSequence>, OpenOption...)**

    Path file = ...;
    byte[] fileArray;
    fileArray = Files.readAllBytes(file);

#### 2.7.3 Буферизированный ввод/вывод в текстовые файлы

**newBufferedReader(Path, Charset)**

    Charset charset = Charset.forName("US-ASCII");
    try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
        String line = null;
        while((line = reader.readLine() != null)) {
            ...
        }
    } catch (IOException x) { ... }

**newBufferedWriter(Path, Charset, OpenOption...)** - запись через BufferedWriter

#### 2.7.4 Небуферизированный ввод/вывод (Input/OutputStream)

**InputStream newInputStream(Path, OpenOption...)**
**OutputStream newOutputStream(Path, OpenOption...)**

    Path p = Paths.get("log.txt");
    String s = "adfx";
    byte[] data = s.getBytes();
    try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(p, CREATE, APPEND))) {
        out.write(data, 0, data.lenght);
    } catch (...) { ... }


#### 2.7.5 Методы для каналов и ByteBuffers

что-то для работы с интерфейсом **ByteChannel** ...

#### 2.7.6 Создание регулярных и временных файлов

**createFile(Path, FileAttribute<?>)**

В атомарной операции проверяет существование файла и создает. Если уже есть такой файл - вызывает исключение.

Разрешения на доступ (это атрибуты файла) создаются по умолчанию, но можно задавать

Другой способ - через метод **newOutputStream**: открытие и закрытие потока создаст файл

Создание временных файлов:

* **createTempFile(Path p, String preffix, String Suffix, FileAttributes)** - в заданной директории
* **createTempFile(String preffix, String Suffix, FileAttributes)** - в директории по умолчанию

Префикс и суффикс могут быть `null`. Возвращают `Path` для созданного файла

### 2.8 Произвольные чтение/запись в файлы

Интерфейс **SeekableByteChannel**:

* `position()` - возвращает/устанавливает текущую позицию курсора
* `read(ByteBuffer)` - читает в буфер
* `write(ByteBuffer)` - записывает из буфера в канал
* `truncate(long)` - обрезает файл до указанного размера

Получить можно

    ByteChannel bc = Files.newByteChannel(Path, OpenOption...);

Другой вариант - класс **FileChannel**. Реализация `SeekableByteChannel` с расширенным набором методов

    FileChannel fc = FileChannel.open(Path, OpenOption...);


### 2.9 Работа с директориями

#### 2.9.1 Получение корневых директорий 

    Iterable<Path> dirs = FileSystems.getDefault().getRootDirectories();
    for (Path p: dirs) {
        System.out.println(p);
    }

#### 2.9.2 Создание директорий

**createDirectory(Path)** - создает директорию без промежуточных директорий
**createDirectories(Path)** - с промежуточными

Вот пример с указанием прав доступа

    Set<PosixFilePermission> perms =  
            PosixFilePermissions.fromString("rwxr-x---");
    FileAttribute<Set<PosixFilePermission>> attr =
            PosixFilePermissions.asFileAttribute(perms);
    Files.createDirectory(file, attr);

#### 2.9.3 Создание временных директорий

**createTempDirectory(Path p, String preffix, FileAttribute attrs)** - в указанной директории
**createTempDirectory(String preffix, FileAttribute attrs)** - в директории для временных файлов

#### 2.9.4 Просмотр содержимого директории

##### 2.9.4.1 Метод newDirectoryStream 

**newDirectoryStream(Path)** - создает поток **DirectoryStream** для обхода содержимого. Также является `Iterable`. Обход не рекурсивный.

Как и любой поток, не забыть закрыть (или try-with-resourses)

    Path cwd = Paths.get("").toAbsolutePath();
    try (DirectoryStream<Path> paths = Files.newDirectoryStream(cwd)) {
        for (Path currPath : paths) {
            System.out.println(currPath);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

##### 2.9.4.2 Фильтр через глоб

    String pattern = "*.{java,class,jar}";
    DirectoryStream<Path> = Files.newDirectoryStream(path, pattern);

##### 2.9.4.3 Собственный фильтр

    DirectoryStream.Filter<Path> filter = newDirectoryStream.Filter<Path>() {
        public boolean accept(Path file) throws IOException {
            try {
                return Files.isDirectory(path);
            } catch(IOException e) {
                return false;
            }
        }
    };
    DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter);

### 2.10 Работа с ссылками

Два вида: symbolic(soft) и regular(hard):

* цель, куда указывает hard-ссылка должен существовать
* hard-ссылки не разрешены на директории
* hard-ссылки не могут указывать на файлы на других томах/разделах
* для окружения hard-ссылка - как оригинальный файл

Используются реже, чем symbolic

**createSymbolicLink(Path, Path)** - создает символическую ссылку
**createLink(Path, Path)** - создает hard-ссылку. Если целевой файл не существует - исключение

**isSymbolicLink(Path)** - проверка

**readSymbolicLink(Path)** - получение файла/директории, на которые указывает ссылка


### 2.11 Обход дерева директории

интерфейс **FileVisitor**

* `preVisitDirectory` - до захода в директорию
* `postVisitDirectory` - после обхода директории
* `visitFile` 
* `visitFileFailed` - при ошибках. В метод передается исключение, можно его обработать

Можно не реализовывать, а унаследовать класс **SimpleFileVisitor** (реализует `FileVisitor`, при ошибках выбрасывает `IOException`)

После того, как готова реализация `FileVisitor`, нужно запустить обход:

* **walkFileTree(Path, FileVisitor)**
* **walkFileTree(Path, Set<FileVisitOption>, int, FileVisitor)** - дополнительно можно указать глубину обхода (Integer.MAX_VALUE - без ограничений) и опции обхода (например как обрабатывать ссылки)

Общие правила обхода:

* обход - сначала в глубину, но порядок не известен
* если рекурсивное удаление: сначала удаляем файлы, затем директории в `postVisitDirectory`
* копирование: создаем директории в `preVisitDirectory`, файлы копируем в `visitFile`
* поиск: сравнение в `visitFile`, если еще и директории ищем - тогда в `pre-` или `postVisitDirectory`
* по умолчанию по ссылкам нет перехода. Но если запущен с опцией `FOLLOW_LINKS` и есть зацикливание, будет вызвано `FileSystemLoopException`

Контроль потока:

* Обход управляется значением **FileVisitResult**, которое возвращают методы интерфейса `FileVisitor`
* **CONTINUE** - обычный обход
* **TERMINATE** - прерывает обход
* **SKIP_SUBTREE** - текущая директория и ее дочерние не обходятся
* **SKIP_SIBLING** - текущая директория и ее дочерние не обходятся




### 2.12 Поиск файлов через PathMatcher

    String pattern = "*.{java,class}";
    PathMatcher matcher = FileSystem.getDefault().getPathMatcher("glob:" + pattern);
    Path p = ...;
    boolean b = matcher.match(p);

Здесь через метод **getPathMatcher()** получаем **PathMatcher** с указанием шаблона через префикс "glob". Далее обход дерева и проверка соответствия

### 2.13 Разные методы работы с файловыми системами

Получаем файловую систему 

    FileSystems.getDefault() 

У нее разные методы

Получение разделителя путей

    String sep = FileSystems.getDefault().getSeparator();

Получение корневых директорий (`/` или `c:\` и т. п.)

    Iterable<Path> roots = FileSystems.getDefault().getRootDirectories();

Получение файловых хранилищ (file store). Это разное: тома, диски, служебные и т.п. 

    Iterable<FileStore> fileStores = FileSystems.getDefault().getFileStores();

Или для конкретного пути

    Path p = ...;
    FileStore fs = Files.getFileStore(p);


