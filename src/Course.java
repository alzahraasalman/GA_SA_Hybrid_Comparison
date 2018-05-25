import java.util.List;
import java.util.ArrayList;

/*
 * Course containing ID and set of events
 */
public class Course {
  private static int nextId = 0;

	private String name;
	private int id;
	private int numLectures;
	private int numLessons;
	private int numLabs;

	/*
	 * Course class constructor
	 * @param id Course ID
	 */
	public Course(String name, int numLectures, int numLessons, int numLabs) {
		this.name = name;
		this.numLectures = numLectures;
		this.numLessons = numLessons;
		this.numLabs = numLabs;
		id = nextId++;		
	}

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

	public int getNumLectures() {
		return numLectures;
	}

	public int getNumLessons() {
		return numLessons;
	}

	public int getNumLabs() {
		return numLabs;
	}
	
	public void resetId() {
	  nextId = 0;
	}
}
