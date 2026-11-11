public class Main {
    public static void main(String[] args) {
        String dataDir = (args.length > 0 && args[0] != null && !args[0].trim().isEmpty())
                ? args[0].trim()
                : "data";
        ConsoleMenu menu = new ConsoleMenu(dataDir);
        menu.run();
    }
}
