require: slotfilling/slotFilling.sc
    module = sys.zb-common

theme: /

    state: Start
        q!: $regex</start>
        script:
            $session.size = null;
            $session.topping = null;
            $session.dough = null;
            $session.sauce = null;
            $session.delivery = null;
            $session.address = null;
        a: Добро пожаловать в PizzaBot! 🍕
        a: Я помогу оформить заказ пиццы.
        a: Например: «Хочу большую пепперони на тонком тесте».


    # Несколько параметров заказа одной фразой
    state: PizzaParams
        intent!: /PizzaParamsIntent
        script:
            if ($parseTree._SizeSlot) {
                $session.size = $parseTree._SizeSlot.slotData;
            }
            if ($parseTree._ToppingSlot) {
                $session.topping = $parseTree._ToppingSlot.slotData;
            }
            if ($parseTree._DoughSlot) {
                $session.dough = $parseTree._DoughSlot.slotData;
            }
        a: Параметры пиццы записаны.
        go!: /CheckOrder


    # Размер
    state: SetSize
        intent!: /SizeIntent
        script:
            if ($parseTree._SizeSlot) {
                $session.size = $parseTree._SizeSlot.slotData;
            }
        a: Размер записан.
        go!: /CheckOrder


    # Начинка
    state: SetTopping
        intent!: /ToppingIntent
        script:
            if ($parseTree._ToppingSlot) {
                $session.topping = $parseTree._ToppingSlot.slotData;
            }
        a: Начинка записана.
        go!: /CheckOrder


    # Основа
    state: SetDough
        intent!: /DoughIntent
        script:
            if ($parseTree._DoughSlot) {
                $session.dough = $parseTree._DoughSlot.slotData;
            }
        a: Основа записана.
        go!: /CheckOrder


    # Соус
    state: SetSauce
        intent!: /SauceIntent
        script:
            if ($parseTree._SauceSlot) {
                $session.sauce = $parseTree._SauceSlot.slotData;
            }
        a: Соус записан.
        go!: /CheckOrder


    # Доставка или самовывоз
    state: SetDelivery
        intent!: /DeliveryIntent
        script:
            if ($parseTree._DeliverySlot) {
                $session.delivery = $parseTree._DeliverySlot.slotData;
            }
        a: Способ получения записан.
        go!: /CheckOrder


    # Адрес
    state: SetAddress
        intent!: /AddressIntent
        script:
            if ($parseTree._AddressSlot) {
                $session.address = $parseTree._AddressSlot.slotData;
            }
        a: Адрес доставки записан.
        go!: /CheckOrder


    # Проверяем, каких данных не хватает
    state: CheckOrder
        script:
            if (!$session.size) {
                $reactions.transition("/AskSize");
            } else if (!$session.topping) {
                $reactions.transition("/AskTopping");
            } else if (!$session.dough) {
                $reactions.transition("/AskDough");
            } else if (!$session.sauce) {
                $reactions.transition("/AskSauce");
            } else if (!$session.delivery) {
                $reactions.transition("/AskDelivery");
            } else {
                $reactions.transition("/CheckAddress");
            }


    # Проверка необходимости адреса
    state: CheckAddress
        script:
            var deliveryText = JSON.stringify($session.delivery);

            if (
                deliveryText &&
                deliveryText.indexOf("delivery") >= 0 &&
                !$session.address
            ) {
                $reactions.transition("/AskAddress");
            } else {
                $reactions.transition("/ConfirmOrder");
            }


    # Вопросы о недостающих параметрах
    state: AskSize
        a: Какой размер пиццы выбрать: маленький, средний или большой?

    state: AskTopping
        a: Какую начинку хотите?
        a: Пепперони, маргарита, четыре сыра, ветчина и грибы или овощная?

    state: AskDough
        a: Какую основу выбрать: тонкое тесто, традиционное тесто или сырный борт?

    state: AskSauce
        a: Какой соус добавить: томатный, сырный, чесночный или без соуса?

    state: AskDelivery
        a: Как хотите получить заказ: доставка или самовывоз?

    state: AskAddress
        a: Назовите адрес доставки.
        a: Например: «улица Ленина дом 15».


    # Итог заказа
    state: ConfirmOrder
        a: 🍕 Все необходимые параметры заказа получены.
        a: Проверьте заказ и подтвердите его.
        a: Всё верно? Скажите «да» или «нет».


    # Подтверждение
    state: ConfirmYes
        intent!: /YesIntent
        a: ✅ Заказ подтверждён!
        a: Пицца передана на приготовление.
        a: Спасибо за заказ!


    # Отказ от подтверждения
    state: ConfirmNo
        intent!: /NoIntent
        a: Хорошо, заказ пока не подтверждаю.
        a: Скажите, что хотите изменить.


    # Изменение заказа
    state: ChangeOrder
        intent!: /ChangeIntent
        a: Что хотите изменить?
        a: Можно изменить размер, начинку, основу, соус, способ получения или адрес.
        a: Например: «изменить размер на большой».


    # Полный сброс
    state: Reset
        intent!: /ResetIntent
        script:
            $session.size = null;
            $session.topping = null;
            $session.dough = null;
            $session.sauce = null;
            $session.delivery = null;
            $session.address = null;
        a: Заказ очищен.
        a: Начинаем заново. Какую пиццу хотите?


    # Помощь
    state: Help
        intent!: /HelpIntent
        a: Я помогу оформить заказ пиццы.
        a: Например: «Хочу большую пепперони на тонком тесте».
        a: Также можно отдельно указать размер, начинку, основу, соус и способ получения.


    # Нераспознанная фраза
    state: NoMatch
        event!: noMatch
        a: Не удалось понять фразу.
        a: Например, скажите: «большая», «пепперони», «тонкое тесто» или «доставка».