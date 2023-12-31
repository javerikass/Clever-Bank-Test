# Clever-Bank
# **Описание проекта**
Проект представляет собой консольное приложение для Clever-Bank, реализующее основные операции банковской системы. 
В проекте используются различные сущности, такие как Банк, Счёт, Пользователь и Транзакция. 
Основной стек технологий включает Java 17, Gradle для сборки проекта, PostgreSQL в качестве базы данных, JDBC для взаимодействия с базой данных,
Lombok для упрощения разработки, Servlets для обработки HTTP-запросов, Docker для контейниризации приложения. 
Также используются различные библиотеки и инструменты, такие как JUnit 5 и Mockito для тестирования, AspectJ для сквозного логирования,
Log4j и Slf4j для логирования, IText для генерации PDF-файлов, HikariCP для управления пулом соединений с базой данных.

# **Проект имеет следующие требования:**
**Реализация операций пополнения и снятия средств со счета.**

**Реализация возможности перевода средств другому клиенту Clever-Bank и клиенту другого банка с обеспечением безопасности и избеганием deadlock.**

**Регулярная проверка и начисление процентов на остаток счета в конце месяца, с асинхронной реализацией.**

**Хранение значений в конфигурационном файле .yml.**

**Формирование чека после каждой операции и сохранение в папке check в форматах PDF и TXT.**

**Применение шаблонов проектирования и соблюдение принципов ООП и SOLID.**

**Реализация операций CRUD (создание, чтение, обновление, удаление) для всех сущностей с использованием Servlet.**

## **Инструкция для сборки и запуска приложения**

Для сборки и запуска приложения необходимо выполнить следующие шаги:

1. **Клонируйте репозиторий проекта на свой локальный компьютер.**

      git clone https://github.com/javerikass/Clever-Bank-Test.git

2. **Откройте терминал или командную строку и выполните следующую команду для сборки проекта и создания Docker-образа:**

      docker build -t clever-bank-image .

3. **После успешного создания Docker-образа запустите контейнер с помощью следующей команды:**

      docker run -p 8080:8080 clever-bank-image

4. **Приложение будет запущено и будет доступно по адресу http://localhost:8080.**


# **CRUD операции с примерами входных и выходных данных**

## **CRUD операций над сущностью "Счет" (Account):**

## **Сохранение аккаунта:**

Метод saveAccount сохраняет новый аккаунт. 

Входные данные для этой операции:

accountNumber (тип: String) - номер аккаунта

currency (тип: String) - валюта аккаунта

openDate (тип: String) - дата открытия аккаунта в формате "yyyy-MM-dd"

bankId (тип: long) - идентификатор банка

userId (тип: long) - идентификатор пользователя

Выходные данные: Отсутствуют.

## **Получение всех аккаунтов:**

Метод getAllAccounts возвращает список всех аккаунтов. 

Эта операция не принимает входных параметров.

Выходные данные:

accountList (тип: List<Account>) - список всех аккаунтов

## **Получение аккаунта по идентификатору**

Метод getAccount возвращает аккаунт по заданному идентификатору. 

Входные данные для этой операции:

id (тип: String) - идентификатор аккаунта

Выходные данные:

account (тип: Optional<Account>) - найденный аккаунт или пустое значение, если аккаунт не найден

### **Обновление аккаунта**

Метод updateAccount обновляет существующий аккаунт. 

Входные данные для этой операции:

id (тип: long) - идентификатор аккаунта

accountNumber (тип: String) - номер аккаунта

currency (тип: String) - валюта аккаунта

openDate (тип: String) - дата открытия аккаунта в формате "yyyy-MM-dd"

balance (тип: String) - баланс аккаунта

bankId (тип: long) - идентификатор банка

userId (тип: long) - идентификатор пользователя

Выходные данные: Отсутствуют.

## **Удаление аккаунта**

Метод deleteAccount удаляет аккаунт. 

Входные данные для этой операции:

id (тип: long) - идентификатор аккаунта

accountNumber (тип: String) - номер аккаунта

currency (тип: String) - валюта аккаунта

openDate (тип: String) - дата открытия аккаунта в формате "yyyy-MM-dd"

