/*
* Layer3.java -- MPEG-1/MPEG-2 Audio Layer III (MP3) 解码
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
 * 解码Layer Ⅲ。
 * @version 0.400
 */
public final class Layer3 extends AbstractLayer {
	private int ngr;		// Number of granules
	private int nch;		// Number of channels
	private Header header;
	private BitStream bsSI;	// 读帧边信息(Side Information)位流
	private BitStreamMainData maindataStream;
	private int main_data_begin;
	private int[] scfsi; // [nch],scale-factor selector information
	private ChannelInformation[] infoCh0; // [ngr]
	private ChannelInformation[] infoCh1; // [ngr]
	private int[] sfbIndexLong;
	private int[] sfbIndexShort;
	private boolean isMPEG1;

	private boolean coresEnabled;
	private int pcmbufCount;
	private byte[] outputLock;
	private byte[] pcmbufLock;
	private AudioDataConcurrent audiodataCh0, audiodataCh1;
	private int nadc;

	/**
	 * 创建一个 Layer Ⅲ 帧解码器，默认启用cpu多核心解码优化。
	 * @param h 已经解码帧头信息的帧头对象。
	 * @param audio 音频输出对象。
	 * @see #Layer3(Header, IAudio, boolean)
	 */
	public Layer3(Header h, IAudio audio) {
		this(h, audio, true);
	}

	/**
	 * 创建一个 Layer Ⅲ 帧解码器。
	 * <br>如果本程序运行在多核心CPU硬件平台上，建议启用多核心CPU解码，这样能够大幅度提高解码速度。
	 * <br>如果本程序运行在单核心CPU硬件平台上，建议禁用多核心CPU解码，否则会使解码速度略有下降。
	 * <p>如果用 {@link #Layer3(Header, IAudio)} 创建Layer3对象，缺省情况下启用了cpu多核心优化。
	 * @param h 已经解码帧头信息的帧头对象。
	 * @param audio 音频输出对象。
	 * @param coresEnabled 若为<b>true</b>启用多核心CPU解码优化，若为<b>false</b>禁用多核心CPU解码优化。
	 */
	public Layer3(Header h, IAudio audio, boolean coresEnabled) {
		super(h, audio);
		header = h;
		this.coresEnabled = coresEnabled;

		isMPEG1 = header.getVersion() == Header.MPEG1 ? true : false;
		ngr = isMPEG1 ? 2 : 1;
		nch = header.getChannels();
		bsSI = new BitStream(0, 0);
		maindataStream = new BitStreamMainData(4096, 512);
		scfsi = new int[nch];
		widthLong = new int[22];
		widthShort = new int[13];
		scalefacLong = new int[nch][23];
		scalefacShort = new int[nch][3 * 13];
		region = new int[4];
		hv = new int[32 * 18 + 4];
		rzeroBandShort = new int[3];
		samplesCh0 = new float[32];
		preBlckCh0 = new float[32 * 18];
		if (nch == 2) {
			preBlckCh1 = new float[32 * 18];
			samplesCh1 = new float[32];
		}

		if (coresEnabled) {
			nadc = nch;
			pcmbufCount = 15;
			outputLock = new byte[0];
			pcmbufLock = new byte[0];
			audiodataCh0 = new AudioDataConcurrent(0); //ch=0
			infoCh0 = audiodataCh0.getChannelInformation();
			if (nch == 2) {
				audiodataCh1 = new AudioDataConcurrent(1); //ch=1
				infoCh1 = audiodataCh1.getChannelInformation();
			}
		} else {
			infoCh0 = new ChannelInformation[ngr];
			for (int gr = 0; gr < ngr; gr++)
				infoCh0[gr] = new ChannelInformation();
			if (nch == 2) {
				infoCh1 = new ChannelInformation[ngr];
				for (int gr = 0; gr < ngr; gr++)
					infoCh1[gr] = new ChannelInformation();
			}
		}

		int i;

		// 用于查表求 v^(4/3)，v是经哈夫曼解码出的一个(正)值，该值的范围是0..8191
		floatPowIS = new float[8207];
		for (i = 0; i < 8207; i++)
			floatPowIS[i] = (float) Math.pow(i, 4.0 / 3.0);

		// 用于查表求 2^(-0.25 * i)
		// 按公式短块时i最大值: 210 - 0   + 8 * 7 + 4 * 15 + 2 = 328
		// 长块或短块时i最小值: 210 - 255 + 0     + 0      + 0 = -45
		// 查表法时下标范围为0..328+45.
		floatPow2 = new float[328 + 46];
		for (i = 0; i < 374; i++)
			floatPow2[i] = (float) Math.pow(2.0, -0.25 * (i - 45));

		//---------------------------------------------------------------------
		//待解码文件的不同特征用到不同的变量.初始化:
		//---------------------------------------------------------------------
		int sfreq = header.getSamplingFrequency();
		sfreq += isMPEG1 ? 0 : ((header.getVersion() == Header.MPEG2) ? 3 : 6);

		// ANNEX B,Table 3-B.8. Layer III scalefactor bands
		switch (sfreq) {
		case 0:
			// MPEG-1, sampling_frequency=0, 44.1kHz
			sfbIndexLong = new int[] { 0, 4, 8, 12, 16, 20, 24, 30, 36, 44,
					52, 62, 74, 90, 110, 134, 162, 196, 238, 288, 342, 418, 576 };
			sfbIndexShort = new int[] { 0, 4, 8, 12, 16, 22, 30, 40, 52, 66,
					84, 106, 136, 192 };
			break;
		case 1:
			// MPEG-1, sampling_frequency=1, 48kHz
			sfbIndexLong = new int[] { 0, 4, 8, 12, 16, 20, 24, 30, 36, 42,
					50, 60, 72, 88, 106, 128, 156, 190, 230, 276, 330, 384, 576 };
			sfbIndexShort = new int[] { 0, 4, 8, 12, 16, 22, 28, 38, 50, 64,
					80, 100, 126, 192 };
			break;
		case 2:
			// MPEG-1, sampling_frequency=2, 32kHz
			sfbIndexLong = new int[] { 0, 4, 8, 12, 16, 20, 24, 30, 36, 44,
					54, 66, 82, 102, 126, 156, 194, 240, 296, 364, 448, 550, 576 };
			sfbIndexShort = new int[] { 0, 4, 8, 12, 16, 22, 30, 42, 58, 78,
					104, 138, 180, 192 };
			break;

		case 3:
			// MPEG-2, sampling_frequency=0, 22.05kHz
			sfbIndexLong = new int[] { 0, 6, 12, 18, 24, 30, 36, 44, 54, 66,
					80, 96, 116, 140, 168, 200, 238, 284, 336, 396, 464, 522, 576 };
			sfbIndexShort = new int[] { 0, 4, 8, 12, 18, 24, 32, 42, 56, 74,
					100, 132, 174, 192 };
			break;
		case 4:
			// MPEG-2, sampling_frequency=1, 24kHz
			sfbIndexLong = new int[] { 0, 6, 12, 18, 24, 30, 36, 44, 54, 66,
					80, 96, 114, 136, 162, 194, 232, 278, 330, 394, 464, 540, 576 };
			sfbIndexShort = new int[] { 0, 4, 8, 12, 18, 26, 36, 48, 62, 80,
					104, 136, 180, 192 };
			break;
		case 5:
			// MPEG-2, sampling_frequency=2, 16kHz
			sfbIndexLong = new int[] { 0, 6, 12, 18, 24, 30, 36, 44, 54, 66,
					80, 96, 116, 140, 168, 200, 238, 284, 336, 396, 464, 522, 576 };
			sfbIndexShort = new int[] { 0, 4, 8, 12, 18, 26, 36, 48, 62, 80,
					104, 134, 174, 192 };
			break;
		case 6:
			// MPEG-2.5, sampling_frequency=0, 11.025kHz
			sfbIndexLong = new int[] { 0, 6, 12, 18, 24, 30, 36, 44, 54, 66,
					80, 96, 116, 140, 168, 200, 238, 284, 336, 396, 464, 522, 576 };
			sfbIndexShort = new int[] { 0, 4, 8, 12, 18, 26, 36, 48, 62, 80,
					104, 134, 174, 192 };
			break;
		case 7:
			// MPEG-2.5, sampling_frequency=1, 12kHz
			sfbIndexLong = new int[] { 0, 6, 12, 18, 24, 30, 36, 44, 54, 66,
					80, 96, 116, 140, 168, 200, 238, 284, 336, 396, 464, 522, 576 };
			sfbIndexShort = new int[] { 0, 4, 8, 12, 18, 26, 36, 48, 62, 80,
					104, 134, 174, 192 };
			break;
		case 8:
			// MPEG-2.5, sampling_frequency=2, 8kHz
			sfbIndexLong = new int[] { 0, 12, 24, 36, 48, 60, 72, 88, 108, 132,
					160, 192, 232, 280, 336, 400, 476, 566, 568, 570, 572, 574, 576 };
			sfbIndexShort = new int[] { 0, 8, 16, 24, 36, 52, 72, 96, 124,
					160, 162, 164, 166, 192 };
			break;
		}
		for (i = 0; i < 22; i++)
			widthLong[i] = sfbIndexLong[i + 1] - sfbIndexLong[i];
		for (i = 0; i < 13; i++)
			widthShort[i] = sfbIndexShort[i + 1] - sfbIndexShort[i];
		//-----------------------------------------------------------------
		if (isMPEG1) {
			// MPEG-1, intensity_stereo
			is_coef = new float[] { 0.0f, 0.211324865f, 0.366025404f, 0.5f,
					0.633974596f, 0.788675135f, 1.0f };
		} else {
			// MPEG-2, intensity_stereo
			is_coef_lsf = new float[][] {
					{ 0.840896415f, 0.707106781f, 0.594603558f, 0.5f, 0.420448208f,
						0.353553391f, 0.297301779f, 0.25f, 0.210224104f, 0.176776695f,
						0.148650889f, 0.125f, 0.105112052f,	0.088388348f, 0.074325445f },
					{ 0.707106781f, 0.5f, 0.353553391f, 0.25f, 0.176776695f, 0.125f,
						0.088388348f, 0.0625f, 0.044194174f, 0.03125f, 0.022097087f,
						0.015625f, 0.011048543f, 0.0078125f, 0.005524272f } };

			initLSF();
		}
	}

