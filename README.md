# MPI Data Exchange Between Two Systems (Java)

A Java MPI program that demonstrates **point-to-point data exchange** between two distributed processes (System 1 and System 2) using **MPJ Express**.

---

## What This Program Does

This program simulates two independent computer systems communicating over a network using the **Message Passing Interface (MPI)** protocol. It launches two processes — **System 1 (Process 0)** and **System 2 (Process 1)** — that exchange data back and forth in four steps:

| Step | System 1 (Process 0)                  | System 2 (Process 1)                              |
|------|----------------------------------------|----------------------------------------------------|
| 1    | Sends integer array `[10,20,30,40,50]` | Receives the array                                 |
| 2    | Receives doubled array                 | Doubles each element → sends `[20,40,60,80,100]`   |
| 3    | Sends string `"Hello from System 1!"`  | Receives the string                                |
| 4    | Receives string reply                  | Sends `"Hello from System 2! Message received."`   |

This demonstrates both **numeric data** and **string message** exchange between processes, along with basic data processing on the receiver side.

---

## Theory Behind the Program

### Distributed Systems & Message Passing

A **distributed system** is a collection of independent computers (nodes) that communicate and coordinate their actions by passing messages over a network. Unlike shared-memory systems where processes access a common memory space, distributed systems rely on **message passing** as the fundamental communication mechanism.

Key characteristics of distributed systems:
- **No shared memory** — each process has its own private address space.
- **Communication via messages** — processes exchange data explicitly using send/receive operations.
- **Concurrency** — multiple processes execute simultaneously and independently.
- **Fault tolerance** — the system can continue functioning even if individual nodes fail.

### Message Passing Interface (MPI)

**MPI (Message Passing Interface)** is a standardized and portable message-passing specification designed for parallel and distributed computing. It defines a set of library routines for:

- **Point-to-point communication** — direct message exchange between two specific processes using `Send()` and `Recv()`.
- **Collective communication** — operations involving all processes in a group (e.g., broadcast, scatter, gather, reduce).
- **Synchronization** — ensuring processes coordinate their execution at specific points.

#### Core MPI Concepts Used in This Program

| Concept | Description |
|---------|-------------|
| **MPI.Init()** | Initializes the MPI execution environment. Must be called before any other MPI function. |
| **MPI.COMM_WORLD** | The default communicator — a predefined group that includes all available processes. |
| **Rank** | A unique integer identifier assigned to each process within a communicator (0-indexed). |
| **Size** | The total number of processes in the communicator. |
| **Send(buf, offset, count, datatype, dest, tag)** | Sends `count` elements from buffer `buf` starting at `offset` to the process with rank `dest`. The `tag` identifies the message. |
| **Recv(buf, offset, count, datatype, source, tag)** | Receives `count` elements into buffer `buf` from the process with rank `source`, matching the specified `tag`. |
| **MPI.Finalize()** | Terminates the MPI execution environment. No MPI calls can be made after this. |

#### How Point-to-Point Communication Works

Point-to-point communication is the most basic form of message passing. It involves exactly **two processes** — one sender and one receiver:

1. The **sender** calls `Send()`, specifying the data buffer, destination rank, and a message tag.
2. The **receiver** calls `Recv()`, specifying a receive buffer, source rank, and the matching tag.
3. The operation is **blocking** — the sender waits until the message is safely buffered or received, and the receiver waits until the data arrives.
4. The **tag** parameter allows processes to distinguish between different messages from the same source.

```
   Sender (rank 0)                    Receiver (rank 1)
   ┌──────────────┐                   ┌──────────────┐
   │  Send(data,  │                   │  Recv(buf,   │
   │    dest=1,   │  ──── message ──► │    src=0,    │
   │    tag=100)  │                   │    tag=100)  │
   └──────────────┘                   └──────────────┘
```

### MPJ Express

**MPJ Express** is an open-source Java implementation of the MPI standard. It allows Java programs to use MPI-style parallel programming. It supports two modes:

- **Multicore mode** — simulates multiple processes on a single machine using threads (used for testing and development).
- **Cluster mode (niodev)** — runs processes on separate machines connected over a TCP/IP network (used for actual distributed computing).

---

## Communication Flow

```
  System 1 (rank 0)                  System 2 (rank 1)
  ┌──────────────┐                   ┌──────────────┐
  │              │  int[5] array     │              │
  │   Send() ────┼──────────────────►│── Recv()     │
  │              │                   │              │
  │              │  doubled int[5]   │  Double each │
  │   Recv() ◄───┼───────────────────┼── Send()     │
  │              │                   │              │
  │              │  string message   │              │
  │   Send() ────┼──────────────────►│── Recv()     │
  │              │                   │              │
  │              │  string reply     │              │
  │   Recv() ◄───┼───────────────────┼── Send()     │
  │              │                   │              │
  └──────────────┘                   └──────────────┘
```

---

## Prerequisites

| Requirement    | Version     | Purpose                    |
|----------------|-------------|----------------------------|
| Java JDK       | 8 or higher | Compile and run Java code  |
| MPJ Express    | 0.44+       | MPI library for Java       |

---

## Project Structure

```
Distributed System/
├── README.md                  ← This file
├── Result/
│   └── Screenshot 2026-11-14 093302.png   ← Program output screenshot
└── src/
    ├── MPIDataExchange.java   ← Main MPI program
    └── MPIDataExchange.class  ← Compiled class file
```

---

## Step 1 — Install Java JDK

### Windows

1. Download the JDK from https://www.oracle.com/java/technologies/downloads/
   or install via winget:
   ```
   winget install Oracle.JDK.21
   ```
2. Verify installation:
   ```
   java -version
   javac -version
   ```

### Linux / macOS

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install default-jdk

