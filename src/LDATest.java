import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceArray2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.Token2FeatureVector;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.ArrayIterator;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

public class LDATest {
public static void main(String[] args) {
	List<String> dataList=new ArrayList<String>();
	String sql="select * from opportunity";
	Connection con=ConnectionBuilder.getConnection();
	PreparedStatement sta;
	try {
		
		File f=new File("C:\\Users\\l1876\\Desktop\\project_files\\oppos.txt");
		FileWriter writer=new FileWriter(f);
		sta = con.prepareStatement(sql);
		ResultSet r= sta.executeQuery();
		while(r.next()) {
			//dataList.add(r.getString("oppo_desc"));
			writer.write(r.getString("oppo_desc")+"\n");
		}
		
		writer.flush();
		writer.close();
		
	}catch (Exception e) {
		// TODO: handle exception
	}finally {
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	//System.out.println(dataList.size());
//	
//	ArrayList<Pipe> pipes=new ArrayList<Pipe>();
//	pipes.add(new CharSequenceLowercase());
//	pipes.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
//	//pipes.add(new CharSequenceArray2TokenSequence());
//	pipes.add(new TokenSequenceRemoveStopwords());
//	pipes.add(new Token2FeatureVector());
//	
//	InstanceList instanceList=new InstanceList(new SerialPipes(pipes));
//	
//	for(String desc:dataList) {
//		instanceList.add(new Instance(desc, null, desc, null));
//	}
	
	
	//instanceList.addThruPipe(new ArrayIterator(dataList));
//    int numTopics = 100;
//    ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);
//
//    model.addInstances(instanceList);
//    model.setNumThreads(2);
//
//    // Run the model for 50 iterations and stop (this is for testing only, 
//    //  for real applications, use 1000 to 2000 iterations)
//    model.setNumIterations(50);
//    try {
//		model.estimate();
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
}
}
