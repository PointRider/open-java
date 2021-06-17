/*
 * IAudio.java -- 音频输出接口
 */
package jmp123.decoder;

/**
 * 音频输出接口。
 * @version 0.400
 */
public interface IAudio {
	/**
	 * 打开源数据行（音频输出设备）。
	 * 
	 * @param rate 音频采样率。
	 * @param channels 音频声道数。
	 * @param bufferSize 音频输出缓冲区长度。
	 * @return 打开成功返回true，否则返回false。
	 */
	public boolean open(int rate, int channels, int bufferSize);

	/**
	 * 将音频数据写入混频器。所请求的源数据是从PCM缓冲区 b 中读取的（从数组中首字节开始），
	 * 并且将被写入数据行的缓冲区。 如果调用者试图写入多于当前可写入数据量的数据，则此方法在
	 * 写入所请求数据量之前一直阻塞。即使要写入的请求数据量大于数据行的缓冲区大小 ，此方法也
	 * 适用。不过，如果在写入请求的数据量之前数据行已关闭、停止或刷新，则该方法不再阻塞。
	 * <p>本方法应该由管理PCM缓冲区的{@link AbstractLayer#outputPCM()}方法调用。
	 * 
	 * @param b 待写入的PCM源数据。
	 * @param off 源数据b的偏移量。
	 * @param size 写入的数据长度。
	 * @return 向混频器器写入PCM数据的字节数。
	 * @see AbstractLayer#outputPCM()
	 */
	public int write(byte[] b, int off, int size);

	/**
	 * 向混频器器写入数据或暂停向混频器器写入数据。
	 * <p>初始状态为向混频器器写入数据，首次调用后将暂停向混频器器写入数据。
	 * 
	 * @param started true表示向混频器器写入数据（即正常播放输出）；fase表示暂停向混频器器写入数据。
	 */
	public void start(boolean started);

	/**
	 * 通过在清空源数据行的内部缓冲区之前继续向混频器器写入数据，排空源数据行中的列队数据。在完成排空操作之前，此方法发生阻塞。因为这是一个阻塞方法，
	 * 所以应小心使用它。如果在队列中有数据的终止行上调用 drain()，则在该行正在运行和数据队列变空之前，此方法将发生阻塞。如果通过一个线程调用
	 * drain()，另一个线程继续填充数据队列，则该操作不能完成排空操作。此方法总是在关闭源数据行时返回。
	 * <p>
	 * 上述描述，看了让人挠头。换种说法，就是正常播放（不被用户终止）完一个文件时调用该方法，把已经向音频输出缓冲写入的数据刷一下，使之播放输出 。
	 * 如果不刷，文件解码完会立即关闭解码器，音频输出设备会随解码器关闭，那么最后一点儿已经被解码的数据不能播放输出。
	 * <p>
	 * 本方法应该由管理PCM缓冲区的{@link AbstractLayer#close(boolean)}方法调用。
	 * @see AbstractLayer#close(boolean)
	 */
	public void drain();

	/**
	 * 关闭源数据行（音频输出设备）。指示可以释放的该源数据行使用的所有系统资源，并且复位相关变量。
	 */
	public void close();
}
