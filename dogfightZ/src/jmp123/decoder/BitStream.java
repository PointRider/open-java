/*
* BitStream.java -- 读取位流
* Copyright (C) 2010
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*
* If you would like to negotiate alternate licensing terms, you may do
* so by contacting the author: <http://jmp123.sourceforge.net/>
*/
package jmp123.decoder;

/**
 * 从输入的字节流获得位流。
 * <p>对于Layer3文件解码， BitStream 仅用于读取帧边信息，要读取Layer3文件的主数据，必须使用{@link BitStreamMainData}。
 * <p>对于Layer1和Layer2文件解码，用 BitStream 获得的位流，用于解码全部数据。
 * @version 0.400
 */
public class BitStream {
	protected int bitPos;
	protected int bytePos;
	protected byte[] bitReservoir;
	private int endPos;		//bitReservoir已填入的字节数
	private int maxOff;

	/**
	 * 创建一个位流BitStream对象，位流的缓冲区大小len指定，位流的缓冲区尾部空出的长度由extra指定。
	 * 
	 * @param len 缓冲区可访问长度。<br>
	 *            缓冲区用于解码帧边信息时len为9、17或32。<br>
	 *            缓冲区用于解码主数据(main_data)时长度不小于最大帧长512+1732。
	 * 
	 * @param extra 缓冲区尾部空出的字节数，防止哈夫曼解码时位流有错误导致缓冲区溢出，
	 * 尾部空出512字节(part2_3_length长度，2^12位)。
	 */
	public BitStream(int len, int extra) {
		maxOff = len;
		bitReservoir = new byte[len + extra];
	}

	/**
	 * 向缓冲区添加len字节。
	 * 
	 * @param b 源数据。
	 * @param off 源数据偏移量。
	 * @param len 源数据长度。
	 * @return 实际填充到缓冲区的字节数。
	 */
	public int append(byte[] b, int off, int len) {
		if (len + endPos > maxOff) {
			// 将缓冲区bytePos及之后的(未处理过的)数据移动到缓冲区首
			System.arraycopy(bitReservoir, bytePos, bitReservoir, 0, endPos - bytePos);
			endPos -= bytePos;
			bitPos = bytePos = 0;
		}
		if (len + endPos > maxOff)
			len = maxOff - endPos;
		System.arraycopy(b, off, bitReservoir, endPos, len);
		endPos += len;
		return len;
	}

	/**
	 * 将缓冲指定为b，缓冲区初始偏移量由off指定。与 {@link #append(byte[], int, int)}
	 * 方法的区别是，本方法不会从源数据b复制数据。
	 * 
	 * @param b 源数据。
	 * @param off 源数据偏移量。
	 */
	public void feed(byte[] b, int off) {
		bitReservoir = b;
		bytePos = off;
		bitPos = 0;
	}

	/**
	 * 从缓冲区读取一位。
	 * @return 0或1。
	 */
	public int get1Bit() {
		int bit = bitReservoir[bytePos] << bitPos;
		bit >>= 7;
		bit &= 0x1;
		bitPos++;
		bytePos += bitPos >> 3;
		bitPos &= 0x7;
		return bit;
	}

	/**
	 * 从缓冲区读取n位。由于运行时速度方面的原因，若读取的位数不大于9，请考虑用{@link #getBits9(int)}方法更高效。
	 * @param n 比特数，n=2..17时调用此方法。
	 * @return n位的值。
	 */
	public int getBits17(int n) {
		int iret = bitReservoir[bytePos];
		iret <<= 8;
		iret |= bitReservoir[bytePos + 1] & 0xff;
		iret <<= 8;
		iret |= bitReservoir[bytePos + 2] & 0xff;
		iret <<= bitPos;
		iret &= 0xffffff; // 高8位置0
		iret >>= 24 - n;
		bitPos += n;
		bytePos += bitPos >> 3;
		bitPos &= 0x7;
		return iret;
	}

	/**
	 * 从缓冲区读取n位。
	 * @param n 比特数，n=2..9时调用此方法。
	 * @return n位的值。
	 */
	public int getBits9(int n) {
		int iret = bitReservoir[bytePos];
		iret <<= 8;
		iret |= bitReservoir[bytePos + 1] & 0xff;
		iret <<= bitPos;
		iret &= 0xffff; // 高16位置0
		iret >>= 16 - n;
		bitPos += n;
		bytePos += bitPos >> 3;
		bitPos &= 0x7;
		return iret;
	}

	/**
	 * 获取缓冲区字节指针。
	 * @return 缓冲区字节指针。
	 */
	public int getBytePos() {
		return bytePos;
	}

	/**
	 * 获取缓冲区已经填入的字节数。
	 * @return 缓冲区已经填入的字节数。
	 */
	public int getSize() {
		return endPos;
	}

	/**
	 * 缓冲区丢弃n字节，缓冲区比特指针复位。
	 * @param n 丢弃的字节数。
	 */
	public void skipBytes(int n) {
		bytePos += n;
		bitPos = 0;
	}
	
	/**
	 * 缓冲区丢弃或回退指定比特。
	 * @param n 若n>0丢弃n比特，若n<0则回退-n比特。
	 */
	public void skipBits(int n) {
		bitPos += n;
		bytePos += bitPos >> 3;
		bitPos &= 0x7;
	}

	//----debug
	private int markPos;
	protected void mark() {
		markPos = bytePos;
	}
	protected int getMark() {
		return bytePos - markPos;
	}
}
