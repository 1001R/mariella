package org.mariella.oxygen.runtime.core;

import javax.persistence.Query;

/**
 * @author aim
 */
public interface OxyQuery extends Query {

/**
 * should be called before the query is executed. 
 * @param detached if set to true, the entities created by this query are detached and should be
 * handled read only. Object identity is only provided within the returned result, not within the
 * associated entity manager
 */
public void setDetachedResults(boolean detached);


/**
 * @return true if the entities returned by the query are detached 
 */
public boolean isDetachedResults();

}
