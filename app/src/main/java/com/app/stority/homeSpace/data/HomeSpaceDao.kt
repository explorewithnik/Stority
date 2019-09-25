package com.app.stority.homeSpace.data

import androidx.lifecycle.LiveData
import androidx.room.*



@Dao
interface HomeSpaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHomeSpaceData(data: HomeSpaceTable?)

    @Update
    fun updateHomeSpaceData(data: HomeSpaceTable)

    @Query("SELECT * FROM HomeSpaceTable")
    fun fetchAllHomeSpaceData(): LiveData<List<HomeSpaceTable>>

    @Query("SELECT * FROM HomeSpaceTable WHERE id= :id ")
    fun fetchHomeSpaceData(id: String): LiveData<HomeSpaceTable>

    @Query("DELETE FROM HomeSpaceTable WHERE id =:id")
    fun deleteHomeSpaceData(id: Int?)

    @Query("DELETE FROM HomeSpaceTable")
    fun deleteHomeSpaceAllData()
}