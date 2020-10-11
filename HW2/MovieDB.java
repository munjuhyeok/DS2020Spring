import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Genre, Title 을 관리하는 영화 데이터베이스.
 * 
 * MyLinkedList 를 사용해 각각 Genre와 Title에 따라 내부적으로 정렬된 상태를  
 * 유지하는 데이터베이스이다. 
 */
public class MovieDB {
	Node<Genre> head;
	
    public MovieDB() {
    	head = new Node<Genre>(null);
    }

	private void addtitle(Genre genre, String title) {
		Node<String> last = genre;
		Node<String> temp = genre;
		if (genre.getNext() != null) {
			last = last.getNext();
		while (last != null && last.getItem().compareTo(title)<0) {
			temp = last;
			last = last.getNext();
			}
		if (last != null && last.getItem().equals(title)) return;
		}
		temp.insertNext(title);
	}
	
	private void removetitle(Genre genre, String title) {
		Node<String> last = genre;
		Node<String> temp = genre;
		if (genre.getNext() != null) {
			last = last.getNext();
		while (last != null && last.getItem().compareTo(title)<0) {
			temp = last;
			last = last.getNext();
			}
		if (last != null && last.getItem().equals(title)) {
			temp.removeNext();
		}
		}	
	}
    
    public void insert(MovieDBItem item) {
    	Genre newgenre = new Genre(item.getGenre());
    	String newtitle = item.getTitle();
		Node<Genre> last = head;
		Node<Genre> temp = head;
		if (head.getNext() !=null ) {
			last = last.getNext();
		while (last != null && last.getItem().compareTo(newgenre)<0) {
			temp = last;
			last = last.getNext();
			}
		if (last != null && last.getItem().equals(newgenre)) {
			newgenre = last.getItem();
			addtitle(newgenre, newtitle);
			last.setItem(newgenre);
			return;
		}
		}
		temp.insertNext(newgenre);
    	addtitle(newgenre, newtitle);
    	// Printing functionality is provided for the sake of debugging.
        // This code should be removed before submitting your work.
        //System.err.printf("[trace] MovieDB: INSERT [%s] [%s]\n", item.getGenre(), item.getTitle());
    }

    public void delete(MovieDBItem item) {
    	Genre newgenre = new Genre(item.getGenre());
    	String newtitle = item.getTitle();
		Node<Genre> last = head;
		Node<Genre> temp = head;
		if (head.getNext() !=null ) {
			last = last.getNext();
		while (last != null && last.getItem().compareTo(newgenre)<0) {
			temp = last;
			last = last.getNext();
			}
		if (last != null && last.getItem().equals(newgenre)) {
			newgenre = last.getItem();
			removetitle(newgenre, newtitle);
			last.setItem(newgenre);
			if (newgenre.getNext() == null) {
				temp.removeNext();
			}
		}
		}
    }
    	

    public MyLinkedList<MovieDBItem> search(String term) {
        // FIXME implement this
        // Search the given term from the MovieDB.
        // You should return a linked list of MovieDBItem.
        // The search command is handled at SearchCmd class.

        MyLinkedList<MovieDBItem> results = new MyLinkedList<MovieDBItem>();
    	
        Node<Genre> last = head;
        Node<String> lasttitle;
        String genre;
        
        while(last.getNext() != null) {
        	last = last.getNext();
        	lasttitle = last.getItem();
        	genre = lasttitle.getItem();
        	while(lasttitle.getNext() != null) {
        		lasttitle = lasttitle.getNext();
            	if(lasttitle.getItem().contains(term)) {
            		MovieDBItem newMovieDBItem = new MovieDBItem(genre, lasttitle.getItem());
            		results.add(newMovieDBItem);
            	}
        	}

        }
    	
    	// Printing search results is the responsibility of SearchCmd class. 
    	// So you must not use System.out in this method to achieve specs of the assignment.
    	
        // This tracing functionality is provided for the sake of debugging.
        // This code should be removed before submitting your work.
    	//System.err.printf("[trace] MovieDB: SEARCH [%s]\n", term);
    	
    	// FIXME remove this code and return an appropriate MyLinkedList<MovieDBItem> instance.
    	// This code is supplied for avoiding compilation error.   

        return results;
    }
    
