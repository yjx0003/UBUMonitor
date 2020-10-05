package es.ubu.lsi.ubumonitor.export.builder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.export.CSVBuilderAbstract;
import es.ubu.lsi.ubumonitor.model.DataBase;
import es.ubu.lsi.ubumonitor.model.DiscussionPost;

public class CSVDiscussionPost extends CSVBuilderAbstract{

	/** Header. */
	private static final String[] HEADER = new String[] { "forumId", "forumName", "discussionId", "discussionName", "parentId", "parentName", "discussionPostId", "discussionPostName", "userId",
			"userName",  "timeCreated", "message" };
	
	public CSVDiscussionPost(String name, DataBase dataBase) {
		super(name, dataBase, HEADER);
	}

	@Override
	public void buildBody() {
		Set<DiscussionPost> discussionPosts= Controller.getInstance().getActualCourse().getDiscussionPosts();
		for(DiscussionPost discussionPost:discussionPosts) {
			
			getData().add(new String[] { 
					Integer.toString(discussionPost.getForum().getCmid()),
					discussionPost.getForum().getModuleName(),
					Integer.toString(discussionPost.getDiscussion().getId()),
					discussionPost.getDiscussion().getName(),
					Integer.toString(discussionPost.getParent().getId()),
					discussionPost.getParent().getSubject(),
					Integer.toString(discussionPost.getId()),
					discussionPost.getSubject(),
					Integer.toString(discussionPost.getUser().getId()),
					discussionPost.getUser().getFullName(),
					Controller.DATE_TIME_FORMATTER.format(LocalDateTime.ofInstant(discussionPost.getCreated(), ZoneId.systemDefault())),
					discussionPost.getMessage()
			});
		}
		
	}

}
