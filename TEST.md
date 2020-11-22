wget --quiet \
  --method POST \
  --header 'content-type: application/json' \
  --body-file '/home/pdewitte/perso/jira-to-slack/src/test/java/org/talend/jiraToSlack/samples/webhook_doc_jira.json' \
  --output-document \
  - http://localhost:8080/jira-webhook