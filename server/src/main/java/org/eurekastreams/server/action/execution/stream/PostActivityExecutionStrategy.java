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
package org.eurekastreams.server.action.execution.stream;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.SharedResourceRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.action.request.stream.PostActivityRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.ActivityVerb;
import org.eurekastreams.server.domain.stream.SharedResource;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.InsertMapper;
import org.eurekastreams.server.persistence.mappers.cache.PostActivityUpdateStreamsByActorMapper;
import org.eurekastreams.server.persistence.mappers.requests.InsertActivityCommentRequest;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.eurekastreams.server.persistence.mappers.stream.InsertActivityComment;
import org.eurekastreams.server.service.actions.strategies.RecipientRetriever;

/**
 * This class contains the business logic for posting an Activity to the system.
 * 
 */
public class PostActivityExecutionStrategy implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /**
     * Local logger instance.
     */
    private final Log logger = LogFactory.make();

    /**
     * Instance of {@link InsertMapper} for {@link Activity} objects.
     */
    private final InsertMapper<Activity> insertMapper;

    /**
     * Instance of the {@link InsertActivityComment} mapper.
     */
    private final InsertActivityComment insertCommentDAO;

    /**
     * Instance of the {@link BulkActivitiesMapper}.
     */
    private final DomainMapper<List<Long>, List<ActivityDTO>> activitiesMapper;

    /**
     * Instance of the {@link RecipientRetriever} responsible for retrieving the recipient from the {@link ActivityDTO}.
     */
    private final RecipientRetriever recipientRetriever;

    /**
     * Instance of the {@link PostActivityUpdateStreamsByActorMapper} responsible for updating the cached lists directly
     * related to the actor of the {@link Activity}.
     */
    private final PostActivityUpdateStreamsByActorMapper updateStreamsByActorMapper;

    /**
     * Mapper to get or insert shared resources.
     */
    private DomainMapper<SharedResourceRequest, SharedResource> findOrInsertSharedResourceMapper;

    /**
     * Constructor for the PostActivityExecutionStrategy.
     * 
     * @param inInsertMapper
     *            - instance of the {@link InsertMapper} for the {@link Activity} object.
     * @param inInsertCommentDAO
     *            - instance of the {@link InsertActivityComment} mapper.
     * @param inActivitiesMapper
     *            - instance of the {@link BulkActivitiesMapper}.
     * @param inRecipientRetriever
     *            - instance of the {@link RecipientRetriever}.
     * @param inUpdateStreamsByActorMapper
     *            - instance of the {@link PostActivityUpdateStreamsByActorMapper}.
     * @param inFindOrInsertSharedResourceMapper
     *            mapper to find or insert shared resources
     */
    public PostActivityExecutionStrategy(final InsertMapper<Activity> inInsertMapper,
            final InsertActivityComment inInsertCommentDAO,
            final DomainMapper<List<Long>, List<ActivityDTO>> inActivitiesMapper,
            final RecipientRetriever inRecipientRetriever,
            final PostActivityUpdateStreamsByActorMapper inUpdateStreamsByActorMapper,
            final DomainMapper<SharedResourceRequest, SharedResource> inFindOrInsertSharedResourceMapper)
    {
        insertMapper = inInsertMapper;
        insertCommentDAO = inInsertCommentDAO;
        activitiesMapper = inActivitiesMapper;
        recipientRetriever = inRecipientRetriever;
        updateStreamsByActorMapper = inUpdateStreamsByActorMapper;
        findOrInsertSharedResourceMapper = inFindOrInsertSharedResourceMapper;
    }

    /**
     * {@inheritDoc}.
     * 
     * Perform the business logic for posting an {@link Activity} to the system.
     * 
     * Create the {@link Activity} object from the provided {@link ActivityDTO}, assign appropriate values, update the
     * cached streams related to the actor (surgical strike) and submit assemble async requests to be submitted to the
     * queue.
     * 
     * @return Populated instance of the {@link ActivityDTO} after being persisted.
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
    {
        ActivityDTO inActivityDTO = ((PostActivityRequest) inActionContext.getActionContext().getParams())
                .getActivityDTO();
        ActivityDTO persistedActivityDTO;
        Activity newActivity = convertDTOToActivity(inActivityDTO);
        List<UserActionRequest> queueRequests = new ArrayList<UserActionRequest>();

        String actorAccountName = inActionContext.getActionContext().getPrincipal().getAccountId();

        newActivity.setPostedTime(new Date());
        newActivity.setActorId(actorAccountName);
        newActivity.setActorType(EntityType.PERSON);

        long actorId = 0;
        long destinationId = 0;
        EntityType destinationType;

        // Persist to long term storage.
        insertMapper.execute(new PersistenceRequest<Activity>(newActivity));
        insertMapper.flush();

        // Force the cache to load the activityDTO in from the db.
        List<ActivityDTO> activityResults = activitiesMapper.execute(Arrays.asList(newActivity.getId()));
        persistedActivityDTO = activityResults.get(0);
        actorId = persistedActivityDTO.getActor().getId();
        destinationId = persistedActivityDTO.getDestinationStream().getDestinationEntityId();
        destinationType = persistedActivityDTO.getDestinationStream().getType();

        // add activity to destination entity streams
        updateStreamsByActorMapper.execute(persistedActivityDTO);

        // Insert the comment that was posted with a shared post.
        if (inActivityDTO.getFirstComment() != null && inActivityDTO.getVerb().equals(ActivityVerb.SHARE))
        {
            insertCommentDAO.execute(new InsertActivityCommentRequest(actorId, persistedActivityDTO.getId(),
                    inActivityDTO.getFirstComment().getBody()));
        }

        RequestType requestType = null;

        // Sends notifications for new personal stream posts.
        if (destinationType == EntityType.PERSON)
        {
            requestType = RequestType.STREAM_POST;
        }

        // Sends notifications for new group stream posts.
        else if (destinationType == EntityType.GROUP)
        {
            requestType = RequestType.GROUP_STREAM_POST;
        }

        // Setup the queued requests.
        if (requestType != null)
        {
            CreateNotificationsRequest notificationRequest = new CreateNotificationsRequest(requestType, actorId,
                    destinationId, persistedActivityDTO.getEntityId());
            queueRequests.add(new UserActionRequest("createNotificationsAction", null, notificationRequest));
        }
        // TODO: fix this so activityDTO fields related to specific user
        // are not saved in cache.

        queueRequests.add(new UserActionRequest("postActivityAsyncAction", null, new PostActivityRequest(
                persistedActivityDTO)));

        inActionContext.getUserActionRequests().addAll(queueRequests);

        return persistedActivityDTO;
    }

    /**
     * Method to convert ActivityDTO to an Activity object.
     * 
     * @param inActivityDTO
     *            - ActivityDTO instance to be converted.
     * @return - Activity object populated with the values from the ActivityDTO passed in.
     */
    private Activity convertDTOToActivity(final ActivityDTO inActivityDTO)
    {
        Activity currentActivity = new Activity();
        currentActivity.setAnnotation(inActivityDTO.getAnnotation());
        // This will only occur in the Share verb scenario.

        if (inActivityDTO.getBaseObjectProperties().containsKey("originalActivityId")
                && (inActivityDTO.getBaseObjectProperties().get("originalActivityId") != null))
        {
            try
            {
                currentActivity.setOriginalActivityId(new Long(inActivityDTO.getBaseObjectProperties().get(
                        "originalActivityId")));
            }
            catch (NumberFormatException nex)
            {
                logger.error("Error occurred parsing original activity id: "
                        + inActivityDTO.getBaseObjectProperties().get("originalActivityId"), nex);
            }
        }

        if (inActivityDTO.getBaseObjectProperties().containsKey("targetUrl")
                && inActivityDTO.getBaseObjectProperties().get("targetUrl") != null)
        {
            String url = inActivityDTO.getBaseObjectProperties().get("targetUrl");
            // has a link to share
            logger.info("New activity shares link with url: " + url);
            SharedResource sr = findOrInsertSharedResourceMapper.execute(new SharedResourceRequest(url, null));
            if (sr != null)
            {
                logger.info("Found shared resource - id: " + sr.getId());
                currentActivity.setSharedLink(sr);
            }
        }

        currentActivity.setBaseObject(inActivityDTO.getBaseObjectProperties());
        currentActivity.setBaseObjectType(inActivityDTO.getBaseObjectType());
        currentActivity.setLocation(inActivityDTO.getLocation());
        currentActivity.setMood(inActivityDTO.getMood());
        currentActivity.setOriginalActorId(inActivityDTO.getOriginalActor().getUniqueIdentifier());
        currentActivity.setOriginalActorType(inActivityDTO.getOriginalActor().getType());
        currentActivity.setRecipientParentOrg(recipientRetriever.getParentOrganization(inActivityDTO));
        currentActivity.setRecipientStreamScope(recipientRetriever.getStreamScope(inActivityDTO));
        currentActivity.setVerb(inActivityDTO.getVerb());
        currentActivity.setIsDestinationStreamPublic(recipientRetriever.isDestinationStreamPublic(inActivityDTO));

        currentActivity.setAppId(inActivityDTO.getAppId());
        currentActivity.setAppName(inActivityDTO.getAppName());
        currentActivity.setAppSource(inActivityDTO.getAppSource());
        currentActivity.setAppType(inActivityDTO.getAppType());
        currentActivity.setShowInStream(inActivityDTO.getShowInStream());

        return currentActivity;
    }
}
