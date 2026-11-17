# XAUUSD ANALYZER PRO 2030

Android starter app for XAUUSD market data.

## BiQuote connection

The app currently requests snapshots from BiQuote's documented anonymous REST API at `https://biquote.io/api/XAUUSD?allowStale=false`. It polls every 15 seconds while the dashboard is foregrounded. The Refresh button performs a new HTTP request. This is snapshot polling, not a streaming connection; SignalR is not used by this implementation.

The implementation only reports `LIVE` when BiQuote reports the market open and the quote age is no more than 60 seconds. Otherwise it labels a returned quote `STALE`. HTTP/network failures clear the displayed quote and enter reconnect backoff (2 seconds, doubling to a 60-second cap). The SDK contract documents `mid`, `bid`, `ask`, `marketState`, `stale`, and `quoteAgeSeconds`; a source timestamp field was not verified, so only local receipt time is shown.

This repository's code has not been run against BiQuote from an Android device or build environment yet. The code therefore does not claim that a live connection has been tested. Market-data availability and freshness depend on the provider and market hours. This is informational software, not trading advice.
