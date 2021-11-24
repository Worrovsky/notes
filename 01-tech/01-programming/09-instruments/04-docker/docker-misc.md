## Содержание

<!-- MarkdownTOC autolink="true" uri_encoding="false" levels="2,3" -->

- [1. OWASP Docker Security](#1-owasp-docker-security)
  - [1.1 Хост и Docker должны быть обновлены](#11-Хост-и-docker-должны-быть-обновлены)
  - [1.2 Ограничить доступ к демону Докера](#12-Ограничить-доступ-к-демону-Докера)
  - [1.3 Устанавливать непривилегированного пользователя](#13-Устанавливать-непривилегированного-пользователя)
  - [1.4 Ограничивать привилегии \(capabilities\)](#14-Ограничивать-привилегии-capabilities)
  - [1.4 Запускать с флагом no-new-privileges](#14-Запускать-с-флагом-no-new-privileges)
  - [1.5 Отключить внутриконтейнерное взаимодействие \(--icc=false\)](#15-Отключить-внутриконтейнерное-взаимодействие---iccfalse)
  - [1.6 Использовать модули безопасности Linux](#16-Использовать-модули-безопасности-linux)
  - [1.7 Ограничивать ресурсы \(память, CPU, дескрипторы, рестарты, процессы\)](#17-Ограничивать-ресурсы-память-cpu-дескрипторы-рестарты-процессы)
  - [1.8 Устанавливать файловые системы, тома только для чтения](#18-Устанавливать-файловые-системы-тома-только-для-чтения)
  - [1.9 Использовать статические анализаторы](#19-Использовать-статические-анализаторы)
  - [1.10 Установить уровень логгирования минимум INFO](#110-Установить-уровень-логгирования-минимум-info)
  - [1.11 Использовать линтеры для Dockerfile](#111-Использовать-линтеры-для-dockerfile)

<!-- /MarkdownTOC -->

## 1. OWASP Docker Security

[github](https://github.com/OWASP/CheatSheetSeries/blob/master/cheatsheets/Docker_Security_Cheat_Sheet.md)

### 1.1 Хост и Docker должны быть обновлены

### 1.2 Ограничить доступ к демону Докера

Как можно дать доступ к демону (избегать такого):

* запуск демона с доступом по сети `-H tcp://0.0.0.0:XXX`. Если очень надо - шифрованные соединения
* проброс сокета демона в другие контейнеры, например `-v /var/run/docker.sock:/var/run/docker.sock`.

Результат: **непривилегированный пользователь может получить root-доступ к хосту**.

### 1.3 Устанавливать непривилегированного пользователя

Варианты:

* запуск с опцией `-u`: `docker run -u 4000 alpine`
* через Dockerfile `USER ...`
* через namespaces `--userns-remap=default` [docs](https://docs.docker.com/engine/security/userns-remap/#enable-userns-remap-on-the-daemon)

### 1.4 Ограничивать привилегии (capabilities)

**Не запускать контейнеры с флагом `--privileged`** (дает все возможные привилегии)

Сбросить все и дать только нужные, например:

    docker run --cap-drop all --cap-add CHOWN alpine

### 1.4 Запускать с флагом no-new-privileges

Всегда запускать с этим флагом, чтобы избежать эскалации привилегий (процесс больше не может запускать дочерние процессы с повышением привилегий через флаги `setuid` и `setgid` или использование `su` / `sudo`).

**docker**: 
  
    docker run --security-opt no-new-privileges
    docker run --security-opt="no-new-privileges:true"

**compose**:

    services:
      some-service:
        ...
        security_opt:
         - no-new-privileges:true

### 1.5 Отключить внутриконтейнерное взаимодействие (--icc=false)

Это флаг демона. Отключает включение контейнеров в сеть по умолчанию.

Создать конфигурационный файл демона (если нет):

    touch /etc/docker/daemon.json

Добавить:

    {
      "icc": false
    }

Перезапустить демон:

    sudo systemctl restart docker

Проверить:

    docker network inspect bridge
    # смотреть com.docker.network.bridge.enable_icc

### 1.6 Использовать модули безопасности Linux

**seccomp**, **AppArmor**, или **SELinux**

### 1.7 Ограничивать ресурсы (память, CPU, дескрипторы, рестарты, процессы)

    --restart=on-failure:<number_of_restarts>

    --ulimit nofile=<number>

    --ulimit nproc=<number>

И другие флаги `--ulimit`

### 1.8 Устанавливать файловые системы, тома только для чтения

Файловая система контейнера:

    docker run --read-only alpine sh -c 'echo "whatever" > /tmp/some-file'

Нельзя выполнить внутри контейнера например `touch ...`, если нет привязки или тома с разрешением на запись.

Том в режиме только чтение:

    docker run --mount source=myvolume,destination=/app,readonly alpine

### 1.9 Использовать статические анализаторы

[список](https://github.com/OWASP/CheatSheetSeries/blob/master/cheatsheets/Docker_Security_Cheat_Sheet.md#rule-9---use-static-analysis-tools)

### 1.10 Установить уровень логгирования минимум INFO

По умолчанию уровень логов демона - INFO

Логи смотреть в `/var/log/syslog` (по умолчанию).

Для compose дополнительно:

    docker-compose --log-level info up

### 1.11 Использовать линтеры для Dockerfile

