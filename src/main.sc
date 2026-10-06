require: slotfilling/slotFilling.sc
    module = sys.zb-common

theme: /

    state: Start
        q!: $regex</start>
        a: Добро пожаловать в PizzaBot!
        a: Я помогу оформить заказ пиццы. Можете сразу назвать размер, начинку и основу.
        a: Например: «Хочу большую пепперони на тонком тесте».
        script:
            $session.size = null;
            $session.topping = null;
            $session.dough = null;
            $session.sauce = null;
            $session.delivery = null;
            $session.address = null;

    state: PizzaParams
        intent!: /PizzaParamsIntent
        script:
            if ($parseTree.SizeSlot) {
                $session.size = $parseTree.SizeSlot.slotData;
            }
            if ($parseTree.ToppingSlot) {
                $session.topping = $parseTree.ToppingSlot.slotData;
            }
            if ($parseTree.DoughSlot) {
                $session.dough = $parseTree.DoughSlot.slotData;
            }
        a: Отлично, параметры пиццы записаны.
        go!: /CheckOrder

    state: SetSize
        intent!: /SizeIntent
        script:
            if ($parseTree.SizeSlot) {
                $session.size = $parseTree.SizeSlot.slotData;
            }
        a: Размер пиццы записан.
        go!: /CheckOrder

    state: SetTopping
        intent!: /ToppingIntent
        script:
            if ($parseTree.ToppingSlot) {
                $session.topping = $parseTree.ToppingSlot.slotData;
            }
        a: Начинка записана.
        go!: /CheckOrder

    state: SetDough
        intent!: /DoughIntent
        script:
            if ($parseTree.DoughSlot) {
                $session.dough = $parseTree.DoughSlot.slotData;
            }
        a: Основа пиццы записана.
        go!: /CheckOrder

    state: SetSauce
        intent!: /SauceIntent
        script:
            if ($parseTree.SauceSlot) {
                $session.sauce = $parseTree.SauceSlot.slotData;
            }
        a: Соус добавлен.
        go!: /CheckOrder

    state: SetDelivery
        intent!: /DeliveryIntent
        script:
            if ($parseTree.DeliverySlot) {
                $session.delivery = $parseTree.DeliverySlot.slotData;
            }
        a: Способ получения записан.
        go!: /CheckOrder

    state: SetAddress
        intent!: /AddressIntent
        script:
            if ($parseTree.AddressSlot) {
                $session.address = $parseTree.AddressSlot.slotData;
            }
        a: Адрес записан.
        go!: /CheckOrder

    state: CheckOrder
        script:
            if (!$session.size) {
                $reactions.transition("/AskSize");
            } else if (!$session.topping) {
                $reactions.transition("/AskTopping");
            } else if (!$session.dough) {
                $reactions.transition("/AskDough");
            } else if (!$session.delivery) {
                $reactions.transition("/AskDelivery");
            } else {
                $reactions.transition("/ConfirmOrder");
            }

    state: AskSize
        a: Какой размер пиццы выбрать: маленький, средний или большой?

    state: AskTopping
        a: Какую начинку хотите?

    state: AskDough
        a: Какую основу выбрать: тонкую, классическую или пышную?

    state: AskDelivery
        a: Как хотите получить заказ: доставка или самовывоз?

    state: ConfirmOrder
        a: Заказ почти готов.
        a: Размер: {{$session.size}}.
        a: Начинка: {{$session.topping}}.
        a: Основа: {{$session.dough}}.
        a: Способ получения: {{$session.delivery}}.
        a: Всё верно? Ответьте «да» или «нет».

    state: ConfirmYes
        intent!: /YesIntent
        a: ✅ Заказ подтверждён!
        a: Спасибо за заказ. Пицца передана на приготовление.

    state: ConfirmNo
        intent!: /NoIntent
        a: Хорошо. Скажите, что хотите изменить.

    state: ChangeOrder
        intent!: /ChangeIntent
        a: Конечно. Назовите новый размер, начинку или основу пиццы.

    state: Reset
        intent!: /ResetIntent
        script:
            $session.size = null;
            $session.topping = null;
            $session.dough = null;
            $session.sauce = null;
            $session.delivery = null;
            $session.address = null;
        a: Заказ очищен. Можем начать заново.
        a: Назовите размер, начинку и основу пиццы.

    state: Help
        intent!: /HelpIntent
        a: Я умею оформлять заказ пиццы.
        a: Например, скажите: «Хочу большую пепперони на тонком тесте».

    state: NoMatch
        event!: noMatch
        a: Я не совсем понял. Назовите параметры заказа, например: «Большая пепперони на тонком тесте».