package agents;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.*;

import java.nio.file.*;

public class LocalAgent extends Agent {

    private String nodeId;
    private double cpuThreshold = 80.0;
    private double bandwidthThreshold = 60.0;
    private int tickCount = 0;

    private long lastCorrectiveActionTime = 0;

    @Override
    protected void setup() {

        Object[] args = getArguments();
        nodeId = (args != null && args.length > 0) ? (String) args[0] : "node-unknown";

        System.out.println("========================================");
        System.out.println("Agent Local " + nodeId + " démarré");
        System.out.println("Agent: " + getLocalName());
        System.out.println("Container: " + here().getName());
        System.out.println("========================================");

        registerInDF();

        if ("node-1".equals(nodeId)) {
            try {
                Runtime.getRuntime().exec("python3 ../monitoring/matplotlib_monitor.py");
            } catch (Exception e) {
                System.err.println("Erreur lancement script Python : " + e.getMessage());
            }
        }

        addBehaviour(new MonitoringBehaviour());
        addBehaviour(new ReceiveMessagesBehaviour());
    }

    private void registerInDF() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("local-monitoring");
        sd.setName("node-" + nodeId);
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            System.err.println("Erreur DF: " + e.getMessage());
        }
    }

    private class MonitoringBehaviour extends TickerBehaviour {
        public MonitoringBehaviour() {
            super(LocalAgent.this, 15000);
        }

        protected void onTick() {
            tickCount++;

            MonitoringData data = collectMetrics();

            logMetricsToFile(data);

            System.out.println("\n[" + nodeId + "] Monitoring #" + tickCount +
                    " - CPU: " + String.format("%.1f", data.cpuUsage) +
                    "% | BW: " + String.format("%.1f", data.bandwidth) + " KB/s");

            if (data.cpuUsage > cpuThreshold || data.bandwidth > bandwidthThreshold) {
                System.out.println("⚠ Anomalie détectée !");
                sendAlertToCentralServer(data);
            }
        }
    }

    private class ReceiveMessagesBehaviour extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
            if (msg != null) {
                handleMessage(msg);
            } else block();
        }
    }

    private MonitoringData collectMetrics() {
        MonitoringData data = new MonitoringData();

        if (System.currentTimeMillis() - lastCorrectiveActionTime < 60000) {
            data.cpuUsage = 10 + Math.random() * 10;
            data.bandwidth = 5 + Math.random() * 10;
            return data;
        }

        if (tickCount % 4 == 0) {
            data.cpuUsage = 85 + Math.random() * 10;
            data.bandwidth = 65 + Math.random() * 15;
        } else {
            data.cpuUsage = 30 + Math.random() * 40;
            data.bandwidth = 20 + Math.random() * 30;
        }

        return data;
    }

    private void logMetricsToFile(MonitoringData data) {
        try {
            String anomaly = (data.cpuUsage > cpuThreshold || data.bandwidth > bandwidthThreshold) ? "1" : "0";

            String line = nodeId + ";" + data.cpuUsage + ";" + data.bandwidth + ";" + anomaly + "\n";

            Files.write(
                    Paths.get("/home/mariem/projetrxv2/multi-agent-system-for-anomaly-detection-main/monitoring/agent_metrics.log"),
                    line.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

        } catch (Exception e) {
            System.err.println("Erreur écriture log Python : " + e.getMessage());
        }
    }

    // ✔✔✔ LA MÉTHODE MANQUANTE (cause de ton erreur)
    private void sendAlertToCentralServer(MonitoringData data) {
        try {
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("server-service");
            template.addServices(sd);

            DFAgentDescription[] result = DFService.search(this, template);

            if (result.length > 0) {
                ACLMessage alert = new ACLMessage(ACLMessage.INFORM);
                alert.addReceiver(result[0].getName());
                alert.setContent("ALERT:" + nodeId +
                        ":CPU=" + data.cpuUsage +
                        ":BW=" + data.bandwidth);
                send(alert);

                System.out.println("✓ Alerte envoyée au Serveur Central");
            }
        } catch (Exception e) {
            System.err.println("Erreur envoi alerte : " + e.getMessage());
        }
    }

    private void handleMessage(ACLMessage msg) {
        String content = msg.getContent();

        if (content.startsWith("AUDIT")) {
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            MonitoringData data = collectMetrics();
            reply.setContent("AUDIT_REPORT:" + nodeId + "|CPU=" + data.cpuUsage + "|BW=" + data.bandwidth);
            send(reply);
        }

        if (content.startsWith("CORRECTIVE_ACTION")) {
            System.out.println("⚙ Action corrective reçue !");
            applyCorrectiveAction();
        }
    }

    private void applyCorrectiveAction() {
        System.out.println("✓ ACTION CORRECTIVE APPLIQUÉE : réduction CPU + BW");
        lastCorrectiveActionTime = System.currentTimeMillis();
    }

    protected void takeDown() {
        try { DFService.deregister(this); } catch (Exception ignored) {}
        System.out.println("Agent " + nodeId + " arrêté.");
    }

    private static class MonitoringData {
        double cpuUsage;
        double bandwidth;
    }
}

