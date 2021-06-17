package jmp123.demo;

import java.io.IOException;

import jmp123.output.Audio;

/**
 * 控制台命令行播放器。演示解码器调用方法。
 * @version 0.400
 */
public class Play {

	/**
	 * 播放器程序的入口。
	 * @param args 命令行参数，是一个本地MP3文件名。
	 * @throws IOException 发生I/O错误时。
	 */
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.out.println("Please specify a valid filename.");
			return;
		}
		MiniPlayer player = new MiniPlayer(new Audio());

		String msg = player.open(args[0]);
		System.out.println(msg);

		player.run();
	}

}
