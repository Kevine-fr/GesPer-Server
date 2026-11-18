#!/usr/bin/env bash
# Restaure une sauvegarde faite par scripts/backup.sh.
#
#   ./scripts/backup.sh                       # l'état actuel, d'abord
#   ./scripts/restore.sh backups/<horodatage>/gesper-<horodatage>.sql.gz
#
# Flyway relit son historique (flyway_schema_history, dans le dump) au
# démarrage suivant : restaurer une base plus ancienne que le code fait
# rejouer les migrations manquantes, jamais l'inverse.
set -euo pipefail

FICHIER="${1:?Usage : restore.sh <gesper-AAAAMMJJ-HHMMSS.sql.gz>}"
CONTAINER="${GESPER_DB_CONTAINER:-gesper-postgres}"
[ -s "$FICHIER" ] || { echo "Sauvegarde introuvable ou vide : $FICHIER" >&2; exit 1; }

echo "Restauration dans $CONTAINER…" >&2
gzip -dc "$FICHIER" \
  | docker exec -i "$CONTAINER" sh -c 'psql -q -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d "$POSTGRES_DB"' >/dev/null
echo "✓ Restauré." >&2
