## Содержание

<!-- MarkdownTOC autolink="true" uri_encoding="false" levels="2,3" -->

- [1. Основы](#1-Основы)
  - [1.1 Начало](#11-Начало)
  - [1.2 Установка](#12-Установка)
- [2. Docker Compose в действии](#2-docker-compose-в-действии)
  - [2.1 Пример работы](#21-Пример-работы)
  - [2.2 Переменные окружения](#22-Переменные-окружения)
  - [2.3 Профили](#23-Профили)
  - [2.4 Разделяемые \(shared\) конфигурации \(несколько compose-файлов\)](#24-Разделяемые-shared-конфигурации-несколько-compose-файлов)
  - [2.5 Сети](#25-Сети)
  - [2.6 Порядок запуска и остановки сервисов](#26-Порядок-запуска-и-остановки-сервисов)
- [3. Спецификация compose-файла](#3-Спецификация-compose-файла)
  - [3.1 Основы](#31-Основы)
  - [3.2 services](#32-services)
  - [3.3 Конфигурация хранилищ \(volumes\)](#33-Конфигурация-хранилищ-volumes)
  - [3.4 Конфигурация сети \(networks\)](#34-Конфигурация-сети-networks)

<!-- /MarkdownTOC -->

[docs](https://docs.docker.com/compose/)

## 1. Основы

### 1.1 Начало

Для управления мультиконтейнерными приложениями.

Использование Compose:

* определить компоненты в `Dockerfile`
* определить сервисы в `docker-compose.yml`
* выполнить команду `docker compose up` или `docker-compose up`

Compose имеет команды для управления жизненным циклом приложений:

* запуск, останка, перезапуск
* просмотр статуса приложений
* просмотр логов
* выполнение одноразовых команд

Особенности:

* множественные изолированные окружения на хосте (через имя проекта)
    - имя проекта:
        + явно задается через `docker-compose -p <имя>`
        + явно задается через  переменную `COMPOSE_PROJECT_NAME` файла
        + по имени директории, где расположен yaml-файл
* сохранение данных в томах при создании контейнеров
    - при запуске `docker compose up`, если у контейнеров с прошлого запуска были тома, подключает их к новым контейнерам
* переиспользование контейнеров (cоздание только изменившихся)
* использование конфигуриционных переменных, наследование вложенных yaml-файлов

### 1.2 Установка

Два варианта:

* отдельная утилита, команды `docker-compose ...`
* часть Docker CLI **Compose V2**, команды `docker compose ...`

Второй вариант возможно по умолчанию будет в будущем, разницы между вариантами не должно быть.

[docs](https://docs.docker.com/compose/cli-command/)

[Compose V2 github]()

Установка V2:

    # скачиваем исполняемый файл (последнюю версию - см. в релизах)
    # можно для конкетного пользователя в ~/.docker/cli-plugins/ (если добавляли в группу docker)
    # или для всех в /usr/local/lib/docker/cli-plugins/ (если запускаем docker через sudo)
    mkdir -p /usr/local/lib/docker/cli-plugins
    curl -SL https://github.com/docker/compose/releases/download/
        v2.0.1/docker-compose-linux-x86_64 -o /usr/local/lib/docker/cli-plugins/docker-compose

    # даем право на запуск
    chmod +x /usr/local/lib/docker/cli-plugins/docker-compose

    # проверка установки
    docker compose version

## 2. Docker Compose в действии

### 2.1 Пример работы

Создаем compose-файл `docker-compose.yml`, например:

    version: "3.9"
    services:
      web:
        build: .
        ports:
          + "5000:5000"
      redis:
        image: "redis:alpine"

Здесь образ первого сервиса `web` создается из dockerfile, второго - готовый образ.

Запускаем через `docker compose up`. Создаются / скачиваются образы, запускаются контейнеры.

Запуск в фоне:

    docker compose up -d 

Просмотр сервисов:

    docker compose ps

Запуск одного сервиса из yml-файла:

    docker compose run web    

Для одного сервиса можно запускать отдельные команды:

    docker compose run <сервис> <команда>
    # например просмотр переменных окружения контейнера web:
    docker compose run web env

Остановка:

    docker compose stop



### 2.2 Переменные окружения

Различать: переменные окружения compose-файла и переменные окружения контейнеров.

#### 2.2.1 Источники переменных compose-файла

**Использование внутри yml-файла**:

    web:
        image: "webapp:${TAG}"

**Источники переменных** (по убыванию приоритета):

* переменные окружения оболочки, откуда запущен `docker compose`
* файл из опции `--env-file`
* файл `.env` из директории с compose-файлом

Если не найдена - подставляется пустая строка в место использования.

В файлах можно задавать значения по умолчанию.

Результирующий yml-файл после подстановки переменных проверить:

    docker-compose config

Примеры:
    
    export tag=v2.0
    docker compose up

    echo "TAG=v3.0" > .env
    docker compose up

    echo "TAG=v3.0d" > .env.dev
    docker compose --env-file=.env.dev up


#### 2.2.2 Синтаксис объявления переменных в compose-файле

Допустимы `$VARIABLE` и `${VARIABLE}`.

Можно со значениями по умолчанию:

* `${VARIABLE:-default}` - по умолчанию, если значение пустое или не установлено
* `${VARIABLE-default}` - по умолчанию только, если переменная не установлена 

Аналогично можно сообщения об ошибках выводить:

* `${VARIABLE:?err_msg}` - если значение пустое или не установлено
* `${VARIABLE?default}` - только если переменная не установлена 

Экранирование знака `$`: `$$`


#### 2.2.3 Файл .env

[docs](https://docs.docker.com/compose/env-file/)

**Раcположение файла `.env`**:

* задан явно `--env-file`
* в корне директории проекта
* корень проекта (по убыванию приоритета):
    - в версиях до 1.28 есть флаг `--project-directory`
    - по расположению yml-файла, если задан явно через `--file`
    - иначе текущая рабочая директория команды `docker compose`

**Содержимое файла**: 

В каждой строке ключ=значение

Комментарии: строка с `#`

Пустые строки игнорируются.
  

#### 2.2.4 Переменные для контейнеров

**Установка переменных в контейнере**:

В обычном режиме это:

    docker run -e VAR=VALUE

В compose-файле:

    web:
        environment:
            - VAR=VALUE

**Передача текущих переменных в контейнер**:

Просто по имени:

    `docker run -e VARIABLE ...`

В файле (контейнер получит значение переменной `DEBUG` из оболочки):

    web:
        environment:
            - DEBUG

**Передача переменных из файла**:

Аналог `docker run --env-file=FILE ...`:

    web:
        env_file:
            - web-variables.env



### 2.3 Профили

Каждому сервису можно назначить профиль. Если профиль не назначен, сервис запускается всегда. Если профиль назначен, сервис будет запускаться только если профиль активен.

**Назначение профиля**: свойство `profiles` и массив значений
    
    version: "3.9"
    services:
      frontend:
        image: frontend
        profiles: ["frontend"]

или

    ...
        profiles:
            - frontend

**Включение профиля**:

Флаг `--profile`

    docker-compose --profile debug up

Или переменная окружения `COMPOSE_PROFILES`

Несколько профилей - через зпт или несколько флагов.


### 2.4 Разделяемые (shared) конфигурации (несколько compose-файлов)

#### 2.4.1 Основы

Используются для задания разных настроек в разных окружениях, для разных задач и т. п.

По умолчанию Compose читает два файла: `docker-compose.yml` (конфигурация по умолчанию) и `docker-compose.override.yml` (переопределение конфигурации).

Результирующая конфигурация - объединение двух файлов. Можно смотреть через `docker compose config`.

Задать несколько файлов, файлы с другими именами: опция `-f` [docs](https://docs.docker.com/compose/reference/#use--f-to-specify-name-and-path-of-one-or-more-compose-files):

     docker-compose -f docker-compose.yml -f docker-compose.admin.yml \
             run backup_db

Если таким образом:

* первый файл - полноценный самодостаточный конфиг (**базовая конфигурация**)
* остальные файлы могут переопределять отдельные блоки
* располагаться должны относительно базового

Примеры использования:

* различные настройки для различных окружений: `dev`, `prod`, `test`
* административные задачи (запуск отдельных команд: миграции БД)

Пример:

    # docker-compose.yml
    services:
        web:
            image: example/my_web_app:latest
            depends_on:
                + db
        db:
          image: postgres:latest

    # docker-compose.dev.yml
    services:
        web:
            ports:
                - 8888:80

#### 2.4.2 Правила объединения конфигураций

Конфигурации применяются в том порядке, в котором указаны (порядок опций `-f`)

Настройки **локальных сервисов** имеют больший приоритет чем настройки **исходных сервисов** (из базовой конфигурации):

* простые значения (`image`, `command`) заменяются из локальных настроек
* настройки из нескольких значений (`posts`, `expose`, `tmpfs`) - конкатенация
* `environment`, `labels` объединяются по имени переменной с приоритетом локальных
* `volumes`, `devices` объединяются по пути в контейнере с приоритетом локальных

Примеры:

    # original
    myservice:
        environment:
          - FOO=original
          - BAR=original
        volumes:
          - ./original:/foo
          - ./original:/bar

    # local
    myservice:
        environment:
          - BAR=local
          - BAZ=local
        volumes:
          - ./local:/bar
          - ./local:/baz

    # результат:
    myservice:
        environment:
          - FOO=original
          - BAR=local
          - BAZ=local
        volumes:
          - ./original:/foo
          - ./local:/bar
          - ./local:/baz



### 2.5 Сети

#### 2.5.1 Сеть по умолчанию

По умолчанию создает стандартную bridge сеть:

* имя сети = имя проекта [см. 1.1] + `_default`
* все контейнеры видят друг друга
* доступ из контейнеров: по имени сервиса/контейнера (например `postgres://db:5432`)
* доступ извне: если проброшены порты

(not recommended) Есть `links` [docs](https://docs.docker.com/compose/compose-file/compose-file-v2/#links). Также для связи контейнеров

Настройки сети по умолчанию:
    
    networks:
      default:
        # Use a custom driver
        driver: custom-driver-1

#### 2.5.2 Пользовательская сеть

Через блок `networks`: разные настройки, подключение к сети, созданной не докером и т. п.

Ключ `networks` верхнего уровня [docs](https://docs.docker.com/compose/compose-file/compose-file-v2/#network-configuration-reference) создает сеть.

Ключ `networks` нижнего уровня [docs](https://docs.docker.com/compose/compose-file/compose-file-v2/#networks) подключает для сервиса.

Пример:

    services:
      proxy:
        build: ./proxy
        networks:
          - frontend
      app:
        build: ./app
        networks:
          - frontend
          - backend
      db:
        image: postgres
        networks:
          - backend

    networks:
      frontend:
        # Use a custom driver
        driver: custom-driver-1
      backend:
        # Use a custom driver which takes special options
        driver: custom-driver-2
        driver_opts:
        foo: "1"
        bar: "2"


#### 2.5.3 Подключение к существующей сети

Через опцию `external`

    services:
      # ...
    networks:
      default:
        external: true
        name: my-pre-existing-network       

### 2.6 Порядок запуска и остановки сервисов

Контролировать можно через опцию `depends_on`.

Docker управляет контейнерами в порядке зависимостей. Зависимости определяются по `depends_on`, `links`, `volumes_from` и `network_mode: "service:..."`.

Но при этом Docker не ждет, когда приложение внутри контейнера будет готово. Только ожидает запуска контейнера. 

## 3. Спецификация compose-файла

[docs](https://docs.docker.com/compose/compose-file/compose-file-v3/)

### 3.1 Основы

#### 3.1.1 Версии, блоки верхнего уровня

Есть разные версии спецификации. Текущая (2021-11) версия - 3.x.

Файл состоит из тегов верхнего уровня:

* `version` - версия спецификации ???
* `services` - описывает конфигурацию контейнера
* `networks` - задает сети
* `volumes` - настраивает тома

Пример:

    version: "3.9"
        
    services:
        redis:

    networks:
      frontend:
      backend:
     
    volumes:
      db-data:

#### 3.1.2 Спецификации значений времени, размера

[docs](https://docs.docker.com/compose/compose-file/compose-file-v3/#specifying-durations)

Единицы времени: `us`, `ms`, `s`, `m`, `h`

Единицы объема: `b`, `k`, `m`, `g` или `kb`, `mb`, `gb`

Примеры:

    2.5s
    1m30s
    2h32m
    5h34m56s
    2b
    1024kb
    2048k
    300m
    1gb


### 3.2 services

#### 3.2.1 build

Определяет как создавать образ, аналог `docker build`

Просто путь к контексту:

    services:
      webapp:
        build: ./dir

Или объект с переменными:

    services:
      webapp:
        build:
          context: ./dir
          dockerfile: Dockerfile-alternate
          args:
            buildno: 1

Возможные переменные объекта:

* `context` - путь или url, контекст
* `dockerfile` - имя Dockerfile, если не по умолчанию. Нужен `context`
* `args` - переменные окружения для Dockerfile
* `network` - сеть для процесса сборки (для команд `RUN`). Можно `host`, `none` или из блока `networks`
* `target` - только определенный этап многоэтапной сборки

#### 3.2.2 command

Переопределяет `CMD` из Dockerfile. Также есть exec и shell формы.

    services:
        webapp:
            build:
                ...
            command: ["bundle", "exec", "thin", "-p", "3000"]



#### 3.2.3 container_name

Задает имя контейнера вместо автогенерируемого.

    services:
        webapp:
            build:
                ...
            container_name: my-web-container

Т. к. имена должны быть уникальны, после задания имени нельзя масштабировать сервисы (> 1 контейнера нельзя).

#### 3.2.4 depends_on

Устанавливает зависимости между сервисами:

* `docker compose up` запускает сервисы по порядку зависимостей (но не ждет готовности)
* `docker compose up <service-name>` запускает также те, от которых зависит
* `docker compose stop` останавливает по порядку зависимостей

Пример:

    version: "3.9"
    services:
      web:
        build: .
        depends_on:
          - db
          - redis
      redis:
        image: redis
      db:
        image: postgres

#### 3.2.5 entrypoint

Переопределяет команду `ENTRYPOINT`.

Также exec- или shell-формы:

    entrypoint: /code/entrypoint.sh
    entrypoint: ["php", "-d", "memory_limit=-1", "vendor/bin/phpunit"]

Если в Dockerfile есть `CMD`, она игнорируется.



#### 3.2.6 env_file

Задает файлы с переменными окружения для контейнеров (не сброки). Можно списком:

    env_file:
      - ./common.env
      - ./apps/web.env
      - /opt/runtime_opts.env

Путь определяется относительно compose-файла (может через `-f` задан)

Переменные, определенные в блоке `environment`, имеют больший приоритет.

Порядок файлов важен: если одинаковые переменные объявлены в разных файлах, значение получит из последнего файла.

#### 3.2.7 environment

Задает переменные окружения для контейнеров.

Два варианта синтаксиса:

    environment:
      RACK_ENV: development
      SHOW: 'true'
      SESSION_SECRET:

    environment:
      - RACK_ENV=development
      - SHOW=true
      - SESSION_SECRET

В первом варианте булево значение - в `'`.

Можно задать просто ключи, значения должны быть установлены при запуске (через параметры командной строки). Например для секретов.

#### 3.2.8 expose

Объявляет порты:

    expose:
      - "3000"
      - "8000"

#### 3.2.9 healthcheck

    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost"]
      interval: 1m30s
      timeout: 10s
      retries: 3
      start_period: 40s

Форматы `test`:

* `NONE` - отключает проверки, определенные в исходном образе
* `CMD` - напрямую выполняет команду
* `CMD-SHELL` - оборачивает в `/bin/sh`
* просто строка - эквивалент `CMD-SHELL`

Отключение проверок:

    healthcheck:
        disable: true

Или

    healthcheck:
        test: ["NONE"]

#### 3.2.10 image

Задает образ `repository/tag` или `id`
  
#### 3.2.11 init

Аналог `docker run --init`

Запускать или нет init-процесс в контейнере. Основные задачи: передача сигналов и работа с зомби-процессами.


По умолчанию используется [tini](https://github.com/krallin/tini)

    services:
      web:
        image: alpine:latest
        init: true

#### 3.2.12 logging

Задает систему логгирования: драйвер и опции:

    logging:
      driver: syslog
      options:
        syslog-address: "tcp://192.168.0.42:123"    

#### 3.2.13 network_mode

??? что-то для сети. 

Подключение к сети хоста:

    network_mode: "host"

В этом режиме не работают пробросы портов, контейнеру доступны приложения из сети хоста.

#### 3.2.14 networks

Сеть из блока верхнего уровня `networks`, к которой подключается контейнер:

    services:
     some-service:
      networks:
       - some-network
       - other-network
    networks:
      - some-network

##### 3.2.14.1 aliases

Внутри сети контейнер может обращаться к другому контейнеру по имени или по заданному псевдониму в сети:

    services:
      some-service:
        networks:
          some-network:
            aliases:
              - alias1
              - alias3

##### 3.2.14.2 ipv4_address, ipv6_address

Задает статический адрес контейнера внутри сети.

Блок `networks`, где настраивается сеть, должен иметь блок `ipam` с настройками подсети.

#### 3.2.15 ports

Проброс портов

##### 3.2.15.1 Краткая запись

* оба порта `HOST:CONTAINER`
* только порт контейнера (на хосте произвольный порт будет выбран)
* с указанием ip-адреса `IPADDR:HOST:CONTAINER`
  - по умолчанию все интерфейсы `0.0.0.0`
  - можно пустой порт хоста `127.0.0.1::80` (произвольный) 
  
Рекомендуется как строки выражать порты:

    ports:
      - "3000"
      - "3000-3005"
      - "8000:8000"
      - "9090-9091:8080-8081"
      - "49100:22"
      - "127.0.0.1:8001:8001"
      - "127.0.0.1:5000-5010:5000-5010"
      - "127.0.0.1::5000"
      - "6060:6060/udp"
      - "12400-12500:1240"
    
##### 3.2.15.2 Длинная запись

    ports:
      - target: 80
        published: 8080
        protocol: tcp
        mode: host

`target` - порт контейнера, `published` - порт хоста. `mode` - для оркестрации



#### 3.2.16 profiles

Устанавливает профили для сервиса. Если профиль задан, сервис запускается:

    profiles: ["frontend", "debug"]
    profiles:
      - frontend
      - debug

Задание профиля:

* `docker-compose --profile debug up`
* переменная окружения `COMPOSE_PROFILES`

#### 3.2.17 restart

[docs](https://docs.docker.com/config/containers/start-containers-automatically/#use-a-restart-policy)

Аналог `docker run --restart`

* `restart: "no"` - по умолчанию
* `restart: always` - всегда
* `restart: on-failure` - если код выхода отличен от 0. В т. ч. при рестарте демона
* `restart: unless-stopped` - перезапускать, если контейнер не был остановлен.

#### 3.2.18 stop_grace_period

Как долго ждать ответа на сигнал `SIGTERM` при попытке остановить контейнер перед отправкой `SIGKILL`. По умолчанию 10 сек

    stop_grace_period: 1s

#### 3.2.19 stop_signal

Задает сигнал для остановки контейнера. По умолчанию `SIGTERM`:

    stop_signal: SIGUSR1

#### 3.2.20 volumes

##### 3.2.20.1 Основы

Монтирует том или директорию хоста в контейнер.

Можно объявить в секции `volumes`, затем использовать в `services.volumes`.

Или директорию сразу объявить в `services.volumes`, но переиспользовать нельзя.

##### 3.2.20.2 Короткая запись

    [SOURCE:]TARGET[:MODE]

* `SOURCE` - путь на хосте или имя тома
* `TARGET` - путь в контейнере
* `MODE` - `ro` или `rw` (по умолчанию)

Путь на хосте можно относительно контекста (compose-файла) задавать.

Если директорий не существует, создаст при запуске.

    volumes:
      # Just specify a path and let the Engine create a volume
      - /var/lib/mysql
      # Specify an absolute path mapping
      - /opt/data:/var/lib/mysql
      # Path on the host, relative to the Compose file
      - ./cache:/tmp/cache
      # User-relative path
      - ~/configs:/etc/configs/:ro
      # Named volume
      - datavolume:/var/lib/mysql

##### 3.2.20.3 Длинная запись

Директории должны быть созданы до запуска.

* `type`: тип `volume`, `bind` или `tmpfs`
* `source`: путь на хосте или имя тома, определенного в `volumes`
* `target`: путь в контейнере
* `read_only`: флаг только на чтение
* `bind` - дополнительные опции для привязок
  - `propagation`
* `volume` - дополнительные опции для томов
  - `nocopy` - не копировать данные из контейнера в том при создании
* `tmpfs`
  - `size` - размер в байтах
  
Пример:

    version: "3.9"
    services:
      web:
        image: nginx:alpine
        ports:
          - "80:80"
        volumes:
          - type: volume
            source: mydata
            target: /data
            volume:
              nocopy: true
          - type: bind
            source: ./static
            target: /opt/app/static


#### 3.2.21 user

Запуск от имени указанного пользователя.

Эквивалент `docker run --user` [docs](https://docs.docker.com/engine/reference/run/#user):

    --user=[ user | user:group | uid | uid:gid | user:gid | uid:group ]

### 3.3 Конфигурация хранилищ (volumes)

#### 3.3.1 Основы

Можно как часть спецификации сервиса в блоке `services`. Блок `volumes` верхнего уровня - для переиспользуемых.

Простой случай - просто имя, тогда создается том по умолчанию (драйвер `local`):

    services:
      db:
        image: db
        volumes:
          - data-volume:/var/lib/db
    volumes:
      data-volume:

В остальных случаях задаем параметры.

#### 3.3.2 Параметры volumes

* `driver` - задает драйвер хранилища. По умолчанию `local`.
* `driver_opts` - параметры для драйвера
* `external` - флаг, что хранилище создано вне Compose. `docker compose up` не пытается создать, только проверяет наличие.
* `labels`
* `name` 

Пример:

    services:
      db:
        image: postgres
        volumes:
          - data:/var/lib/postgresql/data
    
    volumes:
      data:
        external: true


### 3.4 Конфигурация сети (networks)

#### 3.4.1 Параметры

* `driver`: `bridge`, `overlay` (для оркестрации), `host`, `none`
* `driver_opts` - опции для драйвера
* `attachable` - для `overlay` сетей, можно ли подключать сторонние контейнеры
* `ipam` - настройки сети:
  - `driver`
  - `subnet`
* `external` - сеть создана внешними средствами, Compose не будет пытаться ее создать.
* `name` - имя сети в списке сетей `docker network ls`

Пример подключения к внешней сети:

    version: "3.8"
    services:
      db:
        image: backend-image
        networks:
          - database-network # имя сети в этом файле
    networks:
      database-network: # это имя сети в этом файле
        external:
          name: database-network # имя сети в docker network ls

По умолчанию подключаются к сети `default`. Ее можно переопределить, тогда все контецйнеры будут к ней подключаться:

    version: "3.8"
    services:
      db:
        image: backend-image
    networks:
      default:
        external:
          name: database-network # Must match the actual name of the network
