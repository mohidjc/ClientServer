# File Transfer Client-Server Application

## Overview

This is a simple client-server application for transferring files over a network using Java sockets. The server listens for incoming connections and allows clients to upload files or request a list of existing files on the server.

## Features

- **List Files:** Clients can request a list of files currently stored on the server.
- **Upload Files:** Clients can upload files to the server if they don't already exist there.

## Requirements

- Java Development Kit (JDK) 8 or higher
- Basic knowledge of Java and networking concepts

## Getting Started

### Clone the Repository

```bash
git clone <your-repo-url>
cd <your-repo-directory>
```

## Compilation

Compile the server and client code using the following commands:


```bash
javac Server.java
javac Client.java
```

## Running the Server

Before running the client, start the server:

```bash
java Server
```
make sure the `serverFiles` directory exists, as it will store uploaded files:

```bash
mkdir serverFiles
```

## Running the Client

To use the client, open a new terminal window and run:

```bash
java Client <command> [filename]
```

- **Commands**:
  
  -  `List`: Request the list of files on the server.
  -  `put <filensme>`: Upload a file to the server.
    
### Example

1. **List Files**:
  ```bash
  java Client list
  ```

2. **Upload a File**:
  ```bash
  java Client put myfile.txt
  ```

## Logging

The server logs each request to a `log.txt` file, including the timestamp and IP address of the client.

## Notes

- Ensure that the server is running before you try to connect with the client.
- Handle file permissions and directory structures appropriately to avoid issues during file uploads.

## Contributing

Feel free to fork the repository and submit pull requests for improvements or bug fixes!

## License

This project is open-source and available under the MIT License.


