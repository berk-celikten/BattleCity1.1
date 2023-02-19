

import java.awt.*;
import java.awt.event.*;
import java.util.*;



public class Tank {
	public static  int speedX = 6, speedY =6; 
	public static int count = 0;
	public static final int width = 35, length = 35;
	private Yön direction = Yön.STOP;///yönü belirlemek için değişken
	
	
	private Yön Kdirection = Yön.U; ///yön yukarı olarak başlar burda sadece ismini verdik// direk u harfiyle çalışmaya başladığı için anahtarımızda yukarı yönlü olarak başladı
	TankClient tc;
	private int player=0;
	private boolean good;
	private int x, y;
	private int oldX, oldY;
	private boolean live = true;
	private int life = 200;
	private int rate=1;
	private static Random r = new Random();
	private int step = r.nextInt(10)+5 ; 

	private boolean bL = false, bU = false, bR = false, bD = false;
	////tankın yönü için bl bu...
	

	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Image[] tankImags = null; //// image bulunamayabilirse diye throw catch yapılabilir 
	static {
		tankImags = new Image[] {
				tk.getImage(TankPatlama.class.getResource("Images/tank_fast_down_c0_t1_f.png")),
				tk.getImage(TankPatlama.class.getResource("Images/tank_fast_up_c0_t1_f.png")),
				tk.getImage(TankPatlama.class.getResource("Images/tank_fast_left_c0_t1_f.png")),
				tk.getImage(TankPatlama.class.getResource("Images/tank_fast_right_c0_t1_f.png")), 
				tk.getImage(TankPatlama.class.getResource("Images/tank_player1_down_c0_t1_s1.png")), 
				tk.getImage(TankPatlama.class.getResource("Images/tank_player1_up_c0_t1_s1.png")), 
				tk.getImage(TankPatlama.class.getResource("Images/tank_player1_left_c0_t1_s1.png")),
				tk.getImage(TankPatlama.class.getResource("Images/tank_player1_right_c0_t1_s1.png")), 
				
				};

	}

	public Tank(int x, int y, boolean good) {
		this.x = x;
		this.y = y;
		this.oldX = x;
		this.oldY = y;
		this.good = good;
	}

	public Tank(int x, int y, boolean good, Yön dir, TankClient tc, int player) {
		this(x, y, good);
		this.direction = dir;
		this.tc = tc;
		this.player=player;
		
		///tank objesi 
	}

	public void draw(Graphics g) {
		/////2d çizim yapar, grafik oluşturur
		if (!live) {
			if (!good) {
				tc.tanks.remove(this);
			}
			return;
		}
		//if (good)
			//new DrawBloodbBar().draw(g); 
		switch (Kdirection) {
		///tankın farklı resimlerine geçiş yapılır farklı yönlerde
							
		case D:
			if(player==1){	g.drawImage(tankImags[4], x, y, null);
			}
			else{
			g.drawImage(tankImags[0], x, y, null);}
			break;

		case U:
			if(player==1){	g.drawImage(tankImags[5], x, y, null);
			}else{
			g.drawImage(tankImags[1], x, y, null);}
			break;
		case L:if(player==1){	g.drawImage(tankImags[6], x, y, null);
		}else{
			g.drawImage(tankImags[2], x, y, null);}
			break;

		case R:if(player==1){	g.drawImage(tankImags[7], x, y, null);
		}else{
			g.drawImage(tankImags[3], x, y, null);}
			break;

		}

		move();   
		///hareket etme metodu çağrılır
	}

