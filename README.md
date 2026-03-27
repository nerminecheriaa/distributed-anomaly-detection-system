# Distributed Anomaly Detection System Based on a Multi-Agent Architecture

## 1. Overview

This project involves the design and implementation of a **distributed anomaly detection system** based on a **multi-agent architecture**. It was carried out as part of a university practical assignment on distributed systems and agent-oriented programming.

Each node in the system is monitored locally by an autonomous agent capable of detecting CPU and bandwidth overloads. Anomalies are forwarded to a central server responsible for correlating them, logging events, triggering an audit via a mobile agent, and applying a corrective action. A Python module handles real-time visualization of metrics.

---

## 2. Objectives

- Monitor CPU and bandwidth usage across multiple nodes
- Locally detect threshold violations
- Centralize and correlate alerts
- Dynamically deploy a mobile audit agent
- Apply automatic corrective actions
- Visualize metric evolution in real time

---

## 3. Development Steps

### Step 1 – Requirements Analysis

Analysis of the project brief to identify functional and technical requirements:
- distributed resource monitoring
- local anomaly detection
- centralized coordination
- mobile auditing
- automatic reaction
- graphical visualization

This led to the choice of a JADE-based multi-agent architecture, complemented by a Python monitoring module.

---

### Step 2 – Environment Setup

- Installation of JADE (Java Agent DEvelopment Framework)
- Java environment configuration
- Installation of Python 3 and required libraries
- Setup of a SQLite database

---

### Step 3 – Local Agent Development

Each node is associated with a **LocalAgent** responsible for:
- periodically generating CPU and bandwidth metrics
- comparing values against defined thresholds
- detecting anomalies
- sending ALERT messages to the central server
- logging measurements to a log file
- applying a corrective action when needed

This step covers the **autonomous distributed monitoring** phase.

---

### Step 4 – Central Server Implementation

The **CentralServerAgent** handles global coordination:
- receiving alerts from local agents
- storing anomalies in SQLite
- correlating and counting alerts per node
- triggering a mobile agent after repeated anomalies
- sending corrective actions to the affected node

This step introduces the concept of **centralized intelligence**.

---

### Step 5 – Mobile Audit Agent Deployment

When a critical situation is detected:
- dynamic creation of a **MobileAuditAgent**
- migration to the faulty node's container
- collection of additional local information
- sending an audit report back to the central server

This phase enables in-depth analysis directly on the affected node.

---

### Step 6 – Python Monitoring & Visualization

A Python module handles:
- continuous reading of the shared log file
- real-time plotting of CPU and bandwidth curves
- visualization of critical thresholds
- highlighting of detected anomalies

---

### Step 7 – Testing, Validation & Analysis

Tests conducted on various simulated overload scenarios:
- validation of local detection
- verification of alert correlation
- observation of mobile agent deployment
- analysis of corrective action impact
- consistency check between JADE agents and Python graphs

These tests confirmed the overall correct behavior of the system.

---

## 4. System Architecture

### 4.1 JADE Agents

- **LocalAgent** – local monitoring and anomaly detection
- **CentralServerAgent** – coordination, correlation and decision-making
- **MobileAuditAgent** – targeted mobile auditing

### 4.2 Visualization Module

- Python script based on matplotlib
- Reads from `agent_metrics.log`
- Real-time metric display

---

## 5. Technologies

- Java (JDK 8+)
- JADE 4.6
- SQLite
- Python 3
- matplotlib
- psutil

---

## 6. Project Structure

```
project-root/
│
├── jade-agents/
│   ├── src/agents/
│   │   ├── LocalAgent.java
│   │   ├── CentralServerAgent.java
│   │   └── MobileAuditAgent.java
│   ├── src/utils/
│   ├── lib/
│   └── classes/
│
├── monitoring/
│   ├── agent_metrics.log
│   └── matplotlib_monitor.py
│
├── database/
│   └── alerts.db
│
├── docker/
│   ├── Dockerfile.jade
│   ├── Dockerfile.python
│   └── docker-compose.yml
│
└── README.md
```

---

## 7. Local Installation

### 7.1 Prerequisites

- Java JDK installed
- Python 3 installed
- Linux or Windows terminal

### 7.2 Python Dependencies

```bash
sudo apt install python3 python3-pip
pip install matplotlib psutil
```

---

## 8. Build & Run (without Docker)

### 8.1 Java Compilation

```bash
javac -cp "lib/*" -d classes src/agents/*.java src/utils/*.java
```

### 8.2 Launch JADE Platform

```bash
java -cp "lib/*:classes" jade.Boot -gui
```

### 8.3 Launch Python Monitoring

```bash
python3 monitoring/matplotlib_monitor.py
```

---

## 9. Docker Execution

Docker allows running the project in an isolated and reproducible environment, independent of the host system configuration.

### 9.1 Prerequisites

- Docker installed
- Docker Compose installed

```bash
docker --version
docker-compose --version
```

---

### 9.2 Dockerfile – JADE Agents

```dockerfile
FROM openjdk:11-jdk-slim
WORKDIR /app
COPY jade-agents /app/jade-agents
WORKDIR /app/jade-agents
RUN javac -cp "lib/*" -d classes src/agents/*.java src/utils/*.java
CMD ["java", "-cp", "lib/*:classes", "jade.Boot", "-gui"]
```

---

### 9.3 Dockerfile – Python Monitoring

```dockerfile
FROM python:3.10-slim
WORKDIR /app
COPY monitoring /app/monitoring
RUN pip install matplotlib psutil
CMD ["python", "monitoring/matplotlib_monitor.py"]
```

---

### 9.4 Start the System

```bash
docker-compose up --build
```

Stop the system:

```bash
docker-compose down
```

---

## 10. Academic Context

Project carried out as part of a **university practical assignment**
Fields: distributed systems, networking, multi-agent systems
