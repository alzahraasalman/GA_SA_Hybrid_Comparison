public class Room {
	protected static int nextId = 0;
	protected String name;
	protected int id;
	protected int capacity;
	protected Event.Type type;

	public Room(String name, int capacity, Event.Type type) {
		this.name = name;
		this.capacity = capacity;
		this.type = type;
		id = nextId++;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public int getCapacity() {
		return capacity;
	}

	public Event.Type getType() {
		return type;
	}
	
	public static void resetId() {
	  nextId = 0;
	}
}
