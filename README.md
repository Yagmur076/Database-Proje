Kullanılacak Uygulamalar:
  1. MySQL Workbench (https://youtu.be/5OdVJbNCSso?si=V2nBii6WAR4u8w4v&t=142)
  2. Eclipse
  3. JAR (https://dev.mysql.com/downloads/connector/j/)
     //JAR dosyası için "Platform Independent" operating system gerekli, ZIP olarak indirilinecek (2. seçenek)

Nasıl Kullanılıcak:
  1. MySQL Installer'da "Add..." kısmına girip Application kısmında Workbench'in en üstteki sürümü indirilicek.
  2. MySQL üzerinden yeni "MySQL Connections"da yeni host kurulucak (eski varsa silinecek)
  3. MySQL Database üzerinden "Connect Database"
  4. SQL kodu ayni şekilde yazılıcak.
  5. Eğer hata verirse tekrardan yeni host kurulup o host üzerinden bağlanılacak
  6. Eclipse'te Yeni Java projesi üzerinde src dosyasına sağ tık yapıp "New Pakcage" kısmına Java kodu yazılıcak.
  7. İndirilen ZIP'in içindeki "mysql-connector-j" dosyası masaüstüne kopyalanacak.
  8. Eclipse üzerinden Java projesinin üstüne sağ tıklanıp "Build Path" üzerinden "Add External Archives" ile masaüstündeki JAR dosyası eklenecek
  9. Son olarak Java kodunun içindeki User_DB ve User_Password kısmı yapan kişinin host ismi ve şifresi ile değiştirilicek.
  
