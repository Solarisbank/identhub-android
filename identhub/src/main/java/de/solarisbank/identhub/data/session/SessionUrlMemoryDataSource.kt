package de.solarisbank.identhub.data.session

class SessionUrlMemoryDataSource : SessionUrlLocalDataSource {
    private var url: String? = null

    override fun get(): String? {
        return url
    }

    override fun store(url: String?) {
        this.url = url
    }
}