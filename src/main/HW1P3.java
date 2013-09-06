package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.TreeMap;

import pojo.Book;
import pojo.SimAndBook;
import pojo.WordCount;

public class HW1P3 {

	private static ArrayList<Book> testBooks = new ArrayList<Book>();
	private static ArrayList<Book> trainBooks = new ArrayList<Book>();
	private static final int NUM_CATEGORIES = 5;

	/*
	 * 
	 * id = 2166 ; title: Fifty Shades of Grey: Book One of the Fifty Shades
	 * Trilogy
	 * 
	 * id = 3540 ; title: Brains: A Zombie Memoir
	 */

	public static void main(String[] args) {
		System.out.println("Author: Shuyi Wang\nNetId:sw773");
		// testBooks = readBooksFromFile("books.test");
		trainBooks = readBooksFromFile("books.train");
		// System.out.println(testBooks.size());
		System.out.println(trainBooks.size());
		System.out.println(WordCount.maxId);
		//partA();
		ArrayList<Book> centroids = calculateCentroids(trainBooks);
		System.out.println(centroids.size());
	}

	@SuppressWarnings({ "unchecked", "resource" })
	public static ArrayList<Book> readBooksFromFile(String filename) {
		ArrayList<Book> books = new ArrayList<Book>();
		System.out.println("Reading File: " + filename);
		File file = new File(filename);
		File titleFile = new File(filename+".titles");
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
		int id = 1;
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
			Book newBook = new Book(id, category, -1, wordCounts);
			String title = titleIn.next();
			title = title.replace("\n", "").replace("\r", "");
			newBook.setTitle(title);
			books.add(newBook);
			lineTokenizer.close();
			id++;
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
		System.out.println("\n\nBooks similar to "+FSG.getTitle()+"\n");
		for(int i=0;i<10;i++){
			SimAndBook t =pqFSG.poll();
			System.out.println(t.getBook().getTitle()+"   :   "+t.getCosineSimilarity());
		}
		System.out.println("\n\nBooks similar to "+Brain.getTitle());
		for(int i=0;i<10;i++){
			SimAndBook t =pqBrain.poll();
			System.out.println(t.getBook().getTitle()+"   :   "+t.getCosineSimilarity());
		}
		System.out.println("done!");
	}
	
	public static ArrayList<Book> calculateCentroids(ArrayList<Book> books){
		ArrayList<ArrayList<Book>> category = new ArrayList<ArrayList<Book>> ();
		ArrayList<Book> centroids = new ArrayList<Book>();
		for(int i=0;i<NUM_CATEGORIES;i++){
			category.add(new ArrayList<Book>()); 
		}
		for(Book book:books){
			if(book.getCategory()<0 || book.getCategory()>=NUM_CATEGORIES || category.size()!=NUM_CATEGORIES){
				System.err.println("Category does not match assumption");
				return null;
			}
			category.get(book.getCategory()).add(book);
		}
		for(int i=0;i<NUM_CATEGORIES;i++){
			TreeMap<Integer,Integer> centroid = new TreeMap<Integer,Integer>();
			for(Book book:category.get(i)){
				for(WordCount count:book.getWordCounts()){
					if(!centroid.containsKey(count.getWordId())){
						centroid.put(count.getWordId(), 0);
					}
					int temp = centroid.get(count.getWordId());
					temp+=count.getCount();
					centroid.put(count.getWordId(), temp);
				}
			}
			
			Book centroidBook = new Book();
			centroidBook.setCategory(i);
			ArrayList<WordCount> wordCounts = new ArrayList<WordCount>();
			for (Map.Entry<Integer, Integer> entry : centroid.entrySet()){
				int wordId = entry.getKey();
				int count = entry.getValue();
				wordCounts.add(new WordCount(wordId,count));
			}
			centroidBook.setWordCounts(wordCounts);
			centroids.add(centroidBook);
		}
		return centroids;
	}

}
