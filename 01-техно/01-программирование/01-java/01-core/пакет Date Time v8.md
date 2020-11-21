java.time
    
    перечисления DayOfWeek, Month
        DayOfWeek
            .MONDAY и т. п.
            методы
                plus(int), minus(int)   - новый экземпляр
                of(int)     статический метод для создания

                getDisplayName(java.time.format.TextStyle textStyle, Locale locale)
                    текстовое представление
                    варианты: TextStyle.SHORT
                              TextStyle.FULL
                              TextStyle.NARROW

                    Locale locale = locale.getDefault();

        Month
            аналогично DayOfWeek

    LocalDate

        создание 
            LocalDate.now()
            LocalDate.of(int year, int/Month month, int day)


        есть getter-ы различные 
            getDayOfWeek(), getMonth(), ...

        можно добавлять/вычитать дни/годы/недели/месяцы
            minusWeeks(), ...

    Instant
        представление времени в наносекуднах от начала эпохи (Instant.EPOCH)

        создание
            Instant.now()

        добавление/вычитание мили/нано/секунд  
            plusMilis(), ...


        Instant -> LocalDate:
            LocalDateTime ldt = LocalDateTime.ofInstant(Instant inst, ZoneId.systemDefault());
            LocalDate ld = ldt.toLocalDate();


    Парсинг и форматирование
        методы parse, format c указанием класса-форматера DateTimeFormatter (java.time.format.DateTimeFormatter)

        
        LocaleDate date = LocalDate.parse(String in, DateTimeFormatter formatter);
            formatter можно предопределенный
                (напр. DateTimeFormatter.BASIC_ISO_DATE)
            можно создать на основе шаблона
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d yyyy");


        при парсинге - исключение DateTimeParseException
        при форматировании - исключение DateTimeException

    time.temporal
        пакет для работы на низком уровне

        TemporalAdjuster
            интерфейс, взять объект Temporal (интерфейс, методы plus/minus/with, классы LocalDate, Instant) 
                и получить "выровненнное" значение

            TemporalAdjusters
                статич. методы
                    firstDayOfMonth()
                    firstDayOfNextMonth() 
                    lastDayOfMonth() ...

        пример
            LocalDate date = LocalDate.of(1,3,5);
            LocaleDate newDate = date.with(TemporalAdjusters.firstDayOfMonth());

        можно собственные Adjuster:
            класс реалзующий TemporalAdjuster (метод AdjustInto())


    Количество времени:
        Duration                промежуток во временных единицах (секундах)
        Period                  в единицах дат (годы, месяцы)
        ChronoUnit.between()    


        










