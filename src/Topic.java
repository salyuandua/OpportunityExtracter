import java.util.LinkedList;


public class Topic {
private int index;
private LinkedList<String> words;

public Topic(int index,LinkedList<String> words) {
	this.index=index;
	this.words=words;
	
	
}
public int getIndex() {
	return index;
}
public void setIndex(int index) {
	this.index = index;
}
public LinkedList<String> getWords() {
	return words;
}
public void setWords(LinkedList<String> words) {
	this.words = words;
}

}
