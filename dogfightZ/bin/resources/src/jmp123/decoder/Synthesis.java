/*
* Synthesis.java -- Layer I/II/III 一个子带多相合成滤波
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
* so by contacting the author: <http://jmp123.sf.net/>
*/
package jmp123.decoder;

/**
 * 子带多相合成滤波。
 * @version 0.400
 */
public class Synthesis {
	private final int nch;

	/*
	 * 暂存DCT输出结果的FIFO队列。
	 */
	private float[][] fifobuf;

	/*
	 * fifobuf的偏移量，用它完成FIFO队列的移位操作。
	 */
	private int[] fifoIndex;

	/**
	 * 子带多相合成滤波构造器。
	 * @param nch 声道数，用于计算输出PCM时的步长值。
	 */
	public Synthesis(int nch) {
		this.nch = nch;
		fifobuf = new float[nch][1024];
		fifoIndex = new int[nch];
	}

	/**
	 * 一个子带多相合成滤波。
	 * @param samples 源数据，为32个样本值。
	 * @param ch 当前的声道。左声道0，右声道1。
	 * @param b 接收输出结果的PCM缓冲区。
	 * @param off 缓冲区 b 的下标，本次开始向 b 写入数据的起始位置。
	 * @return 缓冲区 b 的下标，本次向 b 写入的结束位置。
	 */
	public int synthesisSubBand (float[] samples, int ch, byte[] b, int off) {
		final float[] fifo = fifobuf[ch];
		final byte[] buf = b;
		float sum, win[];
		int i, pcmi;
		/*
		 * 向PCM缓冲区写入数据的步长值，左右声道的PCM数据在PCM缓冲区内是交替排列的。
		 * 指示解码某一声道时写入一次数据后，下一次应该写入的位置。
		 */
		int step = (nch == 2) ? 4 : 2;

		//1. Shift
		fifoIndex[ch] = (fifoIndex[ch] - 64) & 0x3FF;
		//960,896,832,768,704,640,576,512,448,384,320,256,192,128,64,0

		//2. Matrixing
		dct32to64(samples, fifo, fifoIndex[ch]);

		//3. Build the U vector
		//4. Dewindowing
		//5. Calculate and output 32 samples
		switch(fifoIndex[ch]) {
		case 0:
		//u_vector={0,96,128,224,256,352,384,480,512,608,640,736,768,864,896,992}=u_base
			for(i = 0; i < 32; i++, off += step) {
				win = dewin[i];
				sum = win[0] * fifo[i];
				sum += win[1] * fifo[i + 96];
				sum += win[2] * fifo[i + 128];
				sum += win[3] * fifo[i + 224];
				sum += win[4] * fifo[i + 256];
				sum += win[5] * fifo[i + 352];
				sum += win[6] * fifo[i + 384];
				sum += win[7] * fifo[i + 480];
				sum += win[8] * fifo[i + 512];
				sum += win[9] * fifo[i + 608];
				sum += win[10] * fifo[i + 640];
				sum += win[11] * fifo[i + 736];
				sum += win[12] * fifo[i + 768];
				sum += win[13] * fifo[i + 864];
				sum += win[14] * fifo[i + 896];
				sum += win[15] * fifo[i + 992];
				pcmi = sum > 32767 ? 32767 : (sum < -32768 ? -32768 : (int)sum); //clip
				buf[off] = (byte)pcmi;
				buf[off + 1] = (byte)(pcmi >>> 8);
			}
			break;
		case 64:
			//u_vector={64,160,192,288,320,416,448,544,576,672,704,800,832,928,960,32}
			for(i = 0; i < 32; i++, off += step) {
				win = dewin[i];
				sum = win[0] * fifo[i + 64];
				sum += win[1] * fifo[i + 160];
				sum += win[2] * fifo[i + 192];
				sum += win[3] * fifo[i + 288];
				sum += win[4] * fifo[i + 320];
				sum += win[5] * fifo[i + 416];
				sum += win[6] * fifo[i + 448];
				sum += win[7] * fifo[i + 544];
				sum += win[8] * fifo[i + 576];
				sum += win[9] * fifo[i + 672];
				sum += win[10] * fifo[i + 704];
				sum += win[11] * fifo[i + 800];
				sum += win[12] * fifo[i + 832];
				sum += win[13] * fifo[i + 928];
				sum += win[14] * fifo[i + 960];
				sum += win[15] * fifo[i + 32];
				pcmi = sum > 32767 ? 32767 : (sum < -32768 ? -32768 : (int)sum);
				buf[off] = (byte)pcmi;
				buf[off + 1] = (byte)(pcmi >>> 8);
			}
			break;
		case 128:
			//u_vector={128,224,256,352,384,480,512,608,640,736,768,864,896,992,0,96}
			for(i = 0; i < 32; i++, off += step) {
				win = dewin[i];
				sum = win[0] * fifo[i + 128];
				sum += win[1] * fifo[i + 224];
				sum += win[2] * fifo[i + 256];
				sum += win[3] * fifo[i + 352];
				sum += win[4] * fifo[i + 384];
				sum += win[5] * fifo[i + 480];
				sum += win[6] * fifo[i + 512];
				sum += win[7] * fifo[i + 608];
				sum += win[8] * fifo[i + 640];
				sum += win[9] * fifo[i + 736];
				sum += win[10] * fifo[i + 768];
				sum += win[11] * fifo[i + 864];
				sum += win[12] * fifo[i + 896];
				sum += win[13] * fifo[i + 992];
				sum += win[14] * fifo[i];
				sum += win[15] * fifo[i + 96];
				pcmi = sum > 32767 ? 32767 : (sum < -32768 ? -32768 : (int)sum);
				buf[off] = (byte)pcmi;
				buf[off + 1] = (byte)(pcmi >>> 8);
			}
			break;
		case 192:
			//u_vector={192,288,320,416,448,544,576,672,704,800,832,928,960,32,64,160}
			for(i = 0; i < 32; i++, off += step) {
				win = dewin[i];
				sum = win[0] * fifo[i + 192];
				sum += win[1] * fifo[i + 288];
				sum += win[2] * fifo[i + 320];
				sum += win[3] * fifo[i + 416];
				sum += win[4] * fifo[i + 448];
				sum += win[5] * fifo[i + 544];
				sum += win[6] * fifo[i + 576];
				sum += win[7] * fifo[i + 672];
				sum += win[8] * fifo[i + 704];
				sum += win[9] * fifo[i + 800];
				sum += win[10] * fifo[i + 832];
				sum += win[11] * fifo[i + 928];
				sum += win[12] * fifo[i + 960];
				sum += win[13] * fifo[i + 32];
				sum += win[14] * fifo[i + 64];
				sum += win[15] * fifo[i + 160];
				pcmi = sum > 32767 ? 32767 : (sum < -32768 ? -32768 : (int)sum);
				buf[off] = (byte)pcmi;
				buf[off + 1] = (byte)(pcmi >>> 8);
			}
			break;
		case 256:
			//u_vector={256,352,384,480,512,608,640,736,768,864,896,992,0,96,128,224}
			for(i = 0; i < 32; i++, off += step) {
				win = dewin[i];
				sum = win[0] * fifo[i + 256];
				sum += win[1] * fifo[i + 352];
				sum += win[2] * fifo[i + 384];
				sum += win[3] * fifo[i + 480];
				sum += win[4] * fifo[i + 512];
				sum += win[5] * fifo[i + 608];
				sum += win[6] * fifo[i + 640];
				sum += win[7] * fifo[i + 736];
				sum += win[8] * fifo[i + 768];
				sum += win[9] * fifo[i + 864];
				sum += win[10] * fifo[i + 896];
				sum += win[11] * fifo[i + 992];
				sum += win[12] * fifo[i];
				sum += win[13] * fifo[i + 96];
				sum += win[14] * fifo[i + 128];
				sum += win[15] * fifo[i + 224];
				pcmi = sum > 32767 ? 32767 : (sum < -32768 ? -32768 : (int)sum);
				buf[off] = (byte)pcmi;
				buf[off + 1] = (byte)(pcmi >>> 8);
			}
			break;
		case 320:
			//u_vector={320,416,448,544,576,672,704,800,832,928,960,32,64,160,192,288}
			for(i = 0; i < 32; i++, off += step) {
				win = dewin[i];
				sum = win[0] * fifo[i + 320];
				sum += win[1] * fifo[i + 416];
				sum += win[2] * fifo[i + 448];
				sum += win[3] * fifo[i + 544];
				sum += win[4] * fifo[i + 576];
				sum += win[5] * fifo[i + 672];
				sum += win[6] * fifo[i + 704];
				sum += win[7] * fifo[i + 800];
				sum += win[8] * fifo[i + 832];
				sum += win[9] * fifo[i + 928];
				sum += win[10] * fifo[i + 960];
				sum += win[11] * fifo[i + 32];
				sum += win[12] * fifo[i + 64];
				sum += win[13] * fifo[i + 160];
				sum += win[14] * fifo[i + 192];
				sum += win[15] * fifo[i + 288];
				pcmi = sum > 32767 ? 32767 : (sum < -32768 ? -32768 : (int)sum);
				buf[off] = (byte)pcmi;
				buf[off + 1] = (byte)(pcmi >>> 8);
			}
			break;
		case 384:
			//u_vector={384,480,512,608,640,736,768,864,896,992,0,96,128,224,256,352}
			for(i = 0; i < 32; i++, off += step) {
				win = dewin[i];
				sum = win[0] * fifo[i + 384];
				sum += win[1] * fifo[i + 480];
				sum += win[2] * fifo[i + 512];
				sum += win[3] * fifo[i + 608];
				sum += win[4] * fifo[i + 640];
				sum += win[5] * fifo[i + 736];
				sum += win[6] * fifo[i + 768];
				sum += win[7] * fifo[i + 864];
				sum += win[8] * fifo[i + 896];
				sum += win[9] * fifo[i + 992];
				sum += win[10] * fifo[i];
				sum += win[11] * fifo[i + 96];
				sum += win[12] * fifo[i + 128];
				sum += win[13] * fifo[i + 224];
				sum += win[14] * fifo[i + 256];
				sum += win[15] * fifo[i + 352];
				pcmi = sum > 32767 ? 32767 : (sum < -32768 ? -32768 : (int)sum);
				buf[off] = (byte)pcmi;
				buf[off + 1] = (byte)(pcmi >>> 8);
			}
			break;
		case 448:
			//u_vector={448,544,576,672,704,800,832,928,960,32,64,160,192,288,320,416}
			for(i = 0; i < 32; i++, off += step) {
				win = dewin[i];
				sum = win[0] * fifo[i + 448];
				sum += win[1] * fifo[i + 544];
				sum += win[2] * fifo[i + 576];
				sum += win[3] * fifo[i + 672];
				sum += win[4] * fifo[i + 704];
				sum += win[5] * fifo[i + 800];
				sum += win[6] * fifo[i + 832];
				sum += win[7] * fifo[i + 928];
				sum += win[8] * fifo[i + 960];
				sum += win[9] * fifo[i + 32];
				sum += win[10] * fifo[i + 64];
				sum += win[11] * fifo[i + 160];
				sum += win[12] * fifo[i + 192];
				sum += win[13] * fifo[i + 288];
				sum += win[14] * fifo[i + 320];
				sum += win[15] * fifo[i + 416];
				pcmi = sum > 32767 ? 32767 : (sum < -32768 ? -32768 : (int)sum);
				buf[off] = (byte)pcmi;
				buf[off + 1] = (byte)(pcmi >>> 8);
			}
			break;
		case 512:
			//u_vector={512,608,640,736,768,864,896,992,0,96,128,224,256,352,384,480}
			for(i = 0; i < 32; i++, off += step) {
				win = dewin[i];
				sum = win[0] * fifo[i + 512];
				sum += win[1] * fifo[i + 608];
				sum += win[2] * fifo[i + 640];
				sum += win[3] * fifo[i + 736];
				sum += win[4] * fifo[i + 768];
				sum += win[5] * fifo[i + 864];
				sum += win[6] * fifo[i + 896];
				sum += win[7] * fifo[i + 992];
				sum += win[8] * fifo[i];
				sum += win[9] * fifo[i + 96];
				sum += win[10] * fifo[i + 128];
				sum += win[11] * fifo[i + 224];
				sum += win[12] * fifo[i + 256];
				sum += win[13] * fifo[i + 352];
				sum += win[14] * fifo[i + 384];
				sum += win[15] * fifo[i + 480];
				pcmi = sum > 32767 ? 32767 : (sum < -32768 ? -32768 : (int)sum);
				buf[off] = (byte)pcmi;
				buf[off + 1] = (byte)(pcmi >>> 8);
			}
			break;
		case 576:
			//u_vector={576,672,704,800,832,928,960,32,64,160,192,288,320,416,448,544}
			for(i = 0; i < 32; i++, off += step) {
				win = dewin[i];
				sum = win[0] * fifo[i + 576];
				sum += win[1] * fifo[i + 672];
				sum += win[2] * fifo[i + 704];
				sum += win[3] * fifo[i + 800];
				sum += win[4] * fifo[i + 832];
				sum += win[5] * fifo[i + 928];
				sum += win[6] * fifo[i + 960];
				sum += win[7] * fifo[i + 32];
				sum += win[8] * fifo[i + 64];
				sum += win[9] * fifo[i + 160];
				sum += win[10] * fifo[i + 192];
				sum += win[11] * fifo[i + 288];
				sum += win[12] * fifo[i + 320];
				sum += win[13] * fifo[i + 416];
				sum += win[14] * fifo[i + 448];
				sum += win[15] * fifo[i + 544];
				pcmi = sum > 32767 ? 32767 : (sum < -32768 ? -32768 : (int)sum);
				buf[off] = (byte)pcmi;
				buf[off + 1] = (byte)(pcmi >>> 8);
			}
			break;
		case 640:
			//u_vector={640,736,768,864,896,992,0,96,128,224,256,352,384,480,512,608}
			for(i = 0; i < 32; i++, off += step) {
				win = dewin[i];
				sum = win[0] * fifo[i + 640];
				sum += win[1] * fifo[i + 736];
				sum += win[2] * fifo[i + 768];
				sum += win[3] * fifo[i + 864];
				sum += win[4] * fifo[i + 896];
				sum += win[5] * fifo[i + 992];
				sum += win[6] * fifo[i];
				sum += win[7] * fifo[i + 96];
				sum += win[8] * fifo[i + 128];
				sum += win[9] * fifo[i + 224];
				sum += win[10] * fifo[i + 256];
				sum += win[11] * fifo[i + 352];
				sum += win[12] * fifo[i + 384];
				sum += win[13] * fifo[i + 480];
				sum += win[14] * fifo[i + 512];
				sum += win[15] * fifo[i + 608];
				pcmi = sum > 32767 ? 32767 : (sum < -32768 ? -32768 : (int)sum);
				buf[off] = (byte)pcmi;
				buf[off + 1] = (byte)(pcmi >>> 8);
			}
			break;
		case 704:
			//u_vector={704,800,832,928,960,32,64,160,192,288,320,416,448,544,576,672}
			for(i = 0; i < 32; i++, off += step) {
				win = dewin[i];
				sum = win[0] * fifo[i + 704];
				sum += win[1] * fifo[i + 800];
				sum += win[2] * fifo[i + 832];
				sum += win[3] * fifo[i + 928];
				sum += win[4] * fifo[i + 960];
				sum += win[5] * fifo[i + 32];
				sum += win[6] * fifo[i + 64];
				sum += win[7] * fifo[i + 160];
				sum += win[8] * fifo[i + 192];
				sum += win[9] * fifo[i + 288];
				sum += win[10] * fifo[i + 320];
				sum += win[11] * fifo[i + 416];
				sum += win[12] * fifo[i + 448];
				sum += win[13] * fifo[i + 544];
				sum += win[14] * fifo[i + 576];
				sum += win[15] * fifo[i + 672];
				pcmi = sum > 32767 ? 32767 : (sum < -32768 ? -32768 : (int)sum);
				buf[off] = (byte)pcmi;
				buf[off + 1] = (byte)(pcmi >>> 8);
			}
			break;
		case 768:
			//u_vector={768,864,896,992,0,96,128,224,256,352,384,480,512,608,640,736}
			for(i = 0; i < 32; i++, off += step) {
				win = dewin[i];
				sum = win[0] * fifo[i + 768];
				sum += win[1] * fifo[i + 864];
				sum += win[2] * fifo[i + 896];
				sum += win[3] * fifo[i + 992];
				sum += win[4] * fifo[i];
				sum += win[5] * fifo[i + 96];
				sum += win[6] * fifo[i + 128];
				sum += win[7] * fifo[i + 224];
				sum += win[8] * fifo[i + 256];
				sum += win[9] * fifo[i + 352];
				sum += win[10] * fifo[i + 384];
				sum += win[11] * fifo[i + 480];
				sum += win[12] * fifo[i + 512];
				sum += win[13] * fifo[i + 608];
				sum += win[14] * fifo[i + 640];
				sum += win[15] * fifo[i + 736];
				pcmi = sum > 32767 ? 32767 : (sum < -32768 ? -32768 : (int)sum);
				buf[off] = (byte)pcmi;
				buf[off + 1] = (byte)(pcmi >>> 8);
			}
			break;
		case 832:
			//u_vector={832,928,960,32,64,160,192,288,320,416,448,544,576,672,704,800}
			for(i = 0; i < 32; i++, off += step) {
				win = dewin[i];
				sum = win[0] * fifo[i + 832];
				sum += win[1] * fifo[i + 928];
				sum += win[2] * fifo[i + 960];
				sum += win[3] * fifo[i + 32];
				sum += win[4] * fifo[i + 64];
				sum += win[5] * fifo[i + 160];
				sum += win[6] * fifo[i + 192];
				sum += win[7] * fifo[i + 288];
				sum += win[8] * fifo[i + 320];
				sum += win[9] * fifo[i + 416];
				sum += win[10] * fifo[i + 448];
				sum += win[11] * fifo[i + 544];
				sum += win[12] * fifo[i + 576];
				sum += win[13] * fifo[i + 672];
				sum += win[14] * fifo[i + 704];
				sum += win[15] * fifo[i + 800];
				pcmi = sum > 32767 ? 32767 : (sum < -32768 ? -32768 : (int)sum);
				buf[off] = (byte)pcmi;
				buf[off + 1] = (byte)(pcmi >>> 8);
			}
			break;
		case 896:
			//u_vector={896,992,0,96,128,224,256,352,384,480,512,608,640,736,768,864}
			for(i = 0; i < 32; i++, off += step) {
				win = dewin[i];
				sum = win[0] * fifo[i + 896];
				sum += win[1] * fifo[i + 992];
				sum += win[2] * fifo[i];
				sum += win[3] * fifo[i + 96];
				sum += win[4] * fifo[i + 128];
				sum += win[5] * fifo[i + 224];
				sum += win[6] * fifo[i + 256];
				sum += win[7] * fifo[i + 352];
				sum += win[8] * fifo[i + 384];
				sum += win[9] * fifo[i + 480];
				sum += win[10] * fifo[i + 512];
				sum += win[11] * fifo[i + 608];
				sum += win[12] * fifo[i + 640];
				sum += win[13] * fifo[i + 736];
				sum += win[14] * fifo[i + 768];
				sum += win[15] * fifo[i + 864];
				pcmi = sum > 32767 ? 32767 : (sum < -32768 ? -32768 : (int)sum);
				buf[off] = (byte)pcmi;
				buf[off + 1] = (byte)(pcmi >>> 8);
			}
			break;
		case 960:
			//u_vector={960,32,64,160,192,288,320,416,448,544,576,672,704,800,832,928}
			for(i = 0; i < 32; i++, off += step) {
				win = dewin[i];
				sum = win[0] * fifo[i + 960];
				sum += win[1] * fifo[i + 32];
				sum += win[2] * fifo[i + 64];
				sum += win[3] * fifo[i + 160];
				sum += win[4] * fifo[i + 192];
				sum += win[5] * fifo[i + 288];
				sum += win[6] * fifo[i + 320];
				sum += win[7] * fifo[i + 416];
				sum += win[8] * fifo[i + 448];
				sum += win[9] * fifo[i + 544];
				sum += win[10] * fifo[i + 576];
				sum += win[11] * fifo[i + 672];
				sum += win[12] * fifo[i + 704];
				sum += win[13] * fifo[i + 800];
				sum += win[14] * fifo[i + 832];
				sum += win[15] * fifo[i + 928];
				pcmi = sum > 32767 ? 32767 : (sum < -32768 ? -32768 : (int)sum);
				buf[off] = (byte)pcmi;
				buf[off + 1] = (byte)(pcmi >>> 8);
			}
			break;
		}
		return off;
	}