	@Override
	protected void initialize() {
		if (coresEnabled) {
			audiodataCh0.start();
			if (nch == 2)
				audiodataCh1.start();
		}
	}

	//1.
	//>>>>SIDE INFORMATION (part1)=============================================
	//private int part2_3_bits;//----debug

	private int getSideInfo(byte[] b, int off) {
		int ch, gr, sclen;
		ChannelInformation ci;
		//part2_3_bits = 0;
		bsSI.feed(b, off);

		if (isMPEG1) {
			sclen = 4;
			main_data_begin = bsSI.getBits9(9);
			if (nch == 1) {
				bsSI.skipBits(5);	//private_bits
				scfsi[0] = bsSI.getBits9(4);
			} else {
				bsSI.skipBits(3);	//private_bits
				scfsi[0] = bsSI.getBits9(4);
				scfsi[1] = bsSI.getBits9(4);
			}
		} else {
			sclen = 9;
			main_data_begin = bsSI.getBits9(8);
			bsSI.skipBits(nch);
		}

		for (gr = 0; gr < ngr; gr++) {
			for (ch = 0; ch < nch; ch++) {
				ci = (ch == 0) ? infoCh0[gr] : infoCh1[gr];
				ci.part2_3_length = bsSI.getBits17(12);
				//part2_3_bits += ci.part2_3_length;
				ci.big_values = bsSI.getBits9(9);
				ci.global_gain = bsSI.getBits9(8);
				ci.scalefac_compress = bsSI.getBits9(sclen);
				ci.window_switching_flag = bsSI.get1Bit();
				if (ci.window_switching_flag == 1) {
					ci.block_type = bsSI.getBits9(2);
					ci.mixed_block_flag = bsSI.get1Bit();
					ci.table_select[0] = bsSI.getBits9(5);
					ci.table_select[1] = bsSI.getBits9(5);
					ci.subblock_gain[0] = bsSI.getBits9(3);
					ci.subblock_gain[1] = bsSI.getBits9(3);
					ci.subblock_gain[2] = bsSI.getBits9(3);
					if (ci.block_type == 0)
						return -1;
					else if (ci.block_type == 2 && ci.mixed_block_flag == 0)
						ci.region0_count = 8;
					else
						ci.region0_count = 7;
					ci.region1_count = 20 - ci.region0_count;
				} else {
					ci.block_type = 0;
					ci.mixed_block_flag = 0;
					ci.table_select[0] = bsSI.getBits9(5);
					ci.table_select[1] = bsSI.getBits9(5);
					ci.table_select[2] = bsSI.getBits9(5);
					ci.region0_count = bsSI.getBits9(4);
					ci.region1_count = bsSI.getBits9(3);
				}
				if (isMPEG1)
					ci.preflag = bsSI.get1Bit();
				ci.scalefac_scale = bsSI.get1Bit();
				ci.count1table_select = bsSI.get1Bit();
			}
		}

		return off + header.getSideInfoSize();
	}
	//<<<<SIDE INFORMATION=====================================================

	//2.
	//>>>>SCALE FACTORS========================================================
	private int[][] scalefacLong;		// [nch][23];
	private int[][] scalefacShort;		// [nch][13*3];

	// -------------------------------- MPEG-1 --------------------------------
	private int slen0[] = { 0, 0, 0, 0, 3, 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4 };
	private int slen1[] = { 0, 1, 2, 3, 0, 1, 2, 3, 1, 2, 3, 1, 2, 3, 2, 3 };

	private void getScaleFactors(int gr, int ch) {
		int scf;
		final ChannelInformation ci = (ch == 0) ? infoCh0[gr] : infoCh1[gr];
		final int len0 = slen0[ci.scalefac_compress];
		final int len1 = slen1[ci.scalefac_compress];
		final int[] l = scalefacLong[ch];
		final int[] s = scalefacShort[ch];

		if (ci.window_switching_flag == 1 && ci.block_type == 2) {
			if (ci.mixed_block_flag == 1) {
				// MIXED block
				ci.part2_length = 17 * len0 + 18 * len1;
				for (scf = 0; scf < 8; scf++)
					l[scf] = maindataStream.getBits9(len0);
				for (scf = 9; scf < 18; scf++)
					s[scf] = maindataStream.getBits9(len0);
				for (scf = 18; scf < 36; scf++)
					s[scf] = maindataStream.getBits9(len1);
			} else {
				// pure SHORT block
				ci.part2_length = 18 * (len0 + len1);
				for (scf = 0; scf < 18; scf++)
					s[scf] = maindataStream.getBits9(len0);
				for (scf = 18; scf < 36; scf++)
					s[scf] = maindataStream.getBits9(len1);
			}
		} else {
			// LONG types 0,1,3
			if (gr == 0) {
				ci.part2_length = 10 * (len0 + len1) + len0;
				for (scf = 0; scf < 11; scf++)
					l[scf] = maindataStream.getBits9(len0);
				for (scf = 11; scf < 21; scf++)
					l[scf] = maindataStream.getBits9(len1);
			} else {
				ci.part2_length = 0;
				int selector = scfsi[ch];
				if ((selector & 8) == 0) {
					for (scf = 0; scf < 6; scf++)
						l[scf] = maindataStream.getBits9(len0);
					ci.part2_length += 6 * len0;
				}
				if ((selector & 4) == 0) {
					for (scf = 6; scf < 11; scf++)
						l[scf] = maindataStream.getBits9(len0);
					ci.part2_length += 5 * len0;
				}
				if ((selector & 2) == 0) {
					for (scf = 11; scf < 16; scf++)
						l[scf] = maindataStream.getBits9(len1);
					ci.part2_length += 5 * len1;
				}
				if ((selector & 1) == 0) {
					for (scf = 16; scf < 21; scf++)
						l[scf] = maindataStream.getBits9(len1);
					ci.part2_length += 5 * len1;
				}
			}
		}
	}

