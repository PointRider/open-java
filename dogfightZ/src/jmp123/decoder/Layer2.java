/*
* Layer2.java -- MPEG-1/2/2.5 Audio Layer II 解码
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
* so by contacting the author: <http://jmp123.sf.net/>
*/
package jmp123.decoder;

/**
 * 解码Layer Ⅱ。
 * @version 0.400
 */
public final class Layer2 extends AbstractLayer {
	private Header header;
	private BitStream bs;
	private int channels, aidx, sblimit;
	private byte[][][] aidx_table;	//[3][2][16]
	private byte[][] allocation;	//[2][32]
	private byte[][] nbal;			//[5][]
	private byte[][] sbquant_offset;//[5][]
	private byte[][] scfsi;			//[2][32]
	private byte[][][] scalefactor;	//[2][32][3]
	private int[] cq_steps;			//[17]
	private float[] cq_C;			//[17]
	private float[] cq_D;			//[17]
	private byte[] cq_bits;			//[17]
	private byte[] bitalloc_offset;	//[8]
	private byte[][] offset_table;	//[6][15]
	private byte[] group;			//[17]
	private int[] samplecode;		//[3]
	private float[][][] syin;		//[2][3][32]

	// Layer1也用到factor[]
	// ISO/IEC 11172-3 Table 3-B.1
	// scalefactor值为'0000 00'..'1111 11'(0..63),应该有64个值.在末尾补一个数0.0f
	public static final float[] factor = { 
		2.00000000000000f, 1.58740105196820f, 1.25992104989487f, 1.00000000000000f,
		0.79370052598410f, 0.62996052494744f, 0.50000000000000f, 0.39685026299205f,
		0.31498026247372f, 0.25000000000000f, 0.19842513149602f, 0.15749013123686f,
		0.12500000000000f, 0.09921256574801f, 0.07874506561843f, 0.06250000000000f,
		0.04960628287401f, 0.03937253280921f, 0.03125000000000f, 0.02480314143700f,
		0.01968626640461f, 0.01562500000000f, 0.01240157071850f, 0.00984313320230f,
		0.00781250000000f, 0.00620078535925f, 0.00492156660115f, 0.00390625000000f,
		0.00310039267963f, 0.00246078330058f, 0.00195312500000f, 0.00155019633981f,
		0.00123039165029f, 0.00097656250000f, 0.00077509816991f, 0.00061519582514f,
		0.00048828125000f, 0.00038754908495f, 0.00030759791257f, 0.00024414062500f,
		0.00019377454248f, 0.00015379895629f, 0.00012207031250f, 0.00009688727124f,
		0.00007689947814f, 0.00006103515625f, 0.00004844363562f, 0.00003844973907f,
		0.00003051757813f, 0.00002422181781f, 0.00001922486954f, 0.00001525878906f,
		0.00001211090890f, 0.00000961243477f, 0.00000762939453f, 0.00000605545445f,
		0.00000480621738f, 0.00000381469727f, 0.00000302772723f, 0.00000240310869f,
		0.00000190734863f, 0.00000151386361f, 0.00000120155435f, 0.0f};

