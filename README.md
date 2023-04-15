# CurrencyChartSwing-Java-20-Maven
Maven Java 20 Swing NetBeans project - Construction of charts of currencies of NBU on years for watching of tendencies of change.

Відправка до Github
---------------------------------------------------------------------------------

У Github створюємо в public repository - CurrencyChartSwing-Java-20-Maven
- Генеруємо токен:
  - У Github -> Profile -> Developer settings (https://github.com/settings/apps) -> Personal access tokens -> Generate new token
    - Note - Netbeans
    - Expiration - 90 днів
    - Вибираємо - repo
    - Generate token
    - Копіюємо код токена
  - Копіюємо ключ (приклад: ghp_****************************)

В Netbeans:
  - -> Team -> Git -> Initialize repository (якщо ще не створено)
  - -> Team -> Commit
  - -> Team -> Remote -> Push
    - Specify Git Repository Location
        - URL: https://github.com/LiaArtem/CurrencyChartSwing-Java-20-Maven.git
        - User: git
        - Password: ghp_****************************
        - Next
            - master -> master  (Next)
            - master -> origin/master  (Finish)