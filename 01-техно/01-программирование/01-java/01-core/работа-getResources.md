Загрузка ресурсов

    getClass().getResource(String path)
        - путь относительно пакета текущего класса
        - напр. если String.getResource("1.txt") будет искать "java/lang/1.txt"
        - если путь начинается с "/", тогда полный путь - от корня classpath
        - напр. String.getResource("/1.txt") - "./1.txt"


    getClassLoader().getResource(String path)
        - всегда ищет с корня classpath, независимо от указания "/" в начале
        - напр. String.getClassLoader().getResource("1.txt") 
            и String.getClassLoader().getResource("/.txt") одинаково "./1.txt"


