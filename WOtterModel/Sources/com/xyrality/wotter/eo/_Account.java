// $LastChangedRevision$ DO NOT EDIT.  Make changes to Account.java instead.
package com.xyrality.wotter.eo;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

@SuppressWarnings("all")
public abstract class _Account extends er.extensions.eof.ERXGenericRecord {
	public static final String ENTITY_NAME = "Account";

	// Attributes
	public static final String NAME_KEY = "name";

	// Relationships
	public static final String POSTS_KEY = "posts";

  private static Logger LOG = Logger.getLogger(_Account.class);

  public Account localInstanceIn(EOEditingContext editingContext) {
    Account localInstance = (Account)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public String name() {
    return (String) storedValueForKey("name");
  }

  public void setName(String value) {
    if (_Account.LOG.isDebugEnabled()) {
    	_Account.LOG.debug( "updating name from " + name() + " to " + value);
    }
    takeStoredValueForKey(value, "name");
  }

  public NSArray<com.xyrality.wotter.eo.Post> posts() {
    return (NSArray<com.xyrality.wotter.eo.Post>)storedValueForKey("posts");
  }

  public NSArray<com.xyrality.wotter.eo.Post> posts(EOQualifier qualifier) {
    return posts(qualifier, null, false);
  }

  public NSArray<com.xyrality.wotter.eo.Post> posts(EOQualifier qualifier, boolean fetch) {
    return posts(qualifier, null, fetch);
  }

  public NSArray<com.xyrality.wotter.eo.Post> posts(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<com.xyrality.wotter.eo.Post> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = new EOKeyValueQualifier(com.xyrality.wotter.eo.Post.ACCOUNT_KEY, EOQualifier.QualifierOperatorEqual, this);
    	
      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        NSMutableArray qualifiers = new NSMutableArray();
        qualifiers.addObject(qualifier);
        qualifiers.addObject(inverseQualifier);
        fullQualifier = new EOAndQualifier(qualifiers);
      }

      results = com.xyrality.wotter.eo.Post.fetchPosts(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = posts();
      if (qualifier != null) {
        results = (NSArray<com.xyrality.wotter.eo.Post>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<com.xyrality.wotter.eo.Post>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }
  
  public void addToPostsRelationship(com.xyrality.wotter.eo.Post object) {
    if (_Account.LOG.isDebugEnabled()) {
      _Account.LOG.debug("adding " + object + " to posts relationship");
    }
    addObjectToBothSidesOfRelationshipWithKey(object, "posts");
  }

  public void removeFromPostsRelationship(com.xyrality.wotter.eo.Post object) {
    if (_Account.LOG.isDebugEnabled()) {
      _Account.LOG.debug("removing " + object + " from posts relationship");
    }
    removeObjectFromBothSidesOfRelationshipWithKey(object, "posts");
  }

  public com.xyrality.wotter.eo.Post createPostsRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName("Post");
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, "posts");
    return (com.xyrality.wotter.eo.Post) eo;
  }

  public void deletePostsRelationship(com.xyrality.wotter.eo.Post object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, "posts");
    editingContext().deleteObject(object);
  }

  public void deleteAllPostsRelationships() {
    Enumeration objects = posts().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deletePostsRelationship((com.xyrality.wotter.eo.Post)objects.nextElement());
    }
  }


  public static Account createAccount(EOEditingContext editingContext, String name
) {
    Account eo = (Account) EOUtilities.createAndInsertInstance(editingContext, _Account.ENTITY_NAME);    
		eo.setName(name);
    return eo;
  }

  public static NSArray<Account> fetchAllAccounts(EOEditingContext editingContext) {
    return _Account.fetchAllAccounts(editingContext, null);
  }

  public static NSArray<Account> fetchAllAccounts(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _Account.fetchAccounts(editingContext, null, sortOrderings);
  }

  public static NSArray<Account> fetchAccounts(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    EOFetchSpecification fetchSpec = new EOFetchSpecification(_Account.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<Account> eoObjects = (NSArray<Account>)editingContext.objectsWithFetchSpecification(fetchSpec);
    return eoObjects;
  }

  public static Account fetchAccount(EOEditingContext editingContext, String keyName, Object value) {
    return _Account.fetchAccount(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Account fetchAccount(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<Account> eoObjects = _Account.fetchAccounts(editingContext, qualifier, null);
    Account eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = (Account)eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one Account that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Account fetchRequiredAccount(EOEditingContext editingContext, String keyName, Object value) {
    return _Account.fetchRequiredAccount(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static Account fetchRequiredAccount(EOEditingContext editingContext, EOQualifier qualifier) {
    Account eoObject = _Account.fetchAccount(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no Account that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static Account localInstanceIn(EOEditingContext editingContext, Account eo) {
    Account localInstance = (eo == null) ? null : (Account)EOUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}
