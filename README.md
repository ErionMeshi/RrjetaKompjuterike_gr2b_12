# RrjetaKompjuterike_gr2b_12
🧠 Përshkrimi

Ky projekt është një implementim i thjeshtë i një sistemi klient-server duke përdorur UDP në Java.
Ideja kryesore është që serveri të komunikojë me disa klientë njëkohësisht, të pranojë mesazhe prej tyre dhe t’i menaxhojë ato.
Përveç kësaj, serveri ka edhe një HTTP server të vogël për të parë statistikat (si numri i klientëve dhe mesazhet).

⚙️ Çfarë bën serveri?
Pranon shumë klientë
Kufizon numrin maksimal të lidhjeve
Lexon dhe ruan mesazhet nga klientët
Mbyll lidhjen nëse klienti nuk dërgon asgjë për një kohë
Lejon rikonektimin
Jep qasje në file për një klient admin
Ka një HTTP endpoint (/stats) për monitorim

💻 Çfarë bëjnë klientët?
Lidhen me serverin përmes IP dhe portit
Dërgojnë dhe marrin mesazhe
Kanë role:
User → vetëm lexon
Admin → mund të bëjë komanda mbi file

🛠️ Komandat kryesore
/list
/read <file>
/upload <file>
/download <file>
/delete <file>
/search <keyword>
/info <file>

🚀 Si me e startu
javac Server.java Client.java
java Server
java Client

📝 Shënim
Ky projekt është bërë për qëllime mësimore dhe tregon bazat e komunikimit në rrjet me UDP dhe menaxhimin e klientëve.

👥 Autorët gr.12
Erion Meshi,
Arbnor Dragaj,
Erduard Basha,
Orlind Bajraktari.
