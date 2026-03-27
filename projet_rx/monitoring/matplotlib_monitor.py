import matplotlib.pyplot as plt
import time
import os

LOG_FILE = "agent_metrics.log"      # LocalAgent écrit dedans
CPU_THRESHOLD = 80
BW_THRESHOLD = 60

timestamps = []
cpu_history = []
bw_history = []
anomaly_history = []
node_history = []

def read_last_metrics():
    if not os.path.exists(LOG_FILE):
        return None

    with open(LOG_FILE, "r") as f:
        lines = f.readlines()
        if not lines:
            return None

        last = lines[-1].strip()
        parts = last.split(";")

        if len(parts) != 4:
            return None

        node = parts[0]
        cpu = float(parts[1])
        bw = float(parts[2])
        anomaly = int(parts[3])

        return node, cpu, bw, anomaly


def start_monitoring():
    plt.ion()
    fig, ax = plt.subplots(2, 1, figsize=(12, 8))

    while True:
        data = read_last_metrics()
        if data is None:
            time.sleep(1)
            continue

        node, cpu, bw, anomaly = data

        timestamps.append(len(timestamps))
        cpu_history.append(cpu)
        bw_history.append(bw)
        anomaly_history.append(anomaly)
        node_history.append(node)

        ax[0].clear()
        ax[1].clear()

        # CPU GRAPH
        ax[0].plot(timestamps, cpu_history, color="blue", label="CPU (%)")
        ax[0].axhline(CPU_THRESHOLD, color="red", linestyle="--", label="CPU Threshold")

        for i, a in enumerate(anomaly_history):
            if a == 1:
                ax[0].scatter(i, cpu_history[i], color="red", s=50)

        ax[0].set_title(f"CPU Usage (%) — Dernier agent : {node}")
        ax[0].set_ylim(0, 100)
        ax[0].legend()

        # BW GRAPH
        ax[1].plot(timestamps, bw_history, color="green", label="BW (KB/s)")
        ax[1].axhline(BW_THRESHOLD, color="red", linestyle="--", label="BW Threshold")

        for i, a in enumerate(anomaly_history):
            if a == 1:
                ax[1].scatter(i, bw_history[i], color="red", s=50)

        ax[1].set_title("Bandwidth (KB/s)")
        ax[1].legend()

        if anomaly == 1:
            fig.suptitle(f"⚠ ANOMALIE détectée par {node} !", color="red")
        else:
            fig.suptitle("Monitoring Temps Réel des Agents JADE", color="black")

        plt.pause(0.2)


if __name__ == "__main__":
    start_monitoring()

