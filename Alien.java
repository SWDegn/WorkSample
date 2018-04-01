/*
 * This class contains variables and methods used exclusively by alien objects
 * it Extends upon the Opponent class, which extends upon GameObject class and 
 * implements iMoveable.
 * As of version 1.0 this class holds the information needed to initialize the 
 * aliens at the start of the game.
 * As of version 3.0 this class contained the method for drawing a graphical 
 * representation of this object, as well as collision detection and handling 
 */

package com.mycompany.a4;
import com.codename1.charts.util.*;
import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;
import com.codename1.ui.geom.Point;

public class Alien extends Opponent implements iDrawable, iCollider, iMovingShape {
	private static final int STARTCOLOR=ColorUtil.BLUE;
	private static final int SPEEDCONST = 1;
	private static final int CIRCLE =3;
	private static final int THISGAMESOUND =1;
	
	private Circle alienBody;
	private Limbs [] limbs;
	private Rectangle head;
	private float armOffset =0;
	private float armIncrement =1;
	private float maxArmOffset =6;
	private float legOffset =0;
	private float legIncrement =2;
	private float maxLegOffset =16;
	
	public Alien() {
		setSpeed(5 * SPEEDCONST);
		setColor(STARTCOLOR);		
		setShape(CIRCLE);
		alienBody = new Circle(getSize());
		alienBody.translate ((float) getNewXLoc()
							 ,(float) getNewYLoc());
		alienBody.rotate (getDirection());
		alienBody.scale ((float)1.5, (float)2.5);
		limbs = new Limbs[4];
		limbs[0] = new Limbs(getSize(),getSize());
		limbs[0].scale((float)1,  (float)2.5);
		limbs[0].translate(getSizeX(), getSizeY()/4);	
		limbs[0].firstRotate(90);
		limbs[1] = new Limbs(getSize(),getSize());
		limbs[1].scale((float)1,  (float)2.5);
		limbs[1].translate(-getSizeX(), getSizeY()/4);
		limbs[1].firstRotate(270);
		limbs[2] = new Limbs(getSize(),getSize());
		limbs[2].scale((float)1,  (float)2.5);
		limbs[2].translate(getSizeX()/2, -getSizeY()/2);	
		limbs[3] = new Limbs(getSize(),getSize());
		limbs[3].scale((float)1,  (float)2.5);
		limbs[3].translate(-getSizeX()/2, -getSizeY()/2);
		head = new Rectangle(getSize(), getSize());
		head.scale(1, 1);;
		head.translate(0, getSizeY()/2);;
	}
	public float getXLoc() {
		return alienBody.getTransX();
	}
	public float getYLoc() {
		return alienBody.getTransY();
	}
	public float getScaleX() {
		return alienBody.getScaleX();
	}
	public float getScaleY() {
		return alienBody.getScaleY();
	}
	public int getSizeX() {
		return (int)(getSize() * getScaleX()); 
	}
	public int getSizeY() {
		return (int)(getSize() * getScaleY()); 
	}
	public void changeTranslation(double xVal, double yVal) {
		alienBody.translate((float)(xVal), (float)(yVal));
	}
	public void setDirection(int value) {
		alienBody.rotate (value);
		super.setDirection(value);
	}
	public void setTranslation(double xVal, double yVal) {
		alienBody.setTranslate ((float) xVal
				 ,(float) yVal);
	}
	public void draw(Graphics g, Point pCmpRelPrnt, Point pCmpRelScreen) {
		Transform gXform = Transform.makeIdentity();
		g.getTransform(gXform);
		Transform gOrigXform = gXform.copy(); 
		g.setColor(getColor());
		alienBody.draw(g, pCmpRelPrnt, pCmpRelScreen);
		limbs[0].draw(g, pCmpRelPrnt, pCmpRelScreen);
		limbs[1].draw(g, pCmpRelPrnt, pCmpRelScreen);
		limbs[2].draw(g, pCmpRelPrnt, pCmpRelScreen);
		limbs[3].draw(g, pCmpRelPrnt, pCmpRelScreen);
		head.draw(g, pCmpRelPrnt, pCmpRelScreen);
		g.setTransform(gOrigXform);
		
	}
	public void dance() {
		limbs[0].secondRotate(armIncrement);
		limbs[1].secondRotate(armIncrement);
		limbs[2].firstRotate(legIncrement);
		limbs[3].firstRotate(-legIncrement);
		head.rotate(6);
		armOffset+=armIncrement;
		legOffset+=legIncrement;
		if(Math.abs(armOffset) >= maxArmOffset) {
			armIncrement*=-1;
		}
		if(Math.abs(legOffset) >= maxLegOffset) {
			legIncrement*=-1;
		}
	}
	public boolean collidesWith(iCollider otherObject) {
		boolean collideChk = false;
		Opponent oO = (Opponent) otherObject;
		iMovingShape oMS = (iMovingShape) otherObject;
		if(oO.getCollide() > 0) {
			oO.setCollide(-1);
		}else {
			int l1,r1,b1,t1,l2,r2,b2,t2;
			l1 = (int) (this.getXLoc() - (getScaleX() * getSize()/2));
			r1 = (int) (this.getXLoc() + (getScaleX() * getSize()/2));
			b1 = (int) (this.getYLoc() + (getScaleY() * getSize()/2));
			t1 = (int) (this.getYLoc() - (getScaleY() * getSize()/2));
			l2 = (int) (oMS.getXLoc() - (oMS.getScaleX() * oO.getSize()/2));
			r2 = (int) (oMS.getXLoc() + (oMS.getScaleX() * oO.getSize()/2));
			b2 = (int) (oMS.getYLoc() + (oMS.getScaleY() * oO.getSize()/2));
			t2 = (int) (oMS.getYLoc() - (oMS.getScaleY() * oO.getSize()/2));
			if(((l1 < l2 && l2 < r1) || (l1 < r2 && r2 < r1)) &&
					((t1 < t2 && t2 < b1) || (t1 < b2 && b2 < b1))) {
				collideChk = true;
			}			
		}
		return collideChk;
	}
	public void handleCollision(iCollider otherObject, GameWorld gw) {
		if(gw.getAlienOut() < 30) {
			if(otherObject instanceof Alien) {
				if(getCollide() == 0) {
					Alien oO = (Alien) otherObject;
					gw.spawn(oO.getXLoc(), oO.getYLoc());
					setCollide(20);
					oO.setCollide(300);
					if(gw.getSound() == "on") {
						gw.playSound(THISGAMESOUND).play();
					}
				}
			}
		}
	}
	public String toString() {
		String parentStr = super.toString();
		String myStr = ", Loc: " + getXLoc() + "," +getYLoc();
		return parentStr + myStr;
	}
}
