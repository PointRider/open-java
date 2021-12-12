package jmp123.decoder;

/**
 * 轻量级解码器。
 * <p>AbstractDecoder的 {@link #id3v1()}， {@link #id3v2()}， {@link #vbr(Header)}，{@link #done()}，
 * {@link #cooperate()} 方法内部什么也没做，要执行相应的具体功能，须在子类中重载实现。
 * AbstractDecoder 定义这些方法，是为了保证这些方法在适当的时候被解码器调用。
 * @version 0.400
 */
public abstract class AbstractDecoder {
	private boolean eof;
	private boolean sync;	// true:帧头的特征未改变
	private int headerMask;
	private int frameCount;	//当前帧序号
	private int skipped;
	private int pos, endPos;
	private final byte[] buf;
	private final Header header;
	private final IAudio audio;
	private AbstractLayer layer;
	private boolean isInterrupted;
    //private boolean isInterrupted;
	
	public synchronized final boolean isInterrupted() {
        return isInterrupted;
    }

    public synchronized final void setInterrupted(boolean isInterrupted) {
        this.isInterrupted = isInterrupted;
    }
    
    /**
	 * 用指定的音频输出audio创建一个AbstractDecoder对象。
	 * @param audio 音频输出对象。若为 <b>null</b>，解码但不产生输出。
	 */
	public AbstractDecoder(IAudio audio) {
		this.audio = audio;
		header = new Header();
		buf = new byte[8192];
		isInterrupted = false;
	}
    
	/**
	 * 开始解码，直至到达流的结尾或被用户终止。
	 * <p>如果解码过程中被用户终止，将以立即方式结束音乐数据解码。
	 * @see AbstractLayer#close(boolean)
	 */
	public final void run() {

        isInterrupted = false;
        
	    try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        
		if (layer != null) {
			layer.initialize();
			
			while (!(eof || isInterrupted)) {
				pos = layer.decodeAudioData(buf, pos);
				//eof = isInterrupted = !cooperate();
				nextFrameHeader();
			}
			//System.out.println("\nAbstractDecoder.run: eof = " + eof + ", isInterrupted = " + isInterrupted);
			
			layer.close(isInterrupted);
		}

		endPos = pos = 0;
		layer = null;
		done();
	}

	/**
	 * 暂停或继续解码。这相当于一个单稳态的触发开关，第一次调用该方法暂停解码，第二次调用继续解码，以此类推。
	 * @return 当前状态。若为<b>true</b>表示解码器已经暂停，否则表示解码器正在解码。
	 */
	public final boolean pause() {
		return (layer == null) ? false : (layer.startAudio() == false);
	}

	/**
	 * 获取解码器状态。
	 * @return 若已暂停返回<b>true</b>，否则返回<b>false</b>。
	 */
	public final boolean isPaused() {
		return (layer == null) ? false : (layer.started() == false);
	}

	/**
	 * 获取当前帧序号。
	 * @return 当前帧序号。
	 */
	public final int getFrameCount() {
		return frameCount;
	}

	/**
	 * 初始化解码器。
	 * @return 帧头的简短信息。
	 */
	protected final String openDecoder() {
		headerMask = 0xffe00000;
		eof = sync = false;
		frameCount = 0;
		endPos = pos = buf.length;
		header.initialize();

		id3v1();
		id3v2();
		nextFrameHeader();

		switch (header.getLayer()) {
		case 1:
			layer = new Layer1(header, audio);
			break;
		case 2:
			layer = new Layer2(header, audio);
			break;
		case 3:
			layer = new Layer3(header, audio);
			break;
		default:
			eof = true;
		}

		vbr(header); //####

		return header.toString();
	}

	/**
	 * 填充数据到目标冲区b。解码器内部有一个缓冲区，解码器会在需要的时候调用本方法向其填充数据。
	 * @param b 接收数据的目标缓冲区，其实就是解码器内部的一个缓冲区。
	 * @param off 目标缓冲区的偏移量。
	 * @param len 填充数据的长度。
	 * @return 实际填充数据的长度。
	 */
	protected abstract int fillBuffer(byte[] b, int off, int len);

	/**
	 * 解析 ID3 tag v1.
	 */
	protected void id3v1() {
	}

	/**
	 * 解析 ID3 tag v2.
	 */
	protected void id3v2() {
	}

	/**
	 * 解析 VBR 信息。
	 * @param h 帧头对象，该帧头对象已经被解码器正确初始化。该方法被调用时表明解码器已经成功定位到第一帧并且完成了帧头解码。
	 */
	protected void vbr(Header h) {
	}

	/**
	 * 解码器协作事务处理。cooperate 方法会在 {@link #run() run}
	 * 方法内每解码一帧后被调用一次，子类重载cooperate方法用于处理解码过程中的其它事务。
	 * 
	 * @return 返回值能够控制 {@link #run() run} 方法的执行流程。如果返回值为 <b>true</b> 则
	 * {@link #run() run} 循环解码下一帧，如果返回值为 <b>false</b> 则退出循环结束解码过程。
	 */
	protected boolean cooperate() {
		return true;
	}

