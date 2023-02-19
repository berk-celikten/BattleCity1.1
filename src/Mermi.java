

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Mermi {	/// mermilerin sınıfı
	public static  int speedX = 12;
	public static  int speedY = 12;

	public static final int width = 10;
	public static final int length = 10;

	private int x, y;
	Yön diretion;

	private boolean good;
	private boolean live = true;

	private TankClient tc;

	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Image[] bulletImages = null;
	private static Map<String, Image> imgs = new HashMap<String, Image>(); ///düzgün bir indexleme için hashmap kullanırız
	static {
		///mermileri image dizisine static olarak oluştururuz
		bulletImages = new Image[] {
				tk.getImage(Mermi.class.getClassLoader().getResource(
						"images/bulletL.gif")),

				tk.getImage(Mermi.class.getClassLoader().getResource(
						"images/bulletU.gif")),

				tk.getImage(Mermi.class.getClassLoader().getResource(
						"images/bulletR.gif")),

				tk.getImage(Mermi.class.getClassLoader().getResource(
						"images/bulletD.gif")),

		};

		imgs.put("L", bulletImages[0]); ///hashmap lolarak kaydeder

		imgs.put("U", bulletImages[1]);

		imgs.put("R", bulletImages[2]);

		imgs.put("D", bulletImages[3]);

	}

	public Mermi(int x, int y, Yön dir) { ///mermi objesi
		this.x = x;
		this.y = y;
		this.diretion = dir;
	}

	public Mermi(int x, int y, boolean good, Yön dir, TankClient tc) { ///mermi objesi
		this(x, y, dir);
		this.good = good;
		this.tc = tc;
	}

	private void move() {///hareketleri yönlere göre

		switch (diretion) {
		case L:
			x -= speedX;
			break;

		case U:
			y -= speedY;
			break;

		case R:
			x += speedX; 
			break;

		case D:
			y += speedY;
			break;

		case STOP:
			break;
		}

		if (x < 0 || y < 0 || x > TankClient.Fram_width
				|| y > TankClient.Fram_length) {
			live = false;
		}
	}

	public void draw(Graphics g) {
		if (!live) {
			tc.bullets.remove(this);
			return;				///yaşamıyor ise çizme
		}

		switch (diretion) { 
		case L:
			g.drawImage(imgs.get("L"), x, y, null);///yönlere göre çiz
			break;

		case U:
			g.drawImage(imgs.get("U"), x, y, null);
			break;

		case R:
			g.drawImage(imgs.get("R"), x, y, null);
			break;

		case D:
			g.drawImage(imgs.get("D"), x, y, null);
			break;

		}

		move(); ///hareket fonksiyonunu çağır
	}

	public boolean isLive() { 
		return live;
	}

	public Rectangle getRect() {
		return new Rectangle(x, y, width, length);
	}

	public boolean hitTanks(List<Tank> tanks) {
		for (int i = 0; i < tanks.size(); i++) {
			if (hitTank(tanks.get(i))) {
				return true;
			}
		}
		return false;
	}

	public boolean hitTank(Tank t) { ////tankı vurma metodu

		if (this.live && this.getRect().intersects(t.getRect()) && t.isLive()
				&& this.good != t.isGood()) {

			TankPatlama e = new TankPatlama(t.getX(), t.getY(), tc); 
			tc.bombTanks.add(e);
			if (t.isGood()) {
				t.setLife(t.getLife() - 50); 
				if (t.getLife() <= 0)
					t.setLive(false); 
			} else {
				t.setLive(false); 
			}

			this.live = false;

			return true; 
		}
		return false;
	}

	public boolean hitWall(Duvar w) { 
		if (this.live && this.getRect().intersects(w.getRect())) {
			this.live = false;
			this.tc.otherWall.remove(w); 
			this.tc.homeWall.remove(w);
			return true;
		}
		return false;
	}
	public boolean hitBullet(Mermi w){
		if (this.live && this.getRect().intersects(w.getRect())){
			this.live=false;
			this.tc.bullets.remove(w);
			return true;
		}
		return false;
	}
	public boolean hitWall(MetalDuvar w) { 
		if (this.live && this.getRect().intersects(w.getRect())) {
			this.live = false;
			return true;
		}
		return false;
	}

	public boolean hitHome() { 
		if (this.live && this.getRect().intersects(tc.home.getRect())) {
			this.live = false;
			this.tc.home.setLive(false); 
			return true;
		}
		return false;
	}

}
