

public class TopicCountNode implements Comparable<TopicCountNode>{

	private int topicIndex;
	private int topicCount;

	public TopicCountNode(int topicIndex, int topicCount) {
		
		this.topicIndex = topicIndex;
		this.topicCount = topicCount;
	}

	public int getTopicIndex() {
		return topicIndex;
	}

	public void setTopicIndex(int topicIndex) {
		this.topicIndex = topicIndex;
	}

	public int getTopicCount() {
		return topicCount;
	}

	public void setTopicCount(int topicCount) {
		this.topicCount = topicCount;
	}

	@Override
	public int compareTo(TopicCountNode o) {
		if(this.topicCount>o.topicCount) {
			return 1;
		}
		else if(this.topicCount<o.topicCount) {
			return -1;
		}else {
			return 0;
		}
		
	}
	@Override
	public boolean equals(Object obj) {
		TopicCountNode n=(TopicCountNode)obj;
		if(this.topicIndex==n.topicIndex) {
			return true;
		}
		return false;
	}
}
