/**
 * Copyright 2014 SAP AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lpe.common.util.csvrandomizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Utility class for randomizing originally sequential CSV-Files.
 * 
 * @author Jonas Kunz
 * 
 */
public final class CSVRandomizer {

	private static final int ARGS_LANGTH = 4;
	private static final int ARG_0 = 0;
	private static final int ARG_1 = 1;
	private static final int ARG_2 = 2;
	private static final int ARG_3 = 3;

	/**
	 * Private constructor due to utility class.
	 */
	private CSVRandomizer() {
	}

	/**
	 * Main.
	 * 
	 * @param args
	 *            program arguments
	 */
	public static void main(String[] args) {
		if (args.length != ARGS_LANGTH) {
			printUsageAndExit();
		}
		randomizeCSV(new File(args[ARG_0]), new File(args[ARG_1]), Double.parseDouble(args[ARG_2]),
				Integer.parseInt(args[ARG_3]));
		System.out.println("CSV Randomized!");
	}

	private static void printUsageAndExit() {
		System.out.println("Wrong parameters. Usage:");
		System.out.println("[srcFile] [destFile] [scaleFacotr] [randomseed]");
		System.exit(0);
	}

	/**
	 * Randomizes the given input csv file.
	 * 
	 * @param src
	 *            the source file
	 * @param dest
	 *            the destination file
	 * @param outputSizeMultiplicator
	 *            the output file will contain the number of lines in the input
	 *            file times this factor lines
	 * @param seed
	 *            the seed for the random-generator. Same values produce
	 *            deterministic results.
	 */
	public static void randomizeCSV(File src, File dest, double outputSizeMultiplicator, int seed) {
		ArrayList<String> lines = new ArrayList<String>();
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(src);
			br = new BufferedReader(fr);

			String line = br.readLine();
			while (line != null) {
				lines.add(line);
				line = br.readLine();
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}

		try {
			dest.createNewFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Random rnd = new Random(seed);

		FileWriter fw = null;
		try {
			fw = new FileWriter(dest);

			int outputLineCount = (int) Math.ceil(outputSizeMultiplicator * lines.size());

			for (int i = 0; i < outputLineCount; i++) {
				int index = rnd.nextInt(lines.size());
				if (i != 0) {
					fw.write('\n');
				}
				fw.write(lines.get(index));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}
