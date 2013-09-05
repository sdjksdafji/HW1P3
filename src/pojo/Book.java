package pojo;

import java.util.ArrayList;

public class Book {
	private int category;
	private int predictedCategory;
	private ArrayList<WordCount> wordCounts;

	public Book() {
		category = -1;
		predictedCategory = -1;
		wordCounts = new ArrayList<WordCount>();
	}

	public Book(int category, int predictedCategory,
			ArrayList<WordCount> wordCounts) {
		super();
		this.category = category;
		this.predictedCategory = predictedCategory;
		this.wordCounts = wordCounts;
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

}
