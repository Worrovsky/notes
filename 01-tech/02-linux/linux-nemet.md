## Гл 1. Основы 

### 1.1 Обозначения

`$` - обычный, непривилегированный
`#` - суперпользователь

Элементы синтаксиса:

* `[]` - необязательные элементы
* `...` - можно повторять
* `{..|..}` - на выбор

`*` - любое количество символов
`?` - один символ


### 1.2 man

    man заголовок
    man раздел заголовок
    man -k ключевое_слово

файлы хранятся в `usr/local/man`


## Гл. 2. Загрузка и демоны

### 2.6 Демоны

демон с PID=1 - **init** или **systemd** (новее)
процессы, обозначаемые [] - системные, запускаются ядром


### 2.6.1 Обязанности демона init

Отвечает за запуск демонов, служб в любой момент времени

Основные режимы:

* Однопользовательский: минимальный набор файловых систем, корневая оболочка на консоли
* Многопользовательский: монтируются файловые системы, сетевые службы, оконная система 
* режим сервера: аналог предыдущей, без графического интерфейса

В процессе загрузки запускает разные задачи

### 2.7 systemd

### 2.7.1 Основы

новая версия **init**

управляет *модулями*. Модуль - общий термин: служба, сокет, устройство и т. п.
Поведение каждого модуля управляется модульным файлом. В нем указаны: исполняемый файл, способы запуска, зависимые модули

Расположение модульных файлов:

* `/usr/lib/systemd/system` или `/lib/systemd/system` - здесь ресурсы, не меняются
* `/etc/systemd/system` - для настроек
* `/run/systemd/system` - для переходных модулей

суффиксы модульных файлов указывают на тип устройства
напр. `.service` - служебные модули, `.timer` - таймеры

### 2.7.2 systemctl

#### 2.7.2.1 Основы

systemctrl + <подкоманда> + <параметры команды>

вывод загруженных модулей:

    systemctl
    systemctl list-units    // все
    systemctl list-units --type=service     // отбор по типу

вывод списка файлов модулей:

    systemctl list-unit-files [шаблон] 

примеры подкоманд:

* `enable модуль`
* `disable модуль`
* `start модуль`
* `stop модуль`
* `status модуль`
* `kill шаблон`

например статус модуля 

    systemstl status -l upower

Примеры статусов модулей:

* **bad** - что-то не так, обычно - модульный файл
* **disabled** - модуль есть, но не настроен на автозагрузку
* **enabled** - инсталлирован, запущен
* **linked** - доступен через символическую ссылку
* **masked** - нежелательный статус с логической точки зрения
* **static** - зависит от другого устройства, не требует установки


**disabled/enabled** - только для модулей в каталогах *systemd* и в которых есть раздел `[install]`, т. е. которые авто загружаются при старте

**linked** - символические ссылки на модули

**masked** - заблокирован администратором (командой `systemctl mask`).





#### 2.7.2.2 Цели

Цели как режимы запуска
Цели объединяют разные модули в группы

Имеют суффикс `.target`

Важные цели:

* multi-user.target - серверный режим
* graphical.target - обычный графический
* resque.target - однопользовательский (консольный)

сменить режим

    sudo systemctl isolate multi-user.target

посмотреть текущий

    systemctl get-default

установить по умолчанию

    systemctl set-default [режим]

список всех

    systemctl list-units --type=target


#### 2.7.2.3 Собственные службы и настройки

* примеры взять в `usr/lib/systemd/system`
* адаптировать
* справка по `systemd.service` и `systemd.unit`
* новый файл поместить в `/etc/systemd/system`
* можно запустить `sudo systemctl enable custom.service`

Настройки:

* исходные модульные файлы не редактируют обычно
* вместо этого создается каталог `/etc/systemd/system/xxx.d`, где ххх - имя модуля (напр. `nginx.service`)
* в нем создаем файлы с расширением `.conf` (имя любое - обычно `override.conf`), можно несколько.
* параметры можно несколько раз указывать (списки)




## Гл. 3. Управление доступом

