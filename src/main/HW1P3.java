package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.TreeMap;

import javax.swing.JFrame;

import pojo.Book;
import pojo.GraphingData;
import pojo.SimAndBook;
import pojo.WordCount;

public class HW1P3 {

	private static ArrayList<Book> testBooks = new ArrayList<Book>();
	private static ArrayList<Book> trainBooks = new ArrayList<Book>();
	private static final int NUM_CATEGORIES = 5;
	private static int idCount = 1;

	/*
	 * 
	 * id = 2166 ; title: Fifty Shades of Grey: Book One of the Fifty Shades
	 * Trilogy
	 * 
	 * id = 3540 ; title: Brains: A Zombie Memoir
	 */

	public static void main(String[] args) {
		System.out.println("Author: Shuyi Wang\nNetId:sw773");
		trainBooks = readBooksFromFile("books.train");
		testBooks = readBooksFromFile("books.test");
		Book.initDynamicProgramming(idCount + NUM_CATEGORIES);
		System.out.println(trainBooks.size());
		System.out.println(WordCount.maxId);
		partA();
		partB(testBooks);
		int bestK = partC(trainBooks, testBooks);
		partD(bestK);
		partE(5000);
		// System.out.println(kNN(trainBooks,testBooks,1));
	}

	@SuppressWarnings({ "unchecked", "resource" })
	public static ArrayList<Book> readBooksFromFile(String filename) {
		ArrayList<Book> books = new ArrayList<Book>();
		System.out.println("Reading File: " + filename);
		File file = new File(filename);
		File titleFile = new File(filename + ".titles");
		Scanner in = null;
		Scanner titleIn = null;
		Scanner lineTokenizer = null;
		try {
			in = new Scanner(file);
			titleIn = new Scanner(titleFile);
			titleIn.useDelimiter("title-");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (in != null && in.hasNextLine()) {
			lineTokenizer = new Scanner(in.nextLine()).useDelimiter(" |:");
			int category = lineTokenizer.nextInt();
			ArrayList<WordCount> wordCounts = new ArrayList<WordCount>();
			while (in.hasNextInt()) {
				try {
					int wordId = lineTokenizer.nextInt();
					int count = lineTokenizer.nextInt();
					wordCounts.add(new WordCount(wordId, count));
				} catch (Exception e) {
					break;
				}
			}
			Collections.sort(wordCounts);
			Book newBook = new Book(idCount++, category, -1, wordCounts);
			String title = titleIn.next();
			title = title.replace("\n", "").replace("\r", "");
			newBook.setTitle(title);
			books.add(newBook);
			lineTokenizer.close();
		}
		in.close();
		System.out.println("File Read: " + filename);

		return books;
	}

	public static void partA() {
		int idOfFSG = 2166;
		int idOfBrain = 3540;
		Book FSG = null;
		Book Brain = null;
		for (Book book : trainBooks) {
			if (idOfFSG == book.getId()) {
				FSG = book;
			}
			if (idOfBrain == book.getId()) {
				Brain = book;
			}
		}
		PriorityQueue<SimAndBook> pqFSG = new PriorityQueue<SimAndBook>(
				trainBooks.size());
		PriorityQueue<SimAndBook> pqBrain = new PriorityQueue<SimAndBook>(
				trainBooks.size());
		for (Book book : trainBooks) {
			if (book.getId() != idOfFSG) {
				pqFSG.add(new SimAndBook(book.cosineSimilarity(FSG), book));
			}
			if (book.getId() != idOfBrain) {
				pqBrain.add(new SimAndBook(book.cosineSimilarity(Brain), book));
			}
		}
		System.out.println("\n\nBooks similar to " + FSG.getTitle() + "\n");
		for (int i = 0; i < 10; i++) {
			SimAndBook t = pqFSG.poll();
			System.out.println(t.getBook().getTitle() + "   :   "
					+ t.getCosineSimilarity());
		}
		System.out.println("\n\nBooks similar to " + Brain.getTitle());
		for (int i = 0; i < 10; i++) {
			SimAndBook t = pqBrain.poll();
			System.out.println(t.getBook().getTitle() + "   :   "
					+ t.getCosineSimilarity());
		}
		System.out.println("done!");
	}

	public static ArrayList<Book> calculateCentroids(ArrayList<Book> books) {
		ArrayList<ArrayList<Book>> category = new ArrayList<ArrayList<Book>>();
		ArrayList<Book> centroids = new ArrayList<Book>();
		for (int i = 0; i < NUM_CATEGORIES; i++) {
			category.add(new ArrayList<Book>());
		}
		for (Book book : books) {
			if (book.getCategory() < 0 || book.getCategory() >= NUM_CATEGORIES
					|| category.size() != NUM_CATEGORIES) {
				System.err.println("Category does not match assumption");
				return null;
			}
			category.get(book.getCategory()).add(book);
		}
		for (int i = 0; i < NUM_CATEGORIES; i++) {
			TreeMap<Integer, Integer> centroid = new TreeMap<Integer, Integer>();
			for (Book book : category.get(i)) {
				for (WordCount count : book.getWordCounts()) {
					if (!centroid.containsKey(count.getWordId())) {
						centroid.put(count.getWordId(), 0);
					}
					int temp = centroid.get(count.getWordId());
					temp += count.getCount();
					centroid.put(count.getWordId(), temp);
				}
			}

			Book centroidBook = new Book();
			centroidBook.setId(idCount++);
			centroidBook.setCategory(i);
			ArrayList<WordCount> wordCounts = new ArrayList<WordCount>();
			for (Map.Entry<Integer, Integer> entry : centroid.entrySet()) {
				int wordId = entry.getKey();
				int count = entry.getValue();
				wordCounts.add(new WordCount(wordId, count));
			}
			centroidBook.setWordCounts(wordCounts);
			centroids.add(centroidBook);
		}
		return centroids;
	}

	public static void partB(ArrayList<Book> books) {
		long n = 0;
		long correctPrediction = 0;
		long[] nyCorrectPredict = new long[NUM_CATEGORIES];
		long[] nyPredict = new long[NUM_CATEGORIES];
		long[] nyTrue = new long[NUM_CATEGORIES];
		for (int i = 0; i < NUM_CATEGORIES; i++) {
			nyCorrectPredict[i] = 0;
			nyPredict[i] = 0;
			nyTrue[i] = 0;
		}

		ArrayList<Book> centroids = calculateCentroids(books);

		for (Book book : books) {
			book.predictClass(centroids);

			if (book.getWordCounts().size() == 0) {
				continue;
			}

			n++;
			nyPredict[book.getPredictedCategory()]++;
			nyTrue[book.getCategory()]++;
			if (book.getCategory() == book.getPredictedCategory()) {
				correctPrediction++;
				nyCorrectPredict[book.getCategory()]++;
			}
		}

		System.out.println("\n\nBase Line: " + correctPrediction + "  out of "
				+ n + "; equals to " + (100.0 * correctPrediction / n));
		for (int i = 0; i < NUM_CATEGORIES; i++) {
			System.out.println("\nFor category " + i + ": Precision:"
					+ (100.0 * nyCorrectPredict[i] / nyPredict[i]));
			System.out.println("Recall:"
					+ (100.0 * nyCorrectPredict[i] / nyTrue[i]));
		}
	}

	public static int partC(ArrayList<Book> trainBooks,
			ArrayList<Book> testBooks) {
		int[] k = { 1, 2, 5, 10, 100, 200, 300, 500, 1000, 2000, 3000, 4000,
				5000 };
		int[] accuracyList = new int[k.length];
		int bestK = -1;
		double bestAccuracy = -1.0;
		for (int i = 0; i < k.length; i++) {
			double accuracy = kNN(trainBooks, testBooks, k[i]);
			accuracyList[i] = (int) accuracy;
			if (accuracy > bestAccuracy) {
				bestAccuracy = accuracy;
				bestK = i;
			}
			System.out.print("\nWhen k = " + k[i] + ": Accuracy is ");
			System.out.println(accuracy);
		}
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(new GraphingData(accuracyList));
		f.setSize(400, 400);
		f.setLocation(200, 200);
		f.setVisible(true);
		return bestK;
	}

	public static double kNN(ArrayList<Book> trainBooks,
			ArrayList<Book> testBooks, int k) {
		ArrayList<Book> books = new ArrayList<Book>();
		books.addAll(trainBooks);
		books.addAll(testBooks);

		int correctPrediction = 0;
		int n = 0;

		for (Book book : books) {
			PriorityQueue<SimAndBook> priorityQueue = new PriorityQueue<SimAndBook>();
			for (Book trainBook : books) {
				if (book.getId() == trainBook.getId()) {
					continue;
				}
				priorityQueue.add(new SimAndBook(book
						.cosineSimilarity(trainBook), trainBook));
			}
			int[] count = new int[NUM_CATEGORIES];
			for (int i = 0; i < NUM_CATEGORIES; i++)
				count[i] = 0;
			for (int i = 0; i < k; i++) {
				if(priorityQueue.isEmpty()){
					break;
				}
				SimAndBook t = priorityQueue.poll();
				count[t.getBook().getCategory()]++;
			}
			int max = -1;
			int prediction = -1;
			for (int i = 0; i < NUM_CATEGORIES; i++) {
				if (count[i] > max) {
					prediction = i;
					max = count[i];
				}
			}
			if (prediction == -1)
				System.err.println("error");
			book.setPredictedCategory(prediction);

			if (n % 1000 == 0)
				System.out.println(n);
			n++;
			if (book.getCategory() == book.getPredictedCategory()) {
				correctPrediction++;
			}
		}
		double accuracy = 100.0 * correctPrediction / n;
		return accuracy;
	}
	
	public static void partD(int bestK){
		kNN(trainBooks, testBooks, bestK);
		System.out.println("\n\n Best accuracy occurs when k = "+bestK);
		getPrecisionAndRecall();
	}
	
	private static void getPrecisionAndRecall(){
		double[] precisionAndRecall = new double[2*NUM_CATEGORIES];
		
		ArrayList<Book> books = new ArrayList<Book>();
		books.addAll(trainBooks);
		books.addAll(testBooks);
		
		long n = 0;
		long correctPrediction = 0;
		long[] nyCorrectPredict = new long[NUM_CATEGORIES];
		long[] nyPredict = new long[NUM_CATEGORIES];
		long[] nyTrue = new long[NUM_CATEGORIES];
		for (int i = 0; i < NUM_CATEGORIES; i++) {
			nyCorrectPredict[i] = 0;
			nyPredict[i] = 0;
			nyTrue[i] = 0;
		}

		for (Book book : books) {

			if (book.getWordCounts().size() == 0) {
				continue;
			}

			n++;
			nyPredict[book.getPredictedCategory()]++;
			nyTrue[book.getCategory()]++;
			if (book.getCategory() == book.getPredictedCategory()) {
				correctPrediction++;
				nyCorrectPredict[book.getCategory()]++;
			}
		}

		System.out.println("\n\nBase Line: " + correctPrediction + "  out of "
				+ n + "; equals to " + (100.0 * correctPrediction / n));
		for (int i = 0; i < NUM_CATEGORIES; i++) {
			System.out.println("\nFor category " + i + ": Precision:"
					+ (100.0 * nyCorrectPredict[i] / nyPredict[i]));
			System.out.println("Recall:"
					+ (100.0 * nyCorrectPredict[i] / nyTrue[i]));
		}
		
		
	}
	
	public static void partE(int k){
		partD(k);
	}

}