	/**
	 * 一个子带的矩阵运算。
	 * @param src 输入的32个样本值。
	 * @param dest 暂存输出值的长度为1024个元素的FIFO队列。
	 * @param off FIFO队列的偏移量。一个子带一次矩阵运算输出64个值连续存储到FIFO队列，存储的起始位置由off指定。
	 */
	private void dct32to64(float[] src, float[] dest, int off) {
		final float[] in = src, out = dest;
		final int i = off;
		float in0,in1,in2,in3,in4,in5,in6,in7,in8,in9,in10,in11,in12,in13,in14,in15;
		float out0,out1,out2,out3,out4,out5,out6,out7,out8,out9,out10,out11,out12,out13,out14,out15;
		float d8_0,d8_1,d8_2,d8_3,d8_4,d8_5,d8_6,d8_7;
		float ein0, ein1, oin0, oin1;

		//>>>>>>>>>>>>>>>>
		// 用DCT16计算DCT32输出[0..31]的偶数下标元素
		in0 =  in[0]  + in[31];
		in1 =  in[1]  + in[30];
		in2 =  in[2]  + in[29];
		in3 =  in[3]  + in[28];
		in4 =  in[4]  + in[27];
		in5 =  in[5]  + in[26];
		in6 =  in[6]  + in[25];
		in7 =  in[7]  + in[24];
		in8 =  in[8]  + in[23];
		in9 =  in[9]  + in[22];
		in10 = in[10] + in[21];
		in11 = in[11] + in[20];
		in12 = in[12] + in[19];
		in13 = in[13] + in[18];
		in14 = in[14] + in[17];
		in15 = in[15] + in[16];

		//DCT16
		//{
			//>>>>>>>> 用DCT8计算DCT16输出[0..15]的偶数下标元素
			d8_0 = in0 + in15;
			d8_1 = in1 + in14;
			d8_2 = in2 + in13;
			d8_3 = in3 + in12;
			d8_4 = in4 + in11;
			d8_5 = in5 + in10;
			d8_6 = in6 + in9;
			d8_7 = in7 + in8;

			//DCT8. 加(减)法29,乘法12次
			//{
				//>>>>e 用DCT4计算DCT8的输出[0..7]的偶数下标元素
				out1 = d8_0 + d8_7;
				out3 = d8_1 + d8_6;
				out5 = d8_2 + d8_5;
				out7 = d8_3 + d8_4;

				//>>e DCT2
				ein0 = out1 + out7;
				ein1 = out3 + out5;
				out[i + 48] =  -ein0 - ein1;
				out[i] = (ein0 - ein1) * 0.7071068f;// 0.5/cos(PI/4)

				//>>o DCT2
				oin0 = (out1 - out7) * 0.5411961f;	// 0.5/cos( PI/8)
				oin1 = (out3 - out5) * 1.3065630f;	// 0.5/cos(3PI/8)

				out2 =  oin0 + oin1;
				out12 = (oin0 - oin1) * 0.7071068f; // cos(PI/4)

				out[i + 40] = out[i + 56] = -out2 - out12;
				out[i + 8] = out12;
				//<<<<e 完成计算DCT8的输出[0..7]的偶数下标元素

				//>>>>o 用DCT4计算DCT8的输出[0..7]的奇数下标元素
				//o DCT4 part1
				out1 = (d8_0 - d8_7) * 0.5097956f;	// 0.5/cos( PI/16)
				out3 = (d8_1 - d8_6) * 0.6013449f;	// 0.5/cos(3PI/16)
				out5 = (d8_2 - d8_5) * 0.8999762f;	// 0.5/cos(5PI/16)
				out7 = (d8_3 - d8_4) * 2.5629154f;	// 0.5/cos(7PI/16)

				//o DCT4 part2

				//e DCT2 part1
				ein0 = out1 + out7;
				ein1 = out3 + out5;

				//o DCT2 part1
				oin0 = (out1 - out7) * 0.5411961f;	// 0.5/cos(PI/8)
				oin1 = (out3 - out5) * 1.3065630f;	// 0.5/cos(3PI/8)

				//e DCT2 part2
				out1 =  ein0 + ein1;
				out5 = (ein0 - ein1) * 0.7071068f;	// cos(PI/4)

				//o DCT2 part2
				out3 = oin0 + oin1;
				out7 = (oin0 - oin1) * 0.7071068f;	// cos(PI/4)
				out3 += out7;

				//o DCT4 part3
				out[i + 44] = out[i + 52] = -out1 - out3;	//out1+=out3
				out[i + 36] = out[i + 60] = -out3 - out5;	//out3+=out5
				out[i + 4] = out5 + out7;					//out5+=out7
				out[i + 12] = out7;
				//<<<<o 完成计算DCT8的输出[0..7]的奇数下标元素
			//}
			//<<<<<<<< 完成计算DCT16输出[0..15]的偶数下标元素

			//-----------------------------------------------------------------

			//>>>>>>>> 用DCT8计算DCT16输出[0..15]的奇数下标元素
			d8_0 = (in0 - in15) * 0.5024193f;	// 0.5/cos( 1 * PI/32)
			d8_1 = (in1 - in14) * 0.5224986f;	// 0.5/cos( 3 * PI/32)
			d8_2 = (in2 - in13) * 0.5669440f;	// 0.5/cos( 5 * PI/32)
			d8_3 = (in3 - in12) * 0.6468218f;	// 0.5/cos( 7 * PI/32)
			d8_4 = (in4 - in11) * 0.7881546f;	// 0.5/cos( 9 * PI/32)
			d8_5 = (in5 - in10) * 1.0606777f;	// 0.5/cos(11 * PI/32)
			d8_6 = (in6 - in9) * 1.7224471f;	// 0.5/cos(13 * PI/32)
			d8_7 = (in7 - in8) * 5.1011486f;	// 0.5/cos(15 * PI/32)

			//DCT8
			//{
				//>>>>e 用DCT4计算DCT8的输出[0..7]的偶数下标元素.
				out3  = d8_0 + d8_7;
				out7  = d8_1 + d8_6;
				out11 = d8_2 + d8_5;
				out15 = d8_3 + d8_4;

				//>>e DCT2
				ein0 = out3 + out15;
				ein1 = out7 + out11;
				out1 = ein0 + ein1;
				out9 = (ein0 - ein1) * 0.7071068f;		// 0.5/cos(PI/4)

				//>>o DCT2
				oin0 = (out3 - out15) * 0.5411961f;	// 0.5/cos( PI/8)
				oin1 = (out7 - out11) * 1.3065630f;	// 0.5/cos(3PI/8)

				out5 =  oin0 + oin1;
				out13 = (oin0 - oin1) * 0.7071068f;	// cos(PI/4)

				out5 += out13;
				//<<<<e 完成计算DCT8的输出[0..7]的偶数下标元素

				//>>>>o 用DCT4计算DCT8的输出[0..7]的奇数下标元素
				//o DCT4 part1
				out3  = (d8_0 - d8_7) * 0.5097956f;	// 0.5/cos( PI/16)
				out7  = (d8_1 - d8_6) * 0.6013449f;	// 0.5/cos(3PI/16)
				out11 = (d8_2 - d8_5) * 0.8999762f;	// 0.5/cos(5PI/16)
				out15 = (d8_3 - d8_4) * 2.5629154f;	// 0.5/cos(7PI/16)

				//o DCT4 part2

				//e DCT2 part1
				ein0 = out3 + out15;
				ein1 = out7 + out11;

				//o DCT2 part1
				oin0 = (out3 - out15) * 0.5411961f;	// 0.5/cos(PI/8)
				oin1 = (out7 - out11) * 1.3065630f;	// 0.5/cos(3PI/8)

				//e DCT2 part2
				out3 =  ein0 + ein1;
				out11 = (ein0 - ein1) * 0.7071068f;	// cos(PI/4)

				//o DCT2 part2
				out7 = oin0 + oin1;
				out15 = (oin0 - oin1) * 0.7071068f;	// cos(PI/4)
				out7 += out15;

				//o DCT4 part3
				out3  += out7;
				out7  += out11;
				out11 += out15;
				//<<<<o 完成计算DCT8的输出[0..7]的奇数下标元素
			//}

			out[i + 46] = out[i + 50] = -out1 - out3;	//out1 += out3
			out[i + 42] = out[i + 54] = -out3 - out5;	//out3 += out5
			out[i + 38] = out[i + 58] = -out5 - out7;	//out5 += out7
			out[i + 34] = out[i + 62] = -out7 - out9;	//out7 += out9
			out[i + 2]  = out9 + out11;					//out9 += out11
			out[i + 6]  = out11 + out13;				//out11 += out13
			out[i + 10] = out13 + out15;				//out13 += out15
			//<<<<<<<< 完成计算DCT16输出[0..15]的奇数下标元素
		//}
		out[i + 14] = out15;	//out[i + 14]=out32[30]
		//<<<<<<<<<<<<<<<<
		// 完成计算DCT32输出[0..31]的偶数下标元素

		//=====================================================================

		//>>>>>>>>>>>>>>>>
		// 用DCT16计算DCT32输出[0..31]的奇数下标元素
		in0  = (in[0]  - in[31]) * 0.5006030f;	// 0.5/cos( 1 * PI/64)
		in1  = (in[1]  - in[30]) * 0.5054710f;	// 0.5/cos( 3 * PI/64)
		in2  = (in[2]  - in[29]) * 0.5154473f;	// 0.5/cos( 5 * PI/64)
		in3  = (in[3]  - in[28]) * 0.5310426f;	// 0.5/cos( 7 * PI/64)
		in4  = (in[4]  - in[27]) * 0.5531039f;	// 0.5/cos( 9 * PI/64)
		in5  = (in[5]  - in[26]) * 0.5829350f;	// 0.5/cos(11 * PI/64)
		in6  = (in[6]  - in[25]) * 0.6225041f;	// 0.5/cos(13 * PI/64)
		in7  = (in[7]  - in[24]) * 0.6748083f;	// 0.5/cos(15 * PI/64)
		in8  = (in[8]  - in[23]) * 0.7445362f;	// 0.5/cos(17 * PI/64)
		in9  = (in[9]  - in[22]) * 0.8393496f;	// 0.5/cos(19 * PI/64)
		in10 = (in[10] - in[21]) * 0.9725682f;	// 0.5/cos(21 * PI/64)
		in11 = (in[11] - in[20]) * 1.1694399f;	// 0.5/cos(23 * PI/64)
		in12 = (in[12] - in[19]) * 1.4841646f;	// 0.5/cos(25 * PI/64)
		in13 = (in[13] - in[18]) * 2.0577810f;	// 0.5/cos(27 * PI/64)
		in14 = (in[14] - in[17]) * 3.4076084f;	// 0.5/cos(29 * PI/64)
		in15 = (in[15] - in[16]) * 10.190008f;	// 0.5/cos(31 * PI/64)

		//DCT16
		//{
			//>>>>>>>> 用DCT8计算DCT16输出[0..15]的偶数下标元素
			d8_0 = in0 + in15;
			d8_1 = in1 + in14;
			d8_2 = in2 + in13;
			d8_3 = in3 + in12;
			d8_4 = in4 + in11;
			d8_5 = in5 + in10;
			d8_6 = in6 + in9;
			d8_7 = in7 + in8;

			//DCT8
			//{
				//>>>>e 用DCT4计算DCT8的输出[0..7]的偶数下标元素
				out1 = d8_0 + d8_7;
				out3 = d8_1 + d8_6;
				out5 = d8_2 + d8_5;
				out7 = d8_3 + d8_4;

				//>>e DCT2
				ein0 = out1 + out7;
				ein1 = out3 + out5;
				out0 = ein0 + ein1;
				out8 = (ein0 - ein1) * 0.7071068f;	// 0.5/cos(PI/4)

				//>>o DCT2
				oin0 = (out1 - out7) * 0.5411961f;	// 0.5/cos( PI/8)
				oin1 = (out3 - out5) * 1.3065630f;	// 0.5/cos(3PI/8)

				out4 =  oin0 + oin1;
				out12 = (oin0 - oin1) * 0.7071068f;// cos(PI/4)

				out4 += out12;
				//<<<<e 完成计算DCT8的输出[0..7]的偶数下标元素

				//>>>>o 用DCT4计算DCT8的输出[0..7]的奇数下标元素
				//o DCT4 part1
				out1 = (d8_0 - d8_7) * 0.5097956f;	// 0.5/cos( PI/16)
				out3 = (d8_1 - d8_6) * 0.6013449f;	// 0.5/cos(3PI/16)
				out5 = (d8_2 - d8_5) * 0.8999762f;	// 0.5/cos(5PI/16)
				out7 = (d8_3 - d8_4) * 2.5629154f;	// 0.5/cos(7PI/16)

				//o DCT4 part2

				//e DCT2 part1
				ein0 = out1 + out7;
				ein1 = out3 + out5;

				//o DCT2 part1
				oin0 = (out1 - out7) * 0.5411961f;	// 0.5/cos(PI/8)
				oin1 = (out3 - out5) * 1.3065630f;	// 0.5/cos(3PI/8)

				//e DCT2 part2
				out2 = ein0 + ein1;
				out10 = (ein0 - ein1) * 0.7071068f;// cos(PI/4)

				//o DCT2 part2
				out6 = oin0 + oin1;
				out14 = (oin0 - oin1) * 0.7071068f;
				out6 += out14;

				//o DCT4 part3
				out2  += out6;
				out6  += out10;
				out10 += out14;
				//<<<<o 完成计算DCT8的输出[0..7]的奇数下标元素
			//}
			//<<<<<<<< 完成计算DCT16输出[0..15]的偶数下标元素

			//-----------------------------------------------------------------

			//>>>>>>>> 用DCT8计算DCT16输出[0..15]的奇数下标元素
			d8_0 = (in0 - in15) * 0.5024193f;	// 0.5/cos( 1 * PI/32)
			d8_1 = (in1 - in14) * 0.5224986f;	// 0.5/cos( 3 * PI/32)
			d8_2 = (in2 - in13) * 0.5669440f;	// 0.5/cos( 5 * PI/32)
			d8_3 = (in3 - in12) * 0.6468218f;	// 0.5/cos( 7 * PI/32)
			d8_4 = (in4 - in11) * 0.7881546f;	// 0.5/cos( 9 * PI/32)
			d8_5 = (in5 - in10) * 1.0606777f;	// 0.5/cos(11 * PI/32)
			d8_6 = (in6 - in9) * 1.7224471f;	// 0.5/cos(13 * PI/32)
			d8_7 = (in7 - in8) * 5.1011486f;	// 0.5/cos(15 * PI/32)

			//DCT8
			//{
				//>>>>e 用DCT4计算DCT8的输出[0..7]的偶数下标元素.
				out1 = d8_0 + d8_7;
				out3 = d8_1 + d8_6;
				out5 = d8_2 + d8_5;
				out7 = d8_3 + d8_4;

				//>>e DCT2
				ein0 = out1 + out7;
				ein1 = out3 + out5;
				in0 =  ein0 + ein1;	//out0->in0,out4->in4
				in4 = (ein0 - ein1) * 0.7071068f;	// 0.5/cos(PI/4)

				//>>o DCT2
				oin0 = (out1 - out7) * 0.5411961f;	// 0.5/cos( PI/8)
				oin1 = (out3 - out5) * 1.3065630f;	// 0.5/cos(3PI/8)

				in2 =  oin0 + oin1;					//out2->in2,out6->in6
				in6 = (oin0 - oin1) * 0.7071068f;	// cos(PI/4)

				in2 += in6;
				//<<<<e 完成计算DCT8的输出[0..7]的偶数下标元素

				//>>>>o 用DCT4计算DCT8的输出[0..7]的奇数下标元素
				//o DCT4 part1
				out1 = (d8_0 - d8_7) * 0.5097956f;	// 0.5/cos( PI/16)
				out3 = (d8_1 - d8_6) * 0.6013449f;	// 0.5/cos(3PI/16)
				out5 = (d8_2 - d8_5) * 0.8999762f;	// 0.5/cos(5PI/16)
				out7 = (d8_3 - d8_4) * 2.5629154f;	// 0.5/cos(7PI/16)

				//o DCT4 part2

				//e DCT2 part1
				ein0 = out1 + out7;
				ein1 = out3 + out5;

				//o DCT2 part1
				oin0 = (out1 - out7) * 0.5411961f;	// 0.5/cos(PI/8)
				oin1 = (out3 - out5) * 1.3065630f;	// 0.5/cos(3PI/8)

				//e DCT2 part2
				out1 =  ein0 + ein1;
				out5 = (ein0 - ein1) * 0.7071068f;	// cos(PI/4)

				//o DCT2 part2
				out3 = oin0 + oin1;
				out15 = (oin0 - oin1) * 0.7071068f;
				out3 += out15;

				//o DCT4 part3
				out1 += out3;
				out3 += out5;
				out5 += out15;
				//<<<<o 完成计算DCT8的输出[0..7]的奇数下标元素
			//}
									//out15=out7
			out13 = in6 + out15;	//out13=out6+out7
			out11 = out5 + in6;		//out11=out5+out6
			out9 = in4 + out5;		//out9 =out4+out5
			out7 = out3 + in4;		//out7 =out3+out4
			out5 = in2 + out3;		//out5 =out2+out3
			out3 = out1 + in2;		//out3 =out1+out2
			out1 += in0;			//out1 =out0+out1
			//<<<<<<<< 完成计算DCT16输出[0..15]的奇数下标元素
		//}

		//DCT32out[i]=out[i]+out[i+1]; DCT32out[31]=out[15]
		out[i + 47] = out[i + 49] = -out0 - out1;
		out[i + 45] = out[i + 51] = -out1 - out2;
		out[i + 43] = out[i + 53] = -out2 - out3;
		out[i + 41] = out[i + 55] = -out3 - out4;
		out[i + 39] = out[i + 57] = -out4 - out5;
		out[i + 37] = out[i + 59] = -out5 - out6;
		out[i + 35] = out[i + 61] = -out6 - out7;
		out[i + 33] = out[i + 63] = -out7 - out8;
		out[i + 1] = out8 + out9;
		out[i + 3] = out9 + out10;
		out[i + 5] = out10 + out11;
		out[i + 7] = out11 + out12;
		out[i + 9] = out12 + out13;
		out[i + 11] = out13 + out14;
		out[i + 13] = out14 + out15;
		out[i + 15] = out15;
		//<<<<<<<<<<<<<<<<

		out[i + 16] = 0;

		out[i + 17] = -out15;	//out[i + 17] = -out[i + 15]
		out[i + 18] = -out[i + 14];
		out[i + 19] = -out[i + 13];
		out[i + 20] = -out[i + 12];
		out[i + 21] = -out[i + 11];
		out[i + 22] = -out[i + 10];
		out[i + 23] = -out[i + 9];
		out[i + 24] = -out[i + 8];
		out[i + 25] = -out[i + 7];
		out[i + 26] = -out[i + 6];
		out[i + 27] = -out[i + 5];
		out[i + 28] = -out[i + 4];
		out[i + 29] = -out[i + 3];
		out[i + 30] = -out[i + 2];
		out[i + 31] = -out[i + 1];
		out[i + 32] = -out[i];
	}

