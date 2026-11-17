package com.xauusd.pro2030.core.engine

/** A provider boundary; no news source is configured in this app yet. */
class EconomicNewsEngine(private val provider: NewsProvider? = null) {
    data class NewsItem(val title: String, val timestampMillis: Long, val source: String)
    fun interface NewsProvider { fun fetch(): Result<List<NewsItem>> }

    fun fetchNews(): Result<List<NewsItem>> = provider?.fetch()
        ?: Result.failure(IllegalStateException("No economic news provider is configured"))
}
