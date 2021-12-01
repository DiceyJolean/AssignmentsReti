Definire un Server TimeServer, che:


Invia su un gruppo di multicast dategroup, ad intervalli regolari, la data e
l’ora;

Attende tra un invio ed il successivo un intervallo di tempo simulata
mediante il metodo sleep();

L’indirizzo IP di dategroup viene introdotto da linea di comando;

Definire quindi un client TimeClient che si unisce a dategroup e riceve,
per dieci volte consecutive, data ed ora, le visualizza, quindi termina.
