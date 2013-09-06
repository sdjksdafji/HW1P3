package pojo;


import java.util.ArrayList;
import java.util.Iterator;

public class Book {
	private int id;
	private int category;
	private int predictedCategory;
	private ArrayList<WordCount> wordCounts;
	private double euclideanLength;
	private String title;

	public Book() {
		category = -1;
		predictedCategory = -1;
		wordCounts = new ArrayList<WordCount>();
		euclideanLength = -1.0;
	}

	public Book(int id, int category, int predictedCategory,
			ArrayList<WordCount> wordCounts) {
		super();
		this.id = id;
		this.category = category;
		this.predictedCategory = predictedCategory;
		this.wordCounts = wordCounts;
		euclideanLength = -1.0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public int getPredictedCategory() {
		return predictedCategory;
	}

	public void setPredictedCategory(int predictedCategory) {
		this.predictedCategory = predictedCategory;
	}

	public ArrayList<WordCount> getWordCounts() {
		return wordCounts;
	}

	public void setWordCounts(ArrayList<WordCount> wordCounts) {
		this.wordCounts = wordCounts;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getEuclideanLength() {
		if (euclideanLength < 0.0) {
			euclideanLength = 0;
			for (WordCount wordCount : this.wordCounts) {
				euclideanLength += wordCount.getCount() * wordCount.getCount();
			}
			euclideanLength=Math.sqrt(euclideanLength);
		}
		return euclideanLength;
	}

	public double dotProduct(Book book) {
		double dotProduct = 0;
		Iterator<WordCount> x1 = this.getWordCounts().iterator();
		Iterator<WordCount> x2 = book.getWordCounts().iterator();
		WordCount nextX1, nextX2;
		if (!x1.hasNext() || !x2.hasNext()) {
			return dotProduct;
		}
		nextX1 = x1.next();
		nextX2 = x2.next();
		while (true) {
			if (nextX1.getWordId() == nextX2.getWordId()) {
				dotProduct += nextX1.getCount() * nextX2.getCount();
				if (!x1.hasNext() || !x2.hasNext()) {
					return dotProduct;
				} else {
					nextX1 = x1.next();
					nextX2 = x2.next();
				}
			} else if (nextX1.getWordId() > nextX2.getWordId()) {
				if (!x2.hasNext()) {
					return dotProduct;
				} else {
					nextX2 = x2.next();
				}
			} else {
				if (!x1.hasNext()) {
					return dotProduct;
				} else {
					nextX1 = x1.next();
				}
			}
		}
	}

	public double cosineSimilarity(Book book) {//System.out.println(this.dotProduct(book)+"  "+book.getEuclideanLength());
		return this.dotProduct(book)
				/ (this.getEuclideanLength() * book.getEuclideanLength());
	}

}