### 3.1 Стандартное управление доступом

#### 3.1.1 Основы

* Решения по упоправлению определяется на основе того, какой пользователь пытается выполнить операцию или от членства пользователя в группе
* Объекты (файлы, процессы) имеют владельцев. Владельцы имеют широкий контроль над своими объектами (не обязательно неограниченный)
* Тот кто создает объекты, является их владельцем
* Есть спец. учетная запись **root**, которая является владельцем любого объекта
* root имеет расширенные права на выполнение административных операций

на комбинации этих правил строится вся система доступа к файлам и устройствам (как файлам)

#### 3.1.2 Доступ к файловой системе

Каждый файл имеет владельца и группу. Владелец может устанавливать разрешения для файла. 

Владелец определяет, что могут делать члены группы

`ls -l` показывает права и владельцев файла

Владельцы и группы с точки зрения системы - числа (**UID**, **GID**)

Сопостовление с текстовыми идентификаторами:
* `/etc/passwd` - пользователи
* `/etc/group` - группы

#### 3.1.3 Владельцы процессов

Владелец процесса может отправлят им сигналы и менять приоритет

у процесса есть несколько идентификаторов: реальный, текущий, сохраненный UID. 
аналогично для GID.

что важно и используется - зависит от системы

#### 3.1.4 Учетная запись root

имеет идентификатор UID=0
можно поменять имя root, создать других пользлвателей с uid=0, но это плохая идея

Суперпользователю позволено выполнять любые операции над любыми файлами/процессами

Так например может менять идентификаторы UID и GID. При старте системы программа входа в систему стартует под root. После авторизации меняет 
идентификаторы на идентификаторы текущего пользователя

#### 3.1.5 Установка флагов setuid и setgid

У исполняемого файла могут быть установлены эти флаги.
Это значит, что процесс исполнения этого файла можно запустить с указанными uid и gid, а не с uid/gid текущего пользователя

обычно это root для повышения прав

потенциально это опасное состояние

при установке системы можно отключить этот механизм

### 3.2 Управление учетной записью root

#### 3.2.1 Вход в учетную запись root

Т. к. это пользователь, большинство систем позволяют входить под учетной записью root

Минусы:

* действия под root не логируются
* обычно права нужны нескольким фактически разным пользователям. Кто конкретно будет неизвестно

Большинство систем позволяют отключать вход под root



## Ch. 4. Управление процессами

### 4.1 Компоненты процесса

Состав:

* адресное пространство
    - набор страниц памяти
* структуры данных внутри ядра 
    - таблица распределения памяти
    - текущее состояние процесса
    - информация о ресурсах, используемых процессом
    - имя владельца

**PID** - уникальный идентификатор процесса, назначается ядром.
Команды, работающие с процессами обычно требуют указания PID

**PPID** - идентификатор родительского процесса.
Для создания нового процесса, текущий процесс (родитель) клонирует сам себя. Дочерний процесс будет иметь атрибут PPID равный PID родительского

**UID** - идентификатор пользователя, создавшего процесс (или UID из родительского процесса). Менять может только создатель (владелец) или root

**EUID** (effective user ID) - текущий идентификатор пользователя. Обычно совпадает с UID. Может отличаться у процессов, для которых установлен флаг смены пользователя (setuid). Используется для определения прав доступа

**GID** - идентификатор группы
**EGID** - эффективный идентификатор группы
Аналогичны идентификаторам пользователя. Но для проверки прав используется отдельный список групп, не GID. GID используется, когда процесс создает файлы

**Фактор уступчивости** - в какой степени процесс может уступать ресурсы другим процессам. Используется вместе с приоритетом

**Управляющий терминал** - обычно с процессом не демоном связан терминал, который определяет конфигурацию каналов ввода-вывода и формирует сигналы

### 4.2 Жизненный цикл процесса

При старте системы стартует демон **init** или **systemd** c PID = 0
Он является родителем для (не обязательно прямым) процессов, порождаемых не ядром

