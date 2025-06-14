# ThatTicket.com - Bilet Rezervasyon Sistemi

## Proje Hakkında
ThatTicket.com, otobüs ve uçak seferleri için bilet rezervasyonu yapılabilen bir Java Swing tabanlı masaüstü uygulamasıdır. Sistem, müşterilerin sefer arama, bilet rezervasyonu yapma ve admin kullanıcılarının sefer yönetimi yapabilmesine olanak sağlar.

## Proje Yapısı

### 1. Models Paketi
Temel veri modellerini içerir:

- **User**: Temel kullanıcı sınıfı
  - **Customer**: Müşteri sınıfı (User'dan türetilmiş)
  - **Admin**: Yönetici sınıfı (User'dan türetilmiş)
- **Voyage**: Sefer bilgilerini tutan sınıf
- **Reservation**: Rezervasyon bilgilerini tutan sınıf
- **Notification**: Bildirim bilgilerini tutan sınıf

### 2. Services Paketi
İş mantığı ve veritabanı işlemlerini yöneten servisler:

- **DatabaseService**: Veritabanı bağlantısı ve CRUD işlemleri
- **Admin**: Admin işlemlerini yöneten servis
  - Observer pattern ile müşterilere bildirim gönderme
  - Command pattern ile sefer ekleme/silme işlemleri
- **CustomerService**: Müşteri işlemlerini yöneten servis
  - Rezervasyon işlemleri
  - Bildirim tercihleri yönetimi

### 3. View Paketi
Kullanıcı arayüzü bileşenleri:

- **MainView**: Ana uygulama penceresi
- **LoginPanel**: Giriş ekranı
- **RegisterPanel**: Kayıt ekranı
- **TripCardPanel**: Sefer kartı bileşeni
- **EditVoyagePanel**: Sefer düzenleme ekranı
- **ReservationPanel**: Rezervasyon ekranı
- **NotificationPanel**: Bildirim ekranı

### 4. Factorys Paketi
Factory pattern implementasyonları:

- **VoyageFactory**: Sefer nesneleri oluşturan factory
- **UserFactory**: Kullanıcı nesneleri oluşturan factory

### 5. Commands Paketi
Command pattern implementasyonları:

- **Command**: Komut arayüzü
- **AddVoyageCommand**: Sefer ekleme komutu
- **DeleteVoyageCommand**: Sefer silme komutu
- **CommandCaller**: Komutları yöneten sınıf

## Kullanılan Design Pattern'ler

### 1. Observer Pattern
- **Kullanım Amacı**: Müşterilere yeni sefer bildirimleri göndermek
- **Implementasyon**:
  - `Admin` sınıfı Subject (gözlemlenen) olarak davranır
  - `Customer` sınıfı Observer (gözlemci) olarak davranır
  - Yeni sefer eklendiğinde tüm müşterilere bildirim gönderilir

### 2. Command Pattern
- **Kullanım Amacı**: Sefer ekleme/silme işlemlerini yönetmek
- **Implementasyon**:
  - `Command` arayüzü temel komut yapısını tanımlar
  - `AddVoyageCommand` ve `DeleteVoyageCommand` somut komutları uygular
  - `CommandCaller` komutları yönetir ve çalıştırır

### 3. Factory Pattern
- **Kullanım Amacı**: Nesne oluşturma işlemlerini merkezileştirmek
- **Implementasyon**:
  - `VoyageFactory`: Sefer nesneleri oluşturur
  - `UserFactory`: Kullanıcı nesneleri oluşturur

### 4. MVC (Model-View-Controller) Pattern
- **Kullanım Amacı**: Uygulama mimarisini düzenlemek
- **Implementasyon**:
  - Model: Models paketi
  - View: View paketi
  - Controller: Services paketi

## Veritabanı Yapısı

### Tablolar:
1. **users**
   - id, name, email, password, user_type
2. **voyages**
   - id, type, firm, origin, destination, start_time, arrival_time, seat_count, price, seat_arrangement
3. **reservations**
   - id, user_id, voyage_id, seat_number, created_at
4. **notifications**
   - id, user_id, message, created_at
5. **notification_preferences**
   - id, user_id, type, origin, destination, schedule, created_at

## Sınıf İlişkileri

1. **Admin - Customer İlişkisi**:
   - Admin, Observer pattern ile Customer'ları gözlemler
   - Yeni sefer eklendiğinde Customer'lara bildirim gönderir

2. **Voyage - Reservation İlişkisi**:
   - Bir Voyage birden çok Reservation'a sahip olabilir
   - Reservation, bir Voyage'a ve bir Customer'a bağlıdır

3. **User - Notification İlişkisi**:
   - Bir User birden çok Notification'a sahip olabilir
   - Notification'lar User'a özeldir

4. **View - Service İlişkisi**:
   - View sınıfları, Service sınıflarını kullanarak işlemleri gerçekleştirir
   - Service sınıfları, veritabanı işlemlerini yönetir

## Kurulum ve Çalıştırma

1. Java JDK 8 veya üzeri gereklidir
2. SQLite veritabanı otomatik olarak oluşturulur
3. Projeyi derlemek için:
   ```bash
   mvn clean install
   ```
4. Çalıştırmak için:
   ```bash
   java -jar target/ThatTicket.com.jar
   ```

## Geliştirici Notları

1. Yeni bir sefer tipi eklemek için:
   - `VoyageFactory`'ye yeni tip ekleyin
   - İlgili view sınıflarını güncelleyin

2. Yeni bir özellik eklemek için:
   - İlgili model sınıfını oluşturun
   - Service sınıfını güncelleyin
   - View sınıfını oluşturun/güncelleyin

3. Veritabanı değişiklikleri için:
   - `DatabaseService`'deki ilgili metodları güncelleyin
   - Migration script'i oluşturun 