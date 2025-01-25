package com.pixisky.app.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Pixi] from a given data source.
 */
interface PixiRepository {
    /**
     * Retrieve all the pixies from the the given data source.
     */
    fun getAllPixiesStream(): Flow<List<Pixi>>

    /**
     * Retrieve an pixi from the given data source that matches with the [id].
     */
    fun getPixiStream(id: Int): Flow<Pixi?>

    /**
     * Insert pixi in the data source
     */
    suspend fun insertPixi(pixi: Pixi)

    /**
     * Delete pixi from the data source
     */
    suspend fun deletePixi(pixi: Pixi)

    /**
     * Update pixi in the data source
     */
    suspend fun updatePixi(pixi: Pixi)
}

class OfflinePixiRepository(private val pixiDao: PixiDao) : PixiRepository {
    override fun getAllPixiesStream(): Flow<List<Pixi>> {
        return pixiDao.getAllItems()
    }

    override fun getPixiStream(id: Int): Flow<Pixi?> {
        return pixiDao.getItem(id)
    }

    override suspend fun insertPixi(pixi: Pixi) {
        return pixiDao.insert(pixi)
    }

    override suspend fun deletePixi(pixi: Pixi) {
        return pixiDao.delete(pixi)
    }

    override suspend fun updatePixi(pixi: Pixi) {
        return pixiDao.update(pixi)
    }
}