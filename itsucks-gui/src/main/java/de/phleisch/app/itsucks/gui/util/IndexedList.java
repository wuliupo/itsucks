/* Copyright (C) 2006-2007 Oliver Mihatsch (banishedknight@users.sf.net)
 * This is free software distributed under the terms of the
 * GNU Public License.  See the file COPYING for details. 
 *
 * $Id$
 * Created on 27.09.2007
 */

package de.phleisch.app.itsucks.gui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class IndexedList<E> implements List<E> {

	private List<E> mRows;
	
//	private Map<E, Integer> mHashIndex;
//	private boolean mIndexIsValid;

	public IndexedList() {
//		mRows = new TreeList();
		mRows = new ArrayList<E>();
	}
	
	public IndexedList(Collection<? extends E> pCollection) {
		mRows = new ArrayList<E>(pCollection);
	}
	
	public boolean add(E pE) {
		return mRows.add(pE);
	}

	public void add(int pIndex, E pElement) {
		mRows.add(pIndex, pElement);
	}

	public boolean addAll(Collection<? extends E> pC) {
		return mRows.addAll(pC);
	}

	public boolean addAll(int pIndex, Collection<? extends E> pC) {
		return mRows.addAll(pIndex, pC);
	}

	public void clear() {
		mRows.clear();
	}

	public boolean contains(Object pO) {
		return mRows.contains(pO);
	}

	public boolean containsAll(Collection<?> pC) {
		return mRows.containsAll(pC);
	}

	public boolean equals(Object pO) {
		return mRows.equals(pO);
	}

	public E get(int pIndex) {
		return mRows.get(pIndex);
	}

	public int hashCode() {
		return mRows.hashCode();
	}

	public int indexOf(Object pO) {
		return mRows.indexOf(pO);
	}

	public boolean isEmpty() {
		return mRows.isEmpty();
	}

	public Iterator<E> iterator() {
		return mRows.iterator();
	}

	public int lastIndexOf(Object pO) {
		return mRows.lastIndexOf(pO);
	}

	public ListIterator<E> listIterator() {
		return mRows.listIterator();
	}

	public ListIterator<E> listIterator(int pIndex) {
		return mRows.listIterator(pIndex);
	}

	public E remove(int pIndex) {
		return mRows.remove(pIndex);
	}

	public boolean remove(Object pO) {
		return mRows.remove(pO);
	}

	public boolean removeAll(Collection<?> pC) {
		return mRows.removeAll(pC);
	}

	public boolean retainAll(Collection<?> pC) {
		return mRows.retainAll(pC);
	}

	public E set(int pIndex, E pElement) {
		return mRows.set(pIndex, pElement);
	}

	public int size() {
		return mRows.size();
	}

	public List<E> subList(int pFromIndex, int pToIndex) {
		return mRows.subList(pFromIndex, pToIndex);
	}

	public Object[] toArray() {
		return mRows.toArray();
	}

	public <T> T[] toArray(T[] pA) {
		return mRows.toArray(pA);
	}

	
//	private void rebuildRowCache() {
//		
//		if(!mJobPositionCacheIsInvalid) return; 
//	
//		mJobPositionCache.clear();
//		
//		for (int i = 0; i < mRows.size(); i++) {
//			DownloadJob entry = mRows.get(i);
//			addRowCache(entry, i);
//		}
//	}
	
	
}