	// -------------------------------- MPEG-2 --------------------------------
	// x_slen: 增益因子(scalefactor)比特数
	private int[] i_slen2;		  // MPEG-2 slen for 'intensity stereo' mode
	private int[] n_slen2;		  // MPEG-2 slen for 'normal' mode
	private byte[][][] nr_of_sfb; //[3][6][4]

	private void initLSF() {
		i_slen2 = new int[256];
		n_slen2 = new int[512];
		nr_of_sfb = new byte[][][] {
			// ISO/IEC 13818-3 subclause 2.4.3.2 nr_of_sfbx x=1..4
			{ {6,5,5,5},{6,5,7,3},{11,10,0,0},{7,7,7,0},{6,6,6,3},{8,8,5,0} },
			{ {9,9,9,9},{9,9,12,6},{18,18,0,0},{12,12,12,0},{12,9,9,6},{15,12,9,0} },
			{ {6,9,9,9},{6,9,12,6},{15,18,0,0},{6,15,12,0},{6,12,9,6},{6,18,9,0} } };

		// ISO/IEC 13818-3 subclause 2.4.3.2 slenx, x=1..4
		int sfbi = 0;
		int preflag = 0;
		int slen4 = 0, slen3 = 0, slen2 = 0, slen1 = 0;
		for(int i = 0; i < 512; i++) {
			if (i < 400) {
				slen1 = (i >> 4) / 5;
				slen2 = (i >> 4) % 5;
				slen3 = (i & 0xf) >> 2;
				slen4 = i & 0x3;
				//preflag = sfbi = 0
				n_slen2[i] = (slen4 << 9) | (slen3 << 6) | (slen2 << 3) | slen1;
			} else if (400 <= i && i < 500) {
				slen1 = ((i - 400) >> 2) / 5;
				slen2 = ((i - 400) >> 2) % 5;
				slen3 =  (i - 400) & 0x3;
				//slen4 = preflag = 0
				sfbi = 1;
				n_slen2[i] = (sfbi << 13) | (slen3 << 6) | (slen2 << 3) | slen1;
			} else {
				slen1 = (i - 500) / 3;
				slen2 = (i - 500) % 3;
				//slen3 = slen4 = 0
				preflag = 1;
				sfbi = 2;
				n_slen2[i] = (sfbi << 13) | (preflag << 12) | (slen2 << 3) | slen1;
			}

			if(i > 255)
				continue;
			if (i < 180) {
				slen1 = i / 36;
				slen2 = (i % 36) / 6;
				slen3 = (i % 36) % 6;
				sfbi = 3;
			} else if (180 <= i && i < 244) {
				slen1 = ((i - 180) & 0x3f) >> 4;
				slen2 = ((i - 180) & 0xf) >> 2;
				slen3 =  (i - 180) & 0x3;
				sfbi = 4;
			} else {
				slen1 = (i - 244) / 3;
				slen2 = (i - 244) % 3;
				slen3 = 0;
				sfbi = 5;
			}
			// slen4 = preflag = 0
			i_slen2[i] = (sfbi << 13) | (slen3 << 6) | (slen2 << 3) | slen1;
		}
	}

	private void getScaleFactorsLSF(int gr, int ch) {
		byte[] nr;
		int[] scalefac;
		int i = 0, band, slen, num, scf = 0;
		final ChannelInformation ci = (ch == 0) ? infoCh0[gr] : infoCh1[gr];

		ci.part2_length = 0;
		rzeroBandLong = 0;
		if (ch == 1 && header.isIntensityStereo())
			slen = i_slen2[ci.scalefac_compress >> 1];
		else
			slen = n_slen2[ci.scalefac_compress];
		if (ci.block_type == 2) {
			i = (ci.mixed_block_flag == 1) ? 2 : 1;
			scalefac = scalefacShort[ch];
		} else
			scalefac = scalefacLong[ch];
		ci.preflag = (slen >> 12) & 0x1;
		nr = nr_of_sfb[i][slen >> 13];

		for (i = 0; i < 4; i++) {
			num = slen & 0x7;
			slen >>= 3;

			if (num == 0) {
				for (band = nr[i]; band > 0; band--)
					scalefac[scf++] = 0;
			} else {
				for (band = nr[i]; band > 0; band--)
					scalefac[scf++] = maindataStream.getBits17(num);
				ci.part2_length += nr[i] * num;
			}
		}

		while (scf < scalefac.length)
			scalefac[scf++] = 0;
	}
	//<<<<SCALE FACTORS========================================================

	//3.
	//>>>>HUFFMAN BITS=========================================================
	private int[] region;	// 某一码表用于解码主数据的区域(最多3个区段)
	private int[] hv;		// [32 * 18 + 4], 暂存解得的哈夫曼值

	private void huffBits(int gr, int ch) {
		final ChannelInformation ci = (ch == 0) ? infoCh0[gr] : infoCh1[gr];
		int r1, r2;
		int region1Start, region2Start;

		if (ci.window_switching_flag == 1) {
			int ver = header.getVersion();
			if(ver == Header.MPEG1 || (ver == Header.MPEG2 && ci.block_type == 2)) {
				region1Start = 36;
				region2Start = 576;
			} else {
				if(ver == Header.MPEG25) {
					if(ci.block_type == 2 && ci.mixed_block_flag == 0)
						region1Start = sfbIndexLong[6];
					else
						region1Start = sfbIndexLong[8];
					region2Start = 576;
				} else {
					region1Start = 54;
					region2Start = 576;
				}
			}
		} else {
			r1 = ci.region0_count + 1;
			r2 = r1 + ci.region1_count + 1;
			if (r2 > sfbIndexLong.length - 1)
				r2 = sfbIndexLong.length - 1;
			region1Start = sfbIndexLong[r1];
			region2Start = sfbIndexLong[r2];
		}

		int bv = ci.big_values << 1;
		if(bv > 574)
			bv = 574; // 错误的big_value置为0 ?
		if(region1Start < bv) {
			region[0] = region1Start;
			if(region2Start < bv) {
				region[1] = region2Start;
				region[2] = bv;
			} else
				region[1] = region[2] = bv;
		} else
			region[0] = region[1] = region[2] = bv;
		region[3] = ci.part2_3_length - ci.part2_length; //part3len

		ci.nonzero = maindataStream.decodeHuff(region, ci.table_select, ci.count1table_select, hv);  // 哈夫曼解码
	}
	//<<<<HUFFMAN BITS=========================================================

	//4.
	//>>>>REQUANTIZATION & REORDER=============================================
	private float[] floatPow2;	// [256 + 118 + 4]
	private float[] floatPowIS;	// [8207]
	private int[] widthLong;	// [22] 长块的增益因子频带(用一个增益因子逆量化频率线的条数)
	private int[] widthShort;	// [13] 短块的增益因子频带
	private int rzeroBandLong;
	private int[] rzeroBandShort;

	// ISO/IEC 11172-3 ANNEX B,Table 3-B.6. Layer III Preemphasis
	private int[] pretab = {0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,2,2,3,3,3,2,0};

