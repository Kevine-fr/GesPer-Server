FROM mcr.microsoft.com/devcontainers/java:21-bookworm

# Installation de Maven et outils utiles
RUN apt-get update \
 && apt-get install -y --no-install-recommends \
        maven \
        postgresql-client \
        curl \
        git \
 && rm -rf /var/lib/apt/lists/*

# Compatibilité ID utilisateur (l'image fournit déjà 'vscode')
USER vscode

# Pré-configuration Maven (optionnel)
RUN mkdir -p /home/vscode/.m2