bankId (тип: long) - идентификатор банка

userId (тип: long) - идентификатор пользователя

Выходные данные: Отсутствуют.

## **CRUD операции над сущностью "Банк" (Bank):**

## **Сохранение банка**

Метод saveBank сохраняет новый банк. 

Входные данные для этой операции:

name (тип: String) - название банка

Выходные данные: Отсутствуют.

## **Получение всех банков**

Метод getAllBanks возвращает список всех банков. 

Эта операция не принимает входных параметров.

Выходные данные:

bankList (тип: List<Bank>) - список всех банков

## **Получение банка по идентификатору**

Метод getBank возвращает банк по заданному идентификатору.

Входные данные для этой операции:

id (тип: String) - идентификатор банка

Выходные данные:
bank (тип: Optional<Bank>) - найденный банк или пустое значение, если банк не найден

## **Обновление банка**

Метод updateBank обновляет существующий банк. 

Входные данные для этой операции:

id (тип: long) - идентификатор банка

name (тип: String) - название банка

Выходные данные: Отсутствуют.

## **Удаление банка**

Метод deleteBank удаляет банк. Входные данные для этой операции:

id (тип: long) - идентификатор банка

name (тип: String) - название банка

Выходные данные: Отсутствуют.

## **CRUD операции над сущностью  "Транзакция" (Transaction):**

## **Получение всех транзакций**

Метод getAllTransactions возвращает список всех транзакций. 

Эта операция не принимает входные параметры.

Выходные данные:

transactionList (тип: List<Transaction>) - список всех транзакций

## **Получение транзакции по идентификатору**

Метод getTransaction возвращает транзакцию по заданному идентификатору. 

Входные данные для этой операции:

id (тип: String) - идентификатор транзакции

Выходные данные:

transaction (тип: Optional<Transaction>) - найденная транзакция или пустое значение, если транзакция не найдена

## **Обновление транзакции**

Метод updateTransaction обновляет существующую транзакцию. 

Входные данные для этой операции:

id (тип: long) - идентификатор транзакции

type (тип: TypeTransaction) - тип транзакции (например, "DEPOSIT" или "WITHDRAW")

senderId (тип: long) - идентификатор отправителя

recipientId (тип: long) - идентификатор получателя

amount (тип: BigDecimal) - сумма транзакции

timestamp (тип: String) - временная метка транзакции в формате "yyyy-MM-dd HH:mm:ss"

Выходные данные: Отсутствуют.

## **Удаление транзакции**

Метод deleteTransaction удаляет транзакцию. 

Входные данные для этой операции:

id (тип: long) - идентификатор транзакции

Выходные данные: Отсутствуют.

## **CRUD операции над сущностью "Пользователь" (User):**

## **Сохранение пользователя**

Метод saveUser сохраняет нового пользователя. 

Входные данные для этой операции:

firstName (тип: String) - имя пользователя

lastName (тип: String) - фамилия пользователя

patronymic (тип: String) - отчество пользователя

Выходные данные: Отсутствуют.

### **Получение всех пользователей**

Метод getAllUsers возвращает список всех пользователей. 

Эта операция не принимает входные параметры.

Выходные данные:

userList (тип: List<User>) - список всех пользователей

## **Получение пользователя по идентификатору**

Метод getUser возвращает пользователя по заданному идентификатору. 

Входные данные для этой операции:

id (тип: String) - идентификатор пользователя

Выходные данные:

user (тип: Optional<User>) - найденный пользователь или пустое значение, если пользователь не найден

## **Обновление пользователя**

Метод updateUser обновляет существующего пользователя. 

Входные данные для этой операции:

id (тип: long) - идентификатор пользователя

firstName (тип: String) - имя пользователя

lastName (тип: String) - фамилия пользователя

patronymic (тип: String) - отчество пользователя

Выходные данные: Отсутствуют.

## **Удаление пользователя**

Метод deleteUser удаляет пользователя. 

Входные данные для этой операции:

id (тип: long) - идентификатор пользователя

firstName (тип: String) - имя пользователя

lastName (тип: String) - фамилия пользователя

patronymic (тип: String) - отчество пользователя

Выходные данные: Отсутствуют.

