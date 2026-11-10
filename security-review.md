Privacy and Security Review – WeatherWise

1. Data Privacy and Permissions

WeatherWise follows the principle of least privilege by requesting only the INTERNET and ACCESS_NETWORK_STATE permissions explicitly. It does not require GPS access, user accounts, advertising identifiers or analytics services. WorkManager may introduce additional permissions through manifest merging for background task scheduling.

When users search for a city or select a location, the search text and selected coordinates are transmitted to the Open-Meteo API. The service may also receive standard network metadata, such as IP addresses. The Places screen explains this data processing, helping users make informed decisions.

2. Storage and Transmission Security

WeatherWise communicates with Open-Meteo using HTTPS and Android's platform certificate validation. Cleartext communication and redirects are disabled to reduce network-related risks.

Search input is length-limited and URL-encoded. Room uses parameterised database queries, reducing SQL injection risks. Saved locations and cached forecasts are stored within Android's private application storage. Deleting a saved location also removes its associated cached data.

Application backup is disabled to reduce unintended data transfers, although manufacturer-specific device migration behaviour has not yet been verified. No API keys, passwords or other secrets are embedded in the application.

3. Compliance Assessment and Remaining Risks

The application demonstrates several Android security best practices, including minimal permissions, HTTPS communication, private local storage and input validation.

However, the Room database is not independently encrypted. Saved locations could reveal user interests if a device is unlocked or compromised. Before production release, database encryption should be evaluated against the application's threat model.

Further work includes auditing merged permissions and dependencies, testing data deletion and device transfers, conducting accessibility and API abuse testing, and configuring secure release signing. A public privacy policy and accurate Google Play Data Safety declaration must also be prepared.

Finally, Open-Meteo's free API has non-commercial usage restrictions. Its licensing conditions must be reviewed before publication.

Reference

Android Developers (2026) Improve your app's security. Available at: https://developer.android.com/privacy-and-security/security-best-practices (Accessed: 3 November 2026).