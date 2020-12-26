Компиляция:

    // простая все классы в одном пакете, результат в ту же папку 
    javac src/Main.java

    // компилированные файлы в отдельную папку bin
    // -d
    javac -d bin src/Main.java 

    // если есть импорт из других пакетов, расположенных в src
    // -sourcepath
    javac -sourcepath src src/com.tt.main.Main.java

    // использование скомпилированных классов, сторонних библиотек (jar)
    // -classpath
    // для скомпилированных - папка, содержащая их, для библиотек - сам файл jar
    javac -classpath some_bin; libs/junit3.4.jar Main.java


Общее: 
    несколько путей можно разделять ;
        javac -d bin -sourcepath src; other_src src/com.tt.main.Main.java                

    пути не должны содержать пробелов, если есть - в кавычки можно
        javac "some folder/src/com.tt.main.Main.java"
