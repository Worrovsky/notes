1. Настройка проекта в Google API Console 
    подключаем нужные API, напр. Spreadsheets API
    подключаем OAuth
        запоминаем идентификатор клиента и секрет клиента


2. Как сервер Google отвечать будет на авторизацию:
    
    * если серверное приложение - передаем адрес для ответа

    * не серверное: открываем в стандартном браузере, далее варианты:
        а) Android, Ios: также адрес для ответа
        б) loopback адреса (win, linux) (типа 127.0.0.1:port)
        в) ручные копировать/вставить


3. Запросы:
   
    https://developers.google.com/identity/protocols/OAuth2WebServer
  
    1) Получение authorization code
        запрос на https://accounts.google.com/o/oauth2/auth  
        параметры обязательные:
            client_id - идентификатор клиента
            redirect_uri = "urn:ietf:wg:oauth:2.0:oob" для ручного копирования
            scope - запрашиваемые разрешения 
                есть список
                вид типа "https://www.googleapis.com/auth/spreadsheets"
            response-type - "code" для ручного копировать/вставить
        параметры рекомендуемые:
            access_type - online/offline,  если пользователь не находится в браузере -
                токен невалидным становится, если offline - есть время жизни 
                и доступно обновление токена

            state - любые пары ключ-значение, сервер вернет из обратно во фрагменте #
                может использоваться для валидации пары запрос-ответ для защиты от атак
                (вариант - уникальный идентификатор)

            code_challenge_method - "plain", "S256"
            code_challenge        - code_verifier (строка 43 - 128 символов), кодированная по методу code_challenge_method

    2) Обмен authorization code на refresh token и access token
        //https://developers.google.com/identity/protocols/OAuth2InstalledApp#exchange-authorization-code
        запрос на "https://www.googleapis.com/oauth2/v4/token"
        тело:
            code            - authorization code из предыдущего шага
            client_id       - из настроек проекта
            client_secret   - из настроек проекта
            redirect_uri    - ???
            grant_type      - "authorization_code"
            code_verifier   - 