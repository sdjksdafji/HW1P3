package pojo;

public class WordCount implements Comparable {
	public static int maxId = 0;
	private int wordId;
	private int count;

	public WordCount(int wordId, int count) {
		super();
		this.wordId = wordId;
		if (wordId > maxId)
			maxId = wordId;
		this.count = count;
	}

	public int getWordId() {
		return wordId;
	}

	public void setWordId(int wordId) {
		this.wordId = wordId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public int compareTo(Object o) {
		return this.wordId - ((WordCount) o).wordId;
	}

}
