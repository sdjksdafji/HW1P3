package pojo;

public class WordCount {
	private int wordId;
	private int count;

	public WordCount(int wordId, int count) {
		super();
		this.wordId = wordId;
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

}
