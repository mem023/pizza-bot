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
        a: Например: «Хочу большую пиццу пепперони на тонком тесте».


    state: PizzaParams
        intent!: /PizzaParamsIntent
        script:
            if ($parseTree._SizeSlot) {
                $session.size = $parseTree._SizeSlot.value;
            }

            if ($parseTree._ToppingSlot) {
                $session.topping = $parseTree._ToppingSlot.value;
            }

            if ($parseTree._DoughSlot) {
                $session.dough = $parseTree._DoughSlot.value;
            }
        a: Параметры пиццы записаны.
        go!: /CheckOrder


    state: SetSize
        intent!: /SizeIntent
        script:
            if ($parseTree._SizeSlot) {
                $session.size = $parseTree._SizeSlot.value;
            }
        a: Размер записан.
        go!: /CheckOrder


    state: SetTopping
        intent!: /ToppingIntent
        script:
            if ($parseTree._ToppingSlot) {
                $session.topping = $parseTree._ToppingSlot.value;
            }
        a: Начинка записана.
        go!: /CheckOrder


    state: SetDough
        intent!: /DoughIntent
        script:
            if ($parseTree._DoughSlot) {
                $session.dough = $parseTree._DoughSlot.value;
            }
        a: Основа записана.
        go!: /CheckOrder


    state: SetSauce
        intent!: /SauceIntent
        script:
            if ($parseTree._SauceSlot) {
                $session.sauce = $parseTree._SauceSlot.value;
            }
        a: Соус записан.
        go!: /CheckOrder


    state: SetDelivery
        intent!: /DeliveryIntent
        script:
            if ($parseTree._DeliverySlot) {
                $session.delivery = $parseTree._DeliverySlot.value;
            }
        a: Способ получения записан.
        go!: /CheckOrder


    state: SetAddress
        intent!: /AddressIntent
        script:
            if ($parseTree._AddressSlot) {
                $session.address = $request.query;
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
            if ($session.delivery == "delivery" && !$session.address) {
                $reactions.transition("/AskAddress");
            } else {
                $reactions.transition("/PrepareOrder");
            }


    state: PrepareOrder
        script:
            if ($session.size == "small") {
                $session.sizeName = "маленькая";
                $session.sizePrice = 0;
            } else if ($session.size == "medium") {
                $session.sizeName = "средняя";
                $session.sizePrice = 150;
            } else if ($session.size == "large") {
                $session.sizeName = "большая";
                $session.sizePrice = 300;
            }

            if ($session.topping == "pepperoni") {
                $session.toppingName = "пепперони";
                $session.basePrice = 590;
            } else if ($session.topping == "margherita") {
                $session.toppingName = "маргарита";
                $session.basePrice = 490;
            } else if ($session.topping == "four_cheese") {
                $session.toppingName = "четыре сыра";
                $session.basePrice = 650;
            } else if ($session.topping == "ham_mushrooms") {
                $session.toppingName = "ветчина и грибы";
                $session.basePrice = 620;
            } else if ($session.topping == "vegetable") {
                $session.toppingName = "овощная";
                $session.basePrice = 540;
            }

            if ($session.dough == "thin") {
                $session.doughName = "тонкое тесто";
                $session.doughPrice = 0;
            } else if ($session.dough == "classic") {
                $session.doughName = "традиционное тесто";
                $session.doughPrice = 0;
            } else if ($session.dough == "cheese_crust") {
                $session.doughName = "сырный борт";
                $session.doughPrice = 120;
            }

            if ($session.sauce == "tomato") {
                $session.sauceName = "томатный";
            } else if ($session.sauce == "cheese") {
                $session.sauceName = "сырный";
            } else if ($session.sauce == "garlic") {
                $session.sauceName = "чесночный";
            } else if ($session.sauce == "none") {
                $session.sauceName = "без соуса";
            }

            if ($session.delivery == "delivery") {
                $session.deliveryName = "доставка";
            } else if ($session.delivery == "pickup") {
                $session.deliveryName = "самовывоз";
            }

            $session.totalPrice =
                $session.basePrice +
                $session.sizePrice +
                $session.doughPrice;

            $reactions.transition("/ConfirmOrder");


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
        a: Ваш заказ:
        a: Пицца: {{$session.toppingName}}.
        a: Размер: {{$session.sizeName}}.
        a: Основа: {{$session.doughName}}.
        a: Соус: {{$session.sauceName}}.
        a: Способ получения: {{$session.deliveryName}}.

        if: $session.delivery == "delivery"
            a: Адрес: {{$session.address}}.

        a: Стоимость заказа: {{$session.totalPrice}} руб.
        a: Всё верно? Скажите «да» или «нет».


    state: ConfirmYes
        intent!: /YesIntent
        a: Заказ подтверждён.
        a: Стоимость заказа: {{$session.totalPrice}} руб.

        if: $session.delivery == "delivery"
            a: Заказ передан на приготовление и будет доставлен по указанному адресу.

        if: $session.delivery == "pickup"
            a: Заказ передан на приготовление. Забрать его можно самостоятельно.

        a: Спасибо за заказ!


    state: ConfirmNo
        intent!: /NoIntent
        a: Хорошо, заказ пока не подтверждаю.
        a: Скажите, какой параметр хотите изменить.


    state: ChangeOrder
        intent!: /ChangeIntent
        a: Что хотите изменить?
        a: Можно изменить размер, начинку, основу, соус, способ получения или адрес.
        a: Назовите новое значение.


    state: Reset
        intent!: /ResetIntent
        script:
            $session.size = null;
            $session.topping = null;
            $session.dough = null;
            $session.sauce = null;
            $session.delivery = null;
            $session.address = null;

            $session.sizeName = null;
            $session.toppingName = null;
            $session.doughName = null;
            $session.sauceName = null;
            $session.deliveryName = null;

            $session.basePrice = 0;
            $session.sizePrice = 0;
            $session.doughPrice = 0;
            $session.totalPrice = 0;
        a: Заказ очищен.
        a: Начинаем заново. Какую пиццу хотите?


    state: Help
        intent!: /HelpIntent
        a: Я помогу оформить заказ пиццы.
        a: Можно указать несколько параметров сразу.
        a: Например: «Хочу большую пиццу пепперони на тонком тесте».
        a: Также можно отдельно назвать размер, начинку, основу, соус и способ получения.


    state: NoMatch
        event!: noMatch
        a: Не удалось понять фразу.
        a: Например, скажите: «большая», «пепперони», «тонкое тесто» или «доставка».