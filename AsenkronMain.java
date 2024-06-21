import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsenkronMain2 {

    // Koltukların durumu: 0 boş, 1 dolu
    private static int[] seats = {0, 0, 0, 0, 0};

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        // 3 tane yazar thread oluştur
        for (int i = 1; i <= 3; i++) {
            final int writerId = i;
            executorService.execute(() -> bookSeat(0, writerId));
        }

        executorService.shutdown();
    }

    private static void bookSeat(int seatNo, int writerId) {
        synchronized (AsenkronMain2.class) {
            System.out.println("Zaman: " + System.currentTimeMillis() + " Writer" + writerId + " koltuk " + seatNo + " için rezervasyon yapmaya çalışıyor.");
            System.out.println("Zaman: " + System.currentTimeMillis() + " Writer" + writerId + " mevcut koltuk durumunu kontrol ediyor. Koltukların durumu: ");
            printSeats();

            // Asenkron olarak aynı koltuğu rezerve etmelerine izin ver
            try {
                Thread.sleep(100); // Gecikmeyi simüle etmek için
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            seats[seatNo] = writerId;
            System.out.println("Zaman: " + System.currentTimeMillis() + " Writer" + writerId + " koltuk numarası " + seatNo + " için başarılı bir şekilde rezervasyon yaptı.");
            printSeats();
        }
    }

    private static void printSeats() {
        for (int i = 0; i < seats.length; i++) {
            System.out.print("Koltuk No " + i + ": " + seats[i] + " ");
        }
        System.out.println();
    }
}
