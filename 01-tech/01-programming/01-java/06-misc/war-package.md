src
    main 
        java
            com ...
        resources
            ...
        webapp
            ...

maven war / package:
    содержимое java              ->  /WEB-INF/classes
    другие сторонние классы      ->  /WEB-INF/lib
    содержимое resources         ->  /WEB-INF/classes
    содержимое webapp            ->  /
    содержимое webapp/WEB-INF/   ->  /WEB-INF/

gradle war
    содержимое java              ->  /WEB-INF/classes
    другие сторонние классы      ->  /WEB-INF/lib
    src/main/webapp              -> /

чистый Gradle пакует также как и чистый Maven 

проблемы когда Gradle + Idea

* напр. output directory для artifact war-exploded - `..\build\libs\exploded\.`
* но Gradle не создает папку `exploded`
* выход:
    - изменять output directory артифакта (заменяется каждый раз при изменении build.gradle)
    - Property\Build,Execution,Deployment\Build and run using  -> Gradle
    
    