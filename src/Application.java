public class Application {
	public static void main(String[] args) {
		View view = new View();
		Model model = new Model();
		Controller controller = new Controller(view, model);
	}
}

// To do: change pawn to another piece if it gets to the end