# macOS (Homebrew)
brew install openjdk
```

---

## Step 2 — Download and Install MPJ Express

1. **Download** MPJ Express from:  
   https://sourceforge.net/projects/mpjexpress/files/releases/

2. **Extract** the archive to a known location, for example:
   - **Windows:** `C:\mpj`
   - **Linux/macOS:** `~/mpj`

   After extraction you should see:
   ```
   mpj/
   ├── bin/
   │   ├── mpjrun.bat        (Windows)
   │   └── mpjrun.sh         (Linux/macOS)
   ├── lib/
   │   └── mpj.jar
   ├── conf/
   └── ...
   ```

---

## Step 3 — Set Environment Variables

### Windows (Command Prompt)

```cmd
set MPJ_HOME=C:\mpj
set PATH=%MPJ_HOME%\bin;%PATH%
```

To make these **permanent**, open:  
**System → Advanced System Settings → Environment Variables**  
and add `MPJ_HOME = C:\mpj` and append `%MPJ_HOME%\bin` to `PATH`.

### Windows (PowerShell — current session)

```powershell
$env:MPJ_HOME = "C:\mpj"
$env:PATH = "$env:MPJ_HOME\bin;$env:PATH"
```

### Linux / macOS

Add to `~/.bashrc` or `~/.zshrc`:
```bash
export MPJ_HOME=~/mpj
export PATH=$MPJ_HOME/bin:$PATH
```
Then run `source ~/.bashrc`.

### Verify

```
mpjrun.bat -h       # Windows
mpjrun.sh  -h       # Linux/macOS
```

You should see usage/help text from MPJ Express.

---

## Step 4 — Compile the Program

Open a terminal in the project directory.

### Windows

```cmd
cd src
javac -cp "%MPJ_HOME%\lib\mpj.jar" MPIDataExchange.java
```

### Linux / macOS

```bash
cd src/
javac -cp $MPJ_HOME/lib/mpj.jar MPIDataExchange.java
```

This creates `MPIDataExchange.class` in the `src/` folder.

> **Note:** If you see errors about `mpi.*`, double-check that `MPJ_HOME`
> is set correctly and that `mpj.jar` exists in `$MPJ_HOME/lib/`.

---

## Step 5 — Run the Program (Single Machine)

This simulates two systems on a single computer using two MPI processes.

### Windows

```cmd
cd src
mpjrun.bat -np 2 MPIDataExchange
```

### Linux / macOS

```bash
cd src/
mpjrun.sh -np 2 MPIDataExchange
```

**Explanation of flags:**
- `-np 2` → Launch **2** MPI processes (Process 0 = System 1, Process 1 = System 2)

---

## Step 6 — Run Across Two Machines (Network)

To run each process on a separate physical machine:

### 6.1 — Ensure Both Machines Have

- Java JDK installed (same version recommended)
- MPJ Express installed at the **same path** on both machines
- `MPJ_HOME` environment variable set on both
- Both machines can reach each other over the network (ping test)

### 6.2 — Create a Machines File

Create a file called `machines` in the `src/` directory:

```
192.168.1.10      # IP address of Machine 1 (System 1)
192.168.1.20      # IP address of Machine 2 (System 2)
```

Replace with your actual IP addresses. Use one IP per line.

### 6.3 — Copy Files to Both Machines

Copy `MPIDataExchange.class` (the compiled file) to the **same directory
path** on both machines.

### 6.4 — Start MPJ Daemons on Both Machines

On **each machine**, open a terminal and run:

```bash
mpjdaemon.sh -start       # Linux/macOS
mpjdaemon.bat -start      # Windows
```

### 6.5 — Run the Program

From Machine 1, run:

```bash
mpjrun.sh -np 2 -dev niodev -machinesfile machines MPIDataExchange
```

**Explanation of flags:**
- `-np 2` → Two processes total
- `-dev niodev` → Use the network device (TCP/IP) instead of shared memory
- `-machinesfile machines` → Read machine IPs from the file

---

## Result

The screenshot below shows the successful execution of the MPI Data Exchange program on a Windows machine using MPJ Express in multicore configuration:

![Program Output](Result/Screenshot%202026-11-14%20093302.png)

**What the output shows:**

1. **Environment setup** — Setting `MPJ_HOME` and `PATH` variables.
2. **Compilation** — Compiling `MPIDataExchange.java` with the MPJ classpath.
3. **Execution** — Running with `mpjrun.bat -np 2` which starts MPJ Express (0.44) in multicore configuration.
4. **Integer array exchange** — System 1 sends `[10, 20, 30, 40, 50]`, System 2 receives it and doubles each element to `[20, 40, 60, 80, 100]`, then sends it back.
5. **String message exchange** — System 1 sends `"Hello from System 1!"`, System 2 receives it and replies with `"Hello from System 2! Message received."`.
6. **Successful completion** — Both processes finish without errors.

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| `mpi.* package not found` | Ensure `MPJ_HOME` is set and `mpj.jar` exists in `$MPJ_HOME/lib/` |
| `mpjrun not recognized` | Add `$MPJ_HOME/bin` to your system `PATH` |
| `ClassNotFoundException` | Run `mpjrun` from the directory containing `MPIDataExchange.class` |
| `Connection refused` (multi-machine) | Start `mpjdaemon` on both machines; check firewall rules |
| `java: command not found` | Install Java JDK and ensure `java`/`javac` are in `PATH` |
| Output order is mixed | This is normal — two processes print independently |

---

## References

- [MPJ Express Official Site](http://mpjexpress.org/)
- [MPJ Express User Guide](http://mpjexpress.org/docs/guides/linuxguide.pdf)
- [MPI Standard](https://www.mpi-forum.org/)
