/*
 * 
 * T2AndroidLib
 * 
 * Copyright � 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright � 2009-2012 Contributors. All Rights Reserved. 
 * 
 * THIS OPEN SOURCE AGREEMENT ("AGREEMENT") DEFINES THE RIGHTS OF USE, 
 * REPRODUCTION, DISTRIBUTION, MODIFICATION AND REDISTRIBUTION OF CERTAIN 
 * COMPUTER SOFTWARE ORIGINALLY RELEASED BY THE UNITED STATES GOVERNMENT 
 * AS REPRESENTED BY THE GOVERNMENT AGENCY LISTED BELOW ("GOVERNMENT AGENCY"). 
 * THE UNITED STATES GOVERNMENT, AS REPRESENTED BY GOVERNMENT AGENCY, IS AN 
 * INTENDED THIRD-PARTY BENEFICIARY OF ALL SUBSEQUENT DISTRIBUTIONS OR 
 * REDISTRIBUTIONS OF THE SUBJECT SOFTWARE. ANYONE WHO USES, REPRODUCES, 
 * DISTRIBUTES, MODIFIES OR REDISTRIBUTES THE SUBJECT SOFTWARE, AS DEFINED 
 * HEREIN, OR ANY PART THEREOF, IS, BY THAT ACTION, ACCEPTING IN FULL THE 
 * RESPONSIBILITIES AND OBLIGATIONS CONTAINED IN THIS AGREEMENT.
 * 
 * Government Agency: The National Center for Telehealth and Technology
 * Government Agency Original Software Designation: T2AndroidLib001
 * Government Agency Original Software Title: T2AndroidLib
 * User Registration Requested. Please send email 
 * with your contact information to: robert.kayl2@us.army.mil
 * Government Agency Point of Contact for Original Software: robert.kayl2@us.army.mil
 * 
 */
package com.j256.ormlite.dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.j256.ormlite.field.ForeignCollectionField;

/**
 * Collection that is set on a field that as been marked with the {@link ForeignCollectionField} annotation when an
 * object is refreshed or queried (i.e. not created).
 * 
 * @author graywatson
 */
public class EagerForeignCollection<T, ID> extends BaseForeignCollection<T, ID> implements ForeignCollection<T> {

	private final List<T> results;

	public EagerForeignCollection(Dao<T, ID> dao, String fieldName, Object fieldValue) throws SQLException {
		super(dao, fieldName, fieldValue);
		// go ahead and do the query if eager
		results = dao.query(preparedQuery);
	}

	public CloseableIterator<T> iterator() {
		return iteratorThrow();
	}

	public CloseableIterator<T> iteratorThrow() {
		final Iterator<T> iterator = results.iterator();
		// we have to wrap the iterator since we are returning the List's iterator
		return new CloseableIterator<T>() {
			private T last = null;
			public boolean hasNext() {
				return iterator.hasNext();
			}
			public T next() {
				last = iterator.next();
				return last;
			}
			public void remove() {
				iterator.remove();
				try {
					dao.delete(last);
				} catch (SQLException e) {
					// have to demote this to be runtime
					throw new RuntimeException(e);
				}
			}
			public void close() {
				// noop
			}
		};
	}

	public int size() {
		return results.size();
	}

	public boolean isEmpty() {
		return results.isEmpty();
	}

	public boolean contains(Object o) {
		return results.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return results.containsAll(c);
	}

	public Object[] toArray() {
		return results.toArray();
	}

	public <E> E[] toArray(E[] array) {
		return results.toArray(array);
	}

	@Override
	public boolean add(T data) {
		results.add(data);
		return super.add(data);
	}

	@Override
	public boolean addAll(Collection<? extends T> collection) {
		results.addAll(collection);
		return super.addAll(collection);
	}

	@Override
	public boolean remove(Object data) {
		results.remove(data);
		return super.remove(data);
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		results.removeAll(collection);
		return super.removeAll(collection);
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		results.retainAll(collection);
		return super.retainAll(collection);
	}

	@Override
	public void clear() {
		results.clear();
		super.clear();
	}

	@Override
	public boolean equals(Object other) {
		return results.equals(other);
	}

	@Override
	public int hashCode() {
		return results.hashCode();
	}
}
