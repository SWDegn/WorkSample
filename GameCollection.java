package com.mycompany.a4;
import java.util.*;

/*This class contains a collection of objects who's type(vector) is hidden from the rest
 * of the program. It implements iCollection.
 * This class was created in assignment 2. It initializes a vector and hides the vector
 * behind an iterator. It contains methods to add and remove objects from the vector
 * through the iterator, for returning the iterator, and for monitoring the size of the
 * iterator.
 */
public class GameCollection implements iCollection  {
	@SuppressWarnings("rawtypes")
	private Vector spaceCollection;
	
	@SuppressWarnings("rawtypes")
	public GameCollection() {
		spaceCollection = new Vector();
	}
	
	@SuppressWarnings("unchecked")
	public void add(Object newObject) {
		spaceCollection.addElement(newObject);
	}
	 	
	public iIterator getIterator() {
		return new SpaceVectorIterator();
	}
	
	private class SpaceVectorIterator implements iIterator {
		private int currElementIndex;
		public SpaceVectorIterator() {
			currElementIndex = -1;
		}
		public boolean hasNext() {
			if (spaceCollection.size ( ) <= 0) return false;
			if (currElementIndex == spaceCollection.size() - 1 )
				return false;
			return true;
		}
		public Object getNext() {
			currElementIndex ++ ;
			return(spaceCollection.elementAt(currElementIndex));
		}
		public void remove() {
			spaceCollection.removeElementAt(currElementIndex);
			currElementIndex -- ;
		}
	}
}
