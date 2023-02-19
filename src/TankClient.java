
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.JOptionPane;

public class TankClient extends Frame implements ActionListener {

	
	private static final long serialVersionUID = 1L;
	public static final int Fram_width = 900; /////////////////////// 800e 600lük ekran olarak ya da başka türlü
												/////////////////////// değiştirilebilir
	public static final int Fram_length = 900;
	public static boolean printable = true;
	MenuBar jmb = null;
	Menu jm4 = null;
	MenuItem jmi6 = null, jmi7 = null, jmi8 = null, jmi9 = null;
	Image screenImage = null;

	Tank homeTank = new Tank(300, 560, true, Yön.STOP, this, 1); /// kullandığımız tank

	Boolean Player2 = false;
	GetCan blood = new GetCan();
	Karargah home = new Karargah(373, 557, this);
	Boolean win = false, lose = false;
	
	List<Tank> tanks = new ArrayList<Tank>();///// tankların listesi
	List<TankPatlama> bombTanks = new ArrayList<TankPatlama>();
	List<Mermi> bullets = new ArrayList<Mermi>();
	List<Agac> trees = new ArrayList<Agac>();
	List<Duvar> homeWall = new ArrayList<Duvar>();
	List<Duvar> otherWall = new ArrayList<Duvar>();/// duvar listesi
	List<MetalDuvar> metalWall = new ArrayList<MetalDuvar>();

	public void update(Graphics g) {   //// her kareyi çizen metot

		screenImage = this.createImage(Fram_width, Fram_length);

		Graphics gps = screenImage.getGraphics();
		Color c = gps.getColor();
		gps.setColor(Color.GRAY);
		gps.fillRect(0, 0, Fram_width, Fram_length);
		gps.setColor(c);
		framPaint(gps);
		g.drawImage(screenImage, 0, 0, null);
	}

	public void framPaint(Graphics g) {//// her kareyi çizen metot

		Color c = g.getColor();
		g.setColor(Color.green);

		Font f1 = g.getFont();
		g.setFont(new Font("Times New Roman", Font.BOLD, 20));
		g.drawString("Kalan Tanklar: ", 200, 70); //// kalan tank sayısının cümlesini yazar

		g.setFont(new Font("Times New Roman", Font.BOLD, 30));
		g.drawString("" + tanks.size(), 400, 70); //// kaç adet tank kaldığını yazar

		g.setFont(new Font("Times New Roman", Font.BOLD, 20));
		g.drawString("Can: ", 580, 70);//// kalan canın cümlesini yazar

		g.setFont(new Font("Times New Roman", Font.BOLD, 30));
		g.drawString("" + homeTank.getLife(), 650, 70);//// tankın can sayısını yazar

		g.setFont(f1);
		if (true) {
			if (tanks.size() == 0 && home.isLive() && homeTank.isLive() && lose == false) { /// hiç tank kalmadı mı,
																							/// kalan tank sayısı 0 mı /
																							/// karargah ayakta mı /
																							/// kullandığımız tank
																							/// yaşıyor mu / oyun
																							/// kaybedilmemiş mi
				Font f = g.getFont();
				g.setFont(new Font("Times New Roman", Font.BOLD, 60));
				this.otherWall.clear();
				this.metalWall.clear();
				
				this.trees.clear();
				this.homeWall.clear();
				g.drawString("Kazandın", 200, 300);
				g.setFont(f);
				win = true;
			}

			if (homeTank.isLive() == false && win == false) {//// tank patladıysa ve kazanmadıysa çalış
				Font f = g.getFont();
				g.setFont(new Font("Times New Roman", Font.BOLD, 40));
				tanks.clear(); /// bütün tankları sil
				bullets.clear(); /// mermileri sil
				homeWall.clear(); ///
				g.drawString("Kaybettin", 200, 300);
				lose = true;
				g.setFont(f);
			}
		}
		g.setColor(c);

		

		home.draw(g);
		homeTank.draw(g);
		homeTank.eat(blood);

		for (int i = 0; i < bullets.size(); i++) { //// bütün mermileri kontrol eder
			Mermi m = bullets.get(i);
			m.hitTanks(tanks);
			m.hitTank(homeTank);

			m.hitHome();
			for (int j = 0; j < bullets.size(); j++) {
				if (i == j)
					continue;
				Mermi bts = bullets.get(j);
				m.hitBullet(bts);
			}
			for (int j = 0; j < metalWall.size(); j++) { /// metal duvar
				MetalDuvar mw = metalWall.get(j);
				m.hitWall(mw);
			}

			for (int j = 0; j < otherWall.size(); j++) {
				Duvar w = otherWall.get(j);
				m.hitWall(w);
			}

			for (int j = 0; j < homeWall.size(); j++) {
				Duvar cw = homeWall.get(j);
				m.hitWall(cw);
			}
			m.draw(g);
		}

		for (int i = 0; i < tanks.size(); i++) {
			Tank t = tanks.get(i);

			for (int j = 0; j < homeWall.size(); j++) {
				Duvar cw = homeWall.get(j);
				t.collideWithWall(cw);
				cw.draw(g);
			}
			for (int j = 0; j < otherWall.size(); j++) {
				Duvar cw = otherWall.get(j);
				t.collideWithWall(cw);
				cw.draw(g);
			}
			for (int j = 0; j < metalWall.size(); j++) {
				MetalDuvar mw = metalWall.get(j);
				t.collideWithWall(mw);
				mw.draw(g);
			}
			

			t.collideWithTanks(tanks);
			t.collideHome(home);

			t.draw(g);
		}

		// blood.draw(g);

		for (int i = 0; i < trees.size(); i++) {
			Agac tr = trees.get(i);
			tr.draw(g);
		}

		for (int i = 0; i < bombTanks.size(); i++) {
			TankPatlama bt = bombTanks.get(i);
			bt.draw(g);
		}

		for (int i = 0; i < otherWall.size(); i++) {
			Duvar cw = otherWall.get(i);
			cw.draw(g);
		}

		for (int i = 0; i < metalWall.size(); i++) {
			MetalDuvar mw = metalWall.get(i);
			mw.draw(g);
		}

		homeTank.collideWithTanks(tanks);
		homeTank.collideHome(home);

		for (int i = 0; i < metalWall.size(); i++) {
			MetalDuvar w = metalWall.get(i);
			homeTank.collideWithWall(w);

			w.draw(g);
		}

		for (int i = 0; i < otherWall.size(); i++) {
			Duvar cw = otherWall.get(i);
			homeTank.collideWithWall(cw);

			cw.draw(g);
		}

		for (int i = 0; i < homeWall.size(); i++) {
			Duvar w = homeWall.get(i);
			homeTank.collideWithWall(w);

			w.draw(g);
		}

	}

