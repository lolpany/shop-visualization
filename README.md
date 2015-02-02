# shop-visualization
Задача для собеседования.

###Магазин
Реализуйте модель касс и очередей в магазине. Среда запуска для конечного пользователя –
браузеры IE8+, Chrome, FireFox. Логика работы – сервер, визуализация – браузер. Возможно
дополнительно использование JavaScript, CSS, AJAX, и т.д. Творческий подход с чувством
прекрасного приветствуется, использование библиотек не возбраняется.

####Алгоритм
Магазин имеет определённое число касс. У каждой кассы есть очередь из покупателей, 
имеющих определённое количество товаров. Каждая из касс может обслуживать за один шаг
также определённое число товаров (производительность), которое выбирается случайно из
заданного диапазона. Покупатель (количество товаров которого известно) проходит кассу, когда
все его товары будут обслужены. При этом обслуживание покупателя занимает всегда целое
число шагов: даже если касса может обслужить, например, 5 товаров, а у покупателя их
осталось 2, другой покупатель начнёт обслуживаться только на следующем шаге. Если на кассе
уже обслуживается покупатель, пришедший становится в очередь.

Покупатель выбирает кассу и очередь в зависимости от его типа:
• Ребёнок – выбирает очередь случайно.
• Женщина – выбирает кассу с наименьшей длиной очереди (наименьшим числом
• Мужчина – выбирает кассу, в которой его обслужат через наименьшее число шагов (всю очередь перед ним и его самого).

Магазин работает пошагово. За каждый шаг:
1. Появляется один новый покупатель, тип которого и количество товаров выбирается случайно из заданного константами диапазона, 
2. Он выбирает очередь, становится в неё. 
3. Кассы обслуживают первых в очереди покупателей, уменьшая число их товаров на
определённую величину (производительность кассы). Когда у покупателя
заканчиваются товары, он считается обслуженным, к кассе подходит следующий
покупатель в очереди.
покупателей в очереди).

####Реализация
Нужно отобразить состояние магазина на момент прошедшего числа шагов работы, 
например, 100 (вводится пользователем). За каждый шаг нужно отобразить очереди и нового
покупателя. Очереди и кассы выводить графически. 
Касса: Указать её производительность.
Покупатель: Отображать разный тип покупателя по-разному, с указанием количества
товаров у покупателя. Если покупатель появился на данном шаге, его тоже помечать отдельно.
Тип создаваемого на каждому шагу покупателя выбирается случайно. Отдельным бонусом
будет реализация процентного состава покупателей, который будет задаваться (например, 40% 
мужчин, 50% женщин и 10% детей). Число касс и шагов работы магазина задаются
пользователем.
