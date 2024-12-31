import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int tahtaGenisligi = 360;
    int tahtaYuksekligi = 640;

    // resimler
    Image arkaPlanResmi;
    Image kusResmi;
    Image ustBoruResmi;
    Image altBoruResmi;

    // kuş sınıfı
    int kusX = tahtaGenisligi / 8;
    int kusY = tahtaGenisligi / 2;
    int kusGenisligi = 34;
    int kusYuksekligi = 24;

    class Kus {
        int x = kusX;
        int y = kusY;
        int genislik = kusGenisligi;
        int yukseklik = kusYuksekligi;
        Image resim;

        Kus(Image resim) {
            this.resim = resim;
        }
    }

    // boru sınıfı
    int boruX = tahtaGenisligi;
    int boruY = 0;
    int boruGenisligi = 64;  // ölçek 1/6
    int boruYuksekligi = 512;

    class Boru {
        int x = boruX;
        int y = boruY;
        int genislik = boruGenisligi;
        int yukseklik = boruYuksekligi;
        Image resim;
        boolean gecildi = false;

        Boru(Image resim) {
            this.resim = resim;
        }
    }

    // oyun mantığı
    Kus kus;
    int hizX = -4; // boruları sola hareket ettirme hızı (kuşun sağa hareketini simüle eder)
    int hizY = 0; // kuşun yukarı/aşağı hareket hızı.
    int yercekimi = 1;

    ArrayList<Boru> borular;
    Random rastgele = new Random();

    Timer oyunDongusu;
    Timer boruYerlesimZamani;
    boolean oyunBitti = false;
    double puan = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(tahtaGenisligi, tahtaYuksekligi));
        // setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        // resimleri yükle
        arkaPlanResmi = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        kusResmi = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        ustBoruResmi = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        altBoruResmi = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        // kuş
        kus = new Kus(kusResmi);
        borular = new ArrayList<Boru>();

        // boruları yerleştirme zamanlayıcısı
        boruYerlesimZamani = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                borulariYerlesitir();
            }
        });
        boruYerlesimZamani.start();

        // oyun zamanlayıcısı
        oyunDongusu = new Timer(1000 / 60, this); // zamanlayıcının başlatılma süresi, milisaniye cinsinden çerçeve aralığı
        oyunDongusu.start();
    }

    void borulariYerlesitir() {
        int rastgeleBoruY = (int) (boruY - boruYuksekligi / 4 - Math.random() * (boruYuksekligi / 2));
        int boslukAraligi = tahtaYuksekligi / 4;

        Boru ustBoru = new Boru(ustBoruResmi);
        ustBoru.y = rastgeleBoruY;
        borular.add(ustBoru);

        Boru altBoru = new Boru(altBoruResmi);
        altBoru.y = ustBoru.y + boruYuksekligi + boslukAraligi;
        borular.add(altBoru);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        ciz(g);
    }

    public void ciz(Graphics g) {
        // arka plan
        g.drawImage(arkaPlanResmi, 0, 0, this.tahtaGenisligi, this.tahtaYuksekligi, null);

        // kuş
        g.drawImage(kusResmi, kus.x, kus.y, kus.genislik, kus.yukseklik, null);

        // borular
        for (int i = 0; i < borular.size(); i++) {
            Boru boru = borular.get(i);
            g.drawImage(boru.resim, boru.x, boru.y, boru.genislik, boru.yukseklik, null);
        }

        // puan
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (oyunBitti) {
            g.drawString("Oyun Bitti: " + String.valueOf((int) puan), 10, 35);
        } else {
            g.drawString(String.valueOf((int) puan), 10, 35);
        }

    }

    public void hareket() {
        // kuş
        hizY += yercekimi;
        kus.y += hizY;
        kus.y = Math.max(kus.y, 0); // mevcut kuş yüksekliğine yerçekimi uygula, yüksekliği tuvalin üstüyle sınırla

        // borular
        for (int i = 0; i < borular.size(); i++) {
            Boru boru = borular.get(i);
            boru.x += hizX;

            if (!boru.gecildi && kus.x > boru.x + boru.genislik) {
                puan += 0.5; // 0.5 çünkü 2 boru var! bu yüzden 0.5*2 = 1, her boru çifti için 1 puan
                boru.gecildi = true;
            }

            if (carpisma(kus, boru)) {
                oyunBitti = true;
            }
        }

        if (kus.y > tahtaYuksekligi) {
            oyunBitti = true;
        }
    }

    boolean carpisma(Kus a, Boru b) {
        return a.x < b.x + b.genislik &&   // a'nın sol üst köşesi b'nin sağ üst köşesine ulaşmıyor
                a.x + a.genislik > b.x &&   // a'nın sağ üst köşesi b'nin sol üst köşesini geçiyor
                a.y < b.y + b.yukseklik &&  // a'nın sol üst köşesi b'nin sol alt köşesine ulaşmıyor
                a.y + a.yukseklik > b.y;    // a'nın sol alt köşesi b'nin sol üst köşesini geçiyor
    }

    @Override
    public void actionPerformed(ActionEvent e) { // oyunDongusu zamanlayıcısı tarafından her x milisaniyede bir çağırılır
        hareket();
        repaint();
        if (oyunBitti) {
            boruYerlesimZamani.stop();
            oyunDongusu.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            hizY = -9;

            if (oyunBitti) {
                // oyunu tekrar başlatma: koşulları sıfırla
                kus.y = kusY;
                hizY = 0;
                borular.clear();
                oyunBitti = false;
                puan = 0;
                oyunDongusu.start();
                boruYerlesimZamani.start();
            }
        }
    }

    // gerekli değil
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
