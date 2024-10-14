import java.io.*;
import java.net.*;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server 
{
    // Main method to start the server
    public static void main(String[] args)
    {
        // Create a thread pool with 20 threads
        ExecutorService executor = Executors.newFixedThreadPool(20);
        try {
            // Create a ServerSocket to listen on port 9145
            ServerSocket serverSocket = new ServerSocket(9145);
            System.out.println("Server listening on port " + 9145);
            // Continuously accept incoming client connections
            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept connection
                System.out.println("Connected to client");
                // Execute a client handler in a separate thread
                executor.execute(new ClientHandler1(clientSocket));
            }
        } catch (IOException err) { 
            System.out.println(err.getMessage()); // Print error message if an IOException occurs
        } finally {
            executor.shutdown(); // Shutdown the thread pool when done
        }
    }

    // Method to log client requests
    public static void log(String IP, String request) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("log.txt", true))) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy|HH:mm:ss");
            String[] args = request.split(" ");
            writer.println(dateFormat.format(new Date()) + "|" + IP + "|" + args[0]);
        } catch (IOException err) { 
            System.out.println(err.getMessage()); // Print error message if an IOException occurs
        }
    }

    // Method to upload files from client to server
    public static void uploadFile(DataInputStream inputStream, String fileName) {
        File outputFile = new File("serverFiles/" + fileName);
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[8192];
            int bytes;
            // Read bytes from input stream and write to file
            while ((bytes = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytes);
            }
        } catch (IOException err) {
            System.out.println(err.getMessage()); // Print error message if an IOException occurs
        }
    }

    // Method to list files in server directory
    public static String listFiles() {
        File folder = new File("serverFiles/");
        File[] files = folder.listFiles();
        
        int count = 0; 
        StringBuilder filesList = new StringBuilder();

        // Iterate through files and append their names to a string
        for (int i = 0; i < files.length; i++) {
            filesList.append(files[i].getName()).append("\n");
            count++;
        }
        // Return formatted list of files
        return "Listing " + count + " file(s): \n" + filesList.toString();
    }

    // ClientHandler1 class to handle each client connection in a separate thread
    private static class ClientHandler1 implements Runnable {
        private Socket clientSocket;

        // Constructor to initialize client socket
        public ClientHandler1(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                InetAddress Address = clientSocket.getInetAddress();
                String IP = Address.getHostAddress();
                // Create input and output streams for communication with client
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                // Read client request
                String request = reader.readLine();
                // Log client request
                log(IP, request);
                // Process client request based on command
                switch(request.split(" ")[0]) {
                    case "list":
                        // Send list of files to client
                        writer.println(listFiles());
                        break;
                    
                    case "put":
                        String[] args = request.split(" ");
                        File file = new File("serverFiles/" + args[1]);
                        // Check if file already exists on server
                        if (file.exists()) {
                            writer.println("Error");
                        } else {
                            writer.println("Uploaded file " + args[1]);
                            // Upload file from client to server
                            uploadFile(new DataInputStream(clientSocket.getInputStream()), args[1]);
                        }
                        break;
                    
                    default:
                        break;
                }
                // Close client socket
                clientSocket.close();
            } catch (IOException err) {
                System.out.println(err.getMessage()); // Print error message if an IOException occurs
            }
        }
    }
}
