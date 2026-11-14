import mpi.*;

/**
 * MPIDataExchange.java
 * 
 * A simple MPI program that demonstrates data exchange between two processes
 * (System 1 and System 2) using point-to-point communication (Send/Recv).
 *
 * Process 0 (System 1) sends a message to Process 1 (System 2).
 * Process 1 (System 2) receives it, processes it, and sends a reply back.
 *
 * Usage:  mpjrun.sh -np 2 MPIDataExchange
 */
public class MPIDataExchange {

    public static void main(String[] args) throws Exception {

        // Initialize the MPI environment
        MPI.Init(args);

        // Get the rank (ID) of this process and the total number of processes
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        // We need exactly 2 processes for this program
        if (size != 2) {
            if (rank == 0) {
                System.out.println("Error: This program requires exactly 2 processes.");
                System.out.println("Run with:  mpjrun.sh -np 2 MPIDataExchange");
            }
            MPI.Finalize();
            return;
        }

        // Define a tag to identify messages
        int tag = 100;

        if (rank == 0) {
            // ===================== SYSTEM 1 (Process 0) =====================

            System.out.println("=== System 1 (Process 0) started ===");

            // --- Step 1: Send an integer array to System 2 ---
            int[] sendData = {10, 20, 30, 40, 50};
            System.out.println("System 1: Sending integer array to System 2: "
                    + arrayToString(sendData));

            MPI.COMM_WORLD.Send(sendData, 0, sendData.length, MPI.INT, 1, tag);
            System.out.println("System 1: Data sent successfully.");

            // --- Step 2: Receive the processed reply from System 2 ---
            int[] recvData = new int[5];
            MPI.COMM_WORLD.Recv(recvData, 0, recvData.length, MPI.INT, 1, tag + 1);
            System.out.println("System 1: Received processed data from System 2: "
                    + arrayToString(recvData));

            // --- Step 3: Send a string message to System 2 ---
            String message = "Hello from System 1!";
            char[] charSend = message.toCharArray();
            // First send the length, then the character data
            int[] lengthBuf = {charSend.length};
            MPI.COMM_WORLD.Send(lengthBuf, 0, 1, MPI.INT, 1, tag + 2);
            MPI.COMM_WORLD.Send(charSend, 0, charSend.length, MPI.CHAR, 1, tag + 3);
            System.out.println("System 1: Sent string message: \"" + message + "\"");

            // --- Step 4: Receive a string reply from System 2 ---
            int[] replyLenBuf = new int[1];
            MPI.COMM_WORLD.Recv(replyLenBuf, 0, 1, MPI.INT, 1, tag + 4);
            char[] charRecv = new char[replyLenBuf[0]];
            MPI.COMM_WORLD.Recv(charRecv, 0, replyLenBuf[0], MPI.CHAR, 1, tag + 5);
            String reply = new String(charRecv);
            System.out.println("System 1: Received string reply: \"" + reply + "\"");

            System.out.println("=== System 1 (Process 0) finished ===");

        } else if (rank == 1) {
            // ===================== SYSTEM 2 (Process 1) =====================

            System.out.println("=== System 2 (Process 1) started ===");

            // --- Step 1: Receive the integer array from System 1 ---
            int[] recvData = new int[5];
            MPI.COMM_WORLD.Recv(recvData, 0, recvData.length, MPI.INT, 0, tag);
            System.out.println("System 2: Received integer array from System 1: "
                    + arrayToString(recvData));

            // --- Step 2: Process the data (double each element) and send back ---
            int[] processedData = new int[recvData.length];
            for (int i = 0; i < recvData.length; i++) {
                processedData[i] = recvData[i] * 2;
            }
            System.out.println("System 2: Processed data (doubled): "
                    + arrayToString(processedData));

            MPI.COMM_WORLD.Send(processedData, 0, processedData.length, MPI.INT, 0, tag + 1);
            System.out.println("System 2: Sent processed data back to System 1.");

            // --- Step 3: Receive a string message from System 1 ---
            int[] lengthBuf = new int[1];
            MPI.COMM_WORLD.Recv(lengthBuf, 0, 1, MPI.INT, 0, tag + 2);
            char[] charRecv = new char[lengthBuf[0]];
            MPI.COMM_WORLD.Recv(charRecv, 0, lengthBuf[0], MPI.CHAR, 0, tag + 3);
            String received = new String(charRecv);
            System.out.println("System 2: Received string message: \"" + received + "\"");

            // --- Step 4: Send a string reply to System 1 ---
            String replyMsg = "Hello from System 2! Message received.";
            char[] charSend = replyMsg.toCharArray();
            int[] replyLenBuf = {charSend.length};
            MPI.COMM_WORLD.Send(replyLenBuf, 0, 1, MPI.INT, 0, tag + 4);
            MPI.COMM_WORLD.Send(charSend, 0, charSend.length, MPI.CHAR, 0, tag + 5);
            System.out.println("System 2: Sent string reply: \"" + replyMsg + "\"");

            System.out.println("=== System 2 (Process 1) finished ===");
        }

        // Finalize the MPI environment
        MPI.Finalize();
    }

    /**
     * Helper method to convert an int array to a readable string.
     */
    private static String arrayToString(int[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
