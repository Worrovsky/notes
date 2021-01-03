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



## Авто генерация html файла