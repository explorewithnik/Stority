package com.app.stority.homeSpace.data

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface HomeSpaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHomeSpaceData(data: HomeSpaceTable?)

    @Update
    fun updateHomeSpaceData(data: HomeSpaceTable?)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateSubCategoryData(data: SubCategoryTable?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubCategoryData(data: SubCategoryTable?)

    @Transaction
    fun insertSubCategory(entryId: String, data: SubCategoryTable?) {
        data?.id = entryId.toInt()
        insertSubCategoryData(data)
    }

    @Query("UPDATE HomeSpaceTable SET text=:text WHERE id=:id")
    fun updateHomeSpaceData(text: String?, id: Int?)

    @Query("SELECT * FROM HomeSpaceTable ORDER BY timeStamp DESC")
    fun fetchAllHomeSpaceData(): LiveData<List<HomeSpaceTable>>

    @Query("SELECT * FROM HomeSpaceTable WHERE text LIKE '%' || :query || '%' ")
    fun fetchHomeSpaceDataBySearch(query: String): LiveData<List<HomeSpaceTable>>

    @Query("SELECT * FROM SubCategoryTable")
    fun fetchAllSubCategoryData(): LiveData<List<SubCategoryTable>>

    @Query("SELECT * FROM SubCategoryTable WHERE id=:id ORDER BY timeStamp DESC")
    fun fetchSubCategoryDataList(id: Int?): LiveData<List<SubCategoryTable>>

    @Query("SELECT * FROM HomeSpaceTable WHERE id= :id ")
    fun fetchHomeSpaceData(id: String): LiveData<HomeSpaceTable>

    @Query("DELETE FROM HomeSpaceTable WHERE id =:id")
    fun deleteHomeSpaceData(id: Int?)

    @Delete
    fun deleteAllHomeSpaceData(data: HomeSpaceTable?)

    @Query("DELETE FROM SubCategoryTable WHERE subCategoryId =:id")
    fun deleteSubCategoryData(id: Int?)


    @Query("DELETE FROM HomeSpaceTable")
    fun deleteHomeSpaceAllData()
}