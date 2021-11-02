PING SERVER

PING è una utility per la valutazione delle performance della rete utilizzata per verificare la raggiungibilità di un host su una rete IP e per misurare il round trip time (RTT) per i messaggi spediti da un host mittente verso un host destinazione. Lo scopo di questo assignment è quello di implementare un server PING e un corrispondente client PING che consenta al client di misurare il suo RTT
verso il server. La funzionalità fornita da questi programmi deve essere simile a quella della utility PING disponibile in tutti i moderni sistemi operativi.
La differenza fondamentale è che si utilizza UDP per la comunicazione tra client e server, invece del protocollo ICMP (Internet Control Message Protocol).
Inoltre, poichè l'esecuzione dei programmi avverrà su un solo host o sulla rete locale ed in entrambe i casi sia la latenza che la perdita di pacchetti risultano trascurabili, 
il server deve introdurre un ritardo artificiale e ignorare alcune richieste per simulare la perdita di pacchetti.

È essenzialmente un echo server: rimanda al mittente qualsiasi dato riceve 

• dopo aver ricevuto un PING, il server determina se ignorare il pacchetto (simulandone la perdita) o effettuarne l'eco. La probabilità di perdita di pacchetti di default è del 25%.

• se decide di effettuare l'eco del PING, il server attende un intervallo di tempo casuale per simulare la latenza di rete

• stampa l'indirizzo IP e la porta del client, il messaggio di PING e l'azione intrapresa dal server in seguito alla sua ricezione (PING non inviato, oppure PING ritardato di x ms).


PING CLIENT

accetta due argomenti da linea di comando: nome e porta del server. 
Se uno o più argomenti risultano scorretti, il client termina, dopo aver stampato un messaggio di errore del tip o ERR -arg x, dove x è il numero dell'argomento.
Utilizza una comunicazione UDP per comunicare con il server ed invia 10 messaggi al server, con il seguente formato:

• PING seqno timestamp - in cui seqno è il numero di sequenza del PING (tra 0-9) ed il timestamp (in millisecondi) indica quando il messaggio è stato inviato

• non invia un nuovo PING fino che non ha ricevuto l'eco del PING precedente, oppure è scaduto un timeout.

• stampa ogni messaggio spedito al server ed il RTT del ping oppure un * se la risposta non è stata ricevuta entro 2 secondi

• dopo che ha ricevuto la decima risposta (o dopo il suo timeout), il client stampa un riassunto simile a quello stampato dal PING UNIX
	---- PING Statistics ----
	10 packets transmitted, 7 packets received, 30% packet loss
	round-trip (ms) min/avg/max = 63/190.29/290
	
• il RTT medio è stampato con 2 cifre dopo la virgola
