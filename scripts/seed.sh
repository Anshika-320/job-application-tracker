#!/usr/bin/env bash
#
# Seeds a local Job Application Tracker database with a demo dataset.
#
# The API has no registration endpoint, so the sign-in account is created by the
# `dev` profile when the application starts. Start the API like this first:
#
#   SPRING_PROFILES_ACTIVE=dev \
#   TEST_USER_EMAIL=demo@jobtracker.local \
#   TEST_USER_PASSWORD='choose-a-local-password' \
#   ./mvnw spring-boot:run
#
# Then run this script from the project root:
#
#   TEST_USER_EMAIL=demo@jobtracker.local \
#   TEST_USER_PASSWORD='choose-a-local-password' \
#   ./scripts/seed.sh
#
# Options:
#   --reset   delete every existing application before seeding
#   --help    show this message

set -euo pipefail

API_BASE_URL="${API_BASE_URL:-http://localhost:8080}"
SEED_USER_EMAIL="${SEED_USER_EMAIL:-${TEST_USER_EMAIL:-demo@jobtracker.local}}"
SEED_USER_PASSWORD="${SEED_USER_PASSWORD:-${TEST_USER_PASSWORD:-}}"
RESET=false

while [ $# -gt 0 ]; do
  case "$1" in
    --reset) RESET=true ;;
    --help|-h) sed -n '2,24p' "$0" | sed 's/^# \{0,1\}//'; exit 0 ;;
    *) echo "Unknown option: $1" >&2; exit 2 ;;
  esac
  shift
done

command -v curl >/dev/null || { echo "curl is required" >&2; exit 1; }

if [ -z "$SEED_USER_PASSWORD" ]; then
  cat >&2 <<'MSG'
No password supplied.

Set TEST_USER_PASSWORD (or SEED_USER_PASSWORD) to the password the API was
started with, for example:

  TEST_USER_PASSWORD='choose-a-local-password' ./scripts/seed.sh
MSG
  exit 1
fi

api() {
  # api <method> <path> [body]
  local method="$1" path="$2" body="${3:-}"
  if [ -n "$body" ]; then
    curl -sS -X "$method" "$API_BASE_URL$path" \
      -H "Authorization: Bearer $TOKEN" \
      -H 'Content-Type: application/json' \
      -d "$body"
  else
    curl -sS -X "$method" "$API_BASE_URL$path" -H "Authorization: Bearer $TOKEN"
  fi
}

json_field() {
  # json_field <key> — reads the first value of a flat string/number field
  sed -n "s/.*\"$1\":\"\{0,1\}\([^,\"}]*\)\"\{0,1\}.*/\1/p"
}

echo "Job Application Tracker — seeding $API_BASE_URL"

if ! curl -sS -o /dev/null --max-time 5 "$API_BASE_URL/api/applications"; then
  echo "Could not reach the API at $API_BASE_URL. Is it running?" >&2
  exit 1
fi

login_response=$(curl -sS -X POST "$API_BASE_URL/api/auth/login" \
  -H 'Content-Type: application/json' \
  -d "{\"email\":\"$SEED_USER_EMAIL\",\"password\":\"$SEED_USER_PASSWORD\"}")

TOKEN=$(printf '%s' "$login_response" | json_field token)

if [ -z "$TOKEN" ]; then
  cat >&2 <<MSG
Could not sign in as $SEED_USER_EMAIL.

The account is created by the API at startup when the 'dev' profile is active.
Start the API with:

  SPRING_PROFILES_ACTIVE=dev \\
  TEST_USER_EMAIL=$SEED_USER_EMAIL \\
  TEST_USER_PASSWORD='<the same password>' \\
  ./mvnw spring-boot:run

then run this script again.
MSG
  exit 1
fi

echo "  signed in as $SEED_USER_EMAIL"

existing=$(api GET /api/applications | grep -o '"id"' | wc -l | tr -d ' ')

if [ "$RESET" = true ]; then
  if [ "$existing" -gt 0 ]; then
    echo "  removing $existing existing application(s)"
    for id in $(api GET /api/applications | tr '{' '\n' | sed -n 's/.*"id":\([0-9]*\).*/\1/p'); do
      api DELETE "/api/applications/$id" >/dev/null
    done
  fi
elif [ "$existing" -gt 0 ]; then
  echo "  $existing application(s) already present; nothing to do."
  echo "  re-run with --reset to replace them."
  exit 0
fi

add_application() {
  # add_application <company> <role> <status> <location>
  local location_json="null"
  [ -n "$4" ] && location_json="\"$4\""
  api POST /api/applications \
    "{\"companyName\":\"$1\",\"role\":\"$2\",\"status\":\"$3\",\"location\":$location_json}" \
    | sed -n 's/.*"id":\([0-9]*\).*/\1/p'
}

add_note() {
  # add_note <applicationId> <content>
  api POST "/api/applications/$1/notes" "{\"content\":\"$2\"}" >/dev/null
}

echo "  creating applications"

id=$(add_application "Atlassian" "Senior Backend Engineer" "INTERVIEWED" "Bengaluru")
add_note "$id" "Second round with the platform team on Thursday"
add_note "$id" "Asked about on-call rotation - 1 week in 6"

id=$(add_application "Razorpay" "Backend Engineer" "INTERVIEW_SCHEDULED" "Bengaluru")
add_note "$id" "Recruiter screen booked for Monday 11am"

id=$(add_application "Zerodha" "Platform Engineer" "OFFERED" "Bengaluru")
add_note "$id" "Offer received - responding by the end of the week"

add_application "Postman" "Software Engineer II" "APPLIED" "Remote" >/dev/null
add_application "Freshworks" "Backend Engineer" "APPLIED" "Chennai" >/dev/null

id=$(add_application "Swiggy" "SDE II" "REJECTED" "Bengaluru")
add_note "$id" "Did not clear the system design round"

add_application "Zoho" "Member Technical Staff" "WITHDRAWN" "Chennai" >/dev/null

total=$(api GET /api/applications | grep -o '"id"' | wc -l | tr -d ' ')
echo "  done - $total applications in the database"
echo
echo "Sign in at http://localhost:5173 with $SEED_USER_EMAIL"
