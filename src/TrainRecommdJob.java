import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import java.util.TreeSet;
import java.util.regex.Pattern;

import org.junit.Test;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.w3c.dom.stylesheets.LinkStyle;

import com.alibaba.fastjson.JSON;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicAssignment;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelSequence;

public class TrainRecommdJob implements Job{
private OpportunitiesDataReader oppoReader=new OpportunitiesDataReader(ConnectionBuilder.getConnection());
private List<Topic> topics;	
private List<SortedTopicList> topicsCounts;	
private OpportunityService oService=new OpportunityService();
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//String filePath=context.getJobDetail().getJobDataMap().getString("filePath");
		String filePath="C:\\Users\\l1876\\Desktop\\project_files\\oppos.txt";
		//read opportunities data from database to file
		List<Opportunity> oppos=oppoReader.readData2File(filePath);
		//train
		try {
			train(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//set new topics
		setTopic4db(oppos, ConnectionBuilder.getConnection());
		
		
		
		
	}
	
	
	
	public void predict4User() {
		//write for testing
		File f=new File("C:\\Users\\l1876\\Desktop\\project_files\\distance.txt");
		FileWriter w=null;
		//predict oppos list
		List<Opportunity> predictOppos=new ArrayList<Opportunity>();
		try {
			w=new FileWriter(f);

		
		//get like list
		List<Opportunity> likeOppos=oService.getLikeOppos();
		//get dislike list
		List<Opportunity> dislikeOppos=oService.getDislikeOppos();
		//potiental opportunities
		List<Opportunity> potentialOppos=oService.getPotentialOppos();

		for(Opportunity potentialOppo:potentialOppos) {
			long distance2Like=0;
			long distance2Dislike=0;
			//compute sum distance with like-opportunities
			
			for(Opportunity likeOppo:likeOppos) {
				distance2Like+=computeDocsDistance(potentialOppo.getTopic(), likeOppo.getTopic());
				
				
			}
			//System.out.println(distance2Like/likeOppos.size());
			//compute sum distance with dislike-opportunities
			
			for(Opportunity dislikeOppo:dislikeOppos) {
				distance2Dislike+=computeDocsDistance(potentialOppo.getTopic(), dislikeOppo.getTopic());
				//System.out.println(distance2Dislike/likeOppos.size());
			}
			distance2Like=distance2Like/likeOppos.size();
			distance2Dislike=distance2Dislike/dislikeOppos.size();

			w.write("\n"+distance2Like+","+distance2Dislike);
			w.flush();
			if(distance2Dislike>=distance2Like) {
				predictOppos.add(potentialOppo);
			}
		}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				w.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(predictOppos.size());
	}
	
	
	
	
	
	/**
	 * Compute distance between 
	 * @param doc1
	 * @param doc2
	 * @return
	 */
	
	private long computeDocsDistance(String doc1,String doc2) {
		doc1=doc1.trim();
		doc2=doc2.trim();
		if(doc1.equals(doc2)) {
			return 0;
		}
		String[] docArray1=doc1.split(" ");
		String[] docArray2=doc2.split(" ");
		long distance=0;
		for(int i=0;i<docArray1.length;i++) {
			for(int j=0;j<docArray2.length;j++) {
				if(!docArray1[i].equals(docArray2[j])) {
					distance++;
				}
			}
		}
		return distance;
		
	}
	
	
	
	/**
	 * update topic for database
	 */
	private void setTopic4db(List<Opportunity> oppos,Connection con) {
		if(oppos.size()!=topicsCounts.size()) {
			throw new IllegalArgumentException("The size of data not match");
		}
		String sql="update opportunity set oppo_topic=? where oppo_id=?";
		try {
			PreparedStatement sta=con.prepareStatement(sql);
			//update
			for(int i=0;i<oppos.size();i++) {
				Opportunity oppo=oppos.get(i);
				SortedTopicList sortedTopicList=topicsCounts.get(i);
				//get top 3 topics
				List<TopicCountNode> topicList= sortedTopicList.getTopTopics(3);
				//construct topic string
				StringBuilder topicStr=new StringBuilder("");
				for(TopicCountNode node:topicList) {
					int topicCount= node.getTopicCount();
					//100 words max for a topic
					if(topicCount>100) {
						topicCount=100;
					}
					//System.out.println(topicCount);
					for(int j=0;j<topicCount;j++) {
						topicStr.append(topics.get(node.getTopicIndex()).getWords().get(j)+" ") ;
					}

				}
				//update topicStr to db
				
				
				//System.out.println(topicStr.toString());
				sta.clearParameters();
				sta.setString(1, topicStr.toString());
				sta.setInt(2, oppo.getOppo_id());
				sta.executeUpdate();
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		
		
		
	}
	
	
	
	
	
/**
 * Training process 
 * @param filePath
 * @throws IOException
 */
	private void train(String filePath) throws IOException {
		// Begin by importing documents from text to feature sequences
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Pipes: lowercase, tokenize, remove stopwords, map to features
		pipeList.add( new CharSequenceLowercase() );
		pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
		pipeList.add( new TokenSequenceRemoveStopwords());
		pipeList.add( new TokenSequence2FeatureSequence() );

		InstanceList instances = new InstanceList (new SerialPipes(pipeList));
		
		Reader fileReader = new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8");
		instances.addThruPipe(new CsvIterator (fileReader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
											   3, 2, 1)); // data, label, name fields

		int numTopics = 100;
		ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);

		model.addInstances(instances);
		model.setNumThreads(2);

		model.setNumIterations(2000);
		model.estimate();
		
		//get alphabet that map word's id to string
		Alphabet alphabet=instances.getAlphabet();

				// Get an array of sorted sets of word ID/count pairs
		
		//Get topics
		ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
				
		// Show top 5 words in topics with proportions for the first document
		topics=new ArrayList<Topic>(100);
		for (int topic = 0; topic < numTopics; topic++) {
			
			Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();

			LinkedList<String> topicWords=new LinkedList<String>();
			int rank = 0;
			while (iterator.hasNext() && rank < 100) {
					IDSorter idCountPair = iterator.next();
					topicWords.add(alphabet.lookupObject(idCountPair.getID()).toString());
					rank++;
					}
			topics.add(new Topic(topic, topicWords));
		}
		
		//System.out.println(JSON.toJSONString(topics));
		
		//Generate topic for each opportunity
		List<TopicAssignment> datas= model.getData();
		topicsCounts=new ArrayList<SortedTopicList>();
		for(int i=0;i<datas.size();i++) {
			FeatureSequence docVec = (FeatureSequence) model.getData().get(i).instance.getData();//features vector for one document
			LabelSequence topic4Doc = model.getData().get(i).topicSequence;//topics for each word in doc
			SortedTopicList sortedTopicList=new SortedTopicList();
			//loop all words in this doc
			for (int position = 0; position < docVec.getLength(); position++) {
				int topicIndex=topic4Doc.getIndexAtPosition(position);

				TopicCountNode tNode=new TopicCountNode(topicIndex, 1);
				sortedTopicList.add(tNode);
			}
			sortedTopicList.getTopTopics(3);
			topicsCounts.add(sortedTopicList);
			
			
			
		}
		
		

	}
	
	public static void main(String[] args) {
		TrainRecommdJob j=new TrainRecommdJob();
//		try {
//			j.execute(null);
//		} catch (JobExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		j.predict4User();
		//System.out.println(j.computeDocsDistance(" jack loves you and me", "me first loves is jack"));
		
		
		
	}
}
