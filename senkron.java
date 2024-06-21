import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Uçuş {
    String uçuşId;
    boolean[] koltuklar;
    private final ReentrantReadWriteLock kilit = new ReentrantReadWriteLock(true);

    public Uçuş(String uçuşId, int koltukSayısı) {
        this.uçuşId = uçuşId;
        this.koltuklar = new boolean[koltukSayısı];
    }

    public boolean koltukDurumu(int koltukNumarası) {
        kilit.readLock().lock();
        try {
            return koltuklar[koltukNumarası];
        } finally {
            kilit.readLock().unlock();
        }
    }

    public boolean rezervasyonYap(int koltukNumarası) {
        kilit.writeLock().lock();
        try {
            if (!koltuklar[koltukNumarası]) {
                koltuklar[koltukNumarası] = true;
                return true;
            }
            return false;
        } finally {
            kilit.writeLock().unlock();
        }
    }

    public boolean rezervasyonIptal(int koltukNumarası) {
        kilit.writeLock().lock();
        try {
            if (koltuklar[koltukNumarası]) {
                koltuklar[koltukNumarası] = false;
                return true;
            }
            return false;
        } finally {
            kilit.writeLock().unlock();
        }
    }
}

class Veritabanı {
    private final List<Uçuş> uçuşlar = new ArrayList<>();

    public void uçuşEkle(Uçuş uçuş) {
        uçuşlar.add(uçuş);
    }

    public Uçuş uçuşBul(String uçuşId) {
        for (Uçuş uçuş : uçuşlar) {
            if (uçuş.uçuşId.equals(uçuşId)) {
                return uçuş;
            }
        }
        return null;
    }
}

class KoltukSorgulamaThread extends Thread {
    private final Veritabanı vt;
    private final String uçuşId;

    public KoltukSorgulamaThread(Veritabanı vt, String uçuşId, String isim) {
        super(isim);
        this.vt = vt;
        this.uçuşId = uçuşId;
    }

    public void run() {
        Uçuş uçuş = vt.uçuşBul(uçuşId);
        if (uçuş != null) {
            StringBuilder koltukDurumları = new StringBuilder();
            for (int i = 0; i < uçuş.koltuklar.length; i++) {
                koltukDurumları.append("Koltuk No ").append(i).append(" : ").append(uçuş.koltukDurumu(i) ? "1" : "0").append(" ");
            }
            System.out.println(Thread.currentThread().getName() + " mevcut koltukları kontrol ediyor. Koltukların durumu: ");
            System.out.println(koltukDurumları.toString());
        }
    }
}

class KoltukRezervasyonThread extends Thread {
    private final Veritabanı vt;
    private final String uçuşId;
    private final int koltukNumarası;

    public KoltukRezervasyonThread(Veritabanı vt, String uçuşId, int koltukNumarası, String isim) {
        super(isim);
        this.vt = vt;
        this.uçuşId = uçuşId;
        this.koltukNumarası = koltukNumarası;
    }

    public void run() {
        Uçuş uçuş = vt.uçuşBul(uçuşId);
        if (uçuş != null) {
            if (uçuş.rezervasyonYap(koltukNumarası)) {
                System.out.println(Thread.currentThread().getName() + " " + koltukNumarası + " numaralı koltuğu rezerve etmeye çalışıyor.");
                System.out.println(Thread.currentThread().getName() + " " + koltukNumarası + " numaralı koltuğu başarıyla rezerve etti.");
            } else {
                System.out.println(Thread.currentThread().getName() + " " + koltukNumarası + " numaralı koltuğu rezerve etmeye çalışıyor.");
                System.out.println(Thread.currentThread().getName() + " " + koltukNumarası + " numaralı koltuğu rezerve edemedi, çünkü zaten rezerve edilmiş.");
            }
        }
    }
}

public class senkron {
    public static void main(String[] args) {
        Veritabanı vt = new Veritabanı();
        Uçuş uçuş1 = new Uçuş("Uçuş1", 5); 
        vt.uçuşEkle(uçuş1);


        new KoltukSorgulamaThread(vt, "Uçuş1", "okuyucu-1").start();
        new KoltukSorgulamaThread(vt, "Uçuş1", "okuyucu-2").start();
        new KoltukSorgulamaThread(vt, "Uçuş1", "okuyucu-3").start();


        try {
            Thread.sleep(100); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        new KoltukRezervasyonThread(vt, "Uçuş1", 1, "yazıcı-1").start();
        new KoltukRezervasyonThread(vt, "Uçuş1", 1, "yazıcı-2").start();
        new KoltukRezervasyonThread(vt, "Uçuş1", 1, "yazıcı-3").start();
    }
}
