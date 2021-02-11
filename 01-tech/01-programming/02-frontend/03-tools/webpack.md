# Webpack

## Создание js проекта с нуля

Инициализируем проект

    npm init

Устанавливаем **webpack**

    npm install webpack webpack-cli --save-dev
    (или короче) npm i webpack webpack-cli -D

Создаем корневой (точку входа) js-файл. Содержимое - любое
    
    mkdir src
    cd src
    touch index.js
    cd ..

Создаем папку для собранного кода
    
    mkdir dist

Создаем запускаемый html-файл

    cd dist
    touch index.html

В файл `dist/index.html` добавляем вызов скомпонованного файла. Почему имя `main`: так позже зададим в настройках **webpack**

    ...
    <body>
       <script src="main.js"></script> 
    </body>


Создаем файл настроек **webpack**

    cd ..
    touch webpack.config.js

Настраиваем **entry** (c какого файла начинать компоновку) и **output** (куда результат помещать)

    // webpack.config.js
    const path = require('path')
      
    module.exports = {
      entry: './src/index.js',
      output: {
        filename: 'main.js',
        path: path.resolve(__dirname, 'dist'),
      },
    }

Запуск **webpack**:

* через **npx**
    - `npx webpack`
* добавить скрипт в `package.json`
    - `"build": "webpack"`
    - `npm run build`


## Режимы (mode) production vs development

В режиме `development` у файлов и чанков сохраняются осмысленные имена. В режиме `production` минимизируется размер файлов, имена искажаются

Установка режима через файл настроек:

    module.exports = {
        ...
        mode: 'development'

Установка режима через CLI

    webpack --mode=production

Это можно использовать в блоке скриптов в `package.json`

    "scripts": {
        "build": "webpack --mode:production",
    }


## Source map

Webpack обрабатывает файлы с исходным кодом. На выходе получаются файлы (один) с измененным содержимым (при mode=production). При отладке в проде теряется связь с исходным кодом

Выход - использование source map. Это дополнение к измененному коду, связывающее его с исходниками. По сути - соответствие имен переменных и функций в исходныъ файлах и новых

Настройка:

    // webpack.config.js
    module.exports = {
        ...
        devtool: 'inline-source-map',
        ...
    }

Есть разные варианты: карта отдельным файлом, добавляется в output файлы и т. п. 

в проде лучше вообще не использовать или не давать доступ к map-файлам обычным пользователям

[выбор](https://webpack.js.org/configuration/devtool/#development)


## Dev server

Авто компиляция файлов при сохранении + перезапуск в браузере

    npm install webpack-dev-server --save-dev

Задать настройки в `webpack.config.js`

    module.exports = {
        ...
        devServer: {
            contentBase: './dist',
        }
    }

добавить скрипт в `package.json`

    "scripts": {
        "start": "webpack serve --open",
    }

`serve` - запускает dev-сервер [см. CLI](https://webpack.js.org/api/cli/)
`--open` - открывает браузер (другие флаги, порт например `npx webpack serve -h`)


## Html-webpack-plugin

Для генерации html-файла (напр. `index.html`). 
Создает базовый файл, включает скрипт(ы), указанный в `output`
С ним не нужно вручную прописывать имена bundle (которые еще и динамические бывают)
Есть разные настройки, может с шаблонами работать


Установка:

    npm install html-webpack-plugin --save-dev

Настройки в `webpack.config.js`

    const HtmlWebpackPlugin = require('html-webpack-plugin')
    module.exports = {
        plugins: [
            new HtmlWebpackPlugin({
              title: 'Hello webpack plugin',
            }),
        ],
    }


## clean-webpack-plugin

Очистка директории `dist`

Установка

    npm install clean-webpack-plugin --save-dev

Настройки в `webpack.config.js`

    const { CleanWebpackPlugin } = require('clean-webpack-plugin')
    module.exports = {
        plugins: [
            new CleanWebpackPlugin(),
        ],
    }
