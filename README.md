# XAUUSD ANALYZER PRO 2030

Android starter app for monitoring XAUUSD market data. Market analysis is educational information only, not financial advice or a guarantee of trading results.

## BiQuote connection

The app requests snapshots from BiQuote's documented anonymous REST API at `https://biquote.io/api/XAUUSD?allowStale=false`. Polling is limited to the foreground dashboard. A quote is marked `LIVE` only when the market is open, BiQuote does not mark the response stale, and `quoteAgeSeconds` is within the configured freshness limit. Network and malformed-response failures are surfaced rather than presented as current prices.

## Sampled candles and analysis

The app can aggregate received quote snapshots into one-minute OHLC candles in memory and calculate SMA, EMA, and RSI after enough samples are available. These are **sampled quote candles**, not provider-supplied historical market candles; gaps, app downtime, and the 15-second polling interval mean they are not a complete market history. No historical chart or indicator result is fabricated when input is insufficient. Signals list their source and reasoning; their score is a rule-strength index, not a probability.

## Local history and preferences

Signal summaries are stored privately on-device, bounded to 500 records. Refresh interval and candle timeframe preferences have safe bounds and defaults. Clearing history removes saved signal summaries. Clearing app data removes all locally stored settings and history.

## Tests

Run local unit tests with `./gradlew test`. Tests cover BiQuote parsing/freshness classification, quote-to-candle aggregation, indicator calculations, and explainable signal inputs. Android build/test execution and a live request from an Android device still need to be verified in the project environment.
