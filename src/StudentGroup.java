import java.util.List;
import java.util.ArrayList;

public class StudentGroup {
	private static int nextId = 0;
	private int id;
	private String name;
	private int size;
	private List<Course> courses;

	public StudentGroup(String name, int size) {
		this.name = name;
		this.size = size;
		id = nextId++;
		courses = new ArrayList<Course>();
	}

	public void addCourse(Course course) {
		courses.add(course);
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public int getSize() {
		return size;
	}

	public List<Course> getCourses() {
		return courses;
	}
	
	public static void resetId() {
	  nextId = 0;
	}
}
