# Distributed Anomaly Detection System Based on a Multi-Agent Architecture

## 1. General Overview

This project aims to design and implement a **distributed anomaly detection system** based on a **multi-agent architecture**. It is part of a university practical work focused on distributed systems and agent-oriented programming.

Each node of the system is locally monitored by an autonomous agent capable of detecting CPU and bandwidth overloads. Anomalies are transmitted to a central server responsible for correlating them, logging events, triggering an audit via a mobile agent, and applying a corrective action. A Python module enables real-time visualization of the metrics.

---

## 2. Project Objectives

- Monitor CPU and bandwidth consumption of multiple nodes
- Detect threshold exceedances locally
- Centralize and correlate alerts
- Dynamically deploy a mobile audit agent
- Apply an automatic corrective action
- Visualize metric evolution in real time

---

## 3. Approach and Implementation Steps

### Step 1: Requirements Analysis

Analysis of the specifications to identify functional and technical requirements:
- Distributed resource monitoring
- Local anomaly detection
- Centralized coordination
- Mobile audit
- Automatic reaction
- Graphical visualization

This analysis led to the choice of a multi-agent architecture based on JADE, complemented by a Python monitoring module.

---

### Step 2: Environment Setup

- Installation of JADE (Java Agent DEvelopment Framework)
- Configuration of the Java environment
- Installation of Python 3 and required libraries
- Setup of a SQLite database

---

### Step 3: Development of Local Agents

Each node is associated with a **LocalAgent** responsible for:
- Periodically generating CPU and bandwidth metrics
- Comparing values against defined thresholds
- Detecting anomalies
- Sending ALERT messages to the central server
- Logging measurements to a log file
- Applying a corrective action if necessary

This step corresponds to the **autonomous distributed monitoring** phase.

---

### Step 4: Implementation of the Central Server

The **CentralServerAgent** ensures global system coordination:
- Receiving alerts sent by local agents
- Recording anomalies in SQLite
- Correlating and counting alerts per node
- Triggering a mobile agent after multiple anomalies
- Sending a corrective action to the affected node

This step introduces the concept of **centralized intelligence**.

---

### Step 5: Deployment of the Mobile Audit Agent

When a critical situation is detected:
- Dynamic creation of a **MobileAuditAgent**
- Migration to the faulty node's container
- Collection of additional local information
- Sending an audit report back to the central server

This phase enables in-depth analysis directly on the affected node.

---

### Step 6: Python Monitoring and Visualization

A Python module allows:
- Continuous reading of the shared log file
- Real-time display of CPU and bandwidth curves
- Visualization of critical thresholds
- Highlighting of detected anomalies

---

### Step 7: Testing, Validation, and Results Analysis

Tests conducted on various simulated overload scenarios:
- Validation of local detection
- Verification of alert correlation
- Observation of mobile agent deployment
- Analysis of corrective action impact
- Consistency between JADE agents and Python graphs

These tests confirmed the overall correct functioning of the system.

---

## 4. System Architecture

### 4.1 JADE Agents

- **LocalAgent**: Local monitoring and anomaly detection
- **CentralServerAgent**: Coordination, correlation, and decision-making
- **MobileAuditAgent**: Targeted mobile audit

### 4.2 Visualization Module

- Python script based on matplotlib
- Reads the `agent_metrics.log` file
- Real-time display of metrics

---

## 5. Technologies Used

- Java (JDK 8+)
- JADE 4.6
- SQLite
- Python 3
- matplotlib
- psutil

---

## 6. Project Structure
project-root/
│
├── jade-agents/
│ ├── src/agents/
│ │ ├── LocalAgent.java
│ │ ├── CentralServerAgent.java
│ │ └── MobileAuditAgent.java
│ ├── src/utils/
│ ├── lib/
│ └── classes/
│
├── monitoring/
│ ├── agent_metrics.log
│ └── matplotlib_monitor.py
│
├── database/
│ └── alerts.db
│
├── docker/
│ ├── Dockerfile.jade
│ ├── Dockerfile.python
│ └── docker-compose.yml
│
└── README.md

text

---

## 7. Installation (Local Execution)

### 7.1 Prerequisites

- Java JDK installed
- Python 3 installed
- Linux or Windows terminal

### 7.2 Installing Python Dependencies

```bash
sudo apt install python3 python3-pip
pip install matplotlib psutil
8. Compilation and Execution (Without Docker)
8.1 Java Compilation
bash
javac -cp "lib/*" -d classes src/agents/*.java src/utils/*.java
8.2 Launching the JADE Platform
bash
java -cp "lib/*:classes" jade.Boot -gui
8.3 Launching Python Monitoring
bash
python3 monitoring/matplotlib_monitor.py
9. Running the Project with Docker
Using Docker allows the project to run in an isolated and reproducible environment, independent of the host system's configuration.

9.1 Prerequisites
Docker installed

Docker Compose installed

Verification:

bash
docker --version
docker-compose --version
9.2 Dockerfile – JADE Agents
dockerfile
FROM openjdk:11-jdk-slim

WORKDIR /app

COPY jade-agents /app/jade-agents

WORKDIR /app/jade-agents

RUN javac -cp "lib/*" -d classes src/agents/*.java src/utils/*.java

CMD ["java", "-cp", "lib/*:classes", "jade.Boot", "-gui"]
9.3 Dockerfile – Python Monitoring
dockerfile
FROM python:3.10-slim

WORKDIR /app

COPY monitoring /app/monitoring

RUN pip install matplotlib psutil

CMD ["python", "monitoring/matplotlib_monitor.py"]
9.4 Launching the System with Docker
bash
docker-compose up --build
Stopping the system:

bash
docker-compose down
10. Academic Context
Project carried out as part of a university practical work
Domains: distributed systems, networks, multi-agent systems