	public TankClient() {
		// printable = false;

		jmb = new MenuBar();

		jm4 = new Menu("Seviye");
		// jm5 = new Menu("Addition");

		jmi6 = new MenuItem("Kolay");
		jmi7 = new MenuItem("Normal");
		jmi8 = new MenuItem("Zor");
		jmi9 = new MenuItem("Çok Zor");

		// jmi11= new MenuItem("Join other's game");

		// Levels
		jm4.add(jmi6);
		jm4.add(jmi7);
		jm4.add(jmi8);
		jm4.add(jmi9);

		// jm5.add(jmi11);

		jmb.add(jm4);
		// jmb.add(jm5);

		jmi6.addActionListener(this);
		jmi6.setActionCommand("Easy");
		jmi7.addActionListener(this);
		jmi7.setActionCommand("Normal");
		jmi8.addActionListener(this);
		jmi8.setActionCommand("Classic");
		jmi9.addActionListener(this);
		jmi9.setActionCommand("Expert");

		// jmi11.addActionListener(this);
		// jmi11.setActionCommand("Join");

		this.setMenuBar(jmb);
		this.setVisible(true);

		////////////////////////////////////
		////////////////////////////////////
		///////////////////////////////
		////// harita tasarımı//////////////////
		///////////////////////
		/////////////////////////////
		/////////////

		for (int i = 0; i < 10; i++) { //// karargahın etrafındaki duvar
			if (i < 4)
				homeWall.add(new Duvar(350, 580 - 21 * i, this));
			else if (i < 7)
				homeWall.add(new Duvar(372 + 22 * (i - 4), 517, this));

		}

		for (int i = 0; i < 32; i++) {
			if (i < 12) {
				otherWall.add(new Duvar(250 + 21 * i, 300, this));

				otherWall.add(new Duvar(250 + 21 * i, 700, this));

				otherWall.add(new Duvar(502 + 21 * i, 850, this));

				otherWall.add(new Duvar(600 + 21 * i, 200, this));
				otherWall.add(new Duvar(600 + 21 * i, 180, this));

				otherWall.add(new Duvar(100, 400 + 21 * i, this));
				otherWall.add(new Duvar(120, 400 + 21 * i, this));
				otherWall.add(new Duvar(140, 400 + 21 * i, this));
				otherWall.add(new Duvar(160, 400 + 21 * i, this));
				otherWall.add(new Duvar(180, 400 + 21 * i, this));
			} else if (i < 32) {

			}
		}
//		for (int i = 0; i < 32; i++) {
//			if (i < 16) {
//				otherWall.add(new CommonWall(200 + 21 * i, 300, this)); 
//				otherWall.add(new CommonWall(500 + 21 * i, 180, this));
//				otherWall.add(new CommonWall(200, 400 + 21 * i, this));
//				otherWall.add(new CommonWall(500, 400 + 21 * i, this));
//			} else if (i < 32) {
//				otherWall.add(new CommonWall(200 + 21 * (i - 16), 320, this));
//				otherWall.add(new CommonWall(500 + 21 * (i - 16), 220, this));
//				otherWall.add(new CommonWall(222, 400 + 21 * (i - 16), this));
//				otherWall.add(new CommonWall(522, 400 + 21 * (i - 16), this));
//			}
//		}

		for (int i = 0; i < 20; i++) {
			if (i < 5) {
				metalWall.add(new MetalDuvar(400 + 30 * i, 150, this));
				metalWall.add(new MetalDuvar(180, 220 + 30 * i, this));

				metalWall.add(new MetalDuvar(450, 500 + 20 * (i), this));
			}

		}

//		for (int i = 0; i < 20; i++) { 
//			if (i < 10) {
//				metalWall.add(new MetalWall(140 + 30 * i, 150, this));
//				metalWall.add(new MetalWall(600, 400 + 20 * (i), this));
//			} else if (i < 20)
//				metalWall.add(new MetalWall(140 + 30 * (i - 10), 180, this));
//			
//		}

		for (int i = 0; i < 4; i++) {
			if (i < 4) {
				trees.add(new Agac(0, 360 + 30 * i, this));

				trees.add(new Agac(660, 360 + 30 * i, this));
				trees.add(new Agac(660, 480 + 30 * i, this));
			}

		}

//		for (int i = 0; i < 4; i++) { 
//			if (i < 4) {
//				trees.add(new Tree(0 + 30 * i, 360, this));
//				trees.add(new Tree(220 + 30 * i, 360, this));
//				trees.add(new Tree(440 + 30 * i, 360, this));
//				trees.add(new Tree(660 + 30 * i, 360, this));
//			}
//
//		}

		

		

//		theRiver.add(new River(85, 100, this));

		for (int i = 0; i < 12; i++) {//// tank oluşturur
			if (i < 5)
				tanks.add(new Tank(150 + 70 * i, 40, false, Yön.D, this, 0));//// yan yana tank oluşturur pixel
																					//// aralıklarına göre
			else if (i < 8)
				tanks.add(new Tank(700, 300 + 50 * (i - 6), false, Yön.D, //// alt alta en sağda oluşturur
						this, 0));
			else //// alt alta solda oluşturur
				tanks.add(new Tank(10, 50 * (i - 6), false, Yön.D, this, 0));

		}

		this.setSize(Fram_width, Fram_length);/// çözünürlüğü belirliyo
		this.setLocation(280, 50);
		this.setTitle("Battle City Developed by Berk // Referenced from Jignesh "); /// v1.1.0 - by Jignesh

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		this.setResizable(false);// kullanıcı tekrardan sizını değiştiremez
		this.setBackground(Color.GREEN);// arka planı yeşil yapar
		this.setVisible(true);

		this.addKeyListener(new KeyMonitor());
		new Thread(new PaintThread()).start(); //// thread //belli zaman aralığında ekrana tekrar çizim yapar
	}

	

