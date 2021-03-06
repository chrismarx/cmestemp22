/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.search.modelview;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for SharedResource entity.
 * 
 */
public class SharedResourceDTO implements Serializable
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = -20174090712845648L;

    /**
     * SharedResource key.
     */
    private String key = "NOT SET";

    /**
     * The number of people that like the resource.
     */
    private int likeCount = -1;

    /**
     * The number of people that are sharing the resource.
     */
    private int shareCount = -1;

    /**
     * A sample of 4 likers - check the like count to see how many there are.
     */
    private List<PersonModelView> likersSample;

    /**
     * A sample of 4 sharers - chekc the shareCount to see how many there are.
     */
    private List<PersonModelView> sharersSample;

    /**
     * Stream scope ID, which is nullable, and will be populated when activity is posted to a shared resource.
     */
    private Long streamScopeId = null;

    /**
     * if the current user liked this resource.
     */
    private boolean isLiked;

    /**
     * @return the likeCount
     */
    public int getLikeCount()
    {
        return likeCount;
    }

    /**
     * @param inLikeCount
     *            the likeCount to set
     */
    public void setLikeCount(final int inLikeCount)
    {
        likeCount = inLikeCount;
    }

    /**
     * @return the shareCount
     */
    public int getShareCount()
    {
        return shareCount;
    }

    /**
     * @param inShareCount
     *            the shareCount to set
     */
    public void setShareCount(final int inShareCount)
    {
        shareCount = inShareCount;
    }

    /**
     * @return the likersSample
     */
    public List<PersonModelView> getLikersSample()
    {
        return likersSample;
    }

    /**
     * @param inLikersSample
     *            the likersSample to set
     */
    public void setLikersSample(final List<PersonModelView> inLikersSample)
    {
        likersSample = inLikersSample;
    }

    /**
     * @return the sharersSample
     */
    public List<PersonModelView> getSharersSample()
    {
        return sharersSample;
    }

    /**
     * @param inSharersSample
     *            the sharersSample to set
     */
    public void setSharersSample(final List<PersonModelView> inSharersSample)
    {
        sharersSample = inSharersSample;
    }

    /**
     * @return the key
     */
    public String getKey()
    {
        return key;
    }

    /**
     * @param inKey
     *            the key to set
     */
    public void setKey(final String inKey)
    {
        key = inKey;
    }

    /**
     * @return the streamScopeId
     */
    public Long getStreamScopeId()
    {
        return streamScopeId;
    }

    /**
     * @param inStreamScopeId
     *            the streamScopeId to set
     */
    public void setStreamScopeId(final Long inStreamScopeId)
    {
        streamScopeId = inStreamScopeId;
    }

    /**
     * Set if the current user liked this.
     * 
     * @param inIsLiked
     *            if the current user liked this resource.
     */
    public void setIsLiked(final boolean inIsLiked)
    {
        isLiked = inIsLiked;
    }

    /**
     * @return if the current user liked this resource.
     */
    public boolean isLiked()
    {
        return isLiked;
    }

}
