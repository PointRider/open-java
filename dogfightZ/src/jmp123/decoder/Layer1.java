/*
* Layer1.java -- MPEG-1 Audio Layer I 解码
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
 * 解码Layer Ⅰ。
 * @version 0.400
 */
public class Layer1 extends AbstractLayer {
	Header header;
	BitStream bs;
	float[] factor;
	byte[][] allocation;	//[2][32]
	byte[][] scalefactor;	//[2][32]
	float[][] syin;			//[2][32]

	/**
	 * 创建一个指定头信息和音频输出的LayerⅠ帧解码器。
	 * 
	 * @param h
	 *            已经解码的帧头信息。
	 * @param audio
	 *            音频输出对象。
	 */
	public Layer1(Header h, IAudio audio) {
		super(h, audio);
		header = h;
		
		bs = new BitStream(4096, 512);
		allocation = new byte[2][32];
		scalefactor = new byte[2][32];
		syin = new float[2][32];
		factor = Layer2.factor; // ISO/IEC 11172-3 Table 3-B.1.
	}

	/*
	 * 逆量化公式:
	 * s'' = (2^nb / (2^nb - 1)) * (s''' + 2^(-nb + 1))
	 * s' = factor * s''
	 */
	private float requantization(int ch, int sb, int nb) {
		int samplecode = bs.getBits17(nb);
		int nlevels = (1 << nb);
		float requ = 2.0f * samplecode / nlevels - 1.0f;	//s'''
		requ += (float)Math.pow(2, 1-nb);
		requ *= nlevels / (nlevels - 1);		//s''
		requ *= factor[scalefactor[ch][sb]];	//s'
		return requ;
	}

	public int decodeAudioData(byte[] b, int off) {
		int sb, gr, ch, nb;
		int nch = header.getChannels();
		int bound = (header.getMode() == 1) ? ((header.getModeExtension() + 1) * 4) : 32;
		int intMainDataBytes = header.getMainDataSize();
		if(bs.append(b,off,intMainDataBytes) < intMainDataBytes)
			return -1;
		off += intMainDataBytes;
		int maindata_begin = bs.getBytePos();

		//1. Bit allocation decoding
		for (sb = 0; sb < bound; sb++)
			for (ch = 0; ch < nch; ++ch) {
				nb = bs.getBits9(4);
				if (nb == 15)
					return -2;
				allocation[ch][sb] = (byte)((nb != 0) ? (nb + 1) : 0);
			}
		for (sb = bound; sb < 32; sb++) {
			nb = bs.getBits9(4);
			if (nb == 15)
				return -2;
			allocation[0][sb] = (byte)((nb != 0) ? (nb + 1) : 0);
		}

		//2. Scalefactor decoding
		for (sb = 0; sb < 32; sb++)
			for (ch = 0; ch < nch; ch++)
				if (allocation[ch][sb] != 0)
					scalefactor[ch][sb] = (byte)bs.getBits9(6);

		for (gr = 0; gr < 12; gr++) {
			//3. Requantization of subband samples
			for (sb = 0; sb < bound; sb++)
				for (ch = 0; ch < nch; ch++){
					nb = allocation[ch][sb];
					if(nb == 0)
						syin[ch][sb] = 0;
					else
						syin[ch][sb] = requantization(ch, sb, nb);
				}
			//mode=1(Joint Stereo)
			for (sb = bound; sb < 32; sb++)
				if ((nb = allocation[0][sb]) != 0)
					for (ch = 0; ch < nch; ch++)
						syin[ch][sb] = requantization(ch, sb, nb);
				else
					for (ch = 0; ch < nch; ch++)
						syin[ch][sb] = 0;
			
			//4. Synthesis subband filter
			for (ch = 0; ch < nch; ch++)
				super.synthesisSubBand(syin[ch], ch);
		}

		//5. Ancillary bits
		int discard = intMainDataBytes + maindata_begin - bs.getBytePos();
		bs.skipBytes(discard);

		//6. output
		super.outputPCM();

		return off;
	}

}
