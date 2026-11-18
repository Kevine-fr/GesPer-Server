#!/usr/bin/env bash
# Sauvegarde de la base GesPer (Postgres).
#
#   ./scripts/backup.sh                    -> backups/<horodatage>/gesper-<horodatage>.sql.gz
#   BACKUP_DIR=/srv/sauvegardes ./scripts/backup.sh
#
# Contrat commun aux dépôts du VPS (le dépôt Infrastructure envoie les
# sauvegardes sur MinIO) : un seul fichier écrit, son chemin en DERNIÈRE ligne
# de la sortie standard, les messages sur la sortie d'erreur.
set -euo pipefail

CONTAINER="${GESPER_DB_CONTAINER:-gesper-postgres}"
STAMP="$(date +%Y%m%d-%H%M%S)"
RACINE="$(cd "$(dirname "$0")/.." && pwd)"
DEST="${BACKUP_DIR:-$RACINE/backups/$STAMP}"
mkdir -p "$DEST"
FICHIER="$DEST/gesper-$STAMP.sql.gz"

echo "pg_dump depuis $CONTAINER…" >&2
# Identifiants lus dans le conteneur lui-même : rien à recopier ici.
# --clean --if-exists : la restauration remplace les tables au lieu d'échouer.
docker exec "$CONTAINER" sh -c 'pg_dump -U "$POSTGRES_USER" -d "$POSTGRES_DB" --clean --if-exists --no-owner' \
  | gzip > "$FICHIER.part"
if [ "$(gzip -dc "$FICHIER.part" | head -c 1 | wc -c)" -eq 0 ]; then
  rm -f "$FICHIER.part"
  echo "Échec : dump vide." >&2
  exit 1
fi
mv "$FICHIER.part" "$FICHIER"
echo "✓ $(du -h "$FICHIER" | cut -f1)" >&2
echo "$FICHIER"
