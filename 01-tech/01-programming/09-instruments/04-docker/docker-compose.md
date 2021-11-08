## Содержание

<!-- MarkdownTOC autolink="true" uri_encoding="false" levels="2,3" -->

- [1. Основы](#1-Основы)

<!-- /MarkdownTOC -->
Ы
[docs](https://docs.docker.com/compose/)

## 1. Основы

Для управления мультиконтейнерными приложениями.

Использование Compose:

* определить компоненты в `Dockerfile`
* определить сервисы в `docker-compose.yml`
* выполнить команду `docker compose up` или `docker-compose up`

Compose имеет команды для управления жизненным циклом приложений:

* запуск, останка, перезапуск
* просмотр статуса приложений
* просмотр логов
* выполнение одноразовыхх команд

Особенности:

* множественные изолированные окружения на хосте (через имя проекта)
* сохранение данных в томах при создании контейнеров
    - при запуске `docker compose up`, если у контейнеров с прошлого запуска были тома, подключает их к новым
* переиспользование контейнеров