	/**
	 * 逆量化并对短块(纯短块和混合块中的短块)重排序.在逆量化时赋值的变量:<br>
	 * rzero_bandL -- 长块非零哈夫曼值的频带数,用于强度立体声(intensity stereo)处理<br>
	 * rzero_bandS -- 短块非零哈夫曼值的频带数,用于强度立体声处理<br>
	 * rzero_index -- 非零哈夫曼值的"子带"数
	 * <p>
	 * Layer3 逆量化公式ISO/IEC 11172-3, 2.4.3.4
	 * <p>
	 * XRi = pow(2, 0.25 * (global_gain - 210)) <br>
	 * if (LONG block types) <br>
	 *　　XRi *= pow(2, -0.5 * (1 + scalefac_scale) * (L[sfb] + preflag * pretab[sfb])) <br>
	 * if (SHORT block types) { <br>
	 *　　XRi *= pow(2, 0.25 * -8 * subblock_gain[sfb]) <br>
	 *　　XRi *= pow(2, 0.25 * -2 * (1 + scalefac_scale) * S[scf]) } <br>
	 * XRi *= sign(haffVal) * pow(abs(haffVal), 4/3) <br>
	 * 
	 */
	private void requantizer(int ch, ChannelInformation ci) {
		final int[] l = scalefacLong[ch];
		final float[] buf = ci.buf;
		final boolean preflag = ci.preflag == 1;
		final int shift = 1 + ci.scalefac_scale;
		final int maxi = ci.nonzero;
		float requVal;
		int bi=0, sfb = 0, width, pre, val, hvidx = 0, xri = 0, scf = 0;
		int xriStart = 0; // 用于计算短块重排序后的下标
		int pow2i = 255 - ci.global_gain;

		if (header.isMS())
			pow2i += 2; // 若声道模式为ms_stereo,要除以根2

		// pure SHORT blocks:
		// window_switching_flag=1, block_type=2, mixed_block_flag=0

		if (ci.window_switching_flag == 1 && ci.block_type == 2) {
			rzeroBandShort[0] = rzeroBandShort[1] = rzeroBandShort[2] = -1;
			if (ci.mixed_block_flag == 1) {
				/*
				 * 混合块:
				 * 混合块的前8个频带是长块。 前8块各用一个增益因子逆量化，这8个增益因子的频带总和为36，
				 * 这36条频率线用长块公式逆量化。
				 */
				rzeroBandLong = -1;
				for (; sfb < 8; sfb++) {
					pre = preflag ? pretab[sfb] : 0;
					requVal = floatPow2[pow2i + ((l[sfb] + pre) << shift)];
					width = widthLong[sfb];
					for (bi = 0; bi < width; bi++) {
						val = hv[hvidx]; // 哈夫曼值
						if (val < 0) {
							buf[hvidx] = -requVal * floatPowIS[-val];
							rzeroBandLong = sfb;
						} else if (val > 0) {
							buf[hvidx] = requVal * floatPowIS[val];
							rzeroBandLong = sfb;
						} else
							buf[hvidx] = 0;
						hvidx++;
					}
				}

				/*
				 * 混合块的后9个频带是被加窗的短块，其每一块同一窗口内3个值的增益因子频带相同。
				 * 后9块增益因子对应的频率子带值为widthShort[3..11]
				 */
				rzeroBandShort[0] = rzeroBandShort[1] = rzeroBandShort[2] = 2;
				rzeroBandLong++;
				sfb = 3;
				scf = 9;
				xriStart = 36; // 为短块重排序准备好下标
			}

			// 短块(混合块中的短块和纯短块)
			final int[] s = scalefacShort[ch];
			final int[] subgain = ci.subblock_gain;
			subgain[0] <<= 3;
			subgain[1] <<= 3;
			subgain[2] <<= 3;
			int win;
			for (; hvidx < maxi; sfb++) {
				width = widthShort[sfb];
				for (win = 0; win < 3; win++) {
					requVal = floatPow2[pow2i + subgain[win] + (s[scf++] << shift)];
					xri = xriStart + win;
					for (bi = 0; bi < width; bi++) {
						val = hv[hvidx];
						if (val < 0) {
							buf[xri] = -requVal * floatPowIS[-val];
							rzeroBandShort[win] = sfb;
						} else if (val > 0) {
							buf[xri] = requVal * floatPowIS[val];
							rzeroBandShort[win] = sfb;
						} else
							buf[xri] = 0;
						hvidx++;
						xri += 3;
					}
				}
				xriStart = xri -2;
			}
			rzeroBandShort[0]++;
			rzeroBandShort[1]++;
			rzeroBandShort[2]++;
			rzeroBandLong++;
		} else {
			// 长块
			xri = -1;
			for (; hvidx < maxi; sfb++) {
				pre = preflag ? pretab[sfb] : 0;
				requVal = floatPow2[pow2i + ((l[sfb] + pre) << shift)];
				bi = hvidx + widthLong[sfb];
				for (; hvidx < bi; hvidx++) {
					val = hv[hvidx];
					if (val < 0) {
						buf[hvidx] = -requVal * floatPowIS[-val];
						xri = sfb;
					} else if (val > 0) {
						buf[hvidx] = requVal * floatPowIS[val];
						xri = sfb;
					} else
						buf[hvidx] = 0;
				}
			}
			rzeroBandLong = xri + 1;
		}

		// 不逆量化0值区,置0.
		for (; hvidx < 576; hvidx++)
			buf[hvidx] = 0;
	}
	//<<<<REQUANTIZATION & REORDER=============================================

	//5.
	//>>>>STEREO===============================================================

	// 在requantizer方法内已经作了除以根2处理,ms_stereo内不再除以根2.
	private void ms_stereo(int gr) {
		float v0, v1;
		final float[] buf0 = infoCh0[gr].buf, buf1 = infoCh1[gr].buf;
		int i = infoCh0[gr].nonzero;
		final int nz = (i > infoCh1[gr].nonzero) ? i : infoCh1[gr].nonzero;

		for (i = 0; i < nz; i++) {
			v0 = buf0[i];
			v1 = buf1[i];
			buf0[i] = v0 + v1;
			buf1[i] = v0 - v1;
		}
		infoCh0[gr].nonzero = infoCh1[gr].nonzero = nz; //...若不这样,可能导致声音细节丢失
	}

	private float[][] is_coef_lsf;
	private float[] is_coef;

	// 解码一个频带强度立体声,MPEG-1
	private void is_lines(int pos, int idx0, int width,int step,int gr) {
		float v;
		for (int w = width; w > 0; w--) {
			v = infoCh0[gr].buf[idx0];
			infoCh0[gr].buf[idx0] = v * is_coef[pos];
			infoCh1[gr].buf[idx0] = v * is_coef[6 - pos];
			idx0 += step;
		}
	}

	// 解码一个频带强度立体声,MPEG-2
	private void is_lines_lsf(int tab2, int pos, int idx0, int width,int step,int gr) {
		float v;
		for (int w = width; w > 0; w--) {
			v = infoCh0[gr].buf[idx0];
			if (pos == 0)
				infoCh1[gr].buf[idx0] = v;
			else {
				if ((pos & 1) == 0)
					infoCh1[gr].buf[idx0] = v * is_coef_lsf[tab2][(pos - 1) >> 1];
				else {
					infoCh0[gr].buf[idx0] = v * is_coef_lsf[tab2][(pos - 1) >> 1];
					infoCh1[gr].buf[idx0] = v;
				}
			}
			idx0 += step;
		}
	}

