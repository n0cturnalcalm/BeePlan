import java.util.HashMap;
import java.util.Map;

public class Timetable {
    public Map<String, Boolean> timetable;

    // Constructor
    public Timetable() {
        timetable = new HashMap<>();
        generateTimetable();
    }

    // Timetable'ı oluşturma metodu
    public void generateTimetable() {
        for (int day = 1; day <= 6; day++) {
            for (int lec = 1; lec <= 8; lec++) {
                String key = day + "-" + lec; // Örnek: "1-1", "1-2"...
                timetable.put(key, true); // Varsayılan olarak "true" ekleniyor.
            }
        }
    }

    public boolean checkAvaliablity(String slot) {
        return timetable.get(slot);
    }

    public void changeStatus(String slot, boolean status) {
        this.timetable.put(slot, status);
    }

    public void printTimetable() {
        System.out.println("Timetable:");

        // Gün başlıklarını hizalı yazdır
        System.out.printf("%-12s", ""); // Boşluk için
        for (int day = 1; day <= 6; day++) {
            System.out.printf("%-10s", "Day " + day);
        }
        System.out.println();

        // Renk kodları
        final String RESET = "\u001B[0m";  // Varsayılan renk
        final String GREEN = "\u001B[32m"; // Yeşil (Dolu için)
        final String RED = "\u001B[31m";   // Kırmızı (Boş için)

        // Her ders saatine göre tabloyu doldur
        for (int lec = 1; lec <= 8; lec++) {
            System.out.printf("%-12s", "Lecture " + lec); // Lecture başlıklarını hizalı yazdır
            for (int day = 1; day <= 6; day++) {
                String key = day + "-" + lec;
                // Renk ve sembollerle yazdırma
                if (timetable.get(key)) {
                    System.out.printf(GREEN + "%-10s" + RESET, "■"); // Dolu: Yeşil ve "■"
                } else {
                    System.out.printf(RED + "%-10s" + RESET, "-");  // Boş: Kırmızı ve "-"
                }
            }
            System.out.println();
        }
    }

}