Ядро требует чтобы перед тем, как процесс завершился, он получил подтверждение от родительского процесса. Если по каким-то причинам родительский процесс завершился раньше дочерних, "осиротевшие" процессы переназначаются **init** (или **systemd**)

#### 4.2.1 Сигналы

##### 4.2.1.1 Основы

Сигналы - запросы на прерывание, работающие на уровне процессов

Использование:

* как средство коммуникации между процессами
* терминал может посылать сигналы по `Ctrl + C` или `Ctrl + Z`
* команда **kill** от пользователя
* от ядра в случае недопустимой операции
* от ядра могут поступать сигналы (напр. дочерний завершается или канал ввода освободился и т. п.)

При поступлении сигнала:

* если процесс назначил подпрограмму обработки сигнала - вызывается она
* иначе ядро от имени процесса запускает стандартную обработку сигнала

Перехват сигнала - когда процесс вызывает собственный обработчик
После обработки сигнала, процесс продолжает свое выполнение (если не завершился) с того места, где был получен сигнал 

Процесс может игнорировать сигнал (просто не обрабатывается)  или блокировать (ожидает в очереди до разблокировки)

##### 4.2.1.2 Перечень сигналов

можно получить `kill -l`

имя  |описание     |станд. обработка  |блокируется|перехватывается |дамп   
HUP  |отбой        |завершение        |+          |+               |-
QUIT |выход        |завершение        |+          |+               |+
KILL |уничтожение  |завершение        |-          |-               |-
INT  |прерывание   |завершение        |+          |+               |-
STOP |остановка    |остановка до CONT |-          |-               |-
CONT |продолжение  |игнорируется      |-          |+               |-
BUS  |ошибка шины  |завершение        |+          |+               |+
SEGV |ошибка памяти|завершение        |+          |+               |+

BUS, SEGV - сигналы об ошибках, какая точно - неизвестно

TSTP - сигнал на мягкую остановку. Например в терминале `Ctrl + Z` посылает сигнал процессу на остановку. Процесс готовится к остановке и сам себе посылает сигнал STOP. Могут игнорировать.

Сравнение сигналов:

* **KILL** - безусловное завершение процесса, не перехватывается
* **INT** - прерывание (напр. `Ctrl + C` в терминале), Запрос на завершение, программа завершает себя или предлагает пользователю решить
* **TERM** - запрос на завершение, программа выполняет очистку и завершается. Может игнорировать
* **HUP** - демоны обычно реагирует как на сброс/перезапуск (чтение конфигурации и работа с новыми настройкаии)
* **HUP** также сигнал генерирует терминал при закрытии для уничтожения связанных процессов
* **QUIT** - аналог **TERM**, но с дампом

##### 4.2.1.3 Команда kill

посылает любой сигнал процессу (**TERM** по умолчанию)
обычный пользователь - своим процессам, root - любым

синтаксис: `kill [-сигнал] pid`
где `сигнал` - номер или символьное имя сигнала

`kill 123` - "вежливый" (TERM) способ завершить процесс
`kill -9 123` - "гарантированный" способ завершить (кроме случаем блокировки ввода/вывода (напр. проблемы с hdd))

команда `killall имя` - завершает несколько процессов по имени

команда `pkill` - с расширенным поиском процессов
напр. `pkill -u ben` - все процессы пользователя `ben`







#### 4.2.2 Состояние процессов

Процесс может находится в состоянии ожидания. Это может быть ожидание, установленное сигналом **STOP** или ожидание ресурсов

Спящий процесс - процесс, все потоки которого являются спящими
Напр. системные демоны часто находятся в спящем режиме, ожидая команд

Могут уйти в беспробудный сон, не реагируя на сигналы. Обычно это проблемы с вводом/выводом

### 4.3 Команда ps

вывод информации о процессах

`ps aux` - (а - все, x - показывать отсоединенные от терминала, u - сортировать по пользователям)
`ps lax` - более расширенная информация, нет сопоставления с пользователями (быстрее)
`ps ... | grep ...` отбор через grep

Выводимая информация:

