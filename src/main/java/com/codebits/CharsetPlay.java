package com.codebits;

import java.nio.charset.Charset;

public class CharsetPlay {

	public static void main(String[] args) {
	  final String charsetName = System.setProperty("file.encoding", "UTF-8");
	  final Charset charset = Charset.forName(charsetName);
	  System.out.println("C: " + charset);
	}

}
