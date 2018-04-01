package com.mycompany.a4;

import java.util.Observable;
import java.util.Observer;

import com.codename1.charts.util.ColorUtil;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Graphics;
import com.codename1.ui.Transform;
import com.codename1.ui.Transform.NotInvertibleException;
import com.codename1.ui.geom.Point;
import com.codename1.ui.plaf.Border;

/* This class is the MapView, it extends the Container Class and Implements the Observer
 * class. This class displays the game objects in a view that is part of the MCV design
 * pattern. It requires knowing about the GameWorld
 * This class was implemented in assignment 2, but was largely unused.
 * This class was updated in assignment 3 to include the method for calling each of the
 * objects in the gameworld and drawing the appropriate ones. It was also updated to
 * include a method for handling a mouse pointer click.
 * 
 */
public class MapView extends Container implements Observer {
	private GameWorld theGame;
	private Transform worldToND, ndToDisplay, theVTM, inverseVTM ;
	private float winLeft, winBottom, winRight, winTop, winWidth, winHeight;
	private Point pPrevDragLoc = new Point(-1, -1);
	
	public MapView(GameWorld gw) {
		this.getAllStyles().setBorder(Border.createLineBorder(2, ColorUtil.BLUE));
		theGame = gw;
		winLeft = 0;
		winBottom = 0;
		winRight = 931/2; 
		winTop = 639/2; 
		winWidth = winRight - winLeft;
		winHeight = winTop - winBottom;
	}
	
	public void update(Observable observable, Object data) {
		repaint();
	}
	public void firstUpdate() {
		winRight = this.getWidth()/2;
		winTop = this.getHeight()/2;
		winWidth = winRight - winLeft;
		winHeight = winTop - winBottom;
		repaint();
	}
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		worldToND = buildWorldToNDXform(winWidth, winHeight, winLeft, winBottom);
		ndToDisplay = buildNDToDisplayXform(this.getWidth(), this.getHeight());
		theVTM = ndToDisplay.copy();
		theVTM.concatenate(worldToND);
		Transform gXform = Transform.makeIdentity();
		g.getTransform(gXform);
		gXform.translate(getAbsoluteX(),getAbsoluteY());
		gXform.concatenate(theVTM);
		gXform.translate(0, getHeight());
		gXform.scale(1, -1);
		gXform.translate(-getAbsoluteX(),-getAbsoluteY());
		g.setTransform(gXform);
		GameCollection tGC = theGame.getCollection();
		iIterator theElements = tGC.getIterator() ;
		Object obj;
		Point pCmpRelPrnt = new Point(getX(), getY());
		Point pCmpRelScreen = new Point(getAbsoluteX(),getAbsoluteY());
		while(theElements.hasNext()) {
			obj = theElements.getNext();
			if(obj instanceof iDrawable) {
				((iDrawable) obj).draw(g, pCmpRelPrnt, pCmpRelScreen);
			}
		}
		g.resetAffine();
	}
	@Override
	public void pointerPressed(int x,int y){
		System.out.println("First press Pointer at: " + x + ", " + y);
		System.out.println("Absolutex: " + getAbsoluteX() + " ParentAbsX: " + getParent().getAbsoluteX());
		System.out.println("AbsoluteY: " + getAbsoluteY() + " ParentAbsY: " + getParent().getAbsoluteY());
		float [] fPtr = new float [] {x - getAbsoluteX(), y - getAbsoluteY()};
		System.out.println("Second, absolute Pointer at: " + fPtr[0] + ", " + fPtr[1]);
		inverseVTM = Transform.makeIdentity();
		try {
			theVTM.getInverse(inverseVTM);
		} catch (NotInvertibleException e) {
			System.out.println("Non invertible xform!");
			}
		inverseVTM.transformPoint(fPtr, fPtr);
		System.out.println("Third, vtm inverse Pointer at: " + fPtr[0] + ", " + fPtr[1]);
		GameCollection tGC = theGame.getCollection();
		iIterator theElements = tGC.getIterator() ;
		Object obj;
		while(theElements.hasNext()){
			obj = theElements.getNext();
			if(obj instanceof iSelectable) {
				if (((iSelectable) obj).contains(fPtr)) {
					((iSelectable)	obj).setSelected(true);
				} else {
					((iSelectable) obj).setSelected(false);
				}
			}
		}
		repaint();
	}
	public void zoom(float factor) {
		winWidth = winRight - winLeft;
		winHeight = winTop - winBottom;
		float newWinLeft = winLeft + winWidth*factor;
		float newWinRight = winRight - winWidth*factor;
		float newWinTop = winTop - winHeight*factor;
		float newWinBottom = winBottom + winHeight*factor;
		float newWinHeight = newWinTop - newWinBottom;
		float newWinWidth = newWinRight - newWinLeft;
		if (newWinWidth <= 1000 && newWinHeight <= 1000 && newWinWidth > 100 && newWinHeight > 100 ){
			winLeft = newWinLeft;
			winRight = newWinRight;
			winTop = newWinTop;
			winBottom = newWinBottom;
		}
		else
			System.out.println("Cannot zoom further!");
		this.repaint();
	}
	public void showShip(GameWorld gw) {
		GameCollection tgc = gw.getCollection();
		iIterator theElements = tgc.getIterator() ;
		SpaceShip tmpS = (SpaceShip) theElements.getNext();
		float sY = (float) (getHeight() -tmpS.getYLoc());
		winLeft = (float) (tmpS.getXLoc() - winWidth/2);
		winRight =(float) (tmpS.getXLoc() + winWidth/2);
		winTop = (float) (sY + winHeight/2);
		winBottom = (float) (sY - winHeight/2);
		this.repaint();
	}
	public void panHorizontal(double delta) {
		winLeft += delta;
		winRight += delta;
		this.repaint();
	}
	public void panVertical(double delta) {
		winBottom += delta;
		winTop += delta;
		this.repaint();
	}
	@Override
	public boolean pinch(float scale){
		if(scale < 1.0){
			//Zooming Out: two fingers come closer together (on actual device), right mouse
			//click + drag towards the top left corner of screen (on simulator)
			zoom(-0.05f);
		}else if(scale>1.0){
			//Zooming In: two fingers go away from each other (on actual device), right mouse
			//click + drag away from the top left corner of screen (on simulator)
			zoom(0.05f);
		}
		return true;
	}
	@Override
	public void pointerDragged(int x, int y){
		if (pPrevDragLoc.getX() != -1) {
			if (pPrevDragLoc.getX() < x)
				panHorizontal(5);
			else if (pPrevDragLoc.getX() > x)
				panHorizontal(-5);
			if (pPrevDragLoc.getY() < y)
				panVertical(-5);
			else if (pPrevDragLoc.getY() > y)
				panVertical(5);
		}
		pPrevDragLoc.setX(x);
		pPrevDragLoc.setY(y);
	}
	private Transform buildWorldToNDXform(float winWidth, float winHeight, float
											winLeft, float winBottom){
		Transform tmpXfrom = Transform.makeIdentity();
		tmpXfrom.scale( (1/winWidth) , (1/winHeight) );
		tmpXfrom.translate(-winLeft,-winBottom);
		return tmpXfrom;
	}
	private Transform buildNDToDisplayXform (float displayWidth, float displayHeight){
		Transform tmpXfrom = Transform.makeIdentity();
		tmpXfrom.translate(0, displayHeight);
		tmpXfrom.scale(displayWidth, -displayHeight);
		return tmpXfrom;
	}
}
