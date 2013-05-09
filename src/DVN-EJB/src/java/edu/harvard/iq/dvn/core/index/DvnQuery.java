/*
 Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 Dataverse Network - A web application to share, preserve and analyze research data.
 Developed at the Institute for Quantitative Social Science, Harvard University.
 Version 3.0.
 */
package edu.harvard.iq.dvn.core.index;

import edu.harvard.iq.dvn.core.index.SearchTerm;
import edu.harvard.iq.dvn.core.vdc.VDC;
import edu.harvard.iq.dvn.core.web.common.VDCBaseBean;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import org.apache.lucene.facet.taxonomy.CategoryPath;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

public class DvnQuery {

    private static final Logger logger = Logger.getLogger(DvnQuery.class.getCanonicalName());
    @EJB
    IndexServiceLocal indexService;
//    SearchTerm searchTerm;
    List<SearchTerm> searchTerms;
    VDC vdc;
    Query query = null;
    List<CategoryPath> facetsToQuery = new ArrayList<CategoryPath>();
    private boolean clearPreviousFacetRequests = false;
//    private static boolean clearPreviousFacetRequests = false;
    List<Long> limitToStudyIds;
    List<Query> collectionQueries = new ArrayList<Query>();
    private Query dvOwnerIdQuery;

    public Query getDvOwnerIdQuery() {
        return dvOwnerIdQuery;
    }

    public void setDvOwnerIdQuery(Query dvOwnerIdQuery) {
        this.dvOwnerIdQuery = dvOwnerIdQuery;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public VDC getVdc() {
        return vdc;
    }

    public void setVdc(VDC vdc) {
        this.vdc = vdc;
    }

    public List<SearchTerm> getSearchTerms() {
        return searchTerms;
    }

    public void setSearchTerms(List<SearchTerm> searchTerms) {
        this.searchTerms = searchTerms;
    }

    public List<CategoryPath> getFacetsToQuery() {
        return facetsToQuery;
    }

    public void setFacetsToQuery(List<CategoryPath> facetsToQuery) {
        this.facetsToQuery = facetsToQuery;
    }

    public boolean isClearPreviousFacetRequests() {
        return clearPreviousFacetRequests;
    }

    public void setClearPreviousFacetRequests(boolean clearPreviousFacetRequests) {
        this.clearPreviousFacetRequests = clearPreviousFacetRequests;
    }

    public List<Long> getLimitToStudyIds() {
        return limitToStudyIds;
    }

    public void setLimitToStudyIds(List<Long> limitToStudyIds) {
        this.limitToStudyIds = limitToStudyIds;
    }

    public List<Query> getCollectionQueries() {
        return collectionQueries;
    }

    public void setCollectionQueries(List<Query> collectionQueries) {
        this.collectionQueries = collectionQueries;
    }

    public void constructQuery() {
        logger.fine("in constructQuery...");
        BooleanQuery searchQuery = null;

        // FIXME: how much of this logic do we need? from indexService.searchwithFacets()...

        List<BooleanQuery> searchParts = new ArrayList();

        // "study-level search" is our "normal", default search, that is 
        // performed on the study metadata keywords.
        boolean studyLevelSearch = false;
        boolean containsStudyLevelAndTerms = false;

        // We also support searches on variables and file-level metadata:
        // We do have to handle these 2 separately, because of the 2 different
        // levels of granularity: one searches on variables, the other on files.
        boolean variableSearch = false;
        boolean fileMetadataSearch = false;

        // And the boolean below indicates any file-level searche - i.e., 
        // either a variable, or file metadata search.  
        // -- L.A. 
        boolean fileLevelSearch = false;


        List<SearchTerm> studyLevelSearchTerms = new ArrayList();
        List<SearchTerm> variableSearchTerms = new ArrayList();
        List<SearchTerm> fileMetadataSearchTerms = new ArrayList();
        Indexer indexer = Indexer.getInstance();

        for (Iterator it = searchTerms.iterator(); it.hasNext();) {
            SearchTerm elem = (SearchTerm) it.next();
            logger.fine("elem field name = " + elem.getFieldName().toString());
            if (elem.getFieldName().equals("variable")) {
//                SearchTerm st = dvnTokenizeSearchTerm(elem);
//                variableSearchTerms.add(st);
                variableSearchTerms.add(elem);
                variableSearch = true;
            } else if (indexer.isFileMetadataField(elem.getFieldName())) {

                fileMetadataSearch = true;
                fileMetadataSearchTerms.add(elem);
            } else {
//                SearchTerm nvst = dvnTokenizeSearchTerm(elem);
//                nonVariableSearchTerms.add(nvst);
                if (elem.getOperator().equals("=")) {
                    containsStudyLevelAndTerms = true;
                }
                studyLevelSearchTerms.add(elem);
                studyLevelSearch = true;

            }

        }

        BooleanQuery searchTermsQuery = indexer.andSearchTermClause(studyLevelSearchTerms);
        searchParts.add(searchTermsQuery);
        searchQuery = indexer.andQueryClause(searchParts);


//        logger.info("DvnQuery dump of searchQuery (before collections modifications): " + searchQuery);
        if (!collectionQueries.isEmpty() || dvOwnerIdQuery != null) {
            BooleanQuery queryAcrossAllCollections = new BooleanQuery();
            for (Query collectionQuery : collectionQueries) {
                BooleanQuery submittedAndInCollection = new BooleanQuery();
                submittedAndInCollection.add(searchQuery, BooleanClause.Occur.MUST);
                submittedAndInCollection.add(collectionQuery, BooleanClause.Occur.MUST);
                queryAcrossAllCollections.add(submittedAndInCollection, BooleanClause.Occur.SHOULD);
            }
            BooleanQuery dvSpecific = new BooleanQuery();
            dvSpecific.add(searchQuery, BooleanClause.Occur.MUST);
            dvSpecific.add(dvOwnerIdQuery, BooleanClause.Occur.MUST);
            queryAcrossAllCollections.add(dvSpecific, BooleanClause.Occur.SHOULD);
            searchQuery = queryAcrossAllCollections;
        }
//        logger.info("searchQuery before return: " + searchQuery);
        query = searchQuery;
    }
}