	/*
	 * 强度立体声(intensity stereo)解码
	 *
	 * ISO/IEC 11172-3不对混合块中的长块作强度立体声处理,但很多MP3解码程序都作了处理.
	 */
	private void intensity_stereo(int gr) {
		final ChannelInformation ci = infoCh1[gr]; //信息保存在右声道
		int scf, idx, sfb;
		if(infoCh0[gr].mixed_block_flag != ci.mixed_block_flag
				|| infoCh0[gr].block_type != ci.block_type)
			return;

		if(isMPEG1) {	//MPEG-1
			if(ci.block_type == 2) {
				//MPEG-1, short block/mixed block
				int w3;
				for (w3 = 0; w3 < 3; w3++) {
					sfb = rzeroBandShort[w3]; // 混合块sfb最小为3
					for (; sfb < 12; sfb++) {
						idx = 3 * sfbIndexShort[sfb] + w3;
						scf = scalefacShort[1][3 * sfb + w3];
						if (scf >= 7)
							continue;
						is_lines(scf, idx, widthShort[sfb], 3, gr);
					}
				}
			} else {
				//MPEG-1, long block
				for (sfb = rzeroBandLong; sfb <= 21; sfb++) {
					scf = scalefacLong[1][sfb];
					if (scf < 7)
						is_lines(scf, sfbIndexLong[sfb], widthLong[sfb], 1, gr);
				}
			}
		} else {	//MPEG-2
			int tab2 = ci.scalefac_compress & 0x1;
			if(ci.block_type == 2) {
				//MPEG-2, short block/mixed block
				int w3;
				for (w3 = 0; w3 < 3; w3++) {
					sfb = rzeroBandShort[w3]; // 混合块sfb最小为3
					for (; sfb < 12; sfb++) {
						idx = 3 * sfbIndexShort[sfb] + w3;
						scf = scalefacShort[1][3 * sfb + w3];
						is_lines_lsf(tab2, scf, idx, widthShort[sfb], 3, gr);
					}
				}
			} else {
				//MPEG-2, long block
				for (sfb = rzeroBandLong; sfb <= 21; sfb++)
					is_lines_lsf(tab2, scalefacLong[1][sfb], sfbIndexLong[sfb], widthLong[sfb],1,gr);
			}
		}
	}
	//<<<<STEREO===============================================================

	//6.
	//>>>>ANTIALIAS============================================================

	private void antialias(int ch, ChannelInformation ci) {
		int i, maxi;
		float bu, bd;
		final float[] recv = ci.buf;

		if (ci.block_type == 2) {
			if (ci.mixed_block_flag == 0)
				return;
			maxi = 18;
		} else
			maxi = ci.nonzero - 18;

		for (i = 0; i < maxi; i += 18) {
			bu = recv[i + 17];
			bd = recv[i + 18];
			recv[i + 17] = bu * 0.85749293f + bd * 0.51449576f;
			recv[i + 18] = bd * 0.85749293f - bu * 0.51449576f;
			bu = recv[i + 16];
			bd = recv[i + 19];
			recv[i + 16] = bu * 0.8817420f + bd * 0.47173197f;
			recv[i + 19] = bd * 0.8817420f - bu * 0.47173197f;
			bu = recv[i + 15];
			bd = recv[i + 20];
			recv[i + 15] = bu * 0.94962865f + bd * 0.31337745f;
			recv[i + 20] = bd * 0.94962865f - bu * 0.31337745f;
			bu = recv[i + 14];
			bd = recv[i + 21];
			recv[i + 14] = bu * 0.98331459f + bd * 0.18191320f;
			recv[i + 21] = bd * 0.98331459f - bu * 0.18191320f;
			bu = recv[i + 13];
			bd = recv[i + 22];
			recv[i + 13] = bu * 0.99551782f + bd * 0.09457419f;
			recv[i + 22] = bd * 0.99551782f - bu * 0.09457419f;
			bu = recv[i + 12];
			bd = recv[i + 23];
			recv[i + 12] = bu * 0.99916056f + bd * 0.04096558f;
			recv[i + 23] = bd * 0.99916056f - bu * 0.04096558f;
			bu = recv[i + 11];
			bd = recv[i + 24];
			recv[i + 11] = bu * 0.99989920f + bd * 0.0141986f;
			recv[i + 24] = bd * 0.99989920f - bu * 0.0141986f;
			bu = recv[i + 10];
			bd = recv[i + 25];
			recv[i + 10] = bu * 0.99999316f + bd * 3.69997467e-3f;
			recv[i + 25] = bd * 0.99999316f - bu * 3.69997467e-3f;
		}
	}
	//<<<<ANTIALIAS============================================================

	//7.
	//>>>>HYBRID(synthesize via iMDCT)=========================================
	private float[][] imdctWIN = {
		{0.0322824f,0.1072064f,0.2014143f,0.3256164f,0.5f,0.7677747f,
		1.2412229f,2.3319514f,7.7441506f,-8.4512568f,-3.0390580f,-1.9483297f,
		-1.4748814f,-1.2071068f,-1.0327232f,-0.9085211f,-0.8143131f,-0.7393892f,
		-0.6775254f,-0.6248445f,-0.5787917f,-0.5376016f,-0.5f,-0.4650284f,
		-0.4319343f,-0.4000996f,-0.3689899f,-0.3381170f,-0.3070072f,-0.2751725f,
		-0.2420785f,-0.2071068f,-0.1695052f,-0.1283151f,-0.0822624f,-0.0295815f},
		{0.0322824f,0.1072064f,0.2014143f,0.3256164f,0.5f,0.7677747f,
		1.2412229f,2.3319514f,7.7441506f,-8.4512568f,-3.0390580f,-1.9483297f,
		-1.4748814f,-1.2071068f,-1.0327232f,-0.9085211f,-0.8143131f,-0.7393892f,
		-0.6781709f,-0.6302362f,-0.5928445f,-0.5636910f,-0.5411961f,-0.5242646f,
		-0.5077583f,-0.4659258f,-0.3970546f,-0.3046707f,-0.1929928f,-0.0668476f,
		-0.0f,-0.0f,-0.0f,-0.0f,-0.0f,-0.0f},
		{/* block_type = 2 */},
		{0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,
		0.3015303f,1.4659259f,6.9781060f,-9.0940447f,-3.5390582f,-2.2903500f,
		-1.6627548f,-1.3065630f,-1.0828403f,-0.9305795f,-0.8213398f,-0.7400936f,
		-0.6775254f,-0.6248445f,-0.5787917f,-0.5376016f,-0.5f,-0.4650284f,
		-0.4319343f,-0.4000996f,-0.3689899f,-0.3381170f,-0.3070072f,-0.2751725f,
		-0.2420785f,-0.2071068f,-0.1695052f,-0.1283151f,-0.0822624f,-0.0295815f} };

