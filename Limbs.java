package com.mycompany.a4;

import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;
import com.codename1.ui.geom.Point;
/*	The limbs class was added for assignment 4 and contains the information needed to draw
 * the Hierarchical objects upon the alien body.
 */
public class Limbs {
	private Point top, bottomLeft, bottomRight;
	private Transform myFirstRotation, mySecondRotation, myTranslation, myScale;
	
	public  Limbs (int base, int height) {
		top = new Point (0, height/2);
		bottomLeft = new Point (-base/2, -height/2);
		bottomRight = new Point (base/2, -height/2);
		myFirstRotation = Transform.makeIdentity();
		mySecondRotation = Transform.makeIdentity();
		myTranslation = Transform.makeIdentity();
		myScale = Transform.makeIdentity();
	}
	
	public void firstRotate (float degrees) {
		myFirstRotation.rotate ((float)Math.toRadians(degrees),0,0);
	}
	public void secondRotate (float degrees) {
		mySecondRotation.rotate ((float)Math.toRadians(degrees),0,0);
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
		myFirstRotation.setIdentity();
		mySecondRotation.setIdentity();
		myTranslation.setIdentity();
		myScale.setIdentity();
	}
	public float getTransX() {
		return myTranslation.getTranslateX();
	}
	public float getTransY() {
		return myTranslation.getTranslateY();
	}
	public float getScaleX() {
		return myScale.getScaleX();
	}
	public float getScaleY() {
		return myScale.getScaleY();
	}

	public void draw (Graphics g, Point pCmpRelPrnt, Point pCmpRelScrn) {
		Transform gXform = Transform.makeIdentity();
		g.getTransform(gXform);
		Transform gOrigXform = gXform.copy();
		gXform.translate(pCmpRelScrn.getX(),pCmpRelScrn.getY());
		gXform.concatenate(mySecondRotation);
		gXform.translate(myTranslation.getTranslateX(), myTranslation.getTranslateY());
		gXform.concatenate(myFirstRotation);
		gXform.scale(myScale.getScaleX(), myScale.getScaleY());
		gXform.translate(-pCmpRelScrn.getX(),-pCmpRelScrn.getY());
		g.setTransform(gXform);
		int xPoints[] = {pCmpRelPrnt.getX() + top.getX(),
						 pCmpRelPrnt.getX() + bottomLeft.getX(),
						 pCmpRelPrnt.getX() + bottomRight.getX()};
		int yPoints[] = {pCmpRelPrnt.getY() + top.getY(),
						 pCmpRelPrnt.getY() + bottomLeft.getY(),
						 pCmpRelPrnt.getY() + bottomRight.getY()};
		if(this instanceof iSelectable) {
			if(((iSelectable) this).isSelected()) {
			g.drawPolygon(xPoints, yPoints, 3);
			}else
				g.fillPolygon(xPoints, yPoints, 3);
		}else
			g.fillPolygon(xPoints, yPoints, 3);
		g.setTransform(gOrigXform);
	}
}