	private class PaintThread implements Runnable {//
		public void run() {

			while (printable) {/// belli zaman aralığında çalıştırılır
				repaint();
				try {
					Thread.sleep(50);///// uyut ////saniyede kaç kere çalışıcağını ayarlıyoruz //sleep
										///// fonksiyonundaki sayıyı arttırırsak daha yavaş çalışır daha çok uyur
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class KeyMonitor extends KeyAdapter {

		public void keyReleased(KeyEvent e) { /// tuşa basmayı bıraktığımızda çalışır
			homeTank.keyReleased(e);

		}

		public void keyPressed(KeyEvent e) {/// tuşa basıldığında çalışır
			homeTank.keyPressed(e);

		}

	}

	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("Easy")) {////// zorluk derecelerine göre tankların özellikleri
			Tank.count = 12;
			Tank.speedX = 6;
			Tank.speedY = 6;
			Mermi.speedX = 10;
			Mermi.speedY = 10;
			this.dispose();
			new TankClient();
		} else if (e.getActionCommand().equals("Normal")) {
			Tank.count = 12;
			Tank.speedX = 10;
			Tank.speedY = 10;
			Mermi.speedX = 12;
			Mermi.speedY = 12;
			this.dispose();
			new TankClient();

		} else if (e.getActionCommand().equals("Classic")) {
			Tank.count = 20;
			Tank.speedX = 14;
			Tank.speedY = 14;
			Mermi.speedX = 16;
			Mermi.speedY = 16;
			this.dispose();
			new TankClient();
		} else if (e.getActionCommand().equals("Expert")) {
			Tank.count = 20;
			Tank.speedX = 16;
			Tank.speedY = 16;
			Mermi.speedX = 18;
			Mermi.speedY = 18;
			this.dispose();
			new TankClient();
		}

	}
}
