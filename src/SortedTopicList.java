
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson.JSON;


public class SortedTopicList{
private List<TopicCountNode> topiCountList;

public SortedTopicList() {
	topiCountList=new ArrayList<TopicCountNode>();
}	

/**
 * Get top n topics
 * @param topN
 * @return
 */
public List<TopicCountNode> getTopTopics(int topN){
//	if(topN>topiCountList.size()) {
//		throw new IllegalArgumentException("Number of top should be less than number of topics");
//	}
	Collections.sort(topiCountList);
	if(topiCountList.size()<=topN) {
		System.out.println(JSON.toJSONString(topiCountList));
		return topiCountList;
	}
	
	List<TopicCountNode> topTopicList=topiCountList.subList(topiCountList.size()-topN, topiCountList.size());
	
	//System.out.println(JSON.toJSONString(topTopicList));
	return topTopicList;

	
}


private void mergeNode(TopicCountNode topicNode) {
	for(TopicCountNode n:topiCountList) {
		if(n.equals(topicNode)) {
			n.setTopicCount(n.getTopicCount()+1);
			return;
		}
	}
	topiCountList.add(topicNode);
	
}

public void add(TopicCountNode topicNode) {
	mergeNode(topicNode);
	
	
}



}
