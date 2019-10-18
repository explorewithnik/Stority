package com.app.stority.homeSpace.data

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface HomeSpaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHomeSpaceData(data: HomeSpaceTable?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubCategoryData(data: SubCategoryTable?)

    @Update
    fun updateHomeSpaceData(data: HomeSpaceTable)

    @Query("SELECT * FROM HomeSpaceTable")
    fun fetchAllHomeSpaceData(): LiveData<List<HomeSpaceTable>>

    @Query("SELECT * FROM SubCategoryTable")
    fun fetchAllSubCategoryData(): LiveData<List<SubCategoryTable>>

    @Query("SELECT * FROM SubCategoryTable WHERE id=:id")
    fun fetchSubCategoryDataList(id:Int?): LiveData<List<SubCategoryTable>>

    @Query("SELECT * FROM HomeSpaceTable WHERE id= :id ")
    fun fetchHomeSpaceData(id: String): LiveData<HomeSpaceTable>

    @Query("DELETE FROM HomeSpaceTable WHERE id =:id")
    fun deleteHomeSpaceData(id: Int?)

    @Query("DELETE FROM SubCategoryTable WHERE subCategoryId =:id")
    fun deleteSubCategoryData(id: Int?)


    @Query("DELETE FROM HomeSpaceTable")
    fun deleteHomeSpaceAllData()
}