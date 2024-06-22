import java.util.ArrayList;
import java.util.List;

class Flight {
    String flightId;
    boolean[] seats;

    public Flight(String flightId, int numSeats) {
        this.flightId = flightId;
        this.seats = new boolean[numSeats];
    }

    public boolean querySeat(int seatNumber) {
        return seats[seatNumber];
    }

    public boolean koltukRezervasyonuYap(int koltukNumarasi) {
        if (!seats[koltukNumarasi]) {
            seats[koltukNumarasi] = true;
            return true;
        }
        return false;
    }

    public boolean rezervasyonIptalEt(int koltukNumarasi) {
        if (seats[koltukNumarasi]) {
            seats[koltukNumarasi] = false;
            return true;
        }
        return false;
    }
}

class Database {
    List<Flight> flights = new ArrayList<>();

    public void addFlight(Flight flight) {
        flights.add(flight);
    }

    public Flight getFlight(String flightId) {
        for (Flight flight : flights) {
            if (flight.flightId.equals(flightId)) {
                return flight;
            }
        }
        return null;
    }
}

class ReaderThread extends Thread {
    Database db;
    String flightId;

    public ReaderThread(Database db, String flightId, String name) {
        super(name);
        this.db = db;
        this.flightId = flightId;
    }

    public void run() {
        Flight flight = db.getFlight(flightId);
        if (flight != null) {
            StringBuilder seatsStatus = new StringBuilder();
            for (int i = 0; i < flight.seats.length; i++) {
                seatsStatus.append("Koltuk No ").append((char)('a' + i)).append(" : ").append(flight.querySeat(i) ? "1" : "0").append(" ");
            }
            System.out.println(Thread.currentThread().getName() + " koltuk durumunu kontrol ediyor. Koltuk durumu : ");
            System.out.println(seatsStatus.toString());
        }
    }
}

class WriterThread extends Thread {
    Database db;
    String flightId;
    int seatNumber;

    public WriterThread(Database db, String flightId, int seatNumber, String name) {
        super(name);
        this.db = db;
        this.flightId = flightId;
        this.seatNumber = seatNumber;
    }

    public void run() {
        Flight flight = db.getFlight(flightId);
        if (flight != null) {
            if (flight.koltukRezervasyonuYap(seatNumber)) {
                System.out.println(Thread.currentThread().getName() + " koltuk " + (char)('a' + seatNumber) + " için rezervasyon yapmaya çalışıyor.");
                System.out.println(Thread.currentThread().getName() + " koltuk " + (char)('a' + seatNumber) + " başarıyla rezerve edildi.");
            } else {
                System.out.println(Thread.currentThread().getName() + " koltuk " + (char)('a' + seatNumber) + " için rezervasyon yapmaya çalışıyor.");
                System.out.println(Thread.currentThread().getName() + " koltuk " + (char)('a' + seatNumber) + " rezerve edilemedi.");
            }
        }
    }
}

public class Asenkron {
    public static void main(String[] args) {
        Database db = new Database();
        Flight flight1 = new Flight("100", 5); // 5 koltuk
        Flight flight2 = new Flight("120", 5); // 5 koltuk
        Flight flight3 = new Flight("140", 5); // 5 koltuk
        db.addFlight(flight1);
        db.addFlight(flight2);
        db.addFlight(flight3);

        int sameSeat = 3; // 3 numaralı koltuk (sıfır indeksli)

        ReaderThread reader1 = new ReaderThread(db, "100", "reader-1");
        ReaderThread reader2 = new ReaderThread(db, "120", "reader-2");
        ReaderThread reader3 = new ReaderThread(db, "140", "reader-3");

        WriterThread writer1 = new WriterThread(db, "100", sameSeat, "writer-1");
        WriterThread writer2 = new WriterThread(db, "120", sameSeat, "writer-2");
        WriterThread writer3 = new WriterThread(db, "140", sameSeat, "writer-3");

        reader1.start();
        reader2.start();
        reader3.start();

        writer1.start();
        writer2.start();
        writer3.start();
    }
}