	private void imdct12(float[] preBlck, float[] recv, int off) {
		final float[] io = recv;
		final float[] pre = preBlck;
		int i,j;
		float in1,in2,in3,in4;
		float out0, out1, out2, out3, out4, out5, tmp;
		float out6=0, out7=0, out8=0, out9=0, out10=0, out11=0;
		float out12=0, out13=0, out14=0, out15=0, out16=0, out17=0;
		float f0 = 0, f1 = 0, f2 = 0, f3 = 0, f4 = 0, f5 = 0;

		for (j = 0; j != 3; j++) {
			i = j + off;
			//>>>>>>>>>>>> 12-point IMDCT
			//>>>>>> 6-point IDCT
			io[15 + i] += (io[12 + i] += io[9 + i]) + io[6 + i];
			io[9 + i] += (io[6 + i] += io[3 + i]) + io[i];
			io[3 + i] += io[i];

			//>>> 3-point IDCT on even
			out1 = (in1 = io[i]) - (in2 = io[12 + i]);
			in3 = in1 + in2 * 0.5f;
			in4 = io[6 + i] * 0.8660254f;
			out0 = in3 + in4;
			out2 = in3 - in4;
			//<<< End 3-point IDCT on even

			//>>> 3-point IDCT on odd (for 6-point IDCT)
			out4 = ((in1 = io[3 + i]) - (in2 = io[15 + i])) * 0.7071068f;
			in3 = in1 + in2 * 0.5f;
			in4 = io[9 + i] * 0.8660254f;
			out5 = (in3 + in4) * 0.5176381f;
			out3 = (in3 - in4) * 1.9318516f;
			//<<< End 3-point IDCT on odd

			// Output: butterflies on 2,3-point IDCT's (for 6-point IDCT)
			tmp = out0; out0 += out5; out5 = tmp - out5;
			tmp = out1; out1 += out4; out4 = tmp - out4;
			tmp = out2; out2 += out3; out3 = tmp - out3;
			//<<<<<< End 6-point IDCT
			//<<<<<<<<<<<< End 12-point IDCT

			tmp = out3 * 0.1072064f;
			switch(j) {
			case 0:
				out6  = tmp;
				out7  = out4 * 0.5f;
				out8  = out5 * 2.3319512f;
				out9  = -out5 * 3.0390580f;
				out10 = -out4 * 1.2071068f;
				out11 = -tmp  * 7.5957541f;

				f0 = out2 * 0.6248445f;
				f1 = out1 * 0.5f;
				f2 = out0 * 0.4000996f;
				f3 = out0 * 0.3070072f;
				f4 = out1 * 0.2071068f;
				f5 = out2 * 0.0822623f;
				break;
			case 1:
				out12 = tmp - f0;
				out13 = out4 * 0.5f - f1;
				out14 = out5 * 2.3319512f - f2;
				out15 = -out5 * 3.0390580f - f3;
				out16 = -out4 * 1.2071068f - f4;
				out17 = -tmp * 7.5957541f - f5;

				f0 = out2 * 0.6248445f;
				f1 = out1 * 0.5f;
				f2 = out0 * 0.4000996f;
				f3 = out0 * 0.3070072f;
				f4 = out1 * 0.2071068f;
				f5 = out2 * 0.0822623f;
				break;
			case 2:
				// output
				i = off;
				io[i + 0] = pre[i + 0];
				io[i + 1] = pre[i + 1];
				io[i + 2] = pre[i + 2];
				io[i + 3] = pre[i + 3];
				io[i + 4] = pre[i + 4];
				io[i + 5] = pre[i + 5];
				io[i + 6] = pre[i + 6] + out6;
				io[i + 7] = pre[i + 7] + out7;
				io[i + 8] = pre[i + 8] + out8;
				io[i + 9] = pre[i + 9] + out9;
				io[i + 10] = pre[i + 10] + out10;
				io[i + 11] = pre[i + 11] + out11;
				io[i + 12] = pre[i + 12] + out12;
				io[i + 13] = pre[i + 13] + out13;
				io[i + 14] = pre[i + 14] + out14;
				io[i + 15] = pre[i + 15] + out15;
				io[i + 16] = pre[i + 16] + out16;
				io[i + 17] = pre[i + 17] + out17;

				pre[i + 0] = tmp - f0;
				pre[i + 1] = out4 * 0.5f - f1;
				pre[i + 2] = out5 * 2.3319512f - f2;
				pre[i + 3] = -out5 * 3.0390580f - f3;
				pre[i + 4] = -out4 * 1.2071068f - f4;
				pre[i + 5] = -tmp * 7.5957541f - f5;
				pre[i + 6] = -out2 * 0.6248445f;
				pre[i + 7] = -out1 * 0.5f;
				pre[i + 8] = -out0 * 0.4000996f;
				pre[i + 9] = -out0 * 0.3070072f;
				pre[i + 10] = -out1 * 0.2071068f;
				pre[i + 11] = -out2 * 0.0822623f;
				pre[i + 12] = pre[i + 13] = pre[i + 14] = 0;
				pre[i + 15] = pre[i + 16] = pre[i + 17] = 0;
			}
		}
	}

	private void imdct36(float[] preBlck, float[] recv, int off, int block_type) {
		final float[] io = recv;
		final float[] pre = preBlck;
		int i = off;
		float in0, in1, in2, in3, in4, in5, in6, in7, in8, in9, in10, in11;
		float in12, in13, in14, in15, in16, in17;
		float out0, out1, out2, out3, out4, out5, out6, out7, out8, out9;
		float out10, out11, out12, out13, out14, out15, out16, out17, tmp;

		// 采用 Byeong Gi Lee 的算法

		//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 36-point IDCT
		//>>>>>>>>>>>>>>>>>> 18-point IDCT for odd
		io[i + 17] += (io[i + 16] += io[i + 15]) + io[i + 14];
		io[i + 15] += (io[i + 14] += io[i + 13]) + io[i + 12];
		io[i + 13] += (io[i + 12] += io[i + 11]) + io[i + 10];
		io[i + 11] += (io[i + 10] += io[i + 9]) + io[i + 8];
		io[i + 9] += (io[i + 8] += io[i + 7]) + io[i + 6];
		io[i + 7] += (io[i + 6] += io[i + 5]) + io[i + 4];
		io[i + 5] += (io[i + 4] += io[i + 3]) + io[i + 2];
		io[i + 3] += (io[i + 2] += io[i + 1]) + io[i + 0];
		io[i + 1] += io[i + 0];

		//>>>>>>>>> 9-point IDCT on even
		in0 = io[i + 0] + io[i + 12] * 0.5f;
		in1 = io[i + 0] - io[i + 12];
		in2 = io[i + 8] + io[i + 16] - io[i + 4];

		out4 = in1 + in2;

		in3 = in1 - in2 * 0.5f;
		in4 = (io[i + 10] + io[i + 14] - io[i + 2]) * 0.8660254f; // cos(PI/6)

		out1 = in3 - in4;
		out7 = in3 + in4;

		in5 = (io[i+4] + io[i+8]) * 0.9396926f;		//cos( PI/9)
		in6 = (io[i+16] - io[i+8]) * 0.1736482f;	//cos(4PI/9)
		in7 = -(io[i+4] + io[i+16]) * 0.7660444f;	//cos(2PI/9)

		in17 = in0 - in5 - in7;
		in8 = in5 + in0 + in6;
		in9 = in0 + in7 - in6;

		in12 = io[i+6] * 0.8660254f;				//cos(PI/6)
		in10 = (io[i+2] + io[i+10]) * 0.9848078f;	//cos(PI/18)
		in11 = (io[i+14] - io[i+10]) * 0.3420201f;	//cos(7PI/18)

		in13 = in10 + in11 + in12;

		out0 = in8 + in13;
		out8 = in8 - in13;

		in14 = -(io[i+2] + io[i+14]) * 0.6427876f;	//cos(5PI/18)
		in15 = in10 + in14 - in12;
		in16 = in11 - in14 - in12;

		out3 = in9 + in15;
		out5 = in9 - in15;

		out2 = in17 + in16;
		out6 = in17 - in16;
		//<<<<<<<<< End 9-point IDCT on even

		//>>>>>>>>> 9-point IDCT on odd
		in0 = io[i+1] + io[i+13] * 0.5f;	//cos(PI/3)
		in1 = io[i+1] - io[i+13];
		in2 = io[i+9] + io[i+17] - io[i+5];

		out13 = (in1 + in2) * 0.7071068f;	//cos(PI/4)

		in3 = in1 - in2 * 0.5f;
		in4 = (io[i+11] + io[i+15] - io[i+3]) * 0.8660254f;	//cos(PI/6)

		out16 = (in3 - in4) * 0.5176381f;	// 0.5/cos( PI/12)
		out10 = (in3 + in4) * 1.9318517f;	// 0.5/cos(5PI/12)

		in5 = (io[i+5] + io[i+9]) * 0.9396926f;	// cos( PI/9)
		in6 = (io[i+17] - io[i+9])* 0.1736482f;	// cos(4PI/9)
		in7 = -(io[i+5] + io[i+17])*0.7660444f;	// cos(2PI/9)

		in17 = in0 - in5 - in7;
		in8 = in5 + in0 + in6;
		in9 = in0 + in7 - in6;

		in12 = io[i+7] * 0.8660254f;				// cos(PI/6)
		in10 = (io[i+3] + io[i+11]) * 0.9848078f;	// cos(PI/18)
		in11 = (io[i+15] - io[i+11])* 0.3420201f;	// cos(7PI/18)

		in13 = in10 + in11 + in12;

		out17 = (in8 + in13) * 0.5019099f;		// 0.5/cos(PI/36)
		out9 = (in8 - in13) * 5.7368566f;		// 0.5/cos(17PI/36)

		in14 = -(io[i+3] + io[i+15]) * 0.6427876f;	// cos(5PI/18)
		in15 = in10 + in14 - in12;
		in16 = in11 - in14 - in12;

		out14 = (in9 + in15) * 0.6103873f;		// 0.5/cos(7PI/36)
		out12 = (in9 - in15) * 0.8717234f;		// 0.5/cos(11PI/36)

		out15 = (in17 + in16) * 0.5516890f;		// 0.5/cos(5PI/36)
		out11 = (in17 - in16) * 1.1831008f;		// 0.5/cos(13PI/36)
		//<<<<<<<<< End. 9-point IDCT on odd

		// Butterflies on 9-point IDCT's
		tmp = out0; out0 += out17; out17 = tmp - out17;
		tmp = out1; out1 += out16; out16 = tmp - out16;
		tmp = out2; out2 += out15; out15 = tmp - out15;
		tmp = out3; out3 += out14; out14 = tmp - out14;
		tmp = out4; out4 += out13; out13 = tmp - out13;
		tmp = out5; out5 += out12; out12 = tmp - out12;
		tmp = out6; out6 += out11; out11 = tmp - out11;
		tmp = out7; out7 += out10; out10 = tmp - out10;
		tmp = out8; out8 += out9;  out9  = tmp - out9;
		//<<<<<<<<<<<<<<<<<< End of 18-point IDCT
		//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< End of 36-point IDCT

		// output
		float[] win = imdctWIN[block_type];

		io[i + 0] = pre[i + 0] + out9 * win[0];
		io[i + 1] = pre[i + 1] + out10 * win[1];
		io[i + 2] = pre[i + 2] + out11 * win[2];
		io[i + 3] = pre[i + 3] + out12 * win[3];
		io[i + 4] = pre[i + 4] + out13 * win[4];
		io[i + 5] = pre[i + 5] + out14 * win[5];
		io[i + 6] = pre[i + 6] + out15 * win[6];
		io[i + 7] = pre[i + 7] + out16 * win[7];
		io[i + 8] = pre[i + 8] + out17 * win[8];
		io[i + 9] = pre[i + 9] + out17 * win[9];
		io[i + 10] = pre[i + 10] + out16 * win[10];
		io[i + 11] = pre[i + 11] + out15 * win[11];
		io[i + 12] = pre[i + 12] + out14 * win[12];
		io[i + 13] = pre[i + 13] + out13 * win[13];
		io[i + 14] = pre[i + 14] + out12 * win[14];
		io[i + 15] = pre[i + 15] + out11 * win[15];
		io[i + 16] = pre[i + 16] + out10 * win[16];
		io[i + 17] = pre[i + 17] + out9 * win[17];

		pre[i + 0] = out8 * win[18];
		pre[i + 1] = out7 * win[19];
		pre[i + 2] = out6 * win[20];
		pre[i + 3] = out5 * win[21];
		pre[i + 4] = out4 * win[22];
		pre[i + 5] = out3 * win[23];
		pre[i + 6] = out2 * win[24];
		pre[i + 7] = out1 * win[25];
		pre[i + 8] = out0 * win[26];
		pre[i + 9] = out0 * win[27];
		pre[i + 10] = out1 * win[28];
		pre[i + 11] = out2 * win[29];
		pre[i + 12] = out3 * win[30];
		pre[i + 13] = out4 * win[31];
		pre[i + 14] = out5 * win[32];
		pre[i + 15] = out6 * win[33];
		pre[i + 16] = out7 * win[34];
		pre[i + 17] = out8 * win[35];
	}