    public MyLinkedList<MovieDBItem> items() {
        // FIXME implement this
        // Search the given term from the MovieDatabase.
        // You should return a linked list of QueryResult.
        // The print command is handled at PrintCmd class.
        MyLinkedList<MovieDBItem> results = new MyLinkedList<MovieDBItem>();
    	
        Node<Genre> last = head;
        Node<String> lasttitle;
        String genre;
        
        while(last.getNext() != null) {
        	last = last.getNext();
        	lasttitle = last.getItem();
        	genre = lasttitle.getItem();
        	while(lasttitle.getNext() != null) {
        		lasttitle = lasttitle.getNext();
        		MovieDBItem newMovieDBItem = new MovieDBItem(genre, lasttitle.getItem());
        		results.add(newMovieDBItem);
        	}

        }
    	
    	// Printing movie items is the responsibility of PrintCmd class. 
    	// So you must not use System.out in this method to achieve specs of the assignment.

    	// Printing functionality is provided for the sake of debugging.
        // This code should be removed before submitting your work.
        //System.err.printf("[trace] MovieDB: ITEMS\n");

    	// FIXME remove this code and return an appropriate MyLinkedList<MovieDBItem> instance.
    	// This code is supplied for avoiding compilation error.   
        
    	return results;
    }
}

class Genre extends Node<String> implements Comparable<Genre> {
	public Genre(String name) {
		super(name);
	}
	
	@Override
	public int compareTo(Genre o) {
		return this.getItem().compareTo(o.getItem());
	}

	@Override
	public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getItem() == null) ? 0 : getItem().hashCode());
        return result;
	}

	@Override
	public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Genre other = (Genre) obj;
        if (getItem() == null) {
            if (other.getItem() != null)
                return false;
        } else if (!getItem().equals(other.getItem()))
            return false;
        return true;
	}
}

class MovieList implements ListInterface<String> {	
	Node<String> head;
	int numItems;
	
	public MovieList() {
		head = new Node<String>(null);
	}

	@Override
	public Iterator<String> iterator() {
		return new MovieListIterator(this);
	}

	@Override
	public boolean isEmpty() {
		return head.getNext() == null;
	}

	@Override
	public int size() {
		return numItems;
	}

	@Override
	public void add(String item) {
		Node<String> last = head;
		Node<String> temp = head;
		if (!isEmpty() ) {
			last = last.getNext();
		while (last != null && last.getItem().compareTo(item)<0) {
			temp = last;
			last = last.getNext();
			}
		if (last != null && last.getItem().compareTo(item)==0) return;
		}
		temp.insertNext(item);
		numItems += 1;
	}

	public void remove(String item) {
		Node<String> last = head;
		Node<String> temp = head;
		if (!isEmpty() ) {
			last = last.getNext();
		while (last != null && last.getItem().compareTo(item)<0) {
			temp = last;
			last = last.getNext();
			}
		if (last != null && last.getItem().compareTo(item)==0) {
			temp.removeNext();
			numItems -= 1;
		}
		}
	}
	
	@Override
	public String first() {
		return head.getNext().getItem();
	}

	@Override
	public void removeAll() {
		head.setNext(null);
		throw new UnsupportedOperationException("not implemented yet");
	}
}

class MovieListIterator implements Iterator<String> {
	private MovieList list;
	private Node<String> curr;
	private Node<String> prev;

	public MovieListIterator(MovieList list) {
		this.list = list;
		this.curr = list.head;
		this.prev = null;
	}

	@Override
	public boolean hasNext() {
		return curr.getNext() != null;
	}

	@Override
	public String next() {
		if (!hasNext())
			throw new NoSuchElementException();

		prev = curr;
		curr = curr.getNext();

		return curr.getItem();
	}

	@Override
	public void remove() {
		if (prev == null)
			throw new IllegalStateException("next() should be called first");
		if (curr == null)
			throw new NoSuchElementException();
		prev.removeNext();
		list.numItems -= 1;
		curr = prev;
		prev = null;
	}
}