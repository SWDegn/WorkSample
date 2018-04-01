package com.mycompany.a4;

import java.util.Random;
import java.util.Vector;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;
import com.codename1.ui.geom.Point;
/*	The ShockWave class was created for assignment 4, it is called when the spaceship
 * door opens. It creates a Bezier Curve along 4 random points through recursion.
 * The curve has a max lifetime, represented by MAXLIFE. Because the curve uses
 * the integer based point class, the smallest possible epsilon is just over 0.8
 * By using a large amount of type casting, this could be circumvented, but since
 * we our integer is a pixel representation, the absolute most efficient epsilon would
 * be 0.5, and the difference between 1 and 0.5 is negligible.
 * One issue remains outstanding, the translate of this object is not matching its visual
 * representation, thus the drawing of this object is not originating at the spaceship.
 * Not enough time remains to resolve this issue.
 */
public class ShockWave extends Opponent implements iMovingShape, iDrawable {
	private Transform myRotation, myTranslation, myScale;
	private static final int STARTCOLOR=ColorUtil.rgb(0, 80, 0);
	private static final double EPSILON =1;
	private static final int CURVESIZE = 80;
	private static final int MAXLIFE = 400;
	//private Vector controllPointVector = new Vector();
	private Point sP[] = new Point[4];
	private int lifeTime;
	
	Random rng = new Random();
	
	public ShockWave (SpaceShip tmpS) {
		lifeTime = MAXLIFE;
		setColor(STARTCOLOR);
		setSpeed(3);
		myRotation = Transform.makeIdentity();
		myTranslation = Transform.makeIdentity();
		myScale = Transform.makeIdentity();
		
		sP[0] = new Point((int)(-rng.nextInt(CURVESIZE)+8), (int)(-rng.nextInt(CURVESIZE)+8));	//bl	
		sP[1] = new Point((int)(-rng.nextInt(CURVESIZE)+8), (int)(rng.nextInt(CURVESIZE)+8)); 		//tl
		sP[2] = new Point((int)(rng.nextInt(CURVESIZE)+8),	(int)(rng.nextInt(CURVESIZE)+8));			//tr
		sP[3] = new Point((int)(rng.nextInt(CURVESIZE)+8),	(int)(-rng.nextInt(CURVESIZE)+8));			//br
	
		translate(tmpS.getXLoc(), tmpS.getYLoc());
		rotate(getDirection());
		scale(1.0f,1.0f);
	}
	public int getLifeTime() {
		return lifeTime;
	}

	public float getXLoc() {
		return myTranslation.getTranslateX();
	}
	public float getYLoc() {
		return myTranslation.getTranslateY();
	}
	public float getScaleX() {
		return myScale.getScaleX();
	}
	public float getScaleY() {
		return myScale.getScaleY();
	}
	public int getSizeX() {
		return (int)(getSize() * getScaleX()); 
	}
	public int getSizeY() {
		return (int)(getSize() * getScaleY()); 
	}
	public void rotate (float degrees) {
		myRotation.rotate ((float)Math.toRadians(degrees),0,0);
	}
	public void translate (float tx, float ty) {
		myTranslation.translate (tx, ty);
	}
	public void setTranslate (float tx, float ty) {
		myTranslation.setTranslation (tx, ty);
	}
	public void scale (float sx, float sy) {
		myScale.scale (sx, sy);
	}
	public void resetTransform() {
		myRotation.setIdentity();
		myTranslation.setIdentity();
		myScale.setIdentity();
	}
	public void changeTranslation(double xVal, double yVal) {
		myTranslation.translate((float)(xVal), (float)(yVal));
	}
	
	public void setTranslation(double xVal, double yVal) {
		myTranslation.setTranslation((float) xVal
				 ,(float) yVal);
	}
	