	private float[] preBlckCh0; // [32*18],左声道FIFO队列
	private float[] preBlckCh1; // [32*18],右声道FIFO

	private void hybrid(int ch, ChannelInformation ci) {
		int off, block_type;
		final int nz = ci.nonzero;
		final float[] recv = ci.buf;
		final float[] preBlck = (ch == 0) ? preBlckCh0 : preBlckCh1;
		final boolean mixed = (ci.window_switching_flag == 1 && ci.mixed_block_flag == 1);

		for (off = 0; off < nz; off += 18) {
			block_type = (mixed && off < 36) ? 0 : ci.block_type;

			if(block_type == 2)
				imdct12(preBlck, recv, off);
			else
				imdct36(preBlck, recv, off, block_type);
		}

		// 0值区
		for (; off < 576; off++) {
			recv[off] = preBlck[off];
			preBlck[off] = 0;
		}
	}
	//<<<<HYBRID(synthesize via iMDCT)=========================================

	//8.
	//>>>>INVERSE QUANTIZE SAMPLES=============================================
	//
	// 在 decodeAudioDataChannel 方法内实现多相频率倒置
	//
	//<<<<INVERSE QUANTIZE SAMPLES=============================================

	//9.
	//>>>>SYNTHESIZE VIA POLYPHASE MDCT========================================
	//
	// jmp123.decoder.Synthesis.synthesisSubBand 方法
	//
	//<<<<SYNTHESIZE VIA POLYPHASE MDCT========================================

	//10.
	//>>>>OUTPUT PCM SAMPLES===================================================
	//
	// jmp123.output.Audio 类
	//
	//<<<<OUTPUT PCM SAMPLES===================================================

	/**
	 * 解码1帧Layer Ⅲ除帧头外的音乐数据。
	 */
	public int decodeAudioData(byte[] b, int off) {
		/*
		 * part1 : 帧边信息
		 */
		int gr, i = getSideInfo(b, off);
		if (i < 0)
			return off + header.getFrameSize() - 4; // 跳过这一帧的主数据
		off = i;

		/*
		 * part2_3: 增益因子 + 哈夫曼位
		 * length: ((part2_3_bits + 7) >> 3) bytes
		 */
		int maindataSize = header.getMainDataSize();
		int endPos = maindataStream.getSize();
		if (endPos < main_data_begin) {
			/*
			 * 若出错，不解码当前这一帧， 将主数据(main_data)填入位流缓冲区后返回，
			 * 在解码下一帧时全部或部分利用填入的这些主数据。
			 */
			maindataStream.append(b, off, maindataSize);
			return off + maindataSize;
		}

		// 丢弃上一帧的填充位
		int discard = endPos - maindataStream.getBytePos() - main_data_begin;
		maindataStream.skipBytes(discard);

		// 主数据添加到位流缓冲区
		maindataStream.append(b, off, maindataSize);
		off += maindataSize;
		//maindataStream.mark();//----debug

		for (gr = 0; gr < ngr; gr++) {
			if (isMPEG1)
				getScaleFactors(gr, 0);
			else
				getScaleFactorsLSF(gr, 0);
			huffBits(gr, 0);
			requantizer(0, infoCh0[gr]);

			if (nch == 2) {
				if (isMPEG1)
					getScaleFactors(gr, 1);
				else
					getScaleFactorsLSF(gr, 1);
				huffBits(gr, 1);
				requantizer(1, infoCh1[gr]);

				if(header.isMS())
					ms_stereo(gr);
				if(header.isIntensityStereo())
					intensity_stereo(gr);
			}
		}
		// int part2_3_bytes = maindataStream.getMark();//----debug
		/*
		 * 可以在这调用maindataStream.skipBits(part2_3_bits & 7)丢弃填充位，
		 * 更好的方法是放在解码下一帧主数据之前处理，如果位流错误，可以顺便纠正。
		 */

		if (coresEnabled) {
			// 用异步方式完成解码音乐数据的其余4步
			infoCh0 = audiodataCh0.handover();
			if (nch == 2)
				infoCh1 = audiodataCh1.handover();

			try {
				synchronized (outputLock) {
					// 若缓冲区super.pcmbuf共16块全部分配出且没有解码线程退出,等待super.outputPCM方法被调用
					while (pcmbufCount == 0 && nadc == nch) {
						//owt++;
						outputLock.wait();
					}
					pcmbufCount--;
				}
			} catch (InterruptedException e) {
				System.out.println("Layer3.decodeAudioData: " + e.getMessage());
				return off;
			}
		} else {
			// 单线程串行解码音乐数据,完成其余4步
			decodeAudioDataChannel(infoCh0, 0);
			if (nch == 2)
				decodeAudioDataChannel(infoCh1, 1);

			// 输出结果
			super.outputPCM();
		}

		return off;
	}

