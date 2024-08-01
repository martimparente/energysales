package pt.isel.ps.energysales.market.application

interface MarketService {
    fun getMarketInfo(): MarketInfo
}

class MarketInfo
