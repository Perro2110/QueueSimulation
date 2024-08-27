# Simulatore di un sistema a coda di tipo M/M/C 

### Scopo del Progetto: 
Creare un simulatore ad eventi di un sistema a coda di tipo M/M/C.

### Schema Logico: 
<div align="center"><img src="https://github.com/Perro2110/QueueSimulation/blob/main/M-M-c-queue-model-for-the-distributed-serverThis-is-a-graphical-representation-of-a%20(1).png"></div>

### Metodo:
Per iniziare, si è diviso il problema in diversi blocchi:
#### Come implementare le componenti principali del sistema:
- Generatore: simula l’arrivo dei pacchetti dalla rete con un tempo di interarrivo markoviano dato in input il parametro λ (tasso degli arrivi, pacchetti al secondo).
- Coda: raccoglie tutti i pacchetti arrivati in attesa di essere serviti, trattandosi di un sistema M/M/C è prevista una coda infinita.
- Servitori: simulano le entità (c servitori) che svolgono il servizio per i pacchetti con un tempo di servizio markoviano dato in input il parametro μ (tasso delle partenze, pacchetti al secondo).
#### Quali dati sono necessari al fine della simulazione.
#### Come raccogliere i dati:
- Tempi medi di permanenza dei pacchetti:  
- Tempo in Coda: WQ
- Tempo nel sistema: WS
- Numero medio di pacchetti: (In Coda: LQ , Nel Sistema: LS)
- Probabilità di stato: PK , ossia, la probabilità di avere K pacchetti nel sistema.
#### Come interpretare i dati.
#### Come interfacciare il tutto per l’utilizzo da parte di un utente.

### Implementazione:
1) Per implementare le varie componenti del sistema sono state utilizzate delle classi Thread (Generatore, Coda 2, Servitore).Si è poi deciso di utilizzare due code ai fini implementativi.
- Generatore: genera un tempo casuale, attende per il tempo generato e infine, inserisce il pacchetto all’interno della coda.  
- Coda 1: l'effettiva coda è un Oggetto ConcurrentLinkedQueue dove vengono inseriti e prelevati i pacchetti.
- Coda 2: la seconda coda chiama il primo servitore libero, per elaborare il primo pacchetto della Coda 1.
- Servitore: preleva il primo pacchetto della Coda 1, genera un tempo casuale, attende per il tempo generato e infine, torna “libero”. 

2) Sono stati ritenuti necessari i seguenti dati:
- I parametri di λ e μ, sopracitati.
- Tempo e velocità della simulazione.
- L’estrazione dei tempi di ogni pacchetto per il calcolo dei tempi medi di permanenza.
- L'estrazione del numero di pacchetti in un dato istante, per il calcolo del numero medio di pacchetti e per le probabilità di stato.

3) La raccolta dei dati è stata affrontata simultaneamente all’esecuzione:
- Per i tempi di permanenza: ogni pacchetto memorizza per sé i suoi tempi di permanenza in coda e nel servitore, il tempo di permanenza del sistema è calcolato come la somma dei due.
- Per il numero di pacchetti: un thread esterno ogni secondo (in realtà va in base alla velocità di simulazione selezionata, ad esempio in 𝗑2 il campionamento sarà ogni 0.5 secondi e così via) campiona la dimensione della coda e del sistema (sommando alla dimensione della coda il numero di servitori pieni) per poi memorizzarli in due vettori.

4) Dai dati campionati durante la simulazione vengono calcolate:
- Le diverse medie per trovare WQ, WS, LQ, LS.
- Le probabilità di stato, calcolate contando quante volte è stato campionato uno specifico stato e dividendolo per il numero di campionamenti fatti.

5) Tramite l’utilizzo dei JFrame, è stata implementata un'interfaccia nella quale inserire i valori di λ, μ e c, la durata della simulazione e la velocità della stessa. Successivamente si sceglie se eseguire una simulazione normale, oppure con animazione FIFO (consentita solamente con velocità: 𝗑1,𝗑2).
Al termine della simulazione un’interfaccia permette di visualizzare a schermo i dati e i relativi risultati.

### Raccolta Dati:
Per raccogliere i vari dati sono stati utilizzati tre diversi file di testo: 
- “dati.txt”: contenente i tempi medi di permanenza dei pacchetti:
              Ogni entry contiene:    TCoda   TServitore  TSistema
- “parametri.txt”:  contenente i parametri λ, μ, c , Lq ed Ls.
- "probabilità.txt": contiene le probabilità di essere negli stati K.
Successivamente, questi sono presi in analisi da parte di MatLab che fornisce i rispettivi grafici.

Circa il file “dati.txt” si utilizza la struttura readtable, ottimale per la lettura di dati in colonne su file di tipo .txt, successivamente si creano rispettivamente gli array per le tre colonne.
Anche per il file “probabilità.txt” si utilizza la medesima struttura readtable per poi creare un vettore contenente tutte le probabilità.
Inoltre, per facilitare il salvataggio dei dati, viene creato un file .zip che contiene i 3 file sopra-citati.

### Analisi Dati:
I grafici che seguono raffigurano nello specifico un sistema a coda M/M/4.

#### Tempi medi di permanenza empirici rispetto ai rispettivi teorici
Le curve μcWs e μcWq rappresentano l’andamento teorico degli omonimi valori. I punti presenti sulle curve indicano i valori empirici trovati tramite 5 diverse simulazioni al variare di λ e μ.
<div align="center"><img src="https://github.com/Perro2110/QueueSimulation/blob/main/Immagine%202024-08-27%20112137.png"></div>

#### Tempi medi di permanenza empirici rispetto al numero di pacchetti
In questo caso particolare (con una simulazione con λ = 15, μ = 5 di 8 ore), si può notare che dopo circa 15000 pkt, i diversi valori empirici tendono tutti ai loro rispettivi valori teorici.
<div align="center"><img src="https://github.com/Perro2110/QueueSimulation/blob/main/Immagine%202024-08-27%20112717.png"></div>

#### Lunghezza teorica media della coda e del sistema
Le curve Ls e Lq rappresentano l’andamento teorico degli omonimi valori. I punti presenti sulle curve indicano i valori empirici trovati tramite 5 diverse simulazioni al variare di λ e μ.
<div align="center"><img src="https://github.com/Perro2110/QueueSimulation/blob/main/Immagine%202024-08-27%20112814.png"></div>

#### Confronto dati di un sistema a Coda M/M/7, con lambda = 4.9 e  mu = 2.3 Tempo di Simulazione: 8 ore
<div align="center"><img src="https://github.com/Perro2110/QueueSimulation/blob/main/Immagine%202024-08-27%20112832.png"></div>

### Conclusioni:
A seguito della visione e analisi dei dati e dei grafici si conclude che il simulatore si comporta come previsto. 
