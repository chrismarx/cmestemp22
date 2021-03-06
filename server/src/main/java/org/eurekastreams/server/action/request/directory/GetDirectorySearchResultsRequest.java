/*
 * Copyright (c) 2010 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eurekastreams.server.action.request.directory;

import java.io.Serializable;

import org.eurekastreams.server.action.request.PageableRequest;
import org.eurekastreams.server.domain.ResourceSortCriteria;

/**
 * This class carries the request information for a call to the GetDirectorSearchResults action.
 *
 */
public class GetDirectorySearchResultsRequest implements Serializable, PageableRequest
{
    /**
     *
     */
    private static final long serialVersionUID = 455392055869182716L;

    /**
     * Local instance of the search term for this request.
     */
    private String searchTerm;

    /**
     * Loca instance of the Org short name for this request.
     */
    private String orgShortName;

    /**
     * Local instance of the Weighted field for this request.
     */
    private String weightedField;

    /**
     * Local instance of the paging from for this request.
     */
    private int pagingFrom;

    /**
     * Local instance of the paging to for this request.
     */
    private int pagingTo;

    /**
     * {@link ResourceSortCriteria}.
     */
    ResourceSortCriteria sortCriteria;

    /**
     * This is a default constructor for passage through GWT.
     */
    public GetDirectorySearchResultsRequest()
    {
        // Default constructor.
    }

    /**
     * Constructor.
     *
     * @param inSearchTerm
     *            - instance of the search term for this request.
     * @param inOrgShortName
     *            - instance of the org shortname for this request.
     * @param inWeightedField
     *            - instance of the weighted field for this request.
     * @param inPagingFrom
     *            - instance of the count to start with for paging on this request.
     * @param inPagingTo
     *            - instance of the count to end with for paging on this request.
     */
    public GetDirectorySearchResultsRequest(final String inSearchTerm, final String inOrgShortName,
            final String inWeightedField, final int inPagingFrom, final int inPagingTo)
    {
        searchTerm = inSearchTerm;
        orgShortName = inOrgShortName;
        weightedField = inWeightedField;
        pagingFrom = inPagingFrom;
        pagingTo = inPagingTo;
    }

    /**
     * Constructor.
     *
     * @param inOrgShortName
     *            Organization short name
     * @param inPagingFrom
     *            Instance of the count to start with for paging on this request.
     * @param inPagingTo
     *            Instance of the count to end with for paging on this request.
     * @param inSortCriteria
     *            {@link ResourceSortCriteria}.
     */
    public GetDirectorySearchResultsRequest(final String inOrgShortName, final int inPagingFrom, final int inPagingTo,
            final ResourceSortCriteria inSortCriteria)
    {
        orgShortName = inOrgShortName;
        pagingFrom = inPagingFrom;
        pagingTo = inPagingTo;
        sortCriteria = inSortCriteria;
    }

    /**
     * @return the searchTerm.
     */
    public String getSearchTerm()
    {
        return searchTerm;
    }

    /**
     * @param inSearchTerm
     *            the searchTerm to set.
     */
    public void setSearchTerm(final String inSearchTerm)
    {
        this.searchTerm = inSearchTerm;
    }

    /**
     * @return the orgShortName.
     */
    public String getOrgShortName()
    {
        return orgShortName;
    }

    /**
     * @param inOrgShortName
     *            the orgShortName to set.
     */
    public void setOrgShortName(final String inOrgShortName)
    {
        this.orgShortName = inOrgShortName;
    }

    /**
     * @return the weightedField.
     */
    public String getWeightedField()
    {
        return weightedField;
    }

    /**
     * @param inWeightedField
     *            the weightedField to set.
     */
    public void setWeightedField(final String inWeightedField)
    {
        this.weightedField = inWeightedField;
    }

    /**
     * @return the pagingFrom.
     */
    public Integer getStartIndex()
    {
        return pagingFrom;
    }

    /**
     * @param inPagingFrom
     *            the pagingFrom to set.
     */
    public void setStartIndex(final Integer inPagingFrom)
    {
        this.pagingFrom = inPagingFrom;
    }

    /**
     * @return the pagingTo.
     */
    public Integer getEndIndex()
    {
        return pagingTo;
    }

    /**
     * @param inPagingTo
     *            the pagingTo to set.
     */
    public void setEndIndex(final Integer inPagingTo)
    {
        this.pagingTo = inPagingTo;
    }

    /**
     * @return the sortCriteria
     */
    public ResourceSortCriteria getSortCriteria()
    {
        return sortCriteria;
    }

    /**
     * @param inSortCriteria
     *            the sortCriteria to set
     */
    public void setSortCriteria(final ResourceSortCriteria inSortCriteria)
    {
        sortCriteria = inSortCriteria;
    }
}
