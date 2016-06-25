// $LastChangedRevision$ DO NOT EDIT.  Make changes to Post.java instead.
package com.xyrality.wotter.eo;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

@SuppressWarnings("all")
public abstract class _Post extends er.extensions.eof.ERXGenericRecord {
	public static final String ENTITY_NAME = "Post";

	// Attributes
	public static final String CONTENT_KEY = "content";
	public static final String POSTED_AT_KEY = "postedAt";

	// Relationships
	public static final String ACCOUNT_KEY = "account";

  private static Logger LOG = Logger.getLogger(_Post.class);

  public Post localInstanceIn(EOEditingContext editingContext) {
    Post localInstance = (Post)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String content() {
    return (String) storedValueForKey("content");
  }

  public void setContent(String value) {
    if (_Post.LOG.isDebugEnabled()) {
    	_Post.LOG.debug( "updating content from " + content() + " to " + value);
    }
    takeStoredValueForKey(value, "content");
  }

  public NSTimestamp postedAt() {
    return (NSTimestamp) storedValueForKey("postedAt");
  }

  public void setPostedAt(NSTimestamp value) {
    if (_Post.LOG.isDebugEnabled()) {
    	_Post.LOG.debug( "updating postedAt from " + postedAt() + " to " + value);
    }
    takeStoredValueForKey(value, "postedAt");
  }

  public com.xyrality.wotter.eo.Account account() {
    return (com.xyrality.wotter.eo.Account)storedValueForKey("account");
  }

  public void setAccountRelationship(com.xyrality.wotter.eo.Account value) {
    if (_Post.LOG.isDebugEnabled()) {
      _Post.LOG.debug("updating account from " + account() + " to " + value);
    }
    if (value == null) {
    	com.xyrality.wotter.eo.Account oldValue = account();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, "account");
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, "account");
    }
  }
  

  public static Post createPost(EOEditingContext editingContext, String content
, NSTimestamp postedAt
, com.xyrality.wotter.eo.Account account) {
    Post eo = (Post) EOUtilities.createAndInsertInstance(editingContext, _Post.ENTITY_NAME);    
		eo.setContent(content);
		eo.setPostedAt(postedAt);
    eo.setAccountRelationship(account);
    return eo;
  }

  public static NSArray<Post> fetchAllPosts(EOEditingContext editingContext) {
    return _Post.fetchAllPosts(editingContext, null);
  }

  public static NSArray<Post> fetchAllPosts(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Post.fetchPosts(editingContext, null, sortOrderings);
  }

  public static NSArray<Post> fetchPosts(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_Post.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<Post> eoObjects = (NSArray<Post>)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static Post fetchPost(EOEditingContext editingContext, String keyName, Object value) {
    return _Post.fetchPost(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Post fetchPost(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Post> eoObjects = _Post.fetchPosts(editingContext, qualifier, null);
    Post eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (Post)eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Post that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Post fetchRequiredPost(EOEditingContext editingContext, String keyName, Object value) {
    return _Post.fetchRequiredPost(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Post fetchRequiredPost(EOEditingContext editingContext, EOQualifier qualifier) {
    Post eoObject = _Post.fetchPost(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Post that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Post localInstanceIn(EOEditingContext editingContext, Post eo) {
    Post localInstance = (eo == null) ? null : (Post)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}
