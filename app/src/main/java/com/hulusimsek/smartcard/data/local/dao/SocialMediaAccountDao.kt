package com.hulusimsek.smartcard.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hulusimsek.smartcard.data.local.entity.SocialMediaAccountEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface SocialMediaAccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: SocialMediaAccountEntity): Long

    @Query("SELECT * FROM social_media_accounts WHERE userId = :userId")
    suspend fun getAccountsByUser(userId: Int): List<SocialMediaAccountEntity>

    @Update
    suspend fun updateAccount(account: SocialMediaAccountEntity): Int

    @Query("UPDATE social_media_accounts SET isActive = :isActive WHERE id = :accountId")
    suspend fun updateAccountActiveStatus(accountId: Int, isActive: Boolean): Int

    @Delete
    suspend fun deleteAccount(account: SocialMediaAccountEntity): Int

    //@Query("SELECT * FROM social_media_accounts WHERE userId = :userId ORDER BY isActive DESC")
    @Query("SELECT * FROM social_media_accounts WHERE userId = :userId")
    fun getAccountStream(userId: Int): Flow<List<SocialMediaAccountEntity>>

}