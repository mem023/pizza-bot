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
        a: Я помогу оформить заказ.
        a: Можете назвать параметры в любом порядке или сразу несколько.
        a: Например: «Хочу большую пепперони на тонком тесте».


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
        a: Параметры пиццы записала.
        go!: /CheckOrder


    state: SetSize
        intent!: /SizeIntent
        script:
            $session.size = $parseTree._SizeSlot.slotData;
        a: Размер: {{$session.size.ru_name}}.
        go!: /CheckOrder

    state: SetTopping
        intent!: /ToppingIntent
        script:
            $session.topping = $parseTree._ToppingSlot.slotData;
        a: Начинка: {{$session.topping.ru_name}}.
        go!: /CheckOrder


    state: SetDough
        intent!: /DoughIntent
        script:
            $session.dough = $parseTree._DoughSlot.slotData;
        a: Основа: {{$session.dough.ru_name}}.
        go!: /CheckOrder



    state: SetSauce
        intent!: /SauceIntent
        script:
            $session.sauce = $parseTree._SauceSlot.slotData;
        a: Соус: {{$session.sauce.ru_name}}.
        go!: /CheckOrder


    state: SetDelivery
        intent!: /DeliveryIntent
        script:
            $session.delivery = $parseTree._DeliverySlot.slotData;
        a: Способ получения: {{$session.delivery.ru_name}}.
        go!: /CheckOrder



    state: SetAddress
        intent!: /AddressIntent
        script:
            $session.address = $parseTree._AddressSlot.slotData;
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
            } else if (
                $session.delivery.value == "delivery"
                && !$session.address
            ) {
                $reactions.transition("/AskAddress");
            } else {
                $reactions.transition("/ConfirmOrder");
            }


    state: AskSize
        a: Какой размер выбрать: маленький, средний или большой?

    state: AskTopping
        a: Какую начинку хотите?
        a: Например: пепперони, маргарита, четыре сыра, ветчина и грибы или овощная.

    state: AskDough
        a: Какую основу выбрать?
        a: Тонкое тесто, традиционное тесто или сырный борт.

    state: AskSauce
        a: Какой соус добавить?
        a: Томатный, сырный, чесночный или без соуса.

    state: AskDelivery
        a: Как хотите получить заказ: доставка или самовывоз?

    state: AskAddress
        a: Назовите адрес доставки.
        a: Например: «улица Ленина дом 15».



    state: ConfirmOrder
        a: Проверьте заказ:
        a: Пицца — {{$session.topping.ru_name}}.
        a: Размер — {{$session.size.ru_name}}, {{$session.size.diameter}} см.
        a: Основа — {{$session.dough.ru_name}}.
        a: Соус — {{$session.sauce.ru_name}}.
        a: Получение — {{$session.delivery.ru_name}}.
        if: $session.delivery.value == "delivery"
            a: 📍 Адрес — {{$session.address}}.
        a: Всё верно? Скажите «да» или «нет».


    state: ConfirmYes
        intent!: /YesIntent
        a: ✅ Заказ подтверждён!
        a: Пицца передана на приготовление.
        a: Спасибо за заказ!


    state: ConfirmNo
        intent!: /NoIntent
        a: Хорошо, заказ пока не подтверждаю.
        a: Скажите, что хотите изменить.
        a: Например: «изменить размер на средний».


    state: ChangeOrder
        intent!: /ChangeIntent
        a: Конечно. Можно изменить любой параметр.
        a: Например:
        a: «изменить размер на большой»,
        a: «изменить начинку на маргариту»,
        a: «изменить соус на сырный».
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
        a: 🔄 Заказ полностью очищен.
        a: Начинаем заново.
        a: Назовите любые параметры новой пиццы.


    state: Help
        intent!: /HelpIntent
        a: Я помогу оформить заказ пиццы.
        a: Параметры можно сообщать в любом порядке.
        a: Например:
        a: «Хочу большую пепперони на тонком тесте».
        a: Или отдельно: «пепперони», затем «большая», затем «тонкое тесто».
        a: Уже введённые параметры можно изменить.

    state: NoMatch
        event!: noMatch
        a: Не удалось понять фразу.
        a: Попробуйте назвать параметр заказа, например:
        a: «большая», «пепперони», «тонкое тесто» или «доставка».