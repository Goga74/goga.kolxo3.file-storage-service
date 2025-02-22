file-storage-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── filestorage/
│   │   │               ├── Application.java           # Точка входа приложения
│   │   │               ├── config/
│   │   │               │   └── StorageConfig.java     # Конфигурация путей хранения
│   │   │               ├── controller/
│   │   │               │   └── FileController.java    # REST-контроллер
│   │   │               ├── service/
│   │   │               │   └── FileStorageService.java # Бизнес-логика хранения
│   │   │               ├── model/
│   │   │               │   └── FileMetadata.java      # Модель метаданных файла
│   │   │               └── vertx/
│   │   │                   └── VertxFileHandler.java  # Обработчик файлов через Vert.x
│   │   └── resources/
│   │       ├── application.yml                        # Конфигурация Micronaut
│   │       └── logback.xml                           # Конфигурация логирования
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── filestorage/
│                       └── controller/
│                           └── FileControllerTest.java # Тесты контроллера
├── pom.xml                                           # Maven конфигурация
└── README.md                                         # Документация проекта