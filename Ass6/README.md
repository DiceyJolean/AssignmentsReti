Scrivere un programma Java che implementi un server HTTP che gestisca richieste di trasferimento di file di diverso tipo (es. immagini jpeg, gif) provenienti da un browser Web.


• Il server sta in ascolto su una porta nota al client (es. 6789).

• Il server gestisce richieste HTTP di tipo GET alla request URL http://localhost:port/filename.

• Le connessioni possono essere non persistenti.

• Usare le classi Socket e ServerSocket per sviluppare il programma server.

• Per inviare al server le richieste, utilizzare un qualsiasi browser. In alternativa, se avete un sistema Unix-based (oppure il WSL su Windows) potete utilizzare cURL da terminale o wget.

