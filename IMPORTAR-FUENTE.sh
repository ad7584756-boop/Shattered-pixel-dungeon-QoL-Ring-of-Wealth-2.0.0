#!/usr/bin/env bash
set -Eeuo pipefail

ROOT="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"
ZIP_NAME="Ring-of-Wealth-QoL-1.0.6-source-complete.zip"
ZIP_PATH="$ROOT/$ZIP_NAME"
WORK="$(mktemp -d)"
cleanup() { rm -rf "$WORK"; }
trap cleanup EXIT

echo "== Importador de Ring of Wealth QoL 1.0.6 =="
echo "Repositorio: $ROOT"

if [[ ! -f "$ZIP_PATH" ]]; then
  echo
  echo "No encuentro el ZIP en la carpeta principal:"
  echo "$ZIP_PATH"
  echo "Subi el archivo $ZIP_NAME al nivel principal del Explorador de archivos de Codespaces y volve a ejecutar este script."
  exit 2
fi

if ! command -v unzip >/dev/null 2>&1; then
  echo "Falta unzip. Instalando..."
  sudo apt-get update
  sudo apt-get install -y unzip
fi

echo "Verificando integridad del ZIP..."
unzip -tq "$ZIP_PATH"

echo "Descomprimiendo..."
mkdir -p "$WORK/unpacked"
unzip -q "$ZIP_PATH" -d "$WORK/unpacked"

SETTINGS_FILE="$(find "$WORK/unpacked" -type f \( -name settings.gradle -o -name settings.gradle.kts \) -print -quit)"
if [[ -z "$SETTINGS_FILE" ]]; then
  echo "ERROR: el ZIP no contiene settings.gradle. No se modifico el repositorio."
  exit 3
fi
SOURCE_DIR="$(dirname "$SETTINGS_FILE")"

if [[ ! -d "$SOURCE_DIR/.github/workflows" ]]; then
  echo "ERROR: no encuentro .github/workflows dentro del proyecto. No se modifico el repositorio."
  exit 4
fi

echo "Copiando todos los archivos del proyecto..."
cp -a "$SOURCE_DIR"/. "$ROOT"/

echo "Comprobando archivos principales..."
for f in README.md build.gradle gradlew .github/workflows/build-android-debug.yml .github/workflows/build-release.yml; do
  if [[ ! -e "$ROOT/$f" ]]; then
    echo "ERROR: falta $f despues de copiar."
    exit 5
  fi
done

COUNT="$(find "$ROOT" -type f -not -path "$ROOT/.git/*" | wc -l | tr -d ' ')"
echo "Archivos presentes (incluye este importador y el ZIP): $COUNT"

rm -f "$ZIP_PATH"

cd "$ROOT"
git add -A
if git diff --cached --quiet; then
  echo "No hay cambios nuevos para guardar."
  exit 0
fi

git config user.name "${GITHUB_USER:-Codespaces Importer}"
git config user.email "${GITHUB_USER_ID:-codespaces}@users.noreply.github.com"
git commit -m "Import complete Ring of Wealth QoL 1.0.6 source"
git push origin HEAD:main

echo
echo "IMPORTACION TERMINADA."
echo "Revisa la rama main en GitHub. No se ejecuto una compilacion Android."
