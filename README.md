# RrjetaKompjuterike_gr2b_12
🧠 Përshkrimi

Ky projekt është një aplikacion i zhvilluar në Java që implementon një sistem komunikimi Client–Server duke përdorur protokollin UDP, së bashku me një HTTP server për shfaqjen e statistikave të sistemit. Serveri dëgjon në portën 1234 dhe pranon mesazhe nga klientët, duke i menaxhuar ata në bazë të roleve admin dhe user. Përveç komunikimit UDP, serveri ekzekuton edhe një thread të veçantë që hap një HTTP server në portën 8080 për të shfaqur statistika si klientët aktivë, IP adresat dhe mesazhet e dërguara.

🖥️ Serveri

Serveri mban në memorie një listë të klientëve aktivë, mesazheve dhe roleve të tyre. Çdo klient regjistrohet automatikisht kur lidhet për herë të parë dhe monitorohet me një sistem timeout prej 30 sekondash nëse bëhet joaktiv. Roli i klientit përcakton nivelin e aksesit, ku admin ka privilegje të plota mbi komandat e sistemit, ndërsa user ka akses të kufizuar vetëm në funksione bazike.

📂 Menaxhimi i File-ve

Sistemi mbështet disa komanda për menaxhimin e file-ve në server, duke përfshirë leximin, listimin, fshirjen, kërkimin, shfaqjen e informacionit, upload dhe download të file-ve. Disa nga këto operacione janë të kufizuara vetëm për admin për arsye sigurie dhe kontrolli. Klienti mund të dërgojë përmbajtje nga file lokal në server ose të shkarkojë file nga serveri dhe t’i ruajë në kompjuterin e tij.

👤 Klienti

Nga ana e klientit, aplikacioni lidhet me serverin në adresën 127.0.0.1:1234 dhe lejon komunikim interaktiv përmes komandave. Përdoruesi zgjedh fillimisht rolin e tij dhe më pas mund të dërgojë komanda sipas privilegjeve që ka. Klienti gjithashtu trajton upload dhe download të file-ve duke lexuar ose ruajtur përmbajtjen lokalisht.

📊 HTTP Statistics Server

Serveri përfshin gjithashtu një HTTP server në portën 8080, i cili ofron endpoint-in /stats. Ky endpoint shfaq:

Klientët aktivë 🟢
IP adresat e tyre 🌐
Mesazhet e dërguara 💬
🔥 Përmbledhje

Në përgjithësi, projekti paraqet një implementim praktik të komunikimit në rrjet duke kombinuar:

⚡ UDP për komunikim të shpejtë
🌍 HTTP server për monitorim
🔐 Menaxhim rolesh (admin/user)
📁 Operacione mbi file

📝 Shënim
Ky projekt është bërë për qëllime mësimore dhe tregon bazat e komunikimit në rrjet me UDP dhe menaxhimin e klientëve.

👥 Autorët gr.12
Erion Meshi,
Arbnor Dragaj,
Erdoart Basha,
Orlind Bajraktari.
