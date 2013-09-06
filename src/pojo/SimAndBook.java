package pojo;

public class SimAndBook implements Comparable{
	private double cosineSimilarity;
	private Book book;
	public SimAndBook(double cosineSimilarity, Book book) {
		super();
		this.cosineSimilarity = cosineSimilarity;
		this.book = book;
	}
	public double getCosineSimilarity() {
		return cosineSimilarity;
	}
	public void setCosineSimilarity(double cosineSimilarity) {
		this.cosineSimilarity = cosineSimilarity;
	}
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return this.getCosineSimilarity() - ((SimAndBook)o).getCosineSimilarity()>0?-1:1;
	}
	
}
