import java.io.*;
import java.net.*;

public class Client {

    public static void main(String[] args) {
        if (args.length < 1) {
            // Print an error message if no command is provided
            System.out.println("Error: Please enter the correct command 'java Client <command>'");
            return;
        }
        Client client = new Client();
        client.executeCommand(args); // Execute the provided command
    }

    public void executeCommand(String[] args) {
        try {
            String cmd = args[0]; // Extract the command from arguments
            // Establish connection with the server
            Socket socket = new Socket("localhost", 9145);
            // Create a PrintWriter to send data to the server
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            // Create a BufferedReader to receive data from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            switch (cmd) {
                case "list":
                    // Execute the list command
                    executeListCommand(writer, reader);
                    break;
                case "put":
                    // Execute the put command to upload a file
                    executePutCommand(args, writer, reader, socket);
                    break;
                default:
                    // Print an error message for invalid command
                    System.out.println("Error: Invalid command '" + cmd + "'");
                    break;
            }
        } catch (IOException err) {
            // Print any IOException that occurs during execution
            System.out.println(err.getMessage());
        }
    }

    private void executeListCommand(PrintWriter writer, BufferedReader reader) throws IOException {
        writer.println("list"); // Send 'list' command to the server
        String line;
        // Read and print server responses until no more data is received
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }

    private void executePutCommand(String[] args, PrintWriter writer, BufferedReader reader, Socket socket) throws IOException {
        if (args.length < 2) {
            // Print an error message if filename is missing for put command
            System.out.println("Error: Please enter 'java Client put <filename>'");
            return;
        }
        File file = new File(args[1]);
        if (!file.exists()) {
            // Print an error message if the specified file doesn't exist
            System.out.println("Error: Cannot open local file '" + args[1] + "' for reading.\n");
            return;
        }
        writer.println("put " + args[1]); // Send 'put' command with filename to the server
        String response = reader.readLine(); // Receive response from the server

        if ("Error".equals(response)) {
            // Print an error message if the file already exists on the server
            System.out.println("Error: Cannot upload file '" + args[1] + "'; already exists on server.");
            return;
        }
        uploadFile(file, socket); // Upload the specified file to the server
        System.out.println("Uploaded file " + args[1]);
    }

    private void uploadFile(File filename, Socket socket) {
        try {
            FileInputStream fileInputStream = new FileInputStream(filename);
            byte[] buffer = new byte[8192];
            int bytes;
            OutputStream outputStream = socket.getOutputStream();
            // Read file data and write it to the output stream
            while ((bytes = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytes);
                outputStream.flush();
            }
        } catch (IOException err) {
            // Print any IOException that occurs during file upload
            System.out.println(err.getMessage());
        }

    }
}
