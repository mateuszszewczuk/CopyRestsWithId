# Program kopiujący wiadomości z listy
Program pobiera listę wiadmości z wybranego resta (Json) przy pomocy kijeta obsługujacego porcjowaną opdowiedź serwera. Przy czym zapisuje je w określonej lokalizacji z nazwą &lt;id>.json

Program działa przy pomocy akka streams oraz akka http
- pobiera ścierzkę na którą ma zapisać resty z application.conf
- program można włączyć przez komendę sbt run
- program posiada testy
