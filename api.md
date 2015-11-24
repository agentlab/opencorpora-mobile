#### Описание методов API OpenCorpora для мобильного приложения

##### Авторизация `[POST]` `[/api.php/?action={action}]`

+ Параметры
  + `action` (`required, string`) - Название действия. Для авторизации равно `login`
+ Тело запроса: `login={login}&passwd={password}`
  + `login` (`string, required`) - Идентификатор пользователя
  + `password` (`string, required`) - Пароль пользователя
  
+ Ответ `200`. (В случае неудачи полу `answer` имеет значение `null`, в поле `error` содержится текст ошибки)
  + Поля ответа:
    + `api_version` (`string`) - Версия api
    + `answer` (`object`) - Ответ сервера 
    + `error` (`string`) - Описание ошибки
```
{
  api_version: "0.3",
  answer: {
    token: 458466977
  },
  error: null
}
```



##### Получение типов задач `[GET]` `[/api/get_types.php/?uid={uid}&token={token}]`
+ Парамеры
    + `uid` (`required, string`) - Идентификатор пользователя
    + `token` (`required, string`) - Токен авторизации
+ Ответ `200 (application/json)`
  + Поля ответа
    + `items` (`array(task_type)`) - Массив типов задач
  + Объект `task_type`
    + `type_id` (`number`) - Идентификатор типа
    + `name` (`string`) - Название задачи
    + `complexity` (`number`) - Сложность от 0 до N для задач такого типа
```
{
     "items": [
        {
            "type_id": 1,
            "name": "Прилагательное, мн.ч.: именительный / винительный",
             "complexity": 10
        },
        {
            "type_id": 2,
            "name": "Существительное, ед. ч.: родительный / дательный / предложный",
            "complexity": 5
        }
    ]
}
```
+ Ответ `400 Bad Request` - Указан несуществующий тип задачи
+ Ответ `401 Unauthorized` - Токен авторизации устарел



##### Получение задач `[GET]` `[/api/get_tasks.php/?uid={uid}&type={type}&count={count}&token={token}]`
+ Парамеры
    + `uid` (`required, string`) - Идентификатор пользователя
    + `type` (`required, number`) - Тип задач
    + `count` (`optional, number`) - Количество запрашиваевых задач
    + `token` (`required, string`) - Токен авторизации
+ Ответ `200 (application/json)`
  + Поля ответа
    + `items` (`array(task)`) - Массив задач
  + Объект `task`
    +  `id` (`number`) - Идентификатор задачи
    +  `lcontext` (`string`) - Левый контекст
    +  `rcontext` (`string`) - Правый контекст
    +  `has_instruction` (`boolean`) - Флаг наличия инструкции
    +  `choices` (`array(object)`) - Варианты ответа. Отображение `int => string`

```
{
    "items": [
        {
            "id": 123,
            "target": "одной",
            "left_context": "Недопустимо , чтобы",
            "right_context": "из подсистем являлась сама",
            "has_instruction": true,
            "choices": [
                {
                    "1": "родительный"
                },
                {
                    "2": "дательный"
                },
                {
                    "3": "винительный"
                },
                {
                    "4": "предложный"
                }
            ]
        }
    ]
}
```

+ Ответ `400 Bad Request` - Указан несуществующий тип задачи `type` или некорректное число задач `count`
+ Ответ `401 Unauthorized` - Токен авторизации устарел



##### Актуализация `[POST]` `[/api/post_actual.php/?uid={uid}&token={token}]`
+ Парамеры
    + `uid` (`required, string`) - Идентификатор пользователя
    + `token` (`required, string`) - Токен авторизации
+ Тело запроса `(application/json)`
  + `items` (`array(number)`) -  Массив идентификаторов задач для проверки
```
{
    "items": [
        123,
        145,
        3456
    ]
}
```
+ Ответ `200 (application/json)`
  + Поля ответа
    + `items` (`array(number)`) - Массив задач, которые всё ещё актуальны
```
{ 
  "items": [123]
}
```
+ Ответ `400 Bad Request` - Данные в теле запроса не удалось распарсить
+ Ответ `401 Unauthorized` - Токен авторизации устарел



##### Отправка задач `[POST]` `[/api/post_tasks.php/?uid={uid}&token={token}]`
+ Парамеры
  + `uid` (`required, string`) - Идентификатор пользователя
  + `token` (`required, string`) - Токен авторизации
+ Тело запроса
  + `items` (`array(ready_task)`) - Массив выполненных задач
+ Объект `ready_task`
  + `id` (`number`) - Идентификатор задачи
  + `answer` (`number`) - Номер ответа. `-1` если задача пропущена `99`, если ответ "другое"
  + `before_time` (`number`) - Количество секунд с момента показа задания до ответа пользователя
  + `lcontext_showed` (`boolean`) - Был ли показан левый контекст
  + `rcontext_showed` (`boolean`) - Был ли показан правый контекст
  + `commented` (`boolean`) - Оставил ли пользователь комментарий к заданию
  + `comment_test` (`string`) - Текст комментария
```
{
    "items": [
        {
            "id": 1,
            "answer": -1,
            "seconds_before_answer": 23,
            "is_left_context_showed": true,
            "is_right_context_showed": false,
            "commented": true,
            "comment_text": "very difficult question"
        },
        {
            "id": 2,
            "answer": 2,
            "seconds_before_answer": 2,
            "is_left_context_showed": false,
            "is_right_context_showed": false,
            "commented": false,
            "comment_text": null
        }
    ]
}
```
+ Ответ `400 Bad Request` - Данные в теле запроса не удалось распарсить
+ Ответ `401 Unauthorized` - Токен авторизации устарел



##### Получение инструкций к задачам `[GET]` `[/api/get_manual.php/?type={type}]`
+ Парамеры
  + `type` (`number`) - Тип задачи, для которой понадобилась инструкция
+ Ответ `200`
  + Тело ответа содержит валидный html код без ссылок но с форматированием