	/**
	 * 解码结束时被解码器自动调用。
	 */
	protected void done() {
	}

	/**
	 * 从解码器内部的缓冲区复制数据到b。复制数据后不会移动缓冲区指针。
	 * @param b 接收数据的目标缓冲区。
	 * @param off 目标缓冲区的偏移量。
	 * @param len 复制数据的长度。
	 * @return 实际复制数据的长度，不超过源数据缓冲区的长度。
	 */
	protected final int copy(byte[] b, int off, int len) {
		int rema = endPos - pos;

		if (len > rema) {
			if (rema > 0)
				System.arraycopy(buf, pos, buf, 0, rema);
			pos = 0;
			int r = fillBuffer(buf, rema, buf.length - rema);
			endPos = (r <= 0) ? rema : buf.length;
			if (len > endPos)
				len = endPos;
		}
		System.arraycopy(buf, pos, b, off, len);

		return len;
	}

	/**
	 * 从解码器内部的缓冲区读取数据到b。
	 * @param b 接收数据的目标缓冲区。
	 * @param off 目标缓冲区的偏移量。
	 * @param len 复制数据的长度，可超过源数据缓冲区的长度。
	 * @return 实际复制数据的长度。如果到达流的末尾或者发生I/O错误都将导致返回值小于len。
	 */
	protected final int read(byte[] b, int off, int len) {
		int rema = endPos - pos;

		if (len <= rema) {
			System.arraycopy(buf, pos, b, off, len);
			pos += len;
			return len;
		}

		if (rema > 0)
			System.arraycopy(buf, pos, b, off, rema);
		pos = endPos;
		int r = fillBuffer(b, off + rema, len - rema);
		return (r <= 0) ? rema : r + rema;
	}

	private int byte2int(byte[] b, int off) {
		int val = b[off] << 24;
		val |= (b[off + 1] & 0xff) << 16;
		val |= (b[off + 2] & 0xff) << 8;
		val |= (b[off + 3] & 0xff);
		return val;
	}

	private boolean available(int h, int mask) {
		return (h & mask) == mask
			&& ((h >> 19) & 3) != 1		// version ID:  '01' - reserved
			&& ((h >> 17) & 3) != 0		// Layer index: '00' - reserved
			&& ((h >> 12) & 15) != 15	// Bitrate Index: '1111' - reserved
			&& ((h >> 12) & 15) != 0	// Bitrate Index: '0000' - free
			&& ((h >> 10) & 3) != 3;	// Sampling Rate Index: '11' - reserved
	}

	private boolean syncFrame() {
		int h, framesize, mask = 0;
		int idx = pos;

		if (endPos - pos <= 4)
			return false;

		h = byte2int(buf, pos);
		pos += 4;

		while (!eof) {
			//1. 查找帧同步头
			while (!available(h, headerMask)) {
				h = (h << 8) | (buf[pos++] & 0xff);
				if (pos == endPos) {
					skipped += (pos -= 4) - idx;
					return false;
				}
			}
			if (pos > 4 + idx) {
				sync = false;
				skipped += pos - 4 - idx;
			}

			//2. 帧头解码
			header.decode(h);

			framesize = header.getFrameSize();
			if (pos + framesize > endPos + 4) { 	// 音乐数据不足一帧
				skipped += (pos -= 4) - idx;
				return false;
			}

			if (sync)	// version ID等帧的特征未改变,不用与下一帧的同步头比较
				break;

			//3. 与下一帧的同步头比较,确定是否找到有效的同步字.
			if (pos + framesize > endPos) {
				skipped += (pos -= 4) - idx;
				return false;
			}
			mask = 0xffe00000; 		// syncword
			mask |= h & 0x180000;	// version ID
			mask |= h & 0x60000;	// Layer index
			mask |= h & 0xc00;		// sampling_frequency
			// mode, mode_extension 不是每帧都相同.

			if (available(byte2int(buf, pos - 4 + framesize), mask)) {
				if (headerMask == 0xffe00000) {
					headerMask = mask;
				}
				sync = true;
				break; // 找到有效的帧同步字段，结束查找
			}

			//4. 移动到下一字节，继续重复1-3
			h = (h << 8) | (buf[pos++] & 0xff);
		}

		if (header.isProtected())
			pos += 2; // CRC-word
		frameCount++;

		if (skipped > 0) {
			System.out.printf("frame#%d skipped: %,d bytes.\n", frameCount, skipped);
			skipped = 0;
		}

		return true;
	}

	private void nextFrameHeader() {
		int len;
		while (eof == false && syncFrame() == false) {
			if((len = endPos - pos) > 0)
				System.arraycopy(buf, pos, buf, 0, len);
			endPos = len + fillBuffer(buf, len, pos);
			if (endPos <= len || pos > 0x10000) //64K
				eof = true;
			pos = 0;
		}
	}
}
