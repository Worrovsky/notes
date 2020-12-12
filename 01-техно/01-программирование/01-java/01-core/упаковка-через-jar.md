https://docs.oracle.com/javase/tutorial/deployment/jar/basicsindex.html

создание:
    команда jar с опциями
        с      создать
        f      в файл (иначе в System.out)

        jar cf jar-file input-file(s)

        jar-file    любое имя файла (обычно с расширением .jar)

        input-file(s):
            перечисленные через пробел файлы и директории
                jar cf output.jar file1.clаss file2.class
            можно так 
                jar cf output.jar *         // все файлы из текущей
                jar cf output.jar .         // аналогично
                jar cf output.jar *.class   // файлы из текущей с расширением .class                
    
        дополнительные опции:
            v       выводить доп. инфо в консоль (перечень файлов)
            0       не сжимать 
            -С      смена директории при выполнении:
                jar cf output.jar -C audio . -C images . // добавляем файлы из директории audio и images


        автоматически добавляется манифест META-INF/MANIFEST.MF


манифесты
    содержит информацию о содержимом jar-файла через пары ключ-значение разделенные ":"
    по умолчанию:
        - создается в META-INF/MANIFEST.MF    
        - содержит свою версию и версию jdk

    изменение манифеста:
        - создать текстовый файл с данными
            кодировка файла - UTF-8
            в конце файла должна быть пустая строка
        - использовать опцию -m команды jar
            jar cfm jar-file manifest-file input-file
                !! порядок jar-file и manifest-file должен быть такой же как порядок опций f и m

    указание точки входа приложения:
        Main-Class: classname
            classname - класс с методом public static void main(String[] args)
        после этого можно вызывать
            java -jar jar-file

        варианты:
            - редактируем манифест вручную
            - с помощью опции -e 
                jar cfe app.jar Main Main.class

    добавление классов в classpath
        Class-Path: jar1-name jar2-name directory-name/jar3-name      

    добавление информации о пакете
        набор значений
            Name, Specification-Title, Specification-Version и др.

    вложение (sealed) пакетов    
        добавление конструкции 
            name: myCompany/myPackage/
            sealed: true

            имя пакета должно заканчиваться на /

        можно создать файл с такими строками, и при вызове команды jar указать его как манифест
        