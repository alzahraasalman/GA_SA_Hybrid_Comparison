import java.util.List;
import java.util.ArrayList;

public class Lecturer {
  private static int nextId = 0;

  private String name;
  private int id;
	private List<Course> courses;

  public Lecturer(String name) {
		this.name = name;
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

	public List<Course> getCourses() {
		return courses;
	}

  public boolean canTeach(Course course) {
    for (Course c : courses) {
      if (c.getName().equals(course.getName()))
        return true;
    }

    return false;
  }
  
  public static void resetId() {
    nextId = 0;
  }
}