	/*
	 * dewin: D[i] * 32767 (i=0..511), 然后重新排序
	 * D[]: Coefficients Di of the synthesis window. ISO/IEC 11172-3 ANNEX_B Table 3-B.3
	 */
	private final float[][] dewin = { // [32][16]
		{0f,-14.5f,106.5f,-229.5f,1018.5f,-2576.5f,3287f,-18744.5f,
		37519f,18744.5f,3287f,2576.5f,1018.5f,229.5f,106.5f,14.5f},
		{-0.5f,-15.5f,109f,-259.5f,1000f,-2758.5f,2979.5f,-19668f,
		37496f,17820f,3567f,2394f,1031.5f,200.5f,104f,13f},
		{-0.5f,-17.5f,111f,-290.5f,976f,-2939.5f,2644f,-20588f,
		37428f,16895.5f,3820f,2212.5f,1040f,173.5f,101f,12f},
		{-0.5f,-19f,112.5f,-322.5f,946.5f,-3118.5f,2280.5f,-21503f,
		37315f,15973.5f,4046f,2031.5f,1043.5f,147f,98f,10.5f},
		{-0.5f,-20.5f,113.5f,-355.5f,911f,-3294.5f,1888f,-22410.5f,
		37156.5f,15056f,4246f,1852.5f,1042.5f,122f,95f,9.5f},
		{-0.5f,-22.5f,114f,-389.5f,869.5f,-3467.5f,1467.5f,-23308.5f,
		36954f,14144.5f,4420f,1675.5f,1037.5f,98.5f,91.5f,8.5f},
		{-0.5f,-24.5f,114f,-424f,822f,-3635.5f,1018.5f,-24195f,
		36707.5f,13241f,4569.5f,1502f,1028.5f,76.5f,88f,8f},
		{-1f,-26.5f,113.5f,-459.5f,767.5f,-3798.5f,541f,-25068.5f,
		36417.5f,12347f,4694.5f,1331.5f,1016f,55.5f,84.5f,7f},
		{-1f,-29f,112f,-495.5f,707f,-3955f,35f,-25926.5f,
		36084.5f,11464.5f,4796f,1165f,1000.5f,36f,80.5f,6.5f},
		{-1f,-31.5f,110.5f,-532f,640f,-4104.5f,-499f,-26767f,
		35710f,10594.5f,4875f,1003f,981f,18f,77f,5.5f},
		{-1f,-34f,107.5f,-568.5f,565.5f,-4245.5f,-1061f,-27589f,
		35295f,9739f,4931.5f,846f,959.5f,1f,73.5f,5f},
		{-1.5f,-36.5f,104f,-605f,485f,-4377.5f,-1650f,-28389f,
		34839.5f,8899.5f,4967.5f,694f,935f,-14.5f,69.5f,4.5f},
		{-1.5f,-39.5f,100f,-641.5f,397f,-4499f,-2266.5f,-29166.5f,
		34346f,8077.5f,4983f,547.5f,908.5f,-28.5f,66f,4f},
		{-2f,-42.5f,94.5f,-678f,302.5f,-4609.5f,-2909f,-29919f,
		33814.5f,7274f,4979.5f,407f,879.5f,-41.5f,62.5f,3.5f},
		{-2f,-45.5f,88.5f,-714f,201f,-4708f,-3577f,-30644.5f,
		33247f,6490f,4958f,272.5f,849f,-53f,58.5f,3.5f},
		{-2.5f,-48.5f,81.5f,-749f,92.5f,-4792.5f,-4270f,-31342f,
		32645f,5727.5f,4919f,144f,817f,-63.5f,55.5f,3f},
		{-2.5f,-52f,73f,-783.5f,-22.5f,-4863.5f,-4987.5f,-32009.5f,
		32009.5f,4987.5f,4863.5f,22.5f,783.5f,-73f,52f,2.5f},
		{-3f,-55.5f,63.5f,-817f,-144f,-4919f,-5727.5f,-32645f,
		31342f,4270f,4792.5f,-92.5f,749f,-81.5f,48.5f,2.5f},
		{-3.5f,-58.5f,53f,-849f,-272.5f,-4958f,-6490f,-33247f,
		30644.5f,3577f,4708f,-201f,714f,-88.5f,45.5f,2f},
		{-3.5f,-62.5f,41.5f,-879.5f,-407f,-4979.5f,-7274f,-33814.5f,
		29919f,2909f,4609.5f,-302.5f,678f,-94.5f,42.5f,2f},
		{-4f,-66f,28.5f,-908.5f,-547.5f,-4983f,-8077.5f,-34346f,
		29166.5f,2266.5f,4499f,-397f,641.5f,-100f,39.5f,1.5f},
		{-4.5f,-69.5f,14.5f,-935f,-694f,-4967.5f,-8899.5f,-34839.5f,
		28389f,1650f,4377.5f,-485f,605f,-104f,36.5f,1.5f},
		{-5f,-73.5f,-1f,-959.5f,-846f,-4931.5f,-9739f,-35295f,
		27589f,1061f,4245.5f,-565.5f,568.5f,-107.5f,34f,1f},
		{-5.5f,-77f,-18f,-981f,-1003f,-4875f,-10594.5f,-35710f,
		26767f,499f,4104.5f,-640f,532f,-110.5f,31.5f,1f},
		{-6.5f,-80.5f,-36f,-1000.5f,-1165f,-4796f,-11464.5f,-36084.5f,
		25926.5f,-35f,3955f,-707f,495.5f,-112f,29f,1f},
		{-7f,-84.5f,-55.5f,-1016f,-1331.5f,-4694.5f,-12347f,-36417.5f,
		25068.5f,-541f,3798.5f,-767.5f,459.5f,-113.5f,26.5f,1f},
		{-8f,-88f,-76.5f,-1028.5f,-1502f,-4569.5f,-13241f,-36707.5f,
		24195f,-1018.5f,3635.5f,-822f,424f,-114f,24.5f,0.5f},
		{-8.5f,-91.5f,-98.5f,-1037.5f,-1675.5f,-4420f,-14144.5f,-36954f,
		23308.5f,-1467.5f,3467.5f,-869.5f,389.5f,-114f,22.5f,0.5f},
		{-9.5f,-95f,-122f,-1042.5f,-1852.5f,-4246f,-15056f,-37156.5f,
		22410.5f,-1888f,3294.5f,-911f,355.5f,-113.5f,20.5f,0.5f},
		{-10.5f,-98f,-147f,-1043.5f,-2031.5f,-4046f,-15973.5f,-37315f,
		21503f,-2280.5f,3118.5f,-946.5f,322.5f,-112.5f,19f,0.5f},
		{-12f,-101f,-173.5f,-1040f,-2212.5f,-3820f,-16895.5f,-37428f,
		20588f,-2644f,2939.5f,-976f,290.5f,-111f,17.5f,0.5f},
		{-13f,-104f,-200.5f,-1031.5f,-2394f,-3567f,-17820f,-37496f,
		19668f,-2979.5f,2758.5f,-1000f,259.5f,-109f,15.5f,0.5f}
	};
}
