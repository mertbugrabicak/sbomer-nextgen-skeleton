## Example payload for REST API
* Publisher optional
```json
{
  "generationRequests": [
    {
      "target": {
        "type": "pnc-build",
        "identifier": "AXXEA556AVFAA"
      }
    },
    {
      "target": {
        "type": "pnc-build",
        "identifier": "BZZFB667BWEAB"
      }
    }
  ],
  "publishers": [
    {
      "name": "domino",
      "options": {
        "milestoneId": "12345"
      }
    }
  ]
}
```