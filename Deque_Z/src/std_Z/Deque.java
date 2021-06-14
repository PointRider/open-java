package std_Z;

import java.util.HashMap;
import java.util.Iterator;

public class Deque<elementType>
{
	
	public class Iter implements Iterator<elementType>
	{
		protected 	Deque<elementType>	father;
		protected 	int 				currentIndex;
		protected 	int 				currentBufferIndex;
		protected 	elementType[] 		currentBuffer;
		
		public Iter(Deque<elementType> father_que, elementType[] startBuffer, int startBufferIndex, int startIndex)
		{
			father = father_que;
			currentIndex = startIndex;
			currentBuffer= startBuffer;
			currentBufferIndex  = startBufferIndex;
		}
		
		@Override
		public void remove()
		{
			// TODO: Implement this method
			int start;
			int end;
			elementType[] buffer, nextBuffer;
			
			if(currentIndex < 0)
				currentIndex = 0;
			currentBuffer[currentIndex] = null;
			
			nextBuffer = father.hashIndex.get(currentBufferIndex);
			for(int bufferIndex=currentBufferIndex; bufferIndex<=father.back_start;)
			{
				for
				(
					buffer= nextBuffer,
					start = bufferIndex==currentBufferIndex?currentIndex:0,
					end   = bufferIndex==father.back_start?father.back-father.back_start+1:father.bufferSize;
					start < end;
					++start
				)
				{
					if(start != end-1)
						buffer[start] = buffer[start+1];
					else
					{
						nextBuffer    = father.hashIndex.get(bufferIndex+=father.bufferSize);
						if(nextBuffer!= null)
							buffer[start] = nextBuffer[0];
					}
				}
			}
			father.pop_back();
		}

		@Override
		public elementType next()
		{
			// TODO: Implement this method
			if((++currentIndex) == father.bufferSize)
			{
				currentBufferIndex += father.bufferSize;
				currentBuffer= father.hashIndex.get(currentBufferIndex);
				currentIndex = 0;
			}
			return currentBuffer[currentIndex];
		}

		@Override
		public boolean hasNext()
		{
			// TODO: Implement this method
			return  currentBufferIndex 	!= father.back_start || 
					currentIndex 		!= father.back-father.back_start;
		}
	}
	
	public   static	int 						defaultBufferSize = 128;
	
	protected final int							bufferSize;		//单位: 个元素
	protected 		int							front;
	protected		int							back;
	protected		int							count;
	protected 		int							front_end;
	protected 		int							back_start;
	protected		elementType[]				front_buffer;	//跟踪以加速front元素获取操作
	protected		elementType[]				back_buffer;	//同上
	protected HashMap<Integer, elementType[]>	hashIndex;
	
	public Deque(int buffer_size)
	{
		bufferSize = buffer_size;
		
		front = 0;
		back  = -1;
		count  = 0;
		
		hashIndex = new HashMap<Integer, elementType[]>();
		
		front_end = back_start = 0;
		front_buffer = back_buffer = null;
	}
	
	public Deque()
	{
		this(defaultBufferSize);
	}
	
	@SuppressWarnings("unchecked")
	public elementType push_back(elementType element)
	{
		++back;
		
		elementType[] newRef;
		
		if(count == 0)
		{
			hashIndex.put(front_end = back_start = 0, newRef = (elementType[]) new Object[bufferSize]);
			front_buffer = back_buffer = newRef;
		}
		else if(back_start + bufferSize - 1 < back)
		{
			hashIndex.put(back_start += bufferSize,  newRef = (elementType[]) new Object[bufferSize]);
			back_buffer = newRef;
		}
		else
			newRef = hashIndex.get(back_start);
		
		++count;
		
		return newRef[back - back_start] = element;
	}
	
	@SuppressWarnings("unchecked")
	public elementType push_front(elementType element)
	{
		--front;
		
		elementType[] newRef;
		
		if(count == 0)
		{
			hashIndex.put(front_end = back_start = 0, newRef = (elementType[]) new Object[bufferSize]);
			front_buffer = back_buffer = newRef;
		}
		else if(front_end > front)
		{
			hashIndex.put(front_end -= bufferSize, newRef = (elementType[]) new Object[bufferSize]);
			front_buffer = newRef;
		}
		else
			newRef = hashIndex.get(front_end);
		
		++count;
		
		return newRef[front - front_end] = element;
	}
	
	public elementType get(int index)
	{
		if(index>count-1 || count==0 || index<0)
			throw new ArrayIndexOutOfBoundsException("length=" + count + "; index=" + index);
		int bufferIndex = (int)Math.floor((double)(index + front) / (double)bufferSize) * bufferSize;
		return hashIndex.get(bufferIndex)[index + front - bufferIndex];
	}
	
	public void printAllBuffer()
	{
		int start;
		int end;
		elementType[] buffer;
		
		System.out.println("buffer size: " + bufferSize + "xElements");
		System.out.println();
		
		for(int bufferIndex=front_end ; bufferIndex<=back_start ; bufferIndex+=bufferSize)
		{
			buffer= hashIndex.get(bufferIndex);
			start = bufferIndex==front_end ?front-front_end:0;
			end   = bufferIndex==back_start?back-back_start+1:bufferSize;
			
			System.out.print("buffer start at index " + bufferIndex + ":\t");
			
			for
			(
				start = bufferIndex==front_end ?front-front_end:0,
				end   = bufferIndex==back_start?back-back_start+1:bufferSize;
				start < end;
				++start
			)
			{
				System.out.print(buffer[start]);
				System.out.print(' ');
			}
			System.out.println();
		}
	}
	
	public elementType front()
	{
		return front_buffer[front - front_end];
	}
	
	public elementType back()
	{
		return back_buffer[back - back_start];
	}
	
	public int size()
	{
		return count;
	}
	
	public boolean empty()
	{
		return count == 0;
	}
	
	public void pop_back()
	{
		if(count > 0)
		{
			back_buffer[(back--) - back_start] = null;
			if(back - back_start < 0)
			{
				hashIndex.remove(back_start);
				back_start -= bufferSize;
				back_buffer = hashIndex.get(back_start);
			}
			--count;
		}
	}
	
	public void pop_front()
	{
		if(count > 0)
		{
			front_buffer[(front++) - front_end] = null;
			if(front - front_end + 1 > bufferSize)
			{
				hashIndex.remove(front_end);
				front_end += bufferSize;
				front_buffer = hashIndex.get(front_end);
			}
			--count;
		}
	}
	
	public Iter Iterator()
	{
		return new Iter
		(
			this, front_buffer,
			front_end,
			front - front_end - 1
		);
	}
}
