package agents;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.*;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.util.*;

public class CentralServerAgent extends Agent {

    private Map<String, AlertData> alerts = new HashMap<>();
    private Set<String> auditedNodes = new HashSet<>();

    @Override
    protected void setup() {

        System.out.println("========================================");
        System.out.println("   SERVEUR CENTRAL " + getLocalName());
        System.out.println("   Container: " + here().getName());
        System.out.println("========================================");

        registerInDF();

        // Réception messages ALERT + AUDIT_REPORT
        addBehaviour(new ReceiveMessagesBehaviour());

        // Analyse périodique (toutes les 30 s)
        addBehaviour(new PeriodicAnalysisBehaviour());
    }

    /** =============================
     *     ENREGISTREMENT DF
     *  ============================= */
    private void registerInDF() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setName("central-server");
        sd.setType("server-service");       // IMPORTANT pour correspondre aux LocalAgents
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
            System.out.println("✓ Serveur enregistré dans le Directory Facilitator");
        } catch (FIPAException e) {
            System.err.println("✗ Erreur DF: " + e.getMessage());
        }
    }

    /** =============================
     *     COMPORTEMENT PRINCIPAL
     *  ============================= */
    private class ReceiveMessagesBehaviour extends CyclicBehaviour {

        @Override
        public void action() {

            ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));

            if (msg != null) {

                String content = msg.getContent();

                if (content == null) {
                    block();
                    return;
                }

                if (content.startsWith("ALERT:")) {
                    processAlert(content);

                } else if (content.startsWith("AUDIT_REPORT:")) {
                    processAuditReport(content);

                } else {
                    System.out.println("⚠ Message non reconnu: " + content);
                }

            } else {
                block();
            }
        }
    }

    /** =============================
     *     TRAITEMENT ALERTES
     *  ============================= */
    private void processAlert(String alertContent) {

        System.out.println("\n🚨 ALERTE REÇUE");
        System.out.println("→ " + alertContent);

        String[] parts = alertContent.split(":");
        if (parts.length < 4) {
            System.err.println("✗ Format d’alerte incorrect");
            return;
        }

        String nodeId = parts[1];

        // Mettre à jour statistiques
        AlertData alert = alerts.getOrDefault(nodeId, new AlertData());
        alert.count++;
        alert.lastSeen = System.currentTimeMillis();
        alerts.put(nodeId, alert);

        System.out.println("→ Compteur pour " + nodeId + " = " + alert.count);

        // Déploiement agent mobile ?
        checkAndDeployAgent(nodeId, alert.count);
    }

    /** =============================
     *   DÉCISION : DÉPLOIEMENT MOBILE
     *  ============================= */
    private void checkAndDeployAgent(String nodeId, int count) {

        // Déployer si 3 alertes consécutives
        if (count >= 3 && !auditedNodes.contains(nodeId)) {

            System.out.println("🤖 Déploiement Agent Mobile vers " + nodeId + " (3 alertes consécutives)");

            deployMobileAgent(nodeId);
            auditedNodes.add(nodeId);

            // Reset compteur
            alerts.get(nodeId).count = 0;
        }
    }

    /** =============================
     *      TRAITEMENT AUDIT REPORT
     *  ============================= */
    private void processAuditReport(String reportContent) {

        System.out.println("\n📄 RAPPORT D'AUDIT REÇU");
        System.out.println(reportContent);

        try {
            // Format attendu :
            // AUDIT_REPORT:node-1|Location:X|Time:YYYY|Details:...
            String firstPart = reportContent.split("\\|")[0];
            String nodeId = firstPart.split(":")[1];

            System.out.println("✓ Audit traité pour: " + nodeId);

            auditedNodes.remove(nodeId);

        } catch (Exception e) {
            System.err.println("✗ Erreur parsing audit: " + e.getMessage());
        }
    }

    /** =============================
     *       DEPLOIEMENT AGENT MOBILE
     *  ============================= */
    private void deployMobileAgent(String targetNode) {
        try {
            String agentName = "audit-" + targetNode + "-" + System.currentTimeMillis();
            Object[] args = new Object[]{targetNode};

            AgentController ac = getContainerController().createNewAgent(
                    agentName,
                    "agents.MobileAuditAgent",
                    args
            );

            ac.start();
            System.out.println("✓ Agent mobile lancé: " + agentName);

        } catch (StaleProxyException e) {
            System.err.println("✗ Erreur création agent mobile: " + e.getMessage());
        }
    }

    /** =============================
     *       ANALYSE PÉRIODIQUE
     *  ============================= */
    private class PeriodicAnalysisBehaviour extends TickerBehaviour {

        public PeriodicAnalysisBehaviour() {
            super(CentralServerAgent.this, 30000);
        }

        @Override
        protected void onTick() {

            System.out.println("\n--- ANALYSE PÉRIODIQUE DU SERVEUR ---");

            if (alerts.isEmpty()) {
                System.out.println("Aucune alerte à analyser.");
                return;
            }

            alerts.forEach((node, data) ->
                    System.out.println("Node " + node + " → " + data.count + " alertes récentes")
            );
        }
    }

    /** =============================
     *        STRUCTURE DES ALERTES
     *  ============================= */
    private static class AlertData {
        int count = 0;
        long lastSeen;
    }
    /** =============================
 *   ACTION CORRECTIVE AVANCÉE
 *  ============================= */
private void sendCorrectiveAction(String nodeId) {

    System.out.println("⚙ Envoi d'une action corrective à " + nodeId);

    ACLMessage correction = new ACLMessage(ACLMessage.REQUEST);
    correction.setContent("CORRECTIVE_ACTION:LIMIT_RESOURCES");
    correction.addReceiver(new AID(nodeId, AID.ISLOCALNAME));

    send(correction);

    System.out.println("✓ Action corrective envoyée à " + nodeId);
}

    /** =============================
     *              FIN
     *  ============================= */
    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        System.out.println("Serveur Central arrêté");
    }
}