	//private int owt; //----debug

	/*
	 * samplesChx: 暂存从子带合成(hybrid)的结果中抽取出来的32个样本值,
	 * 这32个样本值作为输入数据被送入多相合成滤波器.
	 */
	private float[] samplesCh0, samplesCh1;

	// 一个声道消混叠、子带合成、多相频率倒置和多相合成滤波
	private void decodeAudioDataChannel(ChannelInformation[] ci, int ch) {
		int gr, ss, sb, i;
		float[] b;
		float[] samples = (ch == 0) ? samplesCh0 : samplesCh1;

		for (gr = 0; gr < ngr; gr++) {
			antialias(ch, ci[gr]);	//消混叠
			hybrid(ch, ci[gr]);		//子带合成

			b = ci[gr].buf;
			for (ss = 0; ss < 18; ss += 2) {
				for (i = ss, sb = 0; sb < 32; sb++, i += 18)
					samples[sb] = b[i];
				super.synthesisSubBand(samples, ch);//多相合成滤波

				for (i = ss + 1, sb = 0; sb < 32; sb += 2, i += 36) {
					samples[sb] = b[i];

					samples[sb + 1] = -b[i + 18];	//多相频率倒置(INVERSE QUANTIZE SAMPLES)
				}
				super.synthesisSubBand(samples, ch);
			}
		}
	}

	@Override
	public void close(boolean interrupted) {
		if (coresEnabled) {
			// AbstractDecoder.run结束while循环,decodeAudioData已返回

			audiodataCh0.stop(interrupted);
			if (nch == 2)
				audiodataCh1.stop(interrupted);

			if (interrupted || !super.started()) {
				super.close(interrupted);
				synchronized (pcmbufLock) {
					pcmbufLock.notifyAll();
				}
				return;
			}

			try {
				audiodataCh0.join();
				if (nch == 2)
					audiodataCh1.join();
			} catch (InterruptedException e) {
				System.out.println("Layer3.close: " + e.getMessage());
			}
		}

		super.close(interrupted);
	}

	/*
	 * 一个粒度内一个声道的信息
	 */
	private class ChannelInformation {
		// 从位流读取数据按下列顺序依次初始化的14个变量,见getSideInfo方法.
		private int part2_3_length;
		private int big_values;
		private int global_gain;
		private int scalefac_compress;
		private int window_switching_flag;
		private int block_type;
		private int mixed_block_flag;
		private int[] table_select;
		private int[] subblock_gain;
		private int region0_count;
		private int region1_count;
		private int preflag;
		private int scalefac_scale;
		private int count1table_select;

		private int part2_length;	//增益因子(scale-factor)比特数
		private int nonzero;		//大值区和小值区总长度
		private float[] buf;		//暂存一个粒度内的一个声道逆量化、消混叠、子带合成等步骤产生的中间结果

		private ChannelInformation() {
			table_select = new int[3];
			subblock_gain = new int[3];
			buf = new float[32 * 18];
		}
	}

	/**
	 * 一个声道消混叠、子带合成、多相频率倒置和多相合成滤波。用于两个声道并发运算。
	 * <p>由于大量浮点运算，这几步耗时最多。解码一帧2声道MP3，实测这几步耗时65%以上，若并发运算可提高解码速度。
	 */
	private class AudioDataConcurrent extends Thread {
		private int ch;
		private boolean hasNextFrame;
		private int ciCount;
		private byte[] ciLock;
		private int writeCursor;
		private ChannelInformation[][] fifoCI;

		private AudioDataConcurrent(int ch) {
			this.ch = ch;
			hasNextFrame = true;
			ciLock = new byte[0];
			fifoCI = new ChannelInformation[16][ngr];
			for(int i = 0; i < 16; i++)
				for(int gr = 0; gr < ngr; gr++)
					fifoCI[i][gr] = new ChannelInformation();
			setName("AudioDataConcurrent_ch" + ch);
		}

		/**
		 * 切换缓冲区。
		 * @return 一个空闲的缓冲区，该缓冲区用于Layer3对象在逆量化时暂存数据。
		 */
		private ChannelInformation[] handover() {
			synchronized (ciLock) {
				ciCount++;
				ciLock.notify();

				writeCursor = (writeCursor + 1) & 0xf; //通过队列移位切换缓冲区
				return fifoCI[writeCursor];
			}
		}

		/**
		 * 获取空闲缓冲区。
		 * @return 该缓冲区内的数据已被run方法内的任务读取完毕。
		 */
		private ChannelInformation[] getChannelInformation() {
			return fifoCI[writeCursor];
		}

		/*
		 * 当handover不再被调用时调用本方法.
		 * 若interrupted为true,线程立即结束;为false,线程完成所有已接收到的任务后自然结束.
		 */
		private void stop(boolean interrupted) {
			synchronized (ciLock) {
				hasNextFrame = false;	//到达流尾,没有下一帧了
				if(interrupted)
					ciCount = 0;
				ciLock.notify();
			}
		}

		/*
		 * 完成解码音乐数据的其余4步并输出解得的PCM;
		 * 3个解码线程同步;
		 * fifoCI 读写同步.
		 */
		private void decodeAudioDataConcurrent() throws InterruptedException {
			int wsize, readCursor = 0;
			//int cwt = 0, pwt = 0; //----debug
			
			for (;;) {
				//1. 线程同步
				synchronized (ciLock) {
					//if(debug1)
					//System.out.printf("ch=%d, ciCount=%2d, readCursor=%2d, eob=%s\n", ch,ciCount,readCursor,isEndOfBuffer(ch));
					while(ciCount == 0 && hasNextFrame) {
						//cwt++;
						ciLock.wait();
					}
					ciCount--;
					if(ciCount < 0) //到达流尾没有下一帧了并且fifoCI中数据已处理完
						break;
				}

				if (readCursor == 0) {
					/*
					 * 每解码16帧尝试与另一解码线程(AudioDataConcurrent对象)同步.
					 * 如果以更复杂的方式管理AbstractLayer.pcmbuf,可以取消此处同步.
					 */
					synchronized (pcmbufLock) {
						while (isEndOfBuffer(ch)) {
							//pwt++;
							pcmbufLock.wait(); // 此等待会被另一解码线程在提交解码结果后唤醒
						}
					}
				}

				//2. 完成解码音乐数据的其余4步
				decodeAudioDataChannel(fifoCI[readCursor], ch);

				//3. 每完成4帧提交一次结果(音频输出)...
				/*
				 * 先完成任务的线程负责输出,可减少忙等提高解码速度.对比实测,提升约10%,效果很显著.
				 * 
				 * AbstractLayer.writeCursor内的值可能不同步,可能正被另一解码线程改写.
				 * ---- 初步分析,这不重要...
				 * 
				 * 如果对AbstractLayer.writeCursor内的值同步,只能采用后提交任务的线程负责输出,会增加忙等.
				 */
				if ((readCursor & 3) == 3) {
					wsize = outputPCM();
					if (wsize > 0) {
						synchronized (outputLock) {
							pcmbufCount += 4;
							outputLock.notify();
						}
						synchronized (pcmbufLock) {
							pcmbufLock.notifyAll();
						}
					} else if (wsize < 0) //catch InterruptedException
						break;
				}

				//4. 移位
				readCursor = (readCursor + 1) & 0xf;
			}
			//System.out.printf("[busy waits: outputLock.wait=%d, ch=%d, ciLock.wait=%d, pcmbufLock.wait=%d]\n",owt,ch,cwt,pwt);
		}

		public void run() {
			try {
				decodeAudioDataConcurrent();
			} catch (InterruptedException e) {
				System.out.println("Layer3.AudioDataConcurrent.run: " + e.getMessage());
			}

			synchronized (outputLock) {
				nadc--;
				outputLock.notify();
			}
		}

	}

}