	void move() {

		this.oldX = x;
		this.oldY = y;

		switch (direction) {  
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

		if (this.direction != Yön.STOP) {
			
			///eşit değil ise yönünü değiştirir. parametredeki değişken this ile kullanılır
			this.Kdirection = this.direction;
		}

		if (x < 0)
			x = 0;
		if (y < 40)     
			y = 40;
		if (x + Tank.width > TankClient.Fram_width)  
			x = TankClient.Fram_width - Tank.width;
		if (y + Tank.length > TankClient.Fram_length)
			y = TankClient.Fram_length - Tank.length;

		if (!good) {      /////tank yapay zekası //////////////////////////
			Yön[] directons = Yön.values();
			if (step == 0) {                  
				step = r.nextInt(12) + 3;  
				int mod=r.nextInt(9);
				if (playertankaround()){
					if(x==tc.homeTank.x){ if(y>tc.homeTank.y) direction=directons[1];
					else if (y<tc.homeTank.y) direction=directons[3];
					}else if(y==tc.homeTank.y){ if(x>tc.homeTank.x) direction=directons[0];
					else if (x<tc.homeTank.x) direction=directons[2];
					}
					else{
						int rn = r.nextInt(directons.length);
						direction = directons[rn]; 
					}
					rate=2;
				}else if (mod==1){
					rate=1;
				}else if(1<mod&&mod<=3){
					rate=1;
				}else{
				int rn = r.nextInt(directons.length);
				direction = directons[rn]; 
				rate=1;}    
			}
			step--;
			if(rate==2){
				if (r.nextInt(40) > 35)
					this.fire();
			}else if (r.nextInt(40) > 38)
				this.fire();
		}
	}
	public boolean playertankaround(){
		int rx=x-15,ry=y-15;
		if((x-15)<0) rx=0;
		if((y-15)<0)ry=0;
		Rectangle a=new Rectangle(rx, ry,60,60);
		if (this.live && a.intersects(tc.homeTank.getRect())) {
		return true;	
		}
		return false;	
	}
	public int getzone(int x,int y){
		int tempx=x;
		int tempy=y;
		if (tempx<85&&tempy<300) return 11;
		else if(tempx>85&&tempx<140&&tempy>0&&tempy<100) return 9;
		else if(tempx>85&&tempx<140&&tempy>254&&tempy<300) return 10;
		else if(tempx>0&&tempx<200&&tempy>300&&tempy<715) return 12;
		else if(tempx>140&&tempx<400&&tempy>0&&tempy<150) return 7;
		else if(tempx>140&&tempx<400&&tempy>210&&tempy<300) return 8;
		else if(tempx>400&&tempx<500&&tempy>0&&tempy<300) return 6;
		else if(tempx>500&&tempy>0&&tempy<180) return 5;
		else if(tempx>500&&tempy>180&&tempy<300) return 4;
		else if(tempx>520&&tempx<600&&tempy>3000&&tempy<715) return 2;
		else if(tempx>600&&tempy>300&&tempy<715) return 3;
	return 1;
	}
	public int getdirect(int a,int b){
		if(b==13){
			
		}
		return 4;
	}
	private void changToOldDir() {  
		x = oldX;
		y = oldY;
	}

	public void keyPressed(KeyEvent e) {  
		////tuşa basıldığında tüm switch çalışır 
		int key = e.getKeyCode();
		
		switch (key) {
		
		
		case KeyEvent.VK_D:
			bR = true;
			break;
			
		case KeyEvent.VK_A:
			bL = true;
			break;
		
		case KeyEvent.VK_W:  
			bU = true;
			break;
		
		case KeyEvent.VK_S:
			bD = true;
			break;
		}
		
		decideDirection();
		///yön belirleme metdou çalışır
	}

	void decideDirection() {
		///yön belirleme metodu
		
		if (!bL && !bU && bR && !bD) 
			direction = Yön.R;
		///sağa çevirir

		else if (bL && !bU && !bR && !bD) 
			direction = Yön.L;
		////sola çevirir

		else if (!bL && bU && !bR && !bD) 
			direction = Yön.U;
		///yukarı çevirir

		else if (!bL && !bU && !bR && bD) 
			direction = Yön.D;
		///aşağı çevirir

		else if (!bL && !bU && !bR && !bD)
			direction = Yön.STOP; 
		///hiçbiri olmaz ise bir şey olmaz :) durur
		
	}

	public void keyReleased(KeyEvent e) {  
		///tuşa basmayı bıraktığımızda çalışır
		
		int key = e.getKeyCode();
		
		switch (key) {
		
		case KeyEvent.VK_F:
			fire();
			break;
			
		case KeyEvent.VK_D:
			bR = false;
			break;
		
		case KeyEvent.VK_A:
			bL = false;
			break;
		
		case KeyEvent.VK_W:
			bU = false;
			break;
		
		case KeyEvent.VK_S:
			bD = false;
			break;
			

		}
		
		decideDirection(); 
		///yönü belirleme metodu çalışır bu seçimden sonra her zaman
	}

	public Mermi fire() { 
		if (!live)
			return null;
		int x = this.x + Tank.width / 2 - Mermi.width / 2; 
		int y = this.y + Tank.length / 2 - Mermi.length / 2;
		Mermi m = new Mermi(x, y + 2, good, Kdirection, this.tc); //yeni mermi objesi oluşur
		tc.bullets.add(m);                                                
		return m;
	}


	public Rectangle getRect() {    // dörtgen oluşturur  
		return new Rectangle(x, y, width, length);
	}

	public boolean isLive() {
		return live;
	}

	public void setLive(boolean live) {
		this.live = live;
	}

	public boolean isGood() {
		return good;
	}

	public boolean collideWithWall(Duvar w) {  ///colide ekler
		if (this.live && this.getRect().intersects(w.getRect())) {
			 this.changToOldDir();    
			return true;
		}
		return false;
	}

	public boolean collideWithWall(MetalDuvar w) {   ///colide yapan //fizik ekleyen fonksiyon
		if (this.live && this.getRect().intersects(w.getRect())) {
			this.changToOldDir();     
			return true;
		}
		return false;
	}

	

	public boolean collideHome(Karargah h) {  
		if (this.live && this.getRect().intersects(h.getRect())) {
			this.changToOldDir();
			return true;
		}
		return false;
	}

	public boolean collideWithTanks(java.util.List<Tank> tanks) {
		for (int i = 0; i < tanks.size(); i++) {
			Tank t = tanks.get(i);
			if (this != t) {
				if (this.live && t.isLive()
						&& this.getRect().intersects(t.getRect())) {
					this.changToOldDir();
					t.changToOldDir();
					return true;
				}
			}
		}
		return false;
	}


	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	private class DrawBloodbBar {			///// can ///////
		public void draw(Graphics g) {
			Color c = g.getColor();
			g.setColor(Color.RED);
			g.drawRect(375, 585, width, 10);
			int w = width * life / 200;
			g.fillRect(375, 585, w, 10);
			g.setColor(c);
		}
	}

	public boolean eat(GetCan b) {
		if (this.live && b.isLive() && this.getRect().intersects(b.getRect())) {
			if(this.life<=100)
			this.life = this.life+100;      
			else
				this.life = 200;
			b.setLive(false);
			return true;
		}
		return false;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}