* `USER` - владелец процесса
* `PID` - идентификатор процесса
* `%CPU` - доля времени ЦП
* `%MEM` - доля памяти
* `VSZ` - виртуальный размер процесса
* `RSS` - количество страниц памяти
* `TTY` - идентификатор управляющего терминала
* `STAT` - текущий статус процесса
    - R - выполняется
    - D - ожидает записи на диск
    - S - неактивен (< 20 c)
    - T - приостановлен
    - Z - зомби
    - доп. флаги:
        + W - процесс выгружен на диск
        + < - процесс с повышенным приоритетом
        + N - с пониженным
        + L - некоторые страницы блокированы в ядре
        + s - процесс является лидером сеанса
* `TIME` - время ЦП, затраченное на выполнение
* `COMMAND` - имя и аргументы команды (может не отражать реальную команду) (команды в [] - это потоки ядра)
* `NI` - фактор уступчивости
* `WCHAN` - ресурс, которого ожидает процесс


### 4.4  Команда top

интерактивный мониторинг процессов
`h` - варианты команд
`s` - установка периода обновления
`1` - переключение на подробную инфо по ядрам 
и др.

### 4.5 Команда nice и renice: изменение приоритета

Фактор уступчивости: чем выше, тем ниже приоритет
в Linux значения от -20 до 19
Дочерние процессы наследуют приоритет родителя. Пользователь может только повышать фактор уступчивости. Root может любой устанавливать

команда `nice` - установка фактора при создании процесса
команда `renice` - изменение приоритет выполняемого процесса

### 4.6 Файловая система /proc

псевдофайловая система, где ядро хранит информацию о процессах, инфо о состоянии и др.
Отсюда получают данные `ps` и `top`

`man proc` - информация о системе
напр. внутри папки по PID для каждого процесса
могут казаться пустыми (напр. для `ls`), информацию ядро дает при считывании (`cat` напр.)

### 4.7 Команда strace

информация о системных вызовах, выполняемых процессом
может использоваться для дополнительного анализа поведения процесса

### 4.8 Контроль системы: процессы вышедшие из-под контроля

различать большую нагрузку и зависшие процессы / процессы, работающие с ошибками

что смотреть: 

* команды **top**, **ps** - процессорное время, долю загрузки ЦП 
* команда **uptime** - среднюю загрузку системы (количество процессов за прошедщий интервал). При чрезмерной нагрузке средняя загрузка будет превышать количество ядер
* проверка памяти (**top** и колонка VIRT(общая память), RES - резидентный набор). Эти колонки могут включать общие библиотеки, можно вывести колонку DATA (`f` для top, выбрать колонку)
* проверка файловой системы **df -h** или **du -h**

### 4.9 Периодические процессы

#### 4.9.1 Демон cron

##### 4.9.1.1 Основы

в RH он же **crond**

файлы конфигурации называются crontab (от tables)
хранятся в `/var/spool/cron`
привязаны к пользователю, на одного пользователя не более одного файла
**cron** по пользователю определяет под каким UID запускать команды

команда **crontab** управляет файлами, вручную редактировать не рекомендуется.
Если вдруг изменения не подхватываются - перегрузить демон (сигнал *HUP*)

может вести логи в `/var/log/cron`

##### 4.9.1.2 Формат файлов crontab

комментарии начинаются с `#`

каждая строка представляет команду

    минута час день месяц день_недели команда

Значения:

* минута: 0-59
* час: 0-23
* день: день месяца 1-31
* месяц: 1-12
* день_недели: 0-6 (0 - вск)

если указан и день, и день_недели работает по ИЛИ. Напр. `0,30 * 13 * 5` - каждые полчаса по пятницам или 13-го числа

В качестве времени может выступать:

* звездочка: любое значение
* единственное целое число: точное значение
* два целых числа через дефис: диапазон значений
* диапазон с шагом: напр `1-10/2`
* список целых чисел или диапазонов через зпт

напр. `45 10 * * 1-5` - будние лни в 10:45

минимальный интервал - 1 минута