	/**
	 * 创建一个指定头信息和音频输出的LayerⅡ帧解码器。
	 * 
	 * @param h
	 *            已经解码的帧头信息。
	 * @param audio
	 *            音频输出对象。
	 */
	public Layer2(Header h, IAudio audio) {
		super(h, audio);
		header = h;

		channels = header.getChannels();
		bs = new BitStream(4096, 512);

		nbal = new byte[5][];
		// ISO/IEC 11172-3 Table 3-B.2a
		nbal[0] = new byte[] { 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3,
				3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2 };

		// ISO/IEC 11172-3 Table 3-B.2b
		nbal[1] = new byte[] { 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3,
				3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2 };

		// ISO/IEC 11172-3 Table 3-B.2c
		nbal[2] = new byte[] { 4, 4, 3, 3, 3, 3, 3, 3 };

		// ISO/IEC 11172-3 Table 3-B.2d
		nbal[3] = new byte[] { 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 };

		// ISO/IEC 13818-3 Table B.1
		nbal[4] = new byte[] { 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2,
				2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 };
		
		aidx_table = new byte[][][] {
			{{0,2,2,2,2,2,2,0,0,0,1,1,1,1,1,0},{0,2,2,0,0,0,1,1,1,1,1,1,1,1,1,0 }},
			{{0,2,2,2,2,2,2,0,0,0,0,0,0,0,0,0},{0,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0 }},
			{{0,3,3,3,3,3,3,0,0,0,1,1,1,1,1,0},{0,3,3,0,0,0,1,1,1,1,1,1,1,1,1,0 }}};

		// cq_xxx: Layer II classes of quantization, ISO/IEC 11172-3 Table 3-B.4
		cq_steps = new int[] { 3, 5, 7, 9, 15, 31, 63, 127, 255, 511, 1023,
				2047, 4095, 8191, 16383, 32767, 65535 };
		cq_C = new float[] { 1.3333333f, 1.6f, 1.1428571f, 1.77777778f,
				1.0666667f, 1.0322581f, 1.015873f, 1.007874f, 1.0039216f,
				1.0019569f, 1.0009775f, 1.0004885f, 1.0002442f, 1.000122f,
				1.000061f, 1.0000305f, 1.00001525902f };
		cq_D = new float[] { 0.5f, 0.5f, 0.25f, 0.5f, 0.125f, 0.0625f,
				0.03125f, 0.015625f, 0.0078125f, 0.00390625f, 0.001953125f,
				0.0009765625f, 0.00048828125f, 0.00024414063f, 0.00012207031f,
				0.00006103516f, 0.00003051758f };
		cq_bits = new byte[] {5,7,3,10,4,5,6,7,8,9,10,11,12,13,14,15,16};

		sbquant_offset = new byte[][] {
			// ISO/IEC 11172-3 Table 3-B.2a
			{7,7,7,6,6,6,6,6,6,6,6,3,3,3,3,3,3,3,3,3,	3,3,3,0,0,0,0},

			// ISO/IEC 11172-3 Table 3-B.2b
			{7,7,7,6,6,6,6,6,6,6,6,3,3,3,3,3,3,3,3,3,	3,3,3,0,0,0,0,0,0,0},

			// ISO/IEC 11172-3 Table 3-B.2c
			{5,5,2,2,2,2,2,2},

			// ISO/IEC 11172-3 Table 3-B.2d
			{5,5,2,2,2,2,2,2,2,2,2,2},

			// ISO/IEC 13818-3 Table B.1
			{4,4,4,4,2,2,2,2,2,2,2,1,1,1,1,1,1,1,1,1,	1,1,1,1,1,1,1,1,1,1} };

		bitalloc_offset = new byte[] { 0, 3, 3, 1, 2, 3, 4, 5 };
		offset_table = new byte[][] { { 0, 1, 16 }, { 0, 1, 2, 3, 4, 5, 16 },
				{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 },
				{ 0, 1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 },
				{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 16 },
				{ 0, 2, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 } };
		group = new byte[] { 2, 3, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

		allocation = new byte[2][32];
		scfsi = new byte[2][32];
		scalefactor = new byte[2][32][3];
		samplecode = new int[3];
		syin = new float[2][3][32];

		//aidx,sblimit...
		if(header.getVersion() == Header.MPEG2) {
			aidx = 4;
			sblimit = 30;
		} else {
			aidx = aidx_table[header.getSamplingFrequency()][2-channels][header.getBitrateIndex()];
			if(aidx == 0) sblimit = 27;
			else if(aidx == 1) sblimit = 30;
			else if(aidx == 2) sblimit = 8;
			else sblimit = 12;
		}
	}

	private void requantization(int index, int gr, int ch, int sb) {
		int nb, s, c;
		int nlevels = cq_steps[index];
		if ((nb = group[index]) != 0) {		// degrouping
			c = bs.getBits17(cq_bits[index]);
			for (s = 0; s < 3; s++) {
				samplecode[s] = c % nlevels;
				c /= nlevels;
			}
			nlevels = (1 << nb) - 1;	//用于计算fractional
		}
		else {
			nb = cq_bits[index];
			for (s = 0; s < 3; s++)
				samplecode[s] = bs.getBits17(nb);
		}

		for (s = 0; s < 3; s++) {
			float fractional = 2.0f * samplecode[s] / (nlevels + 1) - 1.0f;
			// s'' = C * (s''' + D)
			syin[ch][s][sb] = cq_C[index] * (fractional + cq_D[index]);
			// s' = factor * s''
			syin[ch][s][sb] *= factor[scalefactor[ch][sb][gr >> 2]];
		}
	}

	private void stereo(int index, int gr, int sb) {
		int nb, s, c;
		int nlevels = cq_steps[index];
		if ((nb = group[index]) != 0) {
			c = bs.getBits17(cq_bits[index]);
			for (s = 0; s < 3; s++) {
				samplecode[s] = c % nlevels;
				c /= nlevels;
			}
			nlevels = (1 << nb) - 1;
		}
		else {
			nb = cq_bits[index];
			for (s = 0; s < 3; s++)
				samplecode[s] = bs.getBits17(nb);
		}

		for (s = 0; s < 3; s++) {
			float fractional = 2.0f * samplecode[s] / (nlevels + 1) - 1.0f;
			// s'' = C * (s''' + D)
			syin[0][s][sb] = syin[1][s][sb] = cq_C[index] * (fractional + cq_D[index]);
			// s' = factor * s''
			syin[0][s][sb] *= factor[scalefactor[0][sb][gr >> 2]];
			syin[1][s][sb] *= factor[scalefactor[1][sb][gr >> 2]];
		}
	}

	public int decodeAudioData(byte[] b, int off) {
		int maindata_begin ,bound, sb, ch;
		int intMainDataBytes = header.getMainDataSize();
		if (bs.append(b, off, intMainDataBytes) < intMainDataBytes)
			return off + intMainDataBytes; // skip
		off += intMainDataBytes;
		maindata_begin = bs.getBytePos();
		bound = (header.getMode() == 1) ? ((header.getModeExtension() + 1) << 2) : 32;
		if(bound > sblimit)
			bound = sblimit;

		/*
		 * 1. Bit allocation decoding
		 */
		for (sb = 0; sb < bound; sb++)
			for (ch = 0; ch < channels; ch++)
				allocation[ch][sb] = (byte)bs.getBits9(nbal[aidx][sb]); // 2..4 bits
		for (sb = bound; sb < sblimit; sb++)
			allocation[1][sb] = allocation[0][sb] = (byte)bs.getBits9(nbal[aidx][sb]);

		/*
		 * 2. Scalefactor selection information decoding
		 */
		for (sb = 0; sb < sblimit; sb++)
			for (ch = 0; ch < channels; ch++)
				if (allocation[ch][sb] != 0)
					scfsi[ch][sb] = (byte)bs.getBits9(2);
				else
					scfsi[ch][sb] = 0;

		/*
		 * 3. Scalefactor decoding
		 */
		for (sb = 0; sb < sblimit; ++sb)
			for (ch = 0; ch < channels; ++ch)
				if (allocation[ch][sb] != 0) {
					scalefactor[ch][sb][0] = (byte)bs.getBits9(6);
					switch (scfsi[ch][sb]) {
					case 2:
						scalefactor[ch][sb][2] = scalefactor[ch][sb][1] = scalefactor[ch][sb][0];
						break;
					case 0:
						scalefactor[ch][sb][1] = (byte)bs.getBits9(6);
					case 1:
					case 3:
						scalefactor[ch][sb][2] = (byte)bs.getBits9(6);
					}
					if ((scfsi[ch][sb] & 1) == 1)
						scalefactor[ch][sb][1] = scalefactor[ch][sb][scfsi[ch][sb] - 1];
				}

		int gr, index, s;
		for (gr = 0; gr < 12; gr++) {
			/*
			 * 4. Requantization of subband samples
			 */
			for (sb = 0; sb < bound; sb++)
				for (ch = 0; ch < channels; ch++)
					if ((index = allocation[ch][sb]) != 0) {
						index = offset_table[bitalloc_offset[sbquant_offset[aidx][sb]]][index - 1];
						requantization(index, gr, ch, sb);
					} else
						syin[ch][0][sb] = syin[ch][1][sb] = syin[ch][2][sb] = 0;

			//mode=1(Joint Stereo)
			for (sb = bound; sb < sblimit; sb++)
				if ((index = allocation[0][sb]) != 0) {
					index = offset_table[bitalloc_offset[sbquant_offset[aidx][sb]]][index - 1];
					stereo(index, gr, sb);
				} else
					for (ch = 0; ch < channels; ch++)
						syin[ch][0][sb] = syin[ch][1][sb] = syin[ch][2][sb] = 0;
			
			for (ch = 0; ch < channels; ch++)
				for (s = 0; s < 3; s++)
					for (sb = sblimit; sb < 32; sb++)
						syin[ch][s][sb] = 0;

			/*
			 * 5. Synthesis subband filter
			 */
			for (ch = 0; ch < channels; ch++)
				for (s = 0; s < 3; s++)
					super.synthesisSubBand(syin[ch][s], ch);
		} // for(gr...)

		/*
		 * 6. Ancillary bits
		 */
		int discard = intMainDataBytes + maindata_begin - bs.getBytePos();
		bs.skipBytes(discard);

		/*
		 * 7. output
		 */
		super.outputPCM();
		
		return off;
	}

}