	public void changeColor(int value) {
		setColor(value);
	}
	public String toString() {
		String parentStr = super.toString();
		String myStr = "Loc: " + getXLoc() + "," +getYLoc();
		return parentStr + myStr;
	}
	public boolean lifeCheck() {
		lifeTime --;
		if(lifeTime > 0) {
			return false;
		} else return true;
	}
	public void draw(Graphics g, Point pCmpRelPrnt, Point pCmpRelScreen) {
		g.setColor(getColor());
		drawBezierCurve(g, pCmpRelPrnt, pCmpRelScreen, sP);
	}
	void drawBezierCurve (Graphics g,Point pCmpRelPrnt, Point pCmpRelScreen, Point CPV[]) {
		if ( straightEnough (CPV)) {
			Transform gXform = Transform.makeIdentity();
			g.getTransform(gXform);
			Transform gOrigXform = gXform.copy();
			gXform.translate(pCmpRelScreen.getX(),pCmpRelScreen.getY());
			gXform.translate(myTranslation.getTranslateX(), myTranslation.getTranslateY());
			gXform.concatenate(myRotation);
			gXform.scale(myScale.getScaleX(), myScale.getScaleY());
			gXform.translate(-pCmpRelScreen.getX(),-pCmpRelScreen.getY());
			g.setTransform(gXform);
			g.drawLine(CPV[0].getX(), CPV[0].getY(), CPV[1].getX(), CPV[1].getY());
			g.drawLine(CPV[1].getX(), CPV[1].getY(), CPV[2].getX(), CPV[2].getY());
			g.drawLine(CPV[2].getX(), CPV[2].getY(), CPV[3].getX(), CPV[3].getY());
			g.setTransform(gOrigXform);
		}else {
			Point LSV[] = new Point[4];
			Point RSV[] = new Point[4];
			subdivideCurve (CPV, LSV, RSV);
			drawBezierCurve (g, pCmpRelPrnt, pCmpRelScreen, LSV);
			drawBezierCurve (g, pCmpRelPrnt, pCmpRelScreen, RSV);
			}
		}
	boolean straightEnough (Point CPV[]) {
		Point tmpP0 = new Point(CPV[0].getX(), CPV[0].getY());
		Point tmpP1 = new Point(CPV[1].getX(), CPV[1].getY());
		Point tmpP2 = new Point(CPV[2].getX(), CPV[2].getY());
		Point tmpP3 = new Point(CPV[3].getX(), CPV[3].getY());
		int tmpX = tmpP1.getX() - tmpP0.getX();
		int tmpY = tmpP1.getY() - tmpP0.getY();
		double d1 = Math.sqrt((double) ((tmpX * tmpX) + (tmpY * tmpY)));
		tmpX = tmpP2.getX() - tmpP1.getX();
		tmpY = tmpP2.getY() - tmpP1.getY();
		d1 += Math.sqrt((double) ((tmpX * tmpX) + (tmpY * tmpY)));
		tmpX = tmpP3.getX() - tmpP2.getX();
		tmpY = tmpP3.getY() - tmpP2.getY();
		d1 += Math.sqrt((double) ((tmpX * tmpX) + (tmpY * tmpY)));
		tmpX = tmpP3.getX() - tmpP0.getX();
		tmpY = tmpP3.getY() - tmpP0.getY();
		double d2 = Math.sqrt((double) ((tmpX * tmpX) + (tmpY * tmpY)));
		
		if ( Math.abs(d1-d2) < EPSILON ) // epsilon (gtoleranceh) = (e.g.) .001
		return true ;
		else
		return false ;
	}
	void subdivideCurve (Point Q[], Point R[], Point S[]) {
		Point tmpP0 = new Point(Q[0].getX(), Q[0].getY());
		Point tmpP1 = new Point(Q[1].getX(), Q[1].getY());
		Point tmpP2 = new Point(Q[2].getX(), Q[2].getY());
		Point tmpP3 = new Point(Q[3].getX(), Q[3].getY());
		
		Point tmpPA = new Point((tmpP0.getX() + tmpP1.getX())/2,
						(tmpP0.getY() + tmpP1.getY())/2);
		R[0] = tmpP0;
		R[1] = tmpPA;
		Point tmpPB = new Point((tmpP0.getX() + tmpP1.getX())/4,
				(tmpP0.getY() + tmpP1.getY())/4);
		tmpPA = new Point((tmpP2.getX() + tmpP1.getX())/4,
				(tmpP2.getY() + tmpP1.getY())/4);
		Point tmpPC = new Point(tmpPA.getX() + tmpPB.getX(),
				tmpPA.getY() + tmpPB.getY());
		R[2] = tmpPC;
		S[3] = tmpP3;
		tmpPA = new Point((tmpP3.getX() + tmpP2.getX())/2,
				(tmpP3.getY() + tmpP2.getY())/2);
		S[2] = tmpPA;
		
		tmpPB = new Point((tmpP1.getX() + tmpP2.getX())/4,
				(tmpP1.getY() + tmpP2.getY())/4);
		tmpPA = new Point((tmpP3.getX() + tmpP2.getX())/4,
				(tmpP3.getY() + tmpP2.getY())/4);
		Point tmpPD = new Point(tmpPA.getX() + tmpPB.getX(),
				tmpPA.getY() + tmpPB.getY());
		S[1] = tmpPD;
		
		tmpPA = new Point((tmpPC.getX() + tmpPD.getX())/2,
				(tmpPC.getY() + tmpPD.getY())/2);
		R[3] = tmpPA;
		S[0] = tmpPA;
	}
	
}
