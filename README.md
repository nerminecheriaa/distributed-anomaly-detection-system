# Système distribué de détection d’anomalies basé sur une architecture multi-agents

## 1. Présentation générale

Ce projet a pour objectif la conception et l’implémentation d’un **système distribué de détection
d’anomalies** basé sur une **architecture multi-agents**. Il s’inscrit dans le cadre d’un travail
pratique universitaire portant sur les systèmes distribués et la programmation orientée agents.

Chaque nœud du système est surveillé localement par un agent autonome capable de détecter
des surcharges CPU et bande passante. Les anomalies sont transmises à un serveur central
chargé de les corréler, d’enregistrer les événements, de déclencher un audit via un agent mobile
et d’appliquer une action corrective. Un module Python permet la visualisation temps réel
des métriques.

---

## 2. Objectifs du projet

- Surveiller la consommation CPU et bande passante de plusieurs nœuds
- Détecter localement les dépassements de seuils
- Centraliser et corréler les alertes
- Déployer dynamiquement un agent mobile d’audit
- Appliquer une action corrective automatique
- Visualiser l’évolution des métriques en temps réel

---

## 3. Démarche et étapes de réalisation

### Étape 1 : Analyse de l’énoncé et définition des besoins

Analyse de l’énoncé afin d’identifier les exigences fonctionnelles et techniques :
- surveillance distribuée des ressources
- détection d’anomalies locales
- coordination centralisée
- audit mobile
- réaction automatique
- visualisation graphique

Cette analyse a conduit au choix d’une architecture multi-agents basée sur JADE, complétée
par un module Python de monitoring.

---

### Étape 2 : Mise en place de l’environnement

- Installation de JADE (Java Agent DEvelopment Framework)
- Configuration de l’environnement Java
- Installation de Python 3 et des bibliothèques nécessaires
- Mise en place d’une base de données SQLite

---

### Étape 3 : Développement des agents locaux

Chaque nœud est associé à un **LocalAgent** chargé de :
- générer périodiquement des métriques CPU et bande passante
- comparer les valeurs aux seuils définis
- détecter les anomalies
- envoyer des messages ALERT au serveur central
- enregistrer les mesures dans un fichier de log
- appliquer une action corrective si nécessaire

Cette étape correspond à la phase de **surveillance distribuée autonome**.

---

### Étape 4 : Mise en œuvre du serveur central

Le **CentralServerAgent** assure la coordination globale du système :
- réception des alertes envoyées par les agents locaux
- enregistrement des anomalies dans SQLite
- corrélation et comptage des alertes par nœud
- déclenchement d’un agent mobile après plusieurs anomalies
- envoi d’une action corrective au nœud concerné

Cette étape introduit la notion d’**intelligence centralisée**.

---

### Étape 5 : Déploiement de l’agent mobile d’audit

Lorsqu’une situation critique est détectée :
- création dynamique d’un **MobileAuditAgent**
- migration vers le conteneur du nœud fautif
- collecte d’informations locales supplémentaires
- envoi d’un rapport d’audit au serveur central

Cette phase permet une analyse approfondie directement sur le nœud concerné.

---

### Étape 6 : Monitoring et visualisation Python

Un module Python permet :
- la lecture continue du fichier de log partagé
- l’affichage temps réel des courbes CPU et bande passante
- la visualisation des seuils critiques
- la mise en évidence des anomalies détectées

---

### Étape 7 : Tests, validation et analyse des résultats

Tests réalisés sur différents scénarios de surcharge simulée :
- validation de la détection locale
- vérification de la corrélation des alertes
- observation du déploiement de l’agent mobile
- analyse de l’impact des actions correctives
- cohérence entre agents JADE et graphiques Python

Ces tests ont confirmé le bon fonctionnement global du système.

---

## 4. Architecture du système

### 4.1 Agents JADE

- **LocalAgent** : surveillance locale et détection d’anomalies
- **CentralServerAgent** : coordination, corrélation et décision
- **MobileAuditAgent** : audit mobile ciblé

### 4.2 Module de visualisation

- Script Python basé sur matplotlib
- Lecture du fichier `agent_metrics.log`
- Affichage temps réel des métriques

---

## 5. Technologies utilisées

- Java (JDK 8+)
- JADE 4.6
- SQLite
- Python 3
- matplotlib
- psutil

---

## 6. Structure du projet

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

````

---

## 7. Installation (exécution locale)

### 7.1 Prérequis

- Java JDK installé
- Python 3 installé
- Terminal Linux ou Windows

### 7.2 Installation des dépendances Python

```bash
sudo apt install python3 python3-pip
pip install matplotlib psutil
````

---

## 8. Compilation et exécution (sans Docker)

### 8.1 Compilation Java

```bash
javac -cp "lib/*" -d classes src/agents/*.java src/utils/*.java
```

### 8.2 Lancement de la plateforme JADE

```bash
java -cp "lib/*:classes" jade.Boot -gui
```

### 8.3 Lancement du monitoring Python

```bash
python3 monitoring/matplotlib_monitor.py
```

---

## 9. Exécution du projet avec Docker

L’utilisation de Docker permet d’exécuter le projet dans un environnement isolé et reproductible,
sans dépendre de la configuration du système hôte.

### 9.1 Prérequis

* Docker installé
* Docker Compose installé

Vérification :

```bash
docker --version
docker-compose --version
```

---

### 9.2 Dockerfile – Agents JADE

```dockerfile
FROM openjdk:11-jdk-slim

WORKDIR /app

COPY jade-agents /app/jade-agents

WORKDIR /app/jade-agents

RUN javac -cp "lib/*" -d classes src/agents/*.java src/utils/*.java

CMD ["java", "-cp", "lib/*:classes", "jade.Boot", "-gui"]
```

---

### 9.3 Dockerfile – Monitoring Python

```dockerfile
FROM python:3.10-slim

WORKDIR /app

COPY monitoring /app/monitoring

RUN pip install matplotlib psutil

CMD ["python", "monitoring/matplotlib_monitor.py"]
```

---

### 9.4 Lancement du système avec Docker

```bash
docker-compose up --build
```

Arrêt du système :

```bash
docker-compose down
```

---

## 10. Cadre académique

Projet réalisé dans le cadre d’un **Travail Pratique universitaire**
Domaines : systèmes distribués, réseaux, systèmes multi-agents

```

---