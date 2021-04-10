## Начало

Создание репозитория

    git init

Скачивание существующего

    git clone https://github.com/....git

## Работа с репозиториями

**Просмотр сведений о репозитории** 
    
    git remote -v
    git remote show origin

**Получение данных из репозитория** (без изменения локальных данных, нужен merge)

    git fetch origin

**Получение и слияние данных** по отслеживаемой ветви (при клонировании отслеживаемая автоматически настраивается на *main*)

    git pull

**Отправка изменений в репозиторий**

    git push origin main

**Сравнение веток** (например локальной и на удаленном репозитории)

смотрим ветки `git branch -a`

получаем данные из репозитория `git fetch origin`

смотрим различия `git diff main origin/main`

или кратко `git diff main origin/main --stat`

сливаем данные из репозитория `git pull` если надо


## Работа через ssh

[docs](https://docs.github.com/en/github/authenticating-to-github/connecting-to-github-with-ssh)

1. Проверить есть ли готовые ключи `ls -al ~/.ssh `
2. Сгенерировать новую пару `ssh-keygen -t ed25519 -C "your_email@`
3. Добавить приватный ключ в ssh-агент `ssh-add ~/.ssh/id_ed25519`
4. Добавить публичный в настройки гита (скопировать содержимое публичного)
5. Репозитории клонировать через `git@github.com:....`

Репозиторий можно переключить на работу через ssh: [c HTTPS на SSH](https://docs.github.com/en/github/using-git/changing-a-remotes-url#switching-remote-urls-from-https-to-ssh)

    git remote set-url origin git@github.com:USERNAME/REPOSITORY.git


## Разное

Путь, где установлен git

    git --exec-path
