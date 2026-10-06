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
        a: Добро пожаловать в PizzaBot!
        a: Я помогу оформить заказ пиццы.
        a: Например: «Хочу большую пепперони на тонком тесте».


    state: PizzaParams
        intent!: /PizzaParamsIntent
        script:
            if ($parseTree._SizeSlot) {
                $session.size = $parseTree._SizeSlot;
            }
            if ($parseTree._ToppingSlot) {
                $session.topping = $parseTree._ToppingSlot;
            }
            if ($parseTree._DoughSlot) {
                $session.dough = $parseTree._DoughSlot;
            }
        a: Параметры пиццы записаны.
        go!: /CheckOrder


    state: SetSize
        intent!: /SizeIntent
        script:
            if ($parseTree._SizeSlot) {
                $session.size = $parseTree._SizeSlot;
            }
        a: Размер записан.
        go!: /CheckOrder


    state: SetTopping
        intent!: /ToppingIntent
        script:
            if ($parseTree._ToppingSlot) {
                $session.topping = $parseTree._ToppingSlot;
            }
        a: Начинка записана.
        go!: /CheckOrder


    state: SetDough
        intent!: /DoughIntent
        script:
            if ($parseTree._DoughSlot) {
                $session.dough = $parseTree._DoughSlot;
            }
        a: Основа записана.
        go!: /CheckOrder


    state: SetSauce
        intent!: /SauceIntent
        script:
            if ($parseTree._SauceSlot) {
                $session.sauce = $parseTree._SauceSlot;
            }
        a: Соус записан.
        go!: /CheckOrder


    state: SetDelivery
        intent!: /DeliveryIntent
        script:
            if ($parseTree._DeliverySlot) {
                $session.delivery = $parseTree._DeliverySlot;
            }
        a: Способ получения записан.
        go!: /CheckOrder


    state: SetAddress
        intent!: /AddressIntent
        script:
            if ($parseTree._AddressSlot) {
                $session.address = $parseTree._AddressSlot;
            }
        a: Адрес доставки записан.
        go!: /CheckOrder


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


    state: AskSize
        a: Какой размер пиццы выбрать: маленькая, средняя или большая?

    state: AskTopping
        a: Какую начинку хотите?
        a: Пепперони, маргарита, четыре сыра, ветчина и грибы или овощная.

    state: AskDough
        a: Какую основу выбрать: тонкое тесто, традиционное тесто или сырный борт?

    state: AskSauce
        a: Какой соус добавить: томатный, сырный, чесночный или без соуса?

    state: AskDelivery
        a: Как хотите получить заказ: доставка или самовывоз?

    state: AskAddress
        a: Назовите адрес доставки.
        a: Например: «улица Ленина дом 15».


    state: ConfirmOrder
        a: Ваш заказ сформирован.
        a: Размер: {{$session.size.slotData}}.
        a: Начинка: {{$session.topping.slotData}}.
        a: Основа: {{$session.dough.slotData}}.
        a: Соус: {{$session.sauce.slotData}}.
        a: Способ получения: {{$session.delivery.slotData}}.
        if: $session.address
            a: Адрес: {{$session.address.slotData}}.
        a: Всё верно? Скажите «да» или «нет».


    state: ConfirmYes
        intent!: /YesIntent
        a: Заказ подтверждён.
        a: Пицца передана на приготовление.
        a: Спасибо за заказ!


    state: ConfirmNo
        intent!: /NoIntent
        a: Хорошо, заказ пока не подтверждаю.
        a: Скажите, что хотите изменить.


    state: ChangeOrder
        intent!: /ChangeIntent
        a: Что хотите изменить?
        a: Можно изменить размер, начинку, основу, соус, способ получения или адрес.
        a: Например: «изменить размер на большую».


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


    state: Help
        intent!: /HelpIntent
        a: Я помогу оформить заказ пиццы.
        a: Например: «Хочу большую пепперони на тонком тесте».
        a: Можно отдельно указать размер, начинку, основу, соус и способ получения.


    state: NoMatch
        event!: noMatch
        a: Не удалось понять фразу.
        a: Например, скажите: «большая», «пепперони», «тонкое тесто» или «доставка».