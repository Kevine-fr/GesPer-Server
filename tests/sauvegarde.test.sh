#!/usr/bin/env bash
# Épreuve du contrat de sauvegarde, sans Docker (faux `docker`).
#   bash tests/sauvegarde.test.sh
set -euo pipefail
RACINE="$(cd "$(dirname "$0")/.." && pwd)"
T="$(mktemp -d)"; trap 'rm -rf "$T"' EXIT
mkdir -p "$T/bin"
cat > "$T/bin/docker" <<'FAUX'
#!/usr/bin/env bash
shift; [ "$1" = "-i" ] && shift; shift
case "$*" in
  *pg_dump*) cat "$FAUX/base" ;;
  *psql*) cat > "$FAUX/base" ;;
  *) echo "faux docker : commande inattendue $*" >&2; exit 2 ;;
esac
FAUX
chmod +x "$T/bin/docker"
export PATH="$T/bin:$PATH" FAUX="$T" BACKUP_DIR="$T/sauvegardes"

echo "CREATE TABLE gains();" > "$T/base"
F="$(bash "$RACINE/scripts/backup.sh" 2>/dev/null | tail -n1)"
[ -s "$F" ] || { echo "ÉCHEC : pas de sauvegarde ($F)"; exit 1; }
echo "abîmé" > "$T/base"
bash "$RACINE/scripts/restore.sh" "$F" 2>/dev/null
grep -q "CREATE TABLE gains" "$T/base" || { echo "ÉCHEC : base non restaurée"; exit 1; }
: > "$T/base"
if bash "$RACINE/scripts/backup.sh" >/dev/null 2>&1; then echo "ÉCHEC : un dump vide passe pour une sauvegarde"; exit 1; fi
echo "✓ sauvegarde/restauration : contrat tenu"
