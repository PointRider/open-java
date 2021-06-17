package jmp123.decoder;

/**
 * 解码1帧MPEG-1/MPEG-2/MPEG-2.5 Audio LayerⅠ/Ⅱ/Ⅲ  并完成音频输出。
 * <p>源码下载： http://jmp123.sf.net/
 * @version 0.400
 */
public abstract class AbstractLayer {
	//public static boolean debug1; //----debug
	private int channels;

	/*
	 * 控制音频输出：暂停/继续
	 */
	private boolean started;
	private byte[] audioLock;

	/*
	 * Layer1, Layer2, Layer3 都有一个滤波器。
	 */
	private Synthesis filter;

	/*
	 * 音频输出对象。是一个引用变量，由外部初始化后传入。
	 */
	private IAudio audio;

	/*
	 * 管理PCM缓冲区的5个变量
	 */
	private byte[] pcmbuf;
	private int size;			// 一次向音频缓冲区pcmbuf写入的长度
	private int[] writeCursor;	// 两个声道向pcmbuf写入数据时使用的偏移量
	private int readCursor;		// 读取pcmbuf时使用的偏移量
	private int remainder;		// 未按计划输出完的残留的PCM长度

	/**
	 * 用指定帧头h和音频输出audio对象创建一个帧解码器。
	 * @param h 已经解码帧头信息的帧头对象。
	 * @param audio 音频输出对象。如果指定为 <b>null</b> 则调用 {@link #outputPCM()}
	 * 不产生输出，仅移动PCM缓冲区指针（表示缓冲区已经取走，可重新写入数据）。
	 */
	public AbstractLayer(Header h, IAudio audio) {
		this.audio = audio;
		started = true;
		size = 4 * h.getPcmSize();	//#### 几处数字不能更改

		channels = h.getChannels();
		audioLock = new byte[0];
		filter = new Synthesis(channels);
		writeCursor = new int[2];
		writeCursor[1] = 2;				//####
		pcmbuf = new byte[size * 4];	//####

		if (audio != null)
			audio.open(h.getSamplingRate(), channels, size * 4); //####
	}

	/**
	 * 从此缓冲区b中给定偏移量off处开始解码一帧（除帧头外的）音乐数据。
	 * @param b 源数据缓冲区。
	 * @param off 源数据缓冲区的偏移量。
	 * @return 源数据缓冲区新的偏移量，用于计算解码下一帧数据的开始位置在源数据缓冲区的偏移量。
	 */
	public abstract int decodeAudioData(byte[] b, int off);

	/**
	 * 一个子带多相合成滤滤。
	 * @param samples 输入的32个样本值。
	 * @param ch 当前声道。0表示左声道，1表示右声道。
	 */
	protected final void synthesisSubBand (float[] samples, int ch) {
		writeCursor[ch] = filter.synthesisSubBand(samples, ch, pcmbuf, writeCursor[ch]);
	}

	/**
	 * 是否到达PCM缓冲区末尾。如果到达缓冲区末尾，在缓冲区清空或者取走部分数据之前不能写入。
	 * @param ch 声道。0表示左声道，1表示右声道。
	 * @return 返回<b>true</b>表示到达缓冲区末尾，否则返回<b>false</b>。
	 */
	protected final boolean isEndOfBuffer(int ch) {
		return writeCursor[ch] >= pcmbuf.length;
	}

	/**
	 * 初始化。<p>本方法会被解码器在执行解码循环前调用，若子类需要完成某些特定的初始化，重载本方法。
	 */
	protected void initialize() {
	}

	/**
	 * 尝试输出解码结果。
	 * <p>多相合成滤波输出的PCM数据写入缓冲区，当缓冲区至少填入解码4帧得到的PCM数据才产生一次输出，
	 * 但调用者并不需要知道当前缓冲区是否已经填入足够数据。 防止缓冲区溢出，每解码最多4帧应调用本方法1次，
	 * 当然也可以每解码1帧就调用本方法1次。
	 * <p>若产生音频输出将，将从缓冲区取走解码4帧得到的PCM数据。
	 * <p><b>可能产生阻塞：</b>若音频输出已经停止，对本方法的调用将被阻塞，直到开始音频输出。如果输入流解码完，阻塞被自动清除。
	 * @see #startAudio()
	 */
	protected int outputPCM() {
		synchronized (audioLock) {
			//if (debug1)
			//System.out.printf("outputPCM(1): thread=%s, remainder=%d\n",Thread.currentThread().getName(), remainder);
			try {
				while (started == false)
					audioLock.wait();
			} catch (InterruptedException e) {
				return -1;
			}

			int w;
			while (remainder > 0) {
				if ((w = audio.write(pcmbuf, readCursor, remainder)) == 0)
					return 0;
				remainder -= w;
				if ((readCursor += w) >= pcmbuf.length)
					resetOffset();
				if (remainder == 0)
					return size; // 某一块数据输出完毕
			}

			if (writeCursor[0] < size + readCursor || (channels == 2 && writeCursor[1] < size + readCursor))
				return 0;
			w = (audio == null) ? size : audio.write(pcmbuf, readCursor, size);
			remainder = size - w;
			if ((readCursor += w) >= pcmbuf.length)
				resetOffset();
			//if (debug1)
			//System.out.printf("outputPCM(2), thread=%s, w=%d\n",Thread.currentThread().getName(), w);
			return (w != size) ? 0 : size;
		}
	}

	/**
	 * 开始或停止音频输出。
	 * @return 开始音频输出返回 <b>true</b>，否则返回 <b>false</b>。
	 */
	protected boolean startAudio() {
		synchronized (audioLock) {
			started = (!started);
			audio.start(started);
			if (started)
				audioLock.notifyAll();
			//if(debug1 == false)
			//debug1 = true;
			//System.out.println("\nAbstractLayer.startAudio ret: "+ started);
			return started;
		}
	}

	/**
	 * 获取音频输出状态。
	 * @return 已经开始音频输出返回<b>true</b>，若已停止音频输出则返回<b>false</b>。
	 */
	protected final boolean started() {
		synchronized (audioLock) {
			return this.started;
		}
	}

	/**
	 * 结束音乐数据解码，并将PCM缓冲区指针复位。如果因为音频输出已经停止而调用 {@link #outputPCM()} 被阻塞，阻塞将被清除。
	 * @param interrupted 是否需要以立即方式关闭。若为
	 * <b>false</b>，等待音频输出缓冲区的全部内容刷向音频输出对象后返回；若为 <b>true</b> 以立即方式返回。
	 */
	protected void close(boolean interrupted) {
		if (audio != null && started()) {
			if (!interrupted) {
				if(writeCursor[0] > readCursor)
					audio.write(pcmbuf, 0, writeCursor[0] - readCursor);
				audio.drain();
			}
			audio.close();
		}
		resetOffset();

		synchronized (audioLock) {
			started = true;
			audioLock.notifyAll();
		}
	}

	// 缓冲区pcmbuf指针复位
	private void resetOffset() {
		writeCursor[0] = readCursor = 0;
		writeCursor[1] = 2;
	}

}
