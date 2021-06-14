package std_Z;
import java.util.*;

public class Main
{

	public static void main(String[] args)
	{
		// TODO 自动生成的方法存根
		//Deque.defaultBufferSize = 2;
		Deque<String> dq = new Deque<String>();
		dq.push_back("abc");
		
		dq.push_back("def");
		dq.push_front("ghi");
		dq.push_front("jkl");
		dq.push_front("mno");
		dq.push_back("pqr");
		
		//dq.pop_back();
		//dq.pop_front();
		
		dq.printAllBuffer();
		Iterator<String> it=dq.Iterator();
		it.next();
		it.next();
		it.next();
		it.next();
		it.next();
		
		it.remove();
		
		dq.printAllBuffer();
		
		for(Iterator<String> i=dq.Iterator(); i.hasNext();)
		{
			System.out.println(i.next());
		}
		
		/*
		for(int i=0 ; i<dq.size() ; ++i)
			System.out.print(dq.get(i));
		*/
	}

}
