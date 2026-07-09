#!/bin/bash -l
#
# Byte-identity proof for the test_project fixture: generate the Java model
# layer from the Rails 4.2 dump (project.yml) and from the Rails 7.1 dump
# (project_rails71.yml), then prove the two outputs are byte-identical.
#
# Also diffs the 4.2 generation against the committed golden (test/java) so any
# PRE-EXISTING drift there is surfaced separately — the load-bearing check is
# the A-vs-B (4.2-vs-7.1) diff, which must always be empty.
#
# Run from the jack-test directory:  bash test/diff_rails71.sh
set -u

HERE="$(cd "$(dirname "$0")" && pwd)"
JACK_TEST="$(cd "$HERE/.." && pwd)"
JACK_CORE="$JACK_TEST/../jack-core/src/rb/jack.rb"

TMP="$(mktemp -d)"
OUT_OLD="$TMP/java_old"
OUT_71="$TMP/java_71"
mkdir -p "$OUT_OLD" "$OUT_71"

echo "== generating from Rails 4.2 dump (project.yml) =="
bundle exec ruby "$JACK_CORE" "$HERE/test_project/project.yml"          "$OUT_OLD" >/dev/null
echo "== generating from Rails 7.1 dump (project_rails71.yml) =="
bundle exec ruby "$JACK_CORE" "$HERE/test_project/project_rails71.yml"   "$OUT_71"  >/dev/null

status=0

echo
echo "== A-vs-B: 4.2 output  vs  7.1 output  (must be empty) =="
if diff -r "$OUT_OLD" "$OUT_71"; then
  echo "OK: 4.2 and 7.1 generations are byte-identical"
else
  echo "FAIL: 4.2 and 7.1 generations differ"
  status=1
fi

GOLD="$JACK_TEST/test/java/com/rapleaf/jack/test_project"
GEN_OLD="$OUT_OLD/com/rapleaf/jack/test_project"
echo
echo "== drift check: 4.2 output  vs  committed golden test/java (informational) =="
if diff -r "$GEN_OLD" "$GOLD"; then
  echo "OK: 4.2 generation matches committed golden"
else
  echo "NOTE: pre-existing drift vs committed golden (see above)."
  echo "      This does NOT fail the script; the A-vs-B diff above is the"
  echo "      byte-identity proof for the format migration."
fi

rm -rf "$TMP"
exit $status
