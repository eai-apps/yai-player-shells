# YAiPlayer Shells

Оболочки для отдельного Android-превью YAiPlayer. На телефоне доступны **YAiPlayer Classic** и **YAiPlayer VIZO**. На Android TV к ним добавляется **YAiPlayer Mate**.

На стартовом экране рядом с аккаунтом, гостем и радио выберите **Настройки → Тема оформления**. Выбор сохраняется на устройстве. В Vizo и Mate смена оформления также доступна из меню режима.

- [Последний APK для телефона](https://github.com/eai-apps/yai-player-shells/releases/latest/download/YAiPlayer-Shells-mobile.apk)
- [Последний APK для приставки](https://github.com/eai-apps/yai-player-shells/releases/latest/download/YAiPlayer-Shells-tv.apk)
- [Версии и соответствующие исходники](https://github.com/eai-apps/yai-player-shells/releases)

Приложение проверяет обновления при запуске. Проверить вручную можно в настройках. Установку подтверждает Android. Для перехода со старых сборок shells-preview нужно один раз переустановить тестовое приложение из-за смены подписи.
- [Сборка и воспроизведение](integration/README.md)
- [HANDOFF для Claude](HANDOFF.md)

Модули `vizo` и `mate` принимают данные, палитру и действия от приложения. В тестовом YAiPlayer эти действия направлены к штатным экранам и ядру. Для интеграции в SlyX понадобится его собственный адаптер. Правообладатель оригинальных исходников оболочек — YAi. Условия приведены в `LICENSE`.
